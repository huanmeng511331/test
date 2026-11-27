# 问题修复报告 — 20261127 底座变更(待办事项)

- 阶段: review / 问题修复节点
- 技能: dtazziboot-java-coding-standards v1.1.0
- 依据: `cr_report.md`(verdict: 修复后合并, P0=4 / P1=2 / P2=7)、`2026-11-27-todo-create-cr-checklist.md`
- 基线: reviewed commit `5423973`(coding)
- 日期: 2026-11-27

## 一、必修项处置(P0×4 + P1×2)

### P0-1 Controller 层单测未注册 GlobalExceptionHandler(standalone 缺省无 advice)
- 文件: `src/test/java/com/dtazziboot/base/todo/controller/TodoItemControllerTest.java`
- 修复: `MockMvcBuilders.standaloneSetup(...).setControllerAdvice(new GlobalExceptionHandler())`;负向用例可正确断言 `$.code`。
- 补充: `setUp` 中注入 `SimpleMeterRegistry` 构造真实 `TodoItemCreateMetrics`;新增用例 `should_returnSys999_when_dbWriteFails`(DB 失败→HTTP 200 + `SYS_999` + `db_fail` 计数=1.0)。

### P0-2 DB 连接无超时参数,故障时请求线程长时间挂起
- 文件: `src/main/resources/application.yml`
- 修复: JDBC URL 追加 `connectTimeout=1000&socketTimeout=3000`(毫秒);Hikari `connection-timeout: 3000`、`validation-timeout: 1000`。全部连接级超时 ≤3s,低于网关 10s 预期。

### P0-3 接口零埋点(系分 §7.1 承诺未落地)
- 新增: `src/main/java/com/dtazziboot/base/todo/metrics/TodoItemCreateMetrics.java`
  - `todo_item_create_total`(tags: source=oneapi / result=success|biz_fail|db_fail / code=OK|TODO_001~004|AUTH_001|SYS_999)
  - `todo_item_create_cost_milliseconds`(tags: source;耗时分布)
  - DB 写入失败单独计数(`recordDbFailure` 固定 code=SYS_999)。
- 修改: `TodoItemController` 注入 metrics,`try/catch(BizException→biz_fail 按错误码; RuntimeException→db_fail)后原样上抛`,业务编排版式抽取为 `doCreateTodoItem`。
- 修改: `pom.xml` 增加 `spring-boot-starter-actuator`(Micrometer 经 `/actuator/metrics` 暴露;版本由 Spring Boot parent 管理)。
- 决策: 保留系分 §7.1 应用内埋点路线;“与平台确认改网关统计”路线需外部协调,自动流水线不可达,不阻塞合并。

### P1-1 时间获取未显式传时区,宿主 UTC 时区时落库墙钟差 8 小时(M016×3)
- 新增: `src/main/java/com/dtazziboot/base/common/constant/DateTimeConstants.java`(`SYSTEM_ZONE = ZoneId.of("Asia/Shanghai")`,与 JDBC `serverTimezone` 对齐,私有构造)。
- 修改: `GmtFieldsMetaObjectHandler` 两处 `LocalDateTime.now(SYSTEM_ZONE)`;`TodoItemServiceImpl` 兜底 `now(SYSTEM_ZONE)`。

### P1-2 日志无链路标识,跨服务排障无法串联
- 修改: `LoginUserInterceptor` 注入 MDC `traceId`(优先透传网关头 `X-Trace-Id`,缺省 UUID 去横线 32 位);`preHandle=false` 拒绝分支**就地** `MDC.remove`(该分支 afterCompletion 不会回调);正常路径 `afterCompletion` 清理 MDC + LoginUserContext。
- 修改: `application.yml` `logging.pattern.console` 携带 `%X{traceId}`。

## 二、可选低风险项处置(P2×7)

