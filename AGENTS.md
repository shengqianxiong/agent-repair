# agent-repair

## Cursor Cloud specific instructions

- The Node.js toolchain is pre-installed via `nvm` (default `v22.22.2`); `npm`, `pnpm`, and `yarn` are all available on `PATH`.
- 服务端目录为 `aiproduct5-server/`（Spring Boot 2.6 多模块 Maven），构建命令：`cd aiproduct5-server && mvn package -DskipTests -q`。
- 管理端 `aiproduct5-admin/` 使用 npm；`npm run build` 会自动安装依赖后构建。
- 用户端 `aiproduct5-app/` 使用 npm；安装依赖后按 `package.json` scripts 启动。
