package com.loveyou.webController.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.accountfunds.AccountFundsController;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.MakeOrderNumUtil;

/**
 * 商品订单管理控制器
 * 
 * @ClassName: GoodsOrderController
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月15日 上午9:19:01
 *
 * 
 */
public class GoodsOrderController extends Bmob {
	/**
	 * 订单创建
	 */
	public void createOrder() {
		/**
		 * 获取参数
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
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		String buyer_name = (String) memberInfoMap.get("member_name");
		/**
		 * 获取买家电子邮箱
		 */
		String buyer_email = (String) memberInfoMap.get("member_email");
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
		 * 运费(前台提供)
		 */
		Double shipping_fee = BmobAPI.getDoubleValueFromJSONObject(params, "shipping_fee");
		/**
		 * 获取商品编号(前台提供)
		 */
		Integer goods_id = BmobAPI.getIntegerValueFromJSONObject(params, "goods_id");
		/**
		 * 获取商品名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
		String goodsInfo = findOne("loveyou_goods", objectId);
		Map<String, Object> goodsInfoMap = BmobAPI.jsonStrToMap(goodsInfo);
		String goods_name = (String) goodsInfoMap.get("goods_name");
		/**
		 * 获取商品价格
		 */
		Integer goods_price = (Integer) goodsInfoMap.get("goods_price");
		/**
		 * 获取商品数量(前台提供)
		 */
		Integer goods_num = BmobAPI.getIntegerValueFromJSONObject(params, "goods_num");
		/**
		 * 获取商品实际成交价
		 */
		Double goods_pay_price = BmobAPI.getDoubleValueFromJSONObject(params, "goods_pay_price");

		/**
		 * 封装loveyou_order_common表需要的参数
		 */

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_price", goods_name);
		paramMap.put("goods_num", goods_num);
		paramMap.put("goods_pay_price", goods_pay_price);
		paramMap.put("store_id", store_id);
		paramMap.put("shipping_time", 0);// 配送时间,默认为0
		paramMap.put("shipping_express_id", 0);// 配送公司编号,默认为0
		paramMap.put("evaluation_time", 0);// 评价时间,默认为0
		paramMap.put("evalseller_state", 0);// 卖家是否已评价买家,默认为0
		paramMap.put("evalseller_time", 0);// 卖家评价买家的时间,默认为0
		paramMap.put("order_message", "");// 订单留言,默认为空
		paramMap.put("order_pointscount", 0);// 订单赠送积分,默认为0
		paramMap.put("deliver_explain", "");// 订单留言,默认为空
		paramMap.put("daddress_id", 0);// 发货地址编号,默认为0
		paramMap.put("reciver_name", buyer_name);
		paramMap.put("reciver_info", "");// 收货人其他信息，默认为空
		paramMap.put("reciver_province_id", 0);// 收货人省级ID，默认为0
		paramMap.put("invoice_info", "");// 发票信息，默认为空
		paramMap.put("promotion_info", "");// 促销信息备注，默认为空
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向loveyou_order_common表插入数据
		 */
		String result = insert("loveyou_order_common", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 获取loveyou_order_common表的订单索引编号order_id
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		objectId = (String) resultMap.get("objectId");
		String orderCommonInfo = findOne("loveyou_order_common", objectId);
		Map<String, Object> orderCommonInfoMap = BmobAPI.jsonStrToMap(orderCommonInfo);
		Integer order_id = (Integer) orderCommonInfoMap.get("order_id");
		/**
		 * 封装loveyou_order表需要的参数
		 */
		paramMap.clear();
		paramMap.put("order_commonid", order_id);
		paramMap.put("order_sn", order_sn);
		paramMap.put("pay_sn", pay_sn);
		paramMap.put("store_id", store_id);
		paramMap.put("store_name", store_name);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("buyer_name", buyer_name);
		paramMap.put("buyer_email", buyer_email);
		paramMap.put("goods_price", goods_name);
		paramMap.put("goods_num", goods_num);
		paramMap.put("goods_pay_price", goods_pay_price);
		paramMap.put("add_time", add_time);
		paramMap.put("goods_amount", goods_amount);
		paramMap.put("order_amount", order_amount);
		paramMap.put("shipping_fee", shipping_fee);
		paramMap.put("evaluation_state", 0);// 默认未评价
		paramMap.put("order_state", 20);// 订单状态默认已付款
		paramMap.put("lock_state", 0);// 锁定状态:0是正常,大于0是锁定,默认是0
		paramMap.put("delay_time", 0);// 延迟时间,默认为0
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		result = insert("loveyou_order", jsonStr);
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		resultMap = BmobAPI.jsonStrToMap(result);
		/**
		 * 获取订单编号
		 */
		objectId = (String) resultMap.get("objectId");
		String orderInfo = findOne("loveyou_order", objectId);
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		order_id = (Integer) orderInfoMap.get("order_id");

		/**
		 * @author yyp 将已生成的订单支付编号插入到订单支付表里
		 */
		String paySn = (String) orderInfoMap.get("pay_sn");

		Integer buyId = (Integer) orderInfoMap.get("buyer_id");

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
		 * 获取商品类型 '1默认2团购商品3限时折扣商品4组合套装5赠品'(前台提供)
		 */
		Integer goods_type = BmobAPI.getIntegerValueFromJSONObject(params, "goods_type");
		/**
		 * 促销活动ID （团购ID/限时折扣ID/优惠套装ID）与goods_type搭配使用'(前台提供)
		 */
		Integer promotions_id = BmobAPI.getIntegerValueFromJSONObject(params, "promotions_id");
		/**
		 * 获取佣金比例(前台提供)
		 */
		Double commis_rate = BmobAPI.getDoubleValueFromJSONObject(params, "commis_rate");
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("goods_id", goods_id);
		paramMap.put("goods_name", goods_name);
		paramMap.put("goods_price", goods_price);
		paramMap.put("goods_num", goods_num);
		paramMap.put("goods_pay_price", goods_pay_price);
		paramMap.put("store_id", store_id);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("goods_type", goods_type);
		paramMap.put("promotions_id", promotions_id);
		paramMap.put("commis_rate", commis_rate);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		result = insert("loveyou_order_goods", paramStr);
		/**
		 * 返回结果
		 */
		

		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, orderInfo).toString());
		}
	}
	/**
	 * 创建商品订单并返回订单编号
	 * @author yyp
	 */
	public void createOrderReturnOrderSn() {

		/**
		 * 生成订单编号
		 */
		String order_sn = new MakeOrderNumUtil().makeOrderNum();

		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 生成支付编号
		 */
		String pay_sn = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 卖家店铺id(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(jo,"store_id");
		/**
		 * 获取卖家店铺名称
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该商品店铺信息有误不能购买\"}").toString());
			return;
		}
		String storeInfo = findOne("loveyou_store", objectId);
		Map<String, Object> storeInfoMap = BmobAPI.jsonStrToMap(storeInfo);
		String store_name = (String) storeInfoMap.get("store_name");
		/**
		 * 买家id(前台提供)
		 */
		Integer buyer_id = BmobAPI.getIntegerValueFromJSONObject(jo,"buyer_id");
		/**
		 * 获取买家姓名
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", buyer_id);
		if(objectId==null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"买家信息不正确，不能购买商品\"}").toString());
			return;
		}
		
		String memberInfo = findOne("loveyou_member", objectId);
		Map<String, Object> memberInfoMap = BmobAPI.jsonStrToMap(memberInfo);
		String buyer_name = (String) memberInfoMap.get("member_name");
		Integer buyer_store=(Integer) memberInfoMap.get("store_id");
		/**
		 * 获取买家电子邮箱
		 */
		String buyer_email = (String) memberInfoMap.get("member_email");
		/**
		 * 订单生成时间
		 */
		long add_time = System.currentTimeMillis() / 1000;
		/**
		 * 商品总价格(前台提供)
		 */
		Integer goods_amount = BmobAPI.getIntegerValueFromJSONObject(jo,"goods_amount");
		/**
		 * 订单总价格(前台提供)
		 */
		Integer order_amount = BmobAPI.getIntegerValueFromJSONObject(jo,"order_amount");
		
		/**
		 * 运费(前台提供)
		 */
		Integer shipping_fee = BmobAPI.getIntegerValueFromJSONObject(jo,"shipping_fee");
		/**
		 * 获取商品编号(前台提供)
		 */
		Integer goods_id = BmobAPI.getIntegerValueFromJSONObject(jo,"goods_id");
		/**
		 * 获取商品名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
		String goodsInfo = findOne("loveyou_goods", objectId);
		Map<String, Object> goodsInfoMap = BmobAPI.jsonStrToMap(goodsInfo);
		String goods_name = (String) goodsInfoMap.get("goods_name");
		
		Integer goods_storage=(Integer) goodsInfoMap.get("goods_storage");
		
		Integer store=(Integer) goodsInfoMap.get("store_id");
		if(buyer_store!=null&&store.equals(buyer_store)){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"亲，不能刷销量哦\"}").toString());
			return;
		}
		/**
		 * 获取商品价格
		 */
		Integer goods_price = (Integer) goodsInfoMap.get("goods_price");
		/**
		 * 获取商品数量(前台提供)
		 */
		Integer goods_num = BmobAPI.getIntegerValueFromJSONObject(jo,"goods_num");
		
		if(goods_num!=null&&goods_num>0){
			
			Integer surplus=goods_storage-goods_num;
			String jsonStr="{\"goods_storage\":"+surplus+"}";
			String resultd=update("loveyou_goods",objectId , jsonStr);
			if(resultd.indexOf("At")==-1){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"商品信息错误，暂不能购买\"}").toString());
				return;
			}
		}
		
		/**
		 * 获取商品实际成交价
		 */
		Integer goods_pay_price = BmobAPI.getIntegerValueFromJSONObject(jo,"goods_pay_price");
		//TODO
		//需要添加计算商品价格
		
		order_amount=goods_num*goods_amount;
		/**
		 * 地址
		 */
		Integer address_id=BmobAPI.getIntegerValueFromJSONObject(jo, "address_id");
		/**
		 * 封装loveyou_order_common表需要的参数
		 */

		Map<String, Object> paramMap = new HashMap<String, Object>();

