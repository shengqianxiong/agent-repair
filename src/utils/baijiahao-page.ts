import fs from 'node:fs/promises'
import type { Locator, Page } from 'playwright'
import { plainContent } from './article-platform-publish.js'
import { evaluateInBrowser, evaluateInBrowserNoArg } from './browser-eval.js'
import { getBaijiahaoHumanConfig, humanClick, humanPause } from './human.js'
import { resolveCoverUploadPath } from './image.js'
import { clickByText, removeMaskLayers, sleep } from './page.js'

export const BAIJIAHAO_EDITOR_URL = 'https://baijiahao.baidu.com/builder/rc/edit?type=news'

const BAIJIAHAO_LOGIN_PATTERNS = [
  'passport.baidu.com',
  '/login',
  'wappass.baidu.com',
]

export function isBaijiahaoLoginPage(url: string) {
  if (!url || url === 'about:blank') return true
  return BAIJIAHAO_LOGIN_PATTERNS.some((pattern) => url.includes(pattern))
}

async function dismissBaijiahaoOverlays(page: Page) {
  await removeMaskLayers(page)
  for (const label of ['我知道了', '知道了', '关闭', '暂不', '跳过']) {
    const btn = page.getByRole('button', { name: label })
    if (await btn.first().isVisible().catch(() => false)) {
      await humanClick(btn.first(), getBaijiahaoHumanConfig()).catch(() => undefined)
    }
  }
  await humanPause(0.5, getBaijiahaoHumanConfig())
}

export async function waitBaijiahaoEditorReady(page: Page) {
  await dismissBaijiahaoOverlays(page)
  await page.waitForLoadState('networkidle').catch(() => undefined)
  const selectors = [
    'textarea[placeholder*="标题"]',
    'input[placeholder*="标题"]',
    '.cheetah-input',
    '.news-editor-pc',
    '[contenteditable="true"]',
  ]
  for (const selector of selectors) {
    try {
      await page.locator(selector).first().waitFor({ state: 'visible', timeout: 30000 })
      return
    } catch {
      // try next selector
    }
  }
  await humanPause(2, getBaijiahaoHumanConfig())
}

export async function fillBaijiahaoTitle(page: Page, title: string) {
  const value = (title || '').replace(/\s+/g, ' ').trim().slice(0, 40)
  if (!value) {
    throw new Error('百家号 标题为空')
  }

  const selectors = [
    'textarea[placeholder*="标题"]',
    'input[placeholder*="标题"]',
    'textarea[placeholder*="请输入"]',
    '.title-input textarea',
    '.title-input input',
  ]
  for (const selector of selectors) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    if (!(await locator.isVisible().catch(() => false))) continue
    await humanClick(locator, getBaijiahaoHumanConfig())
    await page.keyboard.press('Control+A')
    await page.keyboard.type(value, { delay: 40 })
    await humanPause(0.8, getBaijiahaoHumanConfig())
    return value
  }

  throw new Error('百家号 未找到标题输入框')
}

export async function readBaijiahaoTitle(page: Page) {
  for (const selector of [
    'textarea[placeholder*="标题"]',
    'input[placeholder*="标题"]',
    '.title-input textarea',
    '.title-input input',
  ]) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    const value = (await locator.inputValue().catch(async () => locator.innerText())).trim()
    if (value) return value
  }
  return ''
}

export async function fillBaijiahaoContent(page: Page, content: string) {
  const plain = plainContent(content)
  if (!plain) {
    throw new Error('百家号 正文为空')
  }

  for (const selector of [
    '.news-editor-pc [contenteditable="true"]',
    '.ProseMirror',
    '[contenteditable="true"]',
    '.editor-content',
  ]) {
    const editor = page.locator(selector).first()
    if (!(await editor.count())) continue
    if (!(await editor.isVisible().catch(() => false))) continue
    await humanClick(editor, getBaijiahaoHumanConfig())
    await page.keyboard.press('Control+A')
    await page.keyboard.type(plain.slice(0, 20000), { delay: 15 })
    await humanPause(1.5, getBaijiahaoHumanConfig())
    return
  }

  throw new Error('百家号 未找到正文编辑器')
}

