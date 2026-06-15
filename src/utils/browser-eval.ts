import type { Locator, Page } from 'playwright'

function stripEsbuildNameHelpers(code: string) {
  let result = code.trim()
  let prev = ''
  while (result !== prev) {
    prev = result
    result = result.replace(/__name\(\s*([\s\S]*?)\s*,\s*['"][^'"]*['"]\s*\)/g, '$1')
  }
  return result
}

function toBrowserFunction<Args extends unknown[], Result>(handler: (...args: Args) => Result) {
  const cleaned = stripEsbuildNameHelpers(handler.toString())
  const factory = new Function(`return (${cleaned})`) as () => (...args: Args) => Result
  return factory()
}

export async function evaluateInBrowser<Arg, Result>(page: Page, handler: (arg: Arg) => Result, arg: Arg) {
  const fn = toBrowserFunction(handler)
  return page.evaluate(fn as never, arg as never) as Promise<Result>
}

export async function evaluateInBrowserVoid(page: Page, handler: () => void) {
  const fn = toBrowserFunction(handler)
  return page.evaluate(fn as never)
}

export async function evaluateOnLocator<Arg, Result>(
  locator: Locator,
  handler: (root: HTMLElement, arg: Arg) => Result,
  arg: Arg,
) {
  const fn = toBrowserFunction(handler)
  return locator.evaluate(fn as never, arg as never) as Promise<Result>
}
