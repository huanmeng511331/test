package handler

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"fixedasset/model"
	"fixedasset/repository"
	"fixedasset/service"
)

func setupHandler() (http.Handler, *service.AssetService) {
	repo := repository.NewInMemoryAssetRepository()
	svc := service.NewAssetService(repo)
	mux := http.NewServeMux()
	handler := NewAssetHandler(svc)
	handler.RegisterRoutes(mux)
	return mux, svc
}

func TestCreateAsset_API(t *testing.T) {
	mux, _ := setupHandler()

	body := map[string]interface{}{
		"asset_no":           "A-001",
		"name":               "办公电脑",
		"category":           "IT设备",
		"original_value":     500000,
		"salvage_value":      50000,
		"useful_life_months": 60,
		"acquisition_date":   "2026-01-01T00:00:00Z",
		"method":             "straight_line",
	}
	jsonBody, _ := json.Marshal(body)

	req := httptest.NewRequest("POST", "/api/assets", bytes.NewReader(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusCreated {
		t.Errorf("status = %d, want %d", w.Code, http.StatusCreated)
	}

	var resp model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if resp.AssetNo != "A-001" {
		t.Errorf("AssetNo = %q, want %q", resp.AssetNo, "A-001")
	}
	if resp.ID == "" {
		t.Error("ID should not be empty")
	}
}

func TestCreateAsset_API_ValidationError(t *testing.T) {
	mux, _ := setupHandler()

	body := map[string]interface{}{
		"asset_no":       "",
		"name":           "测试",
		"original_value": -100,
		"method":         "straight_line",
	}
	jsonBody, _ := json.Marshal(body)

	req := httptest.NewRequest("POST", "/api/assets", bytes.NewReader(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusBadRequest {
		t.Errorf("status = %d, want %d", w.Code, http.StatusBadRequest)
	}
}

func TestGetAsset_API(t *testing.T) {
	mux, svc := setupHandler()

	asset, _ := svc.CreateAsset(&service.CreateAssetInput{
		AssetNo:          "A-002",
		Name:             "打印机",
		Category:         "IT设备",
		OriginalValue:    300000,
		SalvageValue:     30000,
		UsefulLifeMonths: 36,
		AcquisitionDate:  time.Now(),
		Method:           model.DepreciationStraightLine,
	})

	req := httptest.NewRequest("GET", "/api/assets/"+asset.ID, nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if resp.AssetNo != "A-002" {
		t.Errorf("AssetNo = %q, want %q", resp.AssetNo, "A-002")
	}
}

func TestGetAsset_API_NotFound(t *testing.T) {
	mux, _ := setupHandler()

	req := httptest.NewRequest("GET", "/api/assets/nonexistent", nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusNotFound {
		t.Errorf("status = %d, want %d", w.Code, http.StatusNotFound)
	}
}

func TestListAssets_API(t *testing.T) {
	mux, svc := setupHandler()

	_, _ = svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-003", Name: "设备1", Category: "IT设备",
		OriginalValue: 100000, SalvageValue: 10000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})
	_, _ = svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-004", Name: "设备2", Category: "办公家具",
		OriginalValue: 200000, SalvageValue: 20000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	req := httptest.NewRequest("GET", "/api/assets", nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp []*model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if len(resp) != 2 {
		t.Errorf("list length = %d, want 2", len(resp))
	}
}

func TestListAssetsByCategory_API(t *testing.T) {
	mux, svc := setupHandler()

	_, _ = svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-005", Name: "设备1", Category: "IT设备",
		OriginalValue: 100000, SalvageValue: 10000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})
	_, _ = svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-006", Name: "设备2", Category: "办公家具",
		OriginalValue: 200000, SalvageValue: 20000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	req := httptest.NewRequest("GET", "/api/assets?category=IT设备", nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp []*model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if len(resp) != 1 {
		t.Errorf("list length = %d, want 1", len(resp))
	}
}

func TestUpdateAsset_API(t *testing.T) {
	mux, svc := setupHandler()

	asset, _ := svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-007", Name: "原始名称", Category: "IT设备",
		OriginalValue: 100000, SalvageValue: 10000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	body := model.FixedAsset{
		ID:               asset.ID,
		AssetNo:          asset.AssetNo,
		Name:             "更新名称",
		Category:         asset.Category,
		OriginalValue:    asset.OriginalValue,
		SalvageValue:     asset.SalvageValue,
		UsefulLifeMonths: asset.UsefulLifeMonths,
		AcquisitionDate:  asset.AcquisitionDate,
		Method:           asset.Method,
		Status:           asset.Status,
		NetValue:         asset.NetValue,
		AccumulatedDep:   asset.AccumulatedDep,
		CreatedAt:        asset.CreatedAt,
		UpdatedAt:        asset.UpdatedAt,
	}
	jsonBody, _ := json.Marshal(body)

	req := httptest.NewRequest("PUT", "/api/assets/"+asset.ID, bytes.NewReader(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if resp.Name != "更新名称" {
		t.Errorf("Name = %q, want %q", resp.Name, "更新名称")
	}
}

func TestDeleteAsset_API(t *testing.T) {
	mux, svc := setupHandler()

	asset, _ := svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-008", Name: "测试", Category: "IT设备",
		OriginalValue: 100000, SalvageValue: 10000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	req := httptest.NewRequest("DELETE", "/api/assets/"+asset.ID, nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusNoContent {
		t.Errorf("status = %d, want %d", w.Code, http.StatusNoContent)
	}
}

func TestApplyDepreciation_API(t *testing.T) {
	mux, svc := setupHandler()

	asset, _ := svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-009", Name: "测试", Category: "IT设备",
		OriginalValue: 600000, SalvageValue: 60000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	body := map[string]interface{}{
		"months_elapsed": 12,
	}
	jsonBody, _ := json.Marshal(body)

	req := httptest.NewRequest("POST", "/api/assets/"+asset.ID+"/depreciate", bytes.NewReader(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if resp.AccumulatedDep != 108000 {
		t.Errorf("AccumulatedDep = %d, want 108000", resp.AccumulatedDep)
	}
}
