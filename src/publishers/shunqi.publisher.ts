import type { Page } from 'playwright';
import type { PublishContent, Publisher, PublishResult } from '../types/publisher.js';
import {
  ensureMinDetailLength,
  fill,
  isLoginRequired,
  open,
  submit,
} from '../utils/shunqi-page.js';

export const publisher: Publisher = {
  platform: 'shunqi',

  async publish(page: Page, content: PublishContent): Promise<PublishResult> {
    await open(page);

    if (await isLoginRequired(page)) {
      return {
        success: false,
        message: 'shunqi：检测到未登录，请先完成顺企网账号登录后再发布。',
      };
    }

    const normalized: PublishContent = {
      title: content.title,
      content: ensureMinDetailLength(content.content, content.title),
    };

    await fill(page, normalized);
    const url = await submit(page);

    return {
      success: true,
      url,
    };
  },
};
