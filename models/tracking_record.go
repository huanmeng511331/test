package models

import "time"

type TrackingRecord struct {
	ID               uint      `gorm:"primaryKey" json:"id"`
	Endpoint         string    `gorm:"size:50;index" json:"endpoint"`
	CallerID         string    `gorm:"size:100" json:"callerId"`
	CallerType       string    `gorm:"size:50;index" json:"callerType"`
	CallerLevel      string    `gorm:"size:50;index" json:"callerLevel"`
	CallerDepartment string    `gorm:"size:100;index" json:"callerDepartment"`
	CreatedAt        time.Time `gorm:"index" json:"createdAt"`
}
