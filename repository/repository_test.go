package repository

import (
	"sync"
	"testing"
	"time"

	"fixedasset/model"
)

func newTestAsset(assetNo string) *model.FixedAsset {
	return model.NewFixedAsset(
		assetNo,
		"测试资产",
		"IT设备",
		100000,
		60,
		time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC),
		model.DepreciationStraightLine,
		10000,
	)
}

func TestInMemoryAssetRepository_Create(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-001")

	err := repo.Create(asset)
	if err != nil {
		t.Fatalf("Create() error = %v", err)
	}

	got, err := repo.GetByID(asset.ID)
	if err != nil {
		t.Fatalf("GetByID() error = %v", err)
	}
	if got.AssetNo != asset.AssetNo {
		t.Errorf("AssetNo = %q, want %q", got.AssetNo, asset.AssetNo)
	}
}

func TestInMemoryAssetRepository_Create_DuplicateAssetNo(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-001")

	_ = repo.Create(asset)
	err := repo.Create(newTestAsset("A-001"))
	if err == nil {
		t.Error("Create() with duplicate AssetNo should return error")
	}
}

func TestInMemoryAssetRepository_GetByID_NotFound(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	_, err := repo.GetByID("nonexistent")
	if err == nil {
		t.Error("GetByID() with nonexistent ID should return error")
	}
}

func TestInMemoryAssetRepository_GetByAssetNo(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-002")
	_ = repo.Create(asset)

	got, err := repo.GetByAssetNo("A-002")
	if err != nil {
		t.Fatalf("GetByAssetNo() error = %v", err)
	}
	if got.ID != asset.ID {
		t.Errorf("ID = %q, want %q", got.ID, asset.ID)
	}
}

func TestInMemoryAssetRepository_Update(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-003")
	_ = repo.Create(asset)

	asset.Status = model.StatusDepreciating
	asset.NetValue = 90000
	err := repo.Update(asset)
	if err != nil {
		t.Fatalf("Update() error = %v", err)
	}

	got, _ := repo.GetByID(asset.ID)
	if got.Status != model.StatusDepreciating {
		t.Errorf("Status = %q, want %q", got.Status, model.StatusDepreciating)
	}
	if got.NetValue != 90000 {
		t.Errorf("NetValue = %d, want %d", got.NetValue, 90000)
	}
}

func TestInMemoryAssetRepository_Delete(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-004")
	_ = repo.Create(asset)

	err := repo.Delete(asset.ID)
	if err != nil {
		t.Fatalf("Delete() error = %v", err)
	}

	_, err = repo.GetByID(asset.ID)
	if err == nil {
		t.Error("GetByID() after Delete should return error")
	}
}

func TestInMemoryAssetRepository_ListByCategory(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	_ = repo.Create(newTestAsset("A-005"))
	_ = repo.Create(newTestAsset("A-006"))

	// 修改第二个资产类别
	asset2, _ := repo.GetByAssetNo("A-006")
	asset2.Category = "办公家具"
	_ = repo.Update(asset2)

	got := repo.ListByCategory("IT设备")
	if len(got) != 1 {
		t.Errorf("ListByCategory() len = %d, want 1", len(got))
	}
}

func TestInMemoryAssetRepository_ListAll(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	_ = repo.Create(newTestAsset("A-007"))
	_ = repo.Create(newTestAsset("A-008"))

	got := repo.ListAll()
	if len(got) != 2 {
		t.Errorf("ListAll() len = %d, want 2", len(got))
	}
}

func TestInMemoryAssetRepository_ConcurrentSafe(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	var wg sync.WaitGroup

	for i := 0; i < 100; i++ {
		wg.Add(1)
		go func(idx int) {
			defer wg.Done()
			asset := newTestAsset("A-CONC")
			asset.AssetNo = ""
			_ = repo.Create(asset)
		}(i)
	}
	wg.Wait()

	if len(repo.ListAll()) != 100 {
		t.Errorf("ListAll() len = %d, want 100", len(repo.ListAll()))
	}
}
