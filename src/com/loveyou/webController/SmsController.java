package com.loveyou.webController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.loveyou.bmob.bson.BSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobInterceptor;

/**
 * @ClassName: SmsController
 * @Description: TODO(这个类的作用是:测试http 请求是否成功)
 * @author ggj
 * @date 2016-6-2 上午5:04:39
 * 
 */
@Before(BmobInterceptor.class)
public class SmsController extends Bmob {

	public void requestSms() {

		String mobileNum = "18523120427";
		String content = "你好";
		String result = "";
		// 注册bmob的代码移到BmobInterceptor 拦截器
		/*
		 * Bmob.initBmob("887295b3ab21109e27b6e5bdc6740b9d",
		 * "717c74a321efa0e5617030c1e7e008ba");
		 */
		if (Bmob.isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/requestSms";
			try {
				BSONObject bson = new BSONObject();
				bson.put("mobilePhoneNumber", mobileNum);
				bson.put("content", content);
				// 获取http对象
				conn = Bmob.connectionCommonSetting(conn, new URL(mURL), "POST");
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, bson.toString());
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + ":" + "(callFunction)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + ":" + "(callFunction)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		renderJson(result);

	}

	/**
	 * 
	 * @author ggj
	 * @Description: TODO(本方法的作用是:测试向bmob注册用户http 请求是否成功)
	 * @date 2016-6-2 上午5:04:39
	 */
	public void register() {
		String username = "ggj01711";
		String password = "123456";
		String result = "";
		if (Bmob.isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/users";
			try {
				BSONObject bson = new BSONObject();
				bson.put("username", username);
				bson.put("password", password);
				// 获取http对象
				conn = Bmob.connectionCommonSetting(conn, new URL(mURL), "POST");
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, bson.toString());
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + ":" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + ":" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		renderJson(result);

	}
	
	public void login() {
		String username = "huxiaobo";
		String password = "admin";
		String result = "";
		if (Bmob.isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/login";
			try {
				BSONObject bson = new BSONObject();
				bson.put("username", username);
				bson.put("password", password);
				// 获取http对象
				conn = Bmob.connectionCommonSetting(conn, new URL(mURL), "GET");
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, bson.toString());
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + ":" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + ":" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		renderJson(result);
		
	}

	/**
	 * 
	 * @author ggj
	 * @Description: TODO(本方法的作用是: 接收前台传的json参数)
	 * @date 2016-6-2 上午7:51:26
	 */
	public void getJson() {
		// 方法过时不用啦
		/* HttpKit.readIncommingRequestData(getRequest()); */

		try {
			// 从requst中读取json字符串
			StringBuilder json = new StringBuilder();
			BufferedReader reader = this.getRequest().getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
			reader.close();

			// 调用fastjson解析出对象
			JSONObject paramObject = JSON.parseObject(json.toString());
			String username = paramObject.getString("username");
			String password = paramObject.getString("password");
			// System.out.println("JsonKit=="+ JsonKit.toJson(json.toString()));
			System.out.println("username==" + username + "password==" + password);

			renderJson(json.toString());
		} catch (Exception e) {
			e.getMessage();
		}

	}

}
