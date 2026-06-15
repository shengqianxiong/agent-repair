import type { Page } from 'playwright'
import { evaluateInBrowser, evaluateInBrowserVoid, evaluateOnLocator } from './browser-eval.js'

export async function sleep(ms: number) {
  await new Promise((resolve) => setTimeout(resolve, ms))
}

export async function removeMaskLayers(page: Page) {
  await evaluateInBrowserVoid(page, () => {
    const nodes = document.querySelectorAll('.mark-mask-box-div, .mask, .modal-mask')
    for (let i = 0; i < nodes.length; i += 1) {
      nodes[i].remove()
    }
  })
}

export async function clickByText(page: Page, text: string, selector = 'button') {
  const locator = page.locator(selector).filter({ hasText: text }).first()
  await locator.waitFor({ state: 'visible', timeout: 15000 })
  await removeMaskLayers(page)
  await locator.click({ force: true })
}

export async function fillFirstVisible(page: Page, selectors: string[], value: string) {
  for (const selector of selectors) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    try {
      if (!(await locator.isVisible())) continue
      await locator.fill(value)
      return true
    } catch {
      // try next selector
    }
  }
  return false
}

export async function fillFirstMatch(page: Page, selectors: string[], value: string) {
  for (const selector of selectors) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue

    try {
      await locator.click({ force: true, timeout: 5000 })
    } catch {
      // ignore click failure and continue with fill/type
    }

    try {
      await locator.fill(value, { force: true, timeout: 5000 })
      return true
    } catch {
      try {
        await locator.press('Control+A')
        await page.keyboard.type(value, { delay: 20 })
        return true
      } catch {
        // try next selector
      }
    }
  }
  return false
}

export async function setInputValueByScript(page: Page, selectors: string[], value: string) {
  return evaluateInBrowser(page, ({ selectors: cssSelectors, value: text }) => {
    for (const selector of cssSelectors) {
      const node = document.querySelector(selector) as HTMLInputElement | HTMLTextAreaElement | null
      if (!node) continue
      node.removeAttribute('aria-hidden')
      node.style.display = 'block'
      node.style.opacity = '1'
      node.value = text
      node.dispatchEvent(new Event('input', { bubbles: true }))
      node.dispatchEvent(new Event('change', { bubbles: true }))
      node.dispatchEvent(new Event('blur', { bubbles: true }))
      return true
    }
    return false
  }, { selectors, value })
}

export async function setContentEditableText(page: Page, selectors: string[], value: string) {
  for (const selector of selectors) {
    const locator = page.locator(selector).first()
    if (!(await locator.count())) continue
    try {
      await locator.click({ force: true })
      await evaluateOnLocator(locator, (node, text) => {
        const el = node as HTMLElement
        el.focus()
        el.textContent = text
        el.dispatchEvent(new Event('input', { bubbles: true }))
        el.dispatchEvent(new Event('change', { bubbles: true }))
      }, value)
      return true
    } catch {
      // try next selector
    }
  }
  return false
}

export async function writeClipboard(page: Page, text: string) {
  await page.context().grantPermissions(['clipboard-read', 'clipboard-write'])
  await evaluateInBrowser(page, (content) => {
    return navigator.clipboard.writeText(content)
  }, text)
}
