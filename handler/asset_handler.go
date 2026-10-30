package handler

import (
	"encoding/json"
	"errors"
	"net/http"
	"strings"
	"time"

	"fixedasset/model"
	"fixedasset/repository"
	"fixedasset/service"
)

// AssetHandler HTTP 请求处理器
type AssetHandler struct {
	svc *service.AssetService
}

// NewAssetHandler 创建 handler 实例
func NewAssetHandler(svc *service.AssetService) *AssetHandler {
	return &AssetHandler{svc: svc}
}

// RegisterRoutes 在 mux 上注册资产相关路由
func (h *AssetHandler) RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/api/assets", h.handleAssetsCollection)
	mux.HandleFunc("/api/assets/", h.handleAssetItem)
}

func (h *AssetHandler) handleAssetsCollection(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case http.MethodPost:
		h.createAsset(w, r)
	case http.MethodGet:
		h.listAssets(w, r)
	default:
		http.Error(w, `{"error":"method not allowed"}`, http.StatusMethodNotAllowed)
	}
}

func (h *AssetHandler) handleAssetItem(w http.ResponseWriter, r *http.Request) {
	path := strings.TrimPrefix(r.URL.Path, "/api/assets/")
	parts := strings.Split(path, "/")

	if len(parts) == 0 || parts[0] == "" {
		http.Error(w, `{"error":"asset id required"}`, http.StatusBadRequest)
		return
	}

	id := parts[0]

	// /api/assets/{id}/depreciate
	if len(parts) == 2 && parts[1] == "depreciate" {
		if r.Method != http.MethodPost {
			http.Error(w, `{"error":"method not allowed"}`, http.StatusMethodNotAllowed)
			return
		}
		h.applyDepreciation(w, r, id)
		return
	}

	// /api/assets/{id}
	switch r.Method {
	case http.MethodGet:
		h.getAsset(w, r, id)
	case http.MethodPut:
		h.updateAsset(w, r, id)
	case http.MethodDelete:
		h.deleteAsset(w, r, id)
	default:
		http.Error(w, `{"error":"method not allowed"}`, http.StatusMethodNotAllowed)
	}
}

func (h *AssetHandler) createAsset(w http.ResponseWriter, r *http.Request) {
	var input service.CreateAssetInput
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		writeJSONError(w, http.StatusBadRequest, "invalid JSON body")
		return
	}

	if input.AcquisitionDate.IsZero() {
		input.AcquisitionDate = time.Now()
	}

	asset, err := h.svc.CreateAsset(&input)
	if err != nil {
		if errors.Is(err, service.ErrValidation) {
			writeJSONError(w, http.StatusBadRequest, err.Error())
			return
		}
		if errors.Is(err, repository.ErrDuplicateAssetNo) {
			writeJSONError(w, http.StatusConflict, err.Error())
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}

	writeJSON(w, http.StatusCreated, asset)
}

func (h *AssetHandler) getAsset(w http.ResponseWriter, r *http.Request, id string) {
	asset, err := h.svc.GetAsset(id)
	if err != nil {
		if errors.Is(err, repository.ErrAssetNotFound) {
			writeJSONError(w, http.StatusNotFound, "asset not found")
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}
	writeJSON(w, http.StatusOK, asset)
}

func (h *AssetHandler) listAssets(w http.ResponseWriter, r *http.Request) {
	category := r.URL.Query().Get("category")
	if category != "" {
		assets := h.svc.ListByCategory(category)
		writeJSON(w, http.StatusOK, assets)
		return
	}
	assets := h.svc.ListAll()
	writeJSON(w, http.StatusOK, assets)
}

func (h *AssetHandler) updateAsset(w http.ResponseWriter, r *http.Request, id string) {
	var asset model.FixedAsset
	if err := json.NewDecoder(r.Body).Decode(&asset); err != nil {
		writeJSONError(w, http.StatusBadRequest, "invalid JSON body")
		return
	}

	asset.ID = id
	if err := h.svc.UpdateAsset(&asset); err != nil {
		if errors.Is(err, repository.ErrAssetNotFound) {
			writeJSONError(w, http.StatusNotFound, "asset not found")
			return
		}
		if errors.Is(err, repository.ErrDuplicateAssetNo) {
			writeJSONError(w, http.StatusConflict, err.Error())
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}

	updated, _ := h.svc.GetAsset(id)
	writeJSON(w, http.StatusOK, updated)
}

func (h *AssetHandler) deleteAsset(w http.ResponseWriter, r *http.Request, id string) {
	if err := h.svc.DeleteAsset(id); err != nil {
		if errors.Is(err, repository.ErrAssetNotFound) {
			writeJSONError(w, http.StatusNotFound, "asset not found")
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

func (h *AssetHandler) applyDepreciation(w http.ResponseWriter, r *http.Request, id string) {
	var body struct {
		MonthsElapsed int `json:"months_elapsed"`
	}
	if err := json.NewDecoder(r.Body).Decode(&body); err != nil {
		writeJSONError(w, http.StatusBadRequest, "invalid JSON body")
		return
	}

	if err := h.svc.ApplyDepreciation(id, body.MonthsElapsed); err != nil {
		if errors.Is(err, repository.ErrAssetNotFound) {
			writeJSONError(w, http.StatusNotFound, "asset not found")
			return
		}
		if errors.Is(err, service.ErrAssetDisposed) {
			writeJSONError(w, http.StatusConflict, "asset has been disposed")
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}

	asset, _ := h.svc.GetAsset(id)
	writeJSON(w, http.StatusOK, asset)
}

func writeJSON(w http.ResponseWriter, status int, data interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(data)
}

func writeJSONError(w http.ResponseWriter, status int, message string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(map[string]string{"error": message})
}
