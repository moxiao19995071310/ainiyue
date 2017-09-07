package com.loveyou.webController.login;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;

/**
 * 登陆控制器
 * 
 * @ClassName: LoginController
 * 
 * @Description: TODO(这个类的作用是：登录权限验证)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月22日 上午10:05:30
 *
 * 
 */
public class LoginController extends Bmob {

	/**
	 * 用户登录
	 */
	public void login() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		if(params==null) {
			renderJson(new JsonResult("510","{\"msg\":\"当前用户未登录或登录已过期\"}").toString());
			return;
		}
		
		/**
		 * 获取用户名(前台提供)
		 */
		String username = BmobAPI.getStringValueFromJSONObject(params, "username");
		/**
		 * 获取密码(前台提供)
		 */
		String password = BmobAPI.getStringValueFromJSONObject(params, "password");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("username", username);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		System.out.println(paramStr);
		String userInfo = findAll("loveyou_user", paramStr);
		System.out.println(userInfo);
		if (userInfo.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_USERNAME_ERROR, JsonResult.USERNAME_ERROR).toString());
			return;
		}
		/**
		 * 获取数据库密码
		 */
		userInfo = userInfo.substring(userInfo.indexOf("[") + 1, userInfo.length() - 2);
		Map<String, Object> userInfoMap = BmobAPI.jsonStrToMap(userInfo);
		String objectId = (String) userInfoMap.get("objectId");
		userInfo = findOne("loveyou_user", objectId);
		String db_password = (String) userInfoMap.get("password");

		if (password != null && password.equals(db_password)) {
			
			//获取用户权限，在user表中system_type属性
			
			Subject currenUser=SecurityUtils.getSubject();
			
			UsernamePasswordToken tokens =new UsernamePasswordToken(username,password);
			
			try {
				
				currenUser.login(tokens);
				
				
			}catch(Exception e){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"登录错误\"}").toString());
				return;
			}
			
//			userInfo = userInfo.substring(0, userInfo.length() - 1) + ",\"sessionToken\":\"" + session_token + "\"}";
//
//			Token token = new Token();
//			Map<String, Object> userInfoMap2 = BmobAPI.jsonStrToMap(userInfo);
//			token.setUid((String) userInfoMap2.get("objectId"));
//			BigDecimal sessionToken = new BigDecimal((String) userInfoMap2.get("session_token"));
//			token.setId(sessionToken.longValue());
//			token.setIp(IpHelper.getIpAddr(getRequest()));
			// 记住密码，设置 cookie 时长 1 周 = 604800 秒 【动态设置 maxAge 实现记住密码功能】
//			this.getRequest().setAttribute(SSOConfig.SSO_COOKIE_MAXAGE, 604800);
//			SSOHelper.setSSOCookie(getRequest(), getResponse(), token, true);
			/**
			 * 获取用户信息
			 */                                                  //yyp 修改
			objectId = BmobAPI.getObjectIdById("loveyou_member", "phone_number", username);
			String memberInfo = findOne("loveyou_member", objectId);
			userInfoMap = BmobAPI.jsonStrToMap(userInfo);
			userInfoMap.remove("password");
			userInfoMap.remove("session_token");
			userInfoMap.remove("sessionToken");
			userInfoMap.remove("updatedAt");
			userInfoMap.remove("objectId");
			userInfoMap.remove("username");
			userInfoMap.remove("user_id");
			userInfo = BmobAPI.mapToJSONStr(userInfoMap);
			memberInfo = memberInfo.substring(0, memberInfo.length() - 1) + ","
					+ userInfo.substring(1, userInfo.length() - 1) + "}";
			System.out.println(memberInfo);
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, memberInfo).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_PASSWORD_ERROR, JsonResult.PASSWORD_ERROR).toString());
		}
	}

	@RequiresRoles("admin")
	public void admin(){
		
		renderText("roles : admin");
	}
	
	@RequiresRoles("user")
	public void user(){
		
		renderText("roles : user");
	}
	
	public void demo(){
		
		renderText("test");
	}
	@RequiresRoles("1")
	public void system_type(){
		
		renderText("1");
	}
	
	public void demo2(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		
		System.out.println(jo.toJSONString());
		
		
		renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"msg\":\"this is demo\"}").toString());
		
	}
	
	public void logout(){
		
		Subject currentUser = SecurityUtils.getSubject();
		if(currentUser.isAuthenticated()){
			currentUser.logout();
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"msg\":\"注销成功！\"}").toString());
			return;
		}
		
	
	}
	
}
