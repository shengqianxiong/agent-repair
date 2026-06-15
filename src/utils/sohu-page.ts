import type { Locator, Page } from 'playwright';
import type { PublishContent } from '../types/publisher.js';

export const MANAGE_URL = 'https://mp.sohu.com/mpfe/v3/main';
export const EDITOR_URL = 'https://mp.sohu.com/mpfe/v3/main/news/addarticle';

/** 未登录特征：跳转登录页或出现登录入口 */
export const loginPatterns = [
  'text=登录',
  'text=立即登录',
  'text=扫码登录',
  'input[placeholder*="手机号"]',
  'input[placeholder*="账号"]',
  '.login',
  '#login',
] as const;

const LOGGED_IN_PATTERNS = [
  'text=内容管理',
  'text=发布文章',
  'text=写文章',
  '.mp-main',
  '.mpfe-main',
  'input[placeholder*="标题"]',
  'textarea[placeholder*="标题"]',
  '.ql-editor',
  '[contenteditable="true"]',
] as const;

const TITLE_SELECTORS = [
  'input[placeholder*="标题"]',
  'input[placeholder*="请输入标题"]',
  'textarea[placeholder*="标题"]',
  '.article-title input',
  '.title-input input',
] as const;

const CONTENT_SELECTORS = [
  '.ql-editor',
  '.editor-content [contenteditable="true"]',
  '.ProseMirror',
  '[contenteditable="true"]',
  'textarea[placeholder*="正文"]',
  'textarea[placeholder*="内容"]',
] as const;

async function findFirstVisible(
  page: Page,
  selectors: readonly string[],
): Promise<Locator | null> {
  for (const selector of selectors) {
    const locator = page.locator(selector).first();
    const visible =
      (await locator.count()) > 0 &&
      (await locator.isVisible().catch(() => false));
    if (visible) {
      return locator;
    }
  }
  return null;
}

function isLoginUrl(url: string): boolean {
  return /passport\.sohu\.com|\/login|\/signin/i.test(url);
}

export async function open(page: Page): Promise<void> {
  await page.goto(MANAGE_URL, { waitUntil: 'domcontentloaded' });
  await page.waitForLoadState('networkidle').catch(() => undefined);

  if (!isLoginUrl(page.url())) {
    await page.goto(EDITOR_URL, { waitUntil: 'domcontentloaded' });
    await page.waitForLoadState('networkidle').catch(() => undefined);
  }
}

export async function hasLoggedInMarkers(page: Page): Promise<boolean> {
  for (const pattern of LOGGED_IN_PATTERNS) {
    const locator = page.locator(pattern).first();
    const visible =
      (await locator.count()) > 0 &&
      (await locator.isVisible().catch(() => false));
    if (visible) {
      return true;
    }
  }
  return false;
}

export async function isLoginRequired(page: Page): Promise<boolean> {
  if (isLoginUrl(page.url())) {
    return true;
  }

  if (await hasLoggedInMarkers(page)) {
    return false;
  }

  for (const pattern of loginPatterns) {
    const locator = page.locator(pattern).first();
    const visible =
      (await locator.count()) > 0 &&
      (await locator.isVisible().catch(() => false));
    if (visible) {
      return true;
    }
  }

  return !isLoginUrl(page.url()) ? false : true;
}

export async function fillTitle(page: Page, title: string): Promise<void> {
  const element = await findFirstVisible(page, TITLE_SELECTORS);
  if (!element) {
    throw new Error('搜狐号：未找到标题输入框，请确认已登录并进入文章编辑器。');
  }
  await element.fill(title);
}

export async function fillContent(page: Page, content: string): Promise<void> {
  const element = await findFirstVisible(page, CONTENT_SELECTORS);
  if (!element) {
    throw new Error('搜狐号：未找到正文编辑区域，请确认已登录并进入文章编辑器。');
  }

  const tagName = await element.evaluate((node) => node.tagName.toLowerCase());
  const isEditable = (await element.getAttribute('contenteditable')) === 'true';
  if (tagName === 'div' || isEditable) {
    await element.click();
    await element.fill(content);
    return;
  }
  await element.fill(content);
}

export async function fill(page: Page, payload: PublishContent): Promise<void> {
  if (payload.title) {
    await fillTitle(page, payload.title);
  }
  if (payload.content) {
    await fillContent(page, payload.content);
  }
}

export async function submit(page: Page): Promise<string> {
  const submitCandidates: Locator[] = [
    page.getByRole('button', { name: /发布|提交|发表/ }),
    page.locator('button:has-text("发布")'),
    page.locator('button:has-text("提交")'),
    page.locator('button:has-text("发表")'),
  ];

  for (const locator of submitCandidates) {
    const target = locator.first();
    const visible =
      (await target.count()) > 0 && (await target.isVisible().catch(() => false));
    if (visible) {
      await target.click();
      await page.waitForLoadState('networkidle').catch(() => undefined);
      return page.url();
    }
  }

  throw new Error('搜狐号：未找到发布/提交按钮，请确认编辑器页面已加载完成。');
}
