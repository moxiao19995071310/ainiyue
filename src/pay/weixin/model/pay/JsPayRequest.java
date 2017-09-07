package pay.weixin.model.pay;


/**
 * JS支付请求对象
 * Date: 26/11/15
 * @since 1.0.0
 */
public class JsPayRequest extends PayRequest {

    /**
     * 用户标识
     * {@link pay.weixin.model.enums.WepayField#OPEN_ID}
     */
    private String openId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String toString() {
        return "JsPayRequest{" +
                "openId='" + openId + '\'' +
                "} " + super.toString();
    }
}
