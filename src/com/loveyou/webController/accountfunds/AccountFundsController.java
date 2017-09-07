package com.loveyou.webController.accountfunds;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.MakeOrderNumUtil;
import com.loveyou.webController.weixin.pay.NotifiesController;

/**
 * 资金账户管理控制器
 * 
 * @ClassName: AccountFundsController
 * 
 * @Description: TODO(这个类的作用是：资金账户管理)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月27日 上午11:35:32
 *
 * 
 */
public class AccountFundsController extends Bmob {
	/**
	 * 账户余额查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAccountBalance() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		/**
		 * 获取会员信息
		 */
		
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户不存在\"}").toString());
			return;
			
		}
		
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		/**
		 * 获取预存款可用金额
		 */
		Object available_predeposit = memberInfoMap.get("available_predeposit");
		/**
		 * 获取预存款冻结金额
		 */
		Object freeze_predeposit = memberInfoMap.get("freeze_predeposit");
		
		if(freeze_predeposit==null){
			freeze_predeposit=0;
		}
		/**
		 * 返回结果
		 */
		System.out.println(available_predeposit);
		if (available_predeposit != null) {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "{\"available_predeposit\":" + available_predeposit
					+ ",\"freeze_predeposit\":" + freeze_predeposit + "}").toString());
			return;
		}
		if (available_predeposit == null) {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "{\"available_predeposit\":0,\"freeze_predeposit\":" + freeze_predeposit + "}").toString());
		}
	}

	/**
	 * 修改账户可用余额
	 */
	@RequiresRoles("0")
	public void updateAccountBalance() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取修改后的余额(前台提供)
		 */
		Double available_predeposit = BmobAPI.getDoubleValueFromJSONObject(params, "available_predeposit");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("available_predeposit", available_predeposit);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 修改余额
		 */
		String result = update("loveyou_member", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 修改账户冻结余额
	 */
	@RequiresRoles("0")
	public void updateAccountFreezeBalance() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取修改后的余额(前台提供)
		 */
		Double freeze_predeposit = BmobAPI.getDoubleValueFromJSONObject(params, "freeze_predeposit");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("freeze_predeposit", freeze_predeposit);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 修改余额
		 */
		String result = update("loveyou_member", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 账户余额冻结
	 */
	@RequiresRoles("0")
	public void freezeAccountBalance() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		/**
		 * 获取会员信息
		 */
		String memberInfo = findOne("loveyou_member", objectId);
		
		System.out.println(memberInfo);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		/**
		 * 获取预存款可用金额
		 */
		Integer available_predeposit = (Integer) memberInfoMap.get("available_predeposit");
		
		/**
		 * 获取冻结金额
		 */
		Integer freeze_predeposit = (Integer) memberInfoMap.get("freeze_predeposit");
		if(freeze_predeposit==null) freeze_predeposit=0;
		
		if(available_predeposit==null) available_predeposit=0;
		System.out.println(freeze_predeposit);
		System.out.println(available_predeposit);
		freeze_predeposit += available_predeposit;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("available_predeposit", 0);// 可用余额变为0
		paramMap.put("freeze_predeposit", freeze_predeposit);// 冻结余额变为可用余额
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 修改余额
		 */
		String result = update("loveyou_member", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 会员充值
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void accountRecharge() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 生成充值编号(数据库类型为字符串！！)
		 */
		String pdr_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取会员名称
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		String member_name = (String) memberInfoMap.get("member_name");
		/**
		 * 获取充值金额(前台提供)
		 */
		Integer pdr_amount = BmobAPI.getIntegerValueFromJSONObject(params, "pdr_amount");
		/**
		 * 获取支付方式编号(前台提供)
		 */
		Integer payment_id = BmobAPI.getIntegerValueFromJSONObject(params, "payment_id");
		/**
		 * 获取支付代码名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_payment", "payment_id", payment_id);
		String paymentInfo = findOne("loveyou_payment", objectId);
		Map<String, Object> paymentInfoMap = BmobAPI.jsonStrToMap(paymentInfo);
		String payment_code = (String) paymentInfoMap.get("payment_code");
		/**
		 * 获取支付名称
		 */
		String payment_name = (String) paymentInfoMap.get("payment_name");
		/**
		 * 添加时间
		 */
		long pdr_add_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pdr_sn", pdr_sn);
		paramMap.put("pdr_member_id", member_id);
		paramMap.put("pdr_member_name", member_name);
		paramMap.put("pdr_amount", pdr_amount);
		paramMap.put("pdr_payment_code", payment_code);
		paramMap.put("pdr_payment_name", payment_name);
		paramMap.put("pdr_add_time", pdr_add_time);
		paramMap.put("pdr_payment_state", 0);// 支付状态默认为未支付
		paramMap.put("pdr_payment_time", 0);// 支付时间默认为0
		paramMap.put("pdr_admin", "");// 管理员名称，默认为空
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = insert("loveyou_pd_recharge", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 修改loveyou_member表
		 */
		// objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id",
		// member_id);
		/**
		 * 获取预存款可用金额
		 */
		// Integer available_predeposit = (Integer)
		// memberInfoMap.get("available_predeposit");
		/**
		 * 封装参数
		 */
		// available_predeposit += pdr_amount;
		// paramMap.clear();
		// paramMap.put("available_predeposit", available_predeposit);
		// paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 修改可用余额
		 */
		// result = update("loveyou_member", objectId, paramStr);
		/**
		 * 封装返回结果
		 */
		paramMap.clear();
		paramMap.put("pdr_sn", pdr_sn);// 充值编号
		paramMap.put("pdr_amount", pdr_amount);// 充值金额
		paramMap.put("body", "系统默认");
		result = BmobAPI.mapToJSONStr(paramMap);
		renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
	}

	/**
	 * 会员充值确认收到款
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void confirmRecharge() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取充值编号(前提提供)
		 */
		String pdr_sn = BmobAPI.getStringValueFromJSONObject(params, "pdr_sn");
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取充值金额(前台提供)
		 */
		Integer available_predeposit = BmobAPI.getIntegerValueFromJSONObject(params, "available_predeposit");
		/**
		 * 获取obejctId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_pd_recharge", "pdr_sn", pdr_sn);
		/**
		 * 支付时间
		 */
		long pdr_payment_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pdr_payment_state", 1);// 支付状态改为已支付
		paramMap.put("pdr_payment_time", pdr_payment_time);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = update("loveyou_pd_recharge", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 修改可用余额
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		// FIXME 
		result = update("loveyou_member", objectId, "{\"available_predeposit\":\"" + available_predeposit + "\"}");
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * FIXME  提现
	 * @author ggj  2016年7月8日16:24:06
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void confirmPayment() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前提提供)
		 */
		String order_sn = BmobAPI.getStringValueFromJSONObject(params, "order_sn");
		/**
		 * 获取预存款支付金额(前台提供)
		 */
		Integer pd_amount = BmobAPI.getIntegerValueFromJSONObject(params, "pd_amount");
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取obejctId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_sn", order_sn);
		/**
		 * 支付时间
		 */
		long payment_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", 20);// 支付状态改为已支付
		paramMap.put("payment_time", payment_time);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = update("loveyou_order", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 修改可用余额
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		Integer available_predeposit = (Integer) memberInfoMap.get("available_predeposit");
		String paramContent = "{\"available_predeposit\":" + (available_predeposit - pd_amount) + "}";
		System.out.println("paramContent:" + paramContent);
		result = update("loveyou_member", objectId, paramContent);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 会员充值明细列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getRechargeList() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = params.getInteger("page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = params.getInteger("pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pdr_member_id", member_id);
		paramMap.put("pdr_payment_state", 1);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_pd_recharge", paramStr, skip, pageSize,"-updatedAt,-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	
	/**
	 * 会员提现
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void withdraw() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 生成提现编号(数据库类型应该是字符串！！)
		 */
		String pdc_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 获取会员编号
		 */
		Integer pdc_member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取会员名称
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", pdc_member_id);
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		String pdc_member_name = (String) memberInfoMap.get("member_name");
		
		/**
		 * 获取提现金额(前台提供)
		 */
		Integer pdc_amount = BmobAPI.getIntegerValueFromJSONObject(params, "pdc_amount");
//		/**
//		 * 获取收款银行编号(前台提供)
//		 */
		Integer bankcard_id = BmobAPI.getIntegerValueFromJSONObject(params, "bankcard_id");
		
		/**
		 * 添加时间
		 */
		long pdc_add_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pdc_sn", pdc_sn);
		paramMap.put("pdc_member_id", pdc_member_id);
		paramMap.put("pdc_member_name", pdc_member_name);
		paramMap.put("pdc_amount", pdc_amount);
		paramMap.put("bankcard_id", bankcard_id);
		paramMap.put("pdc_add_time", pdc_add_time);
		paramMap.put("pdc_payment_state", 0);// 提现支付状态 0默认1支付完成
		paramMap.put("pdc_payment_admin", "");// 支付管理员,默认为空
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = insert("loveyou_pd_cash", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			// return;
		}else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 修改提现状态
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateWithdrawState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取提现记录编号(前台提供)
		 */
		String pdc_sn = BmobAPI.getStringValueFromJSONObject(params, "pdc_sn");
		/**
		 * 获取obejctId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_pd_cash", "pdc_sn", pdc_sn);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pdc_payment_state", 1);// 支付状态改为成功
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = update("loveyou_pd_cash", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 根据状态查询提现列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getWithdrwaListByState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取提现状态(前台提供)
		 */
		Integer pdc_payment_state = BmobAPI.getIntegerValueFromJSONObject(params, "pdc_payment_state");
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = params.getInteger("page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = params.getInteger("pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pdc_payment_state", pdc_payment_state);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_pd_cash", paramStr, skip, pageSize);
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 会员收人明细列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getIncomeListByMember() {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			/**
			 * 获取参数
			 */
			JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
			/**
			 * 获取会员编号(前台提供)
			 */
			Integer buyer_id = BmobAPI.getIntegerValueFromJSONObject(params, "buyer_id");
			/**
			 * 封装参数
			 */
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("buyer_id", buyer_id);
			paramMap.put("order_state", 40);// 订单状态是已完成状态
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 查询满足条件的所有订单
			 */
			String fworderInfo = findAll("shopnc_fworder", paramStr);
			if (fworderInfo.indexOf("objectId") != -1) {
				fworderInfo = fworderInfo.substring(fworderInfo.indexOf("["), fworderInfo.length() - 1);
				List<Map<String, Object>> orderInfoList = BmobAPI.jsonStrToList(fworderInfo);
				for (Map<String, Object> fworderInfoMap : orderInfoList) {
					/**
					 * 获取订单编号
					 */
					Integer order_id = (Integer) fworderInfoMap.get("order_id");
					String order_sn = (String) fworderInfoMap.get("order_sn");
					/**
					 * 获取该订单的结算金额
					 */
					String objectId = BmobAPI.getObjectIdById("loveyou_order_bill", "order_id", order_id);
					String billInfo = findOne("loveyou_order_bill", objectId);
					Map<String, Object> billInfoMap = BmobAPI.jsonStrToMap(billInfo);
					Integer ob_result_totals = (Integer) billInfoMap.get("ob_result_totals");
					/**
					 * 获取到账日期
					 */
					@SuppressWarnings("deprecation")
					String ob_pay_date = new Date((long) ((Double) billInfoMap.get("ob_pay_date") + 0L))
							.toLocaleString();
					/**
					 * 存入map集合
					 */
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("order_sn", order_sn);
					map.put("ob_result_totals", ob_result_totals);
					map.put("ob_pay_date", ob_pay_date);
					list.add(map);
				}
			}
			/**
			 * 返回结果
			 */
			System.out.println(new JsonResult(JsonResult.STATE_SUCCESS, list.toString()).toString());
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "\"" + list.toString() + "\"").toString());
		} catch (Exception e) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "\"" + list.toString() + "\"").toString());
			e.printStackTrace();
		}
	}

	/**
	 * 会员支出明细列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getPayOutByMember() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			/**
			 * 获取参数
			 */
			JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
			/**
			 * 获取会员编号(前台提供)
			 */
			Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
			/**
			 * 获取商品订单总价格
			 */
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("buyer_id", member_id);
			paramMap.put("order_state", 40);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 获取该用户所有的订单
			 */
			String orderInfo = findAll("loveyou_order", paramStr);
			if (orderInfo.indexOf("objectId") != -1) {
				/**
				 * 获取商品订单总价格并累加
				 */
				orderInfo = orderInfo.substring(orderInfo.indexOf("["), orderInfo.length() - 1);
				List<Map<String, Object>> orderInfoList = BmobAPI.jsonStrToList(orderInfo);
				for (Map<String, Object> orderInfoMap : orderInfoList) {
					/**
					 * 获取订单编号
					 */
					String order_sn = (String) orderInfoMap.get("order_sn");
					/**
					 * 获取订单完成时间
					 */
					@SuppressWarnings("deprecation")
					String finnshed_time = new Date((long) ((Double) orderInfoMap.get("finnshed_time") + 0L))
							.toLocaleString();
					/**
					 * 获取金额
					 */
					Integer order_amount = (Integer) orderInfoMap.get("order_amount");
					/**
					 * 存入map集合
					 */
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("order_sn", order_sn);
					map.put("finnshed_time", finnshed_time);
					map.put("order_amount", order_amount);
					list.add(map);
				}
			}
			/**
			 * 获取服务订单总价格
			 */
			String fworderInfo = findAll("shopnc_fworder", paramStr);
			if (fworderInfo.indexOf("objectId") != -1) {
				fworderInfo = fworderInfo.substring(fworderInfo.indexOf("["), fworderInfo.length() - 1);
				List<Map<String, Object>> fworderInfoList = BmobAPI.jsonStrToList(fworderInfo);
				for (Map<String, Object> fworderInfoMap : fworderInfoList) {
					/**
					 * 获取订单编号
					 */
					String order_sn = (String) fworderInfoMap.get("order_sn");
					/**
					 * 获取订单完成时间
					 */
					@SuppressWarnings("deprecation")
					String finnshed_time = new Date((long) ((Double) fworderInfoMap.get("finnshed_time") + 0L))
							.toLocaleString();
					/**
					 * 获取金额
					 */
					Integer order_amount = (Integer) fworderInfoMap.get("order_amount");
					/**
					 * 获取打赏金额
					 */
					Integer reward_amount = (Integer) fworderInfoMap.get("reward_amount");
					/**
					 * 存入map集合
					 */
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("order_sn", order_sn);
					map.put("finnshed_time", finnshed_time);
					map.put("order_amount", order_amount);
					map.put("reward_amount", reward_amount);
					list.add(map);
				}
			}
			/**
			 * 返回结果
			 */
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "\"" + list.toString() + "\"").toString());
		} catch (Exception e) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, list.toString()).toString());
			e.printStackTrace();
		}
	}

	/**
	 * 会员提现明细列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getWithdrawList() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer pdc_member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = params.getInteger("page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = params.getInteger("pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pdc_member_id", pdc_member_id);
		paramMap.put("pdc_payment_state", 1);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_pd_cash", paramStr, skip, pageSize);
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 会员充值确认收到款_发送短信
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void confirmPaymentByMessage() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取手机号码(前台提供)
		 */
		String mobilePhoneNumber = BmobAPI.getStringValueFromJSONObject(params, "mobilePhoneNumber");
		String mobilePhoneNumber2 = mobilePhoneNumber.substring(0, 3) + "******" + mobilePhoneNumber.substring(9);
		/**
		 * 获取充值的金额(前台提供)
		 */
		Double pdr_amount = BmobAPI.getDoubleValueFromJSONObject(params, "pdr_amount");
		/**
		 * 生成时间
		 */
		@SuppressWarnings("deprecation")
		String date = new Date().toLocaleString();
		/**
		 * 定义短信内容
		 */
		String content = "亲爱的爱你约会员(" + mobilePhoneNumber2 + ")，您好！你于" + date + "在线充值" + pdr_amount
				+ "元已成功到账！请查收！爱你约情感威客";
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("content", content);
		paramMap.put("mobilePhoneNumber", mobilePhoneNumber);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 准备发送短信
		 */
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
		try {
			/**
			 * 发送请求
			 */
			conn = connectionCommonSetting(conn, new URL(BmobAPI.SEND_MESSAGE_URL), BmobAPI.METHOD_POST);
			conn.setDoOutput(true);
			conn.connect();
			printWriter(conn, paramStr);
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * 返回结果
		 */
		if (result.indexOf("smsId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 会员提现确认收到款_发送短信
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void confirmWinthdrawByMessage() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取手机号码(前台提供)
		 */
		String mobilePhoneNumber = BmobAPI.getStringValueFromJSONObject(params, "mobilePhoneNumber");
		String mobilePhoneNumber2 = mobilePhoneNumber.substring(0, 3) + "******" + mobilePhoneNumber.substring(9);
		/**
		 * 获取提现的金额(前台提供)
		 */
		Double pdc_amount = BmobAPI.getDoubleValueFromJSONObject(params, "pdc_amount");
		/**
		 * 生成时间
		 */
		@SuppressWarnings("deprecation")
		String date = new Date().toLocaleString();
		/**
		 * 定义短信内容
		 */
		String content = "亲爱的爱你约会员(" + mobilePhoneNumber2 + ")，您好！你于" + date + "提现" + pdc_amount + "元已成功到账！请查收！爱你约情感威客";
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("mobilePhoneNumber", mobilePhoneNumber);
		paramMap.put("content", content);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 准备发送短信
		 */
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
		try {
			/**
			 * 发送请求
			 */
			conn = connectionCommonSetting(conn, new URL(BmobAPI.SEND_MESSAGE_URL), BmobAPI.METHOD_POST);
			conn.setDoOutput(true);
			conn.connect();
			printWriter(conn, paramStr);
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * 返回结果
		 */
		if (result.indexOf("smsId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	/**
	 * 审核所有的充值列表
	 */
	@RequiresRoles("0")
	public void getAllRechargeList() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = params.getInteger("page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = params.getInteger("pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 封装参数
		 */
		/**
		 * 发送请求
		 */
		String jsonStr="{\"pdr_payment_state\":{\"$ne\":1}}";
		
		String result = find("loveyou_pd_recharge", jsonStr, skip, pageSize,"-updatedAt,-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 会员充值确认收到款
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void confirmRecharge1() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取充值编号(前提提供)
		 */
		String pdr_sn = BmobAPI.getStringValueFromJSONObject(params, "pdr_sn");
		if(!new NotifiesController().queryOrderByOutTradeNo(pdr_sn)){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您还未充值成功\"}").toString());
			return;
		}
		
		/**
		 * 获取会员编号(前台提供)
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_pd_recharge", "pdr_sn", pdr_sn);
		String mn=objectId;
		//预存款充值表：loveyou_pd_recharge
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"数据错误\"}").toString());
			return;
		}
		
		String mresult=BmobAPI.findOne("loveyou_pd_recharge", objectId);
		if(mresult.indexOf("findOne")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, mresult).toString());
			return;
		}
		JSONObject ou=JSONObject.parseObject(mresult);
		Integer pdr_member_id=ou.getInteger("pdr_member_id");
		Integer pdr_state=ou.getInteger("pdr_payment_state");
		if(pdr_state==1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"已完成充值确认\"}").toString());
			return;
		}
		
		/**
		 * 支付时间
		 */
		long pdr_payment_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装参数
		 */
		
	
		// 修改member表中的预存款
		
		Integer available_predeposit=ou.getInteger("pdr_amount");
		Integer ap=available_predeposit;
		
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", pdr_member_id);
		
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"数据错误\"}").toString());
			return;
		}
		
		String result=BmobAPI.findOne("loveyou_member", objectId);
		if(result.indexOf("findOne")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}else{
			ou=JSONObject.parseObject(result);
			Integer oldAvailable_predeposit=null;
			try{
				oldAvailable_predeposit=ou.getInteger("available_predeposit");
			}catch(Exception e){
				System.out.println("oldAvailable_predeposit为空");
				
			}
			
			if(available_predeposit>0&&oldAvailable_predeposit!=null)
			 available_predeposit+=oldAvailable_predeposit;
			
			if(available_predeposit<=0){
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"充值金额小于最小充值金额，请更改\"}").toString());
				return;
			}
				
			
			result = update("loveyou_member", objectId, "{\"available_predeposit\":" + available_predeposit + "}");
		
		}
		
		
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("pdr_payment_state", 1);// 支付状态改为已支付
			paramMap.put("pdr_payment_time", pdr_payment_time);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 发送请求
			 */
			String resultd = update("loveyou_pd_recharge", mn, paramStr);
			/**
			 * 返回结果
			 */
			if (resultd.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, resultd).toString());
				return;
			}
			writerLog(pdr_member_id, "手动到账", JsonResult.LG_TYPE_RECHARGE,false, ap);
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	public void confirmRecharge2( JSONObject jsonObject) {
	 
		/**
		 * 获取充值编号(前提提供)
		 */
		String pdr_sn = BmobAPI.getStringValueFromJSONObject(jsonObject, "out_trade_no");
		
		if(!new NotifiesController().queryOrderByOutTradeNo(pdr_sn)){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您还未充值成功\"}").toString());
			return;
		}

		String objectId = BmobAPI.getObjectIdById("loveyou_pd_recharge", "pdr_sn", pdr_sn);
		//预存款充值表：loveyou_pd_recharge
		
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"充值编号错误\"}").toString());
			return;
		}
		String mn=objectId;
		String mresult=BmobAPI.findOne("loveyou_pd_recharge", objectId);
		if(mresult.indexOf("findOne")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, mresult).toString());
			return;
		}
		JSONObject ou=JSONObject.parseObject(mresult);
		Integer pdr_member_id=ou.getInteger("pdr_member_id");
		Integer pdr_state=ou.getInteger("pdr_payment_state");
		if(pdr_state==1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"已完成充值确认\"}").toString());
			return;
		}
		
		/**
		 * 支付时间
		 */
		long pdr_payment_time = System.currentTimeMillis() / 1000;
	
		// 修改member表中的预存款
		
		Integer available_predeposit=ou.getInteger("pdr_amount");
		Integer ap=available_predeposit;
		
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", pdr_member_id);
		
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"充值编号错误\"}").toString());
			return;
		}
		
		String result=BmobAPI.findOne("loveyou_member", objectId);
		if(result.indexOf("findOne")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}else{
			ou=JSONObject.parseObject(result);
			Integer oldAvailable_predeposit=null;
			try{
				oldAvailable_predeposit=ou.getInteger("available_predeposit");
			}catch(Exception e){
				System.out.println("oldAvailable_predeposit为空");
				
			}
			
			if(available_predeposit>0&&oldAvailable_predeposit!=null)
			 available_predeposit+=oldAvailable_predeposit;
			
			if(available_predeposit<=0){
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"充值金额小于最小充值金额，请更改\"}").toString());
				return;
			}
				
			
			result = update("loveyou_member", objectId, "{\"available_predeposit\":" + available_predeposit + "}");
		
		}
		
		
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("pdr_payment_state", 1);// 支付状态改为已支付
			paramMap.put("pdr_payment_time", pdr_payment_time);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 发送请求
			 */
			String resultd = update("loveyou_pd_recharge", mn, paramStr);
			
			writerLog(pdr_member_id, "系统", JsonResult.LG_TYPE_RECHARGE,false, ap);
			/**
			 * 返回结果
			 */
			if (resultd.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, resultd).toString());
				return;
			}
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}

	/**
	 * 获取所有的提现记录
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllWithdrwaList() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = params.getInteger("page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = params.getInteger("pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 封装参数
		 */
		String jsonStr="{\"pdc_payment_state\":{\"$ne\":1}}";
		String result = find("loveyou_pd_cash", jsonStr, skip, pageSize,"-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 插入提现数据
	 * @param jo
	 *  jo ={member_id,pdc_amount}
	 */
	public boolean withdraw(JSONObject jo) {
		/**
		 * 获取提现编号(数据库类型应该是字符串！！)
		 */
		String pdc_sn=jo.getString("order_sn");
		/**
		 * 获取会员编号
		 */
		Integer pdc_member_id = jo.getInteger("member_id");
		/**
		 * 获取会员名称
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", pdc_member_id);
		if(objectId==null){
			return false;
		}
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		String pdc_member_name = (String) memberInfoMap.get("member_name");
		
		/**
		 * 获取提现金额(前台提供)
		 */
		Integer pdc_amount = jo.getInteger("pdc_amount");
		
		/**
		 * 添加时间
		 */
		long pdc_add_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pdc_sn", pdc_sn);
		paramMap.put("pdc_member_id", pdc_member_id);
		paramMap.put("pdc_member_name", pdc_member_name);
		paramMap.put("pdc_amount", pdc_amount);
		paramMap.put("bankcard_id", 0);//默认微信，为0
		paramMap.put("pdc_add_time", pdc_add_time);
		paramMap.put("pdc_payment_state", 0);// 提现支付状态 0默认1支付完成
		paramMap.put("pdc_payment_admin", "系统转账");// 支付管理员,默认为空
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = insert("loveyou_pd_cash", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") != -1) {
			
			return true;
		}
		
		return  false;
	}

	/**
	 * 修改转账记录
	 * FIXME  提现
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void confirmPayment1() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取充值编号(前提提供)
		 */
		String pdc_sn = BmobAPI.getStringValueFromJSONObject(params, "pdc_sn");
		/**
		 * 获取obejctId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_pd_cash", "pdc_sn", pdc_sn);
		//预存款充值表：loveyou_pd_recharge
		String mn=objectId;

		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"提现编号错误\"}").toString());
			return;
		}
		String mresult=BmobAPI.findOne("loveyou_pd_cash", objectId);
		
		if(mresult.indexOf("findOne")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, mresult).toString());
			return;
		}
		JSONObject ou=JSONObject.parseObject(mresult);
		Integer pdc_member_id=ou.getInteger("pdc_member_id");
		Integer pdc_state=ou.getInteger("pdc_payment_state");
		if(pdc_state==1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"已完成提现确认\"}").toString());
			return;
		}
		
		/**
		 * 支付时间
		 */
		long pdc_payment_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装参数
		 */
		// 修改member表中的预存款
		
		Integer available_predeposit=ou.getInteger("pdc_amount");
		
		Integer ap=available_predeposit;
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", pdc_member_id);
		
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"提现编号错误\"}").toString());
			return;
		}
		String result=BmobAPI.findOne("loveyou_member", objectId);
		if(result.indexOf("findOne")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"确认提现错误,该用户不存在\"}").toString());
			return;
		}else{
			ou=JSONObject.parseObject(result);
			Integer oldAvailable_predeposit=ou.getInteger("available_predeposit");
			if(available_predeposit>0&&oldAvailable_predeposit!=null&&oldAvailable_predeposit>=available_predeposit)
			 available_predeposit=oldAvailable_predeposit-available_predeposit;
			else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"确认提现错误，余额不足\"}").toString());
				return;
			}
			result = update("loveyou_member", objectId, "{\"available_predeposit\":" + available_predeposit + "}");
		
		}
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("pdc_payment_state", 1);// 支付状态改为已支付
			paramMap.put("pdc_payment_time", pdc_payment_time);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 发送请求
			 */
			String resultd = update("loveyou_pd_cash", mn, paramStr);
			writerLog(pdc_member_id, "手动",JsonResult.LG_TYPE_CASH_PAY,true, ap);
			/**
			 * 返回结果
			 */
			if (resultd.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, resultd).toString());
				return;
			}
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 系统调用
	 */
	/**
	 * 修改转账记录
	 * FIXME  提现
	 */
	public void confirmPayment2(String pdc_sn) {
		/**
		 * 获取obejctId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_pd_cash", "pdc_sn", pdc_sn);
		//预存款充值表：loveyou_pd_recharge
		String mn=objectId;
		String mresult=BmobAPI.findOne("loveyou_pd_cash", objectId);
		if(mresult.indexOf("findOne")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, mresult).toString());
			return;
		}
		JSONObject ou=JSONObject.parseObject(mresult);
		Integer pdc_member_id=ou.getInteger("pdc_member_id");
		Integer pdc_state=ou.getInteger("pdc_payment_state");
		if(pdc_state==1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"已完成提现确认\"}").toString());
			return;
		}
		
		/**
		 * 支付时间
		 */
		long pdc_payment_time = System.currentTimeMillis() / 1000;
	
		// 修改member表中的预存款
		
		Integer available_predeposit=ou.getInteger("pdc_amount");
		
		Integer ap=available_predeposit;
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", pdc_member_id);
		
		String result=BmobAPI.findOne("loveyou_member", objectId);
		if(result.equals("(findOne)")){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"确认提现错误\"}").toString());
			return;
		}else{
			ou=JSONObject.parseObject(result);
			Integer oldAvailable_predeposit=ou.getInteger("available_predeposit");
			if(available_predeposit>0&&oldAvailable_predeposit!=null&&oldAvailable_predeposit>=available_predeposit)
			 available_predeposit=oldAvailable_predeposit-available_predeposit;
			else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"确认提现错误,余额不足\"}").toString());
				return;
			}
			result = update("loveyou_member", objectId, "{\"available_predeposit\":" + available_predeposit + "}");
		
		}
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("pdc_payment_state", 1);// 支付状态改为已支付
			paramMap.put("pdc_payment_time", pdc_payment_time);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 发送请求
			 */
			String resultd = update("loveyou_pd_cash", mn, paramStr);
			
			writerLog(pdc_member_id, "系统", JsonResult.LG_TYPE_CASH_PAY,true, ap);
			/**
			 * 返回结果
			 */
			if (resultd.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, resultd).toString());
				return;
			}

			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 资金日志表，即资金明细记录
	 * @param lg_type
	 * 		 recharge 充值，cash_pay 提现，refund 退款，order_pay 下单支付预存款， settlement 结算
	 */
