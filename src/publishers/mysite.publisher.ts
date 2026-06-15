import type { Page } from 'playwright';
import type { PublishContent, Publisher, PublishResult } from '../types/publisher.js';
import { fill, isLoginRequired, open, submit } from '../utils/mysite-page.js';

export const publisher: Publisher = {
  platform: 'mysite',

  async publish(page: Page, content: PublishContent): Promise<PublishResult> {
    await open(page);

    if (await isLoginRequired(page)) {
      return {
        success: false,
        message:
          'mysite：检测到未登录（页面存在「企业登录」入口），请先完成企业微信登录后再发布。',
      };
    }

    await fill(page, content);
    const url = await submit(page);

    return {
      success: true,
      url,
    };
  },
};
