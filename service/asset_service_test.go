package service

import (
	"testing"
	"time"

	"fixedasset/model"
	"fixedasset/repository"
)

func newTestService() *AssetService {
	repo := repository.NewInMemoryAssetRepository()
	return NewAssetService(repo)
}

func newCreateInput() *CreateAssetInput {
	return &CreateAssetInput{
		AssetNo:          "A-001",
		Name:             "办公电脑",
		Category:         "IT设备",
		OriginalValue:    500000,
		SalvageValue:     50000,
		UsefulLifeMonths: 60,
		AcquisitionDate:  time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC),
		Method:           model.DepreciationStraightLine,
	}
}

func TestAssetService_CreateAsset(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()

	asset, err := svc.CreateAsset(input)
	if err != nil {
		t.Fatalf("CreateAsset() error = %v", err)
	}
	if asset.ID == "" {
		t.Error("CreateAsset() should return asset with non-empty ID")
	}
	if asset.Status != model.StatusActive {
		t.Errorf("Status = %q, want %q", asset.Status, model.StatusActive)
	}
}

func TestAssetService_CreateAsset_InvalidMethod(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()
	input.Method = model.DepreciationMethod("invalid")

	_, err := svc.CreateAsset(input)
	if err == nil {
		t.Error("CreateAsset() with invalid method should return error")
	}
}

func TestAssetService_CreateAsset_NegativeValue(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()
	input.OriginalValue = -100

	_, err := svc.CreateAsset(input)
	if err == nil {
		t.Error("CreateAsset() with negative original value should return error")
	}
}

func TestAssetService_CreateAsset_DuplicateAssetNo(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()

	_, _ = svc.CreateAsset(input)
	_, err := svc.CreateAsset(input)
	if err == nil {
		t.Error("CreateAsset() with duplicate AssetNo should return error")
	}
}

func TestAssetService_GetAsset(t *testing.T) {
	svc := newTestService()
	created, _ := svc.CreateAsset(newCreateInput())

	got, err := svc.GetAsset(created.ID)
	if err != nil {
		t.Fatalf("GetAsset() error = %v", err)
	}
	if got.AssetNo != created.AssetNo {
		t.Errorf("AssetNo = %q, want %q", got.AssetNo, created.AssetNo)
	}
}

func TestAssetService_UpdateAsset(t *testing.T) {
	svc := newTestService()
	created, _ := svc.CreateAsset(newCreateInput())

	created.Name = "更新后的电脑"
	err := svc.UpdateAsset(created)
	if err != nil {
		t.Fatalf("UpdateAsset() error = %v", err)
	}

	got, _ := svc.GetAsset(created.ID)
	if got.Name != "更新后的电脑" {
		t.Errorf("Name = %q, want %q", got.Name, "更新后的电脑")
	}
}

func TestAssetService_DeleteAsset(t *testing.T) {
	svc := newTestService()
	created, _ := svc.CreateAsset(newCreateInput())

	err := svc.DeleteAsset(created.ID)
	if err != nil {
		t.Fatalf("DeleteAsset() error = %v", err)
	}

	_, err = svc.GetAsset(created.ID)
	if err == nil {
		t.Error("GetAsset() after delete should return error")
	}
}

func TestAssetService_ListAll(t *testing.T) {
	svc := newTestService()

	input1 := newCreateInput()
	input2 := newCreateInput()
	input2.AssetNo = "A-002"
	_, _ = svc.CreateAsset(input1)
	_, _ = svc.CreateAsset(input2)

	list := svc.ListAll()
	if len(list) != 2 {
		t.Errorf("ListAll() len = %d, want 2", len(list))
	}
}

func TestAssetService_ApplyDepreciation(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()
	input.OriginalValue = 600000
	input.SalvageValue = 60000
	input.UsefulLifeMonths = 60

	created, _ := svc.CreateAsset(input)

	err := svc.ApplyDepreciation(created.ID, 12)
	if err != nil {
		t.Fatalf("ApplyDepreciation() error = %v", err)
	}

	got, _ := svc.GetAsset(created.ID)
	// 直线法：(600000 - 60000) / 60 * 12 = 108000
	if got.AccumulatedDep != 108000 {
		t.Errorf("AccumulatedDep = %d, want 108000", got.AccumulatedDep)
	}
	if got.NetValue != 492000 {
		t.Errorf("NetValue = %d, want 492000", got.NetValue)
	}
	if got.Status != model.StatusDepreciating {
		t.Errorf("Status = %q, want %q", got.Status, model.StatusDepreciating)
	}
}

func TestAssetService_ApplyDepreciation_DisposedAsset(t *testing.T) {
	svc := newTestService()
	created, _ := svc.CreateAsset(newCreateInput())

	created.Status = model.StatusDisposed
	_ = svc.UpdateAsset(created)

	err := svc.ApplyDepreciation(created.ID, 12)
	if err == nil {
		t.Error("ApplyDepreciation() on disposed asset should return error")
	}
}
