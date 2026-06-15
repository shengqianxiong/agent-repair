import type { Page } from 'playwright'
import type { PublishTask } from '../api.js'
import type { ArticlePublishConfig } from './article-platform-publish.js'
import { plainContent } from './article-platform-publish.js'
import { humanClick, humanPause, getHumanConfig } from './human.js'
import { removeMaskLayers, sleep, writeClipboard } from './page.js'

export const SOHU_MANAGE_URL = 'https://mp.sohu.com/mpfe/v4/main'
export const SOHU_EDITOR_URL = 'https://mp.sohu.com/mpfe/v4/main/news/addarticle'
export const SOHU_LOGIN_URL = `https://v4.passport.sohu.com/fe/login?service=${encodeURIComponent(SOHU_MANAGE_URL)}`
export const SOHU_LOGIN_PATTERNS = [
  'passport.sohu.com',
  'v4.passport.sohu.com',
  '/fe/login',
  'mp.sohu.com/login',
]

export function isSohuLoggedIn(url: string) {
  if (!url || url === 'about:blank') return false
  if (/passport\.sohu\.com/i.test(url)) return false
  if (url.includes('mp.sohu.com/login')) return false
  return url.includes('mp.sohu.com')
}

const SOHU_LOGGED_IN_PATTERNS = [
  'text=内容管理',
  'text=发布文章',
  'text=写文章',
  'input[name="title"]',
  'input[placeholder*="标题"]',
  'textarea[placeholder*="标题"]',
  '.ql-editor',
  '[contenteditable="true"]',
] as const

export async function open(page: Page) {
  await page.goto(SOHU_MANAGE_URL, { waitUntil: 'domcontentloaded' })
  await page.waitForLoadState('networkidle').catch(() => undefined)
  if (!isSohuLoggedIn(page.url())) return
  await page.goto(SOHU_EDITOR_URL, { waitUntil: 'domcontentloaded' })
  await page.waitForLoadState('networkidle').catch(() => undefined)
}

export async function hasLoggedInMarkers(page: Page) {
  for (const pattern of SOHU_LOGGED_IN_PATTERNS) {
    const locator = page.locator(pattern).first()
    if ((await locator.count()) > 0 && (await locator.isVisible().catch(() => false))) {
      return true
    }
  }
  return false
}

export async function isLoginRequired(page: Page) {
  if (!isSohuLoggedIn(page.url())) return true
  if (await hasLoggedInMarkers(page)) return false
  const blocked = page.getByText(/未通过审核|审核未通过|重选注册类型/).first()
  if (await blocked.isVisible().catch(() => false)) return true
  for (const pattern of SOHU_LOGIN_PATTERNS) {
    if (page.url().includes(pattern)) return true
  }
  return false
}

async function dismissSohuOverlays(page: Page) {
  await removeMaskLayers(page)
  for (const label of ['我知道了', '知道了', '关闭', '暂不']) {
    const btn = page.getByRole('button', { name: label })
    if (await btn.first().isVisible().catch(() => false)) {
      await humanClick(btn.first(), getHumanConfig()).catch(() => undefined)
    }
  }
  await humanPause(0.4, getHumanConfig())
}

async function assertSohuEditorReady(page: Page) {
  const blocked = page.getByText(/未通过审核|审核未通过|重选注册类型|修改注册信息/).first()
  if (await blocked.isVisible().catch(() => false)) {
    throw new Error('搜狐号 账号未通过审核，无法进入文章编辑器')
  }
}

async function prepareSohuEditor(page: Page, _task: PublishTask, _config: ArticlePublishConfig) {
  await dismissSohuOverlays(page)
  await assertSohuEditorReady(page)
  await page.waitForLoadState('networkidle').catch(() => undefined)
  const waitSelectors = [
    'input[name="title"]',
    'input[placeholder*="标题"]',
    'textarea[placeholder*="标题"]',
    '.title-input input',
    '.article-title input',
  ]
  for (const selector of waitSelectors) {
    try {
      await page.locator(selector).first().waitFor({ state: 'visible', timeout: 20000 })
      return
    } catch {
      // try next selector
    }
  }
  await humanPause(2, getHumanConfig())
}

