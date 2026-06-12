import type { Locator, Page } from 'playwright'
import { downloadImageToTemp, resolveImageUrl } from './image.js'
import {
  detectCsdnRisk,
  humanClick,
  humanFillTextarea,
  humanPause,
  humanPasteInChunks,
  humanType,
  humanWaitAfterLoad,
  humanWaitBeforePublish,
} from './human.js'
import { evaluateInBrowser, evaluateInBrowserNoArg, evaluateOnLocatorNoArg } from './browser-eval.js'
import { removeMaskLayers, setInputValueByScript, sleep, syncCsdnTitleDisplay, writeClipboard } from './page.js'

const EDITOR_URL = 'https://editor.csdn.net/md/?not_checkout=1'
const DEFAULT_TAG = '小程序开发'

export function getCsdnEditorUrl(articleId?: string | number) {
  if (!articleId) return EDITOR_URL
  return `https://editor.csdn.net/md?not_checkout=1&articleId=${articleId}`
}

export function normalizeCsdnTitle(title: string, fallback = '自动化发布文章') {
  let value = (title || '').replace(/\s+/g, ' ').trim()
  if (!value) value = fallback
  if (value.length < 5) value = `${value} · 实战分享`.slice(0, 100)
  if (value.length > 100) value = value.slice(0, 100)
  return value
}

export async function dismissCsdnOverlays(page: Page) {
  if (await isCsdnPublishDialogOpen(page)) {
    return
  }

  await removeMaskLayers(page)

  const closeSelectors = [
    '.guide-close',
    '.el-message-box__close',
    '.beginnerGuide-box .close',
  ]
  for (const selector of closeSelectors) {
    const locator = page.locator(selector).first()
    if (await locator.count()) {
      await humanClick(locator).catch(() => undefined)
    }
  }

  const dismissButtons = ['我知道了', '知道了', '跳过', '暂不', '以后再说', '下次再说']
  for (const label of dismissButtons) {
    const btn = page.getByRole('button', { name: label })
    if (await btn.count()) {
      await humanClick(btn.first()).catch(() => undefined)
    }
  }

  await humanPause(0.4)
}

export async function waitCsdnEditorReady(page: Page) {
  await page.waitForLoadState('domcontentloaded', { timeout: 60000 }).catch(() => undefined)
  await humanWaitAfterLoad()
  await detectCsdnRisk(page)

  await page.locator('input.article-bar__title--input, input[placeholder*="请输入文章标题"], .article-bar__title-display')
    .first()
    .waitFor({ state: 'attached', timeout: 45000 })

  const readySelectors = [
    '.article-bar',
    '.operate-box',
    'button.btn.btn-publish',
    '.cledit-section',
    '.CodeMirror',
    '.editor-container',
  ]
  for (const selector of readySelectors) {
    const locator = page.locator(selector).first()
    try {
      await locator.waitFor({ state: 'visible', timeout: 15000 })
      await humanPause(0.8)
      return
    } catch {
      // try next
    }
  }

  throw new Error('CSDN 编辑器加载失败')
}

export async function readCsdnTitle(page: Page) {
  return evaluateInBrowserNoArg(page, () => {
    function cleanTitle(value: string) {
      const text = value.trim()
      if (!text || text === '【无标题】') return ''
      return text
    }

    const input = document.querySelector('input.article-bar__title--input, input[placeholder*="请输入文章标题"]') as HTMLInputElement | null
    if (input && input.value) {
      const text = cleanTitle(input.value)
      if (text) return text
    }

    const editable = document.querySelector('.article-bar__title[contenteditable="true"]') as HTMLElement | null
    if (editable && editable.textContent) {
      const text = cleanTitle(editable.textContent)
      if (text) return text
    }

    const display = document.querySelector('.article-bar__title-display') as HTMLElement | null
    if (display && display.textContent) {
      const text = cleanTitle(display.textContent)
      if (text && text.indexOf('请输入文章标题') < 0) return text
    }
    return ''
  })
}

export async function fillCsdnTitle(page: Page, title: string) {
  const normalized = normalizeCsdnTitle(title)

  const strategies = [
    async () => fillCsdnTitleByPlaceholder(page, normalized),
    async () => fillCsdnTitleByHiddenInput(page, normalized),
    async () => fillCsdnTitleByScript(page, normalized),
  ]

  for (const strategy of strategies) {
    try {
      if (await strategy()) {
        await humanPause(0.8)
        const current = await readCsdnTitle(page)
        if (current.length >= 5) return normalized
      }
    } catch {
      // try next strategy
    }
  }

  const finalTitle = await readCsdnTitle(page)
  if (finalTitle.length >= 5) return finalTitle
  throw new Error('CSDN 标题填写失败，标题需 5~100 个字')
}

