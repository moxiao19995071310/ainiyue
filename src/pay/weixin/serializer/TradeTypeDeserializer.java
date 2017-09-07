package pay.weixin.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

import pay.weixin.model.enums.TradeType;

/**
 * 交易类型反序列化器
 * Date: 27/11/15
 * @since 1.0.0
 */
public class TradeTypeDeserializer extends JsonDeserializer<TradeType> {

    @Override
    public TradeType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return TradeType.from(jp.getValueAsString());
    }
}
