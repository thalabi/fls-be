package com.kerneldc.fls.domain;

import java.util.Arrays;

import com.kerneldc.fls.domain.acparameters.AcParameters;
import com.kerneldc.fls.domain.enginelog.EngineLog;
import com.kerneldc.fls.domain.enginelogv.EngineLogV;
import com.kerneldc.fls.domain.fuellog.FuelLog;
import com.kerneldc.fls.domain.fuelprice.FuelPrice;
import com.kerneldc.fls.domain.journeylog.JourneyLog;
import com.kerneldc.fls.domain.journeylogv.JourneyLogV;
import com.kerneldc.fls.domain.logsheet.LogSheet;
import com.kerneldc.fls.domain.tsmoh.TsmohV;
import com.kerneldc.fls.domain.tsnv.TsnV;

public enum FlsEntityEnum implements IEntityEnum {
	AC_PARAMETERS(AcParameters.class, false, new String[] {}),
	FUEL_LOG(FuelLog.class, false, new String[] {}),
	FUEL_PRICE(FuelPrice.class, false, new String[] {}),
	LOG_SHEET(LogSheet.class, false, new String[] {}),
	JOURNEY_LOG(JourneyLog.class, false, new String[] {}),
	ENGINE_LOG(EngineLog.class, false, new String[] {}),
	TSN_V(TsnV.class, true, new String[] {}),
	TSMOH_V(TsmohV.class, true, new String[] {}),
	JOURNEY_LOG_V(JourneyLogV.class, true, new String[] {}),
	ENGINE_LOG_V(EngineLogV.class, true, new String[] {}),
	;

	Class<? extends AbstractEntity> entity;
	boolean immutable;
	String[] writeColumnOrder;

	FlsEntityEnum(Class<? extends AbstractEntity> entity, boolean immutable) {
		this.entity = entity;
		this.immutable = immutable;
	}
	FlsEntityEnum(Class<? extends AbstractEntity> entity, boolean immutable, String[] writeColumnOrder) {
		this.entity = entity;
		this.immutable = immutable;
		// tag SOURCECSVLINENUMBER to the end of the writeColumnOrder
		this.writeColumnOrder = Arrays.copyOf(writeColumnOrder, writeColumnOrder.length+1);
		this.writeColumnOrder[this.writeColumnOrder.length-1] = "SOURCECSVLINENUMBER";  
	}

	@Override
	public Class<? extends AbstractEntity> getEntity() {
		return entity;
	}

	@Override
	public boolean isImmutable() {
		return immutable;
	}

	@Override
	public String[] getWriteColumnOrder() {
		return writeColumnOrder;
	}

}
