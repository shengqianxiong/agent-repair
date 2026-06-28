const fs = require('fs');
const path = require('path');
const { marked } = require('marked');
const puppeteer = require('puppeteer');

const mdPath = path.join(__dirname, '赛事系统-完整需求与设计文档.md');
const outDir = path.join(__dirname, 'out');
let md = fs.readFileSync(mdPath, 'utf8');

// 按出现顺序，把 ```mermaid 代码块替换为已渲染好的 PNG（section 8 共 8 张，顺序一致）
const pngOrder = [
  '01-overall', '02-grouping', '03-roundrobin', '04-knockout-bracket',
  '05-score-entry', '06-advance-group', '07-knockout-progress', '08-event-points',
];
let idx = 0;
const renderer = new marked.Renderer();
const origCode = renderer.code.bind(renderer);
renderer.code = (a, b, c) => {
  // 兼容两种签名：新版传 token 对象 {text, lang}，旧版传 (code, infostring, escaped)
  const isToken = a && typeof a === 'object';
  const lang = ((isToken ? a.lang : b) || '').trim();
  if (lang === 'mermaid' && idx < pngOrder.length) {
    const png = path.join(outDir, pngOrder[idx] + '.png');
    idx += 1;
    if (fs.existsSync(png)) {
      const b64 = fs.readFileSync(png).toString('base64');
      return `<div class="diagram"><img src="data:image/png;base64,${b64}"/></div>`;
    }
  }
  return origCode(a, b, c);
};

const bodyHtml = marked.parse(md, { renderer });
const html = `<!doctype html><html><head><meta charset="utf-8">
<style>
  body{margin:36px;font-family:'WenQuanYi Micro Hei',sans-serif;line-height:1.6;color:#222;}
  h1{font-size:24px;border-bottom:2px solid #444;padding-bottom:8px;}
  h2{font-size:19px;margin-top:28px;border-bottom:1px solid #ddd;padding-bottom:4px;}
  h3{font-size:16px;margin-top:18px;}
  table{border-collapse:collapse;width:100%;margin:12px 0;font-size:13px;}
  th,td{border:1px solid #ccc;padding:6px 8px;text-align:left;}
  th{background:#f3f3f7;}
  code{background:#f2f2f2;padding:1px 5px;border-radius:3px;font-size:13px;}
  pre{background:#f6f6f6;padding:12px;border-radius:6px;overflow:auto;}
  pre code{background:none;padding:0;}
  .diagram{text-align:center;margin:16px 0;page-break-inside:avoid;}
  .diagram img{max-width:88%;height:auto;}
  h2,h3{page-break-after:avoid;}
</style></head><body>${bodyHtml}</body></html>`;

(async () => {
  const browser = await puppeteer.launch({ args: ['--no-sandbox', '--disable-setuid-sandbox'] });
  const page = await browser.newPage();
  await page.setContent(html, { waitUntil: 'networkidle0' });
  await page.pdf({
    path: path.join(__dirname, '赛事系统-完整需求与设计文档.pdf'),
    format: 'A4',
    printBackground: true,
    margin: { top: '14mm', bottom: '14mm', left: '12mm', right: '12mm' },
  });
  await browser.close();
  console.log('Doc PDF generated, mermaid blocks replaced:', idx);
})();
