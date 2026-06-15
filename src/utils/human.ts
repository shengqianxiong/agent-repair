import type { Locator, Page } from 'playwright'
import { sleep, writeClipboard } from './page.js'

export interface HumanConfig {
  enabled: boolean
  stepDelayMin: number
  stepDelayMax: number
  typeDelayMin: number
  typeDelayMax: number
  afterLoadDelay: number
  beforePublishDelay: number
  chunkPauseMin: number
  chunkPauseMax: number
}

let cachedConfig: HumanConfig | null = null

function buildHumanConfig(prefix: string, defaults: {
  base: number
  afterLoadDelay: number
  beforePublishDelay: number
  chunkPauseMin: number
  chunkPauseMax: number
  typeDelayMin: number
  typeDelayMax: number
}): HumanConfig {
  const enabled = process.env[`${prefix}_HUMAN_MODE`] !== 'false'
  const base = Number(process.env[`${prefix}_HUMAN_DELAY_MS`] || defaults.base)
  return {
    enabled,
    stepDelayMin: Number(process.env[`${prefix}_STEP_DELAY_MIN_MS`] || base),
    stepDelayMax: Number(process.env[`${prefix}_STEP_DELAY_MAX_MS`] || Math.round(base * 2.8)),
    typeDelayMin: Number(process.env[`${prefix}_TYPE_DELAY_MIN_MS`] || defaults.typeDelayMin),
    typeDelayMax: Number(process.env[`${prefix}_TYPE_DELAY_MAX_MS`] || defaults.typeDelayMax),
    afterLoadDelay: Number(process.env[`${prefix}_AFTER_LOAD_DELAY_MS`] || defaults.afterLoadDelay),
    beforePublishDelay: Number(process.env[`${prefix}_BEFORE_PUBLISH_DELAY_MS`] || defaults.beforePublishDelay),
    chunkPauseMin: Number(process.env[`${prefix}_CHUNK_PAUSE_MIN_MS`] || defaults.chunkPauseMin),
    chunkPauseMax: Number(process.env[`${prefix}_CHUNK_PAUSE_MAX_MS`] || defaults.chunkPauseMax),
  }
}

export function getHumanConfig(): HumanConfig {
  if (cachedConfig) return cachedConfig
  cachedConfig = buildHumanConfig('CSDN', {
    base: 900,
    afterLoadDelay: 4000,
    beforePublishDelay: 2500,
    chunkPauseMin: 500,
    chunkPauseMax: 1200,
    typeDelayMin: 70,
    typeDelayMax: 160,
  })
  return cachedConfig
}

export function randomBetween(min: number, max: number) {
  return Math.floor(min + Math.random() * (max - min + 1))
}

export async function humanPause(scale = 1, config = getHumanConfig()) {
  if (!config.enabled) {
    await sleep(Math.round(250 * scale))
    return
  }
  const ms = randomBetween(config.stepDelayMin, config.stepDelayMax)
  await sleep(Math.round(ms * scale))
}

export async function humanWaitAfterLoad(config: HumanConfig = getHumanConfig()) {
  if (!config.enabled) {
    await sleep(1500)
    return
  }
  await sleep(config.afterLoadDelay + randomBetween(500, 1500))
}

export async function humanClick(locator: Locator, config: HumanConfig = getHumanConfig()) {
  await humanPause(0.6, config)
  const visible = await locator.isVisible().catch(() => false)
  if (!visible) {
    await locator.click({ force: true, timeout: 15000 })
    await humanPause(0.5, config)
    return
  }

  try {
    await locator.scrollIntoViewIfNeeded({ timeout: 8000 })
    await humanPause(0.4, config)
    await locator.hover({ timeout: 5000 })
    await humanPause(0.3, config)
    await locator.click({ timeout: 15000 })
    await humanPause(0.5, config)
  } catch {
    await locator.click({ force: true, timeout: 15000 })
    await humanPause(0.5, config)
  }
}

export async function humanPasteInChunks(page: Page, text: string, config = getHumanConfig()) {
  const chunks = splitTextChunks(text, 280)
  for (let index = 0; index < chunks.length; index += 1) {
    await writeClipboard(page, chunks[index])
    await humanPause(randomBetween(6, 10) / 10, config)
    await page.keyboard.press('Control+V')
    if (index < chunks.length - 1) {
      await humanPause(randomBetween(config.chunkPauseMin, config.chunkPauseMax) / 500, config)
    }
  }
  await humanPause(0.8, config)
}

function splitTextChunks(text: string, chunkSize: number) {
  const paragraphs = text.split(/\n{2,}/)
  const chunks: string[] = []
  let current = ''

  for (const paragraph of paragraphs) {
    const block = paragraph.trim()
    if (!block) continue
    const candidate = current ? `${current}\n\n${block}` : block
    if (candidate.length <= chunkSize) {
      current = candidate
      continue
    }
    if (current) chunks.push(current)
    if (block.length <= chunkSize) {
      current = block
      continue
    }
    for (let i = 0; i < block.length; i += chunkSize) {
      chunks.push(block.slice(i, i + chunkSize))
    }
    current = ''
  }

  if (current) chunks.push(current)
  return chunks.length ? chunks : [text]
}
