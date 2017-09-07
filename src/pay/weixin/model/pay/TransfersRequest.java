package pay.weixin.model.pay;

/**
 * @ClassName: TransfersRequest
 * @Description: (这个类的作用是：转账请求对象)
 * @author ggj
 * @date 2016年6月26日 下午2:25:36
 * 
 */
public class TransfersRequest {
	 
	/**
	 * 公众账号appid mch_appid 是 wx8888888888888888 String
	 * 微信分配的公众账号ID（企业号corpid即为此appId）
	 */
	private String mch_appid;
	/**
	 * 户号 mchid 是 1900000109 String(32) 微信支付分配的商户号
	 */
	private String mchid;
	/**
	 * 设备号 device_info 否 013467007045764 String(32) 微信支付分配的终端设备号
	 */
	private String device_info;
	/**
	 * 随机字符串 nonce_str 是 5K8264ILTKCH16CQ2502SI8ZNMTM67VS String(32)
	 * 随机字符串，不长于32位
	 */
	private String nonce_str;
	/**
	 * 签名 sign 是 C380BEC2BFD727A4B6845133519F3AD6 String(32) 签名，详见签名算法
	 */
	private String sign;
	/**
	 * 商户订单号 partner_trade_no 是 10000098201411111234567890 String 商户订单号，需保持唯一性
	 */
	private String partner_trade_no;
	/**
	 * 用户openid openid 是 oxTWIuGaIt6gTKsQRLau2M0yL16E String 商户appid下，某用户的openid
	 */
	private String openid;
	/**
	 * 校验用户姓名选项 check_name 是 OPTION_CHECK String 
	 * NO_CHECK：不校验真实姓名
	 * FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）
	 * OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功）
	 */
	private String check_name;
	/**
	 * 收款用户姓名 re_user_name 可选 马花花 String 收款用户真实姓名。
	 * 如果check_name设置为FORCE_CHECK或OPTION_CHECK，则必填用户真实姓名
	 */
	private String re_user_name;
	/**
	 * 金额 amount 是 10099 int 企业付款金额，单位为分
	 */
	private String amount;
	/**
	 * 企业付款描述信息 desc 是 理赔 String 企业付款操作说明信息。必填。
	 */
	private String desc;
	/**
	 * Ip地址 spbill_create_ip 是 192.168.0.1 String(32) 调用接口的机器Ip地址
	 */
	private String spbill_create_ip;
	
	/**
	 * 构建转账请求基本参数
	 * @param mch_appid
	 * @param mchid
	 * @param nonce_str
	 * @param check_name
	 */

	 


	public String getMch_appid() {
		return mch_appid;
	}


    // sign 是动态的不能初始化
	public TransfersRequest(String mch_appid, String mchid, String device_info,
			String nonce_str, String partner_trade_no, String openid,
			String check_name, String re_user_name, String amount, String desc,
			String spbill_create_ip) {
		this.mch_appid = mch_appid;
		this.mchid = mchid;
		this.device_info = device_info;
		this.nonce_str = nonce_str;
		this.partner_trade_no = partner_trade_no;
		this.openid = openid;
		this.check_name = check_name;
		this.re_user_name = re_user_name;
		this.amount = amount;
		this.desc = desc;
		this.spbill_create_ip = spbill_create_ip;
	}





	public void setMch_appid(String mch_appid) {
		this.mch_appid = mch_appid;
	}


	public String getMchid() {
		return mchid;
	}


	public void setMchid(String mchid) {
		this.mchid = mchid;
	}


	public String getDevice_info() {
		return device_info;
	}


	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}


	public String getNonce_str() {
		return nonce_str;
	}


	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getPartner_trade_no() {
		return partner_trade_no;
	}


	public void setPartner_trade_no(String partner_trade_no) {
		this.partner_trade_no = partner_trade_no;
	}


	public String getOpenid() {
		return openid;
	}


	public void setOpenid(String openid) {
		this.openid = openid;
	}


	public String getCheck_name() {
		return check_name;
	}


	public void setCheck_name(String check_name) {
		this.check_name = check_name;
	}


	public String getRe_user_name() {
		return re_user_name;
	}


	public void setRe_user_name(String re_user_name) {
		this.re_user_name = re_user_name;
	}


	public String getAmount() {
		return amount;
	}


	public void setAmount(String amount) {
		this.amount = amount;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}


	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}


	@Override
	public String toString() {
		return "TransfersRequest [mch_appid=" + mch_appid + ", mchid=" + mchid
				+ ", device_info=" + device_info + ", nonce_str=" + nonce_str
				+ ", sign=" + sign + ", partner_trade_no=" + partner_trade_no
				+ ", openid=" + openid + ", check_name=" + check_name
				+ ", re_user_name=" + re_user_name + ", amount=" + amount
				+ ", desc=" + desc + ", spbill_create_ip=" + spbill_create_ip
				+ "]";
	}
	
	

}
