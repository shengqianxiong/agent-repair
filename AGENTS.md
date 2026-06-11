# agent-repair

## Cursor Cloud specific instructions

### Projects in this repo
- `aiproduct2-app` — uni-app (Vue 3) used-clothing recycling client (H5 / WeChat mini-program). Run H5 with `npm run dev:h5` (Vite, port 8080). Mini-program: `npm run dev:mp-weixin`.
- `login-app` — uni-app (Vue 3) login client (H5 / mini-program / APP), Vite-based.
- `login-admin` — Vue 3 + Vite admin frontend.
- `login-server` — Spring Boot 2.6.15 multi-module Maven backend (`sqx-common`, `sqx-framework`, `sqx-modules`, `sqx-admin`). Targets **Java 1.8**. Runnable jar: `sqx-admin/target/sqx-admin-1.0.0.jar`. Serves on port `9295` with context-path `/sqx_fast`.

### Backend (login-server) environment
- Build/run with **JDK 1.8** even though the system default `java` is 21. JDK 8 lives at `/usr/lib/jvm/java-8-openjdk-amd64`; set `JAVA_HOME` to it for Maven and when running the jar (e.g. `JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 mvn -DskipTests clean package`).
- Build: `cd login-server && JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 mvn -DskipTests clean package`.
- Run: `JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 /usr/lib/jvm/java-8-openjdk-amd64/bin/java -jar sqx-admin/target/sqx-admin-1.0.0.jar`.
- DB config (`sqx-admin/src/main/resources/application.yml`): MySQL at `127.0.0.1:3306`, database `login`, user/pass `root`/`root`.

### MySQL 5.7 (via Docker)
- MySQL 5.7 is not in the Ubuntu 24.04 apt repos, so it runs as a Docker container. Docker runs as a daemon (`sudo dockerd`); systemd is not available in this VM, so start it manually if not running (e.g. in a tmux session) and use `sudo docker ...`.
- Start MySQL 5.7: `sudo docker run -d --name mysql57 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=login -v /workspace/login-server/db/login_account.sql:/docker-entrypoint-initdb.d/01_login_account.sql:ro mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci` (or `sudo docker start mysql57` if it already exists).
- Schema/seed scripts live in `login-server/db/` (`login_account.sql` seeds accounts `admin`/`user`; `bar_product.sql` targets a separate `travel` database).
- Docker note: this is Docker 29 with `fuse-overlayfs` storage driver and `containerd-snapshotter` disabled (`/etc/docker/daemon.json`); iptables is set to legacy. These are required for docker-in-docker to work in this VM.

### Known issue (not environment-related)
- `login-server` currently fails to boot with `ConflictingBeanDefinitionException`: two classes (`com.sqx.framework.config.WebMvcConfig` and `com.sqx.modules.login.config.WebMvcConfig`) both register the bean name `webMvcConfig`. This is a code defect, not a setup problem; the build succeeds and the JVM/DB are fine.
