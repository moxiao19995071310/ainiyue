package com.loveyou.webController.refund;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.accountfunds.AccountFundsController;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;

/**
 * 退款管理模块控制器
 * 
 * @ClassName: RefundController
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月18日 下午4:49:25
 *
 * 
 */
public class RefundController extends Bmob {

	/**
	 * 查询卖家收到的退款列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllRefundByStore() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取店铺编号(前台提供)
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
		 * 发送请求
		 */
		String result = find("refund_return", paramStr, skip, pageSize,"-createdAt");
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
	 * 单笔退款详情查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getRefundById() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取退单单号Id(前台提供)
		 */
		Integer refund_id = BmobAPI.getIntegerValueFromJSONObject(params, "refund_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("refund_return", "refund_id", refund_id);
		/**
		 * 发送请求
		 */
		String result = findOne("refund_return", objectId);
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
	 * 查询退款留言/凭证列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getRefundComment() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取商品编号(前台提供)
		 */
		Integer goods_id = BmobAPI.getIntegerValueFromJSONObject(params, "goods_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_id", goods_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = findAll("refund_return", paramStr);
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
	 * 卖家拒绝退款
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void refuseRefund() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取退货记录编号(前台提供)
		 */
		Integer refund_id = BmobAPI.getIntegerValueFromJSONObject(params, "refund_id");
		/**
		 * 获取订单编号
		 */
		Integer order_id=BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		
		/**
		 * 卖家处理时间
		 */
		long seller_time = System.currentTimeMillis() / 1000;
		/**
		 * 获取卖家留言(前台提供)
		 */
		String seller_message = BmobAPI.getStringValueFromJSONObject(params, "seller_message");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("seller_time", seller_time);
		paramMap.put("seller_message", seller_message);
		paramMap.put("seller_state", 3);// 卖家处理状态为不同意
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("refund_return", "refund_id", refund_id);
		/**
		 * 发送请求
		 */
		String result = update("refund_return", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			
		} else {
			
			objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
			//退单状态 0 待审核 1服务方确认退单 3服务方确认不退单 2 管理员确认退单 5管理员确认不退单 4已完成退单
			result=update("shopnc_fworder", objectId, "{\"refund_state\":3}");
			
			if(result.indexOf("At") != -1){
			
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
				return;
			}
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
	}

	/**
	 * 卖家同意退款
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void agreeRefund() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取退货记录编号(前台提供)
		 */
		Integer refund_id = BmobAPI.getIntegerValueFromJSONObject(params, "refund_id");
		/**
		 * 获取订单编号
		 */
		Integer order_id=BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		
		/**
		 * 卖家处理时间
		 */
		long seller_time = System.currentTimeMillis() / 1000;
		/**
		 * 获取卖家留言(前台提供)
		 */
		String seller_message = BmobAPI.getStringValueFromJSONObject(params, "seller_message");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("seller_time", seller_time);
		
		if(seller_message!=null&&!"".equals(seller_message))
			paramMap.put("seller_message", seller_message);
		paramMap.put("seller_state", 1);// 卖家处理状态为同意
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("refund_return", "refund_id", refund_id);
		/**
		 * 发送请求
		 */
		String result = update("refund_return", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			
		} else {
			
			objectId = BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
			//退单状态 0 待审核 1服务方确认退单 3服务方确认不退单 2 管理员确认退单 5管理员确认不退单 4已完成退单
			result=update("shopnc_fworder", objectId, "{\"refund_state\":1}");
			
			if(result.indexOf("At") != -1){
			
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
				return;
			}
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
	}

	/**
	 * 审核退款单
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void checkRefundApplication() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取申请记录编号(前台提供)
		 */
		Integer refund_id = BmobAPI.getIntegerValueFromJSONObject(params, "refund_id");
		/**
		 * 获取卖家处理状态(前台提供)
		 */
		Integer refund_state=BmobAPI.getIntegerValueFromJSONObject(params, "refund_state");
		/**
		 * 卖家处理时间
		 */
		long seller_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//paramMap.put("seller_state", seller_state);
		paramMap.put("seller_time", seller_time);
		paramMap.put("refund_state", refund_state);// 卖家审核状态改为，审核完成
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("refund_return", "refund_id", refund_id);
		/**
		 * 发送请求
		 */
		String result = update("refund_return", objectId, paramStr);
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
	 * 卖家回填物流信息
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void addLogisticsySeller() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取快递公司名称(前台提供)
		 */
		String e_name = BmobAPI.getStringValueFromJSONObject(params, "e_name");
		/**
		 * 获取快递公司编号(前台提供)
		 */
		String e_code = BmobAPI.getStringValueFromJSONObject(params, "e_code");
		/**
		 * 获取快递公司首字母(前台获取)
		 */
		String e_letter = BmobAPI.getStringValueFromJSONObject(params, "e_letter");
		/**
		 * 获取快递公司网址(前台提供)
		 */
		String e_url = BmobAPI.getStringValueFromJSONObject(params, "e_url");
		/**
		 * 封装loveyou_express需要的参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("e_name", e_name);
		paramMap.put("e_state", 1);// 状态默认为1
		paramMap.put("e_code", e_code);
		paramMap.put("e_letter", e_letter);
		paramMap.put("e_order", 2);// 默认不常用
		paramMap.put("e_url", e_url);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向loveyou_express快递公司表插入数据
		 */
		String result = insert("loveyou_express", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 获取loveyou_express表objectId
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		String objectId = (String) resultMap.get("objectId");
		/**
		 * 获取loveyou_express表信息
		 */
		String expressInfo = findOne("loveyou_express", objectId);
		Map<String, Object> expressInfoMap = BmobAPI.jsonStrToMap(expressInfo);
		Integer express_id = (Integer) expressInfoMap.get("id");
		/**
		 * 获取订单编号(前台获取)
		 */
		Integer order_id = BmobAPI.getIntegerValueFromJSONObject(params, "order_id");
		/**
		 * 获取订单公共表编号
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_order", "order_id", order_id);
		/**
		 * 获取物流编号(前台提供)
		 */
		String shipping_code = BmobAPI.getStringValueFromJSONObject(params, "shipping_code");
		/**
		 * 封装loveyou_order表需要的参数
		 */
		paramMap.clear();
		paramMap.put("shipping_code", shipping_code);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 修改loveyou_order表中物流编号字段
		 */
		result = update("loveyou_order", objectId, paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 获取loveyou_order_common表中objectId
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_order_common", "order_id", order_id);
		/**
		 * 配送时间
		 */
		long shipping_time = System.currentTimeMillis() / 1000;
		/**
		 * 封装loveyou_order_common表需要的参数
		 */
		paramMap.clear();
		paramMap.put("shipping_time", shipping_time);
		paramMap.put("shipping_express_id", express_id);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 修改loveyou_order表中物流编号字段
		 */
		result = update("loveyou_order_common", objectId, paramStr);
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
	 * 查询退单人的退单列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getPersonRefund(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		Integer skip=0;
		
		if(page!=null&&pageSize!=null&&page>1&&pageSize>0){
			
			skip=(page-1)*pageSize;
		}
		
		
		if(member_id==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"参数为空，不能查询\"}").toString());
			return;
		}
		
		String result=find("refund_return", "{\"apply_member_id\":"+member_id+"}", skip, pageSize, "-createdAt");
		
		System.out.println(result);
		if(result.indexOf("[")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"查询错误\"}").toString());
			return;
		}else{
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
		}
	}
	
