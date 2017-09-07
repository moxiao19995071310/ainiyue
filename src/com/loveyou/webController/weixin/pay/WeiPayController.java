package com.loveyou.webController.weixin.pay;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import me.hao0.common.date.Dates;
import pay.weixin.model.pay.JsPayRequest;
import pay.weixin.model.pay.JsPayResponse;
import pay.weixin.model.pay.QrPayRequest;
import pay.weixin.test.HttpUtil;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.MakeOrderNumUtil;

/**
 * @ClassName: WeiPayController
 * @Description:(这个类的作用是：：)
 * @author ggj
 * @date 2016年6月16日 上午1:18:32
 * 
 */
public class WeiPayController extends Bmob {

	/**
	 * js支付 测试 FIXME （wepay初始化已经放到Bmob父类中 需要前端先通过获取微信openid接口获取openid传给后台 返回js
	 * H5请求支付的必须参数，必须由客户端网页方式提交给微信
	 * 
	 */
	public void testjsPay() {
		// FIXME o_E-7wf-PPrZfiwYqd2pNPVv3RVc 是郭功君爱你约服务号 的 openid
		// js支付必须先获取该用户的openid
		// final String openId = "o_E-7wf-PPrZfiwYqd2pNPVv3RVc";

		String openId = getPara("openId");
		if (null == openId) {
			openId = "o_E-7wf-PPrZfiwYqd2pNPVv3RVc";
		}

		JsPayRequest request = new JsPayRequest();
		request.setBody("测试订单");
		// jfinal-weixin1.7 里面的方法
		// String ip = IpKit.getRealIp(getRequest());
		// System.out.println("获取IP 000=====" + ip);
		request.setClientId("127.0.0.1");
		// 总金额 1代表1分钱
		request.setTotalFee(1);
		request.setNotifyUrl(notify_url);
		request.setOpenId(openId);
		// 业务系统唯一订单号
		//request.setOutTradeNo("TEST12345679js");
		request.setOutTradeNo(new MakeOrderNumUtil().makeOrderNum());
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		JsPayResponse resp = wepay.pay().jsPay(request);
		// assertNotNull(resp);
		System.out.println(resp);
		renderJson(resp);
	}

	public void jsPay() {

		JSONObject jsonObject = getparamJson();

		String openId = jsonObject.get("openId").toString();
		String notify_url = jsonObject.get("notify_url").toString();
			
		//充值还购买商品
		String body = jsonObject.get("body").toString();
		String totalfee = jsonObject.get("totalfee").toString();
		String setOutTradeNo = jsonObject.get("setOutTradeNo").toString();
		
		JsPayRequest request = new JsPayRequest();
		request.setBody(body);
		request.setClientId( super.getIpAddr(getRequest()));
		// 总金额  
		request.setTotalFee(Integer.parseInt(totalfee)*100);
		request.setNotifyUrl(notify_url);
		request.setOpenId(openId);
		
		// 业务系统唯一订单号
		request.setOutTradeNo(setOutTradeNo);
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		JsPayResponse resp = wepay.pay().jsPay(request);
		System.out.println(resp);
		
		renderJson(resp);
	}

	
	
	/**
	 * 测试二维码支付（返回二维码支付地址） 电脑端直接返回二维码网页
	 */

	public void testQrPay() {
		QrPayRequest request = new QrPayRequest();
		// String body="{'name':'爱你约服务名称2','paytype':'order'}";
		// request.setBody(body);
		request.setBody("爱你约服务名称1 ");
		// tomcat 跑的时候下面这句会报错
		// request.setClientId(IpKit.getRealIp(getRequest()));
		String ip = super.getIpAddr(getRequest());
		System.out.println("ip===" + ip);
		request.setClientId(ip);
		request.setTotalFee(1);
		request.setNotifyUrl(notify_url);
		// request.setOutTradeNo("TEST1122334456");
		request.setOutTradeNo(new MakeOrderNumUtil().makeOrderNum());
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		/* 自己定义的内容 */
		request.setAttach("支付类型订单还是充值");

		String resp = wepay.pay().qrPay(request);
		// assertNotNull(resp);
		// System.out.println(resp);
		if (getClientype()) {
			renderJson(resp);
		} else {
			// redirect(resp);
			Map<String, String> map = new HashMap<String, String>();
			map.put("url", resp);
			renderJson(map);
		}
	}

