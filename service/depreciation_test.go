package service

import (
	"testing"
	"time"

	"fixedasset/model"
)

func newDepreciationTestAsset(method model.DepreciationMethod) *model.FixedAsset {
	return &model.FixedAsset{
		ID:               "test-1",
		AssetNo:          "A-001",
		Name:             "测试设备",
		Category:         "IT设备",
		OriginalValue:    600000, // 6000.00 元
		SalvageValue:     60000,  // 600.00 元（残值率 10%）
		UsefulLifeMonths: 60,     // 5 年
		AcquisitionDate:  time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC),
		Method:           method,
		Status:           model.StatusActive,
		NetValue:         600000,
		AccumulatedDep:   0,
	}
}

func TestCalculateDepreciation_StraightLine(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationStraightLine)

	// 直线法：月折旧 = (原值 - 残值) / 使用月数 = (600000 - 60000) / 60 = 9000
	tests := []struct {
		name          string
		monthsElapsed int
		wantMonthly   int64
		wantTotal     int64
		wantNet       int64
	}{
		{"1 month", 1, 9000, 9000, 591000},
		{"12 months", 12, 9000, 108000, 492000},
		{"60 months (full)", 60, 9000, 540000, 60000},
		{"61 months (capped)", 61, 9000, 540000, 60000},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			asset.AccumulatedDep = 0
			asset.NetValue = asset.OriginalValue
			monthly, total := CalculateDepreciation(asset, tt.monthsElapsed)
			if monthly != tt.wantMonthly {
				t.Errorf("monthly depreciation = %d, want %d", monthly, tt.wantMonthly)
			}
			if total != tt.wantTotal {
				t.Errorf("total depreciation = %d, want %d", total, tt.wantTotal)
			}
		})
	}
}

func TestCalculateDepreciation_DoubleDeclining(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationDoubleDeclining)

	// 双倍余额递减法：月折旧率 = 2 / 60 = 0.0333...
	// 第1月：600000 * (2/60) = 20000
	tests := []struct {
		name          string
		monthsElapsed int
		wantMonthly   int64
	}{
		{"month 1", 1, 20000},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			asset.AccumulatedDep = 0
			asset.NetValue = asset.OriginalValue
			monthly, _ := CalculateDepreciation(asset, tt.monthsElapsed)
			if monthly != tt.wantMonthly {
				t.Errorf("monthly depreciation = %d, want %d", monthly, tt.wantMonthly)
			}
		})
	}
}

func TestCalculateDepreciation_DoubleDeclining_NetNotBelowSalvage(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationDoubleDeclining)

	// 折旧 60 个月后净值不应低于残值
	asset.AccumulatedDep = 0
	asset.NetValue = asset.OriginalValue
	_, total := CalculateDepreciation(asset, 60)

	netValue := asset.OriginalValue - total
	if netValue < asset.SalvageValue {
		t.Errorf("net value %d should not be below salvage value %d", netValue, asset.SalvageValue)
	}
}

func TestCalculateDepreciation_ZeroUsefulLife(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationStraightLine)
	asset.UsefulLifeMonths = 0

	monthly, total := CalculateDepreciation(asset, 12)
	if monthly != 0 {
		t.Errorf("monthly depreciation with zero useful life = %d, want 0", monthly)
	}
	if total != 0 {
		t.Errorf("total depreciation with zero useful life = %d, want 0", total)
	}
}

func TestCalculateDepreciation_InvalidMethod(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationMethod("invalid"))
	monthly, total := CalculateDepreciation(asset, 12)
	if monthly != 0 || total != 0 {
		t.Error("invalid method should return zero depreciation")
	}
}
