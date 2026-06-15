import { existsSync } from 'node:fs';
import { mkdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import type { BrowserContext, Page } from 'playwright';

const AUTH_DIR = path.resolve('.auth');

export function getStorageStatePath(platform: string): string {
  return path.join(AUTH_DIR, `${platform}.json`);
}

export async function ensureAuthDir(): Promise<void> {
  await mkdir(AUTH_DIR, { recursive: true });
}

export function hasStorageState(platform: string): boolean {
  return existsSync(getStorageStatePath(platform));
}

export async function saveStorageState(
  context: BrowserContext,
  platform: string,
): Promise<string> {
  await ensureAuthDir();
  const filePath = getStorageStatePath(platform);
  await context.storageState({ path: filePath });
  return filePath;
}

export async function createContextWithAuth(
  browser: import('playwright').Browser,
  platform: string,
): Promise<BrowserContext> {
  const storageStatePath = getStorageStatePath(platform);
  if (hasStorageState(platform)) {
    return browser.newContext({ storageState: storageStatePath });
  }
  return browser.newContext();
}

export async function waitForManualLogin(
  page: Page,
  isLoggedIn: () => Promise<boolean>,
  timeoutMs = 180_000,
): Promise<void> {
  const startedAt = Date.now();
  while (Date.now() - startedAt < timeoutMs) {
    if (await isLoggedIn()) {
      return;
    }
    await page.waitForTimeout(1_500);
  }
  throw new Error('登录超时，请重试');
}
