package pay.weixin.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

import pay.weixin.model.enums.TradeState;

/**
 * 交易状态反序列化器
 * Date: 27/11/15
 * @since 1.0.0
 */
public class TradeStateDeserializer extends JsonDeserializer<TradeState> {

    @Override
    public TradeState deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return TradeState.from(jp.getValueAsString());
    }
}
