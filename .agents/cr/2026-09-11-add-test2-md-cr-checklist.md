# Code Review Checklist

> **Change** `add-test2-md` · **分支/Commit** `AI/task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-7a516d71-7d09-4e96-8403-b1922ece354d` / `0a9fcb1` · **日期** `2026-09-11`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`。本次变更**无 Java 文件**，Java 守卫触发，**跳过 `scan-all-rules.sh` 预扫**（无 Java 目标路径，脚本规则 B/M/I 仅针对 `.java` 源码）。

---

## Step 1 — 执行队列（产物 A）

**变更范围**（`git show --stat 0a9fcb1`）：

```
test2.md | 3 +++ (new file)
```

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1–G17 | S1–S10 | 总状态 |
|---|----------------------|----------|-------|-------|--------|--------|--------|
| 1 | `test2.md` | 非 Java 文件（Markdown 文档） | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | **跳过** |

- **守卫结果**：变更集中**无任何 `.java` 文件** → 按技能要求：「本次变更不包含 Java 文件，本技能仅适用于 Java 代码审查，审查终止。」逐文件审查不再展开，Step 2–5 以守卫结论收口。

---

## Step 2 — 功能（产物 B）

> 仅从需求原文提 REQ，勿臆造。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 需求为「在根目录增加test2.md文件」，When 执行编码实现，Then 仓库根目录应存在 `test2.md` | 需求原文：「在根目录增加test2.md文件」 | `test2.md` | ✅ | `test2.md`（新文件，3 行：`# Test2` / 空行 / `This is the test2.md file created in the project root.`）；`git show 0a9fcb1` 确认已提交，根目录存在该文件 |

---

## Step 3 — 可读性检查（产物 C）

> 无 Java：**整节 N/A**。

| ID | 检查项 | 状态 | 备注 |
|----|--------|------|------|
| A1 | 源文件格式 | N/A | 无 Java 文件，本技能可读性清单仅适用 `.java` |
| A2 | 源文件结构/import 顺序 | N/A | 同上 |
| A3 | 代码样式 | N/A | 同上 |
| A4 | 命名规范 | N/A | 同上 |
| A5 | 编码实践 | N/A | 同上 |
| A6 | 特定元素样式 | N/A | 同上 |
| A7 | Javadoc 规范 | N/A | 同上 |

---

## Step 4 — 可靠性检查（产物 D）

> 本次变更无 `.java` 文件，`scan-all-rules.sh` 无可扫描的 Java 目标路径，**预扫跳过**；G/S/B/M/I 清单均针对 Java 代码语义，对纯 Markdown 文件无适用项。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID 区间 | 状态 | 备注 |
|----|------|------|
| B001–B081 | N/A(无 Java) | 无 `.java` 变更，Bug 模式规则仅适用 Java 源码 |
| M001–M027 | N/A(无 Java) | 同上 |
| I001–I010 | N/A(无 Java) | 同上 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID 区间 | 状态 | 备注 |
|----|------|------|
| G1.1–G18.3 | N/A(无 Java) | 无 `.java` 变更，可靠性规则（超时/重试/资源/并发/事务/灰度等）无适用对象 |

### 4.3 安全（`security-checklist.md`）

| ID 区间 | 状态 | 备注 |
|----|------|------|
| S1.1–S10.3 | N/A(无 Java) | 无 `.java` 变更，安全规则（注入/校验/密钥/依赖）无适用对象；`test2.md` 内容为纯说明文本，无密钥/敏感信息 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID 区间 | 状态 | 备注 |
|----|------|------|
| U1.1–U2.3 | N/A(无 Java) | 无 `.java` 变更；自定义规则未启用 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件各列均非 `⬜`（唯一文件 `test2.md` 非 Java，按守卫 `跳过`/`N/A`）
- [x] Step 2 的 REQ-1 已核销为 `✅`（需求实现已验证）
- [x] Step 3 的 A1–A7 均非 `⬜`（N/A 原因已写明）
- [x] Step 4 全部 G/S 与 B/M/I 均已标注 `N/A(无 Java)` 并写明原因
- [x] Step 5 U* 已标注 `N/A` 并写明原因
- [x] 无 `❌/⚠️` 项需写入 report（report 结论见 `2026-09-11-add-test2-md-code-review.md`）
