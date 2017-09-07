package com.loveyou.webController.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.RepeatStyle;
import com.loveyou.webController.userinfo.UserInfoController;

/**
 * 服务管理控制器
 * 
 * @ClassName: ServiceController
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月4日 上午10:25:29
 *
 * 
 */
public class ServiceController extends Bmob {

	static String pattern="yyyy-MM-dd HH:mm:ss";
	static SimpleDateFormat sdf=new SimpleDateFormat(pattern);

	/**
	 * 一级服务类别查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllFirstClassServiceType() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 封装条件参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("fc_parent_id", 0);
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_fwxq_class", whereJsonStr, (pageNum - 1) * pageSize, pageSize);
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 二级服务类别查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getSecondClassServiceType() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 获取一级服务类别id(前台提供)
		 */
		Integer fc_parent_id = BmobAPI.getIntegerValueFromJSONObject(params, "fc_id");
		/**
		 * 封装条件参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("fc_parent_id", fc_parent_id);
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_fwxq_class", whereJsonStr, (pageNum - 1) * pageSize, pageSize);
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 获取会员的服务列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllServiceListByMemberId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 获取会员id
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_id", member_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		System.out.println(paramStr);
		String result = find("shopnc_fb_classtotal", paramStr, (pageNum - 1) * pageSize, pageSize,"-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 重复方式传参格式：<br/>
	 * 从不：fw_repeat_style:"once" <br/>
	 * 每天：fw_repeat_style:"everyday" <br/>
	 * 每天(仅限工作日)：fw_repeat_style:"everyworkday" <br/>
	 * 每周(周三、周五)：fw_repeat_style:"everyweek,3,5" <br/>
	 * 发布服务 TODO
	 */
	@Deprecated
	public void publishService() {
		/**
		 * 上传图片
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
		String fileName = "";
		String url = "";
		if (maps!=null&&!maps.isEmpty()) {
			/**
			 * 获取上传文件的文件名
			 */
			fileName = BmobAPI.getSingleUploadFileName(maps);
			/**
			 * 获取上传文件的url
			 */
			url = BmobAPI.getSingleUploadFileUrl(maps);
		}
		/**
		 * 获取服务名称(前台提供)
		 */
		String fw_name = getPara("fw_name");
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = getParaToInt("member_id");
		/**
		 * 获取服务广告词(前台提供)
		 */
		String fw_jingle = getPara("fw_jingle");
		/**
		 * 获取服务分类编号(前台提供)
		 */
		Integer fc_id = getParaToInt("fc_id");
		/**
		 * 获取服务分类名称
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fwxq_class", "fc_id", fc_id);
		String fwClassInfo = findOne("shopnc_fwxq_class", objectId);
		Map<String, Object> fwClassInfoMap = BmobAPI.jsonStrToMap(fwClassInfo);
		String fc_name = (String) fwClassInfoMap.get("fc_name");
		/**
		 * 获取店铺编号(前台提供)
		 */
		Integer store_id = getParaToInt("store_id");
		/**
		 * 获取店铺名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		String storeInfo = findOne("loveyou_store", objectId);
		String store_name = (String) BmobAPI.jsonStrToMap(storeInfo).get("store_name");
		/**
		 * 服务添加时间
		 */
		long fw_addtime = System.currentTimeMillis() / 1000;
		/**
		 * 获取服务价格(前台提供)
		 */
		Double fw_price = Double.parseDouble(getPara("fw_price"));
		/**
		 * 获取起始时间(前台提供)
		 */
		Integer fw_starttime = getParaToInt("fw_starttime");
		/**
		 * 获取结束时间(前台提供)
		 */
		Integer fw_endtime = getParaToInt("fw_endtime");
		/**
		 * 获取工作方式(前台提供)
		 */
		Integer fw_work_style = getParaToInt("fw_work_style");
		/**
		 * 获取一级地区id(前台提供)
		 */
		Integer areaid_1 = getParaToInt("areaid_1");
		/**
		 * 获取二级地区id(前台提供)
		 */
		Integer areaid_2 = getParaToInt("areaid_2");

