# Plan: 为 sort.go 建立可运行的 Go 测试基线（需求：测试）

## Global Constraints (binding)

- Git 只读：禁止一切 git 写操作（commit/push/merge/reset/rebase/checkout/add 等），
  仅允许 git status/log/diff/show 读取。所有变更保留在工作区，不产生提交。
- 工具黑名单：禁止使用 shell grep/glob。
- Go 工具链位于 /usr/local/go/bin（不在默认 PATH），所有 go 命令前须
  `export PATH=/usr/local/go/bin:$PATH`。
- 验收（可观测证据）：`go test ./...` 退出码 0 且输出无杂音；`go vet ./...` 退出码 0；
  验证总耗时 ≤ 5 分钟。
- go.mod 不引入任何外部依赖（无 require，无需网络）。
- 不修改 sort.go；若发现其真实缺陷，停止并上报 DONE_WITH_CONCERNS，不得擅自改算法。
- 所有产物写入 cwd 内；`.superpowers/` 下的文件是控制器/子代理协作草稿，不属于交付物。
- 已知范围外事实（变更前即存在）：package main 无非测试文件的 main 函数，
  `go build` 失败与本任务无关，不在验收范围。

## Task 1: 建立可运行测试基线（go.mod + sort_test.go + 修复伪测试文件）

1. 新增 go.mod：module sortapp，go 1.24，无外部依赖。
2. 新增 sort_test.go：表驱动测试 Sort 的正确性（nil、空、单元素、两元素、
   已排序、逆序、全相等、含重复、含负数、固定种子随机序列与 sort.Ints 对拍、
   长度 0..64 全覆盖）；表驱动测试 IsSorted；断言 Sort 不修改输入切片
   （sort.go 注释承诺 copy 语义）。
3. 重写 review_test.go：现内容含 func main() 与除零常量表达式，导致
   `go test` 编译失败（已实测：invalid operation: division by zero）。
   将其转换为合法测试文件：仅包含一个"Sort 不改变原输入（copy 语义）"的
   回归测试，package main，不得包含 func main、不得有除零表达式。
4. 运行验收命令并记录完整输出。
