# Task 1 Report: 建立可运行测试基线

## 实现内容
1. 新建 `go.mod`（内容逐字符合 brief）：
   ```
   module sortapp

   go 1.24
   ```
2. 新建 `sort_test.go`（package main）：
   - `TestSort`：表驱动 11 个用例（t.Run），覆盖 nil、空、单元素、两元素（逆序对 `2,1` 与已排序 `1,2`）、已排序、严格逆序、全相等、含重复、含负数、正负混合；期望值一律为排序后的正确序列字面量。每个子测试断言：长度一致、`sort.IntsAreSorted` 升序、逐元素相等（`slices.Equal`），失败信息用 `%v` 打印输入。
   - `TestSort` 内子测试 `randomized_fixed_seed`：固定种子 `rand.New(rand.NewSource(42))`，对 n=0..64 生成 [-100,100] 随机 []int，与 `sort.Ints` 结果逐一比对（长度、升序、相等三重断言）。
   - `TestIsSorted`：表驱动 9 个用例，覆盖 true/false 两侧及空、单元素、重复边界（全相等为 true，`2,2,1` 为 false）。
3. 重写 `review_test.go`（删除全部旧内容，保留文件名）：
   - 恰好一个测试 `TestSortDoesNotMutateInput`：构造含重复与负数输入 `{3,-1,2,3,0,-7,2}`，深拷贝留底，调 `Sort` 后用 `slices.Equal` 断言原输入未被修改、返回值升序且与 `sort.Ints` 求得的期望一致。
   - 文件中无 `func main`、无任何除法表达式。
4. 未修改 `sort.go`、`README.md`、`test2.md`、`test0817.md`、`tmp/`、`.agents/`；未新增其他文件；未引入外部依赖；未执行任何 git 写操作。

## 验收命令与真实输出（Go 1.24.5 linux/amd64，/usr/local/go/bin）
```
$ export PATH=/usr/local/go/bin:$PATH
$ go test ./...
ok  	sortapp	0.002s
（退出码 0）
$ go vet ./...
（无输出，退出码 0）
```
补充证据（`go test -v ./...`）：`--- PASS` 顶层 3 个（TestSort、TestIsSorted、TestSortDoesNotMutateInput），子测试 21 个（12+9+0）全 PASS；统计 24 个叶子测试全部通过，无 FAIL、无 SKIP、无 t.Log 杂音。

## 变更文件清单（均未提交，工作区保留）
- `go.mod`（新增）
- `sort_test.go`（新增）
- `review_test.go`（整体重写）

## 自审发现
- 与 brief 逐项核对：go.mod 内容、10 类必覆盖用例、固定种子对拍、TestIsSorted 边界、name 字段 + t.Run + %v 失败信息、长度与升序断言、review_test.go 恰好一个测试且无 func main/除法——全部满足。
- 测试卫生：无 t.Log/fmt 打印、无跳过、随机源固定种子（确定性）、无外部依赖。
- 实现说明：n=0 对拍时 `want` 用 `make([]int,0)` 构造以保持与 `Sort` 返回的非 nil 空切片可比（`slices.Equal` 对 nil 与空切片判等，此处无影响，但保持构造一致更稳）。
- 本任务中仅一次工具层补丁冲突（同一文件不能 Delete+Add），改为整体重写写入 review_test.go，最终内容与预期一致。

## 疑虑
- 无阻塞性疑虑。sort.go 评审未发现缺陷，未做任何修改。
