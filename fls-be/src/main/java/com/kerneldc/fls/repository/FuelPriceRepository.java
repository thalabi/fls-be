package com.kerneldc.fls.repository;

import com.kerneldc.fls.domain.FlsEntityEnum;
import com.kerneldc.fls.domain.IEntityEnum;
import com.kerneldc.fls.domain.fuelprice.FuelPrice;

public interface FuelPriceRepository extends BaseTableRepository<FuelPrice, Long>{

	@Override
	default IEntityEnum canHandle() {
		return FlsEntityEnum.FUEL_PRICE;
	}

}
