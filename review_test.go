package main

import "fmt"

func main() {
	fmt.Println("Hello, Code Review!")
	var x int = 10
	if x != 0 {
		fmt.Println(x / 1) // safe division, x is non-zero
	}
}
