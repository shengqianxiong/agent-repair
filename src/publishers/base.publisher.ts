import type { BrowserContext, Page } from 'playwright'
import type { PublishTask } from '../api.js'

export interface PublishResult {
  success: boolean
  resultUrl?: string
  errorMsg?: string
  screenshotPath?: string
}

export interface Publisher {
  platform: string
  publish(context: BrowserContext, task: PublishTask): Promise<PublishResult>
}

export async function createPage(context: BrowserContext): Promise<Page> {
  return context.newPage()
}
