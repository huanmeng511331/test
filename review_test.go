package main

import (
	"slices"
	"sort"
	"testing"
)

func TestSortDoesNotMutateInput(t *testing.T) {
	in := []int{3, -1, 2, 3, 0, -7, 2}
	backup := make([]int, len(in))
	copy(backup, in)

	got := Sort(in)

	if !slices.Equal(in, backup) {
		t.Fatalf("input was mutated: got %v, want %v", in, backup)
	}
	if !sort.IntsAreSorted(got) {
		t.Fatalf("returned slice not ascending: %v", got)
	}
	want := make([]int, len(backup))
	copy(want, backup)
	sort.Ints(want)
	if !slices.Equal(got, want) {
		t.Fatalf("returned slice wrong: got %v, want %v", got, want)
	}
}
