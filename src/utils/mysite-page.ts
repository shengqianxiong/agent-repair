import type { Locator, Page } from 'playwright';
import type { PublishContent } from '../types/publisher.js';

export const MANAGE_URL = 'https://work.weixin.qq.com/';
export const EDITOR_URL = 'https://work.weixin.qq.com/';

/** 未登录特征：页面出现企业登录/注册入口 */
export const loginPatterns = [
  'a.index_top_operation_loginBtn',
  'text=企业登录',
  'a.index_top_operation_registerBtn',
  'text=注册',
] as const;

const TITLE_SELECTORS = [
  'input[placeholder*="标题"]',
  'input[name*="title" i]',
  'textarea[placeholder*="标题"]',
] as const;

const CONTENT_SELECTORS = [
  'textarea[placeholder*="内容"]',
  'textarea[name*="content" i]',
  '.editor [contenteditable="true"]',
  '[contenteditable="true"]',
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

export async function open(page: Page): Promise<void> {
  await page.goto(EDITOR_URL, { waitUntil: 'domcontentloaded' });
}

export async function isLoginRequired(page: Page): Promise<boolean> {
  for (const pattern of loginPatterns) {
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

export async function fillTitle(page: Page, title: string): Promise<void> {
  const element = await findFirstVisible(page, TITLE_SELECTORS);
  if (!element) {
    throw new Error(
      'mysite：未找到标题输入框。当前 URL 为企业微信官网首页，探测结果仅含导航链接，无标题控件。请提供真实编辑器 URL 并补充 TITLE_SELECTORS。',
    );
  }
  await element.fill(title);
}

export async function fillContent(page: Page, content: string): Promise<void> {
  const element = await findFirstVisible(page, CONTENT_SELECTORS);
  if (!element) {
    throw new Error(
      'mysite：未找到正文编辑区域。DOM 探测未发现 input/textarea/contenteditable 编辑器控件。请登录后进入内容发布页并补充 CONTENT_SELECTORS。',
    );
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
    page.getByRole('button', { name: /发布|提交|保存|发表/ }),
    page.locator('button:has-text("发布")'),
    page.locator('button:has-text("提交")'),
    page.locator('a:has-text("发布")'),
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

  throw new Error(
    'mysite：未找到发布/提交按钮。DOM 探测仅发现「下载」「企业登录」「注册」等官网导航元素，请人工登录并进入内容发布页后补充 submit 选择器。',
  );
}
