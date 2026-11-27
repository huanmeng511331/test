# Task 1 Brief: 在仓库根目录创建文本文档 document.txt

## 需求原文

> 生成一个 txt 文档

## 任务规格（控制器依据自主决策优先级裁定：上下文已验证事实 > 风险最低/改动最小 > 行业最佳实践）

- **产物路径（唯一交付物）**：`document.txt`，位于仓库根目录（cwd）。
- **文件内容（逐字转录以下内容，UTF-8 编码，LF 换行，纯文本 .txt）**：

```
自动生成文本文档
================

生成日期：2026-11-27
生成方式：subagent-driven-development 编码流水线

说明
----
本文档为本次任务（阶段：coding，节点：编码实现）的交付产物，
按需求"生成一个 txt 文档"创建，位于仓库根目录。
```

（代码块内第一行 `自动生成文本文档` 为文件首行，随后空行、标题分隔行 `================`，依次类推；文件以一个换行符结尾。）

## 全局约束（硬性，违反即失败）

1. **Git 只读**：禁止一切 git 写操作（commit / add / push / checkout / stash 等）。新文件保持为未跟踪的工作树改动。
2. **仅新建 `document.txt`**：不得创建其他文件，不得修改任何已有文件（sort.go / review_test.go / README.md / *.md 等）。
3. 产物路径相对 cwd（仓库根目录），严禁写到仓库外。
4. 纯文本 .txt；不得使用 HTML/Markdown 之外的二进制格式。

## 验证要求（本任务无测试套件，验证即：文件存在 + 内容逐字一致）

- 文件存在于 `document.txt`（仓库根目录）。
- `cat document.txt` 输出与本简报"文件内容"代码块逐字一致（UTF-8、LF、末尾单个换行）。
- `git status --short` 显示仅新增 `?? document.txt`，无其他改动。

## 报告契约

完整报告写入 `.superpowers/sdd/task-1-report.md`；最终消息只返回：Status（DONE / DONE_WITH_CONCERNS / BLOCKED / NEEDS_CONTEXT）、一行验证摘要、疑虑（如有）、报告文件路径。
