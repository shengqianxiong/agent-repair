import type { BrowserContext } from 'playwright'
import type { PublishTask } from '../api.js'
import { extractImageUrls } from '../utils/image.js'
import { screenshotPath as buildScreenshotPath } from '../utils/paths.js'
import {
  BAIJIAHAO_EDITOR_URL,
  clickBaijiahaoPublishButton,
  confirmBaijiahaoPublish,
  fillBaijiahaoContent,
  fillBaijiahaoPublishDialog,
  fillBaijiahaoSummary,
  fillBaijiahaoTitle,
  prepareInlineBaijiahaoCover,
  readBaijiahaoTitle,
  selectBaijiahaoAiDeclaration,
  isBaijiahaoLoginPage,
  waitBaijiahaoEditorReady,
  waitBaijiahaoPublishSuccess,
} from '../utils/baijiahao-page.js'
import { getBaijiahaoHumanConfig, humanPause } from '../utils/human.js'
import { sleep } from '../utils/page.js'
import { createPage, type Publisher, type PublishResult } from './base.publisher.js'

export class BaijiahaoPublisher implements Publisher {
  platform = 'baijiahao'

  async publish(context: BrowserContext, task: PublishTask): Promise<PublishResult> {
    const page = await createPage(context)
    const config = getBaijiahaoHumanConfig()
    let normalizedTitle = task.title
    try {
      await page.goto(BAIJIAHAO_EDITOR_URL, { waitUntil: 'domcontentloaded', timeout: 90000 })
      await waitBaijiahaoEditorReady(page)

      if (isBaijiahaoLoginPage(page.url())) {
        throw new Error('百家号未登录，请先在管理端触发登录或执行 npm run login:baijiahao')
      }

      normalizedTitle = await fillBaijiahaoTitle(page, task.title)
      await humanPause(1.5, config)
      await fillBaijiahaoContent(page, task.content)
      await humanPause(1, config)

      const titleAfterContent = await readBaijiahaoTitle(page)
      if (titleAfterContent.length > normalizedTitle.length + 20) {
        normalizedTitle = await fillBaijiahaoTitle(page, task.title)
        await humanPause(1, config)
      }

      await humanPause(1, config)
      await fillBaijiahaoSummary(page, task.content)
      await humanPause(1.5, config)
      await selectBaijiahaoAiDeclaration(page)
      await humanPause(1, config)

      const resultUrl = await this.submitPublish(page, task, normalizedTitle)

      const screenshot = buildScreenshotPath(task.id, task.platform, 'success')
      await page.screenshot({ path: screenshot, fullPage: true })

      return {
        success: true,
        resultUrl,
        screenshotPath: screenshot,
      }
    } catch (error) {
      const screenshot = buildScreenshotPath(task.id, task.platform, 'error')
      await page.screenshot({ path: screenshot, fullPage: true }).catch(() => undefined)
      return {
        success: false,
        errorMsg: error instanceof Error ? error.message : String(error),
        screenshotPath: screenshot,
      }
    } finally {
      const keepMs = Number(process.env.BAIJIAHAO_KEEP_OPEN_MS || 0)
      if (keepMs > 0) {
        await sleep(keepMs)
      }
      await page.close()
    }
  }

  private async submitPublish(
    page: Awaited<ReturnType<typeof createPage>>,
    task: PublishTask,
    normalizedTitle: string,
  ) {
    const coverImage = task.coverImage || extractImageUrls(task.content)[0]
    if (!coverImage) {
      throw new Error('百家号 缺少封面图片，请提供 coverImage 或正文内图片')
    }
    await prepareInlineBaijiahaoCover(page, coverImage)
    await clickBaijiahaoPublishButton(page)
    await fillBaijiahaoPublishDialog(page, coverImage)
    await confirmBaijiahaoPublish(page)
    return waitBaijiahaoPublishSuccess(page, normalizedTitle)
  }
}