async function fillCsdnTitleByPlaceholder(page: Page, title: string) {
  const bar = page.locator('.article-bar, .article-bar__input-box').first()
  await humanClick(bar)
  await humanPause(0.4)

  const placeholder = page.locator('.article-bar').getByText(/请输入文章标题/)
  if (await placeholder.count()) {
    await humanClick(placeholder.first())
  }

  await page.keyboard.press('Control+A')
  await humanPause(0.3)
  await humanType(page, title)
  return (await readCsdnTitle(page)).length >= 5
}

async function fillCsdnTitleByHiddenInput(page: Page, title: string) {
  const input = page.locator('input.article-bar__title--input, input[placeholder*="请输入文章标题"]').first()
  if (!(await input.count())) return false

  await input.click({ force: true })
  await humanPause(0.3)
  await page.keyboard.press('Control+A')
  await humanType(page, title)
  await syncCsdnTitleDisplay(page, title)
  return (await readCsdnTitle(page)).length >= 5
}

async function fillCsdnTitleByScript(page: Page, title: string) {
  const filled = await setInputValueByScript(page, [
    'input.article-bar__title--input',
    'input[placeholder*="请输入文章标题"]',
  ], title)
  if (!filled) return false
  await syncCsdnTitleDisplay(page, title)
  await humanPause(0.5)
  return (await readCsdnTitle(page)).length >= 5
}

export async function insertCsdnEditorImages(page: Page, imageUrls: string[]) {
  const unique = [...new Set(imageUrls.map((item) => resolveImageUrl(item)).filter(Boolean))]
  if (!unique.length) return

  for (const imageUrl of unique) {
    await humanPause(1)
    const localPath = await downloadImageToTemp(imageUrl)
    const imageButtons = [
      page.locator('[title*="图片"]'),
      page.locator('[aria-label*="图片"]'),
      page.locator('button, a, span').filter({ hasText: /^图片$/ }),
      page.locator('.toolbar [class*="image"]'),
      page.locator('.cledit-toolbar [class*="image"]'),
      page.locator('.editor-toolbar [class*="image"]'),
    ]

    let uploaded = false
    for (const locator of imageButtons) {
      if (!(await locator.count())) continue
      try {
        const [fileChooser] = await Promise.all([
          page.waitForEvent('filechooser', { timeout: 8000 }),
          humanClick(locator.first()),
        ])
        await fileChooser.setFiles(localPath)
        uploaded = true
        await humanPause(2.2)
        break
      } catch {
        // try next button
      }
    }

    if (!uploaded) {
      const fileInput = page.locator('.cledit-section input[type="file"], .editor-container input[type="file"], input[type="file"]').first()
      if (await fileInput.count()) {
        await fileInput.setInputFiles(localPath)
        await humanPause(2.2)
      }
    }
  }
}

export async function fillCsdnContent(page: Page, content: string) {
  const editorAreas = [
    '.cledit-section .CodeMirror-scroll',
    '.cledit-section',
    '.CodeMirror-scroll',
    '.editor-container',
  ]

  for (const selector of editorAreas) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    await humanClick(locator)
    await humanPause(0.6)
    await page.keyboard.press('Control+A')
    await humanPause(0.4)
    await humanPasteInChunks(page, content)
    await humanPause(1.2)
    await detectCsdnRisk(page)
    return
  }

  const injected = await evaluateInBrowser(page, (text) => {
    const cmElements = Array.from(document.querySelectorAll('.CodeMirror'))
    for (const element of cmElements) {
      const cm = (element as HTMLElement & { CodeMirror?: { setValue: (v: string) => void; refresh: () => void; focus: () => void } }).CodeMirror
      if (cm) {
        cm.focus()
        return 'codemirror-api'
      }
    }
    return ''
  }, content)

  if (injected) {
    await writeClipboard(page, content)
    await humanPause(0.8)
    await page.keyboard.press('Control+V')
    await humanPause(1.5)
    return
  }

  throw new Error('未找到 CSDN 正文编辑器')
}

