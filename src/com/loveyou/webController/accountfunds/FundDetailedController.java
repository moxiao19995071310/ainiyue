package com.loveyou.webController.accountfunds;

import java.util.ArrayList;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;

/**
 * 结算资金管理控制器
 * 
 * @ClassName: AccountFundsController
 * 
 * @Description: TODO(这个类的作用是：资金账户管理)
 * 
 * @author 姚永鹏
 * 
 * @date 2016年6月27日 上午11:35:32
 *
 * 
 */
public class FundDetailedController extends Bmob {
	/**
	 * 记录 
	 */
	public void record(){
		
		
	}
//	1默认2店家已确认3平台已审核4结算完成_enum('1','2','3','4'
	public final Integer DEFAUL=1;
	
	public final Integer CHECK_SETTLEMENT=3;
	
	public final Integer PROCESS_SETTLEMENT=4;
	
	public final Integer SELLER_CHECK=2;
	
	/**
	 * 服务需求及商品订单结算接口//开始结算
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void settlementStart(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Long ob_start_date=System.currentTimeMillis()/1000;
		
		Integer store_id=jo.getInteger("store_id");
		
		String storeObject=BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		
		JSONObject jm=new JSONObject();
		
		if(storeObject!=null){
			String storeInfo=findOne("loveyou_store", storeObject);
			
			JSONObject storeJObject =JSONObject.parseObject(storeInfo);
			
			String store_name=storeJObject.getString("store_name");
			jm.put("ob_store_name", store_name);
		}
		
		jm.put("ob_store_id", store_id);
		jm.put("ob_start_date", ob_start_date);
		jm.put("ob_pay_date", ob_start_date);
		if(store_id!=null&&store_id>0){
			
			String where="{\"store_id\":"+store_id+",\"order_state\":{\"$gte\":30,\"$lte\":50},\"xq_or_fw\":0,\"settle\":{\"$ne\":1}}";
			ArrayList<String> al=BmobAPI.getAllObjectIdById("shopnc_fworder", where);
			
			Integer order_amount=0;
			if(null!=al)
				
				for(String objectId:al){
					String json=BmobAPI.findOne("shopnc_fworder", objectId);
					jo=JSONObject.parseObject(json);
					update("shopnc_fworder",objectId,"{\"settle\":1}");
					order_amount+=jo.getInteger("order_amount");
				}
			
//			System.out.println(order_amount);
//			where="{\"store_id\":"+store_id+",\"order_state\":{\"$gte\":30},\"settle\":{\"$ne\":1}}";
//			al=BmobAPI.getAllObjectIdById("loveyou_goods", where);
//			Integer goodsOrder_amount=0;
//			if(null!=al)
//				
//				for(String objectId:al){
//					String json=BmobAPI.findOne("loveyou_goods", objectId);
//					jo=JSONObject.parseObject(json);
//					update("loveyou_goods",objectId,"{\"settle\":1}");
//					goodsOrder_amount+=jo.getInteger("order_amount");
//				}

//			System.out.println(goodsOrder_amount);
			jm.put("ob_order_totals", order_amount);
			
			if(order_amount<10){
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"该店铺的所有订单总金额小于可结算金额10\"}").toString());
				return;
			}
			jm.put("ob_state", 1);//1默认2店家已确认3平台已审核4结算完成_enum('1','2','3','4'
			//ob_commis_totals 佣金金额
			//ob_result_totals //应结金额
			//ob_shipping_totals 运费
			PropKit.use("a_little_config.txt");
			Double m=Double.parseDouble(PropKit.get("proportion"));
			
			Double ob_commis_totals=m*jm.getInteger("ob_order_totals")/100;
			Integer commis_totalsm=0;
			if(ob_commis_totals>ob_commis_totals.intValue()+0.5d){
				commis_totalsm=ob_commis_totals.intValue()+1;
			}else{
				commis_totalsm=ob_commis_totals.intValue();
			}
			
			jm.put("ob_commis_totals", commis_totalsm);
			jm.put("ob_result_totals", jm.getInteger("ob_order_totals")-commis_totalsm);
			jm.put("ob_shipping_totals", 0);
			String result=insert("loveyou_order_bill", jm.toJSONString());
			
			if(result.indexOf("At")==-1){
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
				return;
			}else{
				JSONObject jw=JSONObject.parseObject(result);
				String objectId=jw.getString("objectId");
				Long ob_end_date=System.currentTimeMillis()/1000;
				result=update("loveyou_order_bill", objectId, "{\"ob_end_date\":"+ob_end_date+"}");
				result=findOne("loveyou_order_bill",objectId);
				if(result.indexOf("At")==-1){
					renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
					return;
				}
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			}
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"该店铺为空\"}").toString());
			return;
		}
	}
	
	/**
	 * 管理员确定结算
	 */
	@RequiresRoles("0")
	public void affirmSettlement(){
		
		JSONObject ou=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer obNo=ou.getInteger("ob_no");
		
		String objectId=BmobAPI.getObjectIdById("loveyou_order_bill","ob_no",obNo);
		
		String result="";
		
		if(objectId!=null){
			
			String orderBill=findOne("loveyou_order_bill",objectId);
			
			ou=JSONObject.parseObject(orderBill);
			Integer bill_state=ou.getInteger("ob_state");
			
			if(bill_state==SELLER_CHECK){
				
				JSONObject jstate=new JSONObject();
				jstate.put("ob_state", CHECK_SETTLEMENT);
				
				result=update("loveyou_order_bill",objectId,jstate.toJSONString());
				
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
				return;
			}
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"该结算内容为空\"}").toString());
			return;
			
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"该结算单已确认结算，或没有该结算单\"}").toString());
		return;
		
	}

	/**
	 * 商家确定结算
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void sellerAffirmSettlement(){
		
		JSONObject ou=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer obNo=ou.getInteger("ob_no");
		
		String objectId=BmobAPI.getObjectIdById("loveyou_order_bill","ob_no",obNo);
		
		String result="";
		
		if(objectId!=null){
			
			String orderBill=findOne("loveyou_order_bill",objectId);
			
			ou=JSONObject.parseObject(orderBill);
			Integer bill_state=ou.getInteger("ob_state");
			
			if(bill_state==DEFAUL){
				
				JSONObject jstate=new JSONObject();
				jstate.put("ob_state", SELLER_CHECK);
				
				result=update("loveyou_order_bill",objectId,jstate.toJSONString());
				
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
				return;
			}
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"该结算内容为空\"}").toString());
			return;
			
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"该结算单已确认结算，或没有该结算单\"}").toString());
		return;
		
	}
	
	/**
	 * 服务需求订单结算接口
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void fwxqSettlement(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Long ob_start_date=System.currentTimeMillis()/1000;
		
		String order_sn=jo.getString("order_sn");
		
		JSONObject jm=new JSONObject();
		
		jm.put("ob_start_date", ob_start_date);
		jm.put("ob_pay_date", ob_start_date);
		if(order_sn!=null&&!"".equals(order_sn)){
			
			ArrayList<String> al=BmobAPI.getAllObjectIdById("shopnc_fworder", "{\"order_sn\":\""+order_sn+"\",\"order_state\":{\"$gte\":30},\"xq_or_fw\":0}");
			if(null!=al)
				for(String objectId:al){
					String json=BmobAPI.findOne("shopnc_fworder", objectId);
					jo=JSONObject.parseObject(json);
					Integer store_id=jo.getInteger("store_id");
					String store_name=jo.getString("store_name");
					Integer order_amount=jo.getInteger("order_amount");
					Integer order_id=jo.getInteger("order_id");
					jm.put("order_id", order_id);
					jm.put("ob_store_id", store_id);
					jm.put("ob_store_name", store_name);
					jm.put("ob_order_totals", order_amount);
				}
			else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":false}").toString());
			}
				
		}
		String order_id=jo.getString("order_id");
		if(order_id!=null&&!"".equals(order_id)){
			ArrayList<String> al=BmobAPI.getAllObjectIdById("shopnc_fworder", "{\"order_id\":"+order_id+"}");
			if(null!=al)
				for(String objectId:al){
					String json=BmobAPI.findOne("shopnc_fworder", objectId);
					jo=JSONObject.parseObject(json);
					jo=JSONObject.parseObject(json);
					Integer store_id=jo.getInteger("store_id");
					String store_name=jo.getString("store_name");
					Integer order_amount=jo.getInteger("order_amount");
					jm.put("order_id", order_id);
					jm.put("ob_store_id", store_id);
					jm.put("ob_store_name", store_name);
					jm.put("ob_order_totals", order_amount);
		
				}
			else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":false}").toString());
			}
		}
		jm.put("ob_state", DEFAUL);//1默认2店家已确认3平台已审核4结算完成_enum('1','2','3','4'
		//ob_commis_totals 佣金金额
		//ob_result_totals //应结金额
		//ob_shipping_totals 运费
		PropKit.use("a_little_config.txt");
		Double m=Double.parseDouble(PropKit.get("proportion"));
		Double ob_commis_totals=m*jm.getInteger("ob_order_totals")/100;
		Integer commis_totalsm=0;
		if(ob_commis_totals>ob_commis_totals.intValue()+0.5d){
			commis_totalsm=ob_commis_totals.intValue()+1;
		}else{
			commis_totalsm=ob_commis_totals.intValue();
		}
		jm.put("ob_commis_totals", commis_totalsm);
		jm.put("ob_result_totals", jm.getInteger("ob_order_totals")-commis_totalsm);
		jm.put("ob_shipping_totals", 0);
		
		String result=insert("loveyou_order_bill", jm.toJSONString());
		if(result.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		}else{
			JSONObject jw=JSONObject.parseObject(result);
			String objectId=jw.getString("objectId");
			Long ob_end_date=System.currentTimeMillis()/1000;
			result=update("loveyou_order_bill", objectId, "{\"ob_end_date\":"+ob_end_date+"}");
			if(result.indexOf("At")==-1){
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
				return;
			}
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
		}
	}
	/**
	 * 商品订单结算接口
	 */
	public void goodsSettlement(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Long ob_start_date=System.currentTimeMillis()/1000;
		
		String order_sn=jo.getString("order_sn");
		
		JSONObject jm=new JSONObject();
		
		jm.put("ob_start_date", ob_start_date);
		jm.put("ob_pay_date", ob_start_date);
		if(order_sn!=null&&!"".equals(order_sn)){
			
			ArrayList<String> al=BmobAPI.getAllObjectIdById("loveyou_order", "{\"order_sn\":\""+order_sn+"\"}");
			if(null!=al)
				for(String objectId:al){
					String json=BmobAPI.findOne("loveyou_order", objectId);
					jo=JSONObject.parseObject(json);
					Integer store_id=jo.getInteger("store_id");
					String store_name=jo.getString("store_name");
					Integer order_amount=jo.getInteger("order_amount");
					Integer order_id=jo.getInteger("order_id");
					jm.put("order_id", order_id);
					jm.put("ob_store_id", store_id);
					jm.put("ob_store_name", store_name);
					jm.put("ob_order_totals", order_amount);
				}
			else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":false}").toString());
			}
				
		}
		String order_id=jo.getString("order_id");
		if(order_id!=null&&!"".equals(order_id)){
			ArrayList<String> al=BmobAPI.getAllObjectIdById("loveyou_order", "{\"order_id\":"+order_id+"}");
			if(null!=al)
				for(String objectId:al){
					String json=BmobAPI.findOne("loveyou_order", objectId);
					jo=JSONObject.parseObject(json);
					jo=JSONObject.parseObject(json);
					Integer store_id=jo.getInteger("store_id");
					String store_name=jo.getString("store_name");
					Integer order_amount=jo.getInteger("order_amount");
					jm.put("order_id", order_id);
					jm.put("ob_store_id", store_id);
					jm.put("ob_store_name", store_name);
					jm.put("ob_order_totals", order_amount);
		
				}
			else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":false}").toString());
			}
		}
		jm.put("ob_state", DEFAUL);//1默认2店家已确认3平台已审核4结算完成_enum('1','2','3','4'
		//ob_commis_totals 佣金金额
		//ob_result_totals //应结金额
		//ob_shipping_totals 运费
		PropKit.use("a_little_config.txt");
		Double m=Double.parseDouble(PropKit.get("proportion"));
		Double ob_commis_totals=m*jm.getInteger("ob_order_totals")/100;
		Integer commis_totalsm=0;
		if(ob_commis_totals>ob_commis_totals.intValue()+0.5d){
			commis_totalsm=ob_commis_totals.intValue()+1;
		}else{
			commis_totalsm=ob_commis_totals.intValue();
		}
		jm.put("ob_commis_totals", commis_totalsm);
		jm.put("ob_result_totals", jm.getInteger("ob_order_totals")-commis_totalsm);
		jm.put("ob_shipping_totals", 0);
		
		String result=insert("loveyou_order_bill", jm.toJSONString());
		if(result.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		}else{
			JSONObject jw=JSONObject.parseObject(result);
			String objectId=jw.getString("objectId");
			Long ob_end_date=System.currentTimeMillis()/1000;
			result=update("loveyou_order_bill", objectId, "{\"ob_end_date\":"+ob_end_date+"}");
			if(result.indexOf("At")==-1){
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
				return;
			}
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
		}
	}
	/**
	 * 获取所有结算列表值
	 */
	
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllSettlementList(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
			
		Integer getWay=jo.getInteger("way");
		
		Integer page=jo.getInteger("page");
		
		Integer pageSize=jo.getInteger("pageSize");

		Integer pageNum=0;
		if(page!=null&&pageSize!=null){
			pageNum=(page-1)*pageSize;
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"页码错误参数\"}").toString());
			return;
		}
		
		String jsonStr="";

					
		String result=find("loveyou_order_bill","",pageNum,pageSize,"-createdAt");
					
				if(getWay!=null){
					if(getWay==1){
						jsonStr="{\"ob_state\":{\"$lt\":"+PROCESS_SETTLEMENT+"}}";
						System.out.println(jsonStr);
						result=find("loveyou_order_bill", jsonStr, pageNum, pageSize, "-createdAt");
						
						if(result.equals("findColumns")){
							renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此方式无结果\"}").toString());
							return;
						}
					}else if(getWay==2){
						jsonStr="{\"ob_state\":"+CHECK_SETTLEMENT+"}";
						result=find("loveyou_order_bill", jsonStr, pageNum, pageSize, "-createdAt");
						if(result.equals("findColumns")){
							renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此方式无结果\"}").toString());
							return;
						}
					}
				}
				if(result.indexOf("[")!=-1){
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
					return;
				}
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"查询失败\"}").toString());
				return;
			
	}
	
	/**
	 * 卖家查询自己的结算订单
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getSellerSettlementList(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer memberId=jo.getInteger("member_id");
		
		Integer page=jo.getInteger("page");
		
		Integer pageSize=jo.getInteger("pageSize");

		Integer pageNum=0;
		if(page!=null&&pageSize!=null){
			pageNum=(page-1)*pageSize;
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"页码错误参数\"}").toString());
			return;
		}
		
		String jsonStr="";
		if(memberId!=null&&memberId>0){
			
			String objectId=BmobAPI.getObjectIdById("loveyou_member","member_id",memberId);
			
			String result="";
			if(objectId!=null){
				
				String memberInfo=findOne("loveyou_member",objectId);
				
				JSONObject jsonInfo=JSONObject.parseObject(memberInfo);
				String phone_number=jsonInfo.getString("phone_number");
				
				Integer store_id=jsonInfo.getInteger("store_id");
				
				objectId=BmobAPI.getObjectIdById("loveyou_user", "username", phone_number);
				
				jsonStr="{\"ob_store_id\":"+store_id+"}";
				if(objectId!=null){
					result=find("loveyou_order_bill",jsonStr,pageNum,pageSize,"-createdAt");
				}
				if(result.indexOf("[")!=-1){
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
					return;
				}else{
					renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"此管理员不存在\"}").toString());
					return;
				}
			}
			if(result.indexOf("[")!=-1)
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"此管理员不存在\"}").toString());
		}

	}
	
	/**TODO 待调试
	 * process 执行结算
	 */
	@RequiresRoles("0")
	public void processSettlement(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer ob_No=Integer.parseInt(jo.get("ob_no").toString());
		
		String ObjectId=BmobAPI.getObjectIdById("loveyou_order_bill", "ob_no", ob_No);
						
		String billInfo=BmobAPI.findOne("loveyou_order_bill",ObjectId);
		JSONObject bill=JSONObject.parseObject(billInfo);
						
		Integer obState=bill.getInteger("ob_state");
						
		if(obState==CHECK_SETTLEMENT){
					
			Integer menny=bill.getInteger("ob_result_totals");
			Integer m1=menny;
							
			/**
			 * 通过店铺ID查找用户ID
			 */
			Integer storeId=bill.getInteger("ob_store_id");
					
			String storeObject=BmobAPI.getObjectIdById("loveyou_store", "store_id", storeId);
			if(storeObject==null){
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"执行结算失败，用户店铺为空，或用户店铺信息不全面\"}").toString());
					return;
			}
							
			String oneow=findOne("loveyou_store", storeObject);
							
			JSONObject oneowJo=JSONObject.parseObject(oneow);
							
			Integer memberId=oneowJo.getInteger("member_id");

			String memberObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", memberId);
							
			if(memberObject==null){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"执行结算失败，用户信息不全面\"}").toString());
				return;
			}
							
			String result=BmobAPI.findOne("loveyou_member", memberObject);
							
			if(result.indexOf("findOne")!=-1){
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
				return;
			}else{
				JSONObject ou=JSONObject.parseObject(result);
						
				Integer oldAvailable_predeposit=ou.getInteger("available_predeposit");

				if(oldAvailable_predeposit!=null&&oldAvailable_predeposit>0)
					menny+=oldAvailable_predeposit;
								
					result = update("loveyou_member", memberObject, "{\"available_predeposit\":" + menny + "}");
								
					if(result.indexOf("At")!=-1)
					update("loveyou_order_bill",ObjectId,"{\"ob_state\":4}");
								
					new AccountFundsController().writerLog(memberId,"手动到账",JsonResult.LG_TYPE_SETTLEMENT,false,m1);
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
					return;
			}
							
		}
				
		renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"此结算数据还未被管理人员同意,或已结算\"}").toString());
	}
			
		
	
	/**
	 * @author 姚永鹏，根据订单单号查询买家和卖家手机号，
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getRelation(){

		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String order_sn=jo.get("order_sn").toString();
		if(order_sn!=null&&!"".equals(order_sn)){
			
			JSONObject result=new JSONObject();
			ArrayList<String> al= BmobAPI.getAllObjectIdById("shopnc_fworder", "{\"order_sn\":\""+order_sn+"\"}");
			if(null!=al)
				if(al.size()==1)
				for(String objectId:al){
					
					String fwOrderInfo=findOne("shopnc_fworder", objectId);
					
					JSONObject fworder=JSONObject.parseObject(fwOrderInfo);
					
					Integer storeId=fworder.getInteger("store_id");
					
					Integer buyerId=fworder.getInteger("buyer_id");
					
					String sellerObject=BmobAPI.getObjectIdById("loveyou_member","store_id",storeId);
					
					String seller="";
					if(sellerObject!=null){
						seller=BmobAPI.findOne("loveyou_member", sellerObject);
						
						JSONObject sel=JSONObject.parseObject(seller);
						String sellerPhoneNum=sel.getString("phone_number");

						result.put("seller_phone_num", sellerPhoneNum);
					}
					String buyer="";
					
					String buyerObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", buyerId);
					if(buyerObject!=null){
						buyer=BmobAPI.findOne("loveyou_member", buyerObject);
						
						JSONObject sel=JSONObject.parseObject(buyer);
						String buyerPhoneNum=sel.getString("phone_number");

						result.put("buyer_phone_num", buyerPhoneNum);
					}
					
				}
				if(result.size()==2){
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result.toJSONString()).toString());
					return;
				}
			
				al= BmobAPI.getAllObjectIdById("loveyou_order", "{\"order_sn\":\""+order_sn+"\"}");
				if(null!=al)
					if(al.size()==1)
					for(String objectId:al){
						
						String goodsOrderInfo=findOne("loveyou_order", objectId);
						
						JSONObject fworder=JSONObject.parseObject(goodsOrderInfo);
						
						Integer storeId=fworder.getInteger("store_id");
						
						Integer buyerId=fworder.getInteger("buyer_id");
						
						String sellerObject=BmobAPI.getObjectIdById("loveyou_member","store_id",storeId);
						
						String seller="";
						if(sellerObject!=null){
							seller=BmobAPI.findOne("loveyou_member", sellerObject);
							
							JSONObject sel=JSONObject.parseObject(seller);
							String sellerPhoneNum=sel.getString("phone_number");

							result.put("seller_phone_num", sellerPhoneNum);
						}
						String buyer="";
						
						String buyerObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", buyerId);
						if(buyerObject!=null){
							buyer=BmobAPI.findOne("loveyou_member", buyerObject);
							
							JSONObject sel=JSONObject.parseObject(buyer);
							String buyerPhoneNum=sel.getString("phone_number");

							result.put("buyer_phone_num", buyerPhoneNum);
						}
						
					}
					if(result.size()==2){
						renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result.toJSONString()).toString());
						return;
					}

			if(result.size()!=2){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该订单不存在\"}").toString());
				return;
			}
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该订单为空，或无效\"}").toString());
		return;
	}
}
