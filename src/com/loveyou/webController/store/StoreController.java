package com.loveyou.webController.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;

/**
 * 
 * @author 姚永鹏
 *
 */
public class StoreController extends BmobAPI {
	
	/**
	 * 创建一个店铺，不需要身份证信息
	 */
	public void createStore(){
		
		/**
		 * 获取店pu,logo
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap2();
		
		JSONObject jo=new JSONObject();
		
		String store_logo = null;
		int i=0;
		for (i=0;i<maps.size();i++){
			String paramtename=(String) maps.get(i).get("parametername");
			if(paramtename.equals("store_logo")){
				store_logo=(String) maps.get(i).get("url");
				jo.put("store_label", store_logo);
			}
		}
		
		/**
		 * 获取store_name
		 */
		String storeName=getPara("store_name");
		jo.put("store_name", storeName);
		
		Integer member_id=getParaToInt("member_id");
		jo.put("member_id",member_id);	
		//member_name
		/**
		 * 获取会员姓名(前台提供)
		 */
		String name = getPara("name");
		/**
		 * 获取会员身份证号码(前台提供)
		 */
		String idcard = getPara("idcard");

		jo.put("seller_name", name);
		
		jo.put("store_owner_card", idcard);
//		Integer sc_id=getParaToInt("sc_id");//店铺分类
//		jo.put("sc_id", sc_id);
		
		jo.put("store_state", 1);//店铺状态1为默认开启，(0关闭，1开启，2审核中),
		
		String store_banner=getPara("store_banner");//店铺横幅
		jo.put("store_banner", store_banner);
		String store_zy=getPara("store_zy");//主营商品
		jo.put("store_zy",store_zy);
		String description=getPara("description");//描述
		jo.put("description", description);
		
		//Integer store_sales 订单销量
		String result=insert("loveyou_store",jo.toJSONString());
		/** 获取刚创建的店铺ID
		 */
		JSONObject jm=JSONObject.parseObject(result);
		String objectId=jm.getString("objectId");
		String m=findOne("loveyou_store", objectId);
		JSONObject js=JSONObject.parseObject(m);
		Integer store_id=js.getInteger("store_id");
		
		/** 插入到member 表中
		 */
		ArrayList<String> list =getAllObjectIdById("loveyou_member", "{\"member_id\":"+member_id+"}");
		if(null!=list)
			for(String object:list){
				BmobAPI.update("loveyou_member", object, "{\"store_id\":"+store_id+"}");
			}
		else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "").toString());
			return;
		}
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result)
					.toString());
		}
	}
	
	/**
	 * 创建一个店铺，和实名认证一起提交
	 */
	public void createStoreAndVerifyIdentity(){
		
		/**
		 * 获取店pu,logo
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap2();
		
		JSONObject jo=new JSONObject();
		JSONObject person=new JSONObject();
		
		String store_logo = null,front = null,back = null,handheld = null;
		int i=0;
		for (i=0;i<maps.size();i++){
			String paramtename=(String) maps.get(i).get("parametername");
			if(paramtename.equals("store_logo")){
				store_logo=(String) maps.get(i).get("url");
				jo.put("store_label", store_logo);
			}else if(paramtename.equals("front")){
				front=(String) maps.get(i).get("url");
				person.put("front_photo", front);
			}else if(paramtename.equals("back")){
				back=(String) maps.get(i).get("url");
				person.put("back_photo", back);
			}else if(paramtename.equals("handheld")){
				handheld=(String) maps.get(i).get("url");
				person.put("handheld_photo", handheld);
			}
		}
		
		/**
		 * 获取store_name
		 */
		String storeName=getPara("store_name");
		jo.put("store_name", storeName);
		
		Integer member_id=getParaToInt("member_id");
		jo.put("member_id",member_id);	
		person.put("member_id", member_id);
		//member_name
		/**
		 * 获取会员姓名(前台提供)
		 */
		String name = getPara("name");
		person.put("name", name);
		/**
		 * 获取会员身份证号码(前台提供)
		 */
		String idcard = getPara("idcard");
		person.put("idcard", idcard);
		/**
		 * 获取性别
		 */
		Integer gender = getParaToInt("gender");//1为男，2为女
		person.put("gender", gender);
		
		jo.put("seller_name", name);
		
		jo.put("store_owner_card", idcard);
//		Integer sc_id=getParaToInt("sc_id");//店铺分类
//		jo.put("sc_id", sc_id);
		
		jo.put("store_state", 2);//店铺状态1为默认开启，(0关闭，1开启，2审核中),
		
		String store_banner=getPara("store_banner");//店铺横幅
		jo.put("store_banner", store_banner);
		String store_zy=getPara("store_zy");//主营商品
		jo.put("store_zy",store_zy==null?"":store_zy);
		String description=getPara("description");//描述
		jo.put("description", description==null?"":description);
		System.out.println(person.toJSONString());
		String result=insert("identity_authentica", person.toJSONString());
		if(result.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,result).toString());
			return;
		}
		System.out.println(jo.toJSONString());
		
		result=insert("loveyou_store", jo.toJSONString());
		
		/** 获取刚创建的店铺ID
		 */
		JSONObject jm=JSONObject.parseObject(result);
		String objectId=jm.getString("objectId");
		String m=findOne("loveyou_store", objectId);
		JSONObject js=JSONObject.parseObject(m);
		Integer store_id=js.getInteger("store_id");
		
		/** 插入到member 表中
		 */
		ArrayList<String> list =getAllObjectIdById("loveyou_member", "{\"member_id\":"+member_id+"}");
		if(null!=list)
			for(String object:list){
				BmobAPI.update("loveyou_member", object, "{\"store_id\":"+store_id+"}");
			}
		else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}

		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result)
					.toString());
		}
	}
	
	public void editStore(){
		
		/**
		 * 获取店pu,logo
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap2();
		
		JSONObject jo=new JSONObject();
		
		String store_logo = null;
		int i=0;
		if (null!=maps&&!maps.equals("")) {
			
			for (i=0;i<maps.size();i++){
				String paramtename=(String) maps.get(i).get("parametername");
				if(paramtename.equals("store_logo")){
					store_logo=(String) maps.get(i).get("url");
					jo.put("store_label", store_logo);
				}
			}
		}
		
		Integer store_id=getParaToInt("store_id");
		/**
		 * 获取store_name
		 */
		String storeName=getPara("store_name");
		jo.put("store_name", storeName);
		
		String store_banner=getPara("store_banner");//店铺横幅
		jo.put("store_banner", store_banner);
		String store_zy=getPara("store_zy");//主营商品
		jo.put("store_zy",store_zy==null?"":store_zy);
		String description=getPara("description");//描述
		jo.put("description", description==null?"":description);
		
		ArrayList<String> al=BmobAPI.getAllObjectIdById("loveyou_store", "{\"store_id\":"+store_id+"}");
		System.out.println(al);
		String result="";
		if(null!=al){
			for(String objectId:al){
				result=update("loveyou_store", objectId, jo.toJSONString());
			}
		}else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
				return;
		}
		System.out.println(result);
		//Integer store_sales 订单销量
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result)
					.toString());
		}
		
	}
	/**
	 * 创建个人店铺,不需要实名认证
	 */
	public void createPersonneStore(){
		
		/**
		 * 获取店pu,logo
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap2();
		
		JSONObject jo=new JSONObject();
		
		String store_logo = null;
		int i=0;
		for (i=0;i<maps.size();i++){
			String paramtename=(String) maps.get(i).get("parametername");
			if(paramtename.equals("store_logo")){
				store_logo=(String) maps.get(i).get("url");
				jo.put("store_label", store_logo);
			}
		}
		
		/**
		 * 获取store_name
		 */
		String storeName=getPara("store_name");
		jo.put("store_name", storeName);
		
		Integer member_id=getParaToInt("member_id");
		jo.put("member_id",member_id);	
		/**
		 * 用户已通过实名认证，从用户实名认证表中查询数据
		 */
		ArrayList<String> list =getAllObjectIdById("identity_authentica", "{\"member_id\":"+member_id+"}");
		if(null!=list)
			for(String objectId:list){
				String jsonStr=findOne("identity_authentica", objectId);
				JSONObject bu=JSONObject.parseObject(jsonStr);
				String name=bu.getString("name");
				jo.put("seller_name", name);
				String idcard=bu.getString("idcard");
				jo.put("store_owner_card", idcard);
			}
		
		//		Integer sc_id=getParaToInt("sc_id");//店铺分类
//		jo.put("sc_id", sc_id);
		
		jo.put("store_state", 2);//店铺状态1为默认开启，(0关闭，1开启，2审核中),
		
		String store_banner=getPara("store_banner");//店铺横幅
		jo.put("store_banner", store_banner);
		String store_zy=getPara("store_zy");//主营商品
		jo.put("store_zy",store_zy==null?"":store_zy);
		String description=getPara("description");//描述
		jo.put("description", description==null?"":description);
		
		System.out.println(jo.toJSONString());
		//Integer store_sales 订单销量
		String result=insert("loveyou_store",jo.toJSONString());
		/** 获取刚创建的店铺ID
		 */
		JSONObject jm=JSONObject.parseObject(result);
		String objectId=jm.getString("objectId");
		String m=findOne("loveyou_store", objectId);
		JSONObject js=JSONObject.parseObject(m);
		Integer store_id=js.getInteger("store_id");
		
		/** 插入到member 表中
		 */
		ArrayList<String> list1 =getAllObjectIdById("loveyou_member", "{\"member_id\":"+member_id+"}");
		if(null!=list1)
			for(String object:list1){
				BmobAPI.update("loveyou_member", object, "{\"store_id\":"+store_id+"}");
			}
		else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "").toString());
			return;
		}
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "{\"store_id\":"+store_id+"}")
					.toString());
		}
	}
	
	/**
	 * 创建企业店铺
	 */
	public void createCompanyStore(){
		
		/**
		 * 获取店pu,logo
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap2();
		
		JSONObject jo=new JSONObject();
		JSONObject joinin=new JSONObject();
		//store_image1 营业执照，organize_electronic组织机构电子版，
		
		
		//tax_register_num_ele 税务登记电子版  oc_licence开户许可证
		String store_logo = null,store_image1=null,organize_electronic=null,tax_register_num_ele=null,oc_licence=null;
		int i=0;
		for (i=0;i<maps.size();i++){
			String paramtename=(String) maps.get(i).get("parametername");
			if(paramtename.equals("store_logo")){
				store_logo=(String) maps.get(i).get("url");
				jo.put("store_label", store_logo);
			}else if(paramtename.equals("store_image1")){
				store_image1=(String) maps.get(i).get("url");
				joinin.put("licence_electronic", store_image1);
				jo.put("store_image1", store_image1);
			}else if(paramtename.equals("organize_electronic")){
				organize_electronic =(String) maps.get(i).get("url");
				joinin.put("organize_electronic", organize_electronic);
			}else if(paramtename.equals("oc_licence")){
				tax_register_num_ele=(String) maps.get(i).get("url");
				joinin.put("tax_register_num_ele", tax_register_num_ele);
			}else if(paramtename.equals("oc_licence")){
				oc_licence=(String) maps.get(i).get("url");
				joinin.put("oc_licence", oc_licence);
			}
						
		}
		if(store_image1==null||"".equals(store_image1)){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "").toString());
			return;
		}
		if(organize_electronic==null&tax_register_num_ele==null&oc_licence==null&store_image1!=null){
			
			joinin.put("oc_licence", oc_licence);
			joinin.put("tax_register_num_ele", tax_register_num_ele);
			joinin.put("organize_electronic", organize_electronic);
		}

		//store
		/**
		 * 获取store_name
		 */
		String storeName=getPara("store_name");
		jo.put("store_name", storeName);
		
		Integer member_id=getParaToInt("member_id");
		jo.put("member_id",member_id);	
		/**
		 * 用户已通过实名认证，从用户实名认证表中查询数据
		 */
		ArrayList<String> list =getAllObjectIdById("identity_authentica", "{\"member_id\":"+member_id+"}");
		if(null!=list)
			for(String objectId:list){
				String jsonStr=findOne("identity_authentica", objectId);
				JSONObject bu=JSONObject.parseObject(jsonStr);
				String name=bu.getString("name");
				jo.put("seller_name", name);
				String idcard=bu.getString("idcard");
				jo.put("store_owner_card", idcard);
			}
		
		//		Integer sc_id=getParaToInt("sc_id");//店铺分类
//		jo.put("sc_id", sc_id);
		
		jo.put("store_state", 2);//店铺状态1为默认开启，(0关闭，1开启，2审核中),
		
		String store_banner=getPara("store_banner");//店铺横幅
		jo.put("store_banner", store_banner==null?"":store_banner);
		
		joinin.put("member_id", member_id);
		
		String company_name=getPara("company_name");
		joinin.put("company_name", company_name);
		String company_address=getPara("company_address");
		joinin.put("company_addresss", company_address);
		String address_detail=getPara("address_detail");// 公司详细地址
		joinin.put("address_detail", address_detail);
		String contacts_name=getPara("contacts_name");//联系人姓名
		joinin.put("contacts_name", contacts_name);
		String contacts_phone=getPara("contacts_phone");//联系电话
		joinin.put("contacts_phone", contacts_phone);
		
		String licence_number=getPara("licence_number");//营业执照号
		joinin.put("licence_number", licence_number);
		String licence_start_time=getPara("licence_start_time");
		joinin.put("licence_start_time", licence_start_time);
		String licence_endtime=getPara("licence_end_time");
		joinin.put("licence_end_time", licence_endtime);//营业执照结束时间
		String business_sphere=getPara("business_sphere");//法定经营范围  
		joinin.put("business_sphere", business_sphere);
		String licence_address= getPara("licence_address");//营业执照地址
		joinin.put("licence_address", licence_address);
		joinin.put("joinin_state", 1);//店铺连接状态
		String create_licence_num=getPara("create_licence_num");//开户许可证号
		joinin.put("create_licence_num", create_licence_num);
		
		String store_zy=getPara("store_zy");//主营商品
		jo.put("store_zy",store_zy==null?"":store_zy);
		String description=getPara("description");//描述
		jo.put("description", description==null?"":description);
		
		System.out.println(jo.toJSONString());
		//Integer store_sales 订单销量
		String result=insert("loveyou_store",jo.toJSONString());
		/** 获取刚创建的店铺ID
		 */
		JSONObject jm=JSONObject.parseObject(result);
		String objectId=jm.getString("objectId");
		String m=findOne("loveyou_store", objectId);
		JSONObject js=JSONObject.parseObject(m);
		Integer store_id=js.getInteger("store_id");
		
		/** 插入到member 表中
		 */
		ArrayList<String> list1 =getAllObjectIdById("loveyou_member", "{\"member_id\":"+member_id+"}");
		if(null!=list1)
			for(String object:list1){
				BmobAPI.update("loveyou_member", object, "{\"store_id\":"+store_id+"}");
			}
		else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "").toString());
			return;
		}
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} 
		JSONObject jcost=new JSONObject();
		jcost.put("cost_store_id", store_id);
		jcost.put("cost_seller_id", member_id);
		jcost.put("cost_price", 0);
		jcost.put("cost_state", 0); //0 未结算，1已结算
		result=insert("shopnc_store_cost",jcost.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} 
		result=insert("loveyou_store_joinin", joinin.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "{\"store_id\":"+store_id+"}")
					.toString());
		}
	}
	/**
	 * 判断是否已经创建店铺
	 */
	public void isCreatedStore(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=jo.getInteger("member_id");
		
		if(member_id!=null&&member_id>0){
		
			String where="{\"member_id\":"+member_id+"}";
			ArrayList<String> list=getAllObjectIdById("loveyou_store", where);

			JSONObject resu=new JSONObject();
			if(null==list){
				resu.put("msg", false);
				renderJson(new JsonResult(JsonResult.STATE_FAIL, resu.toJSONString()).toString());
				return;
			} else {
				//TODO
				
				for(String m :list){
					
					String ou=BmobAPI.findOne("loveyou_store", m);
					
					JSONObject jod= JSONObject.parseObject(ou);
					
					Integer store_state=jod.getInteger("store_state");
					
					Integer store_id=jod.getInteger("store_id");
					
					resu.put("store_id", store_id);
					
					resu.put("store_state", store_state);
					
					resu.put("msg", true);
				}
				
				if(resu.get("msg")==null){
					
					resu.put("msg", false);
					renderJson(new JsonResult(JsonResult.STATE_FAIL, resu.toJSONString()).toString());
					return;
				}
				
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, resu.toJSONString())
						.toString());
			}
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "").toString());
		}
		
	}
	/**
	 * 修改店铺审核状态
	 */
	@RequiresRoles("0")
	public void updateStoreState(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer store_id=jo.getInteger("store_id");
		
		Integer store_state=jo.getInteger("store_state");
		
		
		
		String ObjectId=BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		
		String result="";
		
		if(ObjectId!=null&&!"".equals(ObjectId)){
			
			String jsonStr="{\"store_state\":"+store_state+"}";
			if(store_state==0){
				String verifyReason=jo.getString("verifyReason");
				jsonStr="{\"store_state\":"+store_state+",\"verifyReason\":\""+verifyReason+"\"}";
			}
			result=BmobAPI.update("loveyou_store", ObjectId, jsonStr);
		}
		
		if(result.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result)
					.toString());
		}
	}
	
	/**
	 * 查询店铺详情
	 */
	
	public void getStoreByMemberId(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=jo.getInteger("member_id");
		
		String ObjectId =BmobAPI.getObjectIdById("loveyou_store", "member_id", member_id);
		String result="";
		if(ObjectId!=null&&!"".equals(ObjectId))
		result=BmobAPI.findOne("loveyou_store", ObjectId);
		
		if(result.indexOf("objectId")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result)
					.toString());
		}
		
	}
	
	/**
	 * 根据店铺状态查询店铺
	 */
	@RequiresRoles("0")
	public void getStoreByState(){
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer page = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 获取会员编号(前台提供)
		 */
		// Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params,
		// "member_id");
		/**
		 * 获取审核状态(前台提供)
		 */
		Integer store_state = BmobAPI.getIntegerValueFromJSONObject(params, "store_state");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// paramMap.put("member_id", member_id);
		paramMap.put("store_state", store_state);
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_store", jsonStr, (page - 1) * pageSize, pageSize, "-createdAt");
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
	 * 查询所有已审核通过并确认由管理员托管的店铺
	 */
	public void getAllStore(){
		
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=Integer.parseInt(jo.get("member_id").toString());
		
			if(member_id!=null&&member_id>0){
				
				String objectId=BmobAPI.getObjectIdById("loveyou_member","member_id",member_id);
				
				if(objectId!=null){
					
					String memberInfo=findOne("loveyou_member",objectId);
					
					JSONObject jsonInfo=JSONObject.parseObject(memberInfo);
					String phone_number=jsonInfo.getString("phone_number");
					
					Integer store_id=jsonInfo.getInteger("store_id");
					
					String objectId1=BmobAPI.getObjectIdById("loveyou_user", "username", phone_number);
					
					String userInfo=findOne("loveyou_user", objectId1);
					
					JSONObject user=JSONObject.parseObject(userInfo);

					if(objectId1!=null&&user.getInteger("system_type")==0){
						
						//example 
						// {"$or":[{"wins":{"$gt":150}},{"wins":{"$lt":5}}]}
						
						String storeInfo="";
						if(store_id!=null&&store_id>0){
							storeInfo=BmobAPI.findAll("loveyou_store", "{\"$or\":[{\"istrusteeship\":true},{\"store_id\":"+store_id+"}]}");
						}else{
							storeInfo=BmobAPI.findAll("loveyou_store", "{\"istrusteeship\":true}");
						}
						
						if(storeInfo.indexOf("results\":[]")!=-1){
							
							renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"暂无托管店铺\"}").toString());
							return;
						}
						
						
						renderJson(new JsonResult(JsonResult.STATE_SUCCESS, storeInfo).toString());
						return;
						//TODO
					}
				}
			}
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"你无权限托管店铺\"}").toString());
			return;

	}
}
