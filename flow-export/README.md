# 赛事系统关键流程图（可重新生成）

本目录保存轻量级球馆赛事系统的关键交互流程图源码与渲染脚本。

## 内容

- `src/*.mmd`：8 张流程图的 Mermaid 源码
- `赛事系统-关键流程图.md`：合并版（含全部 Mermaid 源码，可直接在支持 Mermaid 的编辑器中预览）
- `theme.css` / `puppeteer.json`：渲染时的字体与沙箱配置
- `make-pdf.js`：将渲染出的 PNG 合并为单个 PDF

## 流程图列表

1. 整体主流程
2. 自动分组（小组循环赛）
3. 小组循环赛对战生成
4. 单败淘汰赛对阵生成（含轮空）
5. 录入比分
6. 小组赛晋级判定（含同分排序）
7. 淘汰赛逐轮推进（含复活）
8. 赛事积分结算

## 重新生成图片 / PDF

```bash
npm install @mermaid-js/mermaid-cli

# 渲染所有图为 PNG + SVG
for f in src/*.mmd; do
  name=$(basename "$f" .mmd)
  npx mmdc -i "$f" -o "out/$name.png" -b white -p puppeteer.json -C theme.css --scale 2
  npx mmdc -i "$f" -o "out/$name.svg" -b white -p puppeteer.json -C theme.css
done

# 合并为单个 PDF
node make-pdf.js
```

> 中文渲染依赖 CJK 字体（如 `WenQuanYi Micro Hei` 或 `Noto Sans CJK SC`），如缺失请先安装。
