import path from 'node:path'

export function screenshotPath(taskId: string | number, platform: string, stage: string) {
  return path.join('screenshots', `${taskId}-${platform}-${stage}.png`)
}
