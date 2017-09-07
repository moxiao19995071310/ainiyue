package pay.weixin.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import me.hao0.common.date.Dates;

import org.junit.Before;
import org.junit.Test;

import pay.weixin.core.Wepay;
import pay.weixin.core.WepayBuilder;
import pay.weixin.model.order.WePayOrder;
import pay.weixin.model.pay.AppPayResponse;
import pay.weixin.model.pay.JsPayRequest;
import pay.weixin.model.pay.JsPayResponse;
import pay.weixin.model.pay.PayRequest;
import pay.weixin.model.pay.QrPayRequest;
import pay.weixin.model.pay.TransfersRequest;
import pay.weixin.model.refund.RefundApplyRequest;
import pay.weixin.model.refund.RefundApplyResponse;
import pay.weixin.model.refund.RefundQueryResponse;
import pay.weixin.util.RandomStrs;

import com.jfinal.kit.PathKit;

public class WepayTest {

	private Wepay wepay;

	// 爱你约 郭功君的 openid js支付必须先获取该用户的openid
	// FIXME
	private String openId = "o_E-7wf-PPrZfiwYqd2pNPVv3RVc";

	@Before
	public void init() throws IOException {
		Properties props = new Properties();
		InputStream in = Object.class
				.getResourceAsStream("/loveyou_weixin.properties");
		props.load(in);
		in.close();

		// Path path =Paths.get("/Users/haolin/GitHub/wepay/wepay-core/src/test/resources/apiclient_cert.p12");
		 
		//Path path = Paths.get(PathKit.getWebRootPath()+"/loveyouweixin_apiclient_cert.p12");
		 Path path = Paths.get(PathKit.getWebRootPath()+"/apiclient_cert.p12");
				
		System.out.println("WepayTest 打印证书地址===="+path);
		
		byte[] data = Files.readAllBytes(path);

		// wepay=  WepayBuilder.newBuilder("wx0667d13434a471a0", "98b140251296318bcd32cf22a5b2844e", "1348107201").certPasswd("1348107201").certs(data).build();
		wepay = WepayBuilder
				.newBuilder(props.getProperty("appId"),
						props.getProperty("appKey"), props.getProperty("mchId"))
				.certPasswd(props.getProperty("mchId")).certs(data).build(); 
    
    
	}

	/**
	 * @Title: testTransfers
	 * @Description: TODO (本方法的作用是： 测试转账)
	 * @param @throws Exception 设定文件
	 * @return void 返回类型
	 * @author ggj
	 * @date 2016年6月29日 下午12:30:51
	 * @throws
	 */
	@Test
	public void testTransfers() throws Exception {
		
		String mch_appid = wepay.getAppId();
		String mchid = wepay.getMchId();
		// 设备号
		String device_info = "013467007045764";
		String nonce_str = RandomStrs.generate(16);
		String partner_trade_no = "1116";
		String openid = "o_E-7wf-PPrZfiwYqd2pNPVv3RVc";
		String check_name = "NO_CHECK";
		String re_user_name = "郭功军";
		String amount = Integer.parseInt("100") + "";
		// 企业付款描述信息 desc
		String desc = "测试  转账 雷汉贡献的机会";
		String spbill_create_ip = "127.0.0.1";

		TransfersRequest transfersRequest = new TransfersRequest(mch_appid,
				mchid, device_info, nonce_str, partner_trade_no, openid,
				check_name, re_user_name, amount, desc, spbill_create_ip);
		wepay.Transfers().doTransfers2(transfersRequest);

	}

	/**
	 * js 支付
	 */

	@Test
	public void testJsPay() {
		JsPayRequest request = new JsPayRequest();
		request.setBody("测试订单");
		request.setClientId("127.0.0.1");
		request.setTotalFee(1);
		request.setNotifyUrl("http://www.tokeys.com/notify");
		request.setOpenId(openId);
		request.setOutTradeNo("TEST12345678js");
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		JsPayResponse resp = wepay.pay().jsPay(request);
		assertNotNull(resp);
		System.out.println(resp);
	}

	/**
	 * 二维码支付
	 */
	@Test
	public void testQrPay() {
		QrPayRequest request = new QrPayRequest();
		request.setBody("测试订单");
		request.setClientId("127.0.0.1");
		request.setTotalFee(1);
		request.setNotifyUrl("http://www.xxx.com/notify");
		// request.setOutTradeNo("TEST1122334455");
		request.setOutTradeNo("TEST1122334456");
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		String resp = wepay.pay().qrPay(request);
		assertNotNull(resp);
		System.out.println(resp);
	}

	@Test
	public void testQrPayConvert() {
		QrPayRequest request = new QrPayRequest();
		request.setBody("测试订单");
		request.setClientId("127.0.0.1");
		request.setTotalFee(1);
		request.setNotifyUrl("http://www.xxx.com/notify");
		// request.setOutTradeNo("TEST3344520");
		request.setOutTradeNo("TEST3344521");
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		String resp = wepay.pay().qrPay(request, Boolean.TRUE);
		assertNotNull(resp);
		System.out.println(resp);
	}

	@Test
	public void testAppPay() {
		PayRequest request = new PayRequest();
		request.setBody("测试订单");
		request.setClientId("127.0.0.1");
		request.setTotalFee(1);
		request.setNotifyUrl("http://www.xxx.com/notify");
		request.setOutTradeNo("TEST12345678app");
		request.setTimeStart(Dates.now("yyyyMMddHHmmss"));
		AppPayResponse resp = wepay.pay().appPay(request);
		assertNotNull(resp);
		System.out.println(resp);
	}

	/** 
	* @Title: testQueryOrderByOutTradeNo 
	* @Description:  (本方法的作用是： 根据商家订单查询是否支付成功！) 
	* @return void    返回类型 
	* @author ggj
	* @date 2016年6月30日 上午3:58:41 
	* @throws 
	*/
	@Test
	public void testQueryOrderByOutTradeNo() {
		WePayOrder order = wepay.order().queryByOutTradeNo("20160630034614798009");
		assertNotNull(order);
		System.out.println(order);
	}

	@Test
	public void testQueryOrderByTransactionId() {
		WePayOrder order = wepay.order().queryByTransactionId(
				"1000530784201510111158030445");
		assertNotNull(order);
		System.out.println(order);
	}

	@Test
	public void testCloseOrder() {
		assertTrue(wepay.order().closeOrder("TEST12345678"));
	}

	@Test
	public void testRefundApply() {
		RefundApplyRequest req = new RefundApplyRequest();
		req.setTransactionId("1003110578201512021860142525");
		req.setOutRefundNo("TEST3344520");
		req.setTotalFee(1);
		req.setRefundFee(1);
		req.setOpUserId(wepay.getMchId());

		RefundApplyResponse resp = wepay.refund().apply(req);
		assertNotNull(resp);
		System.out.println(resp);
	}

	@Test
	public void testRefundQuery() {
		RefundQueryResponse resp = wepay.refund().queryByOutTradeNo(
				"TEST3344556677");
		assertNotNull(resp);
		System.out.println(resp);

		wepay.refund().queryByOutRefundNo("TEST3344556677");
		assertNotNull(resp);
		System.out.println(resp);

		wepay.refund().queryByTransactionId("1003110578201511281803217943");
		assertNotNull(resp);
		System.out.println(resp);

		wepay.refund().queryByRefundId("2003110578201512010090200506");
		assertNotNull(resp);
		System.out.println(resp);

	}
}
