'use strict'

const fs = require('fs')
const path = require('path')
const { spawnSync } = require('child_process')

const projectRoot = path.resolve(__dirname, '..')

// 源码在根目录时需指定 UNI_INPUT_DIR，避免 Windows cmd 无法解析 UNI_INPUT_DIR=. 语法
process.env.UNI_INPUT_DIR = process.env.UNI_INPUT_DIR || projectRoot

function resolveUniBin() {
  const candidates = [
    '@dcloudio/vite-plugin-uni/bin/uni.js',
    path.join(projectRoot, 'node_modules', '@dcloudio', 'vite-plugin-uni', 'bin', 'uni.js'),
    path.join(projectRoot, 'node_modules', '.bin', 'uni')
  ]

  for (const candidate of candidates) {
    try {
      const resolved = candidate.startsWith('@')
        ? require.resolve(candidate, { paths: [projectRoot] })
        : path.resolve(candidate)

      if (fs.existsSync(resolved)) {
        return resolved
      }
    } catch (_) {
      // try next candidate
    }
  }

  console.error(
    'Cannot find uni CLI. Run "npm install" in verifydevplan-1780929413041-app first.'
  )
  process.exit(1)
}

const uniBin = resolveUniBin()
const args = process.argv.slice(2)

const result = spawnSync(process.execPath, [uniBin, ...args], {
  stdio: 'inherit',
  env: process.env,
  cwd: projectRoot
})

process.exit(result.status ?? 1)
