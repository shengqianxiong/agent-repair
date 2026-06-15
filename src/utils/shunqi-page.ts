import type { Locator, Page } from 'playwright';
import type { PublishContent } from '../types/publisher.js';

export const MANAGE_URL = 'https://cp.11467.com/home/personal/index';
export const EDITOR_URL = 'https://cp.11467.com/home/personal/news_add';

export const MIN_DETAIL_LENGTH = 200;

/** 未登录特征：页面出现登录表单或快捷登录 */
export const loginPatterns = [
  'text=用户登录',
  'text=顺企网欢迎您',
  'text=登 录',
  'input[type="password"]',
  'text=快捷登录',
] as const;

const TITLE_SELECTORS = [
  'input[name="title"]',
  'input[placeholder*="标题"]',
  'input[name*="title" i]',
] as const;

/** 顺企网「详细描述」字段，平台要求不少于 200 字 */
const DETAIL_SELECTORS = [
  'textarea[name="content"]',
  'textarea[name="body"]',
  'textarea[name="detail"]',
  'textarea[placeholder*="详细"]',
  'textarea[placeholder*="描述"]',
  'textarea[placeholder*="内容"]',
  '#content',
  '#detail',
] as const;

const CONTENT_SELECTORS = [
  ...DETAIL_SELECTORS,
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

export function countPlainTextChars(text: string): number {
  return text.replace(/<[^>]+>/g, '').replace(/\s+/g, '').length;
}

export function ensureMinDetailLength(
  content: string,
  title: string,
  minLength = MIN_DETAIL_LENGTH,
): string {
  const plain = content.replace(/<[^>]+>/g, '').trim();
  if (countPlainTextChars(plain) >= minLength) {
    return plain;
  }

  let combined = `${title}\n\n${plain}`.trim();
  const filler =
    '本文详细介绍相关产品与服务信息，包括功能特点、应用场景及优势说明，便于用户全面了解并做出选择。';

  while (countPlainTextChars(combined) < minLength) {
    combined += filler;
  }

  return combined;
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
    throw new Error('shunqi：未找到标题输入框，请检查 TITLE_SELECTORS。');
  }
  await element.fill(title);
}

export async function fillDetail(page: Page, content: string): Promise<void> {
  const element = await findFirstVisible(page, DETAIL_SELECTORS);
  if (!element) {
    throw new Error(
      'shunqi：未找到「详细描述」输入框。平台要求不少于 200 字，请补充 DETAIL_SELECTORS。',
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

export async function fillContent(page: Page, content: string): Promise<void> {
  await fillDetail(page, content);
}

export async function fill(page: Page, payload: PublishContent): Promise<void> {
  if (payload.title) {
    await fillTitle(page, payload.title);
  }
  if (payload.content) {
    const detail = ensureMinDetailLength(payload.content, payload.title ?? '');
    await fillDetail(page, detail);
  }
}

export async function submit(page: Page): Promise<string> {
  const detailField = await findFirstVisible(page, DETAIL_SELECTORS);
  if (detailField) {
    const current = (await detailField.inputValue().catch(() => '')) || '';
    if (countPlainTextChars(current) < MIN_DETAIL_LENGTH) {
      throw new Error(
        `shunqi：详细描述仅 ${countPlainTextChars(current)} 字，不足 ${MIN_DETAIL_LENGTH} 字，无法提交。`,
      );
    }
  }

  const submitCandidates: Locator[] = [
    page.getByRole('button', { name: /发布|提交|保存|发表/ }),
    page.locator('button:has-text("发布")'),
    page.locator('button:has-text("提交")'),
    page.locator('input[type="submit"][value*="发布"]'),
    page.locator('a:has-text("发布")'),
  ];

  for (const locator of submitCandidates) {
    const target = locator.first();
    const visible =
      (await target.count()) > 0 && (await target.isVisible().catch(() => false));
    if (visible) {
      await target.click();
      await page.waitForLoadState('networkidle').catch(() => undefined);

      const validationError = page.locator('text=详细内容不足200字');
      if (await validationError.isVisible().catch(() => false)) {
        throw new Error(
          'shunqi：平台提示「详细内容不足200字，很难被收录，请添加请详细描述」。',
        );
      }

      return page.url();
    }
  }

  throw new Error('shunqi：未找到发布/提交按钮，请补充 submit 选择器。');
}
