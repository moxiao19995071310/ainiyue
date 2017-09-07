package pay.weixin.core;

import me.hao0.common.http.Http;
import me.hao0.common.http.Https;
import me.hao0.common.json.Jsons;
import me.hao0.common.security.MD5;
import me.hao0.common.util.Strings;
import me.hao0.common.xml.XmlReaders;

import java.util.Map;
import java.util.TreeMap;

import pay.weixin.exception.WepayException;
import pay.weixin.model.enums.WepayField;
import pay.weixin.util.Maps;
import pay.weixin.util.ReadXML;

/**
 */
public abstract class Component {

    protected Wepay wepay;

    protected Component(Wepay wepay){
        this.wepay = wepay;
    }

    protected Map<String, Object> doPost(final String url, final Map<String, String> params){
        String requestBody = Maps.toXml(params);
        String resp = Http.post(url).ssl().body(requestBody).request();
        Map<String, Object> respMap = toMap(resp.replaceAll("(\\r|\\n)", ""));
        doVerifySign(respMap);
        return respMap;
    }

    protected <T> T doHttpsPost(final String url, final Map<String, String> params, Class<T> respClazz){
        String requestBody = Maps.toXml(params);
        String resp = Https.post(url).body(requestBody)
                .ssLSocketFactory(wepay.getSslSocketFactory()).request();
        /*System.out.println("转账返回微信后台+++"+resp);*/
        Map<String, Object> respMap = toMap(resp.replaceAll("(\\r|\\n)", ""));
        doVerifySign(respMap);
        return Jsons.DEFAULT.fromJson(Jsons.DEFAULT.toJson(respMap), respClazz);
    }
    
    /** 
    * @Title: doHttpsPost2 
    * @Description: (本方法的作用是：转账请求返回Map) 
    * @param @param url
    * @param @param params
    * @param @param respClazz
    * @param @return    设定文件 
    * @return T    返回类型 
    * @author ggj
    * @date 2016年6月30日 上午2:48:49 
    * @throws 
    */
    protected <T> T doHttpsPost2(final String url, final Map<String, String> params, Class<T> respClazz){
    	String requestBody = Maps.toXml(params);
    	String resp = Https.post(url).body(requestBody)
    			.ssLSocketFactory(wepay.getSslSocketFactory()).request();
    	/* 直接将xml 转为Map (新增的一个方法)*/
    	Map<String, String> respMap =  ReadXML.getXmlElmentValue(resp);
       System.out.println("微信转账返回--"+respMap);
    	doVerifySign(respMap);
    	return (T) respMap ;
    }


    /**
     * 将微信XML转换为Map
     * @param xml xml字符串
     * @return Map对象，或抛WechatException
     */
    protected Map<String, Object> toMap(final String xml) {
        XmlReaders readers = readResp(xml);
        return Maps.toMap(readers);
    }

    /**
     * 读取微信xml响应
     * @param xml xml字符串
     * @return 若成功，返回对应Reader，反之抛WepayException
     */
    private XmlReaders readResp(final String xml) {
        XmlReaders readers = XmlReaders.create(xml);
        String returnCode = readers.getNodeStr(WepayField.RETURN_CODE);
        if (WepayField.SUCCESS.equals(returnCode)){
            String resultCode = readers.getNodeStr(WepayField.RESULT_CODE);
            if (WepayField.SUCCESS.equals(resultCode)){
                return readers;
            }
            throw new WepayException(
                    readers.getNodeStr(WepayField.ERR_CODE),
                    readers.getNodeStr(WepayField.ERR_CODE_DES));
        }
        throw new WepayException(
                readers.getNodeStr(WepayField.RETURN_CODE),
                readers.getNodeStr(WepayField.RETURN_MSG));
    }

    /**
     * 构建配置参数
     * @param params 参数
     */
    protected void buildConfigParams(final Map<String, String> params){
        params.put(WepayField.APP_ID, wepay.getAppId());
        params.put(WepayField.MCH_ID, wepay.getMchId());
    }
    
    /**
     * 构建配置 转账参数
     * @param params 参数
     */
    protected void buildConfigParams2(final Map<String, String> params){
    	params.put(WepayField.MCH_APPID, wepay.getAppId());
    	params.put(WepayField.MCHID, wepay.getMchId());
    }

    /**
     * 构建签名参数  FIXME ggj 签名
     * @param params 支付参数
     */
    protected void buildSignParams(final Map<String, String> params) {
        String sign = doSign(params);
        put(params, WepayField.SIGN, sign);
    }

    /**
     * 支付请求前签名 FIXME ggj
     * @param params 参数(已经升序, 排出非空值和sign)
     * @return MD5的签名字符串(大写)
     */
    protected String doSign(final Map<String, String> params) {
        StringBuilder signing = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!Strings.isNullOrEmpty(entry.getValue())){
                signing.append(entry.getKey()).append('=').append(entry.getValue()).append("&");
            }
        }

        // append key
        signing.append("key=").append(wepay.getAppKey());

        // md5
        return MD5.generate(signing.toString(), false).toUpperCase();
    }

    /**
     * 校验
     * @param xml 微信xml内容
     * @return 校验成功返回true，反之false
     */
    protected Boolean doVerifySign(final String xml) {
        return doVerifySign(toMap(xml));
    }

    /**
     * 校验参数
     * @param data 待校验参数
     * @return 校验成功返回true，反之false
     */
    protected Boolean doVerifySign(final Map<String, ?> data) {
        String actualSign = String.valueOf(data.get(WepayField.SIGN));
        Map<String, String> signingMap = filterSignParams(data);
        String expectSign = doSign(signingMap);
        return expectSign.equals(actualSign);
    }

    /**
     * 过滤签名参数(升序，排出空值，sign)
     * @param params 待校验参数
     * @return 过滤后的参数
     */
    protected Map<String, String> filterSignParams(final Map<String, ?> params) {
        Map<String, String> validParams = new TreeMap<>();

        for (Map.Entry<String, ?> param : params.entrySet()){
            if (WepayField.SIGN.equals(param.getKey())
                    || param.getValue() == null
                    || "".equals(String.valueOf(param.getValue()))){
                continue;
            }
            validParams.put(param.getKey(), String.valueOf(param.getValue()));
        }

        return validParams;
    }

    protected void putIfNotEmpty(final Map<String, String> map, String field, String paramValue) {
        if (!Strings.isNullOrEmpty(paramValue)){
            map.put(field, paramValue);
        }
    }

    protected void put(final Map<String, String> map, String field, String paramValue){
        map.put(field, paramValue);
    }
}
