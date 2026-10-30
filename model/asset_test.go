package model

import (
	"testing"
	"time"
)

func TestDepreciationMethod_IsValid(t *testing.T) {
	tests := []struct {
		name      string
		method    DepreciationMethod
		wantValid bool
	}{
		{"straight_line is valid", DepreciationStraightLine, true},
		{"double_declining is valid", DepreciationDoubleDeclining, true},
		{"empty is invalid", DepreciationMethod(""), false},
		{"unknown is invalid", DepreciationMethod("sum_of_years"), false},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := tt.method.IsValid(); got != tt.wantValid {
				t.Errorf("DepreciationMethod.IsValid() = %v, want %v", got, tt.wantValid)
			}
		})
	}
}

func TestAssetStatus_IsValid(t *testing.T) {
	tests := []struct {
		name      string
		status    AssetStatus
		wantValid bool
	}{
		{"active is valid", StatusActive, true},
		{"depreciating is valid", StatusDepreciating, true},
		{"disposed is valid", StatusDisposed, true},
		{"empty is invalid", AssetStatus(""), false},
		{"unknown is invalid", AssetStatus("lost"), false},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := tt.status.IsValid(); got != tt.wantValid {
				t.Errorf("AssetStatus.IsValid() = %v, want %v", got, tt.wantValid)
			}
		})
	}
}

func TestNewFixedAsset(t *testing.T) {
	acquisitionDate := time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC)
	asset := NewFixedAsset(
		"A-001",
		"办公电脑",
		"IT设备",
		500000, // 5000.00 元 = 500000 分
		60,     // 使用年限 60 个月
		acquisitionDate,
		DepreciationStraightLine,
		50000, // 残值 500.00 元 = 50000 分
	)

	if asset.ID == "" {
		t.Error("NewFixedAsset should generate a non-empty ID")
	}
	if asset.AssetNo != "A-001" {
		t.Errorf("AssetNo = %q, want %q", asset.AssetNo, "A-001")
	}
	if asset.Status != StatusActive {
		t.Errorf("Status = %q, want %q", asset.Status, StatusActive)
	}
	if asset.NetValue != asset.OriginalValue {
		t.Errorf("NetValue should equal OriginalValue on creation, got %d, want %d", asset.NetValue, asset.OriginalValue)
	}
}