//	order_pay下单支付预存款,order_freeze下单冻结预存款,
//	order_cancel取消订单解冻预存款,order_comb_pay下单支付被冻结的预存款,
//	recharge充值,cash_apply申请提现冻结预存款,cash_pay提现成功,
//	cash_del取消提现申请，解冻预存款,refund退款',
	//lg_type 选项
	//即 recharge 充值，cash_pay 提现，refund 退款，order_pay 下单支付预存款， settlement 结算
	
	public boolean writerLog(Integer member_id,String adminName,String lg_type,Boolean isout,Integer lg_av_amount){
		
		String ObjectId=BmobAPI.getObjectIdById("loveyou_member","member_id",member_id);
		
		if(ObjectId!=null){
			
			JSONObject log=new JSONObject();
			String memberInfo=findOne("loveyou_member", ObjectId);
			JSONObject jo=JSONObject.parseObject(memberInfo);
			String memberName=jo.getString("member_name");
			Integer available_predeposit=jo.getInteger("available_predeposit");
			log.put("lg_member_id", member_id);
			log.put("lg_member_name", memberName);
			log.put("lg_admin_name", adminName);
			log.put("lg_type", lg_type);
			log.put("lg_av_amount", lg_av_amount);
			if(available_predeposit==null)
				available_predeposit=0;
			log.put("lg_now_capital", available_predeposit);
			
			String lg_type_ZH="";
			//即 recharge 充值，cash_pay 提现，refund 退款，order_pay 下单支付预存款， settlement 结算
			switch (lg_type) {
			case JsonResult.LG_TYPE_RECHARGE:
				lg_type_ZH="充值";
				break;
			case JsonResult.LG_TYPE_CASH_PAY:
				lg_type_ZH="提现";
				break;
			case JsonResult.LG_TYPE_REFUND:
				lg_type_ZH="退款";
				break;
			case JsonResult.LG_TYPE_ORDER_PAY_GOODS:
				lg_type_ZH="购买商品";
				break;
			case JsonResult.LG_TYPE_ORDER_PAY_REQUIREMENT:
				lg_type_ZH="发布需求";
				break;
			case JsonResult.LG_TYPE_ORDER_PAY_SERVICE:
				lg_type_ZH="购买服务";
				break;
			case JsonResult.LG_TYPE_SETTLEMENT:
				lg_type_ZH="（服务或者商品）结算（服务扣5%的平台费）";
				break;
			default:
				lg_type_ZH="其他";
				break;
			
			}
			log.put("isout", isout);
			
			log.put("lg_desc", "您"+lg_type_ZH+"使用余额"+lg_av_amount.toString()+"  单位（元）");
			if(lg_type.equals(JsonResult.LG_TYPE_SETTLEMENT))
				log.put("lg_desc", "您"+lg_type_ZH+"获取"+lg_av_amount.toString()+"  单位（元）");
			
			Long operateDate=System.currentTimeMillis()/1000;
			
			log.put("lg_add_time", operateDate);
			
			String result=insert("loveyou_pd_log", log.toJSONString());
			
			if(result.indexOf("At")==-1){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
	

	/**
	 * 资金明细-支出
	 * @author 姚永鹏
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void accountParticularByOutlay(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer memberId=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		
		
		String ObjectId=BmobAPI.getObjectIdById("loveyou_pd_log", "lg_member_id", memberId);
		if(ObjectId!=null){
			String result=null;
			if(page!=null&&page>0&&pageSize!=null&&pageSize>0){
				Integer skip=(page-1)*pageSize;
				
				result=find("loveyou_pd_log", "{\"lg_member_id\":"+memberId+",\"isout\":true}", skip, pageSize,"-createdAt");
			}else{
			
				result=findAll("loveyou_pd_log", "{\"lg_member_id\":"+memberId+",\"isout\":true}");
			}
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			return;
			
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此用户还未进行交易\"}").toString());
	}
	
	/**
	 * 资金明细-收入
	 * @author 姚永鹏
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void accountParticularByIncome(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer memberId=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		
		
		String ObjectId=BmobAPI.getObjectIdById("loveyou_pd_log", "lg_member_id", memberId);
		if(ObjectId!=null){
			String result=null;
			if(page!=null&&page>0&&pageSize!=null&&pageSize>0){
				Integer skip=(page-1)*pageSize;
				
				result=find("loveyou_pd_log", "{\"lg_member_id\":"+memberId+",\"isout\":false}", skip, pageSize,"-createdAt");
			}else{
			
				result=findAll("loveyou_pd_log", "{\"lg_member_id\":"+memberId+",\"isout\":false}");
			}
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			return;
			
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此用户还未进行交易\"}").toString());
	}
	
	/**
	 * 资金明细
	 * @author 姚永鹏
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void accountParticular(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer memberId=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		
		
		String ObjectId=BmobAPI.getObjectIdById("loveyou_pd_log", "lg_member_id", memberId);
		if(ObjectId!=null){
			String result=null;
			if(page!=null&&page>0&&pageSize!=null&&pageSize>0){
				Integer skip=(page-1)*pageSize;
				
				result=find("loveyou_pd_log", "{\"lg_member_id\":"+memberId+"}", skip, pageSize,"-createdAt");
			}else{
			
				result=findAll("loveyou_pd_log", "{\"lg_member_id\":"+memberId+"}");
			}
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			return;
			
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此用户还未进行交易\"}").toString());
	}	

}
