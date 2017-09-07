package com.loveyou.webController;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.plugin.shiro.ShiroInterceptor;
import com.jfinal.ext.plugin.shiro.ShiroPlugin;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.loveyou.TestDemo;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.accountfunds.AccountFundsController;
import com.loveyou.webController.accountfunds.FundDetailedController;
import com.loveyou.webController.common.BmobInterceptor;
import com.loveyou.webController.common.DrawUtil;
import com.loveyou.webController.common.JsonToBmob;
import com.loveyou.webController.login.LoginController;
import com.loveyou.webController.order.GoodsOrderController;
import com.loveyou.webController.order.ServiceOrderController;
import com.loveyou.webController.product.ProductController;
import com.loveyou.webController.refund.RefundController;
import com.loveyou.webController.requirement.RequirementController;
import com.loveyou.webController.service.ServiceController;
import com.loveyou.webController.store.StoreController;
import com.loveyou.webController.upload.UploadController;
import com.loveyou.webController.upload.UploadFileTest;
import com.loveyou.webController.upload.UploadMoreController;
import com.loveyou.webController.user.PersonAlbumController;
import com.loveyou.webController.user.UserController;
import com.loveyou.webController.userinfo.UserInfoController;
import com.loveyou.webController.userinfo.UserInfoController2;
import com.loveyou.webController.userinfo.UserReceiptAddress;
import com.loveyou.webController.verifyCode.VerifyCodeController;
import com.loveyou.webController.voice.VoiceController;
import com.loveyou.webController.voice.WeixinMsgController;
import com.loveyou.webController.weixin.pay.NotifiesController;
import com.loveyou.webController.weixin.pay.TransfersController;
import com.loveyou.webController.weixin.pay.WeiPayController;

/**
 * API引导式配置
 */
public class DemoConfig extends JFinalConfig {

	Routes routes;
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("a_little_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		// 配置 文件 过时的方法
		/*me.setUploadedFileSaveDirectory("love"); me.setEncoding("utf-8");*/
		me.setEncoding("utf-8");
		me.setUrlParaSeparator("utf-8");
		 
		 
	}

	/**
	 * 配置路由
	 */

	public void configRoute(Routes me) {
		System.out.println("配置路由");
		// me.add("/", IndexController.class, "/index"); //
		// 第三个参数为该Controller的视图存放路径
		// me.add("/blog", BlogController.class); // 第三个参数省略时默认与第一个参数值相同，在此即为
		// "/blog"
		
		this.routes=me;
		/**
		 * 配置登录的访问路径
		 */
		me.add("/requirement", RequirementController.class);
		me.add("/service", ServiceController.class);
		me.add("/user", UserController.class);
		me.add("/order", ServiceOrderController.class);
		me.add("/goodsOrder", GoodsOrderController.class);
		me.add("/Bmob", Bmob.class);
		me.add("/SmsController", SmsController.class);
		me.add("/UploadController", UploadController.class);
		me.add("/UploadMoreController", UploadMoreController.class);
		me.add("/uploadTest", UploadFileTest.class);
		me.add("/WeiPayController", WeiPayController.class);
		me.add("/product", ProductController.class);
		me.add("/userInfo", UserInfoController.class);
		me.add("/userInfo2", UserInfoController2.class);
		me.add("/receiptAddress", UserReceiptAddress.class);
		me.add("/NotifiesController", NotifiesController.class);
		me.add("/store", StoreController.class);
		me.add("/accountfunds", AccountFundsController.class);
		me.add("/login", LoginController.class);
		me.add("/account", AccountFundsController.class);
		me.add("/refund", RefundController.class);
		me.add("/fundDetailed", FundDetailedController.class);
		me.add("/test", TestDemo.class);
		me.add("/writeData",JsonToBmob.class);
		me.add("/TransfersController",TransfersController.class);
		me.add("/draw",DrawUtil.class);
		me.add("/verifyCode",VerifyCodeController.class);
		me.add("/voice",VoiceController.class);
		me.add("/weixinMsg",WeixinMsgController.class);
		me.add("/personAlbum",PersonAlbumController.class); // 个人相册

	}

	public static C3p0Plugin createC3p0Plugin() {
		return new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		
		//shiro 插件
		ShiroPlugin sp=new ShiroPlugin(this.routes);
		sp.setLoginUrl("/login/login");
		sp.setUnauthorizedUrl("/403.html");
		me.add(sp);
		
		// 配置C3p0数据库连接池插件
		C3p0Plugin C3p0Plugin = createC3p0Plugin();
		me.add(C3p0Plugin);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(C3p0Plugin);
		me.add(arp);

		// 所有配置在 MappingKit 中搞定

		// _MappingKit.mapping(arp);

	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.add(new BmobInterceptor());
		me.add(new ShiroInterceptor());
		// me.add(new LoginInterceptor());

	}

	/**
	 * 微信会话配置
	 */
	public void afterJFinalStart() {
		// 1.5 之后支持redis存储access_token、js_ticket，需要先启动RedisPlugin
//		ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache());
		// 1.6新增的2种初始化
//		ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache(Redis.use("weixin")));
//		ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache("weixin"));

		ApiConfig ac = new ApiConfig();
		// 配置微信 API 相关参数
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));

		/**
		 *  是否对消息进行加密，对应于微信平台的消息加解密方式：
		 *  1：true进行加密且必须配置 encodingAesKey
		 *  2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));

		/**
		 * 多个公众号时，重复调用ApiConfigKit.putApiConfig(ac)依次添加即可，第一个添加的是默认。
		 */
		ApiConfigKit.putApiConfig(ac);
	}
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {

	}

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目 运行此 main
	 * 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
 
		JFinal.start("WebContent", 8011, "/", 5);
	}
}
