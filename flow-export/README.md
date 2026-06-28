# 赛事系统关键流程图（可重新生成）

本目录保存轻量级球馆赛事系统的关键交互流程图源码与渲染脚本。

## 内容

- `src/*.mmd`：8 张流程图的 Mermaid 源码
- `赛事系统-关键流程图.md`：流程图合并版（含全部 Mermaid 源码）
- `赛事系统-完整需求与设计文档.md`：完整需求与设计文档（定位+模块+开发计划+数据表+接口+页面原型+流程图+规则）
- `theme.css` / `puppeteer.json`：渲染时的字体与沙箱配置
- `make-pdf.js`：将渲染出的 PNG 合并为单个 PDF（流程图合集）
- `make-doc-pdf.js`：将完整文档 Markdown 渲染为 PDF（自动内嵌流程图）

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

# 合并流程图为单个 PDF
node make-pdf.js

# 将完整需求文档渲染为 PDF（内嵌流程图）
npm install marked
node make-doc-pdf.js
```

> 中文渲染依赖 CJK 字体（如 `WenQuanYi Micro Hei` 或 `Noto Sans CJK SC`），如缺失请先安装。