export async function fillBaijiahaoSummary(page: Page, content: string) {
  const plain = plainContent(content).replace(/\s+/g, ' ').trim()
  if (!plain) return
  const summary = plain.slice(0, 120)
  for (const selector of [
    'textarea[placeholder*="摘要"]',
    'textarea[placeholder*="导语"]',
    'input[placeholder*="摘要"]',
  ]) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    if (!(await locator.isVisible().catch(() => false))) continue
    await locator.fill(summary)
    await humanPause(0.6, getBaijiahaoHumanConfig())
    return
  }
}

export async function selectBaijiahaoAiDeclaration(page: Page) {
  const labels = ['AI创作', 'AI辅助', '含AI', '人工智能']
  for (const label of labels) {
    const option = page.locator('label, span, div').filter({ hasText: new RegExp(label) }).first()
    if (await option.isVisible().catch(() => false)) {
      await option.click({ force: true }).catch(() => undefined)
      await humanPause(0.4, getBaijiahaoHumanConfig())
      return
    }
  }
}

async function clickBaijiahaoSingleCoverMode(page: Page, root: Page | Locator = page) {
  const single = root.locator('label.cheetah-radio-wrapper').filter({ hasText: '单图' }).first()
  if (await single.count()) {
    await humanClick(single, getBaijiahaoHumanConfig())
    await humanPause(0.6, getBaijiahaoHumanConfig())
    return true
  }

  const scope = root === page ? page : root
  const clicked = await evaluateInBrowserNoArg(scope as Page, () => {
    const buttons = Array.from(document.querySelectorAll('label.cheetah-radio-wrapper, .cheetah-radio-wrapper'))
    const singleButton = buttons.find((node) => node.textContent?.includes('单图'))
    if (!singleButton) return false
    ;(singleButton as HTMLElement).click()
    return true
  }).catch(() => false)

  if (clicked) {
    await humanPause(0.6, getBaijiahaoHumanConfig())
  }
  return clicked
}

async function clickBaijiahaoLocalImageTab(page: Page, root: Page | Locator = page) {
  const localTab = root.locator('.cheetah-tabs-tab').filter({ hasText: '本地图片' }).first()
  if (await localTab.count() && (await localTab.isVisible().catch(() => false))) {
    await humanClick(localTab, getBaijiahaoHumanConfig())
    await humanPause(0.5, getBaijiahaoHumanConfig())
    return true
  }
  return false
}

async function uploadBaijiahaoCoverFile(
  page: Page,
  coverImage: string | undefined,
  root: Page | Locator = page,
) {
  const uploadPath = await resolveCoverUploadPath(coverImage)
  if (!uploadPath) {
    throw new Error('百家号 缺少封面图片')
  }

  await clickBaijiahaoSingleCoverMode(page, root)
  await clickBaijiahaoLocalImageTab(page, root)

  const uploadTrigger = root.locator('div.coverUploaderView div.container, div.coverUploaderView, .coverUploaderView').first()
  if (await uploadTrigger.count() && (await uploadTrigger.isVisible().catch(() => false))) {
    await humanClick(uploadTrigger, getBaijiahaoHumanConfig())
    await humanPause(0.5, getBaijiahaoHumanConfig())
  }

  const fileInput = root.locator('input[type="file"]').last()
  await fileInput.waitFor({ state: 'attached', timeout: 15000 })
  await fileInput.setInputFiles(uploadPath)
  await humanPause(1.2, getBaijiahaoHumanConfig())

  if (/^https?:\/\//i.test(coverImage || '')) {
    await fs.unlink(uploadPath).catch(() => undefined)
  }

  await waitBaijiahaoCoverReady(page, root)
}

async function waitBaijiahaoCoverReady(page: Page, root: Page | Locator = page) {
  const previewSelectors = [
    '.coverUploaderView img[src*="http"]',
    '.cover-list img[src*="http"]',
    '.cover-image img[src*="http"]',
    '.cover-preview img[src*="http"]',
    'img[src*="pic"]',
  ]
  const started = Date.now()
  while (Date.now() - started < 30000) {
    for (const selector of previewSelectors) {
      const preview = root.locator(selector).first()
      if (await preview.isVisible().catch(() => false)) {
        const src = (await preview.getAttribute('src').catch(() => '')) || ''
        if (src && !src.startsWith('data:image/svg')) {
          return
        }
      }
    }
    await sleep(800)
  }
  throw new Error('百家号 封面图片上传未完成')
}

