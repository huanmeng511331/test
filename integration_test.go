package main

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestIntegration_CreateAndGetAsset(t *testing.T) {
	server := httptest.NewServer(setupRouter())
	defer server.Close()

	// 创建资产
	createBody := map[string]interface{}{
		"asset_no":           "INT-001",
		"name":               "集成测试设备",
		"category":           "IT设备",
		"original_value":     600000,
		"salvage_value":      60000,
		"useful_life_months": 60,
		"acquisition_date":   "2026-01-01T00:00:00Z",
		"method":             "straight_line",
	}
	jsonBody, _ := json.Marshal(createBody)

	resp, err := http.Post(server.URL+"/api/assets", "application/json", bytes.NewReader(jsonBody))
	if err != nil {
		t.Fatalf("Create request failed: %v", err)
	}
	if resp.StatusCode != http.StatusCreated {
		t.Errorf("Create status = %d, want %d", resp.StatusCode, http.StatusCreated)
	}

	var created map[string]interface{}
	_ = json.NewDecoder(resp.Body).Decode(&created)
	resp.Body.Close()

	assetID := created["id"].(string)

	// 获取资产
	getResp, err := http.Get(server.URL + "/api/assets/" + assetID)
	if err != nil {
		t.Fatalf("Get request failed: %v", err)
	}
	if getResp.StatusCode != http.StatusOK {
		t.Errorf("Get status = %d, want %d", getResp.StatusCode, http.StatusOK)
	}

	var fetched map[string]interface{}
	_ = json.NewDecoder(getResp.Body).Decode(&fetched)
	getResp.Body.Close()

	if fetched["asset_no"] != "INT-001" {
		t.Errorf("asset_no = %v, want INT-001", fetched["asset_no"])
	}
}

func TestIntegration_CreateAndDepreciate(t *testing.T) {
	server := httptest.NewServer(setupRouter())
	defer server.Close()

	// 创建资产
	createBody := map[string]interface{}{
		"asset_no":           "INT-002",
		"name":               "折旧测试设备",
		"category":           "IT设备",
		"original_value":     600000,
		"salvage_value":      60000,
		"useful_life_months": 60,
		"acquisition_date":   "2026-01-01T00:00:00Z",
		"method":             "straight_line",
	}
	jsonBody, _ := json.Marshal(createBody)

	resp, _ := http.Post(server.URL+"/api/assets", "application/json", bytes.NewReader(jsonBody))
	var created map[string]interface{}
	_ = json.NewDecoder(resp.Body).Decode(&created)
	resp.Body.Close()

	assetID := created["id"].(string)

	// 应用折旧
	depBody := map[string]interface{}{
		"months_elapsed": 12,
	}
	depJSON, _ := json.Marshal(depBody)

	depResp, err := http.Post(server.URL+"/api/assets/"+assetID+"/depreciate", "application/json", bytes.NewReader(depJSON))
	if err != nil {
		t.Fatalf("Depreciate request failed: %v", err)
	}
	if depResp.StatusCode != http.StatusOK {
		t.Errorf("Depreciate status = %d, want %d", depResp.StatusCode, http.StatusOK)
	}

	var depreciated map[string]interface{}
	_ = json.NewDecoder(depResp.Body).Decode(&depreciated)
	depResp.Body.Close()

	accumulatedDep := int64(depreciated["accumulated_depreciation"].(float64))
	if accumulatedDep != 108000 {
		t.Errorf("accumulated_depreciation = %d, want 108000", accumulatedDep)
	}
}

func TestIntegration_ListAndDelete(t *testing.T) {
	server := httptest.NewServer(setupRouter())
	defer server.Close()

	// 创建两个资产
	for _, assetNo := range []string{"INT-003", "INT-004"} {
		body := map[string]interface{}{
			"asset_no": assetNo, "name": "设备", "category": "IT设备",
			"original_value": 100000, "salvage_value": 10000, "useful_life_months": 60,
			"acquisition_date": "2026-01-01T00:00:00Z", "method": "straight_line",
		}
		jsonBody, _ := json.Marshal(body)
		resp, _ := http.Post(server.URL+"/api/assets", "application/json", bytes.NewReader(jsonBody))
		resp.Body.Close()
	}

	// 列出
	listResp, _ := http.Get(server.URL + "/api/assets")
	var list []map[string]interface{}
	_ = json.NewDecoder(listResp.Body).Decode(&list)
	listResp.Body.Close()

	if len(list) != 2 {
		t.Errorf("list length = %d, want 2", len(list))
	}

	// 删除第一个
	firstID := list[0]["id"].(string)
	delReq, _ := http.NewRequest("DELETE", server.URL+"/api/assets/"+firstID, nil)
	delResp, err := http.DefaultClient.Do(delReq)
	if err != nil {
		t.Fatalf("Delete request failed: %v", err)
	}
	if delResp.StatusCode != http.StatusNoContent {
		t.Errorf("Delete status = %d, want %d", delResp.StatusCode, http.StatusNoContent)
	}
	delResp.Body.Close()

	// 验证删除后列表长度
	listResp2, _ := http.Get(server.URL + "/api/assets")
	var list2 []map[string]interface{}
	_ = json.NewDecoder(listResp2.Body).Decode(&list2)
	listResp2.Body.Close()

	if len(list2) != 1 {
		t.Errorf("list length after delete = %d, want 1", len(list2))
	}
}
