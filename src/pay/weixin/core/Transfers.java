package pay.weixin.core;

import java.util.Map;
import java.util.TreeMap;

import me.hao0.common.date.Dates;
import me.hao0.common.security.MD5;
import pay.weixin.model.enums.TradeType;
import pay.weixin.model.enums.WepayField;
import pay.weixin.model.pay.JsPayRequest;
import pay.weixin.model.pay.JsPayResponse;
import pay.weixin.model.pay.TransfersRequest;
import pay.weixin.model.pay.TransfersResponse;
import pay.weixin.util.RandomStrs;

/** 
* @ClassName: Transfers 
* @Description:   (这个类的作用是： 转账组件！！) 
* @author ggj
* @date 2016年6月28日 下午10:28:57 
*  
*/
public class Transfers extends Component {

	protected Transfers(Wepay wepay) {
		super(wepay);
	}
	 /**
     * 统一下单接口
     */
    private static final String TRANSFER_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    
    /**
	 * @Title: doTransfers
	 * @Description: (本方法的作用是：发送转账请求对象)
	 * @param @param TransfersParams
	 * @param @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @author ggj
	 * @date 2016年6月28日 下午5:01:11
	 * @throws
	 */
    
    /*public TransfersResponse doTransfers(TransfersRequest request) {
	// 构建转账请求参数
	Map<String, String> transfersParams = buildTransfersParams(request);

	System.out.println("签名前sign==" + transfersParams.get("sign"));
	// 请求前,添加签名sign 到请求参数
	buildSignParams(transfersParams);

	// System.out.println("签名后：：：sign==" + transfersParams.get("sign"));
	// System.out.println("transfersParams "+transfersParams.toString());
	TransfersResponse transfersResponse = null;
	try {
		transfersResponse = doHttpsPost(TRANSFER_URL, transfersParams,
				TransfersResponse.class);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		System.out.println("transfersResponse====" + transfersResponse);
		e.printStackTrace();
	}
	return transfersResponse;
}*/
    
    
	public Map<String, Object> doTransfers2(TransfersRequest request) {
		// 构建转账请求参数
		Map<String, String> transfersParams = buildTransfersParams(request);

		 
		// 请求前,添加签名sign 到请求参数
		buildSignParams(transfersParams);

		System.out.println("Transfers.java 签名后：：：sign==" + transfersParams.get("sign"));
		 
		return (Map<String, Object>) doHttpsPost2(TRANSFER_URL, transfersParams, TransfersRequest.class);
	}
    
	
    
	
    /**
     * 构建 转账支付参数
     * @param request 支付请求对象
     * @param tradeType 交易类型
     * @return 支付MAP参数
     */
	private Map<String, String> buildTransfersParams(TransfersRequest request) {
		Map<String, String> transfers = new TreeMap<>();

		// 配置mch_appid mchid 参数
		buildConfigParams2(transfers);

		// 业务必需参数

		put(transfers, WepayField.AMOUNT, request.getAmount() + "");
		// 随机字符串
		put(transfers, WepayField.NONCE_STR, RandomStrs.generate(16));
		put(transfers, WepayField.PARTNER_TRADE_NO,request.getPartner_trade_no());
		
		put(transfers, WepayField.CHECK_NAME, request.getCheck_name());
		put(transfers, WepayField.RE_USER_NAME, request.getRe_user_name());
		put(transfers, WepayField.DESC, request.getDesc());
		put(transfers, WepayField.SPBILL_CREATE_IP,request.getSpbill_create_ip());
		
		put(transfers, WepayField.OPEN_ID, request.getOpenid());
		// FIXME sign 签名 后面传入

		// 业务可选参数
		putIfNotEmpty(transfers, WepayField.DEVICE_INFO,
				request.getDevice_info());

		return transfers;
	}
	
    /**
     * FIXME 
     * 后台直接请求微信后台（不需要返回客户端它再请求微信后台）。所以不需要 pay.weixin.core.Pays 的 buildAppPayResp()
     * 后台直接请求微信后台（不需要返回客户端它再请求微信后台） 。所以不需要 pay.weixin.core.Pays 的 buildJsPayResp()
     * 
     */
	
    
	/** 
	* @Title: buildTransfersRespons 
	* @Description:  (本方法的作用是：封装转账返回对象) 
	* @param 
	* @return TransfersResponse    返回类型 
	* @author ggj
	* @date 2016年6月28日 下午10:56:38 
	* @throws 
	*/
	public TransfersResponse buildTransfersRespons(Map<String, Object> data) {

		String return_code = data.get("return_code").toString();

		String mch_appid = data.get("mch_appid").toString();
		String mchid = data.get("mchid").toString();
		String nonce_str = data.get("nonce_str").toString();
		String result_code = data.get("result_code").toString();
		String partner_trade_no = data.get("partner_trade_no").toString();
		String payment_no = data.get("payment_no").toString();
		String payment_time = data.get("payment_time").toString();

		return new TransfersResponse(return_code, mch_appid, mchid, nonce_str,
				result_code, partner_trade_no, payment_no, payment_time);

	}

}
