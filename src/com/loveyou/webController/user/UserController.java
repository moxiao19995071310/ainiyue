package com.loveyou.webController.user;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.Token;
import com.baomidou.kisso.common.IpHelper;
import com.jfinal.kit.PropKit;
import com.loveyou.bmob.bson.BSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.JsonToBmob;
import com.loveyou.webController.common.MakeOrderNumUtil;

/**
 * 用户控制器
 * 
 * @author Hu Xiaobo
 *
 */
public class UserController extends Bmob {
 
	private static Logger log = Logger.getLogger(UserController.class);
	
//	public static  Map<String ,Object> WEIXIN_USERINFO=new HashMap<String, Object>();
	
	/*private   Map<String ,Object> weixin_userinfo=new HashMap<String, Object>();
	
	public Map getweixin_userInfo () {
		return weixin_userinfo;
	}*/
	

	/**
	 * 用户注册
	 */
	public void register() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取用户名(前台提供)
		 */
		String username = BmobAPI.getStringValueFromJSONObject(params, "username");
		/**
		 * 获取密码(前台提供)
		 */
		String password = BmobAPI.getStringValueFromJSONObject(params, "password");
		/**
		 * 生成sessionToken
		 */
		String sessionToken = new MakeOrderNumUtil().makeOrderNum();
		/**
		 * 获取用户邀请码(前段提供，用户手机号)
		 */
		String invitation_number = BmobAPI.getStringValueFromJSONObject(params, "invitation_number");
		/**
		 * 判断推荐人是否存在
		 */
		String result = findAll("loveyou_user", "{\"username\":\"" + invitation_number + "\"}");
		if (invitation_number!=null&&(!"".equals(invitation_number)) && result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"推荐人不存在\"}").toString());
			return;
		}
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("username", username);
		/**
		 * 判断用户名是否已经存在
		 */
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		String userInfo = findAll("loveyou_user", paramStr);
		if (userInfo.indexOf("objectId") > 0) {
			renderJson(new JsonResult(JsonResult.STATE_USERNAME_EXISTS, JsonResult.USERNAME_EXISTS).toString());
			return;
		}
		paramMap.put("password", password);
		paramMap.put("session_token", sessionToken);
		
		//用户类型 1为普通用户 2为未审核通过的普通用户 0为超级管理员用户
		
		paramMap.put("system_type", 2);// 用户类型默认为未审核通过
		paramMap.put("available_predeposit", 0);// 余额为0
		paramMap.put("freeze_predeposit", 0);// 冻结金额为0
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向loveyou_user表插入数据
		 */
		result = insert("loveyou_user", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") != -1) {
			/**
			 * 向loveyou_member插入数据
			 */
			paramMap.clear();
			paramMap.put("member_name", username);
			
			//yyp 添加
			paramMap.put("phone_number", username);
			// paramMap.put("member_passwd", password);
			if(invitation_number!=null&&invitation_number.length()==11)
			paramMap.put("inviteNum", invitation_number);
			paramMap.put("user_type", 0);// 用户类型，默认为0
			paramStr = BmobAPI.mapToJSONStr(paramMap);
			result = insert("loveyou_member", paramStr);
			if (result.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, JsonResult.REGISTER_FAIL).toString());
				return;
			}
			if (invitation_number!=null&&!"".equals(invitation_number)&&invitation_number.length()==11) {
				/**
				 * 修改邀请人数据
				 */
				String objectId = BmobAPI.getObjectIdById("loveyou_member", "phone_number", invitation_number);
				paramMap.clear();
				result = update("loveyou_member", objectId, "{\"invitedNum\":{\"__op\":\"Increment\",\"amount\":1}}");
			}
			/**
			 * 返回结果
			 */
			if (result.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, JsonResult.REGISTER_FAIL).toString());
				return;
			} else {
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
			}
		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, JsonResult.REGISTER_FAIL).toString());
		}
	}



	/**
	 * 锁粉用户注册
	 * 用户注册 用户通过微信锁粉后可以选择是否注册，当用户选择立即注册时调用此接口
	 * 
	 * @author 姚永鹏
	 */
	public void registerShare3() {

		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取用户名(前台提供)
		 */
		String username = BmobAPI.getStringValueFromJSONObject(params, "username");
		/**
		 * 获取密码(前台提供)
		 */
		String password = BmobAPI.getStringValueFromJSONObject(params, "password");
		
		String openid=BmobAPI.getStringValueFromJSONObject(params, "openid").trim();
		
		String inviteNum=BmobAPI.getStringValueFromJSONObject(params, "m");
		if(inviteNum==null){

			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"推荐人为空\"}").toString());
			return;

		}
		
		String result = findAll("loveyou_user", "{\"username\":\"" + inviteNum + "\"}");
		if ((!"".equals(inviteNum)) || result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"推荐人不存在\"}").toString());
			return;
		}
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("username", username);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);

		/**
		 * 发送请求
		 */
		String userInfo = findAll("loveyou_user", paramStr);

		if (userInfo.indexOf("results\":[]") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_USERNAME_EXISTS, JsonResult.USERNAME_EXISTS).toString());
			return;
		}
		/**
		 * 封装参数
		 */
		paramMap.put("password", password);
		paramMap.put("password", password);
		String sessionToken = new MakeOrderNumUtil().makeOrderNum();
		paramMap.put("session_token", sessionToken);
		paramMap.put("system_type", 1);// 用户类型默认为普通用户

		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		result = insert("loveyou_user", paramStr);
		if (result.indexOf("At") != -1) {
//			String member_json = "{\"member_name\":\"" + username + "\",\"phone_number\":\""+username+"\",\"invitedNum\":0}";
			JSONObject memberInfo=new JSONObject();
			memberInfo.put("member_name", username);
			memberInfo.put("phone_number", username);
			memberInfo.put("invitedNum", 0);
			memberInfo.put("available_predeposit", 0);
			memberInfo.put("inviteNum", inviteNum);
			if(openid!=null&&openid.length()>7)
			memberInfo.put("openid", openid);
			result = insert("loveyou_member", memberInfo.toJSONString());
			if (result.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
				return;
			}

			JSONObject jom = JSONObject.parseObject(result);
			String objectid = jom.getString("objectId");
			String jsonStr = "{\"invitedNum\":{\"__op\":\"Increment\",\"amount\":1}}";
			result = update("loveyou_member", objectid, jsonStr);
			if (result.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
				return;
			}
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, JsonResult.REGISTER_SUCCESS).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, JsonResult.REGISTER_FAIL).toString());
		}
	}

	/**
	 * 发送短信
	 */
	public void sendMessage() {
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
		try {
			/**
			 * 接收并获取参数
			 */
			List<String> paraList = new ArrayList<String>();
			paraList.add("content");
			paraList.add("mobilePhoneNumber");
			BSONObject bson = BmobAPI.getBSONByRequest(this.getRequest(), paraList);
			/**
			 * 发送请求
			 */
			conn = connectionCommonSetting(conn, new URL(BmobAPI.SEND_MESSAGE_URL), BmobAPI.METHOD_POST);
			conn.setDoOutput(true);
			conn.connect();
			printWriter(conn, bson.toString());
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			/**
			 * 返回结果
			 */
			if (result.indexOf("smsId") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			} else {
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
			}
		}
	}

	/**
	 * 注册时发送验证码
	 */
	public void sendRegisterCheckCode() {
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
		String str="";
		try {
			/**
			 * 接收并获取参数
			 */
			List<String> paraList = new ArrayList<String>();
			paraList.add("mobilePhoneNumber");

			BSONObject bson = BmobAPI.getBSONByRequest(this.getRequest(), paraList);
			bson.put("template", "爱你约");
			/**
			 * 发送请求
			 */
			
			str = Bmob.findAll("loveyou_user", "{\"username\":\"" + bson.getString("mobilePhoneNumber") + "\"}");
			if (str.indexOf("results\":[{") != -1) {
				renderJson(new JsonResult(JsonResult.STATE_USERNAME_EXISTS, JsonResult.USERNAME_EXISTS).toString());
				return;
			}
			conn = connectionCommonSetting(conn, new URL(BmobAPI.SEND_CHECKCODE_URL), BmobAPI.METHOD_POST);
			conn.setDoOutput(true);
			conn.connect();
			printWriter(conn, bson.toString());
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			/**
			 * 返回结果
			 */
			if (str.indexOf("results\":[{") != -1) {
				renderJson(new JsonResult(JsonResult.STATE_USERNAME_EXISTS, JsonResult.USERNAME_EXISTS).toString());
				return;
			}
			
			if (result.indexOf("smsId") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			} else {
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
			}
		}
	}

	public void sendCheckCode() {
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
		try {
			/**
			 * 接收并获取参数
			 */
			List<String> paraList = new ArrayList<String>();
			paraList.add("mobilePhoneNumber");
			BSONObject bson = BmobAPI.getBSONByRequest(this.getRequest(), paraList);
			bson.put("template", "爱你约");
			/**
			 * 发送请求
			 */
			conn = connectionCommonSetting(conn, new URL(BmobAPI.SEND_CHECKCODE_URL), BmobAPI.METHOD_POST);
			conn.setDoOutput(true);
			conn.connect();
			printWriter(conn, bson.toString());
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
	 * 通过旧密码重置密码
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void resetByPassword() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取手机号(前台提供)
		 */
		String username = BmobAPI.getStringValueFromJSONObject(params, "username");
		/**
		 * 获取原始密码(前台提供)
		 */
		String oldPassword = BmobAPI.getStringValueFromJSONObject(params, "oldPassword");
		/**
		 * 获取新密码(前台提供)
		 */
		String newPassword = BmobAPI.getStringValueFromJSONObject(params, "newPassword");
		/**
		 * 获取loveyou_user表中的objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_user", "username", username);
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"用户名不存在\"}").toString());
			return;
		}
		/**
		 * 获取该用户信息
		 */
		String userInfo = findOne("loveyou_user", objectId);

		System.out.println(userInfo);
		JSONObject jo=JSONObject.parseObject(userInfo);
		
		String db_password=jo.getString("password");
		/**
		 * 比对原始密码
		 */
		if (db_password != null && db_password.equals(oldPassword)) {
			/**
			 * 封装参数
			 */
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("password", newPassword);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 修改密码
			 */
			String result = update("loveyou_user", objectId, paramStr);
			if (result.indexOf("At") != -1) {
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "{\"msg\":\"update success\"}").toString());
				return;
			}else {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, JsonResult.REGISTER_FAIL).toString());
			}
		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, JsonResult.OLD_PASSWORD_ERROR).toString());
		}
	}

	/**
	 * 通过验证码重置密码
	 */
	
	public void resetByMessage() {
		
		HttpURLConnection conn = null;
		/**
		 * 定义结果常量
		 */
		String result = "";
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取密码(前台提供)
		 */
		String username= BmobAPI.getStringValueFromJSONObject(params, "username");
		
		String MOBILE = "^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$";
		if(username==null||!username.matches(MOBILE)){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您输入的手机号格式错误,请重新输入\"}").toString());
			return;
		}
		
		Integer memberId=BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		
		String password = BmobAPI.getStringValueFromJSONObject(params, "password");
		/**
		 * 获取短信验证码(前台提供)
		 */
		String smsCode = BmobAPI.getStringValueFromJSONObject(params, "smsCode");

		try {
			/**
			 * 封装参数
			 */
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("mobilePhoneNumber", username);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 发送请求
			 */
			conn = connectionCommonSetting(conn, new URL(BmobAPI.VERIFY_SMSCODE_URL + smsCode), BmobAPI.METHOD_POST);
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
		}finally{
			
			String msg = "";
			JSONObject jo=null;
			if(result!=null&&!"".equals(result)){
				jo=JSONObject.parseObject(result);
				msg=jo.getString("msg");
			}
			
			
			if ("ok".equals(msg)) {
				String objectId=BmobAPI.getObjectIdById("loveyou_member", "member_id", memberId);
				
				if(objectId!=null){
					
					String memberInfo=findOne("loveyou_member", objectId);
					
					jo=JSONObject.parseObject(memberInfo);
					
					String phone_number= jo.getString("phone_number");
					
					if(phone_number.equals(username)){

						objectId=BmobAPI.getObjectIdById("loveyou_user", "username", username);
						
						if(objectId!=null){
							
							result=update("loveyou_user", objectId, "{\"password\":\""+password+"\"}");
							renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
							return;
						}
					}
					
				}
				
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"用户信息错误不能修改密码\"}").toString());
			} else {
				jo=new JSONObject();
				jo.put("msg", "code verify errer");
				renderJson(new JsonResult(JsonResult.STATE_FAIL, jo.toJSONString()).toString());
			}	
		}
		
	}

	/**
	 * 检查验证码是否正确
	 */
	public void verifySmsCode() {
		HttpURLConnection conn = null;
		/**
		 * 定义结果常量
		 */
		String result = "";
		try {
			/**
			 * 接收参数
			 */
			JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
			/**
			 * 获取手机号(前台提供)
			 */
			String  mobilePhoneNumber = BmobAPI.getStringValueFromJSONObject(params, "mobilePhoneNumber");
			/**
			 * 获取验证码(前台提供)
			 */
			String smsCode = BmobAPI.getStringValueFromJSONObject(params, "smsCode");
			/**
			 * 封装参数
			 */
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("mobilePhoneNumber", mobilePhoneNumber);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 发送请求
			 */
			conn = connectionCommonSetting(conn, new URL(BmobAPI.VERIFY_SMSCODE_URL + smsCode), BmobAPI.METHOD_POST);
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
		}finally{
			
			String msg = "";
			JSONObject jo=null;
			if(result!=null&&!"".equals(result)){
				jo=JSONObject.parseObject(result);
				msg=jo.getString("msg");
			}
			
			
			if ("ok".equals(msg)) {
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
			} else {
				jo=new JSONObject();
				jo.put("msg", "code verify errer");
				renderJson(new JsonResult(JsonResult.STATE_FAIL, jo.toJSONString()).toString());
			}	
		}
		
		
	}

	/**
	 * 修改密码
	 */
	@Deprecated
	public void updatePassword() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取手机号(前台提供)
		 */
		String username = BmobAPI.getStringValueFromJSONObject(params, "username");
		/**
		 * 获取新密码(前台提供)
		 */
		String newPassword = BmobAPI.getStringValueFromJSONObject(params, "pwd");
		/**
		 * 获取loveyou_user表中的objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_user", "username", username);
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("password", newPassword);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 修改密码
		 */
		System.out.println(paramStr);
		String result = update("loveyou_user", objectId, paramStr);
		System.out.println(result);
		if (result.indexOf("At") != -1) {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, JsonResult.REGISTER_SUCCESS).toString());
			return;
		}
		/**
		 * 获取loveyou_member表的objectId
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_name", username);
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap.put("member_passwd", newPassword);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 修改loveyou_member中的密码
		 */
		result = update("loveyou_member", objectId, paramStr);
		if (result.indexOf("At") != -1) {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, JsonResult.REGISTER_SUCCESS).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, JsonResult.REGISTER_FAIL).toString());
		}
	}

	/**
	 * 分享注册码并返回公众号二维码
	 */
	public void registerCreateOfShare() {

		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		Integer member_id = jo.getInteger("member_id");

		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);

		if (objectId != null) {

			String json = findOne("loveyou_member", objectId);
			JSONObject jm = JSONObject.parseObject(json);
			String member_name = jm.getString("phone_number");

			PropKit.use("a_little_config.txt");
			String return_url = PropKit.get("return_register_url");

			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx0667d13434a471a0&redirect_uri="
					+ return_url+"?m="+member_name
					+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

			
			String bn = "http://qr.liantu.com/api.php?text=";

			try {
				String url1 = URLEncoder.encode(url, "UTF-8");
				String uc = bn + url1;
				
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"image\":\""+uc+"\"}" ).toString());
				return;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此用户不存在\"}").toString());
	}
	
	
	
	/**
	 * 第一步 ：微信授权获取code
	 * 微信登录获取code接口
	 * FIXME  微信授权登陆 ！！！！
	 */
	public void weixinLogin() {

		PropKit.use("a_little_config.txt");
		String return_url = PropKit.get("return_url");
		try {
			return_url = URLEncoder.encode(return_url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx0667d13434a471a0&redirect_uri="
				+ return_url
				+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

		redirect(url);
	}
	

	/** 
	 * 第二步 通过code 获取微信用户信息
	* @Title: getweixinUserInfoBycode2openId 
	* @Description:  (本方法的作用是：对外使用方法通过code 获取微信用户信息) 
	* @param 
	* @return void    返回类型 
	* @author ggj
	* @date 2016年7月9日 下午3:11:33  
	* @throws 
	*/
	public void getweixinUserInfoBycode2openId() {

		JSONObject jsonObject= super.getparamJson();
		
		Integer  member_id=Integer.parseInt(jsonObject.get("member_id").toString());		
		String code = 	jsonObject.get("code").toString();
		
		String appId = PropKit.get("appId");
		String secret = PropKit.get("AppSecret");

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?";
		url = url + "appid=" + appId + "&secret=" + secret + "&code=" + code
				+ "&grant_type=authorization_code";

		String stringjson = pay.weixin.test.HttpUtil.sendGet(url, "UTF-8");

		JSONObject jsonobject = JSONObject.parseObject(stringjson);

		if (jsonobject != null && jsonobject.get("errcode") == null) {

			log.error("getweixinUserInfoBycode2openId=====jsonobject=========="
					+ jsonobject);

			String access_token = jsonobject.getString("access_token");
			String openid = jsonobject.getString("openid");
			//获取微信用户信息
			JSONObject weixinUser = getweixinUserInfo(access_token, openid);
			weixinUser.put("member_id", member_id);
			/* weixin_userinfo=weixinUser;*/
//			WEIXIN_USERINFO=(Map)weixinUser;
			// 将微信信息保存到我们系统的数据里面！！
			// FIXME 
			weixinUser.remove("privilege");
			
			String objectId=BmobAPI.getObjectIdById("loveyou_member","member_id",member_id);
			
			if(objectId!=null){
				
				String memberInfo=findOne("loveyou_member", objectId);
				JSONObject memberI=JSONObject.parseObject(memberInfo);
				String oldopen=memberI.getString("openid");
				if(oldopen==null||"".equals(oldopen)||oldopen.length()<1){
					if(openid!=null&&!"".equals(openid)){
						String result=update("loveyou_member",objectId,"{\"openid\":\""+openid+"\"}");
						if(result.indexOf("At")==-1){
							
							renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户提现信息获取不全，不能提现\"}").toString());
							return;
						}
					}else{
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户提现信息获取不全，不能提现\"}").toString());
						return;
					}
				}

			}else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户信息不全，不能提现\"}").toString());
				return;
			}
			String weixinObject=BmobAPI.getObjectIdById("loveyou_weixinUser","openid",openid);
			
			if(weixinObject==null){
			
				String ser=insert("loveyou_weixinUser", weixinUser.toJSONString());
				
				if(ser.indexOf("At")==-1){
					
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"用户提现信息获取不全，不能提现\"}").toString());
					return;
				}
			}else if(weixinObject.length()>7){
				
				String weixinInfo=findOne("loveyou_weixinUser", weixinObject);
				
				JSONObject weixinI=JSONObject.parseObject(weixinInfo);
				
				Integer oldMember=weixinI.getInteger("member_id");
				
				if(oldMember==null||oldMember<16){
					update("loveyou_weixinUser", weixinObject, "{\"member_id\":"+member_id+"}");
				}
				
			}
			//只回显给客户端昵称和openID 和昵称
			
			JSONObject openidnickname=	new JSONObject();
			openidnickname.put("openid", weixinUser.get("openid"));
			openidnickname.put("nickname", weixinUser.get("nickname"));
			openidnickname.put("headimgurl", weixinUser.get("headimgurl"));
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,
					openidnickname.toJSONString()).toString());
			// 因为前后端分离的开发模式，直接重定向到该页面前端又拿不到数据
		    //redirect("http://www.tokeys.com/Withdraw.html");
			 

		} else {

			renderJson(new JsonResult(JsonResult.STATE_FAIL, 
				 jsonobject == null ? "{\"errcode\": 0}": jsonobject.toJSONString()).toString());
		}
	}
	
	/**
	 * 第二步：拉取微信用户信息(需scope为 snsapi_userinfo)
	 * 资料来源：http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
	 * 如果网页授权作用域为snsapi_userinfo，  则此时开发者可以通过access_token和openid拉取用户信息了。
	 * @Title: getweixinUserInfoByacctokeopenId
	 * @Description: (本方法的作用是：私有方法对内提供通过code 获取微信用户信息)
	 * @param
	 * @return void 返回类型
	 * @author ggj
	 * @date 2016年7月9日 下午2:27:22
	 * @throws
	 */
	private JSONObject  getweixinUserInfo(String access_token,String openid) {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
				+ access_token + "&openid=" + openid + "&lang=zh_CN";
		String jsono = pay.weixin.test.HttpUtil.sendGet(url, "UTF-8");
		JSONObject weixinUser = JSONObject.parseObject(jsono);
		return weixinUser;
	}
	
	//FIXME 
	@Deprecated    
	public void getAccessTokeByReturnUrl(){
		
		JSONObject ou=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		
		String code=ou.getString("code");
		PropKit.use("loveyou_weixin.properties");
		String appId = PropKit.get("appId");
		String secret=PropKit.get("AppSecret");
		
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?";
		url=url+"appid="+appId+"&secret="+secret+"&code="+
		code+"&grant_type=authorization_code";

		String jsono=pay.weixin.test.HttpUtil.sendGet(url,"UTF-8");
		
		JSONObject jo=JSONObject.parseObject(jsono);

		if(jo!=null&&jo.get("errcode")==null){
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, jo.toJSONString()).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jo==null?"{\"errcode\": 0}":jo.toJSONString()).toString());
		}
		
	}


	/**
	 *  获取微信端用户的微信账号信息通过openid和access_token
	 */
	//FIXME 
	@Deprecated    
	public void getWeixinUserInfoByOpenId(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String access_token=jo.getString("access_token");
		
		String openid=jo.getString("openid");
		String url="https://api.weixin.qq.com/sns/userinfo?access_token="
					+access_token+"&openid="+openid+"&lang=zh_CN";
		
		String jsono=pay.weixin.test.HttpUtil.sendGet(url,"UTF-8");
		
		JSONObject jw=JSONObject.parseObject(jsono);

		if(jw!=null&&jw.get("errcode")==null){
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, jw.toJSONString()).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jw==null?"{\"errcode\": 0}":jw.toJSONString()).toString());
		}
		
	}
	//锁粉
	//获取用户code并判断用户状态
	
	
	
	public void getWeixinUserInfoByCode(){
		
//		JSONObject ou=BmobAPI.getJSONObjectFromRequest(this.getRequest());
//		
//		String code=ou.getString("code");
//		
//		//获取邀请人信息
//		String phoneNum=ou.getString("m");
		
		String code =getPara("code");
		
		String phoneNum=getPara("m");
		
		PropKit.use("loveyou_weixin.properties");
		String appId = PropKit.get("appId");
		String secret=PropKit.get("AppSecret");
		
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?";
		url=url+"appid="+appId+"&secret="+secret+"&code="+
		code+"&grant_type=authorization_code";

		String jsono=pay.weixin.test.HttpUtil.sendGet(url,"UTF-8");

		JSONObject jo=JSONObject.parseObject(jsono);

		if(jo!=null&&jo.get("errcode")==null){
			
			String access_token=jo.getString("access_token");
			
			String openid=jo.getString("openid");
			
			url="https://api.weixin.qq.com/sns/userinfo?access_token="
						+access_token+"&openid="+openid+"&lang=zh_CN";
			
			jsono=pay.weixin.test.HttpUtil.sendGet(url,"UTF-8");
			
			JSONObject jw=JSONObject.parseObject(jsono);

			String openidReal=jw.getString("openid");
			
			String object =BmobAPI.getObjectIdById("loveyou_member", "openid", openidReal);
			
			if(object==null){
				
				// 将微信信息保存到我们系统的数据里面！！
				// FIXME 
				jw.remove("privilege");

				String weixinObject=BmobAPI.getObjectIdById("loveyou_weixinUser","openid",openidReal);
				
				jw.put("inviteNum", phoneNum);
				
				if(weixinObject==null){
					
					String ser=insert("loveyou_weixinUser", jw.toJSONString());
					
					if(ser.indexOf("At")==-1){
						
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"用户信息获取不全，操作失败\"}").toString());
						return;
					}
				}else{
					
					String weixinInfo=findOne("loveyou_weixinUser", weixinObject);
					
					JSONObject weixinI=JSONObject.parseObject(weixinInfo);
					
					String oldInvite=weixinI.getString("inviteNum");
					if(oldInvite==null){
						update("loveyou_weixinUser", weixinObject, "{\"inviteNum\":\""+phoneNum+"\"}");
					}else if(oldInvite.length()==11){
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"你已经成功关注了一位好友，且只能关注一位，并且不能更改\"}").toString());
						return;
					}else {
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"关注失败\"}").toString());
						return;
					}
				}

			}else if(object.length()>7){
				
				String memberInfo=findOne("loveyou_member", object);
				
				JSONObject memberI=JSONObject.parseObject(memberInfo);
				String inviteNum=memberI.getString("inviteNum");
				if(inviteNum==null||"".equals(inviteNum)||inviteNum.length()<11){
					update("loveyou_member", object, "{\"inviteNum\":\""+phoneNum+"\"}");
				}else if(inviteNum.length()==11){
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"你已经成功关注了一位好友，且只能关注一位，并且不能更改\"}").toString());
					return;
				}else{
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"关注失败\"}").toString());
					return;
				}
			}else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"关注失败\"}").toString());
				return;
			}
			
			if(jw!=null&&jw.get("errcode")==null){
				jw.clear();
				jw.put("msg", "ok");
				
				String myself=BmobAPI.getObjectIdById("loveyou_member", "phone_number", phoneNum);

				if(myself==null||"".equals(myself)){
					
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"要关注的用户信息错误\"}").toString());
					return;
				}else{
					
					String jsonStr = "{\"invitedNum\":{\"__op\":\"Increment\",\"amount\":1}}";
					String result = update("loveyou_member", myself, jsonStr);
					if (result.indexOf("At") == -1) {
						renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"errmsg\":\"要关注的用户信息有误\"}").toString());
						return;
					}
				}
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, jw.toJSONString()).toString());
			} else {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, jw==null?"{\"errcode\": 0}":jw.toJSONString()).toString());
			}

		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jo==null?"{\"errcode\": 0}":jo.toJSONString()).toString());
		}

	}
	
	/**
	 * 创建二维码，让用户在手机微信端扫描获取code
	 */
	@Deprecated
	public void createTwoDimensionWeixinLogin() {

		PropKit.use("a_little_config.txt");
		String return_url = PropKit.get("return_url");
		try {
			return_url = URLEncoder.encode(return_url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx0667d13434a471a0&redirect_uri="
				+ return_url + "&response_type=code&scope=snsapi_userinfo&state=STATE";

		String bn = "http://qr.liantu.com/api.php?text=";

		try {
			String urlm = URLEncoder.encode(url, "UTF-8");
			redirect(bn + urlm);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 绑定微信
	 */
	public void bindingWeixin(){
		
		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");

//		Integer member_id=16;
		
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);

		if (objectId != null) {

			PropKit.use("a_little_config.txt");
			String return_url = PropKit.get("bindingWeixin_url");
			try {
				return_url = URLEncoder.encode(return_url+"?md="+member_id, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx0667d13434a471a0&redirect_uri="
					+ return_url
					+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"url\":\""+url+"\"}").toString());
			return;
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此用户不存在\"}").toString());

	}
	
	/**
	 * 执行绑定微信
	 */
	public void executeBindingWeixin(){
	
		String code =getPara("code");
		
		Integer member_id=Integer.parseInt(getPara("md"));
		
		PropKit.use("loveyou_weixin.properties");
		String appId = PropKit.get("appId");
		String secret=PropKit.get("AppSecret");
		
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?";
		url=url+"appid="+appId+"&secret="+secret+"&code="+
		code+"&grant_type=authorization_code";

		String jsono=pay.weixin.test.HttpUtil.sendGet(url,"UTF-8");

		JSONObject jo=JSONObject.parseObject(jsono);

		if(jo!=null&&jo.get("errcode")==null){
			
			String openid=jo.getString("openid");
			
			String infomember=findAll("loveyou_member","{\"openid\":\""+openid+"\"}");
			
			if(infomember.indexOf("results\":[{")!=-1){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该微信号已被绑定\"}").toString());
				return;
			}
			
			String object=BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
			
			if(object!=null&&object.length()>7){
				
				String memberInfo=findOne("loveyou_member", object);
				
				JSONObject memberI=JSONObject.parseObject(memberInfo);
				
				String oldOpenid=memberI.getString("openid");
				
				if(oldOpenid!=null&&!"".equals(oldOpenid)&&oldOpenid.length()>10){
					
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"该用户已绑定过微信号不能重复绑定\"}").toString());
					return;
				}
				if(oldOpenid==null||!"".equals(oldOpenid)||oldOpenid.length()<10){
					
					String result=update("loveyou_member",object,"{\"openid\":\""+openid+"\"}");
					if(result.indexOf("At")==-1){
						
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"错误，修改用户信息失败\"}").toString());
						return;
					}
					
				}
			}
			
			String weixinObject=BmobAPI.getObjectIdById("loveyou_weixinUser", "member_id", member_id);
			
			if(weixinObject!=null&&weixinObject.length()>7){
				
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"该用户已绑定过微信号不能重复绑定\"}").toString());
					return;
			}else if(weixinObject==null){
				
				String weixinObject2=BmobAPI.getObjectIdById("loveyou_weixinUser", "openid", openid);
				
				if(weixinObject2!=null&&weixinObject2.length()>7){
					
					String result=update("loveyou_weixinUser", weixinObject2, "{\"member_id\":"+member_id+"}");
					if(result.indexOf("At")==-1){
						
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"错误，修改用户信息失败\"}").toString());
						return;
					}
					
				}else if(weixinObject2==null){
					
					String access_token=jo.getString("access_token");
					
					url="https://api.weixin.qq.com/sns/userinfo?access_token="
								+access_token+"&openid="+openid+"&lang=zh_CN";
					
					jsono=pay.weixin.test.HttpUtil.sendGet(url,"UTF-8");
					
					JSONObject jw=JSONObject.parseObject(jsono);

					jw.remove("privilege");

					jw.put("member_id", member_id);
					
					String ser=insert("loveyou_weixinUser", jw.toJSONString());
						
					if(ser.indexOf("At")==-1){
							
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"errmsg\":\"用户信息获取不全，操作失败\"}").toString());
						return;
					}
					
				}
				
			}
			
