'use strict'

const path = require('path')
const { spawnSync } = require('child_process')

// 源码在根目录时需指定 UNI_INPUT_DIR，避免 Windows cmd 无法解析 UNI_INPUT_DIR=. 语法
process.env.UNI_INPUT_DIR = process.env.UNI_INPUT_DIR || '.'

const uniBin = require.resolve('@dcloudio/vite-plugin-uni/bin/uni.js')
const args = process.argv.slice(2)

const result = spawnSync(process.execPath, [uniBin, ...args], {
  stdio: 'inherit',
  env: process.env,
  cwd: path.resolve(__dirname, '..')
})

process.exit(result.status ?? 1)
