package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"

	"todo-app/internal/todo"
)

func main() {
	store := todo.NewInMemoryStore()
	svc := todo.NewService(store)
	reader := bufio.NewReader(os.Stdin)

	fmt.Println("=== 待办事项创建助手 ===")
	fmt.Println("请输入待办事项信息（输入空行结束）")
	fmt.Println()

	fmt.Print("事项名称: ")
	name, err := reader.ReadString('\n')
	if err != nil {
		fmt.Fprintf(os.Stderr, "读取名称失败: %v\n", err)
		os.Exit(1)
	}
	name = strings.TrimSpace(name)
	if name == "" {
		fmt.Println("事项名称不能为空")
		os.Exit(1)
	}

	fmt.Print("事项描述: ")
	description, err := reader.ReadString('\n')
	if err != nil {
		fmt.Fprintf(os.Stderr, "读取描述失败: %v\n", err)
		os.Exit(1)
	}
	description = strings.TrimSpace(description)

	item, err := svc.CreateTodo(name, description)
	if err != nil {
		fmt.Fprintf(os.Stderr, "创建失败: %v\n", err)
		os.Exit(1)
	}

	fmt.Printf("\n✅ 待办事项已创建！\n")
	fmt.Printf("ID: %s\n", item.ID)
	fmt.Printf("名称: %s\n", item.Name)
	fmt.Printf("描述: %s\n", item.Description)
	fmt.Printf("创建时间: %s\n", item.CreatedAt.Format("2006-01-02 15:04:05"))
}
