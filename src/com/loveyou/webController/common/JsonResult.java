package com.loveyou.webController.common;

/**
 * 统一返回结果
 * 
 * @ClassName: JsonResult
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月15日 下午3:22:09
 *
 * 
 */
public class JsonResult {
	/**
	 * 成功的状态码
	 */
	public static final String STATE_SUCCESS = "200";
	/**
	 * 失败的状态码
	 */
	public static final String STATE_FAIL = "500";
	/**
	 * 用户名错误的状态码
	 */
	public static final String STATE_USERNAME_ERROR = "501";
	/**
	 * 密码错误的状态码
	 */
	public static final String STATE_PASSWORD_ERROR = "502";
	/**
	 * 用户名已存在的状态码
	 */
	public static final String STATE_USERNAME_EXISTS = "503";
	/**
	 * 逻辑错误，错误码
	 */
	public static final String STATE_LOGIC_ERROR = "505";
	/**
	 * 用户名或者密码错误的状态码
	 */
	public static final String STATE_USERNAME_OR_PASSWORD_ERROR = "504";
	/**
	 * 登录成功返回的信息
	 */
	public static final String LOGIN_SUCCESS = "{\"msg\":\"login success\"}";
	/**
	 * 用户名错误返回的信息
	 */
	public static final String USERNAME_ERROR = "{\"msg\":\"username error\"}";
	/**
	 * 密码错误返回的信息
	 */
	public static final String PASSWORD_ERROR = "{\"msg\":\"password error\"}";
	/**
	 * 用户名已存在返回的信息
	 */
	public static final String USERNAME_EXISTS = "{\"msg\":\"username exists\"}";
	/**
	 * 注册成功返回的信息
	 */
	public static final String REGISTER_SUCCESS = "{\"msg\":\"register success\"}";
	/**
	 * 注册失败返回的信息
	 */
	public static final String REGISTER_FAIL = "{\"msg\":\"register fail\"}";
	/**
	 * 用户名或密码错误返回的信息
	 */
	public static final String USERNAME_OR_PASSWORD_ERROR = "{\"msg\":\"username or password error\"}";
	/**
	 * 用户名或密码错误返回的信息
	 */
	public static final String OLD_PASSWORD_ERROR = "{\"msg\":\"old password error\"}";
	/**
	 * 余额不足
	 */
	public static final String INSUFFICIENT_BALANCE = "{\"msg\":\"Insufficient balance\"}";

//	订单状态：0(待确认)1:已付款;10:待签到;20:服务中;30:待评价;40:待打赏50已关闭;60,取消订单
	/**
	 * 待付款
	 */
	public  static  Integer ORDER_OBLIGATION=0;
	/**
	 * 已付款，等待签到
	 */
	public  static  Integer  ORDER_PAID=1;
	/**
	 * 签到
	 */
	public static Integer ORDER_SIGNIN=10;

	/**
	 * 发货中，待收货
	 */
	public static Integer ORDER_SENDING=10;
	
	/**
	 * 服务中
	 */
	public  static   Integer ORDER_SERVING=20;
	/**
	 * 已确认服务已完成，待评价| 已确认收货，待评价
	 */
	public static Integer ORDER_FINISH=30;
	/**
	 * 可选择，订单完成后是否打赏，暂不提供
	 */
	public static Integer ORDER_AWARDS=40;

	/**
	 *  已评价订单，订单完成，关闭订单
	 */
	public static Integer ORDER_OVER=50;
	/**
	 * 订单退款状态
	 */
	public static Integer ORDER_REFUND=55;
	/**
	 *  取消订单，关闭订单
	 */
	public static Integer ORDER_CLOSE=60;
	
	//即 recharge 充值，cash_pay 提现，refund 退款，order_pay 下单支付预存款， settlement 结算
	/**
	 * lg_type 用户资金明细状态 充值
	 */
	public static final String LG_TYPE_RECHARGE="recharge";
	
	/**
	 * lg_type 用户资金明细状态  提现
	 */
	public static final String LG_TYPE_CASH_PAY="cash_pay";
	
	/**
	 * lg_type 用户资金明细状态 退款
	 */
	public static final String LG_TYPE_REFUND="refund";
	
	/**
	 * lg_type 用户资金明细状态 下单支付预存款（暂只支持微信支付）购买商品
	 */
	public static final String LG_TYPE_ORDER_PAY_GOODS="order_pay_goods";
	
	/**
	 * lg_type 用户资金明细状态 下单支付预存款（暂只支持微信支付）购买服务
	 */
	public static final String LG_TYPE_ORDER_PAY_SERVICE="order_pay_service";
	
	/**
	 * lg_type 用户资金明细状态 下单支付预存款（暂只支持微信支付）发布需求
	 */
	public static final String LG_TYPE_ORDER_PAY_REQUIREMENT="order_pay_requirement";

	/**
	 * 
	 * lg_type 用户资金明细状态 结算
	 */
	public static final String LG_TYPE_SETTLEMENT="settlement";
	
	/**
	 * 退单状态 0 待审核 1服务方确认退单 3服务方确认不退单 2 管理员确认退单 5管理员确认不退单 4已完成退单
	 */
	
	/**
	 * voice_type 语音的类型为：
	 * 
	 * store 店铺
	 * 
	 * service 服务
	 * 
	 * requirement 需求
	 * 
	 * member 个人信息
	 * 
	 */
	
	/**
	 * voice_type 语音的类型为：
	 *  
	 *  store 店铺
	 */
	public static final String VOICE_TYPE_STORE="store";
	
	/**
	 * voice_type 语音的类型为：
	 * 
	 * service 服务
	 */
	public static final String VOICE_TYPE_SERVICE="service";
	
	/**
	 * voice_type 语音的类型为：
	 * 
	 * requirement
	 */
	public static final String VOICE_TYPE_REQUIREMENT="requirement";
	
	/**
	 * voice_type 语音的类型为：
	 * 
	 * member 个人信息
	 */
	public static final String VOICE_TYPE_MEMBER="member";
	
	/** TODO
	 * 消费时谁买单？
	 * 1 不用买单，第三方提供
	 * 2 消费时我买单，服务方发布服务
	 * 3消费时对方买单
	 * 4AA制
	 */
	/**
	 * 1 不用买单，第三方提供
	 */
	public static final Integer EXPENSE_THIRDPARTY=1;
	/**
	 * 2 消费时我买单，服务方发布服务
	 */
	public static final Integer EXPENSE_ME=2;
	/** 
	 * 3消费时对方买单
	 */
	public static final Integer EXPENSE_OPPOSITE_PERSON=3;
	/**
	 * 4AA制
	 */
	public static final Integer EXPENSE_AA=4;
	
	/**
	 * 状态码
	 */
	private String stateCode = STATE_SUCCESS;
	/**
	 * 内容
	 */
	private String data;

	public JsonResult(String stateCode, String data) {
		this.stateCode = stateCode;
		this.data = data;
	}

	@Override
	public String toString() {
		if (data.indexOf("{") == -1) {
			return "{\"stateCode\":" + stateCode + ",\"errorMsg\":\"" + data + "\"}";
		} else if (data.indexOf("results") != -1) {
			data = data.substring(data.indexOf("result") + 9, data.length() - 1);
			return "{\"stateCode\":" + stateCode + ",\"data\":" + data + "}";
		}
		return "{\"stateCode\":" + stateCode + ",\"data\":" + data + "}";
	}
}
