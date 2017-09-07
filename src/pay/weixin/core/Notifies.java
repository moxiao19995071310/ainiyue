package pay.weixin.core;

import java.util.HashMap;
import java.util.Map;

import pay.weixin.model.enums.WepayField;
import pay.weixin.util.Maps;

/**
 * 通知组件
 * Date: 28/11/15
 * @since 1.0.0
 */
public class Notifies extends Component {

    Notifies(Wepay wepay) {
        super(wepay);
    }

    /**
     * 签名校验
     * @param params 待验证参数(包含sign)
     * @return 验证通过返回true，反之false
     */
    public Boolean verifySign(Map<String, ?> params){
        return doVerifySign(params);
    }

    /**
     * 通知成功
     * @return 通知成功的XML消息
     */
    public String ok(){
        Map<String, String> notifyParams = new HashMap<>();
        notifyParams.put(WepayField.RETURN_CODE, "SUCCESS");
        notifyParams.put(WepayField.RETURN_MSG, "OK");
        return Maps.toXml(notifyParams);
    }

    /**
     * 通知不成功
     * @param errMsg 失败消息
     * @return 通知失败的XML消息
     */
    public String notOk(String errMsg){
        Map<String, String> notifyParams = new HashMap<>();
        notifyParams.put(WepayField.RETURN_CODE, "FAIL");
        notifyParams.put(WepayField.RETURN_MSG, errMsg);
        return Maps.toXml(notifyParams);
    }
}