//		重大错误
		//	paramMap.put("goods_price", goods_name);
		paramMap.put("goods_name", goods_name);
		paramMap.put("goods_num", goods_num);
		//姚永鹏添加
		paramMap.put("goods_id", goods_id);
		paramMap.put("goods_pay_price", goods_pay_price);
		paramMap.put("store_id", store_id);
		paramMap.put("shipping_time", 0);// 配送时间,默认为0
		paramMap.put("shipping_express_id", 0);// 配送公司编号,默认为0
		paramMap.put("evaluation_time", 0);// 评价时间,默认为0
		paramMap.put("evalseller_state", 0);// 卖家是否已评价买家,默认为0
		paramMap.put("evalseller_time", 0);// 卖家评价买家的时间,默认为0
		paramMap.put("order_message", "");// 订单留言,默认为空
		paramMap.put("order_pointscount", 0);// 订单赠送积分,默认为0
		paramMap.put("deliver_explain", "");// 订单留言,默认为空
		paramMap.put("daddress_id", 0);// 发货地址编号,默认为0
		paramMap.put("reciver_name", buyer_name);
		paramMap.put("reciver_info", "");// 收货人其他信息，默认为空
		paramMap.put("reciver_province_id", 0);// 收货人省级ID，默认为0
		paramMap.put("invoice_info", "");// 发票信息，默认为空
		paramMap.put("promotion_info", "");// 促销信息备注，默认为空
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向loveyou_order_common表插入数据
		 */
		String result = insert("loveyou_order_common", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 获取loveyou_order_common表的订单索引编号order_id
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		objectId = (String) resultMap.get("objectId");
		String orderCommonInfo = findOne("loveyou_order_common", objectId);
		Map<String, Object> orderCommonInfoMap = BmobAPI.jsonStrToMap(orderCommonInfo);
		Integer order_id = (Integer) orderCommonInfoMap.get("order_id");
		/**
		 * 封装loveyou_order表需要的参数
		 */
		paramMap.clear();
		paramMap.put("order_commonid", order_id);
		paramMap.put("order_sn", order_sn);
		paramMap.put("pay_sn", pay_sn);
		paramMap.put("store_id", store_id);
		paramMap.put("store_name", store_name);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("buyer_name", buyer_name);
		paramMap.put("buyer_email", buyer_email);
		paramMap.put("goods_price", goods_name);
		paramMap.put("goods_num", goods_num);
		//姚永鹏添加
		paramMap.put("goods_id", goods_id);
		paramMap.put("address_id", address_id);
		paramMap.put("goods_pay_price", goods_pay_price);
		paramMap.put("add_time", add_time);
		paramMap.put("goods_amount", goods_amount);
		paramMap.put("order_amount", order_amount);
		paramMap.put("shipping_fee", shipping_fee);
		paramMap.put("evaluation_state", 0);// 默认未评价
		paramMap.put("order_state", JsonResult.ORDER_OBLIGATION);// 订单状态默认已付款
		paramMap.put("lock_state", 0);// 锁定状态:0是正常,大于0是锁定,默认是0
		paramMap.put("delay_time", 0);// 延迟时间,默认为0
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		result = insert("loveyou_order", jsonStr);
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		resultMap = BmobAPI.jsonStrToMap(result);
		/**
		 * 获取订单编号
		 */
		objectId = (String) resultMap.get("objectId");
		String orderInfo = findOne("loveyou_order", objectId);
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		order_id = (Integer) orderInfoMap.get("order_id");

		/**
		 * @author yyp 将已生成的订单支付编号插入到订单支付表里
		 */
		String paySn = (String) orderInfoMap.get("pay_sn");

		Integer buyId = (Integer) orderInfoMap.get("buyer_id");

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
		 * 获取商品类型 '1默认2团购商品3限时折扣商品4组合套装5赠品'(前台提供)
		 */
		Integer goods_type = BmobAPI.getIntegerValueFromJSONObject(jo,"goods_type");
		/**
		 * 促销活动ID （团购ID/限时折扣ID/优惠套装ID）与goods_type搭配使用'(前台提供)
		 */
		Integer promotions_id = BmobAPI.getIntegerValueFromJSONObject(jo,"promotions_id");
		/**
		 * 获取佣金比例(前台提供)
		 */
		Double commis_rate = Double.parseDouble(jo.getString("commis_rate"));
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("goods_id", goods_id);
		paramMap.put("goods_name", goods_name);
		paramMap.put("goods_price", goods_price);
		paramMap.put("goods_num", goods_num);
		
		paramMap.put("goods_pay_price", goods_pay_price);
		paramMap.put("store_id", store_id);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("goods_type", goods_type);
		paramMap.put("promotions_id", promotions_id);
		paramMap.put("commis_rate", commis_rate);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		result = insert("loveyou_order_goods", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			/**
			 * outTradeNo （业务系统唯一订单号）body（商品名称） totalFee（总价）
			 *
			 * 姚永鹏修改
			 */
			JSONObject jod = JSONObject.parseObject(orderInfo);
			String outTradeNo = jod.getString("order_sn");
			Double totalFee = jod.getDouble("order_amount");
			String body = goods_name;
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
	}

	/**
	 * 订单支付确认收到款：后台人员用的接口(修改支付状态)
	 */
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
		String result = insert("loveyou_order_pay", paramStr);
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 买家申请退单
	 */
	public void applyCancelOrder() {
		/**
		 * 上传商品图片
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
		/**
		 * 获取上传文件的文件名
		 */
		String fileName = BmobAPI.getSingleUploadFileName(maps);
		/**
		 * 获取上传文件的url
		 */
		String url = BmobAPI.getSingleUploadFileUrl(maps);
		/**
		 * 获取订单ID(前端提供)
		 */
		Integer order_id = getParaToInt("order_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_id", order_id);
		/**
		 * 获取订单编号
		 */
		String orderInfo = findOne("loveyou_order", objectId);
		/**
		 * 从结果中获取订单编号
		 */
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		// Integer order_sn = (Integer) orderInfoMap.get("order_sn");
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
		 * 获取商品ID(前台提供)
		 */
		Integer goods_id = getParaToInt("goods_id");

		/**
		 * 获取商品数量
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_id", order_id);
		paramMap.put("goods_id", goods_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		// String orderGoodsInfo = findAll("loveyou_order_goods", paramStr);
		// Map<String, Object> orderGoodsInfoMap =
		// BmobAPI.jsonStrToMap(orderGoodsInfo);
		// Integer goods_num = (Integer) orderGoodsInfoMap.get("goods_num");
		/**
		 * TODO 姚永鹏修改
		 */
		ArrayList<String> list = BmobAPI.getAllObjectIdById("loveyou_order_goods", paramStr);
		String jsonStr = "";
		if(null!=list)
		for (String ObjectId : list) {
			jsonStr = BmobAPI.findOne("loveyou_order_goods", ObjectId);
		}
		if (list.isEmpty()) {
			System.out.println("love_order_goods 表中没有匹配值");
			return;
		}
		JSONObject jo = JSONObject.parseObject(jsonStr);
		Integer goodsNum = jo.getInteger("goods_num");
		Integer order_goods_id = jo.getInteger("rec_id");
		String goodsName = jo.getString("goods_name");

		/**
		 * 获取申请类型(前台提供)
		 */
		Integer refund_type = getParaToInt("refund_type");
		/**
		 * 获取退货类型(前台提供)
		 */
		Integer return_type = getParaToInt("return_type");
		/**
		 * 获取退款金额(前台提供)
		 */
		Integer refund_amount = getParaToInt("refund_amount");

		/**
		 * 创建添加时间
		 */
		long add_time = System.currentTimeMillis();
		/**
		 * 获取退款原因
		 */
		String buyer_message = getPara("buyer_message");
		/**
		 * 创建参数列表
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("order_sn", order_sn);
		paramMap.put("refund_sn", refund_sn);
		paramMap.put("store_id", store_id);
		paramMap.put("store_name", store_name);
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("buyer_name", buyer_name);
		paramMap.put("goods_id", goods_id);
		paramMap.put("refund_type", refund_type);

		paramMap.put("goods_num", goodsNum);
		paramMap.put("order_goods_id", order_goods_id);
		paramMap.put("goods_name", goodsName);

		paramMap.put("seller_state", 1);// 卖家处理结果，默认为1，待处理
		paramMap.put("refund_state", 1);// 申请状态，默认为1，处理中
		paramMap.put("return_type", return_type);
		paramMap.put("order_lock", 1);// 订单锁定类型:1为不用锁定,2为需要锁定,默认为1
		paramMap.put("goods_state", 1);// 物流状态:1为待发货,2为待收货,3为未收到,4为已收货,默认为1
		paramMap.put("express_id", 0);// 物流公司编号,默认为0
		paramMap.put("ship_time", 0);// 发货时间,默认为0
		paramMap.put("delay_time", 0);// 收货延迟时间,默认为0
		paramMap.put("receive_time", 0);// 收货时间,默认为0
		paramMap.put("receive_message", "");// 收货备注,默认为空
		paramMap.put("commis_rate", 0);// 佣金比例,默认为0
		paramMap.put("goods_image", url);// 商品图片
		paramMap.put("image_relativelypath", fileName);// 商品图片
		paramMap.put("refund_amount", refund_amount);
		paramMap.put("add_time", add_time);
		paramMap.put("buyer_message", buyer_message);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = insert("refund_return", paramStr);
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		}
		/**
		 * 获取操作人(前台提供，当前用户，会员编号)
		 */
		Integer member_id = getParaToInt("member_id");

		/**
		 * TODO 姚永鹏添加
		 */
		String oj = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		String str = findOne("loveyou_member", oj);
		JSONObject j1 = JSONObject.parseObject(str);
		String memberName = j1.getString("member_name");
		/**
		 * 获取订单状态
		 */
		Integer order_state = (Integer) orderInfoMap.get("order_state");
		/**
		 * 封裝loveyou_order_log表需要的參數
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("log_msg", "买家申请退单");
		paramMap.put("log_time", add_time);
		paramMap.put("log_role", "买家");
		paramMap.put("member_id", member_id);
		paramMap.put("log_user", memberName);
		paramMap.put("log_orderstate", order_state);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向订单日志表插入数据
		 */
		result = insert("loveyou_order_log", paramStr);
		/**
		 * 返回結果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 卖家处理退单
	 */
	public void handleCancelOrderApplication() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单退款申请编号(前台提供)
		 */
		String refund_sn = BmobAPI.getStringValueFromJSONObject(params, "refund_sn");
		/**
		 * 获取卖家处理状态(前台获取)
		 */
		Integer seller_state = BmobAPI.getIntegerValueFromJSONObject(params, "seller_state");
		/**
		 * 卖家处理时间
		 */
		long seller_time = System.currentTimeMillis() / 1000;
		/**
		 * 卖家备注(前台获取)
		 */
		String seller_message = BmobAPI.getStringValueFromJSONObject(params, "seller_message");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("refund_return", "refund_sn", refund_sn);
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("seller_state", seller_state);
		paramMap.put("seller_time", seller_time);
		paramMap.put("seller_message", seller_message);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = update("refund_return", objectId, paramStr);
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 获取操作人(前台提供，当前用户，会员编号)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * TODO 姚永鹏添加
		 */
		String oj = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		String str = findOne("loveyou_member", oj);
		JSONObject j1 = JSONObject.parseObject(str);
		String memberName = j1.getString("member_name");
		/**
		 * 获取订单编号 姚永鹏修改
		 */
		String mn = BmobAPI.findOne("refund_return", objectId);
		JSONObject jo = JSONObject.parseObject(mn);
		Integer order_id = jo.getInteger("order_id");
		/**
		 * 获取订单状态
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_order", "order_id", order_id);
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"订单信息错误\"}").toString());
			return;
		}
		String orderInfo = findOne("loveyou_order", objectId);
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		Integer order_state = (Integer) orderInfoMap.get("order_state");
		/**
		 * 日誌生成時間
		 */
		long log_time = System.currentTimeMillis() / 1000;
		/**
		 * 封裝loveyou_order_log表需要的參數
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("log_msg", "卖家处理退单");
		paramMap.put("log_time", log_time);
		paramMap.put("log_role", "卖家");
		paramMap.put("member_id", member_id);
		paramMap.put("log_user", memberName);
		paramMap.put("log_orderstate", order_state);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向订单日志表插入数据
		 */
		result = insert("loveyou_order_log", paramStr);
		/**
		 * 返回結果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 买家查询所有订单
	 */
	public void getAllOrderByMemberIdAndState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取买家编号(前台提供)
		 */
		Integer buyer_id = BmobAPI.getIntegerValueFromJSONObject(params, "buyer_id");
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer order_state=BmobAPI.getIntegerValueFromJSONObject(params, "order_state");
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
		paramMap.put("buyer_id", buyer_id);
		paramMap.put("order_state", order_state);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 提交请求
		 */
		String result = find("loveyou_order", paramStr, skip, pageSize,"-createdAt");
		
		if (result.indexOf("results\":[]") == -1) {
			JSONObject jo = (JSONObject) JSONObject.parse(result);
//			JSONArray ja = JSONArray.parseArray(result);
			JSONArray ja=jo.getJSONArray("results");
			JSONArray resu = new JSONArray();
			int i = 0;
			for (i = 0; i < ja.size(); i++) {
				JSONObject mo = ja.getJSONObject(i);
				Integer goods_id = mo.getInteger("goods_id");
				if (goods_id != null && goods_id > 0) {
					String objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
					if(objectId!=null&&!"".equals(objectId)&&objectId.indexOf("Not Found:(")==-1){
						String storeInfo = findOne("loveyou_goods", objectId);
						JSONObject on = JSONObject.parseObject(storeInfo);
						String goods_image = on.getString("goods_image");
						String goods_name = on.getString("goods_name");
						mo.put("goods_image", goods_image);
						mo.put("goods_name", goods_name);
						mo.put("goods_id", goods_id);
						resu.add(mo);
				
					}
				}
			}
			result = "{\"results\":" + resu.toJSONString() + "}";
		}

		
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 卖家查询所有订单
	 */
	public void getAllOrderByStoreId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 从结果中获取店铺id(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
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
		paramMap.put("store_id", store_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 提交请求
		 */
		String result = find("loveyou_order", paramStr, skip, pageSize,"-createdAt");
		if (result.indexOf("results\":[]") == -1) {
			JSONObject jo = (JSONObject) JSONObject.parse(result);
//			JSONArray ja = JSONArray.parseArray(result);
			JSONArray ja=jo.getJSONArray("results");
			JSONArray resu = new JSONArray();
			int i = 0;
			for (i = 0; i < ja.size(); i++) {
				JSONObject mo = ja.getJSONObject(i);
				Integer goods_id = mo.getInteger("goods_id");
				if (goods_id != null && goods_id > 0) {
					String objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
					if(objectId!=null&&!"".equals(objectId)&&objectId.indexOf("Not Found:(")==-1){
						String storeInfo = findOne("loveyou_goods", objectId);
						JSONObject on = JSONObject.parseObject(storeInfo);
						String goods_image = on.getString("goods_image");
						String goods_name = on.getString("goods_name");
						mo.put("goods_image", goods_image);
						mo.put("goods_name", goods_name);
						mo.put("goods_id", goods_id);
						resu.add(mo);
				
					}
				}
			}
			result = "{\"results\":" + resu.toJSONString() + "}";
		}

		System.out.println(result);
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 根据订单号查询订单
	 */
	public void getOrderInfoByOrderSn() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		String order_sn = BmobAPI.getStringValueFromJSONObject(params, "order_sn");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_sn", order_sn);
		/**
		 * 发送请求
		 */
		String result = findOne("loveyou_order", objectId);
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 根据订单状态查询订单
	 */
	public void getAllOrderByOrderState() {
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
		 * 获取订单状态(前台提供)
		 */
		Integer order_state = BmobAPI.getIntegerValueFromJSONObject(params, "order_state");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", order_state);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_order", paramStr, skip, pageSize,"-createdAt");
		/**
		 * 姚永鹏添加
		 */
		JSONArray wn=new JSONArray();
		
		if(result.indexOf("results\":[]")==-1){
			
			JSONObject jo=(JSONObject) JSONObject.parse(result);
			
			JSONArray ja=jo.getJSONArray("results");
			
			JSONObject jm;
			int i=0;
			for(i=0;i<ja.size();i++){
				
				jm=(JSONObject) ja.get(i);
				Integer goods_id=jm.getInteger("goods_id");
				
				String object=BmobAPI.getObjectIdById("loveyou_goods","goods_id",goods_id);
				
				if(object!=null&&!"".equals(object)&&object.indexOf("Not Found:(")==-1){
					
					String goodsInfo=BmobAPI.findOne("loveyou_goods", object);
					
					JSONObject du=JSONObject.parseObject(goodsInfo);
					
					String goods_image=du.getString("goods_image");
					
					String goods_name=du.getString("goods_name");
					
					jm.put("goods_image", goods_image);
					
					jm.put("goods_name", goods_name);
					
					jm.put("goods_id", goods_id);
					
					wn.add(jm);
				}
			}
		}
		result="{\"results\":"+wn.toJSONString()+"}";
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 订单高级多条件查询 TODO 待实现
	 */
	public void getAllOrderByCondition() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取起始时间(前台提供)
		 */
		Integer startTime = BmobAPI.getIntegerValueFromJSONObject(params, "startTime");
		/**
		 * 获取结束时间(前台提供)
		 */
		Integer endTime = BmobAPI.getIntegerValueFromJSONObject(params, "endTime");

		/**
		 * 获取最小金额(前台提供)
		 */
		Double minPrice = BmobAPI.getDoubleValueFromJSONObject(params, "minPrice");
		/**
		 * 获取最大金额(前台提供)
		 */
		Double maxPrice = BmobAPI.getDoubleValueFromJSONObject(params, "maxPrice");
		/**
		 * 准备条件表达式
		 */
		String condition1 = "{\"add_time\":{\"$gte\":" + startTime + ",\"$lte\":" + endTime + "}}";
		String condition2 = "{\"order_amount\":{\"$gte\":" + minPrice + ",\"$lte\":" + maxPrice + "}}";
		/**
		 * 发送请求
		 */
		String result = BmobAPI.findAllByConditionAnd("loveyou_order", condition1, condition2);
		System.out.println(result);

	}

	/**
	 * 订单发货
	 */
	public void sendGoods() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前端提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 获取物流编号(前端提供)
		 */
		String shipping_code = params.getString("shipping_code");
		
		String express_company=params.getString("express_company");
		/**
		 * 封装参数
		 */
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = new HashMap<String, Object>();
		paramMap.put("shipping_code", shipping_code);
		paramMap.put("express_company", express_company);
		paramMap.put("order_state", JsonResult.ORDER_SENDING);// 订单状态改为已发货
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_id", order_id);
		/**
		 * 修改订单信息
		 */
		String result = update("loveyou_order", objectId, paramStr);
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 获取订单状态
		 */
		String orderInfo = findOne("loveyou_order", objectId);
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		Integer order_state = (Integer) orderInfoMap.get("order_state");
		/**
		 * 获取卖家编号(seller_id)
		 */
		Integer seller_id = BmobAPI.getIntegerValueFromJSONObject(params, "seller_id");
		/**
		 * TODO 姚永鹏添加
		 */
		String oj = BmobAPI.getObjectIdById("loveyou_member", "member_id", seller_id);
		
		if(oj==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户信息错误\"}").toString());
			return;
		}
		
		String str = findOne("loveyou_member", oj);
		JSONObject j1 = JSONObject.parseObject(str);
		String memberName = j1.getString("member_name");
		/**
		 * 日誌生成時間
		 */
		long log_time = System.currentTimeMillis() / 1000;
		/**
		 * 封裝loveyou_order_log表需要的參數
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("log_msg", "卖家发货");
		paramMap.put("log_time", log_time);
		paramMap.put("log_role", "卖家");
		paramMap.put("member_id", seller_id);
		paramMap.put("log_user", memberName);
		paramMap.put("log_orderstate", order_state);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向订单日志表插入数据
		 */
		result = insert("loveyou_order_log", paramStr);
		/**
		 * 返回結果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 订单收货
	 */
	public void confirmReceiveGoods() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取订单编号(前台提供)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_id", order_id);
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("order_state", JsonResult.ORDER_FINISH);// 修改订单状态为已收货
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
		 * 日志生成时间
		 */
		long log_time = System.currentTimeMillis() / 1000;
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * TODO 姚永鹏添加
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		String userInfo = findOne("loveyou_member", objectId);
		JSONObject jo = JSONObject.parseObject(userInfo);
		String memberName = jo.getString("member_name");
		/**
		 * 获取订单状态
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_order", "order_id", order_id);
		String orderInfo = findOne("loveyou_order", objectId);
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		Integer order_state = (Integer) orderInfoMap.get("order_state");
		/**
		 * 封裝loveyou_order_log表需要的參數
		 */
		paramMap.clear();
		paramMap.put("order_id", order_id);
		paramMap.put("log_msg", "买家收货");
		paramMap.put("log_time", log_time);
		paramMap.put("log_role", "买家");
		paramMap.put("member_id", member_id);
		paramMap.put("log_user", memberName);
		paramMap.put("log_orderstate", order_state);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		System.out.println(paramStr);
		/**
		 * 向订单日志表插入数据
		 */
		result = insert("loveyou_order_log", paramStr);
		/**
		 * 返回結果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 订单评论
	 */
	public void reviewOrder() {
		/**
		 * 上传晒单图片
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
		/**
		 * 获取上传文件的文件名
		 */
		String fileName = "";
		/**
		 * 获取上传文件的url
		 */
		String url = "";
		
		if (null!=maps) {
			fileName = BmobAPI.getSingleUploadFileName(maps);
			url = BmobAPI.getSingleUploadFileUrl(maps);
		}
		/**
		 * 获取订单表自增编号(前台提供)
		 */
		Integer order_id = getParaToInt("order_id");
		/**
		 * 获取订单编号
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_id", order_id);
		String ou=objectId;
		String orderInfo = findOne("loveyou_order", objectId);
		Map<String, Object> orderInfoMap = BmobAPI.jsonStrToMap(orderInfo);
		String order_sn = (String) orderInfoMap.get("order_sn");
		/**
		 * 获取订单编号
		 */
		Integer geval_orderid = (Integer) orderInfoMap.get("order_id");
		/**
		 * 获取商品编号(前台提供)
		 */
		Integer goods_id = getParaToInt("goods_id");
		/**
		 * 获取订单商品表编号
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_order_goods", "goods_id", goods_id);
		String orderGoodsInfo = findOne("loveyou_order_goods", objectId);
		Map<String, Object> orderGoodsInfoMap = BmobAPI.jsonStrToMap(orderGoodsInfo);
		Integer geval_ordergoodsid = (Integer) orderGoodsInfoMap.get("rec_id");
		/**
		 * 获取商品名称
		 */
		String goods_name = (String) orderGoodsInfoMap.get("goods_name");
		/**
		 * 获取商品价格
		 */
		Integer goods_price = (Integer) orderGoodsInfoMap.get("goods_price");
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
		Integer geval_isanonymous = getParaToInt("geval_isanonymous"); //默认匿名 1
		/**
		 * 评价时间
		 */
		long geval_addtime = System.currentTimeMillis() / 1000;
		/**
		 * 获取店铺编号
		 */
		Integer store_id = (Integer) orderGoodsInfoMap.get("store_id");
		/**
		 * 获取店铺名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		String storeInfo = findOne("loveyou_store", objectId);
		Map<String, Object> storeInfoMap = BmobAPI.jsonStrToMap(storeInfo);
		String store_name = (String) storeInfoMap.get("store_name");
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
		paramMap.put("geval_orderid", geval_orderid);
		paramMap.put("geval_orderno", order_sn);
		paramMap.put("geval_ordergoodsid", geval_ordergoodsid);
		paramMap.put("geval_goodsid", goods_id);
		paramMap.put("geval_goodsname", goods_name);
		paramMap.put("geval_goodsprice", goods_price);
		paramMap.put("geval_scores", geval_scores);
		paramMap.put("geval_content", geval_content);
		paramMap.put("geval_isanonymous", geval_isanonymous);
		paramMap.put("geval_addtime", geval_addtime);
		paramMap.put("geval_storeid", store_id);
		paramMap.put("geval_storename", store_name);
		paramMap.put("geval_frommemberid", geval_frommemberid);
		paramMap.put("geval_frommembername", geval_frommembername);
		paramMap.put("geval_state", 0);// 评价信息的状态 0为正常 1为禁止显示，默认正常
		paramMap.put("geval_remark", "");// 管理员对评价的处理备注,默认为空
		paramMap.put("geval_explain", "");// 解释内容,默认为空
		paramMap.put("geval_image", url);// 晒单图片
		paramMap.put("image_relativelypath", fileName);// 晒单图片
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向评价表发送请求
		 */
		String result = insert("evaluate_goods", paramStr);
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
		objectId = BmobAPI.getObjectIdById("loveyou_order_goods", "order_id", order_id);
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
		result = update("loveyou_order_goods", objectId, paramStr);
		
		result=update("loveyou_order",ou,paramStr);
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
	 * 验证支付金额
	 * 
	 * @author 姚永鹏添加
	 */
	public void verifyAmount() {

		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		String order_sn = jo.getString("order_sn");
		Integer order_amount = jo.getInteger("order_amount");

		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_sn", order_sn);

		String oneRow = findOne("loveyou_order", objectId);

		JSONObject je = JSONObject.parseObject(oneRow);

		Integer shop_amount = je.getInteger("order_amount");

		String result = "";
		if (shop_amount == order_amount) {
			result = "{\"msg\":\"ok\"}";
		}

		if (result.indexOf("ok") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}

	}

	/**
	 * @author FIXME 姚永鹏 修改订单支付状态
	 */
	public void updatePayState() {
		// order_state:number=1为已支付
		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		String order_sn = jo.getString("order_sn");

		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_sn", order_sn);

		if(objectId==null){
			jo.put("msg", "order_sn is not exist");
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jo.toJSONString()).toString());
			return;
		}
		String oneRow = findOne("loveyou_order", objectId);
		
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

		BmobAPI.update("loveyou_order", objectId, joStr);

		objectId = BmobAPI.getObjectIdById("loveyou_order_pay", "pay_sn", pay_sn);

		jo.clear();
		
		jo.put("api_pay_state", 1);// 改为已支付

		String result=BmobAPI.update("loveyou_order_pay", objectId, jo.toJSONString());
	
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			new AccountFundsController().writerLog(buyer_id,"手动到账",JsonResult.LG_TYPE_ORDER_PAY_GOODS,true,order_amount);
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}

	}
	/** 
	* @Title: updatePayState2 
	* @Description:  (本方法的作用是：修改订单支付状态) 
	* @param 
	* @return void    返回类型 
	* @author ggj
	* @date 2016年7月9日 下午4:21:36  
	* @throws 
	*/
	public void updatePayState2(JSONObject jsonObject) {
		/* order_state:number=1为已支付*/
		//JSONObject jsonObject = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String order_sn = jsonObject.getString("out_trade_no");
		
		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_sn", order_sn);
		
		if(objectId==null){
			jsonObject.put("msg", "order_sn is not exist");
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jsonObject.toJSONString()).toString());
			return;
		}
		String oneRow = findOne("loveyou_order", objectId);
		
		JSONObject jM = JSONObject.parseObject(oneRow);
		
		if(jM.getInteger("order_state")>1){
			jsonObject.put("msg", "order_sn are paid and success");
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jsonObject.toJSONString()).toString());
			return;
		}
		String pay_sn = jM.getString("pay_sn");
		
		Integer buyer_id=jM.getInteger("buyer_id");
		
		Integer order_amount=jM.getInteger("order_amount");
		
		String joStr ="{\"order_state\":1}";
		
		BmobAPI.update("loveyou_order", objectId, joStr);
		
		objectId = BmobAPI.getObjectIdById("loveyou_order_pay", "pay_sn", pay_sn);
		
		jsonObject.clear();
		
		jsonObject.put("api_pay_state", 1);// 改为已支付
		
		String result=BmobAPI.update("loveyou_order_pay", objectId, jsonObject.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			new AccountFundsController().writerLog(buyer_id,"手动到账",JsonResult.LG_TYPE_ORDER_PAY_GOODS,true,order_amount);
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}

	/**
	 * 获取一级商品类别
	 */
	public void getFirstClassGoodsType() {
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
		 * 封装条件参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("gc_parent_id", 0);
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_goods_class", whereJsonStr, skip, pageSize,"-createdAt");
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
	 * 获取二级商品类别
	 */
	public void getSecondClassGoodsType() {
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
		 * 获取一级服务类别id(前台提供)
		 */
		Integer gc_parent_id = BmobAPI.getIntegerValueFromJSONObject(params, "gc_id");
		/**
		 * 封装条件参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("gc_parent_id", gc_parent_id);
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_goods_class", whereJsonStr, skip, pageSize,"-createdAt");
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
	 * 根据订单单号查询订单信息
	 * 
	 * @author 姚永鹏
	 */
	public void getOrderByOrderSn() {

		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		String order_sn = jo.getString("order_sn");

		String objectId = BmobAPI.getObjectIdById("loveyou_order", "order_sn", order_sn);

		String jsonStr = findOne("loveyou_order", objectId);

			
		if (jsonStr.indexOf("findOne")==-1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL,jsonStr).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,jsonStr).toString());
		}
	}
	/**
	 * 根据状态查询卖家订单
	 */
	public void getOrderByStoreIdAndState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 从结果中获取店铺id(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
		
		Integer order_state=BmobAPI.getIntegerValueFromJSONObject(params, "order_state");
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
		paramMap.put("store_id", store_id);
		paramMap.put("order_state", order_state);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 提交请求
		 */
		String result = find("loveyou_order", paramStr, skip, pageSize,"-createdAt");
		System.out.println(result);
		
		if (result.indexOf("results\":[]") == -1) {
			JSONObject jo = (JSONObject) JSONObject.parse(result);
//			JSONArray ja = JSONArray.parseArray(result);
			JSONArray ja=jo.getJSONArray("results");
			JSONArray resu = new JSONArray();
			int i = 0;
			for (i = 0; i < ja.size(); i++) {
				JSONObject mo = ja.getJSONObject(i);
				Integer goods_id = mo.getInteger("goods_id");
				if (goods_id != null && goods_id > 0) {
					String objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
					if(objectId!=null&&!"".equals(objectId)&&objectId.indexOf("Not Found:(")==-1){
						String storeInfo = findOne("loveyou_goods", objectId);
						JSONObject on = JSONObject.parseObject(storeInfo);
						String goods_image = on.getString("goods_image");
						String goods_name = on.getString("goods_name");
						mo.put("goods_image", goods_image);
						mo.put("goods_name", goods_name);
						mo.put("goods_id", goods_id);
						resu.add(mo);
				
					}
				}
			}
			result = "{\"results\":" + resu.toJSONString() + "}";
		}


		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 *获取所有商品订单
	 */
	
	public void getAllGoodsOrder(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		if(page==null&&pageSize==null){
			
			page=1;
			pageSize=10;
		}
		
		int skip=(page-1)*pageSize;
		
		String result=find("loveyou_order", "", skip,pageSize, "-createdAt");
		
		if(result.indexOf("[")!=-1){
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			
		}else{
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"没有获取到相关内容，请联系管理员\"}").toString());
		}
	}
}