| 项 | 结论 | 说明 |
|----|------|------|
| P2-2 service 层 null 入参 NPE | 已修 | `TodoItemServiceImpl.requireValidName`:request/name 为 null 或空白抛 TODO_001,新增双用例断言且不落库 |
| P2-3 `GMT_FORMATTER` 命名误导 | 已修 | 改名 `CREATED_AT_FORMATTER`;显式时区统一在 `now(SYSTEM_ZONE)` 处,格式化器不加 withZone |
| P2-7 M016 复扫 | 已复扫 | `scan-all-rules.sh`(CR 基线范围)= **No findings, exit 0**;新增 catch 块补行尾注释说明“计数后原样 throw,由全局处理器记录日志” |
| P2-5 保留 starter-validation | 保留 | 零风险移除收益低,记录为后续裁剪项 |
| P2-4 测试文件误 import java.time.LocalDateTime | 已修 | 删除未使用导入(Service/Controller 两测试文件同口径清理) |
| P2-1 应用端口仅网关可达 | 文档落地 | 写入 `docs/modules/todo/README.md` 部署约束节 |
| P2-6 生产 DB 凭据强制注入 | 文档落地 | 同上(生产禁止依赖 yml 本地默认值) |

## 三、文档同步(P0-4 附带)

- `design.md`:§7.3 功能开关改为“静态配置、重启级止血(多副本滚动),接入配置中心后升级为秒级动态止血(扩展点)”;附录验证表第 19 行同步修订。
- `docs/ARCHITECTURE.md`:开关口径同步;新增“可观测”节(指标契约、traceId 日志);测试策略节补充拦截器测试与显式 advice。
- `docs/modules/todo/README.md`:类清单补 `TodoItemCreateMetrics`;新增“可观测性”“部署约束”两节。

## 四、变更文件清单

主代码:
- 新增 `common/constant/DateTimeConstants.java`、`todo/metrics/TodoItemCreateMetrics.java`
- 修改 `TodoItemController.java`、`TodoItemServiceImpl.java`、`GmtFieldsMetaObjectHandler.java`、`LoginUserInterceptor.java`
- 修改 `pom.xml`、`application.yml`

测试(新增 3 用例 + 拦截器测试 3 用例,合计 15):
- 修改 `TodoItemControllerTest.java`(+db_fail 用例、显式 advice、SimpleMeterRegistry)
- 修改 `TodoItemServiceImplTest.java`(+null 入参兜底用例、清理导入)
- 新增 `common/auth/LoginUserInterceptorTest.java`(AUTH_001 拒绝+就地清理 / X-Trace-Id 透传+完成后清理 / 无头自动生成 32 位)

文档: `design.md`、`docs/ARCHITECTURE.md`、`docs/modules/todo/README.md`。

## 五、验证结论

- L1 静态:`scan-all-rules.sh src/main/java/com/dtazziboot/base src/test/java/com/dtazziboot/base` → **No findings(52/222 rules, exit 0)**,M016 清零,P0/P1/P2 全部清空。
  - 全仓库范围复扫时 `pom.xml` 命中 S1.1(MyBatisSqlInjection,`\$\{...\}`)为已知误报:规则针对 MyBatis mapper XML 的 `${}` 拼接,仓库无 mapper XML,pom 命中为 Maven 属性占位符。
- 跨文件契约静态核对:SYS_999 常量与测试断言一致;`GlobalExceptionHandler` WARN(Biz)/ERROR(未知,含堆栈)日志就位;`LoginUserContext.clear()/setCurrentUser` 存在;YAML 语法 `safe_load` 通过;测试捕获写法(`catchThrowableOfType`)合法。
- **[降级说明] L2 动态验证(编译/单测)未执行**:本执行环境无 JDK/Maven(`command -v java javac mvn` 为空,`/usr/lib/jvm` 不存在);后台搭建工具链时 JDK17 下载 239s 仅完成 193MB 中的 5MB(网络限速约 21KB/s),超出 5 分钟验证预算,已终止下载任务。
- 待人工/具备工具链环境执行:`mvn -q compile -DskipTests` 与 `mvn test`(预期 15 用例全绿;ControllerTest db_fail 用例依赖 `SimpleMeterRegistry` 计数断言)。

## 六、遗留与建议

1. `mvn test` 动态验证(环境就绪后)。
2. 配置中心接入后,将 `todo.create.enabled` 升级为秒级动态止血(扩展点,见 design §7.3 修订)。
3. 非业务 RuntimeException 当前按 db_fail 计数:后续若出现其他运行时异常来源,需细分 result 标签。
4. 网关需按约定透传 `X-Trace-Id`(缺省由应用自动生成,已兜底)。
