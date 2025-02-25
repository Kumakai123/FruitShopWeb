package org.xiangan.fruitshopweb.enumType;

import jakarta.persistence.AttributeConverter;

import java.util.Objects;

/**
 * (數據庫枚舉轉換器)身分等級
 *
 * @author kyle
 */
public class LevelEnumConverter implements AttributeConverter<LevelEnum, String> {

	@Override
	public String convertToDatabaseColumn(LevelEnum attribute) {
		return Objects.isNull(attribute) ? null : attribute.name();
	}

	@Override
	public LevelEnum convertToEntityAttribute(String dbData) {
		return Objects.isNull(dbData) || dbData.isBlank() ? null : LevelEnum.valueOf(dbData);
	}
}
