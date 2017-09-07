package com.loveyou.webController.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.bson.BSONObject;
import com.loveyou.bmob.restapi.Bmob;

import net.sf.json.JSONArray;

/**
 * Bmob工具类
 * 
 * @author Hu Xiaobo
 *
 */
public class BmobAPI extends Bmob {

	private static final Logger logger = Logger.getLogger("SSOJfinalInterceptor");

	/**
	 * GET请求方式
	 */
	public static final String METHOD_GET = "GET";
	/**
	 * POST请求方式
	 */
	public static final String METHOD_POST = "POST";
	/**
	 * PUT请求方式
	 */
	public static final String METHOD_PUT = "PUT";
	/**
	 * DELETE请求方式
	 */
	public static final String METHOD_DELETE = "DELETE";
	/**
	 * 注册用户请求地址 <br/>
	 * 使用手机号码一键注册或登陆请求地址
	 */
	public static final String REGISTER_URL = "https://api.bmob.cn/1/users";
	/**
	 * 用户登录请求地址
	 */
	public static final String LOGIN_URL = "https://api.bmob.cn/1/login";
	/**
	 * 操作当前用户请求地址<br/>
	 * 包括获取、修改、删除<br/>
	 * 请求路径不完整，需要拼接指定用户的id
	 */
	public static final String OPERATE_USER_URL = "https://api.bmob.cn/1/users/";
	/**
	 * 获取所有用户请求地址
	 */
	public static final String GET_ALL_USER_URL = "https://api.bmob.cn/1/users";
	/**
	 * 通过邮箱修改密码的请求地址
	 */
	public static final String RESET_BY_EMAIL_URL = "https://api.bmob.cn/1/requestPasswordReset";
	/**
	 * 通过短信修改密码的请求地址<br/>
	 * 请求路径不完整，需要拼接SmsCode短信验证码
	 */
	public static final String RESET_BY_MESSAGE_URL = "https://api.bmob.cn/1/resetPasswordBySmsCode/";
	/**
	 * 以旧密码方式修改密码的请求地址(请求路径不完整，需要拼接指定用户id)
	 */
	public static final String RESET_BY_PASSWORD_URL = "https://api.bmob.cn/1/updateUserPassword/";
	/**
	 * 邮箱验证请求地址
	 */
	public static final String CHECK_EMAIL_URL = "https://api.bmob.cn/1/requestEmailVerify";
	/**
	 * 发送短信请求地址
	 */
	public static final String SEND_MESSAGE_URL = "https://api.bmob.cn/1/requestSms";
	/**
	 * 发送验证码请求地址
	 */
	public static final String SEND_CHECKCODE_URL = "https://api.bmob.cn/1/requestSmsCode";
	/**
	 * 检查验证码是否正确请求地址<br/>
	 * 请求地址不完整，需要拼接验证码
	 */
	public static final String VERIFY_SMSCODE_URL = "https://api.bmob.cn/1/verifySmsCode/";
	/**
	 * UTF-8编码
	 */
	public static final String UTF8 = "utf-8";

	private static int TIME_OUT = 10000;
	private static final String BMOB_APP_ID_TAG = "X-Bmob-Application-Id";
	private static String STRING_EMPTY = "";
	private static String APP_ID = STRING_EMPTY;
	private static String REST_API_KEY = STRING_EMPTY;
	private static String MASTER_KEY = STRING_EMPTY;

	private static final String BMOB_REST_KEY_TAG = "X-Bmob-REST-API-Key";
	private static final String BMOB_MASTER_KEY_TAG = "X-Bmob-Master-Key";
	private static final String CONTENT_TYPE_TAG = "Content-Type";
	private static final String CONTENT_TYPE_JSON = "application/json";
	/**
	 * 未注册
	 */
	public static final String MSG_UNREGISTERED = "Unregistered";

	private static String appId;// appId
	private static String appKey;// appKey
	/**
	 * 分页显示的条数
	 */
	private static int pageSize = 10;

	public static String getAppId() {
		return appId;
	}

	public static void setAppId(String appId) {
		BmobAPI.appId = appId;
	}

	public static String getAppKey() {
		return appKey;
	}

	public static void setAppKey(String appKey) {
		BmobAPI.appKey = appKey;
	}

	public static int getPageSize() {
		return pageSize;
	}

	public static void setPageSize(int pageSize) {
		BmobAPI.pageSize = pageSize;
	}

