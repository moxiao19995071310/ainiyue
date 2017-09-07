package com.loveyou.webController.weixin.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import pay.weixin.model.order.WePayOrder;
import pay.weixin.test.HttpUtil;
import pay.weixin.util.Maps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.accountfunds.AccountFundsController;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.order.GoodsOrderController;
import com.loveyou.webController.order.ServiceOrderController;

/**
 * @ClassName: NotifiesController
 * @Description:TODO (这个类的作用是：： 支付通知！！)
 * @author ggj
 * @date 2016年6月16日 上午6:02:18
 * 
 */
public class NotifiesController extends Bmob {
	private static Logger log = Logger.getLogger(NotifiesController.class);

	public String testpaid() {
		// 开始微信回调我啦
		log.error("===========H_Diao_1111======！！！");

		String notifyXml = getPostRequestBody(getRequest());
		if (notifyXml.isEmpty()) {
			return notifyNotOk("body为空");
		}
		Map<String, Object> notifyParams = Maps.toMap(notifyXml);
		if (verifySign(notifyParams)) {
		// FIXME TODO 修改订单的支付状态，可以手动调用订单查询结果，必须是订单已经支付成功 修改订单支付状态！！

			return notifyOk();
		} else {
			return notifyNotOk("签名失败");
		}

	}
	
	
	
	
	public void paid() {
		log.error("==========H_Diao_22222=========！！！");

		String notifyXml = getPostRequestBody(getRequest());
		if (notifyXml.isEmpty()) {

			renderJson("......body为空" + notifyNotOk("body为空"));
		}
		
		Map<String, Object> notifyParams = Maps.toMap(notifyXml);

		log.error("weixin111---collba111---->>" + notifyParams);
		if (verifySign(notifyParams)) {
		  /* FIXME TODO 修改订单的支付状态，可以手动调用订单查询结果，必须是订单已经支付成功 修改订单支付状态！！*/

			JSONObject  jsonobject= new JSONObject();
			jsonobject.put("out_trade_no", notifyParams.get("out_trade_no"));
			
			switch ( notifyParams.get("attach").toString()) {

			/* attach": "0是充值 ;1是服务订单; 2是商品订单 */
			case "0":
				/* log.assertLog(true, "TODO 修改充值订单支付状态+同时修改添加账户余额 将这个方法最好合并"); */
				log.error("weixin---0000000000----start"+jsonobject.getString("out_trade_no"));
				
				new AccountFundsController().confirmRecharge2(jsonobject);
				
				log.error("weixin---0000000000------end");
				redirect("http://www.tokeys.com/root2/show_all_pay.html");
				//renderJson( notifyOk());
				break;
			case "1":
				log.error("weixin---1111111111111111111111start----$$"+jsonobject.getString("out_trade_no"));
				
				new ServiceOrderController().updatePayState2(jsonobject);
				
				log.error("weixin---1111111111111111111111end----$$");
				redirect("http://www.tokeys.com/root2/show_all_pay.html");
				//renderJson( notifyOk());
				
				break;
			case "2":
				log.error("weixin---2222222222222222222222222start----##"+jsonobject.getString("out_trade_no"));
				
				new GoodsOrderController().updatePayState2(jsonobject);
				
				log.error("weixin---2222222222222222222222222end----##");
				redirect("http://www.tokeys.com/root2/show_all_pay.html");
				//renderJson( notifyOk());
				break;

			default:
				log.error("weixin---3333333333333333333333333333333----%%%%");
				redirect("http://www.tokeys.com/root2/show_all_pay.html");
				log.error("记录jilv1111" + notifyOk());

				break;
			}

		} else {
			log.error("weixin2222---collba222---->>" + notifyParams);

			renderJson(".............." + notifyNotOk("签名失败"));
		}

	}

	/**
	 * 校验签名
	 * 
	 * @param params
	 *            参数(包含sign)
	 * @return 校验成功返回true，反之false
	 */
	public Boolean verifySign(Map<String, ?> params) {
		return wepay.notifies().verifySign(params);
	}

	/**
	 * 通知成功
	 */
	public String notifyOk() {
		return wepay.notifies().ok();
	}

	/**
	 * 通知不成功
	 * 
	 * @param errMsg
	 *            错误消息
	 * 
	 */
	public String notifyNotOk(String errMsg) {
		return wepay.notifies().notOk(errMsg);
	}

