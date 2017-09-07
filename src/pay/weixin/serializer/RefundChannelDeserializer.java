package pay.weixin.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import me.hao0.common.util.Strings;

import java.io.IOException;

import pay.weixin.model.enums.RefundChannel;

/**
 * 退款渠道反序列化器
 * Date: 27/11/15
 * @since 1.0.0
 */
public class RefundChannelDeserializer extends JsonDeserializer<RefundChannel> {

    @Override
    public RefundChannel deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String val = jp.getValueAsString();
        if (Strings.isNullOrEmpty(val)){
            return null;
        }
        return RefundChannel.from(val);
    }
}
