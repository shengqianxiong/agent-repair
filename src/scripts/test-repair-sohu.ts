import { chromium } from 'playwright';
import { publisher } from '../publishers/sohu.publisher.js';
import { createContextWithAuth } from '../utils/auth-storage.js';

async function main(): Promise<void> {
  const browser = await chromium.launch({ headless: true });
  const context = await createContextWithAuth(browser, 'sohu');
  const page = await context.newPage();

  try {
    const result = await publisher.publish(page, {
      title: 'repair-test-title',
      content: 'repair-test-content',
    });

    if (!result.success) {
      throw new Error(result.message ?? '搜狐号发布修复测试失败');
    }

    console.log(`搜狐号发布修复测试通过: ${result.url ?? page.url()}`);
  } finally {
    await browser.close();
  }
}

main().catch((error: unknown) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error(message);
  process.exit(1);
});
