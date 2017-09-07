/**
 * 
 * Bmob移动后端云服务RestAPI工具类
 * 
 * 提供简单的RestAPI增删改查工具，可直接对表、云函数、支付订单、消息推送进行操作。
 * 使用方法：先初始化initBmob，后调用其他方法即可。
 * 具体使用方法及传参格式详见Bmob官网RestAPI开发文档。
 * http://docs.bmob.cn/restful/developdoc/index.html?menukey=develop_doc&key=develop_restful
 * 
 * @author 金鹰
 * @version V1.3.1
 * @since 2015-07-07
 * 
 */
package com.loveyou.bmob.restapi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import pay.weixin.core.Wepay;
import pay.weixin.core.WepayBuilder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;
import com.loveyou.bmob.bson.BSONObject;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.upload.UploadEntity;

import net.coobird.thumbnailator.Thumbnails;

public class Bmob extends Controller {

	private static boolean IS_INIT = false;
	private static int TIME_OUT = 10000;

	private static String STRING_EMPTY = "";
	private static String APP_ID = STRING_EMPTY;
	private static String REST_API_KEY = STRING_EMPTY;
	private static String MASTER_KEY = STRING_EMPTY;

	private static final String BMOB_APP_ID_TAG = "X-Bmob-Application-Id";
	private static final String BMOB_REST_KEY_TAG = "X-Bmob-REST-API-Key";
	private static final String BMOB_MASTER_KEY_TAG = "X-Bmob-Master-Key";
	private static final String CONTENT_TYPE_TAG = "Content-Type";
	private static final String CONTENT_TYPE_JSON = "application/json";

	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_DELETE = "DELETE";

	private static final String UTF8 = "UTF-8";
	private static final String CHAR_RISK = ":";

	public static final String MSG_NOT_FOUND = "Not Found";
	public static final String MSG_FILE_NOT_FOUND = "file Not Found";
	public static final String MSG_ERROR = "Error";
	public static final String MSG_UNREGISTERED = "Unregistered";

	/**
	 * 初始化微信支付对象，应该放到微信支付拦截器里面
	 */

	public static Wepay wepay;
	public static String appid = PropKit.get("appId");
	public static String appKey = PropKit.get("appKey");
	public static String mchId = PropKit.get("mchId");
	public static String notify_url = PropKit.get("domain") + "/v1/NotifiesController/paid";
	// static Path certp12Path = Paths.get(PropKit.get("certp12Path"));
	static Path certp12Path = Paths.get(PathKit.getWebRootPath() + "/loveyouweixin_apiclient_cert.p12");

