package com.kerneldc.fls.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.kerneldc.fls.domain.AbstractEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntitySpecification<T> implements Specification<T> {

	private static final long serialVersionUID = 1L;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private enum QueryOperatorEnum {
		EQUALS("equals"), NOT_EQUALS("notEquals"),
		GT("gt"), GTE("gte"), LT("lt"), LTE("lte"),
		STARTS_WITH("startsWith"), CONTAINS("contains"), NOT_CONTAINS("notContains"), ENDS_WITH("endsWith"),
		DATE_IS("dateIs"), DATE_IS_NOT("dateIsNot"), DATE_BEFORE("dateBefore"), DATE_AFTER("dateAfter");

		private String operator;
		public String getOperator() {
			return operator;
		}
		QueryOperatorEnum(String operator) {
			this.operator = operator;
		}
		public static QueryOperatorEnum fromName(String name) {
			for (QueryOperatorEnum e : QueryOperatorEnum.values()) {
				if (e.getOperator().equalsIgnoreCase(name)) {
					return e;
				}
			}
			throw new IllegalArgumentException(String.format("Invalid operator [%s]", name));
		}
	}
	record Filter(String field, QueryOperatorEnum operator, String value) {};
	private transient List<Filter> filterList = new ArrayList<>();
	private transient EntityType<? extends AbstractEntity> entityMetamodel;

	public EntitySpecification(EntityType<? extends AbstractEntity> entityMetaModel, String searchCriteria) {
		if (StringUtils.isEmpty(searchCriteria)) {
			return;
		}
		this.entityMetamodel = entityMetaModel;
		filterList = Arrays.asList(searchCriteria.split(",")).stream().map(criterion -> {
			var criterionParts = criterion.split("\\|");
			LOGGER.info("criterionParts: {}", Arrays.asList(criterionParts));
			return new Filter(criterionParts[0], QueryOperatorEnum.fromName(criterionParts[1]), criterionParts[2]);
		}).toList();
	}

	@Override
	public Predicate toPredicate(Root<T> entity, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		return buildSpecificationFromFilters().toPredicate(entity, query, criteriaBuilder);
	}

	private Specification<T> buildSpecificationFromFilters() {
		if (filterList.isEmpty()) {
			return Specification.where(null);
		}
		Specification<T> specification = Specification.where(createSpecification(filterList.get(0)));
		for (int i = 1; i < filterList.size(); i++) {
			specification = specification.and(createSpecification(filterList.get(i)));
		}
		return specification;
	}

	private Specification<T> createSpecification(Filter inputFilter) {
		var field = inputFilter.field();
		var value = inputFilter.value();
		
		var fieldType = entityMetamodel.getDeclaredAttribute(field).getJavaType().getSimpleName();
		LOGGER.info("fieldType: {}", fieldType);
		LOGGER.info("input: {}, field: {}, value: {}", inputFilter, field, value);

		switch (fieldType) {
		case "String" -> {
			return handleStringFieldType(inputFilter, field, value);
		}
		case "Short", "Integer", "Double", "Float", "BigDecimal" -> {
			return handleNumberFieldType(inputFilter, field, value);
		}
		case "LocalDateTime" -> {
			return handleLocalDateTimeFieldType(inputFilter, field, value);
		}
		case "Date" -> {
			return handleDateFieldType(inputFilter, field, value);
		}
		default -> {
			var exceptionMessage = String.format("Field data type [%s], not supported.", fieldType);
			throw new IllegalArgumentException(exceptionMessage);
		}
		}
		
	}
	private Specification<T> handleLocalDateTimeFieldType(Filter inputFilter, String field, String value) {
		var localDateTimeValue = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
		switch (inputFilter.operator()) {
		case DATE_IS -> {
				return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(entity.get(field), localDateTimeValue);
		}
		case DATE_IS_NOT -> {
				return (entity, query, criteriaBuilder) -> criteriaBuilder.notEqual(entity.get(field), localDateTimeValue);
		}
		case DATE_BEFORE -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.lessThan(entity.get(field), localDateTimeValue);
		}
		case DATE_AFTER -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.greaterThan(entity.get(field), localDateTimeValue);
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + inputFilter.operator());
		}
	}

	private Specification<T> handleStringFieldType(Filter inputFilter, String field, String value) {
		switch (inputFilter.operator()) {
		case EQUALS -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(entity.get(field)), value.toLowerCase());
		}
		case NOT_EQUALS -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.notEqual(criteriaBuilder.lower(entity.get(field)), value.toLowerCase());
		}
		case STARTS_WITH -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(entity.get(field)), value.toLowerCase() + "%");
		}
		case CONTAINS -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(entity.get(field)), "%" + value.toLowerCase() + "%");
		}
		case NOT_CONTAINS -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.not(criteriaBuilder.like(criteriaBuilder.lower(entity.get(field)), "%" + value.toLowerCase() + "%"));
		}
		case ENDS_WITH -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(entity.get(field)), "%" + value.toLowerCase());
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + inputFilter.operator());
		}
	}

	private Specification<T> handleNumberFieldType(Filter inputFilter, String field, String value) {
		switch (inputFilter.operator()) {
		case EQUALS -> {
				return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(entity.get(field), value);
		}
		case NOT_EQUALS -> {
				return (entity, query, criteriaBuilder) -> criteriaBuilder.notEqual(entity.get(field), value);
		}
		case GT -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.greaterThan(entity.get(field), value);
		}
		case GTE -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(entity.get(field), value);
		}
		case LT -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.lessThan(entity.get(field), value);
		}
		case LTE -> {
			return (entity, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(entity.get(field), value);
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + inputFilter.operator());
		}
	}

	private Specification<T> handleDateFieldType(Filter inputFilter, String field, String value) {
		try {
			var date = dateFormat.parse(value);
			switch (inputFilter.operator()) {
			case DATE_IS -> {
					return (entity, query, criteriaBuilder) -> criteriaBuilder.equal(entity.get(field), date);
			}
			case DATE_IS_NOT -> {
					return (entity, query, criteriaBuilder) -> criteriaBuilder.notEqual(entity.get(field), date);
			}
			case DATE_BEFORE -> {
				return (entity, query, criteriaBuilder) -> criteriaBuilder.lessThan(entity.get(field), date);
			}
			case DATE_AFTER -> {
				return (entity, query, criteriaBuilder) -> criteriaBuilder.greaterThan(entity.get(field), date);
			}
			default -> throw new IllegalArgumentException("Unexpected value: " + inputFilter.operator());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException("Unable to parse value: " + inputFilter.value());
		}
	}

}
