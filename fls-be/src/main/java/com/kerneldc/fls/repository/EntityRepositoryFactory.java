package com.kerneldc.fls.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.kerneldc.fls.domain.AbstractEntity;
import com.kerneldc.fls.domain.AbstractImmutableEntity;
import com.kerneldc.fls.domain.AbstractPersistableEntity;
import com.kerneldc.fls.domain.IEntityEnum;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityRepositoryFactory <T extends AbstractEntity, ID extends Serializable>{

	private Map<IEntityEnum, BaseEntityRepository<T, ID>> baseEntityRepositoryMap = new HashMap<>();
	

	public EntityRepositoryFactory(Collection<BaseEntityRepository<T, ID>> baseEntityRepositories) {
		
		LOGGER.info("Loading baseEntityRepositoryMap with:");
		baseEntityRepositoryMap = baseEntityRepositories.stream().collect(Collectors.toMap(BaseEntityRepository::canHandle, r -> r));
		baseEntityRepositoryMap.forEach((entityEnum, repository) -> LOGGER.info("[{}, {}, {}]", entityEnum,
				entityEnum.getEntity().getSimpleName(),
				entityEnum.isImmutable() ? "immutable entity" : "mutable entity"));
	}

	@SuppressWarnings("unchecked")
	public BaseTableRepository<AbstractPersistableEntity, ID> getTableRepository(IEntityEnum entityEnum) {
		Preconditions.checkArgument(! /* not */ entityEnum.isImmutable(), "getTableRepository() called with %s as argument when argument is immutable", entityEnum);
		return (BaseTableRepository<AbstractPersistableEntity, ID>)getRepository(entityEnum);
	}

	@SuppressWarnings("unchecked")
	public BaseViewRepository<AbstractImmutableEntity, ID> getViewRepository(IEntityEnum entityEnum) {
		Preconditions.checkArgument(entityEnum.isImmutable(), "getTableRepository() called with %s as argument when argument is mutable", entityEnum);
		return (BaseViewRepository<AbstractImmutableEntity, ID>)getRepository(entityEnum);
	}

	public BaseEntityRepository<T, ID> getRepository(IEntityEnum entityEnum) {
		BaseEntityRepository<T, ID> repository = baseEntityRepositoryMap.get(entityEnum);
		Preconditions.checkArgument(repository != null, "Can not find a repository to handle entity: %s", entityEnum);
		return repository;
	}

//	@SuppressWarnings("unchecked")
//	public BaseInstrumentDetailRepository<AbstractPersistableEntity, ID> getBaseInstrumentRepository(IEntityEnum entityEnum) {
//		var repository = baseEntityRepositoryMap.get(entityEnum);
//		Preconditions.checkArgument(repository != null, "Can not find a repository to handle: %s", entityEnum);
//		return (BaseInstrumentDetailRepository<AbstractPersistableEntity, ID>)repository;
//	}
}
