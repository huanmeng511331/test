package main

import (
	"fmt"
	"testing"
)

func TestReviewExample(t *testing.T) {
	fmt.Println("Hello, Code Review!")
	var x int = 10
	// 注意：原始代码有除零问题，这里改为安全输出
	if x > 0 {
		fmt.Println(x)
	}
}
