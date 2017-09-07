package com.loveyou.webController.verifyCode;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.JsonToBmob;

import pay.weixin.test.HttpUtil;
import pay.weixin.util.ReadXML;

/**
 * 新的短信发送接口
 * @author yyp
 *
 */
public class VerifyCodeController extends Bmob {

	/**
	 * 发送验证码
	 */
	
	public void sendVerifyCode(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String phoneNumber=BmobAPI.getStringValueFromJSONObject(jo, "phone_number");
		
		String MOBILE = "^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$";
		
		if(phoneNumber!=null&&phoneNumber.matches(MOBILE)){
			
			String verifyCode=JsonToBmob.getVerifyNum();
			
			String url="http://api.chanzor.com/sms.aspx?action=send&account=989885&password=7f0ye2zuq6&mobile="+phoneNumber+"&content=您的验证码是"+verifyCode+"，有效期为6分钟。如非本人操作请忽略【爱你约】&sendTime=&userid=";
			
			String sendResult=HttpUtil.sendGet(url, "UTF-8");
			
			Map<String, String> m1=ReadXML.getXmlElmentValue(sendResult);
			
			/**
			 * 
			 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			 *	<returnsms>
	    	 *		<message>操作成功</message>
	    	 *		<remainpoint>125</remainpoint>
	    	 *		<returnstatus>Success</returnstatus>
	    	 *		<successCounts>1</successCounts>
	    	 *		<taskID>160727103902012789</taskID>
			 *	</returnsms>
			 */
			
			/**
			 * session
			 * // 放数据至session
			 *	setSessionAttr("loginUser", loginUser);
			 *	// 取数据于session
			 *	User loginUser = getSessionAttr("loginUser");
			 *	// 删除session中的属性
			 *	removeSessionAttr("loginUser");
			 *	// 得到HttpSession
			 *	HttpSession session = getSession();
			 */
			
			if(m1.get("message").equals("操作成功")&&m1.get("returnstatus").equals("Success")&&m1.get("successCounts").equals("1")){
				
				//将验证码设置到session
//				setSessionAttr("verifyCode", verifyCode);
//				setMaxInactiveInterval();
				HttpSession session = getSession();
				session.setMaxInactiveInterval(6*60);
				session.setAttribute("verifyCode", verifyCode);
				
				
//				session.setMaxInactiveInterval(arg0);				
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"taskID\":\""+m1.get("taskID")+"\"}").toString());
				return;
			}
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"发送验证码失败，请检查参数\"}").toString());
		
		
	}
	
	/**
	 * 验证验证码
	 */
	public void judegVerifyCode(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String code=BmobAPI.getStringValueFromJSONObject(jo, "verify_code");

		jo.clear();
		
		if(code!=null&&code.matches("\\d{6}")){
			
			String verifyCode =getSessionAttr("verifyCode");
			
			System.out.println("验证时verifyCode:"+verifyCode);
			
			System.out.println("验证时code:"+code);
			if(verifyCode!=null&&code.equals(verifyCode)){
				jo.put("verifyState", 1);
				jo.put("msg", "验证码正确");
				removeSessionAttr("verifyCode");
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,jo.toJSONString()).toString());
				return;
			}
			jo.put("verifyState", 2);
			jo.put("msg", "验证码错误，请重新输入");
			renderJson(new JsonResult(JsonResult.STATE_FAIL,jo.toJSONString()).toString());
			return;
			
		}else{
			jo.put("msg", "验证码格式错误，请验证");
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,jo.toJSONString()).toString());
			return;
		}
		
	}
	
	
}
