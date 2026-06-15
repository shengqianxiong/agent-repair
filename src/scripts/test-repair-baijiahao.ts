import { resolveCoverUploadPath } from '../utils/image.js'

async function main(): Promise<void> {
  const localPath = await resolveCoverUploadPath('https://example.com/cover.jpg').catch(() => undefined)
  if (localPath) {
    console.log(JSON.stringify({ ok: true, localPath }))
    return
  }
  console.log(JSON.stringify({ ok: true, skipped: 'cover download mock' }))
}

main().catch((error: unknown) => {
  console.error(error)
  process.exit(1)
})
