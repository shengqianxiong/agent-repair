import { chromium } from 'playwright';
import { saveStorageState, waitForManualLogin } from '../utils/auth-storage.js';
import { hasLoggedInMarkers, isLoginRequired, open } from '../utils/sohu-page.js';

async function main(): Promise<void> {
  const browser = await chromium.launch({ headless: false });
  const context = await browser.newContext();
  const page = await context.newPage();

  try {
    await open(page);
    if (!(await isLoginRequired(page))) {
      console.log('搜狐号已处于登录状态，正在保存登录态...');
    } else {
      console.log('请在浏览器中完成搜狐号登录...');
      await waitForManualLogin(page, async () => !(await isLoginRequired(page)));
    }

    await open(page);
    if (await isLoginRequired(page) || !(await hasLoggedInMarkers(page))) {
      throw new Error('登录未完成，未能进入搜狐号内容管理页');
    }

    const savedTo = await saveStorageState(context, 'sohu');
    console.log(`搜狐号登录态已保存到 ${savedTo}`);
  } finally {
    await browser.close();
  }
}

main().catch((error: unknown) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error(message);
  process.exit(1);
});