async function hasBaijiahaoCoverSet(root: Page | Locator) {
  const preview = root.locator('.coverUploaderView img, .cover-list img, .cover-image img, .cover-preview img').first()
  if (!(await preview.count())) return false
  if (!(await preview.isVisible().catch(() => false))) return false
  const src = (await preview.getAttribute('src').catch(() => '')) || ''
  return src.startsWith('http') && !src.includes('placeholder')
}

export async function prepareInlineBaijiahaoCover(page: Page, coverImage?: string) {
  if (!coverImage) return
  await page.evaluate(() => window.scrollTo(0, 0))
  await humanPause(0.8, getBaijiahaoHumanConfig())
  await uploadBaijiahaoCoverFile(page, coverImage, page)
}

export async function clickBaijiahaoPublishButton(page: Page) {
  await dismissBaijiahaoOverlays(page)
  const publishButton = page.getByRole('button', { name: /^发布$/ }).first()
  if (await publishButton.isVisible().catch(() => false)) {
    await humanClick(publishButton, getBaijiahaoHumanConfig())
    return
  }
  await clickByText(page, '发布', 'button')
  await humanPause(1, getBaijiahaoHumanConfig())
}

export async function fillBaijiahaoPublishDialog(page: Page, coverImage?: string) {
  const dialog = page.locator('.cheetah-modal, .cheetah-drawer, [role="dialog"]').filter({ hasText: /封面|发布/ }).last()
  await dialog.waitFor({ state: 'visible', timeout: 20000 }).catch(async () => {
    await page.locator('text=封面').first().waitFor({ state: 'visible', timeout: 10000 })
  })

  const dialogRoot = (await dialog.count()) ? dialog : page.locator('body')

  if (!(await hasBaijiahaoCoverSet(dialogRoot))) {
    await uploadBaijiahaoCoverFile(page, coverImage, dialogRoot)
  }

  if (!(await hasBaijiahaoCoverSet(dialogRoot))) {
    throw new Error('百家号发布失败：请先设置封面（单图）')
  }

  await humanPause(0.8, getBaijiahaoHumanConfig())
}

export async function confirmBaijiahaoPublish(page: Page) {
  const confirmSelectors = [
    'button:has-text("发布")',
    'button:has-text("确认发布")',
    'button:has-text("确定")',
  ]
  for (const selector of confirmSelectors) {
    const button = page.locator('.cheetah-modal, .cheetah-drawer, [role="dialog"]').locator(selector).last()
    if (await button.count() && (await button.isVisible().catch(() => false))) {
      await humanClick(button, getBaijiahaoHumanConfig())
      return
    }
  }
  await clickByText(page, '发布', 'button')
}

export async function waitBaijiahaoPublishSuccess(page: Page, normalizedTitle: string) {
  const titleNeedle = normalizedTitle.trim().slice(0, 8)
  const timeoutMs = Number(process.env.BAIJIAHAO_PUBLISH_WAIT_MS || 90000)
  const started = Date.now()

  while (Date.now() - started < timeoutMs) {
    const bodyText = await page.locator('body').innerText().catch(() => '')
    if (/发布成功|提交成功|审核中|已发布/.test(bodyText)) {
      return page.url()
    }
    const coverError = page.getByText(/请先设置封面|封面/).first()
    if (await coverError.isVisible().catch(() => false)) {
      const text = (await coverError.innerText().catch(() => '')).trim()
      if (text.includes('请先设置封面')) {
        throw new Error(`百家号发布失败：${text}`)
      }
    }
    await sleep(1500)
  }

  if (titleNeedle) {
    await page.goto('https://baijiahao.baidu.com/builder/rc/content', { waitUntil: 'domcontentloaded' }).catch(() => undefined)
    if (await page.getByText(titleNeedle, { exact: false }).first().isVisible().catch(() => false)) {
      return page.url()
    }
  }

  throw new Error('百家号 发布超时')
}