		String fw_area = mergeAddressToAreaInfo(areaid_1, areaid_2);
		/**
		 * 获取重复方式(前台提供)
		 */
		String fw_repeat_style = getPara("fw_repeat_style");
		/**
		 * 获取服务描述(前台提供，可为空)
		 */
		String fw_body = getPara("fw_body");
		/**
		 * 获取停止重复时间(前台提供) FIXME ggj 修改
		 */
		Long fw_repeat_endtime = getParaToLong("fw_repeat_endtime");
		if (null == fw_repeat_endtime) {
			fw_repeat_endtime = 1466497680L;

		}
		/**
		 * 时间戳转换成时间
		 */
		Date date = new Date(fw_repeat_endtime * 1000);
		/**
		 * 封装shopnc_fb_classtotal发布服务统计表需要的参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fw_name", fw_name);
		paramMap.put("member_id", member_id);
		paramMap.put("fc_id", fc_id);
		paramMap.put("fc_name", fc_name);
		paramMap.put("fw_price", fw_price);
		paramMap.put("store_id", store_id);
		paramMap.put("fw_state", 1);// 默认上架
		paramMap.put("fw_verify", 10);// 默认审核中
		paramMap.put("fw_starttime", fw_starttime);
		paramMap.put("fw_endtime", fw_endtime);
		paramMap.put("fw_work_style", fw_work_style);
		paramMap.put("fw_area", fw_area);//服务区域存序列化数组(也可以存经纬度的值)
		paramMap.put("fw_repeat_style", fw_repeat_style);
		paramMap.put("fw_repeat_endtime", fw_repeat_endtime);
		if (!"".equals(url)) {
			paramMap.put("fw_image", url);
		}
		if (!"".equals(fileName)) {
			paramMap.put("image_relativelypath", fileName);
		}
		// paramMap.put("type_id", type_id);//发布服务类型id(当前用不上)
		// paramMap.put("type_name", type_name);//发布服务类型名称(当前用不上)
		paramMap.put("fw_body", fw_body);
		paramMap.put("store_name", store_name);
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向服务公共表发送请求
		 */
		System.out.println(jsonStr);
		String result = insert("shopnc_fb_classtotal", jsonStr);
		System.out.println(result);
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 解析result字符串
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		objectId = (String) resultMap.get("objectId");
		/**
		 * 获取发布表id
		 */
		String totalInfo = findOne("shopnc_fb_classtotal", objectId);
		Map<String, Object> totalInfoMap = BmobAPI.jsonStrToMap(totalInfo);
		Integer fb_id = (Integer) totalInfoMap.get("fb_id");
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap.put("member_id", member_id);
		paramMap.put("fw_name", fw_name);
		paramMap.put("fw_jingle", fw_jingle);
		paramMap.put("store_id", store_id);
		paramMap.put("store_name", store_name);
		paramMap.put("fc_id", fc_id);
		paramMap.put("fc_name", fc_name);
		paramMap.put("fw_repeat_style", fw_repeat_style);
		paramMap.put("fw_repeat_endtime", fw_repeat_endtime);
		if (!"".equals(url)) {
			paramMap.put("fw_image", url);
		}
		if (!"".equals(fileName)) {
			paramMap.put("image_relativelypath", fileName);
		}
		paramMap.put("fw_price", fw_price);
		paramMap.put("fw_state", 1);// 默认上架
		paramMap.put("fw_verify", 10);// 默认审核中
		paramMap.put("fw_addtime", fw_addtime);// 默认审核中
		paramMap.put("fw_starttime", fw_starttime);
		paramMap.put("fw_endtime", fw_endtime);
		paramMap.put("fw_commend", 0);// 服务推荐 1是，0否，默认为0
		paramMap.put("fw_freight", 0);// 交通费 0为免交通费
		paramMap.put("fw_vat", 0);// 是否开具增值税发票 1是，0否
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("fw_area", fw_area);
		paramMap.put("fw_body", fw_body);// 服务描述
		paramMap.put("fb_id", fb_id);// 发布编号
		paramMap.put("fw_click", 0);// 点击次数，默认为0
		paramMap.put("fw_salenum", 0);// 销售数量，默认为0
		paramMap.put("fw_collect", 0);// 收藏数量，默认为0
		jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 解析重复方式
		 */
		String[] repeatStrings = fw_repeat_style.split(",");
		if (repeatStrings.length == 1) {// 每天或者从不
			/**
			 * 不重复
			 */
			String repeatStyle = repeatStrings[0];
			if (repeatStyle.equals(RepeatStyle.ONCE)) {
				result = sendPublishServiceRequest(fw_starttime, fw_endtime, fw_work_style, paramMap, jsonStr);
				/**
				 * 每天
				 */
			} else if (repeatStyle.equals(RepeatStyle.EVERYDAY)) {
				/**
				 * 遍历今天到重复截止时间
				 */
				Calendar start = Calendar.getInstance();
				Long startTIme = System.currentTimeMillis();
				start.setTime(date);
				Long endTime = start.getTimeInMillis();
				Long oneDay = 1000 * 60 * 60 * 24l;
				Long time = startTIme;
				while (time <= endTime) {
					/**
					 * 发送请求
					 */
					result = sendPublishServiceRequest(fw_starttime, fw_endtime, fw_work_style, paramMap, jsonStr);
					time += oneDay;
				}
				/**
				 * 每个工作日
				 */
			} else if (repeatStyle.equals(RepeatStyle.EVERYWORKDAY)) {
				/**
				 * 遍历今天到重复截止时间
				 */
				Calendar start = Calendar.getInstance();
				Long startTIme = System.currentTimeMillis();
				start.setTime(date);
				Long endTime = start.getTimeInMillis();
				Long oneDay = 1000 * 60 * 60 * 24l;
				Long time = startTIme;
				while (time <= endTime) {
					Date d = new Date(time);
					/**
					 * 判断是否是周末
					 */
					start.setTime(d);
					if (!(start.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
							|| start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
						/**
						 * 发送请求
						 */
						result = sendPublishServiceRequest(fw_starttime, fw_endtime, fw_work_style, paramMap, jsonStr);
					}
					time += oneDay;
				}
			}
		} else {
			repeatStrings = fw_repeat_style.substring(fw_repeat_style.indexOf(",") + 1, fw_repeat_style.length())
					.split(",");
			/**
			 * 遍历今天到重复截止时间
			 */
			Calendar start = Calendar.getInstance();
			Long startTIme = System.currentTimeMillis();
			start.setTime(date);
			Long endTime = start.getTimeInMillis();
			Long oneDay = 1000 * 60 * 60 * 24l;
			Long time = startTIme;
			while (time <= endTime) {
				Date d = new Date(time);
				/**
				 * 判断是否是选择的日期
				 */
				start.setTime(d);
				for (String string : repeatStrings) {
					Integer i = Integer.parseInt(string);
					if (start.get(Calendar.DAY_OF_WEEK) == i) {
						/**
						 * 发送请求
						 */
						result = sendPublishServiceRequest(fw_starttime, fw_endtime, fw_work_style, paramMap, jsonStr);
					}
				}
				time += oneDay;
			}
		}
		/**
		 * 返回结果
		 */
		System.out.println("result:" + result);
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	private String sendPublishServiceRequest(Integer fw_starttime, Integer fw_endtime, Integer fw_work_style,
			Map<String, Object> paramMap, String jsonStr) {
		String objectId;
		String result;
		/**
		 * 向服务公共表发送请求
		 */
		System.out.println(jsonStr);
		result = insert("shopnc_fw_common", jsonStr);
		/**
		 * 解析result字符串
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		objectId = (String) resultMap.get("objectId");
		/**
		 * 获取服务公共表id
		 */
		String fwCommonInfo = findOne("shopnc_fw_common", objectId);
		Map<String, Object> fwCommonInfoMap = BmobAPI.jsonStrToMap(fwCommonInfo);
		Integer fw_commonid = (Integer) fwCommonInfoMap.get("fw_commonid");
		/**
		 * 向服务表发送请求
		 */
		paramMap.put("fw_commonid", fw_commonid);
		paramMap.put("fw_starttime", fw_starttime);
		paramMap.put("fw_endtime", fw_endtime);
		paramMap.put("visitNum", 0);
		paramMap.put("fw_work_style", fw_work_style);
		jsonStr = BmobAPI.mapToJSONStr(paramMap);
		result = insert("shopnc_fw", jsonStr);
		return result;
	}

	/**
	 * 关键字检索服务
	 * 
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void searchServiceByKeyword() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取关键字(前台提供)
		 */
		String keyword = BmobAPI.getStringValueFromJSONObject(params, "keyword");
		/**
		 * 拼接bql语句
		 */
		//过期不显示
		Long nowDate=System.currentTimeMillis()/1000;
//		paramMap.put("xq_endtime", "{\"$gt\":"+nowDate+"}");
		System.out.println(nowDate);
//		1469180878
//		1469610180
		String bql = "select * from shopnc_fb_classtotal where fw_state=1 and fw_verify=1 and fw_storage!=0 and fw_name like '%" + keyword    //yyp 添加
				+ "%' or fw_state=1 and fw_verify=1 and fw_storage!=0  and store_id=(select store_id from loveyou_store where store_name like '%" + keyword + "%' or store_banner like '%"+keyword+"%') "+
				"or fc_name like '%"+keyword+"%'";
				// bql = "select * from shopnc_fw where store_id=(select
				// store_id from loveyou_store where store_name like
				// '%"+keyword+"%')";
				/**
				 * 发送请求
				 */
		String result = findBQL(bql);
		
		if (result.indexOf("results\":[]") == -1) {
			JSONObject jo = (JSONObject) JSONObject.parse(result);
			JSONArray ja = jo.getJSONArray("results");

			JSONArray resu = new JSONArray();
			int i = 0;
			for (i = 0; i < ja.size(); i++) {
				JSONObject mo = ja.getJSONObject(i);
				Integer store_id = mo.getInteger("store_id");
				Long fw_endtime =mo.getLong("fw_endtime");
				
				System.out.println("fw_endtime:"+fw_endtime);
				if (store_id != null && store_id > 0) {
					String objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
					if(objectId!=null&&!"".equals(objectId)&&objectId.indexOf("Not Found:(")==-1){
						String storeInfo = findOne("loveyou_store", objectId);
						JSONObject on = JSONObject.parseObject(storeInfo);
						String store_logo = on.getString("store_label");
						String store_banner = on.getString("store_banner");
						mo.put("store_logo", store_logo);
						mo.put("store_banner", store_banner);
						if(fw_endtime>nowDate)
						resu.add(mo);
				
					}
				}
			}
			result = "{\"results\":" + resu.toJSONString() + "}";
		}
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 条件筛选
	 */
	@Deprecated
	public void searchServiceByCondition() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = params.getInteger("page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = params.getInteger("pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取服务类型(可以为空)
		 */
		Integer fc_id = params.getInteger("fc_id");
		/**
		 * 获取起始时间(前台提供)
		 */
		Integer fw_starttime = BmobAPI.getIntegerValueFromJSONObject(params, "fw_starttime");
		/**
		 * 获取结束时间(前台提供)
		 */
		Integer fw_endtime = BmobAPI.getIntegerValueFromJSONObject(params, ("fw_endtime"));
		/**
		 * 获取最大奖金(前台提供)
		 */
		Integer maxReward = BmobAPI.getIntegerValueFromJSONObject(params, "maxReward");
		/**
		 * 获取最小奖金(前台提供)
		 */
		Integer minReward = BmobAPI.getIntegerValueFromJSONObject(params, ("minReward"));
		/**
		 * 获取工作方式(前台提供、可以为空)
		 */
		Integer fw_work_style = params.getInteger("fw_work_style");
		/**
		 * 获取一级地区id(前台提供)
		 */
		Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(params, "areaid_1");
		/**
		 * 拼接bql
		 */
		String bql = "select * from shopnc_fw";
		String where = "";
		if (fc_id != -1) {
			where += " and fc_id=" + fc_id;
		}
		if (areaid_1 != -1) {
			where += " and areaid_1=" + areaid_1;
		}
		if (fw_work_style != -1) {
			where += " and fw_work_style=" + fw_work_style;
		}
		if (maxReward != -1) {
			where += " and fw_price<=" + maxReward;
		}
		if (minReward != -1) {
			where += " and fw_price>=" + minReward;
		}
		if (fw_starttime != -1) {
			where += " and fw_starttime>=" + fw_starttime;
		}
		if (fw_endtime != -1) {
			where += " and fw_starttime<=" + fw_endtime;
		}
		if (where.indexOf("and") != -1) {
			where = " where " + where.substring(5);
		}
		if (where.length() > 0) {
			bql += where;
		}
		bql += " limit " + skip + "," + pageSize;
		String result = findBQL(bql);
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 修改审核状态<br/>
	 * 1通过，0未通过，10审核中'
	 */
	@RequiresRoles("0")
	public void updateVerifyState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取服务编号(前台提供)
		 */
		Integer fb_id = BmobAPI.getIntegerValueFromJSONObject(params, "fb_id");
		/**
		 * 获取审核状态
		 */
		Integer fw_verify = BmobAPI.getIntegerValueFromJSONObject(params, "fw_verify");
		
		
		/**
		 * 获取服务表的objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fb_classtotal", "fb_id", fb_id);
		/**
		 * 封装参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("fw_verify", fw_verify);
		/*
		 * 姚永鹏添加
		 */
		if(fw_verify==0){
			String fw_verifyReason=BmobAPI.getStringValueFromJSONObject(params, "fw_verifyReason");
			paramsMap.put("fw_verifyReason", fw_verifyReason);
		}
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_fb_classtotal", objectId, whereJsonStr);

		ArrayList<String> al=BmobAPI.getAllObjectIdById("shopnc_fw", "{\"fb_id\":"+fb_id+"}");
		
		if(null!=al)
			for(String object:al){
				
				String mu=update("shopnc_fw", object, "{\"fw_verify\":" +fw_verify+"}");
				
			if(mu.indexOf("updateAt")==-1)
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			}
		/**
		 * 返回结果
		 */
		if (result.indexOf("updatedAt") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 修改服务状态
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateServiceState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取服务编号(前台提供)
		 */
		Integer fb_id = BmobAPI.getIntegerValueFromJSONObject(params, "fb_id");
		/**
		 * 获取服务状态
		 */
		Integer fw_state = BmobAPI.getIntegerValueFromJSONObject(params, "fw_state");
		/**
		 * 获取服务表的objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fb_classtotal", "fb_id", fb_id);
		/**
		 * 封装参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("fw_state", fw_state);
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_fb_classtotal", objectId, whereJsonStr);
		
		ArrayList<String> al=BmobAPI.getAllObjectIdById("shopnc_fw", "{\"fb_id\":"+fb_id+"}");
		
		if(null!=al)
			for(String object:al){
				
				String mu=update("shopnc_fw", object, "{\"fw_state\":" +fw_state+"}");
				
			if(mu.indexOf("updateAt")==-1)
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			}
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}


	/**
	 * 提供服务状态查询会员服务列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void searchServiceByMemberIdAndServiceState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取服务状态(前台提供)
		 */
		Integer fw_state = BmobAPI.getIntegerValueFromJSONObject(params, "fw_state");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_id", member_id);
		paramMap.put("fw_state", fw_state);
		paramMap.put("fw_verify", 1);// 默认审核中
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_fb_classtotal", jsonStr, (pageNum - 1) * pageSize, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 通过审核状态查询会员服务列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void searchServiceByMemberIdAndVerifyState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params,
		"member_id");
		/**
		 * 获取审核状态(前台提供)
		 */
		Integer fw_verify = BmobAPI.getIntegerValueFromJSONObject(params, "fw_verify");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_id", member_id);
		paramMap.put("fw_verify", fw_verify);
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_fb_classtotal", jsonStr, (pageNum - 1) * pageSize, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 服务明细查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getServiceInfoById() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取服务编号(前台提供)
		 */
		Integer fw_id = BmobAPI.getIntegerValueFromJSONObject(params, "fw_id");

		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");

		/**
		 * 获取服务表的objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fw", "fw_id", fw_id);
		/**
		 * 发送请求
		 */

		String result = findOne("shopnc_fw", objectId);
		/**
		 * 如果当前用户不是在浏览自己的服务详情，那么给浏览量加1
		 */
		JSONObject jo = JSONObject.parseObject(result);
		Integer member_id1 = jo.getInteger("member_id");
		if (member_id1 != member_id | member_id == null | member_id1 == null) {
			String jsonStr = "{\"visitNum\":{\"__op\":\"Increment\",\"amount\":1}}";
			update("shopnc_fw", objectId, jsonStr);
		}
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 查询店铺的服务 <br/>
	 * TODO库存大于0待实现
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getServiceByStoreId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 获取店铺编号(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
		/**
		 * 封装参数
		 */
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("store_id", store_id);
//		paramMap.put("fw_state", 1);// 默认上架
//		paramMap.put("fw_verify", 1);// 默认审核中
//		paramMap.put("fw_storage", "{\"$ne\":0}");
//
//		//过期不显示
		Long nowDate=System.currentTimeMillis()/1000;
////		paramMap.put("xq_endtime", "{\"$gt\":"+nowDate+"}");
//
//		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
//		System.out.println(jsonStr);
		/**
		 * 发送请求
		 */
		String jsonStr="{\"fw_state\":1,\"fw_verify\":1,\"store_id\":"+store_id+",\"fw_storage\":{\"$ne\":0},\"fw_endtime\":{\"$gt\":"+nowDate+"}}";
		String result = find("shopnc_fw", jsonStr, (pageNum - 1) * pageSize, pageSize, "-fw_addtime");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	
	/**
	 * 会员可服务时间库存查询
	 */
	@Deprecated
	public void searchServiceByTime() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取开始时间
		 */
		Long startTime = BmobAPI.getLongValueFromJSONObject(params, "startTime");
		/**
		 * 获取结束时间
		 */
		Long endTime = BmobAPI.getLongValueFromJSONObject(params, "endTime");
		/**
		 * fw_starttime > startTime and fw_endtime < endTime <br/>
		 * 发送请求(查询服务开始时间和结束时间在startTime与endTime之间的服务)
		 */
		String result = BmobAPI.findAllBetweenMinValueToMaxValue("shopnc_fw", "fw_starttime", startTime, endTime);
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 按地区查询服务
	 */
	@Deprecated
	public void getAllServiceByAreaId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取地区编号(前台提供)
		 */
		Integer area_id = BmobAPI.getIntegerValueFromJSONObject(params, "area_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("areaid_2", area_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = findAll("shopnc_fw", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 根据服务类别获取所有服务
	 */
	public void getAllServiceByFwxqClass() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取服务类别(前台提供)
		 */
		Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(params, "fc_id");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");

		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fc_id", fc_id);
		paramMap.put("fw_state", 1);// 默认上架
		// paramMap.put("fw_verify", 1);// 默认审核中
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_fb_classtotal", paramStr, skip, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 提供服务发布批次获取服务明细
	 */
	@Deprecated
	public void getServiceInfoByFbId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取服务类别(前台提供)
		 */
		Integer fb_id = BmobAPI.getIntegerValueFromJSONObject(params, "fb_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fb_classtotal", "fb_id", fb_id);
		/**
		 * 发送请求
		 */
		String result = findOne("shopnc_fb_classtotal", objectId);
		/**
		 * 返回结果
		 */
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	private String mergeAddressToAreaInfo(int areaId, int cityId) {

		String objectId = BmobAPI.getObjectIdById("loveyou_area", "area_id", areaId);
		String oneRow1 = findOne("loveyou_area", objectId);
		JSONObject jo = JSONObject.parseObject(oneRow1);
		String address = "";
		address = address + jo.getString("area_name") + "  ";

		String objectId1 = BmobAPI.getObjectIdById("loveyou_area", "area_id", cityId);
		String oneRow2 = findOne("loveyou_area", objectId1);
		jo = JSONObject.parseObject(oneRow2);
		address = address + jo.getString("area_name");

		return address;
	}

	/**
	 * yyp修改 按地区查询服务
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllServiceByAreaId1() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = params.getInteger("page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = params.getInteger("pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;

		/**
		 * 封装参数
		 */
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("fw_state", 1);// 默认上架
//		paramMap.put("fw_verify", 1);// 默认审核中
//		String paramStr = BmobAPI.mapToJSONStr(paramMap);

		/**
		 * 发送请求
		 */
		
//		String storeAll=findAll("loveyou_store");
//		System.out.println(storeAll);
		String storeAll=findColumns("loveyou_store", "store_id", "", skip, pageSize,"-updatedAt,-createdAt");
		String result="";
		if(storeAll.indexOf("[]")==-1){
		
			JSONObject oneO =(JSONObject) JSONObject.parse(storeAll);
			
			JSONArray tu=oneO.getJSONArray("results");
			
			JSONArray store=new JSONArray();
			
			for(Object o:tu){
				
				JSONObject wu=(JSONObject) o;
				Integer storeId=wu.getInteger("store_id");
				//过期不显示
				Long nowDate=System.currentTimeMillis()/1000;
//				paramMap.put("xq_endtime", "{\"$gt\":"+nowDate+"}");
				System.out.println(nowDate);
				String jsonStr="{\"fw_state\":1,\"fw_verify\":1,\"store_id\":"+storeId+",\"fw_storage\":{\"$ne\":0},\"fw_endtime\":{\"$gt\":"+nowDate+"}}";
				ArrayList<String> al=BmobAPI.getAllObjectIdById("shopnc_fb_classtotal", jsonStr);
				
				
				
				String resultd = findAll("shopnc_fw", jsonStr);
				if(resultd.indexOf("findColumns")!=-1){
					continue;
				}
				
				
				String  maxTime=null;
				String updateTime=null;
				
				JSONObject oned=null;
				if(null!=al)
					for(String ObjectId:al){
						
						String oneRow=BmobAPI.findOne("shopnc_fb_classtotal", ObjectId);
						
						JSONObject oneR=JSONObject.parseObject(oneRow);
						
						updateTime=oneR.getString("updatedAt");
						
						if(updateTime==null){
							updateTime=oneR.getString("createdAt");
							
						}
						if(maxTime==null){
							maxTime=updateTime;
						    oned =oneR;
						}
						else{
							
							if(compare_date(maxTime,updateTime)<0){
								maxTime=updateTime;
								oned=oneR;
							}
						}
						
						
					}
					if(oned!=null)
					store.add(oned);
				
			}
			
			result=store.toJSONString();	
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"results\":[]}").toString());
			return;
		}
//		String result = find("shopnc_fb_classtotal", paramStr, skip, pageSize, "-createdAt");
//		find(tableName, where, skip, limit, order)
//		String result = find("shopnc_fworder","", skip,
//		pageSize,"-add_time");
//		String result=findColumns("shop_fw", null, paramStr, skip, pageSize,
//		 "-fw_addtime");
		if (result.indexOf("results\":[]") == -1) {
//			JSONObject jo = (JSONObject) JSONObject.parse(result);
			JSONArray ja = JSONArray.parseArray(result);

			JSONArray resu = new JSONArray();
			int i = 0;
			for (i = 0; i < ja.size(); i++) {
				JSONObject mo = ja.getJSONObject(i);
				Integer store_id = mo.getInteger("store_id");
				if (store_id != null && store_id > 0) {
					String objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
					if(objectId!=null&&!"".equals(objectId)&&objectId.indexOf("Not Found:(")==-1){
						String storeInfo = findOne("loveyou_store", objectId);
						JSONObject on = JSONObject.parseObject(storeInfo);
						String store_logo = on.getString("store_label");
						String store_banner = on.getString("store_banner");
						mo.put("store_logo", store_logo);
						mo.put("store_banner", store_banner);
						resu.add(mo);
				
					}
				}
			}
			result = "{\"results\":" + resu.toJSONString() + "}";
		}

		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 获取大厅服务
	 */
	@Deprecated
	public void getAllService() {
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fw_verify", 1);// 审核状态为已通过
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		System.out.println(paramStr);
		String result = findAll("shopnc_fb_classtotal", paramStr);
		System.out.println(result);
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 获取服务列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getServiceListByFbId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取服务发布编号(前台提供)
		 */
		Integer fb_id = BmobAPI.getIntegerValueFromJSONObject(params, "fb_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fb_id", fb_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = findAll("shopnc_fw", paramStr);
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	@RequiresRoles("0")
	public void searchServiceByVerifyState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 获取会员编号(前台提供)
		 */
		// Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params,
		// "member_id");
		/**
		 * 获取审核状态(前台提供)
		 */
		Integer fw_verify = BmobAPI.getIntegerValueFromJSONObject(params, "fw_verify");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// paramMap.put("member_id", member_id);
		paramMap.put("fw_verify", fw_verify);
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		System.out.println(jsonStr);
		String result = find("shopnc_fb_classtotal", jsonStr, (pageNum - 1) * pageSize, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 我的服务查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getServiceByStoreIdw() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取每页显示数据的条数(前台提供)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		/**
		 * 获取页码(前台提供)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		/**
		 * 获取店铺编号(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("store_id", store_id);
		
		/**
		 * 库存大于0 TODO待实现
		 */
		// paramMap.put("fw_storage", 0);

		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_fb_classtotal", jsonStr, (pageNum - 1) * pageSize, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 服务详情
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getServiceInfoByFb() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取服务编号(前台提供)
		 */
		Integer fb_id = BmobAPI.getIntegerValueFromJSONObject(params, "fb_id");

		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");

		/**
		 * 获取服务表的objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fb_classtotal", "fb_id", fb_id);
		/**
		 * 发送请求
		 */

		String result = findOne("shopnc_fb_classtotal", objectId);
		/**
		 * 如果当前用户不是在浏览自己的服务详情，那么给浏览量加1
		 */
		JSONObject jo = JSONObject.parseObject(result);
		Integer member_id1 = jo.getInteger("member_id");
		if (member_id1 != member_id | member_id == null | member_id1 == null) {
			String jsonStr = "{\"visitNum\":{\"__op\":\"Increment\",\"amount\":1}}";
			update("shopnc_fw", objectId, jsonStr);
		}
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	 public int compare_date(String DATE1, String DATE2) {
	        
	        
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	        try {
	            Date dt1 = df.parse(DATE1);
	            Date dt2 = df.parse(DATE2);
	            if (dt1.getTime() > dt2.getTime()) {
	                return 1;
	            } else if (dt1.getTime() < dt2.getTime()) {
	                return -1;
	            } else {
	                return 0;
	            }
	        } catch (Exception exception) {
	            exception.printStackTrace();
	        }
	        return 0;
	    }

	 
		/**
		 * 筛选功能
		 */
	 @RequiresRoles(value={"1","0"},logical=Logical.OR)
		public void getServiceByCondition(){
			
			JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
			
			String member_name=BmobAPI.getStringValueFromJSONObject(jo, "member_name");
			
			JSONObject  jsonWhere=new JSONObject();
			
			/**
			 * 获取所有匹配的用户名的用户ID
			 */
			String ou="{\"member_name\":{\"$regex\":\""+member_name+"*\"}}";
			List<String> objectList=BmobAPI.getAllObjectIdById("loveyou_member", ou);
			
			if(objectList!=null){
				
				int[] m=new int[objectList.size()];
				
				JSONObject du=null;
				
				int i=0;
				
				for(String objectId :objectList){
					
					String memberInfo=findOne("loveyou_member", objectId);
					
					if(du!=null) du.clear();
					du=JSONObject.parseObject(memberInfo);
					
					Integer member_id=du.getInteger("member_id");
					m[i]=member_id;
					i++;
					
				}
				
				JSONObject yu=JSONObject.parseObject("{\"$in\":"+IntArrayToString(m)+"}");
				
				jsonWhere.put("member_id", yu);
			}
			
			Integer fc_id=BmobAPI.getIntegerValueFromJSONObject(jo, "fc_id");
			
			Integer areaId=BmobAPI.getIntegerValueFromJSONObject(jo, "areaid_1");
			
			Integer areaId2=BmobAPI.getIntegerValueFromJSONObject(jo, "areaid_2");
			
			Integer work_style=BmobAPI.getIntegerValueFromJSONObject(jo, "work_style");
			
			Integer priceMin=BmobAPI.getIntegerValueFromJSONObject(jo, "price_min");
			
			Integer priceMax=BmobAPI.getIntegerValueFromJSONObject(jo, "price_max");
			
			String origin_time=jo.getString("origin_time");
			
			String terminal_time=jo.getString("terminal_time");
			
			Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
			
			Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
			
			Integer skip=0;
			
			if(page!=null&&pageSize!=null&&page>0&&pageSize>1){
				skip=(page-1)*pageSize;
			}
			
			if(fc_id!=null&&fc_id>0){
				jsonWhere.put("fc_id", fc_id);
			}
			if(areaId!=null&&areaId>0){
				jsonWhere.put("areaid_1", areaId);
			}
			if(areaId2!=null&&areaId2>1){
				jsonWhere.put("areaid_2", areaId2);
			}
			if(work_style!=null&&work_style>=0&&work_style<2){
				jsonWhere.put("fw_work_style", work_style);
			}
			if(priceMin!=null&&priceMin>=0&&priceMax!=null&&priceMax>priceMin){
				
				String priceStr="{\"$gte\":"+priceMin+",\"$lte\":"+priceMax+"}";
				JSONObject jw=JSONObject.parseObject(priceStr);
				jsonWhere.put("fw_price", jw);
			}
			
			Long nowDate=System.currentTimeMillis()/1000;
			String jsonStr1="{\"$gt\":"+nowDate+"}";
			JSONObject jw3=JSONObject.parseObject(jsonStr1);
			jsonWhere.put("fw_endtime", jw3);
			if(origin_time!=null&&!"".equals(origin_time)&&terminal_time!=null&&!"".equals(terminal_time)){
				
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				
				Date d1=null,d2=null;
				try {
					d1=sdf.parse(origin_time);
					d2=sdf.parse(terminal_time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(d1!=null&&d2!=null){
					Long origin=d1.getTime()/1000;
					Long terminal=d2.getTime()/1000;
					
					String jsonStr="{\"$gte\":"+origin+",\"$lte\":"+terminal+"}";
					JSONObject jw=JSONObject.parseObject(jsonStr);
					
					jsonStr="{\"$gt\":"+nowDate+",\"$lte\":"+terminal+"}";
					JSONObject jw2=JSONObject.parseObject(jsonStr);
					
					jsonWhere.put("fw_starttime", jw);
					jsonWhere.put("fw_endtime", jw2);
				}
				
			}
			jsonWhere.put("fw_state", 1);
			jsonWhere.put("fw_verify", 1);
			String jsonStr="{\"$ne\":0}";
			JSONObject jw=JSONObject.parseObject(jsonStr);
			jsonWhere.put("fw_storage",jw);
			
			System.out.println(jsonWhere);
			
			String result="";
			
			if(skip>=0){
				
				result=find("shopnc_fw", jsonWhere.toJSONString(),skip,pageSize,"-createdAt");
			}else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"这怎么可能\"}").toString());
				return;
			}
			
			JSONArray resultArray=new JSONArray();
			
			try{
				JSONObject rd = (JSONObject) JSONObject.parse(result);

				com.alibaba.fastjson.JSONArray ja = rd.getJSONArray("results");
				
				int i = 0;
				for (i = 0; i < ja.size(); i++) {
					
					JSONObject du=ja.getJSONObject(i);
					
					if(du.containsKey("name"))
						
						du.remove("name");
					
					Integer m = du.getInteger("member_id");
					
					du.put("memberInfo",new UserInfoController().getPersonInfo(m));
					
					resultArray.add(du);
				}
				
				
			}catch(Exception e){
				
			}
			
			if(resultArray.toJSONString().indexOf("[")!=-1){
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,resultArray.toJSONString()).toString());
				return;
			}else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"操作失败\"}").toString());
			}
		}
		
		/**
		 * 发布服务，
		 * @author yyp
		 */
	 @RequiresRoles(value={"1","0"},logical=Logical.OR)
		public void publishService1() {

			JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
			
			/**
			 * 获取服务名称(前台提供)
			 */
			String fw_name = BmobAPI.getStringValueFromJSONObject(jo,"fw_name");
			/**
			 * 获取会员编号(前台提供)
			 */
			Integer member_id = BmobAPI.getIntegerValueFromJSONObject(jo,"member_id");
			/**
			 * 获取服务分类编号(前台提供)
			 */
			Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(jo,"fc_id");
			/**
			 * 获取服务分类名称
			 */
			String objectId = BmobAPI.getObjectIdById("shopnc_fwxq_class", "fc_id", fc_id);
			String fwClassInfo = findOne("shopnc_fwxq_class", objectId);
			Map<String, Object> fwClassInfoMap = BmobAPI.jsonStrToMap(fwClassInfo);
			String fc_name = (String) fwClassInfoMap.get("fc_name");
			/**
			 * 获取店铺编号(前台提供)
			 */
			Integer store_id = BmobAPI.getIntegerValueFromJSONObject(jo,"store_id");
			/**
			 * 获取店铺名称
			 */
			objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
			
			if(objectId==null){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户信息不合法或为空\"}").toString());
				return;
			}
			
			String storeInfo = findOne("loveyou_store", objectId);
			String store_name = (String) BmobAPI.jsonStrToMap(storeInfo).get("store_name");
			/**
			 * 服务添加时间
			 */
			long fw_addtime = System.currentTimeMillis() / 1000;
			/**
			 * 获取服务价格(前台提供)
			 */
			Integer fw_price = Integer.parseInt(BmobAPI.getStringValueFromJSONObject(jo,"fw_price")==null?"0":BmobAPI.getStringValueFromJSONObject(jo,"fw_price"));
			
			Integer expenditure_type=BmobAPI.getIntegerValueFromJSONObject(jo, "expenditure_type");
			/**
			 * 获取起始时间(前台提供)
			 */
			/**
			 * 参照时间
			 */
			String fw_keyday=BmobAPI.getStringValueFromJSONObject(jo,"fw_keyday");
			
			String pattern2="yyyy-MM-dd";
			SimpleDateFormat sdfKeyday =new SimpleDateFormat(pattern2);
			
			Date d=new Date();
			try {
				d=sdfKeyday.parse(fw_keyday);
			} catch (ParseException e1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式错误，请更改后重试\"}").toString());
				return;
			}
			
			String fw_startHour=BmobAPI.getStringValueFromJSONObject(jo,"fw_startHour");
			/**
			 * 获取结束时间(前台提供)
			 */