	/**
	 * @Title: qrPay @Description: (本方法的作用是：对外提供二维码支付) @return void 返回类型 @author
	 *         ggj @date 2016年6月22日 下午6:57:47 @throws
	 */
	public void qrPay() {
		QrPayRequest request = new QrPayRequest();
		JSONObject jsonObject = super.getparamJson();
		try {
			String body = jsonObject.getString("body");
			request.setBody(body);
			// renderJson("totalFee或者body 没有值"); FIXME  总金额
			//request.setTotalFee(Integer.parseInt(jsonObject.getString("totalFee")) );
 			request.setTotalFee(Integer.parseInt(jsonObject.getString("totalFee")) * 100);
		} catch (NullPointerException e) {
			System.out.println("totalFee或者body 没有值2");
			new Throwable(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println("totalFee或者body 没有值3");
			renderJson("totalFee或者body 没有值2");
		}

		request.setClientId(super.getIpAddr(getRequest()));
		request.setNotifyUrl(notify_url);
		// 业务系统唯一订单号 创建订单成功后会返回给前台，前台将订单号 商品名称 总金额传给微信支付接口（其实后台也可以通过订单号获取到）
		request.setOutTradeNo(jsonObject.getString("outTradeNo"));
		System.out.println("outTradeNo==" + jsonObject.getString("outTradeNo"));
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		/* FIXME 自己定义的内容 */
		request.setAttach(jsonObject.getString("attach"));
		
		String resp = wepay.pay().qrPay(request);
		// assertNotNull(resp);
		// redirect(resp);
		// renderJson(resp);
		Map<String, String> map = new HashMap<String, String>();

		// 可以将resp 存到订单二维中

		map.put("url", resp);
		renderJson(map);
	}

	/**
	 * 测试二维码支付（返回二维码支付地址） 手机端直接返回 二维码图片
	 */

	public void testQrPayConvert() {
		QrPayRequest request = new QrPayRequest();
		request.setBody("测试订单");
		request.setClientId("127.0.0.1");
		request.setTotalFee(1);
		request.setNotifyUrl(notify_url);
		// request.setOutTradeNo("TEST3344521");
		request.setOutTradeNo("TEST3344522");
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		String resp = wepay.pay().qrPay(request, Boolean.TRUE);
		// assertNotNull(resp);
		// System.out.println(resp);
		renderText(resp);
	}
 
	/** 
	* @Title: getOpenId 
	* @Description: TODO (本方法的作用是：) 
	* @param 
	* @return String    返回类型 
	* @author ggj
	* @date 2016年6月30日 下午10:01:09  
	* @throws 
	*/
	public  void  getOpenId() {
	       String code=getPara("code");
	       System.out.println("code---->>"+code);
	       
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
	 
			/**
			 * 封装参数
			 */
			/*第二步：通过code换取网页授权access_token获取code后，请求以下链接获取access_token：*/
			//https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
		    
			String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx0667d13434a471a0&secret=a30cf9f0d433d81a0ab92ef3a3c037d0&code="
					+ code + "&grant_type=authorization_code";
			
			 //HttpUtil.sendGet(url, "UTF-8");
		  renderJson( HttpUtil.sendGet(url, "UTF-8"));
			
			/*第三步：刷新access_token（如果需要）获取第二步的refresh_token后，请求以下链接获取access_token：  刷新 ！！！！*/
			//https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN
			
		   
			/*	第四步：拉取用户信息(需scope为 snsapi_userinfo)*/
			//https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
			 
 
	}	
 

	/**
	 * @Title: getOpenId @Description: TODO (本方法的作用是：) @param @return String
	 *         返回类型 @author ggj @date 2016年6月30日 下午10:01:09 @throws
	 */
	/*public void getOpenId() {
		String code = getPara("code");
		String codeInfo = BmobAPI.getInfoByCode(code);
		String accessToken = BmobAPI.getAccess_token(codeInfo);
		String openId = BmobAPI.getOpenId(codeInfo);
		System.out.println("accessToken:" + accessToken);
		System.out.println("openId:" + openId);
	}*/
	
	public void getTest() {
		//http://www.oschina.net/question/131681_129213
		
		// JFinal中：redirect()与 render() 方法的作用与应用
		// 在使用这2个方法是发现他们其实是不同的，调用过程与结果测试如下：
		//
		// render("/user/login.html");//---正确返回视图
		// redirect("user/login.html");//---404错误，找不到
		// redirect("user/login");//---返回login这个Action的处理结果
		//
		// 经过简单的测试发现2个方法的规律，总结如下：
		//
		// （1）redirect:参数对应于 Controller的action；执行后客户端地址栏URL有变化
		// 结论：服务器调用的Action，把Action处理结果返回给客户端
		//
		// （1）render:参数为View Path，客户端地址栏URL无变化
		// 结论：服务器直接将视图返回给客户端
		//
		// 一定要先搞清楚 redirect 的行为与概念：
		//
		// 1：redirect 是重定向，当服务端向客户端响应 redirect后，并没有提供任何view数据进行渲染，仅仅是告诉浏览器响应为
		// redirect，以及重定向的目标地址
		//
		// 2：浏览器收到服务端 redirect 过来的响应，会再次发起一个 http 请求
		//
		// 3：由于是浏览器再次发起了一个新的 http 请求，所以浏览器地址栏中的 url 会发生变化
		//
		// 4：浏览中最终得到的页面是最后这个 redirect url 请求后的页面
		//
		// 5：所以redirect("/user/login.html") 相当于你在浏览器中手动输入
		// localhost/user/login.html
		// renderText("8888888888888888888888&&&&&&&&&&&&&&&&&&&&&&&******+&&&&&&&==============%%%%%%%%%^^^^^^^^^^^^");
        // redirect(url, withQueryString); http://www.oschina.net/question/1387193_149417
		// 求 url 中可能会使用问号挂上参数，如： /user/list?page=123，那么这个 page 参数就叫做
		// queryString，当 render(url, true)
		// 时会将此参数再次带上传给下个请求，withQueryString 默认值是 false
		  redirect("http://www.tokeys.com/show_all_pay.html");
          
	}

}