export async function clickPublishArticle(page: Page) {
  if (await isCsdnPublishDialogOpen(page)) {
    return waitCsdnPublishDialog(page)
  }

  const title = await readCsdnTitle(page)
  if (title.length < 5) {
    throw new Error('标题长度应在 5 ~ 100 个字之间，请先填写标题')
  }

  await humanWaitBeforePublish()
  await dismissCsdnOverlays(page)
  await detectCsdnRisk(page)

  await page.locator('.article-bar, .operate-box').first().waitFor({ state: 'visible', timeout: 20000 }).catch(() => undefined)

  const publishBtn = await resolveCsdnEditorPublishButton(page)
  await publishBtn.waitFor({ state: 'visible', timeout: 30000 })
  await humanClick(publishBtn)
  return waitCsdnPublishDialog(page)
}

async function resolveCsdnEditorPublishButton(page: Page) {
  const selectors = [
    'button.btn.btn-publish',
    'button.btn-publish',
    '.operate-box button.btn-publish',
    '.article-bar button.btn-publish',
    '.article-bar .btn-publish',
    'button[class*="btn-publish"]',
    'xpath=//button[contains(@class,"btn-publish") and contains(normalize-space(.),"发布")]',
    'xpath=//div[contains(@class,"operate-box")]//button[contains(normalize-space(.),"发布文章")]',
  ]
  for (const selector of selectors) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    try {
      await locator.waitFor({ state: 'visible', timeout: 8000 })
      return locator
    } catch {
      // try next
    }
  }

  const byRole = page.getByRole('button', { name: /发布文章|发布/ }).first()
  if (await byRole.count()) {
    try {
      await byRole.waitFor({ state: 'visible', timeout: 8000 })
      return byRole
    } catch {
      // try next
    }
  }

  const byText = page.locator('button').filter({ hasText: /发布文章/ }).first()
  if (await byText.count()) {
    try {
      await byText.waitFor({ state: 'visible', timeout: 8000 })
      return byText
    } catch {
      // try next
    }
  }

  throw new Error('未找到 CSDN 编辑器发布按钮，请确认编辑器已加载且已登录')
}

export async function fillCsdnPublishDialog(page: Page, keyword: string, summary: string, coverImage?: string) {
  const dialog = await waitCsdnPublishDialog(page)
  await detectCsdnRisk(page)

  const tags = buildCsdnTags(keyword)
  const tagFilled = await fillCsdnTags(page, dialog, tags)
  if (!tagFilled) {
    throw new Error('CSDN 发布弹窗标签填写失败')
  }

  await humanPause(0.9)
  await fillCsdnCoverImage(page, dialog, coverImage)
  await humanPause(0.8)
  await fillCsdnSummary(page, dialog, summary)
  await humanPause(0.7)
  await selectFirstCategoryIfNeeded(page, dialog)
}

async function fillCsdnCoverImage(page: Page, dialog: Locator, coverImage?: string) {
  if (!coverImage?.trim()) return

  const contentCoverSelectors = [
    dialog.locator('.cover-box img'),
    dialog.locator('.img-list img'),
    dialog.locator('[class*="cover"] img'),
    dialog.locator('.content-img img'),
    dialog.locator('img[src*="http"]'),
  ]

  for (const locator of contentCoverSelectors) {
    if (!(await locator.count())) continue
    const image = locator.first()
    try {
      if (await image.isVisible()) {
        await humanClick(image)
        await humanPause(1)
        return
      }
    } catch {
      // try next
    }
  }

  try {
    const localPath = await downloadImageToTemp(coverImage)
    await humanPause(0.8)
    const uploadTriggers = [
      dialog.getByText('从本地上传'),
      dialog.locator('[class*="upload"]').filter({ hasText: /上传|本地/ }),
      dialog.locator('.cover-box, [class*="cover"]').getByText(/上传/),
    ]

    for (const trigger of uploadTriggers) {
      if (!(await trigger.count())) continue
      try {
        const [fileChooser] = await Promise.all([
          page.waitForEvent('filechooser', { timeout: 10000 }),
          humanClick(trigger.first()),
        ])
        await fileChooser.setFiles(localPath)
        await humanPause(2.5)
        return
      } catch {
        // try next trigger
      }
    }

    const fileInput = dialog.locator('input[type="file"]').first()
    if (await fileInput.count()) {
      await fileInput.setInputFiles(localPath)
      await humanPause(2.5)
    }
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error)
    console.warn(`[csdn] 封面图处理跳过: ${message}`)
  }
}

