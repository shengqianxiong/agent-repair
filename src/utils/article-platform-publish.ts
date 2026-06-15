import type { BrowserContext, Locator, Page } from 'playwright'
import type { PublishTask } from '../api.js'
import { createPage, type PublishResult, type Publisher } from '../publishers/base.publisher.js'
import { getHumanConfig, humanClick, humanPause, humanPasteInChunks } from './human.js'
import { fillFirstMatch, fillFirstVisible, setContentEditableText, sleep } from './page.js'
import { isUrlLogin, openEditor } from './platform-page.js'
import { screenshotPath as buildScreenshotPath } from './paths.js'

export interface ArticlePublishConfig {
  platform: string
  label: string
  editorUrl: string
  loginPatterns: string[]
  titleSelectors: string[]
  editorSelectors: string[]
  publishButtonTexts?: RegExp[]
  confirmButtonTexts?: RegExp[]
  successTexts?: RegExp[]
  successUrlIncludes?: string[]
  maxTitleLength?: number
  prepareEditor?: (page: Page, task: PublishTask, config: ArticlePublishConfig) => Promise<void>
  waitPublishSuccess?: (page: Page, task: PublishTask, config: ArticlePublishConfig) => Promise<string>
  beforePublish?: (page: Page, task: PublishTask) => Promise<void>
  afterFill?: (page: Page, task: PublishTask) => Promise<void>
  afterPrimaryPublishClick?: (page: Page, task: PublishTask) => Promise<void>
  fillBody?: (page: Page, task: PublishTask, config: ArticlePublishConfig) => Promise<void>
}