//			String  fw_endtime = BmobAPI.getStringValueFromJSONObject(jo,"fw_endtime");
			
			String fw_endHour=BmobAPI.getStringValueFromJSONObject(jo,"fw_endHour");
			
			
			
			String sta=sdf.format(d).substring(0, sdf.format(d).indexOf(" ")+1)+fw_startHour+":00";
			
			String en=sdf.format(d).substring(0, sdf.format(d).indexOf(" ")+1)+fw_endHour+":00";

			Date startdStr=null;
			
			Date enddStr=null;
			
			try {
				startdStr=sdf.parse(sta);
				enddStr=sdf.parse(en);
			} catch (ParseException e) {
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式错误，请更改后重试\"}").toString());
				return;
			}

			if(startdStr.after(enddStr)){
			
				Calendar   calendar   =   new   GregorianCalendar(); 
			     calendar.setTime(enddStr); 
			     calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
			     enddStr=calendar.getTime();
			}
			Long fw_starttime=startdStr.getTime()/1000;
			
			Long fw_endtime=enddStr.getTime()/1000;
			
			/**
			 * 获取工作方式(前台提供)
			 */
			Integer fw_work_style = BmobAPI.getIntegerValueFromJSONObject(jo,"fw_work_style");
			/**
			 * 获取一级地区id(前台提供)
			 */
			Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(jo,"areaid_1");
			/**
			 * 获取二级地区id(前台提供)
			 */
			Integer areaid_2 = BmobAPI.getIntegerValueFromJSONObject(jo,"areaid_2");
			String fw_area="";
			if(areaid_1>0&&areaid_2>0){
				fw_area= mergeAddressToAreaInfo(areaid_1, areaid_2);
			}	
			/**
			 * 获取重复方式(前台提供)
			 */
			String fw_repeat_style = BmobAPI.getStringValueFromJSONObject(jo,"fw_repeat_style");
			/**
			 * 获取服务描述(前台提供，可为空)
			 */
			String fw_body = BmobAPI.getStringValueFromJSONObject(jo,"fw_body");
			/**
			 * 获取停止重复时间(前台提供) FIXME ggj 修改
			 */
			String repeat_endtime=BmobAPI.getStringValueFromJSONObject(jo,"repeat_endtime");
			Long fw_repeat_endtime=0l;
			try {
				if(repeat_endtime!=null&&!repeat_endtime.equals("0"))
					d=sdfKeyday.parse(repeat_endtime);
				
				fw_repeat_endtime=d.getTime();
			} catch (ParseException e1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式错误，请更改后重试\"}").toString());
				return;
			}
			
			/**
			 * 封装shopnc_fb_classtotal发布服务统计表需要的参数
			 */
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("fw_name", fw_name);
			paramMap.put("member_id", member_id);
			paramMap.put("fc_id", fc_id);
			paramMap.put("fc_name", fc_name);
			paramMap.put("fw_price", fw_price);
			paramMap.put("store_id", store_id);
			paramMap.put("fw_state", 1);// 默认上架
			paramMap.put("fw_verify", 10);// 默认审核中

			//yyp 添加
			paramMap.put("fw_startHour", fw_startHour);
			paramMap.put("fw_endHour", fw_endHour);
			paramMap.put("fw_starttime", fw_starttime);
			paramMap.put("fw_endtime", fw_endtime);
			paramMap.put("fw_work_style", fw_work_style);
			paramMap.put("fw_area", fw_area);//服务区域存序列化数组(也可以存经纬度的值)
			paramMap.put("fw_repeat_style", fw_repeat_style);
			paramMap.put("fw_repeat_endtime", fw_repeat_endtime/1000);
			paramMap.put("fw_body", fw_body);
			paramMap.put("store_name", store_name);
			paramMap.put("expenditure_type", expenditure_type);
			
			String jsonStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 向服务公共表发送请求
			 */
			String result = insert("shopnc_fb_classtotal", jsonStr);

			if (result.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"参数错误，不能插入到数据库\"}").toString());
				return;
			}
			/**
			 * 解析result字符串
			 */
			Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
			objectId = (String) resultMap.get("objectId");
			/**
			 * 获取发布表id
			 */
			String totalInfo = findOne("shopnc_fb_classtotal", objectId);
			Map<String, Object> totalInfoMap = BmobAPI.jsonStrToMap(totalInfo);
			Integer fb_id = (Integer) totalInfoMap.get("fb_id");
			/**
			 * 封装参数
			 */
			paramMap.clear();
			paramMap.put("member_id", member_id);
			paramMap.put("fw_name", fw_name);
			paramMap.put("store_id", store_id);
			paramMap.put("store_name", store_name);
			paramMap.put("fc_id", fc_id);
			paramMap.put("fc_name", fc_name);
			paramMap.put("fw_repeat_style", fw_repeat_style);
			paramMap.put("fw_repeat_endtime", fw_repeat_endtime/1000);
			paramMap.put("fw_price", fw_price);
			paramMap.put("fw_state", 1);// 默认上架
			paramMap.put("fw_verify", 10);// 默认审核中
			paramMap.put("fw_addtime", fw_addtime);// 默认审核中
			
			//yyp 添加
			paramMap.put("fw_startHour", fw_startHour);
			paramMap.put("fw_endHour", fw_endHour);
			paramMap.put("fw_commend", 0);// 服务推荐 1是，0否，默认为0
			paramMap.put("fw_freight", 0);// 交通费 0为免交通费
			paramMap.put("fw_vat", 0);// 是否开具增值税发票 1是，0否
			paramMap.put("areaid_1", areaid_1);
			paramMap.put("areaid_2", areaid_2);
			if(!fw_area.equals(""))
				paramMap.put("fw_area", fw_area);
			paramMap.put("fw_body", fw_body);// 服务描述
			paramMap.put("fb_id", fb_id);// 发布编号
			paramMap.put("fw_click", 0);// 点击次数，默认为0
			paramMap.put("fw_salenum", 0);// 销售数量，默认为0
			paramMap.put("fw_collect", 0);// 收藏数量，默认为0
			paramMap.put("expenditure_type", expenditure_type);
			
			jsonStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 解析重复方式
			 */
			String[] repeatStrings = fw_repeat_style.split(",");
			if (repeatStrings.length == 1) {// 每天或者从不
				/**
				 * 不重复
				 */
				
				String repeatStyle = repeatStrings[0];
				if (repeatStyle.equals(RepeatStyle.ONCE)) {
					
					
					result = sendPublishServiceRequest(fw_starttime, fw_endtime, fw_work_style, paramMap, jsonStr);
					/**
					 * 每天
					 */
				} else if (repeatStyle.equals(RepeatStyle.EVERYDAY)) {
					/**
					 * 遍历今天到重复截止时间
					 */
					Long startTime = startdStr.getTime();
					
					Long endTime =enddStr.getTime();
					Long oneDay = 1000 * 60 * 60 * 24l;
					while (endTime <= fw_repeat_endtime) {
						/**
						 * 发送请求
						 */
						result = sendPublishServiceRequest(startTime/1000, endTime/1000, fw_work_style, paramMap, jsonStr);
						startTime += oneDay;
						endTime+=oneDay;
					}
					/**
					 * 每个工作日
					 */
				} else if (repeatStyle.equals(RepeatStyle.EVERYWORKDAY)) {
					/**
					 * 遍历今天到重复截止时间
					 */
					Calendar start = Calendar.getInstance();
					Long startTime = startdStr.getTime();
					
					Long endTime = enddStr.getTime();
					Long oneDay = 1000 * 60 * 60 * 24l;
					while (endTime <= fw_repeat_endtime) {
						start.setTimeInMillis(startTime);
						/**
						 * 判断是否是周末
						 */
						if (!(start.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
								|| start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
								
							start.setTimeInMillis(endTime);;
							if (!(start.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
									|| start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
								
								result = sendPublishServiceRequest(startTime/1000, endTime/1000, fw_work_style, paramMap, jsonStr);
							}
						}
						startTime += oneDay;
						endTime+=oneDay;
					}
				}else if(repeatStyle.equals(RepeatStyle.EVERYWEEK)){
					
					/**
					 * 遍历今天到重复截止时间
					 */
					Long startTime = startdStr.getTime();
					
					Long endTime =enddStr.getTime();
					Long oneDay = 1000 * 60 * 60 * 24*7l;
					while (endTime <= fw_repeat_endtime) {
						/**
						 * 发送请求
						 */
						result = sendPublishServiceRequest(startTime/1000, endTime/1000, fw_work_style, paramMap, jsonStr);
						startTime += oneDay;
						endTime+=oneDay;
					}
				}
			} else {
				repeatStrings = fw_repeat_style.substring(fw_repeat_style.indexOf(",") + 1, fw_repeat_style.length())
						.split(",");
				/**
				 * 遍历今天到重复截止时间
				 */
				Calendar start = Calendar.getInstance();
				Long startTime = startdStr.getTime();
				Long endTime = enddStr.getTime();
				//3600*1000为多加了一小时  3600*24 为加一天
				Long oneDay = 1000 * 60 * 60 * 24l;
				while (endTime <= fw_repeat_endtime) {
					start.setTimeInMillis(startTime);
					/**
					 * 判断是否是选择的日期
					 */
					for (String string : repeatStrings) {
						Integer i = Integer.parseInt(string);
						if (start.get(Calendar.DAY_OF_WEEK) == i) {
							/**
							 * 发送请求
							 */
							result = sendPublishServiceRequest(startTime/1000, endTime/1000, fw_work_style, paramMap, jsonStr);
						}
					}
					startTime += oneDay;
					endTime+=oneDay;
				}
			}
			/**
			 * 返回结果
			 */
			if (result.indexOf("objectId") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			} else {
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
			}
		}
		/**
		 * yyp 添加
		 * @param fw_starttime
		 * @param fw_endtime
		 * @param fw_work_style
		 * @param paramMap
		 * @param jsonStr
		 * @return
		 */
		private String sendPublishServiceRequest(Long fw_starttime, Long fw_endtime, Integer fw_work_style,
				Map<String, Object> paramMap, String jsonStr) {
			String objectId;
			String result;
			/**
			 * 向服务公共表发送请求
			 */
			JSONObject jo=JSONObject.parseObject(jsonStr);
			jo.put("fw_starttime", fw_starttime);
			jo.put("fw_endtime", fw_endtime);
			result = insert("shopnc_fw_common", jo.toJSONString());
			/**
			 * 解析result字符串
			 */
			Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
			objectId = (String) resultMap.get("objectId");
			/**
			 * 获取服务公共表id
			 */
			String fwCommonInfo = findOne("shopnc_fw_common", objectId);
			Map<String, Object> fwCommonInfoMap = BmobAPI.jsonStrToMap(fwCommonInfo);
			Integer fw_commonid = (Integer) fwCommonInfoMap.get("fw_commonid");
			/**
			 * 向服务表发送请求
			 */
			paramMap.put("fw_commonid", fw_commonid);
			paramMap.put("fw_starttime", fw_starttime);
			paramMap.put("fw_endtime", fw_endtime);
			paramMap.put("visitNum", 0);
			paramMap.put("fw_work_style", fw_work_style);
			jsonStr = BmobAPI.mapToJSONStr(paramMap);
			
			result = insert("shopnc_fw", jsonStr);
			return result;
		}

		/**
		 * yyp 
		 */
		@RequiresRoles(value={"1","0"},logical=Logical.OR)
		public void updateService1(){
			
			JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());

			/**
			 * 获取服务名称(前台提供)
			 */
			//获取服务发布编号
			Integer fb_id = BmobAPI.getIntegerValueFromJSONObject(jo,"fb_id");
			
			String fw_name = BmobAPI.getStringValueFromJSONObject(jo,"fw_name");
			/**
			 * 获取会员编号(前台提供)
			 */
			Integer member_id = BmobAPI.getIntegerValueFromJSONObject(jo,"member_id");
			/**
			 * 获取服务分类编号(前台提供)
			 */
			Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(jo,"fc_id");
			/**
			 * 获取服务分类名称
			 */
			String objectId = BmobAPI.getObjectIdById("shopnc_fwxq_class", "fc_id", fc_id);
			String fwClassInfo = findOne("shopnc_fwxq_class", objectId);
			Map<String, Object> fwClassInfoMap = BmobAPI.jsonStrToMap(fwClassInfo);
			String fc_name = (String) fwClassInfoMap.get("fc_name");
			
			Integer expenditure_type=BmobAPI.getIntegerValueFromJSONObject(jo, "expenditure_type");
			/**
			 * 获取店铺编号(前台提供)
			 */
			Integer store_id = BmobAPI.getIntegerValueFromJSONObject(jo,"store_id");
			/**
			 * 获取店铺名称
			 */
			objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
			String storeInfo = findOne("loveyou_store", objectId);
			String store_name = (String) BmobAPI.jsonStrToMap(storeInfo).get("store_name");
			/**
			 * 服务添加时间
			 */
			long fw_addtime = System.currentTimeMillis() / 1000;
			/**
			 * 获取服务价格(前台提供)
			 */
			Integer fw_price = Integer.parseInt(BmobAPI.getStringValueFromJSONObject(jo,"fw_price"));
		
			/**
			 * 参照时间
			 */
			String fw_keyday=BmobAPI.getStringValueFromJSONObject(jo,"fw_keyday");
			
			String pattern2="yyyy-MM-dd";
			SimpleDateFormat sdfKeyday =new SimpleDateFormat(pattern2);
			
			Date d=new Date();
			try {
				d=sdfKeyday.parse(fw_keyday);
			} catch (ParseException e1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式错误，请更改后重试\"}").toString());
				return;
			}
			
			String fw_startHour=BmobAPI.getStringValueFromJSONObject(jo,"fw_startHour");
			/**
			 * 获取结束时间(前台提供)
			 */
			
			String fw_endHour=BmobAPI.getStringValueFromJSONObject(jo,"fw_endHour");
			
			
			
			String sta=sdf.format(d).substring(0, sdf.format(d).indexOf(" ")+1)+fw_startHour+":00";
			
			String en=sdf.format(d).substring(0, sdf.format(d).indexOf(" ")+1)+fw_endHour+":00";

			Date startdStr=null;
			
			Date enddStr=null;
			
			try {
				startdStr=sdf.parse(sta);
				enddStr=sdf.parse(en);
			} catch (ParseException e) {
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式错误，请更改后重试\"}").toString());
				return;
			}

			if(startdStr.after(enddStr)){
			
				Calendar   calendar   =   new   GregorianCalendar(); 
			     calendar.setTime(enddStr); 
			     calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
			     enddStr=calendar.getTime();
			}
			Long fw_starttime=startdStr.getTime()/1000;
			
			Long fw_endtime=enddStr.getTime()/1000;
			
			/**
			 * 获取工作方式(前台提供)
			 */
			Integer fw_work_style = BmobAPI.getIntegerValueFromJSONObject(jo,"fw_work_style");
			/**
			 * 获取一级地区id(前台提供)
			 */
			Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(jo,"areaid_1");
			/**
			 * 获取二级地区id(前台提供)
			 */
			Integer areaid_2 = BmobAPI.getIntegerValueFromJSONObject(jo,"areaid_2");
			String fw_area="";
			if(areaid_1>0&&areaid_2>0){
				fw_area= mergeAddressToAreaInfo(areaid_1, areaid_2);
			}	
			/**
			 * 获取重复方式(前台提供)
			 */
			String fw_repeat_style = BmobAPI.getStringValueFromJSONObject(jo,"fw_repeat_style");
			/**
			 * 获取服务描述(前台提供，可为空)
			 */
			String fw_body = BmobAPI.getStringValueFromJSONObject(jo,"fw_body");
			/**
			 * 获取停止重复时间(前台提供) FIXME ggj 修改
			 */
			String repeat_endtime=BmobAPI.getStringValueFromJSONObject(jo,"repeat_endtime");
			Long fw_repeat_endtime=0l;
			try {
				if(repeat_endtime!=null&&!repeat_endtime.equals("0"))
					d=sdfKeyday.parse(repeat_endtime);
				
				fw_repeat_endtime=d.getTime();
			} catch (ParseException e1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式错误，请更改后重试\"}").toString());
				return;
			}
			
			/**
			 * 封装shopnc_fb_classtotal发布服务统计表需要的参数
			 */
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("fw_name", fw_name);
//			paramMap.put("member_id", member_id);
			paramMap.put("fc_id", fc_id);
			paramMap.put("fc_name", fc_name);
			paramMap.put("fw_price", fw_price);
			paramMap.put("store_id", store_id);
			paramMap.put("fw_state", 1);// 默认上架
			paramMap.put("fw_verify", 10);// 默认审核中

			//yyp 添加
			paramMap.put("fw_startHour", fw_startHour);
			paramMap.put("fw_endHour", fw_endHour);
			paramMap.put("fw_starttime", fw_starttime);
			paramMap.put("fw_endtime", fw_endtime);
			paramMap.put("fw_work_style", fw_work_style);
			 paramMap.put("fw_area", fw_area);//服务区域存序列化数组(也可以存经纬度的值)
			paramMap.put("fw_repeat_style", fw_repeat_style);
			paramMap.put("fw_repeat_endtime", fw_repeat_endtime/1000);
			paramMap.put("fw_body", fw_body);
			paramMap.put("store_name", store_name);
			paramMap.put("expenditure_type", expenditure_type);
			
			String jsonStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 向服务公共表发送请求
			 */
			
			String fbObjectId=BmobAPI.getObjectIdById("shopnc_fb_classtotal", "fb_id", fb_id);
			
			if(fbObjectId==null||fbObjectId.length()<7){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"fb编号不存在，不能修改此服务\"}").toString());
				return;
			}

			String result = update("shopnc_fb_classtotal",fbObjectId, jsonStr);
			
			if (result.indexOf("At") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"参数错误，不能插入到数据库\"}").toString());
				return;
			}
			
			paramMap.put("fb_id", fb_id);
			/**
			 * 获取所有该批次的objectId并删除
			 */
			ArrayList<String> list = BmobAPI.getAllObjectIdById("shopnc_fw", "{\"fb_id\":" + fb_id + ",\"fw_storage\":{\"$ne\":0}}");
			if(list!=null)
			for (String fw_objeceId : list) {
				delete("shopnc_fw", fw_objeceId);
			}

			/**
			 * TODO 删除服务公共表中数据
			 */

			list = BmobAPI.getAllObjectIdById("shopnc_fw_common", "{\"fb_id\":" + fb_id + "}");
			if(list!=null)
			for (String fw_common_objeceId : list) {
				delete("shopnc_fw_common", fw_common_objeceId);
			}
			/**
			 * 封装参数
			 */
			paramMap.clear();
			paramMap.put("member_id", member_id);
			paramMap.put("fw_name", fw_name);
			paramMap.put("store_id", store_id);
			paramMap.put("store_name", store_name);
			paramMap.put("fc_id", fc_id);
			paramMap.put("fc_name", fc_name);
			paramMap.put("fw_repeat_style", fw_repeat_style);
			paramMap.put("fw_repeat_endtime", fw_repeat_endtime/1000);
			paramMap.put("fw_price", fw_price);
			paramMap.put("fw_state", 1);// 默认上架
			paramMap.put("fw_verify", 10);// 默认审核中
			paramMap.put("fw_addtime", fw_addtime);// 默认审核中
			
			//yyp 添加
			paramMap.put("fw_startHour", fw_startHour);
			paramMap.put("fw_endHour", fw_endHour);
			paramMap.put("fw_commend", 0);// 服务推荐 1是，0否，默认为0
			paramMap.put("fw_freight", 0);// 交通费 0为免交通费
			paramMap.put("fw_vat", 0);// 是否开具增值税发票 1是，0否
			paramMap.put("areaid_1", areaid_1);
			paramMap.put("areaid_2", areaid_2);
			if(!fw_area.equals(""))
				paramMap.put("fw_area", fw_area);
			paramMap.put("fw_body", fw_body);// 服务描述
			paramMap.put("fb_id", fb_id);// 发布编号
			paramMap.put("fw_click", 0);// 点击次数，默认为0
			paramMap.put("fw_salenum", 0);// 销售数量，默认为0
			paramMap.put("fw_collect", 0);// 收藏数量，默认为0
			paramMap.put("expenditure_type", expenditure_type);
			
			jsonStr = BmobAPI.mapToJSONStr(paramMap);
			/**
			 * 解析重复方式
			 */
			String[] repeatStrings = fw_repeat_style.split(",");
			if (repeatStrings.length == 1) {// 每天或者从不
				/**
				 * 不重复
				 */
				
				String repeatStyle = repeatStrings[0];
				if (repeatStyle.equals(RepeatStyle.ONCE)) {
					
					
					result = sendPublishServiceRequest(fw_starttime, fw_endtime, fw_work_style, paramMap, jsonStr);
					/**
					 * 每天
					 */
				} else if (repeatStyle.equals(RepeatStyle.EVERYDAY)) {
					/**
					 * 遍历今天到重复截止时间
					 */
					Long startTime = startdStr.getTime();
					
					Long endTime =enddStr.getTime();
					Long oneDay = 1000 * 60 * 60 * 24l;
					while (endTime <= fw_repeat_endtime) {
						/**
						 * 发送请求
						 */
						result = sendPublishServiceRequest(startTime/1000, endTime/1000, fw_work_style, paramMap, jsonStr);
						startTime += oneDay;
						endTime+=oneDay;
					}
					/**
					 * 每个工作日
					 */
				} else if (repeatStyle.equals(RepeatStyle.EVERYWORKDAY)) {
					/**
					 * 遍历今天到重复截止时间
					 */
					Calendar start = Calendar.getInstance();
					Long startTime = startdStr.getTime();
					
					Long endTime = enddStr.getTime();
					Long oneDay = 1000 * 60 * 60 * 24l;
					while (endTime <= fw_repeat_endtime) {
						start.setTimeInMillis(startTime);
						/**
						 * 判断是否是周末
						 */
						if (!(start.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
								|| start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
								
							start.setTimeInMillis(endTime);;
							if (!(start.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
									|| start.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
								
								result = sendPublishServiceRequest(startTime/1000, endTime/1000, fw_work_style, paramMap, jsonStr);
							}
						}
						startTime += oneDay;
						endTime+=oneDay;
					}
				}else if(repeatStyle.equals(RepeatStyle.EVERYWEEK)){
					
					/**
					 * 遍历今天到重复截止时间
					 */
					Long startTime = startdStr.getTime();
					
					Long endTime =enddStr.getTime();
					Long oneDay = 1000 * 60 * 60 * 24*7l;
					while (endTime <= fw_repeat_endtime) {
						/**
						 * 发送请求
						 */
						result = sendPublishServiceRequest(startTime/1000, endTime/1000, fw_work_style, paramMap, jsonStr);
						startTime += oneDay;
						endTime+=oneDay;
					}
				}
			} else {
				repeatStrings = fw_repeat_style.substring(fw_repeat_style.indexOf(",") + 1, fw_repeat_style.length())
						.split(",");
				/**
				 * 遍历今天到重复截止时间
				 */
				Calendar start = Calendar.getInstance();
				Long startTime = startdStr.getTime();
				Long endTime = enddStr.getTime();
				//3600*1000为多加了一小时  3600*24 为加一天
				Long oneDay = 1000 * 60 * 60 * 24l;
				while (endTime <= fw_repeat_endtime) {
					start.setTimeInMillis(startTime);
					/**
					 * 判断是否是选择的日期
					 */
					for (String string : repeatStrings) {
						Integer i = Integer.parseInt(string);
						if (start.get(Calendar.DAY_OF_WEEK) == i) {
							/**
							 * 发送请求
							 */
							result = sendPublishServiceRequest(startTime/1000, endTime/1000, fw_work_style, paramMap, jsonStr);
						}
					}
					startTime += oneDay;
					endTime+=oneDay;
				}
			}
			/**
			 * 返回结果
			 */
			if (result.indexOf("objectId") == -1) {
				renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			} else {
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
			}
		}
		
		private String IntArrayToString(int[]  m){
			
			String str="";
			
			for(int i=0;i<m.length;i++){
				
				if(i<m.length-1)
					str+=m[i]+",";
				else 
					str+=m[i];
			}
			
			return "["+str+"]";
		}
}
