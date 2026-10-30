package model

import (
	"crypto/rand"
	"encoding/hex"
	"time"
)

// DepreciationMethod 折旧方法枚举
type DepreciationMethod string

const (
	DepreciationStraightLine    DepreciationMethod = "straight_line"
	DepreciationDoubleDeclining DepreciationMethod = "double_declining"
)

func (m DepreciationMethod) IsValid() bool {
	switch m {
	case DepreciationStraightLine, DepreciationDoubleDeclining:
		return true
	}
	return false
}

// AssetStatus 资产状态枚举
type AssetStatus string

const (
	StatusActive       AssetStatus = "active"
	StatusDepreciating AssetStatus = "depreciating"
	StatusDisposed     AssetStatus = "disposed"
)

func (s AssetStatus) IsValid() bool {
	switch s {
	case StatusActive, StatusDepreciating, StatusDisposed:
		return true
	}
	return false
}

// FixedAsset 固定资产实体
type FixedAsset struct {
	ID               string             `json:"id"`
	AssetNo          string             `json:"asset_no"`                  // 资产编号
	Name             string             `json:"name"`                      // 资产名称
	Category         string             `json:"category"`                  // 资产类别
	OriginalValue    int64              `json:"original_value"`            // 原值（单位：分）
	SalvageValue     int64              `json:"salvage_value"`             // 残值（单位：分）
	UsefulLifeMonths int                `json:"useful_life_months"`        // 使用年限（月）
	AcquisitionDate  time.Time          `json:"acquisition_date"`          // 取得日期
	Method           DepreciationMethod `json:"method"`                    // 折旧方法
	Status           AssetStatus        `json:"status"`                    // 资产状态
	NetValue         int64              `json:"net_value"`                 // 净值（单位：分）
	AccumulatedDep   int64              `json:"accumulated_depreciation"`   // 累计折旧（单位：分）
	CreatedAt        time.Time          `json:"created_at"`
	UpdatedAt        time.Time          `json:"updated_at"`
}

// NewFixedAsset 创建一个新的固定资产，自动生成 ID 并初始化状态
func NewFixedAsset(
	assetNo string,
	name string,
	category string,
	originalValue int64,
	usefulLifeMonths int,
	acquisitionDate time.Time,
	method DepreciationMethod,
	salvageValue int64,
) *FixedAsset {
	now := time.Now()
	return &FixedAsset{
		ID:               generateID(),
		AssetNo:          assetNo,
		Name:             name,
		Category:         category,
		OriginalValue:    originalValue,
		SalvageValue:     salvageValue,
		UsefulLifeMonths: usefulLifeMonths,
		AcquisitionDate:  acquisitionDate,
		Method:           method,
		Status:           StatusActive,
		NetValue:         originalValue,
		AccumulatedDep:   0,
		CreatedAt:        now,
		UpdatedAt:        now,
	}
}

// generateID 生成一个 16 字符的随机 hex ID
func generateID() string {
	b := make([]byte, 8)
	_, _ = rand.Read(b)
	return hex.EncodeToString(b)
}
