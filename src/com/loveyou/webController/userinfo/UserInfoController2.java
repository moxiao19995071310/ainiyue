package com.loveyou.webController.userinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.JsonToBmob;

import jdk.nashorn.internal.scripts.JO;

/**
 * 个人信息模块管理控制器
 * 
 * @ClassName: UserInfoController2
 * 
 * @Description: TODO(这个类的作用是：个人信息模块管理)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月21日 上午9:46:44
 *
 * 
 */
public class UserInfoController2 extends Bmob {

	/**
	 * 实名认证
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void verifyIdentity() {
		/**
		 * 传照片的几种方案，必须按照顺序传参<br/>
		 * 1、不传照片<br/>
		 * 2、只传身份证正面照<br/>
		 * 3、传身份证正面照、身份证反面照<br/>
		 * 4、传身份证正面照、身份证反面照、手持身份证照片<br/>
		 * 上传图片(多张图片，身份证正面照、身份证反面照、手持身份证照片，按顺序不能为空)
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
		/**
		 * 获取图片地址
		 */
		String front_photo = "";
		String back_photo = "";
		String handheld_photo = "";
		if (maps.size() == 1) {
			front_photo = (String) maps.get(0).get("url");
		} else if (maps.size() == 2) {
			front_photo = (String) maps.get(0).get("url");
			back_photo = (String) maps.get(1).get("url");
		} else if (maps.size() == 3) {
			front_photo = (String) maps.get(0).get("url");
			handheld_photo = (String) maps.get(2).get("url");
		}
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = getParaToInt("member_id");
		/**
		 * 获取会员姓名(前台提供)
		 */
		String name = getPara("name");
		/**
		 * 获取会员身份证号码(前台提供)
		 */
		String idcard = getPara("idcard");
		/**
		 * 获取性别
		 */
		Integer gender = getParaToInt("gender");// 1为男，2为女
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_id", member_id);
		paramMap.put("name", name);
		paramMap.put("idcard", idcard);
		if (!"".equals(front_photo)) {
			paramMap.put("front_photo", front_photo);
		}
		if (!"".equals(back_photo)) {
			paramMap.put("back_photo", back_photo);
		}
		if (!"".equals(handheld_photo)) {
			paramMap.put("handheld_photo", handheld_photo);
		}
		paramMap.put("gender", gender);
		paramMap.put("pay_password", "");// 支付密码默认为空
		paramMap.put("phone_number", 0);// 手机号码默认为空
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 插入数据
		 */
		String result = insert("identity_authentica", paramStr);
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
	 * 判断是否实名认证个人信息
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void checkIdentity() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_id", member_id);
		//String paramStr = BmobAPI.mapToJSONStr(paramMap);
		
