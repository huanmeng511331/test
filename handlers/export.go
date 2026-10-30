package handlers

import (
	"fmt"
	"net/http"
	"test/database"
	"test/models"

	"github.com/gin-gonic/gin"
	"github.com/xuri/excelize/v2"
)

func ExportHandler(c *gin.Context) {
	exportType := c.Query("type")
	if exportType == "" {
		exportType = "all"
	}

	f := excelize.NewFile()
	defer f.Close()

	switch exportType {
	case "hello":
		exportHelloData(f)
	case "hash":
		exportHashData(f)
	case "sort":
		exportSortData(f)
	case "all":
		exportHelloData(f)
		exportHashData(f)
		exportSortData(f)
	default:
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid export type"})
		return
	}

	// 设置响应头
	filename := fmt.Sprintf("export_%s.xlsx", exportType)
	c.Header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	c.Header("Content-Disposition", fmt.Sprintf("attachment; filename=%s", filename))

	if err := f.Write(c.Writer); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
}

func exportHelloData(f *excelize.File) {
	sheet := "HelloWorld"
	index, _ := f.NewSheet(sheet)
	f.SetActiveSheet(index)
	f.SetSheetName("Sheet1", sheet)

	// 设置表头
	f.SetCellValue(sheet, "A1", "调用时间")
	f.SetCellValue(sheet, "B1", "调用者ID")
	f.SetCellValue(sheet, "C1", "参数")

	// 查询数据
	var records []models.TrackingRecord
	database.DB.Where("endpoint = ?", "/api/hello").Find(&records)

	for i, record := range records {
		row := i + 2
		f.SetCellValue(sheet, fmt.Sprintf("A%d", row), record.CreatedAt.Format("2006-01-02 15:04:05"))
		f.SetCellValue(sheet, fmt.Sprintf("B%d", row), record.CallerID)
		f.SetCellValue(sheet, fmt.Sprintf("C%d", row), "name=World")
	}
}

func exportHashData(f *excelize.File) {
	sheet := "HashAlgorithm"
	index, _ := f.NewSheet(sheet)

	// 设置表头
	f.SetCellValue(sheet, "A1", "调用时间")
	f.SetCellValue(sheet, "B1", "调用者ID")
	f.SetCellValue(sheet, "C1", "算法")
	f.SetCellValue(sheet, "D1", "原始数据")

	// 查询数据
	var records []models.TrackingRecord
	database.DB.Where("endpoint = ?", "/api/hash").Find(&records)

	for i, record := range records {
		row := i + 2
		f.SetCellValue(sheet, fmt.Sprintf("A%d", row), record.CreatedAt.Format("2006-01-02 15:04:05"))
		f.SetCellValue(sheet, fmt.Sprintf("B%d", row), record.CallerID)
		f.SetCellValue(sheet, fmt.Sprintf("C%d", row), "various")
		f.SetCellValue(sheet, fmt.Sprintf("D%d", row), "user input")
	}
}

func exportSortData(f *excelize.File) {
	sheet := "BubbleSort"
	index, _ := f.NewSheet(sheet)

	// 设置表头
	f.SetCellValue(sheet, "A1", "调用时间")
	f.SetCellValue(sheet, "B1", "调用者ID")
	f.SetCellValue(sheet, "C1", "数组长度")

	// 查询数据
	var records []models.TrackingRecord
	database.DB.Where("endpoint = ?", "/api/bubble-sort").Find(&records)

	for i, record := range records {
		row := i + 2
		f.SetCellValue(sheet, fmt.Sprintf("A%d", row), record.CreatedAt.Format("2006-01-02 15:04:05"))
		f.SetCellValue(sheet, fmt.Sprintf("B%d", row), record.CallerID)
		f.SetCellValue(sheet, fmt.Sprintf("C%d", row), "N/A")
	}
}
