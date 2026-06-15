import { chromium } from 'playwright';
import { publisher } from '../publishers/shunqi.publisher.js';
import {
  countPlainTextChars,
  ensureMinDetailLength,
  MIN_DETAIL_LENGTH,
} from '../utils/shunqi-page.js';

async function main(): Promise<void> {
  const shortContent = '测试短文';
  const padded = ensureMinDetailLength(shortContent, '测试标题');
  if (countPlainTextChars(padded) < MIN_DETAIL_LENGTH) {
    throw new Error(
      `ensureMinDetailLength failed: got ${countPlainTextChars(padded)} chars`,
    );
  }

  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();

  try {
    const result = await publisher.publish(page, {
      title: '自动化修复验证标题',
      content: shortContent,
    });
    console.log(JSON.stringify(result));
  } finally {
    await browser.close();
  }
}

main().catch((error: unknown) => {
  console.error(error);
  process.exit(1);
});
