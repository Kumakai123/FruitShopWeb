package org.xiangan.fruitshopweb.enumType;

import jakarta.persistence.AttributeConverter;

import java.util.Objects;

/**
 * (數據庫枚舉轉換器)單位類型
 *
 * @author kyle
 */
public class UnitTypeEnumConverter implements AttributeConverter<UnitTypeEnum, String> {

	@Override
	public String convertToDatabaseColumn(UnitTypeEnum attribute) {
		return Objects.isNull(attribute) ? null : attribute.name();
	}

	@Override
	public UnitTypeEnum convertToEntityAttribute(String dbData) {
		return Objects.isNull(dbData) || dbData.isBlank() ? null : UnitTypeEnum.valueOf(dbData);
	}
}
