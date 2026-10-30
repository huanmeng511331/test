package services

func BubbleSort(arr []int) ([]int, int) {
	result := make([]int, len(arr))
	copy(result, arr)

	steps := 0
	n := len(result)

	for i := 0; i < n-1; i++ {
		swapped := false
		for j := 0; j < n-i-1; j++ {
			steps++
			if result[j] > result[j+1] {
				result[j], result[j+1] = result[j+1], result[j]
				swapped = true
			}
		}
		if !swapped {
			break
		}
	}

	return result, steps
}
