const fs = require('fs');
const path = require('path');
const puppeteer = require('puppeteer');

const outDir = path.join(__dirname, 'out');
const titles = {
  '01-overall': '1. 整体主流程',
  '02-grouping': '2. 自动分组（小组循环赛）',
  '03-roundrobin': '3. 小组循环赛对战生成',
  '04-knockout-bracket': '4. 单败淘汰赛对阵生成（含轮空）',
  '05-score-entry': '5. 录入比分（核心高频操作）',
  '06-advance-group': '6. 小组赛晋级判定（含同分排序）',
  '07-knockout-progress': '7. 淘汰赛逐轮推进（含复活）',
  '08-event-points': '8. 赛事积分结算',
};

const order = Object.keys(titles);
let body = `<h1 style="text-align:center;font-family:'WenQuanYi Micro Hei',sans-serif;">轻量级球馆赛事系统 — 关键交互流程图</h1>`;
for (const name of order) {
  const png = path.join(outDir, name + '.png');
  const b64 = fs.readFileSync(png).toString('base64');
  body += `
  <section style="page-break-inside:avoid;margin:24px 0;text-align:center;">
    <h2 style="font-family:'WenQuanYi Micro Hei',sans-serif;text-align:left;">${titles[name]}</h2>
    <img src="data:image/png;base64,${b64}" style="max-width:100%;height:auto;"/>
  </section>`;
}

const html = `<!doctype html><html><head><meta charset="utf-8">
<style>body{margin:32px;font-family:'WenQuanYi Micro Hei',sans-serif;}</style>
</head><body>${body}</body></html>`;

(async () => {
  const browser = await puppeteer.launch({ args: ['--no-sandbox', '--disable-setuid-sandbox'] });
  const page = await browser.newPage();
  await page.setContent(html, { waitUntil: 'networkidle0' });
  await page.pdf({
    path: path.join(__dirname, '赛事系统-关键流程图.pdf'),
    format: 'A4',
    printBackground: true,
    margin: { top: '15mm', bottom: '15mm', left: '12mm', right: '12mm' },
  });
  await browser.close();
  console.log('PDF generated');
})();
