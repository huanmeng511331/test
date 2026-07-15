package main

// Sort sorts a slice of integers in ascending order using quicksort algorithm.
// Time complexity: O(n log n) average, O(n²) worst case
// Space complexity: O(log n) for recursion stack
func Sort(arr []int) []int {
	if len(arr) <= 1 {
		return arr
	}

	// Create a copy to avoid modifying the input
	result := make([]int, len(arr))
	copy(result, arr)
	quicksort(result, 0, len(result)-1)
	return result
}

// quicksort recursively sorts the slice using the divide-and-conquer approach
func quicksort(arr []int, low, high int) {
	if low < high {
		pivotIndex := partition(arr, low, high)
		quicksort(arr, low, pivotIndex-1)
		quicksort(arr, pivotIndex+1, high)
	}
}

// partition rearranges elements so that all elements less than pivot come before it,
// and all elements greater come after it. Returns the final pivot position.
func partition(arr []int, low, high int) int {
	pivot := arr[high] // Choose last element as pivot
	i := low - 1       // Index of smaller element

	for j := low; j < high; j++ {
		if arr[j] <= pivot {
			i++
			arr[i], arr[j] = arr[j], arr[i] // Swap
		}
	}
	arr[i+1], arr[high] = arr[high], arr[i+1] // Place pivot in correct position
	return i + 1
}

// IsSorted checks if the slice is sorted in ascending order
func IsSorted(arr []int) bool {
	for i := 1; i < len(arr); i++ {
		if arr[i-1] > arr[i] {
			return false
		}
	}
	return true
}