	public static String getPostRequestBody(HttpServletRequest req) {
		if (req.getMethod().equals("POST")) {
			StringBuilder sb = new StringBuilder();
			try (BufferedReader br = req.getReader()) {
				char[] charBuffer = new char[128];
				int bytesRead;
				while ((bytesRead = br.read(charBuffer)) != -1) {
					sb.append(charBuffer, 0, bytesRead);
				}
			} catch (IOException e) {

			}
			return sb.toString();
		}
		return "";
	}

	/**
	 * @Description: (本方法的作用是：根据微信的订单号查询订单的支付状态！！)
	 */
	public void queryOrderByTransactionId(String transactionId) {
		// wepay.order().queryByTransactionId("1000530784201510111158030445");
		WePayOrder order = wepay.order().queryByTransactionId("transactionId");
		System.out.println(order);
	}
 
	/** 
	* @Title: queryOrderByOutTradeNo 
	* @Description:  (本方法的作用是：对外提供 ！根据自己的商户的订单号查询微信支付状态是否支付) 
	* @param 
	* @return void    返回类型 
	* @author ggj
	* @date 2016年7月8日 下午2:38:15  
	* @throws 
	*/
	public void queryOrderByOutTradeNo() {
		if (queryOrderByOutTradeNo2()) {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,
					"{\"msg\":\"支付成功\"}").toString());

		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL,
					"{\"msg\":\"支付失败或支付关闭\"}").toString());
		}

	}

	/**
	 * 
	 * @Title: queryOrderByOutTradeNo2
	 * @Description:  (本方法的作用是：对外！！！提供根据商户订单查询微信支付状态)
	 * @param
	 * @return boolean 返回类型
	 * @author ggj
	 * @date 2016年7月8日 下午2:35:17
	 * @throws
	 */
	public boolean queryOrderByOutTradeNo( String order_sn) {
	
//		if(order_sn==null&&"".equals(order_sn)){
//			return false;
//		}
		
		//姚永鹏修改
		if(order_sn==null||"".equals(order_sn))
			return false;
		
		boolean tradeState = false;
		WePayOrder order = new WePayOrder();
		try {
			// order = wepay.order().queryByOutTradeNo("TEST1122334455");
			order = wepay.order().queryByOutTradeNo(order_sn);
		} catch (Exception e) {
			// renderError(500, "");
			// renderJson("500");
			return false;
		}
		String trade_state = order.getTradeState().toString();
		switch (trade_state) {
		case "SUCCESS":
			tradeState = true;
			// renderJson("支付成功");
			
			break;
		case "NOTPAY":
			tradeState = false;
			// renderJson("未支付");
			break;
		case "PAYERROR":
			tradeState = false;
			// renderJson("支付失败(其他原因，如银行返回失败)");
			break;
		case "CLOSED":
			tradeState = false;
			// renderJson("已关闭");
			break;
			
		default:
			break;
		}
		
		return tradeState;
	}
	
	/**
	 *  本方法的作用是：提供根据商户订单查询微信支付状态
	 */
	private boolean queryOrderByOutTradeNo2() {
		/**
		 * 获取订单
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		String order_sn = BmobAPI.getStringValueFromJSONObject(params, "order_sn");
		if(order_sn==null&&"".equals(order_sn)){
			return false;
		}
		boolean tradeState = false;
		WePayOrder order = new WePayOrder();
		try {
			// order = wepay.order().queryByOutTradeNo("TEST1122334455");
			order = wepay.order().queryByOutTradeNo(order_sn);
		} catch (Exception e) {
			// renderError(500, "");
			// renderJson("500");
			return false;
		}
		String trade_state = order.getTradeState().toString();
		switch (trade_state) {
		case "SUCCESS":
			tradeState = true;
			// renderJson("支付成功");

			break;
		case "NOTPAY":
			tradeState = false;
			// renderJson("未支付");
			break;
		case "PAYERROR":
			tradeState = false;
			// renderJson("支付失败(其他原因，如银行返回失败)");
			break;
		case "CLOSED":
			tradeState = false;
			// renderJson("已关闭");
			break;

		default:
			break;
		}

		return tradeState;
	}
}
