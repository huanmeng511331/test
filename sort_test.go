package main

import (
	"math/rand"
	"slices"
	"sort"
	"testing"
)

func TestSort(t *testing.T) {
	tests := []struct {
		name string
		in   []int
		want []int
	}{
		{"nil", nil, nil},
		{"empty", []int{}, []int{}},
		{"single", []int{1}, []int{1}},
		{"two_elements_inversion", []int{2, 1}, []int{1, 2}},
		{"two_elements_sorted", []int{1, 2}, []int{1, 2}},
		{"already_sorted", []int{1, 2, 3, 4, 5}, []int{1, 2, 3, 4, 5}},
		{"strictly_reverse", []int{5, 4, 3, 2, 1}, []int{1, 2, 3, 4, 5}},
		{"all_equal", []int{7, 7, 7, 7, 7}, []int{7, 7, 7, 7, 7}},
		{"with_duplicates", []int{3, 1, 2, 3, 1}, []int{1, 1, 2, 3, 3}},
		{"with_negatives", []int{-5, -1, -3}, []int{-5, -3, -1}},
		{"mixed_signs", []int{3, -1, 0, -7, 2}, []int{-7, -1, 0, 2, 3}},
	}
	for _, tc := range tests {
		t.Run(tc.name, func(t *testing.T) {
			got := Sort(tc.in)
			if len(got) != len(tc.want) {
				t.Fatalf("input %v: length mismatch: got %d, want %d", tc.in, len(got), len(tc.want))
			}
			if !sort.IntsAreSorted(got) {
				t.Fatalf("input %v: result not ascending: %v", tc.in, got)
			}
			if !slices.Equal(got, tc.want) {
				t.Fatalf("input %v: got %v, want %v", tc.in, got, tc.want)
			}
		})
	}

	t.Run("randomized_fixed_seed", func(t *testing.T) {
		rng := rand.New(rand.NewSource(42))
		for n := 0; n <= 64; n++ {
			in := make([]int, n)
			for i := range in {
				in[i] = rng.Intn(201) - 100
			}
			want := make([]int, n)
			copy(want, in)
			sort.Ints(want)
			got := Sort(in)
			if len(got) != len(want) || !sort.IntsAreSorted(got) || !slices.Equal(got, want) {
				t.Fatalf("n=%d input %v: got %v, want %v", n, in, got, want)
			}
		}
	})
}

func TestIsSorted(t *testing.T) {
	tests := []struct {
		name string
		in   []int
		want bool
	}{
		{"nil_true", nil, true},
		{"empty_true", []int{}, true},
		{"single_true", []int{42}, true},
		{"sorted_with_duplicates_true", []int{1, 2, 2, 3}, true},
		{"all_equal_true", []int{5, 5, 5}, true},
		{"two_elements_inversion_false", []int{2, 1}, false},
		{"single_inversion_false", []int{1, 3, 2}, false},
		{"strictly_reverse_false", []int{3, 2, 1}, false},
		{"duplicates_before_smaller_false", []int{2, 2, 1}, false},
	}
	for _, tc := range tests {
		t.Run(tc.name, func(t *testing.T) {
			if got := IsSorted(tc.in); got != tc.want {
				t.Fatalf("input %v: IsSorted = %v, want %v", tc.in, got, tc.want)
			}
		})
	}
}