	/**
	 * 发送请求
	 * 
	 * @param conn
	 *            HttpURLConnection
	 * @param url
	 *            请求路径
	 * @param method
	 *            请求方式
	 * @return HttpURLConnection
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

	/**
	 * 发送内容
	 * 
	 * @param conn
	 *            HttpURLConnection
	 * @param paramContent
	 *            发送的内容
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
	 * 获取相应的内容
	 * 
	 * @param conn
	 *            HttpURLConnection
	 * @return String响应的内容
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String getResultFromConnection(HttpURLConnection conn)
			throws UnsupportedEncodingException, IOException {
		StringBuffer result = new StringBuffer();
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF8));
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		reader.close();
		return result.toString();
	}

	/**
	 * 从请求中获取参数并封装成BSONObject
	 * 
	 * @param request
	 *            请求对象
	 * @param paraList
	 *            参数列表
	 * @return BSONObject
	 */
	public static BSONObject getBSONByRequest(HttpServletRequest request, List<String> paraList) {
		BSONObject bson = new BSONObject();
		try {
			StringBuilder json = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
			reader.close();
			/**
			 * 从request中获取json字符串
			 */
			JSONObject paramObject = JSON.parseObject(json.toString());
			/**
			 * 将json对象封装成BSON对象
			 */
			for (String paraName : paraList) {
				bson.put(paraName, paramObject.getString(paraName));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bson;
	}

	/**
	 * 从请求中获取参数
	 * 
	 * @param request
	 *            请求对象
	 * @param params
	 *            参数列表
	 * @return 值列表
	 */
	public static List<String> getPara(HttpServletRequest request, List<String> params) {
		List<String> values = new ArrayList<String>();
		try {
			StringBuilder json = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
			reader.close();
			/**
			 * 从request中获取json字符串
			 */
			JSONObject paramObject = JSON.parseObject(json.toString());
			/**
			 * 将json对象封装成List集合
			 */
			for (String paraName : params) {
				values.add(paramObject.getString(paraName));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return values;
	}

	/**
	 * 获取请求参数并封装成JSON对象
	 * 
	 * @param request
	 *            请求对象
	 * @return JSONObject
	 */
	public static JSONObject getJSONObjectFromRequest(HttpServletRequest request) {
		BufferedReader reader = null;
		try {
			StringBuilder json = new StringBuilder();
			reader = request.getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
			/**
			 * 从request中获取json字符串
			 */
			System.out.println("页面传过来的参数：" + json.toString());
			return JSON.parseObject(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 一维JSON字符串转成map对象
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @return Map对象
	 */
	public static Map<String, Object> jsonStrToMap(String jsonStr) {
		Map<String, Object> jsonMap = net.sf.json.JSONObject.fromObject(jsonStr);
		return jsonMap;
	}

	/**
	 * 二维json字符串转成list对象
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @return list对象
	 */
	public static List<Map<String, Object>> jsonStrToList(String jsonStr) {
		List<Map<String, Object>> jsonList = (List) net.sf.json.JSONArray.fromObject(jsonStr);
		return jsonList;
	}

	/**
	 * 将map对象转换成json字符串
	 * 
	 * @param paramsMap
	 *            map对象
	 * @return json字符串
	 */
	public static String mapToJSONStr(Map<String, Object> paramsMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Entry<String, Object> entry : paramsMap.entrySet()) {
			sb.append("\"");
			sb.append(entry.getKey());
			/**
			 * 判断类型
			 */
			Object value = entry.getValue();
			if (value instanceof String) {
				sb.append("\":\"");
				sb.append(value);
				sb.append("\",");
			} else {
				sb.append("\":");
				sb.append(value);
				sb.append(",");
			}
		}
		sb.delete(sb.length() - 1, sb.length());
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 包装json字符串
	 * 
	 * @param jsonStr
	 *            json字符串
	 * @return json字符串
	 */
	public static String encodeInsertParams(String jsonStr) {
		return "{\"list\":" + jsonStr + "}";
	}

	/**
	 * 通过表的主键获取ObjectId
	 * 
	 * @param tableName
	 *            表名
	 * @param primaryKeyName
	 *            主键名称
	 * @param primaryKeyValue
	 *            主键值
	 * @return ObjectId
	 */
	public static String getObjectIdById(String tableName, String primaryKeyName, Object primaryKeyValue) {
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(primaryKeyName, primaryKeyValue);
		String where = mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = Bmob.findAll(tableName, where);
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		JSONArray results = (JSONArray) resultMap.get("results");
		if (results.isEmpty()) {
			logger.log(Level.WARNING, tableName + "表里面：" + primaryKeyName + "=" + primaryKeyValue + "这条数据不存在！");
		} else {
			/**
			 * 解析数据
			 */
			Map<String, Object> map = resultToMap(result);
			return (String) map.get("objectId");
		}
		return null;
	}

	/**
	 * 结果字符串转成List对象
	 * 
	 * @param result
	 *            结果字符串
	 * @return List对象
	 */
	public static List<Map<String, Object>> resultToList(String result) {
		/**
		 * 解析数据
		 */
		Map<String, Object> resultMap = jsonStrToMap(result);
		/**
		 * 获取result对应的json数组
		 */
		net.sf.json.JSONArray jsonArray = (JSONArray) resultMap.get("results");
		List<Map<String, Object>> list = (List<Map<String, Object>>) jsonArray;
		return list;
	}

	/**
	 * 结果字符串转成Map对象(只针对唯一结果)
	 * 
	 * @param result
	 *            结果字符串
	 * @return Map对象
	 */
	public static Map<String, Object> resultToMap(String result) {
		/**
		 * 解析数据
		 */
		Map<String, Object> resultMap = jsonStrToMap(result);
		/**
		 * 获取result对应的json数组
		 */
		net.sf.json.JSONArray jsonArray = (JSONArray) resultMap.get("results");
		List<Map<String, Object>> list = (List<Map<String, Object>>) jsonArray;
		if (list == null || list.isEmpty()) {
			return new HashMap();
		}
		return list.get(0);
	}

	/**
	 * 从结果字符串中获取指定参数的值(只针对单条结果)
	 * 
	 * @param result
	 *            结果字符串
	 * @return 参数的值
	 */
	public static Object getParamFromResult(String result, String param) {
		return resultToMap(result).get(param);
	}

	/**
	 * 获取整型参数(兼容字符串)
	 * 
	 * @param jsobObject
	 *            JSONObject参数对象
	 * @param paramName
	 *            参数名
	 * @return Integer整型参数值
	 */
	public static Integer getIntegerValueFromJSONObject(JSONObject jsobObject, String paramName) {
		Integer intValue = jsobObject.getInteger(paramName);
		if (intValue == null) {
			String stringValue = jsobObject.getString(paramName);
			if (stringValue != null) {
				intValue = Integer.parseInt(stringValue);
			} else {
				
				logger.log(Level.WARNING,"缺少参数" +paramName);
//				throw new ParameterMissingException(paramName);
				return null;
			}
		}
		return intValue;
	}

	/**
	 * 获取整型参数(兼容字符串)
	 * 
	 * @param jsobObject
	 *            JSONObject参数对象
	 * @param paramName
	 *            参数名
	 * @return Integer整型参数值
	 */
	public static Double getDoubleValueFromJSONObject(JSONObject jsobObject, String paramName) {
		Double value = jsobObject.getDouble(paramName);
		if (value == null) {
			String doubleValue = jsobObject.getString(paramName);
			if (doubleValue != null) {
				value = Double.parseDouble(doubleValue);
			} else {
				throw new ParameterMissingException(paramName);
			}
		}
		return value;
	}

	/**
	 * 获取整型参数(兼容字符串)
	 * 
	 * @param jsobObject
	 *            JSONObject参数对象
	 * @param paramName
	 *            参数名
	 * @return Integer整型参数值
	 */
	public static Long getLongValueFromJSONObject(JSONObject jsobObject, String paramName) {
		Long value = jsobObject.getLong(paramName);
		if (value == null) {
			String longValue = jsobObject.getString(paramName);
			if (longValue != null) {
				value = Long.parseLong(longValue);
			} else {
				throw new ParameterMissingException(paramName);
			}

		}
		return value;
	}

	/**
	 * 获取参数(兼容字符串)
	 * 
	 * @param jsobObject
	 *            JSONObject参数对象
	 * @param paramName
	 *            参数名
	 * @return String整型参数值
	 */
	public static String getStringValueFromJSONObject(JSONObject jsobObject, String paramName) {
		String value = jsobObject.getString(paramName);
		if (value == null) {
//			throw new ParameterMissingException(paramName);
			logger.log(Level.WARNING,"缺少参数" +paramName);
			return null;
		}
		return value;
	}

	/**
	 * 获取单个上传文件的filename
	 * 
	 * @param list
	 *            文件上传后返回的集合
	 * @return 文件名
	 */
	public static String getSingleUploadFileName(List<Map<String, Object>> list) {

		String relateivelypath = (String) list.get(0).get("relativelypath");
		relateivelypath = relateivelypath.replace("\\", "/");
		return relateivelypath;
	}

	/**
	 * 获取单个上传文件的url
	 * 
	 * @param list
	 *            文件上传后返回的集合
	 * @return 文件名
	 */
	public static String getSingleUploadFileUrl(List<Map<String, Object>> list) {
		return (String) list.get(0).get("url");
	}

	/**
	 * 区间查询<br/>
	 * 例如：查询年龄在20-30之间的数据
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @param minValue
	 *            最小值
	 * @param maxValue
	 *            最大值
	 * @return 查询结果
	 */
	public static String findAllBetweenMinValueToMaxValue(String tableName, String columnName, Long minValue,
			Long maxValue) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"" + columnName + "\":{\"$gte\":" + minValue + ",\"$lte\":" + maxValue + "}}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 两端查询<br/>
	 * 例如：查询年龄小于20或者大于30的数据
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @param minValue
	 *            最小值
	 * @param maxValue
	 *            最大值
	 * @return 查询结果
	 */
	public static String findAllExceptMinValueToMaxValue(String tableName, String columnName, Integer minValue,
			Integer maxValue) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"$or\":[{\"" + columnName + "\":{\"$gt\":" + maxValue + "}},{\"" + columnName
					+ "\":{\"$lt\":" + minValue + "}}]}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询某个字段值在数组内的数据<br/>
	 * 例如查询年龄在[22,24,34,55,66,76]里面的数据
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @param arrays
	 *            数组
	 * @return 查询结果
	 */
	public static String findAllFromArray(String tableName, String columnName, Object[] arrays) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"" + columnName + "\":{\"$in\":" + Arrays.toString(arrays) + "}}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询某个字段值不在数组内的数据<br/>
	 * 例如查询年龄不在[22,24,34,55,66,76]里面的数据
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @param arrays
	 *            数组
	 * @return 查询结果
	 */
	public static String findAllNotFromArray(String tableName, String columnName, Object[] arrays) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"" + columnName + "\":{\"$nin\":" + Arrays.toString(arrays) + "}}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询某个字段值有值的数据
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @return 查询结果
	 */
	public static String findAllExists(String tableName, String columnName) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"" + columnName + "\":{\"$exists\":true}}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询某个字段值有值的数据
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @return 查询结果
	 */
	public static String findAllNotExists(String tableName, String columnName) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"" + columnName + "\":{\"$exists\":false}}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 指定字段模糊查询%keyWord%
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @param keyWord
	 *            关键字
	 * @return 查询结果
	 */
	public static String findAllLike(String tableName, String columnName, String keyWord) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"" + columnName + "\":{\"$regex\":\"%" + keyWord + "%\"}}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 通过BQL语句查询
	 * 
	 * @param bqlStr
	 *            BQL语句
	 * @param valueArray
	 *            值数组
	 * @return 查询结果
	 */
	public static String findByBQL(String bqlStr, Object[] valueArray) {
		HttpURLConnection conn = null;
		String result = "bql=" + bqlStr;
		try {
			String mURL = "https://api.bmob.cn/1/cloudQuery";
			String bql = "";
			String values = "values=" + Arrays.toString(valueArray);
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + bql + "&" + values), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 解决编码问题
	 * 
	 * @param value
	 * @return
	 */
	public static String encoding(String value) {
		try {
			return new String(value.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 两个条件去并集
	 * 
	 * @param tableName
	 *            表名
	 * @param condition1
	 *            条件1
	 * @param condition2
	 *            条件2
	 * @return 查询结果
	 */
	public static String findAllByConditionOr(String tableName, String condition1, String condition2) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"$or\":[" + condition1 + "," + condition2 + "]}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 两个条件去交集
	 * 
	 * @param tableName
	 *            表名
	 * @param condition1
	 *            条件1
	 * @param condition2
	 *            条件2
	 * @return 查询结果
	 */
	public static String findAllByConditionAnd(String tableName, String condition1, String condition2) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "where={\"$and\":[" + condition1 + "," + condition2 + "]}";
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询结果按单个字段排序
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @param isDesc
	 *            是否将序
	 * @return 查询结果
	 */
	public static String findAllSortByColumn(String tableName, String columnName, Boolean isDesc) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "";
			if (isDesc) {
				where = "order=-" + columnName;
			} else {
				where = "order=" + columnName;
			}
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询结果按两个字段排序
	 * 
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @param isDesc
	 *            是否将序
	 * @return 查询结果
	 */
	public static String findAllSortByColumn(String tableName, String columnName1, Boolean isDesc1, String columnName2,
			Boolean isDesc2) {
		HttpURLConnection conn = null;
		String result = "";
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			String where = "";
			if (isDesc1) {
				where = "order=-" + columnName1;
				if (isDesc2) {
					where += "-" + columnName2;
				}
			} else {
				where = "order=" + columnName1;
				if (isDesc2) {
					where += columnName2;
				}
			}
			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where), "GET");
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询表中有多少行
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件 如：where={"member_id":1},"""需要使用转移符\"
	 * @param isDesc
	 *            是否将序
	 * @return 查询结果
	 */
	public static int CountRow(String tableName, String where) {

		HttpURLConnection conn = null;
		int result = 0;
		try {
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;

			conn = connectionCommonSetting(conn, new URL(mURL + "?" + where + "&count=1&limit=0"), "GET");
			conn.connect();
			String resultm = getResultFromConnection(conn);

			JSONObject jo = JSON.parseObject(resultm);
			Integer count = jo.getInteger("count");

			conn.disconnect();

			return count;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            jsonString格式条件
	 * @return 集合
	 */

	public static ArrayList<String> getAllObjectIdById(String tableName, String where) {

		/**
		 * 发送请求
		 */
		String result = Bmob.findAll(tableName, where);
		try{
			JSONObject jo = (JSONObject) JSONObject.parse(result);

			com.alibaba.fastjson.JSONArray ja = jo.getJSONArray("results");
			
			int i = 0;
			ArrayList<String> al = new ArrayList<String>();
			for (i = 0; i < ja.size(); i++) {
				String m = ja.getJSONObject(i).getString("objectId");
				al.add(m);
			}
			if (ja.isEmpty()) {
				return null;
			} else {

				return al;
			}
			
		}catch(Exception e){
			
			return null;
		}

	}

	/**
	 * 通过code获取信息
	 */
	public static String getInfoByCode(String code) {
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
		try {
			/**
			 * 封装参数
			 */
			String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx0667d13434a471a0&secret=a30cf9f0d433d81a0ab92ef3a3c037d0&code="
					+ code + "&grant_type=authorization_code";
			conn = connectionCommonSetting(conn, new URL(url), BmobAPI.METHOD_POST);
			conn.setDoOutput(true);
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取openId
	 */
	public static String getOpenId(String codeInfo) {
		Map<String, Object> resultMap = jsonStrToMap(codeInfo);
		return (String) resultMap.get("openid");
	}

	/**
	 * 获取openId
	 */
	public static String getAccess_token(String codeInfo) {
		Map<String, Object> resultMap = jsonStrToMap(codeInfo);
		return (String) resultMap.get("access_token");
	}

	/**
	 * 获取refresh_token
	 * 
	 * @param codeInfo
	 * @return refresh_token
	 */
	public static String getRefreshToken(String codeInfo) {
		Map<String, Object> resultMap = jsonStrToMap(codeInfo);
		return (String) resultMap.get("refresh_token");
	}

	public static String getInfoByRefreshToken(String refreshToken) {
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
		try {
			/**
			 * 封装参数
			 */
			String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=wx0667d13434a471a0&grant_type=refresh_token&refresh_token="
					+ refreshToken;
			conn = connectionCommonSetting(conn, new URL(url), BmobAPI.METHOD_GET);
			conn.setDoOutput(true);
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取手机号
	 */
	public static String getUserInfo(String access_token, String openid) {
		HttpURLConnection conn = null;
		// 定义结果常量
		String result = "";
		try {
			/**
			 * 封装参数
			 */
			String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid="
					+ openid + "&lang=zh_CN";
			conn = connectionCommonSetting(conn, new URL(url), BmobAPI.METHOD_GET);
			conn.setDoOutput(true);
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
