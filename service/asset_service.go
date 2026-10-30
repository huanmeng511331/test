package service

import (
	"errors"
	"time"

	"fixedasset/model"
	"fixedasset/repository"
)

// ErrValidation 校验错误
var ErrValidation = errors.New("validation error")

// ErrAssetDisposed 资产已处置
var ErrAssetDisposed = errors.New("asset has been disposed")

// CreateAssetInput 创建资产的输入参数
type CreateAssetInput struct {
	AssetNo          string             `json:"asset_no"`
	Name             string             `json:"name"`
	Category         string             `json:"category"`
	OriginalValue    int64              `json:"original_value"`
	SalvageValue     int64              `json:"salvage_value"`
	UsefulLifeMonths int                `json:"useful_life_months"`
	AcquisitionDate  time.Time          `json:"acquisition_date"`
	Method           model.DepreciationMethod `json:"method"`
}

// AssetService 资产业务服务
type AssetService struct {
	repo repository.AssetRepository
}

// NewAssetService 创建资产服务实例
func NewAssetService(repo repository.AssetRepository) *AssetService {
	return &AssetService{repo: repo}
}

// CreateAsset 创建固定资产
func (s *AssetService) CreateAsset(input *CreateAssetInput) (*model.FixedAsset, error) {
	if err := validateCreateInput(input); err != nil {
		return nil, err
	}

	asset := model.NewFixedAsset(
		input.AssetNo,
		input.Name,
		input.Category,
		input.OriginalValue,
		input.UsefulLifeMonths,
		input.AcquisitionDate,
		input.Method,
		input.SalvageValue,
	)

	if err := s.repo.Create(asset); err != nil {
		return nil, err
	}

	return asset, nil
}

// GetAsset 根据 ID 获取资产
func (s *AssetService) GetAsset(id string) (*model.FixedAsset, error) {
	return s.repo.GetByID(id)
}

// GetByAssetNo 根据资产编号获取资产
func (s *AssetService) GetByAssetNo(assetNo string) (*model.FixedAsset, error) {
	return s.repo.GetByAssetNo(assetNo)
}

// UpdateAsset 更新资产
func (s *AssetService) UpdateAsset(asset *model.FixedAsset) error {
	asset.UpdatedAt = time.Now()
	return s.repo.Update(asset)
}

// DeleteAsset 删除资产
func (s *AssetService) DeleteAsset(id string) error {
	return s.repo.Delete(id)
}

// ListByCategory 按类别列出资产
func (s *AssetService) ListByCategory(category string) []*model.FixedAsset {
	return s.repo.ListByCategory(category)
}

// ListAll 列出所有资产
func (s *AssetService) ListAll() []*model.FixedAsset {
	return s.repo.ListAll()
}

// ApplyDepreciation 对指定资产应用折旧计算
func (s *AssetService) ApplyDepreciation(id string, monthsElapsed int) error {
	asset, err := s.repo.GetByID(id)
	if err != nil {
		return err
	}

	if asset.Status == model.StatusDisposed {
		return ErrAssetDisposed
	}

	_, totalDep := CalculateDepreciation(asset, monthsElapsed)

	asset.AccumulatedDep = totalDep
	asset.NetValue = asset.OriginalValue - totalDep
	asset.Status = model.StatusDepreciating
	asset.UpdatedAt = time.Now()

	return s.repo.Update(asset)
}

func validateCreateInput(input *CreateAssetInput) error {
	if input.AssetNo == "" {
		return ErrValidation
	}
	if input.Name == "" {
		return ErrValidation
	}
	if input.OriginalValue <= 0 {
		return ErrValidation
	}
	if input.SalvageValue < 0 {
		return ErrValidation
	}
	if input.SalvageValue >= input.OriginalValue {
		return ErrValidation
	}
	if input.UsefulLifeMonths <= 0 {
		return ErrValidation
	}
	if !input.Method.IsValid() {
		return ErrValidation
	}
	return nil
}
