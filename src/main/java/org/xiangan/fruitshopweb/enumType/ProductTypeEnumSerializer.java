package org.xiangan.fruitshopweb.enumType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;

/**
 * (枚舉序列器)單位類型
 *
 * @author kyle
 */
public class ProductTypeEnumSerializer extends StdSerializer<ProductTypeEnum> {

	@Serial
	private static final long serialVersionUID = -3332008242802249350L;

	/**
	 * 默認構造函式
	 */
	protected ProductTypeEnumSerializer() {
		this(null);
	}

	/**
	 * @param type 枚舉
	 */
	@SuppressWarnings("unchecked")
	protected ProductTypeEnumSerializer(Class type) {
		super(type);
	}

	/**
	 * @param productTypeEnum 枚舉
	 * @param jsonGenerator 杰森產生器
	 * @param serializerProvider 序列化提供者
	 * @throws IOException 輸入輸出操作發生失敗或中斷
	 */
	@Override
	public void serialize(ProductTypeEnum productTypeEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();

		jsonGenerator.writeStringField(
				"value",
				productTypeEnum.name()
		);

		jsonGenerator.writeStringField(
				"chinese",
				productTypeEnum.getChinese()
		);

		jsonGenerator.writeEndObject();
	}
}