async function isCsdnPublishDialogOpen(page: Page) {
  const dialog = getCsdnPublishDialog(page)
  if (!(await dialog.count())) return false
  return dialog.first().isVisible().catch(() => false)
}

function getCsdnPublishDialog(page: Page) {
  return page.locator('.modal__publish-article, .modal.modal__inner-1, .modal')
    .filter({ hasText: '文章标签' })
    .first()
}

async function waitCsdnPublishDialog(page: Page) {
  const dialog = getCsdnPublishDialog(page)
  await dialog.waitFor({ state: 'visible', timeout: 20000 })
  await humanPause(1)
  return dialog
}

async function readCsdnTagsInDialog(dialog: Locator) {
  return evaluateOnLocatorNoArg(dialog, (root) => {
    const box = root.querySelector('.form-tag-box')
    if (!box) return [] as string[]

    const tags: string[] = []
    const skip = ['添加文章标签', '文章标签', '添加', '标签']
    const nodes = box.querySelectorAll('.el-tag, .el-tag__content, .tag__item, .tag-item, .mark_selection_title_el_tag')
    for (let i = 0; i < nodes.length; i += 1) {
      const node = nodes[i]
      const raw = node.textContent || ''
      const text = raw.replace(/[×x+]/g, '').trim()
      if (!text || text.length > 20) continue

      let shouldSkip = false
      for (let j = 0; j < skip.length; j += 1) {
        if (text.indexOf(skip[j]) >= 0) {
          shouldSkip = true
          break
        }
      }
      if (shouldSkip) continue
      tags.push(text)
    }
    return Array.from(new Set(tags))
  })
}

async function fillCsdnTags(page: Page, dialog: Locator, tags: string[]) {
  const existing = await readCsdnTagsInDialog(dialog)
  if (existing.length > 0) {
    return true
  }

  const candidates = [...new Set([...tags, DEFAULT_TAG, '小程序', '前端开发'])]
  for (const tag of candidates) {
    await humanPause(0.7)
    const selected = await selectCsdnTag(page, dialog, tag)
    if (selected) {
      const current = await readCsdnTagsInDialog(dialog)
      if (current.length > 0) return true
    }
  }

  return (await readCsdnTagsInDialog(dialog)).length > 0
}

async function selectCsdnTag(page: Page, dialog: Locator, tag: string) {
  const addBtn = dialog.locator('button.tag__btn-tag, .tag__btn-tag').first()
  await addBtn.waitFor({ state: 'visible', timeout: 10000 })
  await humanClick(addBtn)

  const inputSelectors = [
    dialog.locator('.form-tag-box input'),
    dialog.locator('.mark_selection input'),
    dialog.locator('.tag__input input'),
    dialog.locator('input[placeholder*="标签"]'),
    page.locator('.form-tag-box input:visible'),
    page.locator('.mark_selection input:visible'),
    page.locator('input[placeholder*="标签"]:visible'),
  ]

  let typed = false
  for (const locator of inputSelectors) {
    if (!(await locator.count())) continue
    const input = locator.first()
    try {
      if (!(await input.isVisible())) continue
      await humanClick(input)
      await page.keyboard.press('Control+A')
      await humanPause(0.3)
      await humanType(page, tag)
      typed = true
      await humanPause(1.2)
      break
    } catch {
      // try next
    }
  }

  if (!typed) {
    await humanType(page, tag)
    await humanPause(1)
  }

  const suggestionSelectors = [
    page.locator('.tag-recommend li'),
    page.locator('.tag-recommend-item'),
    page.locator('.tag-recommend .item'),
    page.locator('.mark_selection li'),
    page.locator('.form-tag-box li'),
    page.locator('[class*="recommend"] li'),
    page.locator('.el-select-dropdown__item:visible'),
    page.locator('.el-popper:visible li'),
  ]

  for (const suggestions of suggestionSelectors) {
    const exact = suggestions.filter({ hasText: new RegExp(`^${escapeRegExp(tag)}$`) }).first()
    if (await exact.count()) {
      await humanClick(exact)
      return true
    }
  }

  for (const suggestions of suggestionSelectors) {
    const partial = suggestions.filter({ hasText: tag }).first()
    if (await partial.count()) {
      await humanClick(partial)
      return true
    }
  }

  for (const suggestions of suggestionSelectors) {
    const miniProgram = suggestions.filter({ hasText: /小程序/ }).first()
    if (await miniProgram.count()) {
      await humanClick(miniProgram)
      return true
    }
  }

  await humanPause(0.5)
  await page.keyboard.press('Enter')
  await humanPause(0.8)
  return (await readCsdnTagsInDialog(dialog)).length > 0
}

