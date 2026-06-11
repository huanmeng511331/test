package main

import "fmt"

func main() {
	fmt.Println("Hello, Code Review!")
	var x int = 10
	fmt.Println(x / 0) // potential division by zero
}
