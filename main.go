package main

import (
	"fmt"
	"net/http"

	"fixedasset/handler"
	"fixedasset/repository"
	"fixedasset/service"
)

// setupRouter 组装所有依赖并返回 HTTP handler
func setupRouter() http.Handler {
	repo := repository.NewInMemoryAssetRepository()
	svc := service.NewAssetService(repo)
	assetHandler := handler.NewAssetHandler(svc)

	mux := http.NewServeMux()
	assetHandler.RegisterRoutes(mux)
	return mux
}

func main() {
	router := setupRouter()
	fmt.Println("Fixed Asset Config Service starting on :8080 ...")
	if err := http.ListenAndServe(":8080", router); err != nil {
		fmt.Printf("Server error: %v\n", err)
	}
}