//			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"msg\":ok}").toString());
			
			redirect("http://www.tokeys.com/logOut.html");
			return;
			
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, jo==null?"{\"errcode\": 0}":jo.toJSONString()).toString());
		}
	}
	
	public void getUserOpenid(){
		
//		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());
//
//		Integer member_id = jo.getInteger("member_id");
		Integer member_id=16;
		
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);

		if (objectId != null) {

			PropKit.use("a_little_config.txt");
			String return_url = PropKit.get("demo_url");
			try {
				return_url = URLEncoder.encode(return_url+"?md="+member_id, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx0667d13434a471a0&redirect_uri="
					+ return_url
					+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

			redirect(url);
			return;
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此用户不存在\"}").toString());

	}
	
	/**
	 * 分享二维码锁粉
	 */
	public void userShare(){
		
		JSONObject jo = BmobAPI.getJSONObjectFromRequest(this.getRequest());

		Integer member_id = jo.getInteger("member_id");

		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);

		if (objectId != null) {

			String path=null;
			try {
				path = new File(getClass().getClassLoader().getResource("").toURI()).getPath();
			} catch (URISyntaxException e2) {
				// TODO Auto-generated catch block
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"获取本机路径错误\"}").toString());
				return;
			}
			path=path.replace("\\", "/");
			path=path.replace("/WEB-INF/classes", "/upload/love_you2/");
			path.replace("\\", "/");
			
			Logger.getLogger(this.getClass()).error(path);
			File fi =new File(path+"image2"+member_id+".png");

			String urlResult="";
			String imageName="image2"+member_id+".png";
			
			if(File.separator.equals("\\")){
				try {
					urlResult="http://"+JsonToBmob.getIpAddr()+":8011";
				} catch (SocketException | UnknownHostException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

			}else{
				urlResult="http://www.tokeys.com/vx";
			}
			if(fi.exists()){
				
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"image\":\""+urlResult+"/upload/love_you2/"+imageName+"\"}").toString());
				return;
			}
			
			String json = findOne("loveyou_member", objectId);
			JSONObject jm = JSONObject.parseObject(json);
			String member_name = jm.getString("phone_number");
			
			PropKit.use("a_little_config.txt");
			String return_url = PropKit.get("return_register_url");

			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx0667d13434a471a0&redirect_uri="
					+ return_url+"?m="+member_name
					+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

			String logo="http://bmob-cdn-2167.b0.upaiyun.com/2016/07/18/220afd4d40e62fbe802596afd7badcf7.jpg";
			
			String url2="";
			try {
				url2=URLEncoder.encode(logo, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String bn = "http://qr.liantu.com/api.php?w=600&logo="+url2+"&text=";

			try {
				String url1 = URLEncoder.encode(url, "UTF-8");
				
				String uc = bn + url1;
				
				
				try {
				
					String filename="image1"+new MakeOrderNumUtil().makeOrderNum()+".png";
					
					String twoDimensionPath=path+filename;
					
					String weixinObject=BmobAPI.getObjectIdById("loveyou_weixinUser", "member_id", member_id);
					
					if(weixinObject==null){
						
						renderJson(new JsonResult("503","{\"msg\":\"请先绑定微信\"}").toString());
						return;
					}
					String weixinInfo=findOne("loveyou_weixinUser", weixinObject);
					
					JSONObject weixinI=JSONObject.parseObject(weixinInfo);
					String weixinHead=weixinI.getString("headimgurl");
					String nickname=weixinI.getString("nickname");
					
					String filename2="image1"+new MakeOrderNumUtil().makeOrderNum()+".png";
					
					try{
						JsonToBmob.download(weixinHead, filename2, path);
					}catch(IOException e){
						
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"网络请求超时，请重试\"}").toString());
						return;
					}
					
					try{
						JsonToBmob.download(uc, filename, path);
					}catch(IOException e){
						
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"网络请求超时，请重试\"}").toString());
						return;
					}
					String head=path+filename2;

					String resultImage=path+imageName;
					
					String back=path+"back/Screenshot_2016-07-18-11-07-12.jpg".replace("\\", "/");
					//TODO
					
					
					if(!new JsonToBmob().executeCompound(back, head, twoDimensionPath, resultImage, nickname)){
						
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"很抱歉，这是非常少见的错误：图片合成失败\"}").toString());
						return;
					}
					
					JsonToBmob.DeleteFolder(head);
					JsonToBmob.DeleteFolder(twoDimensionPath);
					
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"image\":\""+urlResult+"/upload/love_you2/"+imageName+"\"}").toString());
					
					return;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此用户不存在\"}").toString());

	}
	
	/**
	 * 通过手机验证码找回密码
	 */
	
	public void getUserPassword(){
		
		HttpURLConnection conn = null;
		/**
		 * 定义结果常量
		 */
		
		String result = "";
		String username="";
		try {
			/**
			 * 接收参数
			 */
			JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
			/**
			 * 获取手机号(前台提供)
			 */
			String  mobilePhoneNumber = BmobAPI.getStringValueFromJSONObject(params, "mobilePhoneNumber");
			username=mobilePhoneNumber;
			/**
			 * 获取验证码(前台提供)
			 */
			String smsCode = BmobAPI.getStringValueFromJSONObject(params, "smsCode");
			
			
			/**
			 * 封装参数
			 */
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("mobilePhoneNumber", mobilePhoneNumber);
			String paramStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 发送请求
			 */
			conn = connectionCommonSetting(conn, new URL(BmobAPI.VERIFY_SMSCODE_URL + smsCode), BmobAPI.METHOD_POST);
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
		}finally{
			
			String msg = "";
			JSONObject jo=null;
			if(result!=null&&!"".equals(result)){
				jo=JSONObject.parseObject(result);
				msg=jo.getString("msg");
			}
			
			
			if ("ok".equals(msg)) {
				
				String objectId=BmobAPI.getObjectIdById("loveyou_user", "username", username);
				
				if(objectId!=null){
					String du=findOne("loveyou_user",objectId);
					jo.clear();
					jo=JSONObject.parseObject(du);
					
					String password=jo.getString("password");
					
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS, "{\"password\":\""+password+"\"}").toString());
					return;
					
				} else {
					jo=new JSONObject();
					jo.put("msg", "code verify errer");
					renderJson(new JsonResult(JsonResult.STATE_FAIL, jo.toJSONString()).toString());
					return;
				}	
			}
		
				renderJson(new JsonResult(JsonResult.STATE_FAIL, jo.toJSONString()).toString());
		
			}
		}
}