		/**
		 * 发送请求
		 */
		String result = findAll("identity_authentica", "{\"member_id\":"+member_id+"}");
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"isCheckIdentity\":false}").toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "{\"isCheckIdentity\":true}").toString());
		}
	}

	/**
	 * 设置支付密码
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void setPayPassword() {
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
		String objectId = BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);

		if(objectId!=null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该用户已设置支付密码\"}").toString());
			return;
		}
		/**
		 * 获取手机号码
		 */
		Long phone_number = BmobAPI.getLongValueFromJSONObject(params, "phone_number");
		/**
		 * 获取支付密码(前台提供)
		 */
		Integer payPassword = BmobAPI.getIntegerValueFromJSONObject(params, "payPassword");
		/**
		 * 支付密码加密
		 */
		String pay_password = MD5.GetMD5Code(payPassword.toString());
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_id", member_id);
		paramMap.put("pay_password", pay_password);
		paramMap.put("phone_number", phone_number);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 设置密码
		 */
		System.out.println(paramStr);
		String result = insert("identity_authentica", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**TODO
	 * 验证支付密码是否正确
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void verifyPayPassword() {
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
		String objectId = BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);

		if(objectId==null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该用户还未设置支付密码\"}").toString());
			return;
		}
		/**
		 * 获取支付密码(前台提供)
		 */
		Integer payPassword = BmobAPI.getIntegerValueFromJSONObject(params, "payPassword");
		/**
		 * 支付密码加密
		 */
		String pay_password = MD5.GetMD5Code(payPassword.toString());
		
		String identityStr=BmobAPI.findOne("identity_authentica",objectId);
		
		JSONObject identityObject=JSONObject.parseObject(identityStr);
		
		String pay_password_original=identityObject.getString("pay_password");
		
		boolean b=false;
		String verify=null;
		if(pay_password_original.equals(pay_password)){
			b=true;
			
			HttpSession session = getSession();
			session.setMaxInactiveInterval(10);
			verify=JsonToBmob.getRandomString(11);
			session.setAttribute("verify", verify);
		}
		params.clear();
		params.put("verify_state", b);
		params.put("verify", verify);
		/**
		 * 返回结果
		 */
		System.out.println(params);
		renderJson(new JsonResult(JsonResult.STATE_SUCCESS, params.toJSONString()).toString());
	}
	/**
	 * 修改支付密码
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void resetPayPassword() {
		/**
		 * 获取数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);
		/**
		 * 获取支付密码(前台提供)
		 */
		Integer payPassword = BmobAPI.getIntegerValueFromJSONObject(params, "payPassword");
		/**
		 * 支付密码加密
		 */
		String pay_password = MD5.GetMD5Code(payPassword.toString());
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pay_password", pay_password);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 设置密码
		 */
		String result = update("identity_authentica", objectId, paramStr);
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
	 * 判断是否设置支付密码
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void isSetPayPassword() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取objectId 姚永鹏修改
		 * 
		 */
		String ou = findAll("identity_authentica", "{\"member_id\":" + member_id + "}");
		if (ou.indexOf("results\":[]") != -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"isSetPayPassword\":false}").toString());
			return;
		}

		String objectId = BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);
		/**
		 * 获取支付密码
		 */
		String identityInfo = findOne("identity_authentica", objectId);
		Map<String, Object> identityInfoMap = BmobAPI.jsonStrToMap(identityInfo);
		String pay_password = (String) identityInfoMap.get("pay_password");
		/**
		 * 判断是否设置支付密码
		 */
		if ("".equals(pay_password)) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"isSetPayPassword\":false}").toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "{\"isSetPayPassword\":true}").toString());
		}
	}

	/**
	 * 上传头像
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void uploadHeadPortrait() {
		/**
		 * 上传头像图片
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
		 * 获取当前用户ID（前台提供）
		 */
		Integer member_id = getParaToInt("member_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		/**
		 * 准备需要的参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_avator", url);
		paramMap.put("image_relativelypath", fileName);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = Bmob.update("loveyou_member", objectId, paramStr);
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
	 * 修改个人信息
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateUserInfo() {
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
		 * 获取会员名称(前台提供)
		 */
		String member_name = BmobAPI.getStringValueFromJSONObject(params, "member_name");
		/**
		 * 获取一级地区编号
		 */
		Integer member_areaid = BmobAPI.getIntegerValueFromJSONObject(params, "member_areaid");
		/**
		 * 获取二级地区编号
		 */
		Integer member_cityid = BmobAPI.getIntegerValueFromJSONObject(params, "member_cityid");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_name", member_name);
		paramMap.put("member_areaid", member_areaid);
		paramMap.put("member_cityid", member_cityid);
		
		String member_areainfo=mergeAddressToAreaInfo(member_areaid, member_cityid);
		if(!member_areainfo.equals("")){
			paramMap.put("member_areainfo", member_areainfo);
		}
		
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 设置密码
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
	 * 添加银行卡
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void addBankCard() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取开户行名称(前台提供)
		 */
		String bank_name = BmobAPI.getStringValueFromJSONObject(params, "bank_name");
		/**
		 * 获取开户名(前台提供)
		 */
		String name = BmobAPI.getStringValueFromJSONObject(params, "name");
		/**
		 * 获取开户行一级地区编号(前台提供)
		 */
		Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(params, "areaid_1");
		/**
		 * 获取开户行二级地区编号(前台提供)
		 */
		Integer areaid_2 = BmobAPI.getIntegerValueFromJSONObject(params, "areaid_2");
		/**
		 * 获取开户行卡号(前台提供)
		 */
		Long card_number = BmobAPI.getLongValueFromJSONObject(params, "card_number");
		/**
		 * 获取银行预留手机号码(前台提供)
		 */
		Long phone_number = BmobAPI.getLongValueFromJSONObject(params, "phone_number");
		/**
		 * 是否设置为默认银行卡(前台)
		 */
		Integer is_default = BmobAPI.getIntegerValueFromJSONObject(params, "is_default");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("bank_name", bank_name);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("card_number", card_number);
		paramMap.put("phone_number", phone_number);
		paramMap.put("member_id", member_id);
		paramMap.put("name", name);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 插入数据
		 */
		String result = insert("loveyou_bank_card", paramStr);
		if (is_default!=null&&is_default == 1) {
			/**
			 * 返回结果
			 */
			if (result.indexOf("objectId") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
				return;
			}
			/**
			 * 解析result字符串
			 */
			Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
			String objectId = (String) resultMap.get("objectId");
			String bankCardInfo = findOne("loveyou_bank_card", objectId);
			Map<String, Object> bankCardInfoMap = BmobAPI.jsonStrToMap(bankCardInfo);
			Integer id = (Integer) bankCardInfoMap.get("id");
			/**
			 * 获取member表objectId
			 */
			objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
			result = update("loveyou_member", objectId, "{\"default_bank_card\":" + id + "}");
		}
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
	 * 修改银行卡号
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateBankCard() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取银行卡编号(前台提供)
		 */
		Integer id = BmobAPI.getIntegerValueFromJSONObject(params, "id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_bank_card", "id", id);
		
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该条记录不存在\"}").toString());
			return;
		}
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取开户行名称(前台提供)
		 */
		String bank_name = BmobAPI.getStringValueFromJSONObject(params, "bank_name");
		/**
		 * 获取开户名(前台提供)
		 */
		String name = BmobAPI.getStringValueFromJSONObject(params, "name");
		/**
		 * 获取开户行一级地区编号(前台提供)
		 */
		Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(params, "areaid_1");
		/**
		 * 获取开户行二级地区编号(前台提供)
		 */
		Integer areaid_2 = BmobAPI.getIntegerValueFromJSONObject(params, "areaid_2");
		/**
		 * 获取开户行卡号(前台提供)
		 */
		Long card_number = BmobAPI.getLongValueFromJSONObject(params, "card_number");
		/**
		 * 获取银行预留手机号码(前台提供)
		 */
		Long phone_number = BmobAPI.getLongValueFromJSONObject(params, "phone_number");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("bank_name", bank_name);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("card_number", card_number);
		paramMap.put("phone_number", phone_number);
		paramMap.put("member_id", member_id);
		paramMap.put("name", name);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 插入数据
		 */
		String result = update("loveyou_bank_card", objectId, paramStr);
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
	 * 删除银行卡号
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void deleteBankCard() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取银行卡编号(前台提供)
		 */
		Integer id = BmobAPI.getIntegerValueFromJSONObject(params, "id");
		/**
		 * 获取objectId
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		String bankcardInfo = findAll("loveyou_bank_card", paramStr);
		bankcardInfo = bankcardInfo.substring(bankcardInfo.indexOf("[") + 1, bankcardInfo.length() - 2);
		if ("".equals(bankcardInfo)) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"要删除的数据不存在!\"}").toString());
			return;
		}
		Map<String, Object> bankcardMap = BmobAPI.jsonStrToMap(bankcardInfo);
		String objectId = (String) bankcardMap.get("objectId");
		/**
		 * 删除
		 */
		String result = delete("loveyou_bank_card", objectId);
		/**
		 * 返回结果
		 */
		if (result.indexOf("ok") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 获取会员个人信息
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getMemberInfo() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取会员表objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		/**
		 * 获取会员信息
		 */
		String result = findOne("loveyou_member", objectId);
		
		//yyp添加
		
		try{
			
			JSONObject memberInfo=JSONObject.parseObject(result);
			memberInfo.remove("objectId");
			memberInfo.remove("member_id");
			memberInfo.remove("available_predeposit");
//			memberInfo.remove("user_type");
			
			result=memberInfo.toJSONString();
		}catch(Exception e){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您的邀请人信息有错误，\"}").toString());
			return;
		}
		/**
		 * 返回结果
		 */
		if (result.indexOf("createdAt") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 获取用户银行卡列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getBankCardListByMemberId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 封装参数
		 */
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("member_id", member_id);
		String paramSr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取会员所有银行卡
		 */
		String result = findAll("loveyou_bank_card", paramSr);
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
	 * 合并地址创建成area_info字段
	 * @param areaId
	 * 省级ID
	 * @param cityId
	 * 城市ID
	 * @return
	 * 	user_info 包括省，市，区/县
	 */
	
	private  String mergeAddressToAreaInfo(int areaId,int cityId){
		
		String objectId=BmobAPI.getObjectIdById("loveyou_area", "area_id", areaId);
		String oneRow1=findOne("loveyou_area", objectId);
		JSONObject jo=JSONObject.parseObject(oneRow1);
		String address="";
		address=address+jo.getString("area_name")+"  ";
		
		String objectId1=BmobAPI.getObjectIdById("loveyou_area", "area_id", cityId);
		String oneRow2=findOne("loveyou_area", objectId1);
		jo=JSONObject.parseObject(oneRow2);
		address=address+jo.getString("area_name");
		
		return address;
	}
}
