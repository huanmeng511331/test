package services

import (
	"fmt"
	"test/database"
	"test/models"
)

type AnalyticsData struct {
	Label string `json:"label"`
	Count int64  `json:"count"`
}

func GetAnalytics(dimension, startDate, endDate, endpoint string) ([]AnalyticsData, int64, error) {
	query := database.DB.Model(&models.TrackingRecord{})

	if startDate != "" {
		query = query.Where("created_at >= ?", startDate)
	}
	if endDate != "" {
		query = query.Where("created_at <= ?", endDate)
	}
	if endpoint != "" {
		query = query.Where("endpoint = ?", endpoint)
	}

	var results []AnalyticsData
	var total int64

	switch dimension {
	case "type":
		query.Select("caller_type as label, COUNT(*) as count").
			Group("caller_type").
			Find(&results)
	case "level":
		query.Select("caller_level as label, COUNT(*) as count").
			Group("caller_level").
			Find(&results)
	case "department":
		query.Select("caller_department as label, COUNT(*) as count").
			Group("caller_department").
			Find(&results)
	default:
		return nil, 0, fmt.Errorf("invalid dimension: %s", dimension)
	}

	database.DB.Model(&models.TrackingRecord{}).Count(&total)

	return results, total, nil
}