	/**
	 * 后台管理退单
	 */
	@RequiresRoles("0")
	public void getAllRefundBystate(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer refund_state=BmobAPI.getIntegerValueFromJSONObject(jo, "refund_state");
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		Integer skip=0;
		
		if(page!=null&&pageSize!=null&&page>1&&pageSize>0){
			
			skip=(page-1)*pageSize;
			
		}
		
		String result = find("refund_return", "{\"refund_state\":"+refund_state+"}", skip, pageSize,"-createdAt");
		
		if(result.indexOf("[")!=-1){
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			return;
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL,result).toString());
		
		}
		
	}
	
	
	/**
	 * 管理处理退单
	 */
	@RequiresRoles("0")
	public void verifyRefundOrder(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer refund_id=BmobAPI.getIntegerValueFromJSONObject(jo, "refund_id");
		
		//退单状态 0 待审核 1服务方确认退单 3服务方确认不退单 2 管理员确认退单 5管理员确认不退单 4已完成退单
		Integer refund_state =BmobAPI.getIntegerValueFromJSONObject(jo, "refund_state");
		
		Integer order_id =BmobAPI.getIntegerValueFromJSONObject(jo, "order_id");
		
		Integer admin_member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "admin_member_id");
		
		String admin_message=BmobAPI.getStringValueFromJSONObject(jo, "admin_message");
		
		if(admin_message==null||admin_message.equals("")){
			admin_message="管理员未留言";
		}
		
		long admin_time = System.currentTimeMillis() / 1000;
		
		//此时管理员确认不退单 即 更改退单状态为5 修改订单状态为已完成 50 
		if(refund_state.equals(5)){
			
		String objectId =BmobAPI.getObjectIdById("refund_return", "refund_id", refund_id);
		
		if(objectId!=null){
			
			String result =update("refund_return", objectId, "{\"refund_state\":5,\"admin_message\":\""+admin_message+"\",\"admin_time\":"+admin_time+",\"admin_member_id\":"+admin_member_id+"}");
			
			if(result.indexOf("At")!=-1){
				
				objectId =BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
				result=update("shopnc_fworder",objectId,"{\"refund_state\":5,\"order_state\":50}");
				
				if(result.indexOf("At")!=-1){
					
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
					return;
				}
			}
				
			
		}
			
		}else if(refund_state.equals(2)){//此时管理员确认退单 即 更改退单状态为4完成退单 不修改订单状态  并将要退回的钱退回到需求方的账户 
			
			String objectId1 =BmobAPI.getObjectIdById("refund_return", "refund_id", refund_id);
			
			if(objectId1!=null){
				
				String refundObject=findOne("refund_return", objectId1);
				
				JSONObject refundJObject=JSONObject.parseObject(refundObject);
				
				Integer refund_amount=refundJObject.getInteger("refund_amount");
				
				Integer buyer_id=refundJObject.getInteger("buyer_id");
				
				//将订单的钱退回到用户余额
				String userObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", buyer_id);
				
				if(userObject!=null){
					
					String userInfo=findOne("loveyou_member", userObject);
					
					JSONObject userJObject =JSONObject.parseObject(userInfo);
					
					Integer available_predeposit=userJObject.getInteger("available_predeposit");
					
					if(available_predeposit==null||available_predeposit.equals(0)){
						
						available_predeposit=refund_amount;
					}else if(available_predeposit!=null&&available_predeposit>0){
						available_predeposit+=refund_amount;
					}
					
					//修改用户的余额
					String resultR=update("loveyou_member", userObject, "{\"available_predeposit\":"+available_predeposit+"}");
					
					if(resultR.indexOf("At")!=-1){
						
						String objectId =BmobAPI.getObjectIdById("shopnc_fworder", "order_id", order_id);
						String result=update("shopnc_fworder",objectId,"{\"refund_state\":4,\"order_state\":60}");
							
						if(result.indexOf("At")!=-1){
							
							new AccountFundsController().writerLog(admin_member_id, "管理员", JsonResult.LG_TYPE_REFUND, false, refund_amount);
							
							result =update("refund_return", objectId1, "{\"refund_state\":4,\"admin_message\":\""+admin_message+"\",\"admin_time\":"+admin_time+",\"admin_member_id\":"+admin_member_id+"}");
							
							if(result.indexOf("At")!=-1){
								renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
								return;
							}
						}
					}
					
				}
				
			}
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"请求参数有误不能操作\"}").toString());
		
	}
}