	static {
		try {
			byte[] data = Files.readAllBytes(certp12Path);
			wepay = WepayBuilder.newBuilder(appid, appKey, mchId).certPasswd(mchId).certs(data).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static Logger log = Logger.getLogger(Bmob.class);

	/**
	 * 是否初始化Bmob
	 * 
	 * @return 初始化结果
	 */
	public static boolean isInit() {
		return IS_INIT;
	}

	/**
	 * 初始化Bmob
	 * 
	 * @param appId
	 *            填写 Application ID
	 * @param apiKey
	 *            填写 REST API Key
	 * @return 注册结果
	 */
	public static boolean initBmob(String appId, String apiKey) {
		return initBmob(appId, apiKey, 10000);
	}

	/**
	 * 初始化Bmob
	 * 
	 * @param appId
	 *            填写 Application ID
	 * @param apiKey
	 *            填写 REST API Key
	 * @param timeout
	 *            设置超时（1000~20000ms）
	 * @return 注册结果
	 */
	public static boolean initBmob(String appId, String apiKey, int timeout) {
		APP_ID = appId;
		REST_API_KEY = apiKey;
		if (!APP_ID.equals(STRING_EMPTY) && !REST_API_KEY.equals(STRING_EMPTY)) {
			IS_INIT = true;
		}
		if (timeout > 1000 && timeout < 20000) {
			TIME_OUT = timeout;
		}
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			IS_INIT = false;
		}
		return isInit();
	}

	/**
	 * 初始化Bmob Master权限
	 * 
	 * @param masterKey
	 *            填写 Master Key
	 */
	public static void initMaster(String masterKey) {
		MASTER_KEY = masterKey;
	}

	/**
	 * 查询表全部记录(最多仅查询1000条记录)
	 * 
	 * @param tableName
	 *            表名
	 * @return JSON格式结果
	 */
	public static String findAll(String tableName) {
		return find(tableName, STRING_EMPTY);
	}

	/**
	 * 条件查询表全部记录(最多仅查询1000条记录)
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @return JSON格式结果
	 */
	public static String findAll(String tableName, String where) {
		return find(tableName, where, STRING_EMPTY);
	}

	/**
	 * 查询表单条记录
	 * 
	 * @param tableName
	 *            表名
	 * @param objectId
	 *            objectId
	 * @return JSON格式结果
	 */
	public static String findOne(String tableName, String objectId) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName + "/" + objectId;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findOne)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findOne)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 查询表限定数量记录
	 * 
	 * @param tableName
	 *            表名
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, int limit) {
		return find(tableName, "{}", 0, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表限定数量记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, int limit) {
		return find(tableName, where, 0, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表限定数量记录，返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, int limit) {
		return findColumns(tableName, keys, where, 0, limit, STRING_EMPTY);
	}

	/**
	 * 查询表区间记录
	 * 
	 * @param tableName
	 *            表名
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, int skip, int limit) {
		return find(tableName, "{}", skip, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表区间记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, int skip, int limit) {
		return find(tableName, where, skip, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表区间记录,返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, int skip, int limit) {
		return findColumns(tableName, keys, where, skip, limit, STRING_EMPTY);
	}

	/**
	 * 排序查询表记录
	 * 
	 * @param tableName
	 *            表名
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String order) {
		return find(tableName, "{}", 0, 1000, order);
	}

	/**
	 * 条件排序查询表记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, String order) {
		return find(tableName, where, 0, 1000, order);
	}

	/**
	 * 条件排序查询表记录,返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, String order) {
		return findColumns(tableName, keys, where, 0, 1000, order);
	}

	/**
	 * 排序查询表限定数量记录
	 * 
	 * @param tableName
	 *            表名
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, int limit, String order) {
		return find(tableName, "{}", 0, limit, order);
	}

	/**
	 * 条件排序查询表限定数量记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, int limit, String order) {
		return find(tableName, where, 0, limit, order);
	}

	/**
	 * 条件排序查询表限定数量记录,返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, int limit, String order) {
		return findColumns(tableName, keys, where, 0, limit, order);
	}

	/**
	 * 条件排序查询表区间记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String find(String tableName, String where, int skip, int limit, String order) {
		return findColumns(tableName, STRING_EMPTY, where, skip, limit, order);
	}

	/**
	 * 条件排序查询表区间记录,返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return JSON格式结果
	 */
	public static String findColumns(String tableName, String keys, String where, int skip, int limit, String order) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			skip = skip < 0 ? 0 : skip;
			limit = limit < 0 ? 0 : limit;
			limit = limit > 1000 ? 1000 : limit;
			where = where.equals(STRING_EMPTY) ? "{}" : where;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName + "?where=" + urlEncoder(where) + "&limit="
					+ limit + "&skip=" + skip + "&order=" + order + "&keys=" + keys;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findColumns)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findColumns)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * BQL查询表记录
	 * 
	 * @param BQL
	 *            SQL语句。例如：select * from Student where name=\"张三\" limit 0,10
	 *            order by name
	 * @return JSON格式结果
	 */
	public static String findBQL(String BQL) {
		return findBQL(BQL, STRING_EMPTY);
	}

	/**
	 * BQL查询表记录
	 * 
	 * @param BQL
	 *            SQL语句。例如：select * from Student where name=? limit ?,? order by
	 *            name
	 * @param value
	 *            参数对应SQL中?以,为分隔符。例如"\"张三\",0,10"
	 * @return JSON格式结果
	 */
	public static String findBQL(String BQL, String value) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			BQL = urlEncoder(BQL) + "&values=[" + urlEncoder(value) + "]";
			String mURL = "https://api.bmob.cn/1/cloudQuery?bql=" + BQL;

			System.out.println(mURL);
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findBQL)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findBQL)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 获取服务器时间
	 * 
	 * @return
	 */
	public static String getServerTime() {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/timestamp/";
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(getServerTime)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(getServerTime)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 查询表记录数
	 * 
	 * @param tableName
	 *            表名
	 * @return 统计值
	 */
	public static int count(String tableName) {
		return count(tableName, "{}");
	}

	/**
	 * 条件查询记录数
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            查询条件(JSON格式)
	 * @return 统计值
	 */
	public static int count(String tableName, String where) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName + "?where=" + urlEncoder(where)
					+ "&count=1&limit=0";
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(count)" + e.getMessage();
				System.err.println("Warn: " + result);
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(count)" + e.getMessage();
				System.err.println("Warn: " + result);
			}
		} else {
			result = MSG_UNREGISTERED;
			System.err.println("Warn: " + result);
		}
		int count = 0;
		if (result.contains(MSG_NOT_FOUND) || result.contains(MSG_ERROR) || result.equals(MSG_UNREGISTERED)) {
			return count;
		} else {
			if (result.contains("count")) {
				count = Integer.valueOf(result.replaceAll("[^0-9]", STRING_EMPTY));
			}
		}
		return count;
	}

	/**
	 * 修改记录
	 * 
	 * @param tableName
	 *            表名
	 * @param objectId
	 *            objectId
	 * @param paramContent
	 *            JSON格式参数
	 * @return JSON格式结果
	 */
	public static String update(String tableName, String objectId, String paramContent) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName + "/" + objectId;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_PUT);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, paramContent);
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(update)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(update)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 插入记录
	 * 
	 * @param tableName
	 *            表名
	 * @param paramContent
	 *            JSON格式参数
	 * @return JSON格式结果
	 */
	public static String insert(String tableName, String paramContent) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, paramContent);
				conn.disconnect();
				result = getResultFromConnection(conn);
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(insert)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(insert)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 删除记录
	 * 
	 * @param tableName
	 *            表名
	 * @param objectId
	 *            objectId
	 * @return JSON格式结果
	 */
	public static String delete(String tableName, String objectId) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName + "/" + objectId;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_DELETE);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(delete)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(delete)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 查询支付订单
	 * 
	 * @param payId
	 *            交易编号
	 * @return JSON格式结果
	 */
	public static String findPayOrder(String payId) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/pay/" + payId;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findPayOrder)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findPayOrder)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 推送消息
	 * 
	 * @param JSON格式
	 *            data 详细使用方法参照
	 *            http://docs.bmob.cn/restful/developdoc/index.html?menukey=
	 *            develop_doc&key=develop_restful#index_消息推送简介
	 * @return JSON格式结果
	 */
	public static String pushMsg(String data) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/push";
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, data);
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(pushMsg)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(pushMsg)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 调用云端代码
	 * 
	 * @param funcName
	 *            云函数名
	 * @param paramContent
	 *            JSON格式参数
	 * @return JSON格式结果
	 */
	public static String callFunction(String funcName, String paramContent) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/functions/" + funcName;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, paramContent);
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(callFunction)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(callFunction)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 发送短信
	 * 
	 * @param mobileNum
	 *            电话号码
	 * @param content
	 *            短信内容
	 * @return JSON格式结果
	 */
	public static String requestSms(String mobileNum, String content) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/requestSms";
			try {
				BSONObject bson = new BSONObject();
				bson.put("mobilePhoneNumber", mobileNum);
				bson.put("content", mobileNum);
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, bson.toString());
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(callFunction)" + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(callFunction)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	// 这个上传文件接口将于2016.07.13正式下线,请开发者尽快升级并使用uploadFile2接口
	//TODO
	public static String uploadFile(String file) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			// 获取文件名
			String fileName = file.trim();
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

			String mURL = "https://api.bmob.cn/1/files/" + fileName;
			try {

				FileInputStream fis = new FileInputStream(file);

				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();

				// 一次读多个字节
				byte[] tempbytes = new byte[1];
				int byteread = 0;
				int i = 0;
				ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
				OutputStream os = conn.getOutputStream();
				while ((byteread = fis.read(tempbytes)) != -1) {
					os.write(tempbytes);
				}

				os.flush();
				os.close();
				fis.close();

				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_FILE_NOT_FOUND + CHAR_RISK + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	public static String uploadFile2(String file) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			// 获取文件名
			/*
			 * String fileName = file.trim(); fileName =
			 * fileName.substring(fileName.lastIndexOf("/")+1); String mURL =
			 * "https://api.bmob.cn/1/files/"+fileName;
			 */

			/*
			 * 重新设置文件名 增加时间戳_
			 */
			String fileName = file.trim();
			long time = System.currentTimeMillis();
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd
			// HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String date = sdf.format(time);
			
 			/*fileName = date + "_" + fileName.substring(fileName.lastIndexOf("\\") + 1);*/
			
			fileName = date + "_" + fileName.substring(fileName.lastIndexOf(File.separator) + 1);
			
			log.error("bmob——SC_fileName==="+fileName+"bmob_file=="+file);
			
			
			//fileName = date + "_" + "测试";
			String mURL = "https://api.bmob.cn/2/files/" + fileName;

			try {
				
				Thumbnails.of(file).size(400, 250).toFile(file);
				
				FileInputStream fis = new FileInputStream(file);

				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();

				// 一次读多个字节
				byte[] tempbytes = new byte[1];
				int byteread = 0;
				int i = 0;
				ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
				OutputStream os = conn.getOutputStream();
				System.out.println("os:" + os);
				while ((byteread = fis.read(tempbytes)) != -1) {
					os.write(tempbytes);
				}

				os.flush();
				os.close();
				fis.close();

				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_FILE_NOT_FOUND + CHAR_RISK + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * yyp 上传语音
	 * @param file
	 * @return
	 */
	public static String uploadFile3(String file) {
		
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			// 获取文件名
			/*
			 * String fileName = file.trim(); fileName =
			 * fileName.substring(fileName.lastIndexOf("/")+1); String mURL =
			 * "https://api.bmob.cn/1/files/"+fileName;
			 */

			String fileName = file.trim();
			
			fileName =  fileName.substring(fileName.lastIndexOf("/") + 1);
			
			String mURL = "https://api.bmob.cn/2/files/" + fileName;

			try {
				
				
				FileInputStream fis = new FileInputStream(file);

				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();

				// 一次读多个字节
				byte[] tempbytes = new byte[1];
				OutputStream os = conn.getOutputStream();
				
				while ((fis.read(tempbytes)) != -1) {
					os.write(tempbytes);
				}

				os.flush();
				os.close();
				fis.close();

				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_FILE_NOT_FOUND + CHAR_RISK + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}
	
	/**
	 * 复合查询-或
	 * 
	 * @param where1
	 *            JSON格式条件一
	 * @param where2
	 *            JSON格式条件二
	 * @return 复合或字符串
	 */
	public static String whereOr(String where1, String where2) {
		return "{\"$or\":[" + where1 + "," + where2 + "]}";
	}

	/**
	 * 复合查询-与
	 * 
	 * @param where1
	 *            JSON格式条件一
	 * @param where2
	 *            JSON格式条件二
	 * @return 复合与字符串
	 */
	public static String whereAnd(String where1, String where2) {
		return "{\"$and\":[" + where1 + "," + where2 + "]}";
	}

	/**
	 * 操作符-小于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合小于字符串
	 */
	public static String whereLess(int value) {
		return "{\"$lt\":" + value + "}";
	}

	/**
	 * 操作符-小于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合小于字符串
	 */
	public static String whereLess(String value) {
		return "{\"$lt\":" + value + "}";
	}

	/**
	 * 操作符-小于等于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合小于等于字符串
	 */
	public static String whereLessEqual(int value) {
		return "{\"$lte\":" + value + "}";
	}

	/**
	 * 操作符-小于等于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合小于等于字符串
	 */
	public static String whereLessEqual(String value) {
		return "{\"$lte\":" + value + "}";
	}

	/**
	 * 操作符-大于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合大于字符串
	 */
	public static String whereGreate(int value) {
		return "{\"$gt\":" + value + "}";
	}

	/**
	 * 操作符-大于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合大于字符串
	 */
	public static String whereGreate(String value) {
		return "{\"$gt\":" + value + "}";
	}

	/**
	 * 操作符-大于等于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合大于等于字符串
	 */
	public static String whereGreateEqual(int value) {
		return "{\"$gte\":" + value + "}";
	}

	/**
	 * 操作符-大于等于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合大于等于字符串
	 */
	public static String whereGreateEqual(String value) {
		return "{\"$gte\":" + value + "}";
	}

	/**
	 * 操作符-不等于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合不等于字符串
	 */
	public static String whereNotEqual(int value) {
		return "{\"$ne\":" + value + "}";
	}

	/**
	 * 操作符-不等于
	 * 
	 * @param value
	 *            目标值
	 * @return 复合不等于字符串
	 */
	public static String whereNotEqual(String value) {
		return "{\"$ne\":" + value + "}";
	}

	/**
	 * 操作符-包含
	 * 
	 * @param value
	 *            目标数组值(例：new int[]{1,3,5,7})
	 * @return 复合包含字符串
	 */
	public static String whereIn(int[] value) {
		String result = STRING_EMPTY;
		for (int i = 0; i < value.length; i++) {
			result = i == value.length - 1 ? String.valueOf(result + value[i]) : result + value[i] + ",";
		}
		return "{\"$in\":[" + result + "]}";
	}

	/**
	 * 操作符-包含
	 * 
	 * @param value
	 *            目标数组值(例：new String[]{"张三","李四","王五"})
	 * @return 复合包含字符串
	 */
	public static String whereIn(String[] value) {
		String result = STRING_EMPTY;
		for (int i = 0; i < value.length; i++) {
			result = i == value.length - 1 ? result + "\"" + value[i] + "\"" : result + "\"" + value[i] + "\",";
		}
		return "{\"$in\":[" + result + "]}";
	}

	/**
	 * 操作符-包含
	 * 
	 * @param value
	 *            目标数组值(例："1,3,5,7")
	 * @return 复合包含字符串
	 */
	public static String whereIn(String value) {
		return "{\"$in\":[" + value + "]}";
	}

	/**
	 * 操作符-不包含
	 * 
	 * @param value
	 *            目标数组值(例：new int[]{1,3,5,7})
	 * @return 复合不包含字符串
	 */
	public static String whereNotIn(int[] value) {
		String result = STRING_EMPTY;
		for (int i = 0; i < value.length; i++) {
			result = i == value.length - 1 ? String.valueOf(result + value[i]) : result + value[i] + ",";
		}
		return "{\"$nin\":[" + result + "]}";
	}

	/**
	 * 操作符-不包含
	 * 
	 * @param value
	 *            目标数组值(例：new String[]{"张三","李四","王五"})
	 * @return 复合不包含字符串
	 */
	public static String whereNotIn(String[] value) {
		String result = STRING_EMPTY;
		for (int i = 0; i < value.length; i++) {
			result = i == value.length - 1 ? result + "\"" + value[i] + "\"" : result + "\"" + value[i] + "\",";
		}
		return "{\"$nin\":[" + result + "]}";
	}

	/**
	 * 操作符-不包含
	 * 
	 * @param value
	 *            目标数组值(例："\"张三\",\"李四\",\"王五\"")
	 * @return 复合不包含字符串
	 */
	public static String whereNotIn(String value) {
		return "{\"$nin\":[" + value + "]}";
	}

	/**
	 * 操作符-存在
	 * 
	 * @param value
	 *            布尔值
	 * @return 复合存在字符串
	 */
	public static String whereExists(boolean value) {
		return "{\"$exists\":" + value + "}";
	}

	/**
	 * 操作符-全包含
	 * 
	 * @param value
	 *            目标值
	 * @return 复合全包含字符串
	 */
	public static String whereAll(String value) {
		return "{\"$all\":[" + value + "]}";
	}

	/**
	 * 操作符-区间包含
	 * 
	 * @param greatEqual
	 *            是否大于包含等于
	 * @param greatValue
	 *            大于的目标值
	 * @param lessEqual
	 *            是否小于包含等于
	 * @param lessValue
	 *            小于的目标值
	 * @return 复合区间包含字符串
	 * 
	 *         例：查询[1000,3000), whereIncluded(true,1000,false,3000)
	 */
	public static String whereIncluded(boolean greatEqual, int greatValue, boolean lessEqual, int lessValue) {
		return whereIncluded(greatEqual, String.valueOf(greatValue), lessEqual, String.valueOf(lessValue));
	}

	/**
	 * 操作符-区间包含
	 * 
	 * @param greatEqual
	 *            是否大于包含等于
	 * @param greatValue
	 *            大于的目标值
	 * @param lessEqual
	 *            是否小于包含等于
	 * @param lessValue
	 *            小于的目标值
	 * @return 复合区间包含字符串
	 * 
	 *         例：查询[1000,3000), whereIncluded(true,"1000",false,"3000")
	 */
	public static String whereIncluded(boolean greatEqual, String greatValue, boolean lessEqual, String lessValue) {
		String op1;
		String op2;
		op1 = greatEqual ? "\"$gte\"" : "\"$gt\"";
		op2 = lessEqual ? "\"$lte\"" : "\"$lt\"";
		return "{" + op1 + ":" + greatValue + "," + op2 + ":" + lessValue + "}";
	}

	/**
	 * 操作符-正则表达式
	 * 
	 * @param regexValue
	 * @return 复合正则表达式字符串
	 */
	public static String whereRegex(String regexValue) {
		String op = "\"$regex\"";
		return "{" + op + ":\"" + regexValue + "\"}";
	}

	public static int getTimeout() {
		return TIME_OUT;
	}

	public static void setTimeout(int timeout) {
		TIME_OUT = timeout;
	}

	/**
	 * private 改为public
	 * 
	 * @param conn
	 * @param paramContent
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void printWriter(HttpURLConnection conn, String paramContent)
			throws UnsupportedEncodingException, IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), UTF8));
		out.write(paramContent);
		out.flush();
		out.close();
	}

	/**
	 * private 转为 public
	 * 
	 * @param conn
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String getResultFromConnection(HttpURLConnection conn)
			throws UnsupportedEncodingException, IOException {
		StringBuffer result = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF8));
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		reader.close();
		return result.toString();
	}

	/**
	 * 
	 * @param conn
	 * @param url
	 * @param method
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection connectionCommonSetting(HttpURLConnection conn, URL url, String method)
			throws IOException {
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setReadTimeout(TIME_OUT);

		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);

		conn.setRequestProperty(BMOB_APP_ID_TAG, APP_ID);
		conn.setRequestProperty(BMOB_REST_KEY_TAG, REST_API_KEY);
		if (!MASTER_KEY.equals(STRING_EMPTY)) {
			conn.setRequestProperty(BMOB_MASTER_KEY_TAG, MASTER_KEY);
		}

		conn.setRequestProperty(CONTENT_TYPE_TAG, CONTENT_TYPE_JSON);
		return conn;
	}

	private static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		}
	} };

	private static String urlEncoder(String str) {
		try {
			return URLEncoder.encode(str, UTF8);
		} catch (UnsupportedEncodingException e1) {
			return str;
		}
	}


	/**
	 * @author ggj
	 * 文件上传公共方法 返回 文件上传后的封装对象 2016年6月14日09:36:08
	 */
	public List<UploadEntity> upload1() {
		if (null==getFile()) return null;
		Integer maxPostSize = 10485760;
		// 指定路径
		//List<UploadFile> files = this.getFiles("//LOVE_YOU", maxPostSize,

		
		List<UploadFile> files = this.getFiles(File.separator+"love_you2", maxPostSize,
				"utf-8");
	 
		log.error("files $$$$$$$$$$$=="+files.get(0).getFileName());
		// 创建一个文件上传对象
		// new UploadEntity(ids, filename, originalfilename, path);

		// 线程安全适合多线程(用可变字符串装)
		StringBuffer sb = new StringBuffer();
		List<UploadEntity> uploadEntities = new ArrayList<UploadEntity>();
		long size = 0L;
		String uploadEntityJson = "";
		String fileName = "";
		String originalFileName = "";
		String path = "";
		String relativelypath = "";
		int i = files.size();
		// 用集合来装文件上传文件
		for (UploadFile uploadFile : files) {
			size = uploadFile.getFile().length();
			// 文件名 去掉所有空格
			fileName = uploadFile.getFileName().replaceAll(" +", "");
			// 原文件名 去掉所有空格
			originalFileName = uploadFile.getOriginalFileName().replaceAll(
					" +", "");
			// 存放路径
			// path=uploadFile.getFile().getAbsolutePath();
			// 存放路径 去掉空格 FIXME linux+nginx 环境下获取路径报错
			path = uploadFile.getFile().getAbsolutePath().replaceAll(" ", "");
			//System.out.println("linux path################"+path);
			log.error(path);

			sb.append("文件长度:").append(size);
			sb.append("\n绝对路径:").append(uploadFile.getFile().getAbsolutePath());
			 
			//  获取相对路径 去掉空格==========相对路径能不能去掉空格
			String filepath = uploadFile.getFile().getAbsolutePath()
					.replaceAll(" +", "");

			relativelypath = filepath
					.substring(filepath.lastIndexOf("upload") - 1);
		 
			sb.append("\nsavedir 保存目录:").append(uploadFile.getUploadPath());
			sb.append("\n文件名称getFileName:").append(uploadFile.getFileName());
			sb.append("\n文件名称getOriginalFileName:").append(
					uploadFile.getOriginalFileName());
			sb.append("\n文件名称getParameterName:").append(
					uploadFile.getParameterName());
			UploadEntity uploadEntity = new UploadEntity(i-- + "", fileName,
					originalFileName, path, relativelypath);
			uploadEntity.setPath(path);
			
			uploadEntities.add(uploadEntity);
		}
		return uploadEntities;
	}

	/**
	 * @author  姚永鹏！！
	 * 文件上传公共方法 返回 文件上传后的封装对象 2016年6月14日09:36:08
	 */
	public List<UploadEntity> upload2() {
		if (null==getFile()) return null;
		Integer maxPostSize = 10485760;
		// 指定路径
		// List<UploadFile> files = this.getFiles("//LOVE_YOU", maxPostSize,
		// "UTF-8");
		List<UploadFile> files = this.getFiles("/" + "LOVE_YOU", maxPostSize,
				"UTF-8");
		// 创建一个文件上传对象
		// new UploadEntity(ids, filename, originalfilename, path);

		// 线程安全适合多线程(用可变字符串装)
		StringBuffer sb = new StringBuffer();
		List<UploadEntity> uploadEntities = new ArrayList<UploadEntity>();
		long size = 0L;
		String uploadEntityJson = "";
		String fileName = "";
		String originalFileName = "";
		String path = "";
		String relativelypath = "";
		int i = files.size();
		// 用集合来装文件上传文件
		for (UploadFile uploadFile : files) {
			size = uploadFile.getFile().length();
			uploadEntityJson = uploadFile.getParameterName();
			// 文件名
			fileName = uploadFile.getFileName();
			// 原文件名
			originalFileName = uploadFile.getOriginalFileName();
			// 存放路径
			// yyp 添加.replace("\\", File.separator);
			path = uploadFile.getFile().getAbsolutePath();
			sb.append("文件长度:").append(size);
			sb.append("\n绝对路径:").append(
					uploadFile.getFile().getAbsolutePath());
			// 获取相对路径
			String filepath = uploadFile.getFile().getAbsolutePath();
			relativelypath = filepath.substring(
					filepath.lastIndexOf("upload") - 1);
			/*
			 * sb.append("\nsavedir 保存目录:"
			 * ).append(uploadFile.getSaveDirectory());
			 */
			sb.append("\nsavedir 保存目录:").append(uploadFile.getUploadPath());
			sb.append("\n文件名称getFileName:").append(uploadFile.getFileName());
			sb.append("\n文件名称getOriginalFileName:").append(
					uploadFile.getOriginalFileName());
			sb.append("\n文件名称getParameterName:").append(
					uploadFile.getParameterName());
			UploadEntity uploadEntity = new UploadEntity(i-- + "", fileName,
					originalFileName, path, relativelypath, uploadEntityJson);
			
			uploadEntities.add(uploadEntity);
		}
		return uploadEntities;
	}

	/**
	 *
	 * @author ggj
	 * @Description: (本方法的作用是: [bmob云 多图上传都到云 增加相对路径返回参数!!] )
	 * @date 2016-6-9 下午10:07:58
	 */
	public List<Map<String, Object>> uploadMoreBmobListMap() {
		
		
		List<UploadEntity> uploadEntities = upload1();
		if (null==uploadEntities)  return null;
		
		System.out.println("获取到的上传文件:" + uploadEntities);
		String path = "";
		List<Map<String, Object>> listMap = new ArrayList<>();
		for (Iterator iterator = uploadEntities.iterator(); iterator.hasNext();) {
			UploadEntity uploadEntity = (UploadEntity) iterator.next();
			path = uploadEntity.getPath();

			Map<String, Object> map = new HashMap<String, Object>();
			String relativelypath=uploadEntity.getRelativelypath();
			
			// BMob 传送文件   windows
			  if("\\".equals(File.separator)){   
				  map = JSONObject.parseObject(Bmob.uploadFile2(path));
				  log.error("JDLj1111***===="+path);
			    }else{
			    	log.error("JDLj222222***===="+"root"+path);
			    	//map = JSONObject.parseObject(Bmob.uploadFile2("/root"+path));
			    	//map = JSONObject.parseObject(Bmob.uploadFile2("."+relativelypath));
			    	 map = JSONObject.parseObject(Bmob.uploadFile2("."+path));
			    }
			
			map.put("relativelypath", uploadEntity.getRelativelypath());
			map.put("path", uploadEntity.getPath());
			listMap.add(map);
		}
		// renderJson(listMap);
		return listMap;

	}

	/** 
	* @author 姚永鹏
	* @date 2016年7月7日 上午11:55:24  
	* @throws 
	*/
	public List<Map<String, Object>> uploadMoreBmobListMap2() {
		List<UploadEntity> uploadEntities = upload2();
		if (null==uploadEntities)  return null;
		
		System.out.println("获取到的上传文件:" + uploadEntities);
		String path = "";
		List<Map<String, Object>> listMap = new ArrayList<>();
		for (Iterator iterator = uploadEntities.iterator(); iterator.hasNext();) {
			UploadEntity uploadEntity = (UploadEntity) iterator.next();
			path = uploadEntity.getPath();

			Map<String, Object> map = new HashMap<String, Object>();
			// BMob 传送文件
			  if("\\".equals(File.separator)){   
				  map = JSONObject.parseObject(Bmob.uploadFile2(path));
			    }else{
			    	 map = JSONObject.parseObject(Bmob.uploadFile2("."+path));
			    }

			map.put("relativelypath", uploadEntity.getRelativelypath());
			map.put("parametername", uploadEntity.getParametername());
			map.put("path", path);
			listMap.add(map);
		}
		// renderJson(listMap);
		return listMap;

	}

	/**
	 * @Title: getparamJson @Description: (本方法的作用是：获取json对象) @param @return
	 * 设定文件 @return JSONObject 返回类型 @author ggj @date 2016年6月22日
	 * 下午4:38:12 @throws
	 */
	public JSONObject getparamJson() {
		// 方法过时不用啦
		/* HttpKit.readIncommingRequestData(getRequest()); */
		JSONObject paramObject = new JSONObject();
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
			paramObject = JSON.parseObject(json.toString());
			// System.out.println("JsonKit=="+ JsonKit.toJson(json.toString()));

			renderJson(json.toString());
		} catch (Exception e) {
			e.getMessage();
		}
		return paramObject;

	}

	/**
	 * @Title: getIpAddr @Description: (本方法的作用是：：获取请求ip) @param @param
	 * request @param @return 设定文件 @return String 返回类型 @author ggj @date
	 * 2016年6月22日 下午5:45:03 @throws
	 */
	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public boolean getClientype() {
		String userAgent = getRequest().getHeader("user-agent");
		if (userAgent.indexOf("Android") != -1) {
			// 手机客户端
			return true;
		}
		// pc端
		return false;
	}
	
	/**
	 * @author 姚永鹏，根据订单单号查询买家和卖家手机号，
	 */
	
	public void getRelation(String order_sn){

//		String order_sn=jo.get("order_sn").toString();
		if(order_sn!=null&&!"".equals(order_sn)){
			
			JSONObject result=new JSONObject();
			ArrayList<String> al= BmobAPI.getAllObjectIdById("shopnc_fworder", "{\"order_sn\":\""+order_sn+"\"}");
			if(null!=al)
				if(al.size()==1)
				for(String objectId:al){
					
					String fwOrderInfo=findOne("shopnc_fworder", objectId);
					
					JSONObject fworder=JSONObject.parseObject(fwOrderInfo);
					
					Integer storeId=fworder.getInteger("store_id");
					
					Integer buyerId=fworder.getInteger("buyer_id");
					
					String sellerObject=BmobAPI.getObjectIdById("loveyou_member","store_id",storeId);
					
					String seller="";
					if(sellerObject!=null){
						seller=BmobAPI.findOne("loveyou_member", sellerObject);
						
						JSONObject sel=JSONObject.parseObject(seller);
						String sellerPhoneNum=sel.getString("phone_number");

						result.put("seller_phone_num", sellerPhoneNum);
					}
					String buyer="";
					
					String buyerObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", buyerId);
					if(buyerObject!=null){
						buyer=BmobAPI.findOne("loveyou_member", buyerObject);
						
						JSONObject sel=JSONObject.parseObject(buyer);
						String buyerPhoneNum=sel.getString("phone_number");

						result.put("buyer_phone_num", buyerPhoneNum);
					}
					
				}
				if(result.size()==2){
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result.toJSONString()).toString());
					return;
				}
			
				al= BmobAPI.getAllObjectIdById("loveyou_order", "{\"order_sn\":\""+order_sn+"\"}");
				System.out.println("+++++++++++++++"+al);
				if(null!=al)
					if(al.size()==1)
					for(String objectId:al){
						
						String goodsOrderInfo=findOne("loveyou_order", objectId);
						
						JSONObject fworder=JSONObject.parseObject(goodsOrderInfo);
						
						Integer storeId=fworder.getInteger("store_id");
						
						Integer buyerId=fworder.getInteger("buyer_id");
						
						String sellerObject=BmobAPI.getObjectIdById("loveyou_member","store_id",storeId);
						
						String seller="";
						if(sellerObject!=null){
							seller=BmobAPI.findOne("loveyou_member", sellerObject);
							
							JSONObject sel=JSONObject.parseObject(seller);
							String sellerPhoneNum=sel.getString("phone_number");

							result.put("seller_phone_num", sellerPhoneNum);
						}
						String buyer="";
						
						String buyerObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", buyerId);
						if(buyerObject!=null){
							buyer=BmobAPI.findOne("loveyou_member", buyerObject);
							
							JSONObject sel=JSONObject.parseObject(buyer);
							String buyerPhoneNum=sel.getString("phone_number");

							result.put("buyer_phone_num", buyerPhoneNum);
						}
						
					}
					if(result.size()==2){
						renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result.toJSONString()).toString());
						return;
					}

			if(result.size()!=2){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该订单不存在\"}").toString());
				return;
			}
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该订单为空，或无效\"}").toString());
		return;
	}

	
   /**
	* 
	* @Title: sendSms 
	* @Description:   (本方法的作用是： 发送短信给商家和买家) 
	* @param 
	* @return void    返回类型 
	* @author ggj
	* @date 2016年7月10日 下午7:27:12  
	* @throws
	 */
	public void sendSms() {
		String result = "";
		JSONObject jObject = getparamJson();
		String mobileNum = jObject.get("mobilePhoneNumber").toString();
		
		/*
		 * (模板选填，需先在管理后台创建) "ordersell" "orderbuy" "pay" 充值 :你的充值成功，爱你约感谢有您的支持。
		 * 订单买家: 您的订单已经更新，请关注微信公众号"爱你约"查看。爱你约感谢有你的支持 订单卖家 :
		 * 您有新的订单，请关注微信公众号"爱你约"查看。爱你约感谢有你
		 */
		String template = jObject.get("template") + "";
		// String template = "订单买家";

		if (Bmob.isInit()) {
			HttpURLConnection conn = null;
			// 模板
			String mobanURL = "https://api.bmob.cn/1/requestSmsCode";
			// 自定义
			// String URL = "https://api.bmob.cn/1/requestSms";
			try {
				BSONObject bson = new BSONObject();
				bson.put("mobilePhoneNumber", mobileNum);

				// 自己定义的，但是签名确是比目鱼
				// bson.put("content", template);
				bson.put("template", template);
				// 获取http对象
				conn = Bmob.connectionCommonSetting(conn, new URL(mobanURL),
						"POST");
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, bson.toString());
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + ":" + "(callFunction)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + ":" + "(callFunction)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		renderJson(result);

	}
}