async function fillSohuSummary(page: Page, content: string) {
  const plain = plainContent(content).replace(/\s+/g, ' ').trim()
  if (!plain) return
  const summary = plain.slice(0, 120)
  const selectors = [
    'textarea[name="summary"]',
    'textarea[placeholder*="摘要"]',
    'textarea[placeholder*="导语"]',
    'input[placeholder*="摘要"]',
  ]
  for (const selector of selectors) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    if (!(await locator.isVisible().catch(() => false))) continue
    await locator.fill(summary)
    await humanPause(0.5, getHumanConfig())
    return
  }
}

async function prepareSohuForm(page: Page, task: PublishTask) {
  await dismissSohuOverlays(page)
  await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight))
  await sleep(1200)
  await fillSohuSummary(page, task.content)
  await page.locator('label, span, div').filter({ hasText: /^原创/ }).first().click({ force: true }).catch(() => undefined)
  await humanPause(0.5, getHumanConfig())
}

async function fillSohuBody(page: Page, task: PublishTask, _config: ArticlePublishConfig) {
  const plain = plainContent(task.content)
  if (!plain) {
    throw new Error('搜狐号 正文为空')
  }

  for (const selector of ['#editor', '.ql-editor', '.ProseMirror', '[contenteditable="true"]', '.editor-content']) {
    const editor = page.locator(selector).first()
    if (!(await editor.count())) continue
    if (!(await editor.isVisible().catch(() => false))) continue
    await humanClick(editor, getHumanConfig())
    await writeClipboard(page, plain)
    await page.keyboard.press('Control+V')
    await humanPause(2, getHumanConfig())
    const text = (await editor.innerText().catch(() => '')).replace(/\s+/g, ' ').trim()
    if (text.length >= 20) {
      return
    }
  }

  throw new Error('搜狐号 未找到正文编辑器')
}

async function waitSohuPublishSuccess(page: Page, task: PublishTask, config: ArticlePublishConfig) {
  const titleNeedle = (task.title || '').trim().slice(0, 8)
  const successTexts = config.successTexts ?? [/发布成功/, /提交成功/, /审核中/]
  const timeoutMs = Number(process.env.ARTICLE_PUBLISH_WAIT_MS || 90000)
  const started = Date.now()

  while (Date.now() - started < timeoutMs) {
    for (const pattern of successTexts) {
      if (await page.getByText(pattern).first().isVisible().catch(() => false)) {
        return page.url()
      }
    }
    await sleep(1500)
  }

  await page.goto(SOHU_MANAGE_URL, { waitUntil: 'domcontentloaded', timeout: 60000 }).catch(() => undefined)
  for (let i = 0; i < 6; i += 1) {
    await sleep(i === 0 ? 2500 : 2000)
    if (titleNeedle && (await page.getByText(titleNeedle, { exact: false }).first().isVisible().catch(() => false))) {
      return page.url()
    }
    await page.reload({ waitUntil: 'domcontentloaded' }).catch(() => undefined)
  }

  throw new Error('搜狐号 发布超时，内容管理页未找到文章')
}

export const SOHU_PUBLISH_CONFIG: ArticlePublishConfig = {
  platform: 'sohu',
  label: '搜狐号',
  editorUrl: SOHU_EDITOR_URL,
  loginPatterns: SOHU_LOGIN_PATTERNS,
  maxTitleLength: 60,
  prepareEditor: prepareSohuEditor,
  fillBody: fillSohuBody,
  afterFill: prepareSohuForm,
  waitPublishSuccess: waitSohuPublishSuccess,
  titleSelectors: [
    'input[name="title"]',
    'textarea[name="title"]',
    'input[placeholder*="请输入标题"]',
    'input[placeholder*="标题"]',
    'textarea[placeholder*="标题"]',
    '.title-input input',
    '.article-title input',
    '.publish-title input',
    '.publish-title textarea',
    '[class*="title"] input:not([type="hidden"])',
    '[contenteditable="true"][placeholder*="标题"]',
    '#title',
  ],
  editorSelectors: [
    '#editor',
    '.ql-editor',
    '.editor-content',
    '[contenteditable="true"]',
    '.ProseMirror',
  ],
  publishButtonTexts: [/^发布$/, /^提交$/, /立即发布/, /确认发布/],
  confirmButtonTexts: [/确认发布/, /^发布$/, /^确定$/],
  successTexts: [/发布成功/, /提交成功/, /审核中/, /已发布/],
  successUrlIncludes: ['/content', '/manage', '/main'],
}
