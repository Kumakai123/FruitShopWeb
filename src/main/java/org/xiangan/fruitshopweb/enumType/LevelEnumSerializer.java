package org.xiangan.fruitshopweb.enumType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;

/**
 * (枚舉序列器)身分等級
 *
 * @author kyle
 */
public class LevelEnumSerializer extends StdSerializer<LevelEnum> {

	@Serial
	private static final long serialVersionUID = -6363601416553043319L;

	/**
	 * 默認構造函式
	 */
	protected LevelEnumSerializer() {
		this(null);
	}

	/**
	 * @param type 枚舉
	 */
	@SuppressWarnings("unchecked")
	protected LevelEnumSerializer(Class type) {
		super(type);
	}

	/**
	 * @param levelEnum 枚舉
	 * @param jsonGenerator 杰森產生器
	 * @param serializerProvider 序列化提供者
	 * @throws IOException 輸入輸出操作發生失敗或中斷
	 */
	@Override
	public void serialize(LevelEnum levelEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();

		jsonGenerator.writeStringField(
				"value",
				levelEnum.name()
		);

		jsonGenerator.writeStringField(
				"chinese",
				levelEnum.getChinese()
		);

		jsonGenerator.writeEndObject();
	}
}
