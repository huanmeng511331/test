# Task 1 Brief: 建立可运行测试基线

## 背景（一行）
仓库唯一业务代码是 sort.go（package main，导出 Sort/IsSorted，quicksort 实现），
当前没有任何可运行的测试；本任务为其建立测试基线。

## 已验证事实（可直接信任，勿重复验证）
- Go 1.24.5 在 /usr/local/go/bin（不在 PATH）。所有 go 命令前执行：
  `export PATH=/usr/local/go/bin:$PATH`
- 仓库无 go.mod。`go test` 在 module 模式下无法运行。
- review_test.go 现内容为伪测试（func main() + `fmt.Println(x / 0)`），
  已实测导致 `go test` 编译失败：`./review_test.go:8:18: invalid operation: division by zero`。
  修复其编译破坏是本任务范围内动作（保留文件名，重写内容）。
- sort.go 经评审为正确实现，禁止修改。

## 你要做的事（精确值，逐字使用）
1. 新建 `go.mod`，内容恰好为：
   ```
   module sortapp

   go 1.24
   ```
   （无 require，无外部依赖。）
2. 新建 `sort_test.go`（package main），包含：
   - TestSort：表驱动。用例至少覆盖：nil、[]、[1]、两元素（含逆序对）、
     已排序、严格逆序、全相等元素、含重复、含负数、正负混合；
     期望输出一律用排序后的正确序列字面量或 sort.Ints 求得；
     另含固定种子（rand.New(rand.NewSource(42))）的随机对拍子测试：
     对长度 0..64 的每个 n，生成随机 []int，与 sort.Ints 结果逐一比对。
   - TestIsSorted：表驱动，覆盖 true/false 两侧及空、单元素、重复边界。
   - 表内用例必须带 name 字段（t.Run 子测试），失败信息用 %v 打印输入。
   - 断言 Sort 返回升序且长度一致。
3. 重写 `review_test.go`（package main），删除现有全部内容，替换为恰好一个回归测试：
   - TestSortDoesNotMutateInput：构造含重复与负数的输入，先深拷贝留底，
     调 Sort 后用 slices.Equal 断言原输入未被修改，同时断言返回值已升序。
   - 文件中不得出现 func main，不得出现任何除法表达式。
4. 验收命令（按顺序，全部须退出码 0，且输出无 FAIL/杂音）：
   ```
   export PATH=/usr/local/go/bin:$PATH
   go test ./...
   go vet ./...
   ```
   将两条命令及关键输出原文记入报告。

## 边界与禁止
- 禁止一切 git 写操作（不得 commit/add/push/checkout；git status/log/diff/show 可用）。
- 禁止使用 shell grep/glob。
- 禁止修改 sort.go、README.md、test2.md、test0817.md、tmp/、.agents/。
- 不得新增除 go.mod、sort_test.go 之外的文件，不得引入外部依赖。

## 报告契约
- 完整报告写入：.superpowers/sdd/task-1-report.md
  （实现内容、测试结果含命令原文与输出、变更文件清单、自审发现、疑虑）。
- 回复正文 ≤15 行：Status / Commits（本任务禁止 commit，写 "none (git read-only)"）/
  一行测试摘要（形如 "N/N passing, output pristine"）/ 疑虑 / 报告文件路径。
