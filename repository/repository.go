package repository

import (
	"errors"
	"sync"

	"fixedasset/model"
)

// ErrAssetNotFound 资产未找到
var ErrAssetNotFound = errors.New("asset not found")

// ErrDuplicateAssetNo 资产编号重复
var ErrDuplicateAssetNo = errors.New("duplicate asset number")

// AssetRepository 资产仓库接口
type AssetRepository interface {
	Create(asset *model.FixedAsset) error
	GetByID(id string) (*model.FixedAsset, error)
	GetByAssetNo(assetNo string) (*model.FixedAsset, error)
	Update(asset *model.FixedAsset) error
	Delete(id string) error
	ListByCategory(category string) []*model.FixedAsset
	ListAll() []*model.FixedAsset
}

// InMemoryAssetRepository 内存实现的资产仓库
type InMemoryAssetRepository struct {
	mu        sync.RWMutex
	byID      map[string]*model.FixedAsset
	byAssetNo map[string]*model.FixedAsset
}

// NewInMemoryAssetRepository 创建内存仓库实例
func NewInMemoryAssetRepository() *InMemoryAssetRepository {
	return &InMemoryAssetRepository{
		byID:      make(map[string]*model.FixedAsset),
		byAssetNo: make(map[string]*model.FixedAsset),
	}
}

func (r *InMemoryAssetRepository) Create(asset *model.FixedAsset) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	if asset.AssetNo != "" {
		if _, exists := r.byAssetNo[asset.AssetNo]; exists {
			return ErrDuplicateAssetNo
		}
	}

	stored := *asset
	r.byID[asset.ID] = &stored
	if asset.AssetNo != "" {
		r.byAssetNo[asset.AssetNo] = &stored
	}
	return nil
}

func (r *InMemoryAssetRepository) GetByID(id string) (*model.FixedAsset, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	asset, exists := r.byID[id]
	if !exists {
		return nil, ErrAssetNotFound
	}
	c := *asset
	return &c, nil
}

func (r *InMemoryAssetRepository) GetByAssetNo(assetNo string) (*model.FixedAsset, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	asset, exists := r.byAssetNo[assetNo]
	if !exists {
		return nil, ErrAssetNotFound
	}
	c := *asset
	return &c, nil
}

func (r *InMemoryAssetRepository) Update(asset *model.FixedAsset) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.byID[asset.ID]
	if !exists {
		return ErrAssetNotFound
	}

	// 如果 AssetNo 变更，更新索引
	if existing.AssetNo != asset.AssetNo {
		if existing.AssetNo != "" {
			delete(r.byAssetNo, existing.AssetNo)
		}
		if asset.AssetNo != "" {
			if _, dup := r.byAssetNo[asset.AssetNo]; dup {
				return ErrDuplicateAssetNo
			}
			r.byAssetNo[asset.AssetNo] = asset
		}
	}

	stored := *asset
	stored.UpdatedAt = asset.UpdatedAt
	r.byID[asset.ID] = &stored
	if asset.AssetNo != "" {
		r.byAssetNo[asset.AssetNo] = &stored
	}
	return nil
}

func (r *InMemoryAssetRepository) Delete(id string) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	asset, exists := r.byID[id]
	if !exists {
		return ErrAssetNotFound
	}

	delete(r.byID, id)
	if asset.AssetNo != "" {
		delete(r.byAssetNo, asset.AssetNo)
	}
	return nil
}

func (r *InMemoryAssetRepository) ListByCategory(category string) []*model.FixedAsset {
	r.mu.RLock()
	defer r.mu.RUnlock()

	var result []*model.FixedAsset
	for _, asset := range r.byID {
		if asset.Category == category {
			c := *asset
			result = append(result, &c)
		}
	}
	return result
}

func (r *InMemoryAssetRepository) ListAll() []*model.FixedAsset {
	r.mu.RLock()
	defer r.mu.RUnlock()

	result := make([]*model.FixedAsset, 0, len(r.byID))
	for _, asset := range r.byID {
		c := *asset
		result = append(result, &c)
	}
	return result
}