async function fillCsdnSummary(page: Page, dialog: Locator, summary: string) {
  const text = summary.replace(/\s+/g, ' ').slice(0, 256)
  const selectors = [
    dialog.locator('textarea[placeholder*="展现列表"]'),
    dialog.locator('textarea[placeholder*="摘要"]'),
    dialog.locator('.desc-box textarea'),
    dialog.locator('textarea.el-textarea__inner'),
    dialog.locator('textarea'),
  ]
  for (const locator of selectors) {
    if (!(await locator.count())) continue
    try {
      const target = locator.first()
      if (!(await target.isVisible())) continue
      const current = await target.inputValue().catch(() => '')
      if (current.trim()) return
      await humanFillTextarea(target, page, text)
      return
    } catch {
      // try next
    }
  }
}

async function selectFirstCategoryIfNeeded(page: Page, dialog: Locator) {
  const requiredHint = dialog.getByText(/请选择|必选|分类专栏/).first()
  if (!(await requiredHint.count())) return

  const categorySelect = dialog.locator('.el-select, .select-category, [class*="category"]').first()
  if (await categorySelect.count()) {
    await humanClick(categorySelect)
    await humanPause(0.7)
    const option = page.locator('.el-select-dropdown__item:visible, .el-cascader-menu__item:visible').first()
    if (await option.count()) {
      await humanClick(option)
      await humanPause(0.6)
    }
  }
}

export async function submitCsdnPublish(page: Page) {
  const dialog = await waitCsdnPublishDialog(page)
  await humanPause(1.2)
  await detectCsdnRisk(page)

  const publishBtn = dialog.locator('.modal__button-bar button.btn-b-red, button.btn-b-red')
    .filter({ hasText: '发布文章' })
    .last()

  if (!(await publishBtn.count())) {
    throw new Error('未找到发布弹窗中的确认发布按钮')
  }

  await humanClick(publishBtn)
  return waitCsdnPublishSuccess(page)
}

export async function waitCsdnPublishSuccess(page: Page, timeoutMs = 90000) {
  const start = Date.now()
  while (Date.now() - start < timeoutMs) {
    await detectCsdnRisk(page)
    const url = page.url()
    if (/blog\.csdn\.net\/article\//.test(url)) return url
    if (/mp\.csdn\.net\/mp_blog\/creation\/success\//.test(url)) return url

    const successHints = [
      page.getByText('发布成功'),
      page.getByText('文章发布成功'),
      page.getByText('提交成功'),
      page.locator('.el-message--success'),
    ]
    for (const locator of successHints) {
      if (await locator.count()) {
        await humanPause(1.5)
        const currentUrl = page.url()
        if (/blog\.csdn\.net\/article\//.test(currentUrl)) return currentUrl
        if (/mp\.csdn\.net\/mp_blog\/creation\/success\//.test(currentUrl)) return currentUrl

        const articleLink = page.locator('a[href*="blog.csdn.net/article"], a[href*="mp.csdn.net/mp_blog/creation/success"]').first()
        if (await articleLink.count()) {
          const href = await articleLink.getAttribute('href')
          if (href) return href
        }
      }
    }

    const errorHint = page
      .locator('.el-message--error, .el-message-box, .modal, [class*="toast"], [class*="message"]')
      .filter({ hasText: /发布失败|操作过于频繁|访问过于频繁|标题长度|必填|请填写|不能为空|请选择/ })
    if (await errorHint.first().isVisible().catch(() => false)) {
      const text = (await errorHint.first().innerText().catch(() => '')).trim()
      throw new Error(`CSDN 发布失败: ${text || '请检查标签、分类或摘要'}`)
    }

    await sleep(1200)
  }
  throw new Error('CSDN 发布超时，未检测到成功状态')
}

function buildCsdnTags(keyword: string) {
  const normalized = keyword.trim()
  const tags = [
    normalized,
    normalized.includes('小程序') ? DEFAULT_TAG : '',
    DEFAULT_TAG,
    '小程序',
  ].filter(Boolean)
  return [...new Set(tags)].slice(0, 5)
}

function escapeRegExp(value: string) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}
