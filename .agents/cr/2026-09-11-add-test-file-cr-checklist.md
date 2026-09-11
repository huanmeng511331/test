# Code Review Checklist

> **Change** `add-test-file` · **分支/Commit** `AI/task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-b1e5b6e7-ed5a-464d-9d8f-5712c3a095a3` / `94234b8` · **日期** `2026-09-11`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|--------|
| 1 | `sort.go` | 变更文件，非 Java | 跳过 | 跳过 | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | ⚠️ 已审 |
| 2 | `review_test.go` | 变更文件，非 Java | 跳过 | 跳过 | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | N/A(非 Java) | ⚠️ 已审 |

> **守卫判定**：变更范围内无 `.java` 文件，本次变更不包含 Java 文件，本技能仅适用于 Java 代码审查，审查终止。
>
> **自动化预扫**：`bash references/script/scan-all-rules.sh .` → 输出：`=== No findings. 52/222 rules scanned ===`（无命中）

---

## Step 2 — 功能（产物 B）

> 变更范围内无 Java 文件，无 spec 文档可对照。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| — | — | — | — | N/A | 本次变更不包含 Java 文件 |

---

## Step 3 — 可读性检查（产物 C）

> 无 Java：**整节 N/A**。

| ID | 检查项 | 状态 | 备注（命中写 `path:行号`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | N/A | 变更文件非 Java |
| A2 | 源文件结构/import 顺序 | N/A | 变更文件非 Java |
| A3 | 代码样式 | N/A | 变更文件非 Java |
| A4 | 命名规范 | N/A | 变更文件非 Java |
| A5 | 编码实践 | N/A | 变更文件非 Java |
| A6 | 特定元素样式 | N/A | 变更文件非 Java |
| A7 | Javadoc 规范 | N/A | 变更文件非 Java |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫结果：`scan-all-rules.sh` 输出 `No findings. 52/222 rules scanned`。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 非 Java 文件 |
| B002 | N/A | 非 Java 文件 |
| B003 | N/A | 非 Java 文件 |
| B004 | N/A | 非 Java 文件 |
| B005 | N/A | 非 Java 文件 |
| B006 | N/A | 非 Java 文件 |
| B007 | N/A | 非 Java 文件 |
| B008 | N/A | 非 Java 文件 |
| B009 | N/A | 非 Java 文件 |
| B010 | N/A | 非 Java 文件 |
| B011 | N/A | 非 Java 文件 |
| B012 | N/A | 非 Java 文件 |
| B013 | N/A | 非 Java 文件 |
| B014 | N/A | 非 Java 文件 |
| B015 | N/A | 非 Java 文件 |
| B016 | N/A | 非 Java 文件 |
| B017 | N/A | 非 Java 文件 |
| B018 | N/A | 非 Java 文件 |
| B019 | N/A | 非 Java 文件 |
| B020 | N/A | 非 Java 文件 |
| B021 | N/A | 非 Java 文件 |
| B022 | N/A | 非 Java 文件 |
| B023 | N/A | 非 Java 文件 |
| B024 | N/A | 非 Java 文件 |
| B025 | N/A | 非 Java 文件 |
| B026 | N/A | 非 Java 文件 |
| B027 | N/A | 非 Java 文件 |
| B028 | N/A | 非 Java 文件 |
| B029 | N/A | 非 Java 文件 |
| B030 | N/A | 非 Java 文件 |
| B031 | N/A | 非 Java 文件 |
| B032 | N/A | 非 Java 文件 |
| B033 | N/A | 非 Java 文件 |
| B034 | N/A | 非 Java 文件 |
| B035 | N/A | 非 Java 文件 |
| B036 | N/A | 非 Java 文件 |
| B037 | N/A | 非 Java 文件 |
| B038 | N/A | 非 Java 文件 |
| B039 | N/A | 非 Java 文件 |
| B040 | N/A | 非 Java 文件 |
| B041 | N/A | 非 Java 文件 |
| B042 | N/A | 非 Java 文件 |
| B043 | N/A | 非 Java 文件 |
| B044 | N/A | 非 Java 文件 |
| B045 | N/A | 非 Java 文件 |
| B046 | N/A | 非 Java 文件 |
| B047 | N/A | 非 Java 文件 |
| B048 | N/A | 非 Java 文件 |
| B049 | N/A | 非 Java 文件 |
| B050 | N/A | 非 Java 文件 |
| B051 | N/A | 非 Java 文件 |
| B052 | N/A | 非 Java 文件 |
| B053 | N/A | 非 Java 文件 |
| B054 | N/A | 非 Java 文件 |
| B055 | N/A | 非 Java 文件 |
| B056 | N/A | 非 Java 文件 |
| B057 | N/A | 非 Java 文件 |
| B058 | N/A | 非 Java 文件 |
| B059 | N/A | 非 Java 文件 |
| B060 | N/A | 非 Java 文件 |
| B061 | N/A | 非 Java 文件 |
| B062 | N/A | 非 Java 文件 |
| B063 | N/A | 非 Java 文件 |
| B064 | N/A | 非 Java 文件 |
| B065 | N/A | 非 Java 文件 |
| B066 | N/A | 非 Java 文件 |
| B067 | N/A | 非 Java 文件 |
| B068 | N/A | 非 Java 文件 |
| B069 | N/A | 非 Java 文件 |
| B070 | N/A | 非 Java 文件 |
| B071 | N/A | 非 Java 文件 |
| B072 | N/A | 非 Java 文件 |
| B073 | N/A | 非 Java 文件 |
| B074 | N/A | 非 Java 文件 |
| B075 | N/A | 非 Java 文件 |
| B076 | N/A | 非 Java 文件 |
| B077 | N/A | 非 Java 文件 |
| B078 | N/A | 非 Java 文件 |
| B079 | N/A | 非 Java 文件 |
| B080 | N/A | 非 Java 文件 |
| B081 | N/A | 非 Java 文件 |
| M001 | N/A | 非 Java 文件 |
| M002 | N/A | 非 Java 文件 |
| M003 | N/A | 非 Java 文件 |
| M004 | N/A | 非 Java 文件 |
| M005 | N/A | 非 Java 文件 |
| M006 | N/A | 非 Java 文件 |
| M007 | N/A | 非 Java 文件 |
| M008 | N/A | 非 Java 文件 |
| M009 | N/A | 非 Java 文件 |
| M010 | N/A | 非 Java 文件 |
| M011 | N/A | 非 Java 文件 |
| M012 | N/A | 非 Java 文件 |
| M013 | N/A | 非 Java 文件 |
| M014 | N/A | 非 Java 文件 |
| M015 | N/A | 非 Java 文件 |
| M016 | N/A | 非 Java 文件 |
| M017 | N/A | 非 Java 文件 |
| M018 | N/A | 非 Java 文件 |
| M019 | N/A | 非 Java 文件 |
| M020 | N/A | 非 Java 文件 |
| M021 | N/A | 非 Java 文件 |
| M022 | N/A | 非 Java 文件 |
| M023 | N/A | 非 Java 文件 |
| M024 | N/A | 非 Java 文件 |
| M025 | N/A | 非 Java 文件 |
| M026 | N/A | 非 Java 文件 |
| M027 | N/A | 非 Java 文件 |
| I001 | N/A | 非 Java 文件 |
| I002 | N/A | 非 Java 文件 |
| I003 | N/A | 非 Java 文件 |
| I004 | N/A | 非 Java 文件 |
| I005 | N/A | 非 Java 文件 |
| I006 | N/A | 非 Java 文件 |
| I007 | N/A | 非 Java 文件 |
| I008 | N/A | 非 Java 文件 |
| I009 | N/A | 非 Java 文件 |
| I010 | N/A | 非 Java 文件 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 非 Java 文件 |
| G1.2 | N/A | 非 Java 文件 |
| G1.3 | N/A | 非 Java 文件 |
| G1.4 | N/A | 非 Java 文件 |
| G2.1 | N/A | 非 Java 文件 |
| G2.2 | N/A | 非 Java 文件 |
| G2.3 | N/A | 非 Java 文件 |
| G3.1 | N/A | 非 Java 文件 |
| G3.2 | N/A | 非 Java 文件 |
| G4.1 | N/A | 非 Java 文件 |
| G4.2 | N/A | 非 Java 文件 |
| G4.3 | N/A | 非 Java 文件 |
| G4.4 | N/A | 非 Java 文件 |
| G5.1 | N/A | 非 Java 文件 |
| G6.1 | N/A | 非 Java 文件 |
| G6.2 | N/A | 非 Java 文件 |
| G7.1 | N/A | 非 Java 文件 |
| G7.2 | N/A | 非 Java 文件 |
| G7.3 | N/A | 非 Java 文件 |
| G8.1 | N/A | 非 Java 文件 |
| G8.2 | N/A | 非 Java 文件 |
| G8.3 | N/A | 非 Java 文件 |
| G8.4 | N/A | 非 Java 文件 |
| G8.5 | N/A | 非 Java 文件 |
| G8.6 | N/A | 非 Java 文件 |
| G9.1 | N/A | 非 Java 文件 |
| G9.2 | N/A | 非 Java 文件 |
| G9.3 | N/A | 非 Java 文件 |
| G10.1 | N/A | 非 Java 文件 |
| G10.2 | N/A | 非 Java 文件 |
| G11.1 | N/A | 非 Java 文件 |
| G11.2 | N/A | 非 Java 文件 |
| G11.3 | N/A | 非 Java 文件 |
| G11.4 | N/A | 非 Java 文件 |
| G12.1 | N/A | 非 Java 文件 |
| G12.2 | N/A | 非 Java 文件 |
| G13.1 | N/A | 非 Java 文件 |
| G14.1 | N/A | 非 Java 文件 |
| G14.2 | N/A | 非 Java 文件 |
| G14.3 | N/A | 非 Java 文件 |
| G14.4 | N/A | 非 Java 文件 |
| G15.1 | N/A | 非 Java 文件 |
| G15.2 | N/A | 非 Java 文件 |
| G15.3 | N/A | 非 Java 文件 |
| G16.1 | N/A | 非 Java 文件 |
| G16.2 | N/A | 非 Java 文件 |
| G16.3 | N/A | 非 Java 文件 |
| G16.4 | N/A | 非 Java 文件 |
| G17.1 | N/A | 非 Java 文件 |
| G17.2 | N/A | 非 Java 文件 |
| G17.3 | N/A | 非 Java 文件 |
| G18.1 | N/A | 非 Java 文件 |
| G18.2 | N/A | 非 Java 文件 |
| G18.3 | N/A | 非 Java 文件 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 非 Java 文件 |
| S1.2 | N/A | 非 Java 文件 |
| S1.3 | N/A | 非 Java 文件 |
| S2.1 | N/A | 非 Java 文件 |
| S2.2 | N/A | 非 Java 文件 |
| S2.3 | N/A | 非 Java 文件 |
| S3.1 | N/A | 非 Java 文件 |
| S3.2 | N/A | 非 Java 文件 |
| S3.3 | N/A | 非 Java 文件 |
| S4.1 | N/A | 非 Java 文件 |
| S4.2 | N/A | 非 Java 文件 |
| S5.1 | N/A | 非 Java 文件 |
| S5.2 | N/A | 非 Java 文件 |
| S6.1 | N/A | 非 Java 文件 |
| S6.2 | N/A | 非 Java 文件 |
| S6.3 | N/A | 非 Java 文件 |
| S7.1 | N/A | 非 Java 文件 |
| S7.2 | N/A | 非 Java 文件 |
| S7.3 | N/A | 非 Java 文件 |
| S8.1 | N/A | 非 Java 文件 |
| S8.2 | N/A | 非 Java 文件 |
| S8.3 | N/A | 非 Java 文件 |
| S8.4 | N/A | 非 Java 文件 |
| S9.1 | N/A | 非 Java 文件 |
| S9.2 | N/A | 非 Java 文件 |
| S9.3 | N/A | 非 Java 文件 |
| S9.4 | N/A | 非 Java 文件 |
| S10.1 | N/A | 非 Java 文件 |
| S10.2 | N/A | 非 Java 文件 |
| S10.3 | N/A | 非 Java 文件 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 非 Java 文件 |
| U1.2 | N/A | 非 Java 文件 |
| U1.3 | N/A | 非 Java 文件 |
| U2.1 | N/A | 非 Java 文件 |
| U2.2 | N/A | 非 Java 文件 |
| U2.3 | N/A | 非 Java 文件 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [ ] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`

> **注**：本次变更不包含 Java 文件，本技能仅适用于 Java 代码审查，审查终止。所有步骤均标 `N/A` 或 `跳过`。
