package com.loveyou.webController.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.accountfunds.AccountFundsController;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.MakeOrderNumUtil;

/**
 * 服务订单管理控制器
 * 
 * @ClassName: ServiceOrderController
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月13日 下午8:32:47
 *
 * 
 */
public class ServiceOrderController extends Bmob {
	/**
	 * 创建订单(需求方下单) 目前没有！
	 */
	
	@Deprecated
	public void createServiceOrder() {
		/**
		 * 获取参数列表
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 生成订单编号
		 */
		String order_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 生成支付编号
		 */
		String pay_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 卖家店铺id(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
		/**
		 * 获取卖家店铺名称
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		String storeInfo = findOne("loveyou_store", objectId);
		Map<String, Object> storeInfoMap = BmobAPI.jsonStrToMap(storeInfo);
		String store_name = (String) storeInfoMap.get("store_name");
		/**
		 * 买家id(前台提供)
		 */
		Integer buyer_id = BmobAPI.getIntegerValueFromJSONObject(params, "buyer_id");

		/**
		 * 获取买家姓名
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", buyer_id);
		String buyerInfo = findOne("loveyou_member", objectId);
		Map<String, Object> buyerInfoMap = BmobAPI.jsonStrToMap(buyerInfo);
		String buyer_name = (String) buyerInfoMap.get("member_name");
		String buyer_email = (String) buyerInfoMap.get("member_email");

		/**
		 * 订单生成时间
		 */
		long add_time = System.currentTimeMillis() / 1000;
		/**
		 * 商品总价格(前台提供)
		 */
		Double goods_amount = BmobAPI.getDoubleValueFromJSONObject(params, "goods_amount");
		/**
		 * 订单总价格(前台提供)
		 */
		Double order_amount = BmobAPI.getDoubleValueFromJSONObject(params, "order_amount");
		/**
		 * 获取服务编号(前台提供)
		 */
		Integer fw_id = BmobAPI.getIntegerValueFromJSONObject(params, "fw_id");
		/**
		 * 获取服务名称
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_fw", "fw_id", fw_id);
		String fwInfo = findOne("shopnc_fw", objectId);
		Map<String, Object> fwInfoMap = BmobAPI.jsonStrToMap(fwInfo);
		String fw_name = (String) fwInfoMap.get("fw_name");
		/**
		 * 获取服务价格
		 */
		Integer fw_price = (Integer) fwInfoMap.get("fw_price");
		/**
		 * 获取服务图片
		 */
		String fw_image = (String) fwInfoMap.get("fw_image");
		String image_relativelypath = (String) fwInfoMap.get("image_relativelypath");

		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fw_name", fw_name);
		paramMap.put("fw_image", fw_image);
		paramMap.put("order_sn", order_sn);
		paramMap.put("pay_sn", pay_sn);
		paramMap.put("store_id", store_id);
		paramMap.put("store_name", store_name);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("buyer_name", buyer_name);
		paramMap.put("buyer_email", buyer_email);
		paramMap.put("add_time", add_time);
		paramMap.put("goods_amount", goods_amount);
		paramMap.put("order_amount", order_amount);
		paramMap.put("reward_amount", 0);// 打赏金额，默认为0
		paramMap.put("evaluation_state", 0);// 评价状态 0未评价，1已评价
		paramMap.put("order_state", JsonResult.ORDER_OBLIGATION);// 订单状态：0未付款，1(待确认，待同意)，10:待签到;20:服务中;30:待评价;40:待打赏，50已关闭;45申请退款
		paramMap.put("xq_or_fw", 0);// 需求方下单
		/**
		 * 发送请求
		 */
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		String result = insert("shopnc_fworder", jsonStr);
		if (result.indexOf("objectId") == -1) {
			JsonResult object = new JsonResult(JsonResult.STATE_FAIL, result);
			renderJson(object.toString());
			return;
		}
		/**
		 * 获取订单编号
		 */
		/**
		 * TODO 姚永鹏修改
		 */
		objectId = (String) BmobAPI.jsonStrToMap(result).get("objectId");
		String fwOrderInfo = findOne("shopnc_fworder", objectId);
		Map<String, Object> fwOrderInfoMap = BmobAPI.jsonStrToMap(fwOrderInfo);
		Integer order_id = (Integer) fwOrderInfoMap.get("order_id");
		/**
		 * 将已生成的订单支付编号插入到订单支付表里
		 */
		String paySn = (String) fwOrderInfoMap.get("pay_sn");

		Integer buyId = (Integer) fwOrderInfoMap.get("buyer_id");

		JSONObject jm = new JSONObject();
		jm.put("pay_sn", paySn);
		jm.put("buyer_id", buyId);
		jm.put("api_pay_state", 0);// 默认为0，未支付
		result = insert("loveyou_order_pay", jm.toJSONString());
		if (result.indexOf("objectId") == -1) {
			JsonResult object = new JsonResult(JsonResult.STATE_FAIL, result);
			renderJson(object.toString());
			return;
		}

		/**
		 * 获取服务实际成交价格(前台提供)
		 */
		Double fw_pay_price = BmobAPI.getDoubleValueFromJSONObject(params, "fw_pay_price");
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("fw_id", fw_id);
		paramMap.put("fw_name", fw_name);
		paramMap.put("fw_price", fw_price);
		paramMap.put("fw_num", 1);// 服务数量暂时默认为1
		paramMap.put("fw_image", fw_image);// 服务图片
		paramMap.put("image_relativelypath", image_relativelypath);// 服务图片
		paramMap.put("fw_pay_price", fw_pay_price);
		paramMap.put("store_id", store_id);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("fw_type", 1);// 1默认2团购商品3限时折扣商品4组合套装5赠品[可为空]
		paramMap.put("promotions_id", 0);// 促销活动ID（团购ID/限时折扣ID/优惠套装ID）与goods_type搭配使用[可为空]
		paramMap.put("commis_rate", 0);// 佣金比例
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		result = insert("shopnc_fworder_fw", paramStr);
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
/**
 * 创建订单并返回订单编号
 *	姚永鹏添加
 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void createServiceOrderReturnOrderId() {

		/**
		 * 获取参数列表
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 生成订单编号
		 */
		String order_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 生成支付编号
		 */
		String pay_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 卖家店铺id(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
		/**
		 * 获取卖家店铺名称
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		String storeInfo = findOne("loveyou_store", objectId);
		Map<String, Object> storeInfoMap = BmobAPI.jsonStrToMap(storeInfo);
		String store_name = (String) storeInfoMap.get("store_name");
		/**
		 * 买家id(前台提供)
		 */
		Integer buyer_id = BmobAPI.getIntegerValueFromJSONObject(params, "buyer_id");

		/**
		 * 获取买家姓名
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", buyer_id);
		String buyerInfo = findOne("loveyou_member", objectId);
		Map<String, Object> buyerInfoMap = BmobAPI.jsonStrToMap(buyerInfo);
		String buyer_name = (String) buyerInfoMap.get("member_name");
		String buyer_email = (String) buyerInfoMap.get("member_email");
		Integer buyer_store=(Integer) buyerInfoMap.get("store_id");
		
		if(buyer_store!=null&&buyer_store.equals(store_id)){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"亲，不能刷销量哦\"}").toString());
			return;
		}
		
		/**
		 * 订单生成时间
		 */
		long add_time = System.currentTimeMillis() / 1000;
		/**
		 * 订单总价格(前台提供)
		 */
		Double order_amount = BmobAPI.getDoubleValueFromJSONObject(params, "order_amount");
		/**
		 * 获取服务编号(前台提供)
		 */
		
		
		Integer fw_id = BmobAPI.getIntegerValueFromJSONObject(params, "fw_id");
		/**
		 * 获取服务名称
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_fw", "fw_id", fw_id);
		String fwInfo = findOne("shopnc_fw", objectId);
		Map<String, Object> fwInfoMap = BmobAPI.jsonStrToMap(fwInfo);
		String fw_name = (String) fwInfoMap.get("fw_name");
		
		String resultd=update("shopnc_fw", objectId, "{\"fw_storage\":0}");
		if(resultd.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"服务信息错误,不能创建订单\"}").toString());
			return;
		}
		
		/**
		 * 获取服务价格 yyp修改
		 */
		Integer fw_price =  (Integer) fwInfoMap.get("fw_price");
		
		Integer fb_member_id=(Integer) fwInfoMap.get("member_id");
		/**
		 * 添加用户头像
		 */
		String fb_member_avatar=null;
		String fbObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", fb_member_id);
		if(fbObject!=null){
			
			String fbResult=findOne("loveyou_member", fbObject);
		
			JSONObject fbJObject =JSONObject.parseObject(fbResult);
			
			fb_member_avatar=fbJObject.getString("member_avatar");
			
		}
		
		
		/**
		 * 获取服务图片
		 */
		String voice_url = (String) fwInfoMap.get("voice_url");
//		String image_relativelypath = (String) fwInfoMap.get("image_relativelypath");

		/**
		 * 获取服务种类fc_id
		 */
		Integer fc_id=(Integer) fwInfoMap.get("fc_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fw_id", fw_id);
		paramMap.put("fw_name", fw_name);
		paramMap.put("fc_id", fc_id);
		if(voice_url!=null&&!voice_url.equals(""))
			paramMap.put("voice_url", voice_url);
		if(fb_member_avatar!=null)
			paramMap.put("fb_member_avatar", fb_member_avatar);
		paramMap.put("order_sn", order_sn);
		paramMap.put("pay_sn", pay_sn);
		paramMap.put("store_id", store_id);
		paramMap.put("store_name", store_name);
		
		
		paramMap.put("fb_member_id", fb_member_id);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("buyer_name", buyer_name);
		if(buyer_email!=null&&!"".equals(buyer_email))
		paramMap.put("buyer_email", buyer_email);
		paramMap.put("add_time", add_time);
//		paramMap.put("refund_state",0);//退单状态，默认为0
		paramMap.put("order_amount", order_amount);
		paramMap.put("evaluation_state", 0);// 评价状态 0未评价，1已评价
		paramMap.put("order_state", JsonResult.ORDER_OBLIGATION);// 订单状态：0(待确认)10(默认):待签到;20:服务中;30:待评价;40:待打赏50已关闭;
		paramMap.put("xq_or_fw", 0);// 需求方下单
		/**
		 * 发送请求
		 */
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		String result = insert("shopnc_fworder", jsonStr);
		if (result.indexOf("objectId") == -1) {
			JsonResult object = new JsonResult(JsonResult.STATE_FAIL, result);
			renderJson(object.toString());
			return;
		}
		/**
		 * 获取订单编号
		 */
		/**
		 * TODO 姚永鹏修改
		 */
		objectId = (String) BmobAPI.jsonStrToMap(result).get("objectId");
		String fwOrderInfo = findOne("shopnc_fworder", objectId);
		Map<String, Object> fwOrderInfoMap = BmobAPI.jsonStrToMap(fwOrderInfo);
		Integer order_id = (Integer) fwOrderInfoMap.get("order_id");
		/**
		 * 将已生成的订单支付编号插入到订单支付表里
		 */
		String paySn = (String) fwOrderInfoMap.get("pay_sn");

		Integer buyId = (Integer) fwOrderInfoMap.get("buyer_id");

		JSONObject jm1 = new JSONObject();
		jm1.put("pay_sn", paySn);
		jm1.put("buyer_id", buyId);
		jm1.put("api_pay_state", 0);// 默认为0，未支付
		result = insert("loveyou_order_pay", jm1.toJSONString());
		if (result.indexOf("objectId") == -1) {
			JsonResult object = new JsonResult(JsonResult.STATE_FAIL, result);
			renderJson(object.toString());
			return;
		}

		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("fw_id", fw_id);
		paramMap.put("fw_name", fw_name);
		paramMap.put("fw_price", fw_price);
		paramMap.put("fw_num", 1);// 服务数量暂时默认为1
		paramMap.put("store_id", store_id);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("fw_type", 1);// 1默认2团购商品3限时折扣商品4组合套装5赠品[可为空]
		paramMap.put("promotions_id", 0);// 促销活动ID（团购ID/限时折扣ID/优惠套装ID）与goods_type搭配使用[可为空]
		paramMap.put("commis_rate", 0);// 佣金比例
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		result = insert("shopnc_fworder_fw", paramStr);

		/**
		 * outTradeNo （业务系统唯一订单号）body（商品名称） totalFee（总价）
		 *
		 * 姚永鹏修改
		 */
		JSONObject jo = JSONObject.parseObject(fwOrderInfo);
		String outTradeNo = jo.getString("order_sn");
		Double totalFee = jo.getDouble("order_amount");
		String body = fw_name==null||fw_name.equals("")?"系统默认":fw_name;;
		jo.clear();
		jo.put("outTradeNo", outTradeNo);
		jo.put("body", body);
		jo.put("totalFee", totalFee);
		result = jo.toJSONString();
		if (result.indexOf("body") < 0) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 创建订单(服务方接单  FIXME  要修改支付状态！)
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void createRequirementOrder() {
		/**
		 * 获取参数列表
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 生成订单编号
		 */
		String order_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 生成支付编号
		 */
		String pay_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 买家id(前台提供)
		 */
		Integer buyer_id = BmobAPI.getIntegerValueFromJSONObject(params, "buyer_id");
		/**
		 * 订单生成时间
		 */
		long add_time = System.currentTimeMillis() / 1000;
		/**
		 * 订单总价格(前台提供)
		 */
		Double order_amount = BmobAPI.getDoubleValueFromJSONObject(params, "order_amount");
		/**
		 * 获取需求编号(前台提供)
		 */
		Integer xq_id = BmobAPI.getIntegerValueFromJSONObject(params, "xq_id");
		/**
		 * 获取需求名称
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		String xqInfo = findOne("shopnc_xq", objectId);
		Map<String, Object> fwInfoMap = BmobAPI.jsonStrToMap(xqInfo);
		String xq_name = (String) fwInfoMap.get("xq_name");
		Integer member=(Integer) fwInfoMap.get("member_id");
		if(buyer_id.equals(member)){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"亲，不能刷销量哦\"}").toString());
			return;
		}
		
		String resultd=update("shopnc_xq", objectId, "{\"xq_storage\":0}");
		if(resultd.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该需求信息错误,不能创建订单\"}").toString());
			return;
		}
		/**
		 * 获取需求价格
		 */
		Integer xq_price = (Integer) fwInfoMap.get("xq_price");
		/**
		 * 获取需求图片
		 */
		String voice_url = (String) fwInfoMap.get("voice_url");
//		String image_relativelypath = (String) fwInfoMap.get("image_relativelypath");

		/**
		 * 获取需求类型 fc_id
		 */
		Integer fc_id=(Integer) fwInfoMap.get("fc_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		/**
		 * 姚永鹏添加
		 */
		paramMap.put("fc_id", fc_id);
		paramMap.put("fw_name", xq_name);
		if(voice_url!=null&&!voice_url.equals(""))
		paramMap.put("voice_url", voice_url);
		paramMap.put("order_sn", order_sn);
		paramMap.put("pay_sn", pay_sn);
//		paramMap.put("store_id", store_id);
//		paramMap.put("store_name", store_name);

		paramMap.put("add_time", add_time);
		//yyp添加
		
		//FIXME 重要标记， 此时要切换卖家和买家的位置。
		paramMap.put("buyer_id", member);
		paramMap.put("xq_id", xq_id);
		paramMap.put("fb_member_id", buyer_id);
		
		/**
		 * 添加用户头像
		 */
		String fb_member_avatar=null;
		String fbObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", buyer_id);
		if(fbObject!=null){
			
			String fbResult=findOne("loveyou_member", fbObject);
		
			JSONObject fbJObject =JSONObject.parseObject(fbResult);
			
			fb_member_avatar=fbJObject.getString("member_avatar");
			
			String member_name=fbJObject.getString("member_name");
			
			paramMap.put("buyer_name", member_name);
		}
		if(fb_member_avatar!=null)
			paramMap.put("fb_member_avatar", fb_member_avatar);
		// FIXME 此时已切换
		
		paramMap.put("order_amount", order_amount);
//		paramMap.put("refund_state",0);//退单状态，默认为0
		paramMap.put("evaluation_state", 0);// 评价状态 0未评价，1已评价
		paramMap.put("order_state", JsonResult.ORDER_OBLIGATION);// 订单状态：0(代付款)1，带签到，10:待签到;20:服务中;30:待评价;40:待打赏50已关闭;
		paramMap.put("xq_or_fw", 1);// 服务方接单
		
		/**
		 * 发送请求
		 */
		
		
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		
		System.out.println(jsonStr);
		String result = insert("shopnc_fworder", jsonStr);
		if (result.indexOf("objectId") == -1) {
			JsonResult object = new JsonResult(JsonResult.STATE_FAIL, result);
			renderJson(object.toString());
			return;
		}
		/**
		 * 获取订单编号
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_sn", order_sn);
		String fwOrderInfo = findOne("shopnc_fworder", objectId);
		Map<String, Object> fwOrderInfoMap = BmobAPI.jsonStrToMap(fwOrderInfo);
		Integer order_id = (Integer) fwOrderInfoMap.get("order_id");
		
		
		
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("xq_id", xq_id);
		paramMap.put("fb_member_id", member);
		paramMap.put("fw_name", xq_name);
		paramMap.put("fw_price", xq_price);
		paramMap.put("fw_num", 1);// 服务数量暂时默认为1
		paramMap.put("fw_pay_price", xq_price);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("fw_type", 1);// 1默认2团购商品3限时折扣商品4组合套装5赠品[可为空]
		paramMap.put("promotions_id", 0);// 促销活动ID（团购ID/限时折扣ID/优惠套装ID）与goods_type搭配使用[可为空]
		paramMap.put("commis_rate", 0);// 佣金比例
		paramMap.put("order_state", JsonResult.ORDER_PAID);//FIXME 支付 
		
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		System.out.println(paramStr);
		result = insert("shopnc_fworder_fw", paramStr);

		/**
		 * outTradeNo （业务系统唯一订单号）body（商品名称） totalFee（总价）
		 *
		 * 姚永鹏修改
		 */
		JSONObject jo = JSONObject.parseObject(fwOrderInfo);
		String outTradeNo = jo.getString("order_sn");
		Double totalFee = jo.getDouble("order_amount");
		String body = xq_name==null||xq_name.equals("")?"系统默认":xq_name;
		jo.clear();
		jo.put("outTradeNo", outTradeNo);
		jo.put("body", body);
		jo.put("totalFee", totalFee);
		result = jo.toJSONString();
		if (result.indexOf("body") < 0) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}

	}

	/**
	 * 订单支付确认收到款：后台人员用的接口(修改支付状态)
	 */
	@Deprecated
	public void confirmReceiveMoney() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取支付单号(前台提供)
		 */
		String pay_sn = BmobAPI.getStringValueFromJSONObject(params, "pay_sn");
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer buyer_id = BmobAPI.getIntegerValueFromJSONObject(params, "buyer_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pay_sn", pay_sn);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("api_pay_state", 1);// 支付状态改成已支付
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		System.out.println(paramStr);
		String result = insert("loveyou_order_pay", paramStr);
		System.out.println(result);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 订单签到(服务方签到)
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void checkInOrder() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 封装参数 订单状态：0(代付款)1，带签到，10:待签到;20:服务中;30:待评价;40:待打赏50已关闭;
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", 20);// 订单状态由待签到改为服务中
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取ObjectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_fworder", objectId, paramStr);
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
	 * 订单签到(需求方签到)
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void confirmOrder() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", 10);// 订单状态由待确认改为待签到
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取ObjectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
		System.out.println(objectId);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_fworder", objectId, paramStr);
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
	 * 取消订单 TODO 在买家未付款的情况下可以取消订单
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void cancelOrder() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", JsonResult.ORDER_CLOSE);// 订单状态改为已关闭
		paramMap.put("member_id", member_id);// 订单状态改为已关闭
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取ObjectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_fworder", objectId, paramStr);
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
	 * 订单所有服务订单
	 */
	@RequiresRoles("0")
	public void getAllServiceOrder() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 提交请求
		 */
		// find(tableName, where, skip, limit, order)
		String result = find("shopnc_fworder", "", skip, pageSize, "-add_time");
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 根据订单状态查询所有服务订单
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllServiceOrderByOrderState() {
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
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取订单状态(前台提供)
		 */
		Integer order_state = BmobAPI.getIntegerValueFromJSONObject(params, "order_state");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", order_state);
		paramMap.put("fb_member_id", member_id);
//		paramMap.put("xq_or_fw", 0);// 需求方下单
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 提交请求
		 */
		String result = find("shopnc_fworder", paramStr, skip, pageSize, "-createdAt");
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
	 * TODO 根据订单生成时间范围查询待实现<br/>
	 * TODO 根据手机号查询，手机号这个字段没有，待实现<br/>
	 * TODO 根据金额范围查询待实现<br/>
	 * 
	 * 订单多条件高级查询(根据订单生成时间、用户名、手机号、金额查询)
	 */
	@Deprecated
	public void getAllOrderByCondition() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取添加时间(前台提供)
		 */
		Long add_time = BmobAPI.getLongValueFromJSONObject(params, "add_time");
		/**
		 * 获取买家名称(前台提供)
		 */
		String buyer_name = BmobAPI.getStringValueFromJSONObject(params, "buyer_name");
		/**
		 * 获取手机号(前台提供)
		 */
		Integer phonenumber = BmobAPI.getIntegerValueFromJSONObject(params, "phonenumber");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("buyer_name", buyer_name);
		paramMap.put("phonenumber", phonenumber);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_order", paramStr, skip, pageSize);
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
	 * 订单奖赏
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void rewardOrder() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 获取打赏金额(前台提供)
		 */
		Double reward_amount = BmobAPI.getDoubleValueFromJSONObject(params, "reward_amount");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", JsonResult.ORDER_AWARDS);// 订单状态由待奖赏改为已关闭
		paramMap.put("reward_amount", reward_amount);// 订单状态由待奖赏改为已关闭
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取ObjectId
		 */
		String objectId = BmobAPI.getObjectIdById("fw_order ", "order_id", order_id);
		/**
		 * 发送请求
		 */
		String result = update("fw_order ", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 订单完成(需求方选择不奖赏就直接关闭订单),待评价
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void complishOrder() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", JsonResult.ORDER_FINISH);// 订单状态由待奖赏改为已关闭
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取ObjectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_fworder", objectId, paramStr);
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
	 * 服务订单评论
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void reviewOrder() {
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(jo, "order_id");
		/**
		 * 获取订单表自增编号
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
		String orderInfo = findOne("shopnc_fworder", objectId);
		
		update("shopnc_fworder", objectId, "{\"order_state\":"+JsonResult.ORDER_OVER+"}");
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		/**
		 * 获取订单编号
		 */
		String order_sn = (String) orderInfoMap.get("order_sn");
		/**
		 * 姚永鹏加
		 */
		Integer storeId = (Integer) orderInfoMap.get("store_id");
//		String storeName = (String) orderInfoMap.get("store_name");

		/**
		 * 获取订单服务(或需求)表编号
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_fworder_fw", "order_id", order_id);
		String orderGoodsInfo = findOne("shopnc_fworder_fw", objectId);
		Map<String, Object> orderGoodsInfoMap = BmobAPI.jsonStrToMap(orderGoodsInfo);
		Integer geval_ordergoodsid = (Integer) orderGoodsInfoMap.get("rec_id");

		/**
		 * 获取商品评分(前台提供，1-5分)
		 */
		Integer geval_scores = BmobAPI.getIntegerValueFromJSONObject(jo, "geval_scores");
		/**
		 * 获取评价内容(前台提供)
		 */
		String geval_content = BmobAPI.getStringValueFromJSONObject(jo, "geval_content");
		/**
		 * 是否是匿名评论(前台提供)
		 */
		Integer geval_isanonymous = BmobAPI.getIntegerValueFromJSONObject(jo, "geval_isanonymous");//1为匿名
		/**
		 * 评价时间
		 */
		long geval_addtime = System.currentTimeMillis() / 1000;
		/**
		 * 获取评价人编号(前台提供、会员编号、当前登录人)
		 */
		Integer geval_frommemberid = BmobAPI.getIntegerValueFromJSONObject(jo, "geval_frommemberid");
		/**
		 * 获取评价人名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", geval_frommemberid);
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		String geval_frommembername = (String) memberInfoMap.get("member_name");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("geval_orderid", order_id);
		paramMap.put("geval_orderno", order_sn);
		paramMap.put("geval_ordergoodsid", geval_ordergoodsid);
//		paramMap.put("geval_goodsid", fw_id);
//		paramMap.put("geval_goodsname", fw_name);
		paramMap.put("geval_storeid", storeId);
//		paramMap.put("geval_storename", storeName);
//		paramMap.put("geval_goodsprice", fw_price);
		paramMap.put("geval_scores", geval_scores);
		paramMap.put("geval_content", geval_content);
		paramMap.put("geval_isanonymous", geval_isanonymous);
		paramMap.put("geval_addtime", geval_addtime);
		paramMap.put("geval_frommemberid", geval_frommemberid);
		paramMap.put("geval_frommembername", geval_frommembername);
		paramMap.put("geval_state", 0);// 评价信息的状态 0为正常 1为禁止显示，默认正常
		paramMap.put("geval_remark", "");// 管理员对评价的处理备注,默认为空
		paramMap.put("geval_explain", "");// 解释内容,默认为空
		
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向评价表发送请求
		 */
		System.out.println(paramStr); 
		String result = insert("evaluate_goods", paramStr);
		System.out.println(result);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 获取objectId
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap = new HashMap<String, Object>();
		paramMap.put("evaluation_state", 1);// 评价状态改为已评价
		paramMap.put("order_state", JsonResult.ORDER_OVER);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向订单表发送请求
		 */
		result = update("shopnc_fworder", objectId, paramStr);
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
	 * 订单投诉
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void complainOrder() {
		/**
		 * 上传晒单图片
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();

		String complainId = new MakeOrderNumUtil().makeOrderNum();
		BigDecimal complain_id = new BigDecimal(complainId);
		/**
		 * 获取服务编号(前台提供)
		 */
		Integer fw_id = getParaToInt("fw_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder_fw", "fw_id", fw_id);
		/**
		 * 获取服务名称
		 */
		String fwOrderInfo = findOne("shopnc_fworder_fw", objectId);
		Map<String, Object> fwOrderInfoMap = BmobAPI.jsonStrToMap(fwOrderInfo);
		String fw_name = (String) fwOrderInfoMap.get("fw_name");
		/**
		 * 获取商品价格
		 */
		Integer fw_price = (Integer) fwOrderInfoMap.get("fw_price");
		/**
		 * 获取订单id（前台提供）
		 */
		Integer order_id = getParaToInt("order_id");
		/**
		 * 获取商品数量
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order_id", order_id);
		map.put("fw_id", fw_id);
		String param = BmobAPI.mapToJSONStr(map);
		String orderFwInfo = findAll("shopnc_fworder_fw", param);
		orderFwInfo = orderFwInfo.substring((orderFwInfo.indexOf("[") + 1), orderFwInfo.length() - 2);
		Map<String, Object> orderFwInfoMap = BmobAPI.jsonStrToMap(orderFwInfo);
		Integer fw_num = (Integer) orderFwInfoMap.get("fw_num");
		/**
		 * 获取被投诉商品的问题描述(前台提供)
		 */
		String complain_message = getPara("complain_message");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("complain_id", complain_id);
		paramMap.put("goods_id", fw_id);
		paramMap.put("goods_name", fw_name);
		paramMap.put("goods_price", fw_price);
		paramMap.put("goods_num", 1);
		paramMap.put("fw_num", fw_num);
	
		if(!maps.isEmpty()){
			/**
			 * 获取上传文件的文件名
			 */
			String fileName = BmobAPI.getSingleUploadFileName(maps);
			/**
			 * 获取上传文件的url
			 */
			String url = BmobAPI.getSingleUploadFileUrl(maps);
			/**
			 * 生成投诉编号
			 */
			paramMap.put("goods_image", url);
			paramMap.put("image_relativelypath", fileName);
		}
		paramMap.put("complain_message", complain_message);
		paramMap.put("order_goods_id", order_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = insert("complain_goods", paramStr);
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
	 * 申请退单
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void applyCancelOrder() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单ID(前端提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
		if(objectId==null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该订单不存在\"}").toString());
			return;
		}
		
		/**
		 * 获取订单编号
		 */
		String orderInfo = findOne("shopnc_fworder", objectId);

		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		
		
		/**
		 *  FIXME 获取订单状态如果是未付款以及已完成或者已取消的订单都不能申请退单
		 */
		Integer order_state =(Integer) orderInfoMap.get("order_state");
		
		if(order_state<1||order_state>=30){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"订单未付款或订单已完成不能申请退单\"}").toString());
			return;
		}
		
		/**
		 * 获取订单编号
		 */
		String order_sn = (String) orderInfoMap.get("order_sn");
		/**
		 * 生成申请编号
		 */
		String refund_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 从结果中获取店铺id
		 */
		Integer store_id = (Integer) orderInfoMap.get("store_id");
		/**
		 * 从结果中获取店铺名称
		 */
		String store_name = (String) orderInfoMap.get("store_name");
		/**
		 * 从结果中获取买家ID
		 */
		Integer buyer_id = (Integer) orderInfoMap.get("buyer_id");
		/**
		 * 从结果中获取买家ID
		 */
		String buyer_name = (String) orderInfoMap.get("buyer_name");
		/**
		 * 订单价格
		 */
		Integer order_amount =(Integer) orderInfoMap.get("order_amount");
		/**
		 * FIXME 获取服务方Id（如果是服务方发起退单直接待管理员确认）
		 */
		Integer fb_member_id=(Integer) orderInfoMap.get("fb_member_id");
		
		/**
		 * 如果是需求订单即服务方接单的订单没有店铺信息此时要根据服务用户member_id查询用户店铺信息
		 */
		if(store_id==null||store_id.equals("")){
			
			String fbObjectId=BmobAPI.getObjectIdById("loveyou_member", "member_id", fb_member_id);
			
			if(fbObjectId!=null){
				
				String ru=findOne("loveyou_member", fbObjectId);
				
				JSONObject fbJObject =JSONObject.parseObject(ru);
				
				store_id=fbJObject.getInteger("store_id");
				
				String storeId=BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
				
				if(storeId!=null){
					
					ru=findOne("loveyou_store", storeId);
					
					fbJObject=JSONObject.parseObject(ru);
					
					store_name=fbJObject.getString("store_name");
				}
			}
			
		}
		
		/**
		 * 获取退单人(前台提供)
		 */
		Integer apply_member_id = BmobAPI.getIntegerValueFromJSONObject(params, "apply_member_id");

		Map<String, Object> paramMap = new HashMap<String, Object>();

		/**
		 * 获取退款金额(前台提供)
		 */
		Integer refund_amount = BmobAPI.getIntegerValueFromJSONObject(params, "refund_amount");
		
		if(refund_amount>order_amount||refund_amount<1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"退款金额不能大于订单金额,或者退款金额不大于1\"}").toString());
			return;
		}
		
		/**
		 * 创建添加时间
		 */
		long add_time = System.currentTimeMillis();
		/**
		 * 获取退款原因
		 */
		String buyer_message = params.getString("buyer_message");
		/**
		 * 创建参数列表
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("order_sn", order_sn);
		paramMap.put("refund_sn", refund_sn);
		if(store_id!=null&&store_id>0){
			paramMap.put("store_id", store_id);
			paramMap.put("store_name", store_name);
		}
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("buyer_name", buyer_name);
		
		paramMap.put("order_amount", order_amount);

		paramMap.put("refund_amount", refund_amount);
		paramMap.put("add_time", add_time);
		paramMap.put("buyer_message", buyer_message);
		paramMap.put("apply_member_id", apply_member_id);// 退单申请人
		
		paramMap.put("fb_member_id", fb_member_id);
		
		Integer refund_state=0;
		if(fb_member_id.equals(apply_member_id)){
			paramMap.put("seller_state", 1);// 卖家处理状态为同意
		}
		//退单状态
		paramMap.put("refund_state", refund_state);// 退单状态 0 待审核 1服务方确认退单 3服务方确认不退单 2 管理员确认退单 5管理员确认不退单 4已完成退单
		
		
			
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = insert("refund_return", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			
		} else {
			result=update("shopnc_fworder", objectId,"{\"order_state\":55,\"refund_state\":"+refund_state+"}");
			
			if(result.indexOf("At")!=-1){
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
				return;
			}
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		
	}

	/**
	 * 验证支付金额
	 * 
	 * @author 姚永鹏添加
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void verifyAmount() {

		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		String order_sn = jo.getString("order_sn");
		Integer order_amount = jo.getInteger("order_amount");

		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_sn", order_sn);

		String oneRow = findOne("shopnc_fworder", objectId);

		JSONObject je = JSONObject.parseObject(oneRow);

		Integer shop_amount = je.getInteger("order_amount");

		String result = "";
		if (shop_amount.equals(order_amount)) {
			result = "{\"msg\":\"ok\"}";
		}
		if (result.indexOf("ok") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}

	}

	/**
	 * @author FIXME  姚永鹏 修改订单支付状态
	 * 
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updatePayState() {
		// order_state:number=1为已支付
		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		String order_sn = jo.getString("order_sn");

		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_sn", order_sn);

		if(objectId==null){
			jo.clear();
			jo.put("msg", "order_sn is not exist");
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jo.toJSONString()).toString());
			return;
		}

		/**
		 * TODO 
		 * <code>false</code>
		 * 需要通过微信支付接口查询用户订单存在再修改
		 * FIXME
		 * FIXME
		 */
		
		
		String oneRow = findOne("shopnc_fworder", objectId);
		
		JSONObject jM = JSONObject.parseObject(oneRow);

		if(jM.getInteger("order_state")>1){
			jo.put("msg", "order_sn are paid and success");
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jo.toJSONString()).toString());
			return;
		}
		String pay_sn = jM.getString("pay_sn");
		
		Integer buyer_id=jM.getInteger("buyer_id");
		
		Integer order_amount=jM.getInteger("order_amount"); 
		
		String joStr ="{\"order_state\":1}";

		BmobAPI.update("shopnc_fworder", objectId, joStr);

		objectId = BmobAPI.getObjectIdById("loveyou_order_pay", "pay_sn", pay_sn);

		jo.clear();

		jo.put("api_pay_state", 1);// 改为已支付

		String result = BmobAPI.update("loveyou_order_pay", objectId, jo.toJSONString());

		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			new AccountFundsController().writerLog(buyer_id,"手动到账",JsonResult.LG_TYPE_ORDER_PAY_SERVICE,true,order_amount);
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}

	}
	/** 
	* @Title: updatePayState2 
	* @Description:  (本方法的作用是：修改支付订单) 
	* @param 
	* @return void    返回类型 
	* @author ggj
	* @date 2016年7月9日 下午4:14:01  
	* 
	* @throws 
	*/
	public void updatePayState2(JSONObject jsonObject) {
		/* order_state:number=1为已支付*/
		
		//JSONObject jsonObject = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String order_sn = jsonObject.getString("out_trade_no");
		
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_sn", order_sn);
		
		if(objectId==null){
			jsonObject.put("msg", "order_sn is not exist");
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jsonObject.toJSONString()).toString());
			return;
		}
		String oneRow = findOne("shopnc_fworder", objectId);
		
		JSONObject jM = JSONObject.parseObject(oneRow);
		
		if(jM.getInteger("order_state")>1){
			jsonObject.put("msg", "order_sn are paid and success");
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jsonObject.toJSONString()).toString());
			return;
		}
		String pay_sn = jM.getString("pay_sn");
		
		Integer buyer_id=jM.getInteger("buyer_id");
		
		Integer order_amount=jM.getInteger("order_amount");
		// FIXME  
		String joStr ="{\"order_state\":1}";
		
		BmobAPI.update("shopnc_fworder", objectId, joStr);
		
		objectId = BmobAPI.getObjectIdById("loveyou_order_pay", "pay_sn", pay_sn);
		
		jsonObject.clear();
		//修改的订单支付表（订单表里面的订单编号 应该在这个表里存在）
		jsonObject.put("api_pay_state", 1);// 改为已支付
		
		String result = BmobAPI.update("loveyou_order_pay", objectId, jsonObject.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			new AccountFundsController().writerLog(buyer_id,"手动到账",JsonResult.LG_TYPE_ORDER_PAY_SERVICE,true,order_amount);
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}

	/**
	 * 判断是否此订单是否已经支付
	 * 
	 * @author 姚永鹏
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void judgePayment() {

		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		String order_sn = jo.getString("order_sn");

		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_sn", order_sn);

		String jsonStr = findOne("shopnc_fworder", objectId);

		JSONObject jm = JSONObject.parseObject(jsonStr);

		Integer order_state = jm.getInteger("order_state");
		// 10为未付款，默认未付款
		String result = "{\"msg\":\"paid\"}";
		if (order_state.equals(10)) {
			result = "{\"msg\":\"no\"}";
		}
		if (result.indexOf("paid") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 买家确认订单
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void agreeArder() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 封装参数 订单状态：0(代付款)1，带签到，10:待签到;20:服务中;30:待评价;40:待打赏50已关闭;
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", 1);// 订单状态由待签到改为带签到
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取ObjectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_fworder", objectId, paramStr);
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
	 * 根据订单状态查询所有需求订单
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllRequirementOrderByOrderState() {
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
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取订单状态(前台提供)
		 */
		Integer order_state = BmobAPI.getIntegerValueFromJSONObject(params, "order_state");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", order_state);
		paramMap.put("buyer_id", member_id);
		paramMap.put("xq_or_fw", 1);// 需求方下单
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 提交请求
		 */
		String result = find("shopnc_fworder", paramStr, skip, pageSize, "-createdAt");
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
	 * 发布需求的用户查看自己的需求订单状态 /发布人的需求或者服务。
	 * 
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllRequirementOnlyOrderByOrderState() {
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
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		
		
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取订单状态(前台提供)
		 */
		Integer order_state = BmobAPI.getIntegerValueFromJSONObject(params, "order_state");
		/**
		 * 封装参数
		 */
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", order_state);
		paramMap.put("buyer_id", member_id);
//		paramMap.put("xq_or_fw", 1);// 服务方下单
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 提交请求
		 */
		String result = find("shopnc_fworder", paramStr, skip, pageSize, "-createdAt");
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
	 * 根据订单单号查询订单信息
	 * 
	 * @author 姚永鹏
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getOrderByOrderSn() {

		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		String order_sn = jo.getString("order_sn");

		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_sn", order_sn);

		String jsonStr = findOne("shopnc_fworder", objectId);

			
		if (jsonStr.indexOf("findOne")==-1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL,jsonStr).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,jsonStr).toString());
		}
	}
	
	/**
	 * 需求订单评论
	 * TODO
	 */
	public void reviewRequirementOrder() {
		/**
		 * 上传晒单图片
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();

		
		String order_sn = getPara("order_sn");
		
		
		/**
		 * 获取订单服务(或需求)表编号
		 */
		String ou="";
		String objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_sn", order_sn);
		String orderGoodsInfo = findOne("shopnc_fworder", objectId);
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderGoodsInfo);
		ou=objectId;
		Integer order_id=(Integer) orderInfoMap.get("order_id");
		
