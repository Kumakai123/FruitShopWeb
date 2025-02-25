package org.xiangan.fruitshopweb.enumType;

import jakarta.persistence.AttributeConverter;

import java.util.Objects;

/**
 * (數據庫枚舉轉換器)單位類型
 *
 * @author kyle
 */
public class ProductTypeEnumConverter implements AttributeConverter<ProductTypeEnum, String> {

	@Override
	public String convertToDatabaseColumn(ProductTypeEnum attribute) {
		return Objects.isNull(attribute) ? null : attribute.name();
	}

	@Override
	public ProductTypeEnum convertToEntityAttribute(String dbData) {
		return Objects.isNull(dbData) || dbData.isBlank() ? null : ProductTypeEnum.valueOf(dbData);
	}
}
