package service

import (
	"fixedasset/model"
)

// CalculateDepreciation 根据折旧方法和已使用月数计算月折旧额和累计折旧额
// 返回值: (月折旧额, 累计折旧额)，单位均为分
func CalculateDepreciation(asset *model.FixedAsset, monthsElapsed int) (monthlyDep int64, totalDep int64) {
	if asset.UsefulLifeMonths <= 0 || monthsElapsed <= 0 {
		return 0, 0
	}

	switch asset.Method {
	case model.DepreciationStraightLine:
		return calculateStraightLine(asset, monthsElapsed)
	case model.DepreciationDoubleDeclining:
		return calculateDoubleDeclining(asset, monthsElapsed)
	default:
		return 0, 0
	}
}

// calculateStraightLine 直线法折旧
// 月折旧额 = (原值 - 残值) / 使用月数
// 累计折旧不超过 (原值 - 残值)
func calculateStraightLine(asset *model.FixedAsset, monthsElapsed int) (monthlyDep int64, totalDep int64) {
	depreciableBase := asset.OriginalValue - asset.SalvageValue
	if depreciableBase <= 0 {
		return 0, 0
	}

	monthlyDep = depreciableBase / int64(asset.UsefulLifeMonths)

	effectiveMonths := monthsElapsed
	if effectiveMonths > asset.UsefulLifeMonths {
		effectiveMonths = asset.UsefulLifeMonths
	}

	totalDep = monthlyDep * int64(effectiveMonths)
	if totalDep > depreciableBase {
		totalDep = depreciableBase
	}

	return monthlyDep, totalDep
}

// calculateDoubleDeclining 双倍余额递减法
// 月折旧率 = 2 / 使用月数
// 每月折旧 = 当前净值 * 月折旧率
// 净值不低于残值
func calculateDoubleDeclining(asset *model.FixedAsset, monthsElapsed int) (monthlyDep int64, totalDep int64) {
	depreciableBase := asset.OriginalValue - asset.SalvageValue
	if depreciableBase <= 0 {
		return 0, 0
	}

	monthlyRate := float64(2) / float64(asset.UsefulLifeMonths)
	netValue := float64(asset.OriginalValue)

	effectiveMonths := monthsElapsed
	if effectiveMonths > asset.UsefulLifeMonths {
		effectiveMonths = asset.UsefulLifeMonths
	}

	// 第一个月的月折旧额（用于返回值）
	firstMonthly := int64(netValue * monthlyRate)

	var accumulated int64
	for i := 0; i < effectiveMonths; i++ {
		dep := int64(netValue * monthlyRate)
		netValue -= float64(dep)

		// 确保净值不低于残值
		if int64(netValue) < asset.SalvageValue {
			dep += int64(netValue) - asset.SalvageValue
			netValue = float64(asset.SalvageValue)
			accumulated += dep
			break
		}
		accumulated += dep
	}

	if accumulated > depreciableBase {
		accumulated = depreciableBase
	}

	return firstMonthly, accumulated
}
