package main

import (
	"reflect"
	"testing"
)

// ==================== Sort 测试 ====================

func TestSort_shouldReturnEmptySlice_when_inputIsEmpty(t *testing.T) {
	// Arrange
	input := []int{}

	// Act
	result := Sort(input)

	// Assert
	if len(result) != 0 {
		t.Errorf("expected empty slice, got %v", result)
	}
}

func TestSort_shouldReturnSameSlice_when_singleElement(t *testing.T) {
	// Arrange
	input := []int{42}

	// Act
	result := Sort(input)

	// Assert
	if len(result) != 1 || result[0] != 42 {
		t.Errorf("expected [42], got %v", result)
	}
}

func TestSort_shouldReturnSortedSlice_when_validInput(t *testing.T) {
	// Arrange
	input := []int{3, 1, 4, 1, 5, 9, 2, 6}
	expected := []int{1, 1, 2, 3, 4, 5, 6, 9}

	// Act
	result := Sort(input)

	// Assert
	if !reflect.DeepEqual(result, expected) {
		t.Errorf("expected %v, got %v", expected, result)
	}
}

func TestSort_shouldNotModifyOriginal_when_inputIsUnsorted(t *testing.T) {
	// Arrange
	original := []int{5, 3, 1}
	expectedOriginal := []int{5, 3, 1}

	// Act
	Sort(original)

	// Assert
	if !reflect.DeepEqual(original, expectedOriginal) {
		t.Errorf("original slice was modified: %v", original)
	}
}

func TestSort_shouldHandleNegativeNumbers(t *testing.T) {
	// Arrange
	input := []int{-3, -1, -4, 0, 2, 1}
	expected := []int{-4, -3, -1, 0, 1, 2}

	// Act
	result := Sort(input)

	// Assert
	if !reflect.DeepEqual(result, expected) {
		t.Errorf("expected %v, got %v", expected, result)
	}
}

func TestSort_shouldHandleAlreadySorted(t *testing.T) {
	// Arrange
	input := []int{1, 2, 3, 4, 5}
	expected := []int{1, 2, 3, 4, 5}

	// Act
	result := Sort(input)

	// Assert
	if !reflect.DeepEqual(result, expected) {
		t.Errorf("expected %v, got %v", expected, result)
	}
}

func TestSort_shouldHandleReverseSorted(t *testing.T) {
	// Arrange
	input := []int{5, 4, 3, 2, 1}
	expected := []int{1, 2, 3, 4, 5}

	// Act
	result := Sort(input)

	// Assert
	if !reflect.DeepEqual(result, expected) {
		t.Errorf("expected %v, got %v", expected, result)
	}
}

func TestSort_shouldHandleDuplicates(t *testing.T) {
	// Arrange
	input := []int{3, 3, 3, 1, 1, 2, 2}
	expected := []int{1, 1, 2, 2, 3, 3, 3}

	// Act
	result := Sort(input)

	// Assert
	if !reflect.DeepEqual(result, expected) {
		t.Errorf("expected %v, got %v", expected, result)
	}
}

// ==================== IsSorted 测试 ====================

func TestIsSorted_shouldReturnTrue_when_sorted(t *testing.T) {
	// Arrange
	input := []int{1, 2, 3, 4, 5}

	// Act
	result := IsSorted(input)

	// Assert
	if !result {
		t.Errorf("expected true, got false")
	}
}

func TestIsSorted_shouldReturnFalse_when_unsorted(t *testing.T) {
	// Arrange
	input := []int{1, 3, 2, 4, 5}

	// Act
	result := IsSorted(input)

	// Assert
	if result {
		t.Errorf("expected false, got true")
	}
}

func TestIsSorted_shouldReturnTrue_when_empty(t *testing.T) {
	// Arrange
	input := []int{}

	// Act
	result := IsSorted(input)

	// Assert
	if !result {
		t.Errorf("expected true for empty slice, got false")
	}
}

func TestIsSorted_shouldReturnTrue_when_singleElement(t *testing.T) {
	// Arrange
	input := []int{42}

	// Act
	result := IsSorted(input)

	// Assert
	if !result {
		t.Errorf("expected true for single element, got false")
	}
}

// ==================== Sort 边界值测试 ====================

func TestSort_shouldHandleLargeInput(t *testing.T) {
	// Arrange
	input := make([]int, 1000)
	for i := 0; i < 1000; i++ {
		input[i] = 1000 - i
	}

	// Act
	result := Sort(input)

	// Assert
	for i := 0; i < 999; i++ {
		if result[i] > result[i+1] {
			t.Errorf("result not sorted at index %d: %d > %d", i, result[i], result[i+1])
			break
		}
	}
}

func TestSort_shouldHandleAllSameElements(t *testing.T) {
	// Arrange
	input := []int{7, 7, 7, 7, 7}
	expected := []int{7, 7, 7, 7, 7}

	// Act
	result := Sort(input)

	// Assert
	if !reflect.DeepEqual(result, expected) {
		t.Errorf("expected %v, got %v", expected, result)
	}
}

// ==================== 场景矩阵 ====================
//
// | 被测方法 | 业务场景 | 输入条件 | 预期结果 |
// |---------|---------|---------|---------|
// | Sort | 正常排序 | 无序整数切片 | 返回升序排列的新切片 |
// | Sort | 空输入 | 空切片 | 返回空切片 |
// | Sort | 单元素 | 含一个元素的切片 | 返回相同切片 |
// | Sort | 不修改原切片 | 无序切片 | 原切片保持不变 |
// | Sort | 含负数 | 含负数的切片 | 正确升序排列 |
// | Sort | 重复元素 | 含重复值的切片 | 重复元素相邻排列 |
// | IsSorted | 已排序 | 升序切片 | 返回 true |
// | IsSorted | 未排序 | 乱序切片 | 返回 false |
// | IsSorted | 空切片 | 空切片 | 返回 true |
