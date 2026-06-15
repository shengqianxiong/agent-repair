import type { Page } from 'playwright'
import { humanWaitAfterLoad } from './human.js'

export function isUrlLogin(url: string, patterns: string[]) {
  if (!url || url === 'about:blank') return true
  return patterns.some((pattern) => url.includes(pattern))
}

export async function openEditor(page: Page, editorUrl: string) {
  await page.goto(editorUrl, { waitUntil: 'domcontentloaded', timeout: 60000 })
  await page.waitForLoadState('networkidle').catch(() => undefined)
  await humanWaitAfterLoad()
}