export function plainContent(content: string) {
  return content
    .replace(/^#{1,6}\s+/gm, '')
    .replace(/\*\*(.*?)\*\*/g, '$1')
    .replace(/\*(.*?)\*/g, '$1')
    .replace(/`([^`]+)`/g, '$1')
    .trim()
}

async function fillTitle(page: Page, config: ArticlePublishConfig, title: string) {
  const maxLen = config.maxTitleLength ?? 120
  const value = (title || '').replace(/\s+/g, ' ').trim().slice(0, maxLen)
  if (!value) {
    throw new Error(`${config.label} 标题为空`)
  }

  const filled = await fillFirstVisible(page, config.titleSelectors, value)
  if (filled) {
    await humanPause(1, getHumanConfig())
    return
  }

  const matched = await fillFirstMatch(page, config.titleSelectors, value)
  if (matched) {
    await humanPause(1, getHumanConfig())
    return
  }

  const editableFilled = await setContentEditableText(page, config.titleSelectors, value)
  if (editableFilled) {
    await humanPause(1, getHumanConfig())
    return
  }

  const byPlaceholder = page.getByPlaceholder(/标题/)
  if (await byPlaceholder.count()) {
    const input = byPlaceholder.first()
    if (await input.isVisible().catch(() => false)) {
      await input.click({ force: true })
      await page.keyboard.press('Control+A')
      await page.keyboard.type(value, { delay: 40 })
      await humanPause(1, getHumanConfig())
      return
    }
  }

  const hint = page.getByText(/请输入标题|文章标题/).first()
  if (await hint.isVisible().catch(() => false)) {
    await hint.click({ force: true })
    await page.keyboard.press('Control+A')
    await page.keyboard.type(value, { delay: 40 })
    await humanPause(1, getHumanConfig())
    return
  }

  throw new Error(`${config.label} 未找到标题输入框`)
}

async function fillBody(page: Page, config: ArticlePublishConfig, content: string) {
  const plain = plainContent(content)
  if (!plain) {
    throw new Error(`${config.label} 正文为空`)
  }

  for (const selector of config.editorSelectors) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    if (!(await locator.isVisible().catch(() => false))) continue
    await humanClick(locator, getHumanConfig())
    await humanPause(0.4, getHumanConfig())
    await page.keyboard.press('Control+A')
    await humanPasteInChunks(page, plain, getHumanConfig())
    await humanPause(1.2, getHumanConfig())
    const text = await locator.innerText().catch(() => '')
    if (text.trim().length >= 8) {
      return
    }
  }

  const bodyHint = page.getByPlaceholder(/正文|内容/).first()
  if (await bodyHint.isVisible().catch(() => false)) {
    await humanClick(bodyHint, getHumanConfig())
    await humanPasteInChunks(page, plain, getHumanConfig())
    await humanPause(1.2, getHumanConfig())
    return
  }

  const hint = page.getByText(/请输入正文|请输入内容/).first()
  if (await hint.isVisible().catch(() => false)) {
    await hint.click({ force: true })
    await humanPasteInChunks(page, plain, getHumanConfig())
    await humanPause(1.2, getHumanConfig())
    return
  }

  throw new Error(`${config.label} 未找到正文编辑器`)
}

async function clickMatchingButton(page: Page, patterns: RegExp[], optional = false) {
  const buttons = page.locator('button, [role="button"], a, div[class*="publish"], span[class*="publish"]').filter({ hasText: /.+/ })
  const count = await buttons.count()
  const candidates: Array<{ index: number; text: string; score: number }> = []

  for (let index = 0; index < count; index += 1) {
    const button = buttons.nth(index)
    if (!(await button.isVisible().catch(() => false))) continue
    const text = (await button.innerText().catch(() => '')).trim().replace(/\s+/g, '')
    if (!text || /取消|关闭|返回|草稿/.test(text)) continue
    if (/预览/.test(text) && !/发布/.test(text)) continue
    const matched = patterns.find((pattern) => pattern.test(text))
    if (!matched) continue
    candidates.push({ index, text, score: text.length })
  }

  candidates.sort((a, b) => b.score - a.score)
  const target = candidates[0]
  if (target) {
    const button = buttons.nth(target.index)
    await humanClick(button, getHumanConfig())
    await humanPause(1.2, getHumanConfig())
    return true
  }

  if (!optional) {
    throw new Error('未找到发布按钮')
  }
  return false
}

async function waitPublishSuccess(page: Page, config: ArticlePublishConfig) {
  const timeoutMs = Number(process.env.ARTICLE_PUBLISH_WAIT_MS || 120000)
  const started = Date.now()
  const successTexts = config.successTexts ?? [/发布成功/, /提交成功/, /已发布/, /审核中/, /保存成功/]
  const urlHints = config.successUrlIncludes ?? []
  let editorPath = ''
  try {
    editorPath = new URL(config.editorUrl).pathname
  } catch {
    editorPath = '/graphic/publish'
  }

  while (Date.now() - started < timeoutMs) {
    const currentUrl = page.url()
    const stillOnEditor = editorPath && currentUrl.includes(editorPath)

    if (!stillOnEditor && urlHints.some((item) => currentUrl.includes(item))) {
      await humanPause(1.5, getHumanConfig())
      return currentUrl
    }

    for (const pattern of successTexts) {
      const hint = page.getByText(pattern).first()
      if (await hint.isVisible().catch(() => false)) {
        await humanPause(1.5, getHumanConfig())
        return currentUrl
      }
    }

    if (!stillOnEditor && urlHints.length === 0) {
      for (const pattern of successTexts) {
        if (pattern.test(await page.locator('body').innerText().catch(() => ''))) {
          return currentUrl
        }
      }
    }

    const errorHint = page
      .locator('[class*="error"], [class*="toast"], [class*="message"]')
      .filter({ hasText: /失败|必填|请填写|不能为空|请选择封面|封面/ })
    if (await errorHint.first().isVisible().catch(() => false)) {
      const text = (await errorHint.first().innerText().catch(() => `${config.label} 发布失败`)).trim()
      throw new Error(text || `${config.label} 发布失败`)
    }

    await sleep(1200)
  }

  throw new Error(`${config.label} 发布超时，未检测到成功状态`)
}

export async function publishArticlePlatform(
  context: BrowserContext,
  task: PublishTask,
  config: ArticlePublishConfig,
): Promise<PublishResult> {
  const page = await createPage(context)
  try {
    await openEditor(page, config.editorUrl)
    if (isUrlLogin(page.url(), config.loginPatterns)) {
      throw new Error(`${config.label} 未登录，请先在管理端触发登录或执行 npm run login:${config.platform}`)
    }

    if (config.prepareEditor) {
      await config.prepareEditor(page, task, config)
    }

    await fillTitle(page, config, task.title)
    if (config.fillBody) {
      await config.fillBody(page, task, config)
    } else {
      await fillBody(page, config, task.content)
    }
    if (config.afterFill) {
      await config.afterFill(page, task)
    }
    if (config.beforePublish) {
      await config.beforePublish(page, task)
    }

    const publishPatterns = config.publishButtonTexts ?? [/^发布$/, /^发表$/, /立即发布/, /提交文章/]
    await clickMatchingButton(page, publishPatterns)
    if (config.afterPrimaryPublishClick) {
      await config.afterPrimaryPublishClick(page, task)
    } else {
      const confirmPatterns = config.confirmButtonTexts ?? [/确认发布/, /确定发布/, /^确定$/, /^发布$/]
      await clickMatchingButton(page, confirmPatterns, true)
    }

    const resultUrl = config.waitPublishSuccess
      ? await config.waitPublishSuccess(page, task, config)
      : await waitPublishSuccess(page, config)
    const screenshot = buildScreenshotPath(task.id, task.platform, 'success')
    await page.screenshot({ path: screenshot, fullPage: true })
    return { success: true, resultUrl, screenshotPath: screenshot }
  } catch (error) {
    const screenshot = buildScreenshotPath(task.id, task.platform, 'error')
    await page.screenshot({ path: screenshot, fullPage: true }).catch(() => undefined)
    return {
      success: false,
      errorMsg: error instanceof Error ? error.message : String(error),
      screenshotPath: screenshot,
    }
  } finally {
    await page.close()
  }
}

export class ArticlePlatformPublisher implements Publisher {
  platform: string
  private readonly config: ArticlePublishConfig

  constructor(config: ArticlePublishConfig) {
    this.config = config
    this.platform = config.platform
  }

  publish(context: BrowserContext, task: PublishTask) {
    return publishArticlePlatform(context, task, this.config)
  }
}

export async function clickVisibleText(page: Page, text: string | RegExp) {
  const locator: Locator = typeof text === 'string' ? page.getByText(text, { exact: false }) : page.getByText(text)
  const target = locator.first()
  if (await target.isVisible().catch(() => false)) {
    await humanClick(target, getHumanConfig())
    return true
  }
  return false
}