//		Integer xq_id=(Integer) orderInfoMap.get("xq_id");
		
		
		Integer fb_member_id=(Integer) orderInfoMap.get("fb_member_id");
		/**
		 * 获取商品评分(前台提供，1-5分)
		 */
		Integer geval_scores = getParaToInt("geval_scores");
		/**
		 * 获取评价内容(前台提供)
		 */
		String geval_content = getPara("geval_content");
		/**
		 * 是否是匿名评论(前台提供)
		 */
		Integer geval_isanonymous = getParaToInt("geval_isanonymous");//1为匿名
		/**
		 * 评价时间
		 */
		long geval_addtime = System.currentTimeMillis() / 1000;
		/**
		 * 获取评价人编号(前台提供、会员编号、当前登录人)
		 */
		Integer geval_frommemberid = getParaToInt("geval_frommemberid");
		/**
		 * 获取评价人名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", geval_frommemberid);
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		String geval_frommembername = (String) memberInfoMap.get("member_name");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		//
		paramMap.put("geval_orderid", order_id);
		paramMap.put("geval_fb_member", fb_member_id);
		
		paramMap.put("geval_orderno", order_sn);
		paramMap.put("geval_scores", geval_scores);
		paramMap.put("geval_content", geval_content);
		paramMap.put("geval_isanonymous", geval_isanonymous);
		paramMap.put("geval_addtime", geval_addtime);
		paramMap.put("geval_frommemberid", geval_frommemberid);
		paramMap.put("geval_frommembername", geval_frommembername);
		paramMap.put("geval_state", 0);// 评价信息的状态 0为正常 1为禁止显示，默认正常
		paramMap.put("geval_remark", "");// 管理员对评价的处理备注,默认为空
		paramMap.put("geval_explain", "");// 解释内容,默认为空
		
		if(null!=maps&&!maps.equals("")){
			/**
			 * 获取上传文件的文件名
			 */
			
			String fileName = BmobAPI.getSingleUploadFileName(maps);
			/**
			 * 获取上传文件的url
			 */
			String url = BmobAPI.getSingleUploadFileUrl(maps);
	
			paramMap.put("geval_image", url);// 晒单图片
			paramMap.put("image_relativelypath", fileName);// 晒单图片
		
		}
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向评价表发送请求
		 */
		System.out.println(paramStr);
		String result = insert("evaluate_goods", paramStr);
		System.out.println(result);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap = new HashMap<String, Object>();
		paramMap.put("evaluation_state", 1);// 评价状态改为已评价
		paramMap.put("order_state", JsonResult.ORDER_OVER);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向订单表发送请求
		 */
		result = update("shopnc_fworder", ou, paramStr);
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
	 * 根店铺订单状态查询所有服务订单
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllServiceOrderByOrderStateAndStoreId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取订单状态(前台提供)
		 */
		Integer order_state = BmobAPI.getIntegerValueFromJSONObject(params, "order_state");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", order_state);
		paramMap.put("store_id", store_id);
		paramMap.put("xq_or_fw", 0);// 需求方下单
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 提交请求
		 */
		String result = find("shopnc_fworder", paramStr, skip, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
}
