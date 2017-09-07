package com.loveyou.webController.weixin.pay;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pay.weixin.model.pay.TransfersRequest;
import pay.weixin.util.RandomStrs;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.accountfunds.AccountFundsController;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.MakeOrderNumUtil;

/**
 * 微信企业企业付款 官网文档地址
 * https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2
 * 
 * @ClassName: TransfersController
 * @Description: (这个类的作用是：：微信转账给微信用户！！)
 * @author ggj
 * @param <withdraw>
 * @date 2016年6月25日 下午7:09:27
 * 
 */
public class TransfersController<withdraw> extends Bmob {

	private static Logger log = Logger.getLogger(TransfersController.class);
	private String re_user_name = "";

	/**
	 * @Title: testTransfers
	 * @Description:(本方法的作用是：测试转账)
	 * @author ggj
	 * @date 2016年6月30日 上午3:11:20
	 * @throws
	 */
	public void testTransfers() {
		String mch_appid = wepay.getAppId();
		String mchid = wepay.getMchId();
		String openid = "o_E-7wU0uPbfTPhwPP6ihpS3stI0";
		String amount = Integer.parseInt("100") + "";
		String spbill_create_ip = "127.0.0.1";
		// 获取客户端 设备号
		String device_info = "861462030186969";
		String nonce_str = RandomStrs.generate(16);
		String partner_trade_no = "111622";
		String check_name = "NO_CHECK";
		String re_user_name = "微信昵称";
		// 企业付款描述信息 desc
		String desc = "爱你约转账 ";

		TransfersRequest transfersRequest = new TransfersRequest(mch_appid,
				mchid, device_info, nonce_str, partner_trade_no, openid,
				check_name, re_user_name, amount, desc, spbill_create_ip);
		
		try{
			wepay.Transfers().doTransfers2(transfersRequest);
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	/**
	 * @Title: transfers
	 * @Description: (本方法的作用是： 对外提供转账)
	 * @author ggj
	 * @date 2016年6月30日 上午3:11:09
	 * @throws
	 */
	public void transfers() {
		JSONObject jsonObject = super.getparamJson();
		System.out.println(jsonObject);
		String mch_appid = wepay.getAppId();
		String mchid = wepay.getMchId();
		String ordernumber = new MakeOrderNumUtil().makeOrderNum();

		/*
		 * 前台直接通过ajax请求带参数用户openid;
		 */

		String openid = jsonObject.get("openid").toString();

		Integer member_id = Integer.parseInt(jsonObject.get("member_id").toString());

		Integer amountSecont = Integer.parseInt(jsonObject.get("amount").toString());
		
		String verify_out=jsonObject.get("verify").toString();
		
		String verify=getSessionAttr("verify");
		
		if(verify==null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"提现操作超时，不能提现\"}").toString());
			return;
		}else if(!verify.equals(verify_out)){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"提现前未验证支付密码，不能提现\"}").toString());
			return;
		}
		
		 //判断用户member_id是否存在，以及openid是否存在，并验证是否正确
		 
		String objectId = BmobAPI.getObjectIdById("loveyou_member",
				"member_id", member_id);
        //账户金额
		Integer available_predeposit = null;
		if (objectId != null) {

			String memberInfo = BmobAPI.findOne("loveyou_member", objectId);
			JSONObject member = JSONObject.parseObject(memberInfo);
			String oldOpenid = member.getString("openid");
			if (oldOpenid==null||!oldOpenid.equals(openid)) {

				renderJson(new JsonResult(JsonResult.STATE_FAIL,
						"{\"msg\":\"用户信息错误，不能提现\"}").toString());
				return;
			} else {
				available_predeposit = member
						.getInteger("available_predeposit");
			}
		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL,
					"{\"msg\":\"用户不存在\"}").toString());
			return;
		}

		 /*Map<String, Object> userInfomap=new UserController().getweixin_userInfo(); 
		  
		  Map<String, Object> userInfomap=UserController.WEIXIN_USERINFO;
		
		 log.error("getweixin_userInfo()-->userInfomap:::"+userInfomap);
		
		 if (null==userInfomap.get("openid")) {
		 openid = jsonObject.get("openid").toString();
		 re_user_name = jsonObject.get("nickname").toString();
		 }else {
		 openid=userInfomap.get("openid").toString();
		 re_user_name=userInfomap.get("nickname").toString();
		 }  */

		// ②判断用户的提现金额是否超出余额，以及是否超出限制额度， 判断amount是否超出 用户的约额（需要一个方法，超过就直接返回错误）
		AccountFundsController afc = new AccountFundsController();
		if (available_predeposit != null && available_predeposit >= 1
				&& available_predeposit >= amountSecont) {

			JSONObject jo = new JSONObject();
			jo.put("member_id", member_id);
			jo.put("pdc_amount", amountSecont);
			jo.put("order_sn", ordernumber);

			if (!afc.withdraw(jo)) {

				renderJson(new JsonResult(JsonResult.STATE_FAIL,
						"{\"msg\":\"提现申请失败\"}").toString());
				return;
			}

		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL,
					"{\"msg\":\"余额不足\"}").toString());
			return;
		}

		// 企业付款转账金额，单位为分
		String amount = Integer.parseInt(jsonObject.get("amount").toString())
				* 100 + "";
		String spbill_create_ip = super.getIpAddr(getRequest());
		// 获取客户端 设备号可null
		String device_info = jsonObject.get("device_info") + "";
		String nonce_str = RandomStrs.generate(16);
		// 从提现订单后返回的数据中获取！！
		// String partner_trade_no = getPara("partner_trade_no");
		String partner_trade_no = ordernumber;
		String check_name = "NO_CHECK";
		/* String re_user_name = getPara("nickname"); */
		// 企业付款描述信息 desc
		String desc = "爱你约转账 ";
		TransfersRequest transfersRequest = new TransfersRequest(mch_appid,
				mchid, device_info, nonce_str, partner_trade_no, openid,
				check_name, re_user_name, amount, desc, spbill_create_ip);
		
		// 转账成功后扣除用户系统的金额
		JSONObject object = (JSONObject) JSONObject.toJSON(wepay.Transfers()
				.doTransfers2(transfersRequest));

		// FIXME 转账成功后扣除用户系统的金额
		afc.confirmPayment2(ordernumber);

		renderJson(new JsonResult(JsonResult.STATE_SUCCESS,
				object.toJSONString()).toString());
				return;
	}
}
