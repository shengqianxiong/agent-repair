import fs from 'node:fs/promises'
import os from 'node:os'
import path from 'node:path'

const IMAGE_URL_PATTERN = /!\[[^\]]*]\((https?:\/\/[^\s)]+)\)|https?:\/\/[^\s"'<>]+\.(?:png|jpe?g|gif|webp)(?:\?[^\s"'<>]*)?/gi

export function extractImageUrls(content: string): string[] {
  const urls = new Set<string>()
  for (const match of content.matchAll(IMAGE_URL_PATTERN)) {
    const url = (match[1] || match[0]).trim()
    if (url) urls.add(url)
  }
  return [...urls]
}

function guessExtension(contentType: string | null, url: string) {
  if (contentType?.includes('png')) return '.png'
  if (contentType?.includes('webp')) return '.webp'
  if (contentType?.includes('gif')) return '.gif'
  if (contentType?.includes('jpeg') || contentType?.includes('jpg')) return '.jpg'
  const fromUrl = path.extname(new URL(url).pathname)
  return fromUrl || '.jpg'
}

export async function resolveCoverUploadPath(coverImage?: string): Promise<string | undefined> {
  if (!coverImage) return undefined
  const trimmed = coverImage.trim()
  if (!trimmed) return undefined
  if (!/^https?:\/\//i.test(trimmed)) {
    return trimmed
  }

  const response = await fetch(trimmed)
  if (!response.ok) {
    throw new Error(`封面图片下载失败: ${response.status}`)
  }
  const buffer = Buffer.from(await response.arrayBuffer())
  const ext = guessExtension(response.headers.get('content-type'), trimmed)
  const filePath = path.join(os.tmpdir(), `baijiahao-cover-${Date.now()}${ext}`)
  await fs.writeFile(filePath, buffer)
  return filePath
}
