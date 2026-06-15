import type { Page } from 'playwright';
import type { PublishContent, Publisher, PublishResult } from '../types/publisher.js';
import { hasStorageState } from '../utils/auth-storage.js';
import { fill, isLoginRequired, open, submit } from '../utils/sohu-page.js';

export const publisher: Publisher = {
  platform: 'sohu',

  async publish(page: Page, content: PublishContent): Promise<PublishResult> {
    if (!hasStorageState('sohu')) {
      return {
        success: false,
        message:
          '搜狐号 未登录，请先在管理端触发登录或执行 npm run login:sohu',
      };
    }

    await open(page);

    if (await isLoginRequired(page)) {
      return {
        success: false,
        message:
          '搜狐号 未登录，请先在管理端触发登录或执行 npm run login:sohu',
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
