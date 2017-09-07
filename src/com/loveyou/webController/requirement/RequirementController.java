package com.loveyou.webController.requirement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.accountfunds.AccountFundsController;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.userinfo.UserInfoController;

/**
 * 
 * @ClassName: RequirementController
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月6日 下午2:48:57
 * 
 * 
 * 
 * 
 */
public class RequirementController extends Bmob {
	/**
	 * 一级需求类型查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getFirstClassRequirementType() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize =BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 封装条件参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("fc_parent_id", 0);
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_fwxq_class", whereJsonStr, skip, pageSize);
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
	 * 二级需求类别查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getSecondClassRequirementType() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
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
		String result = find("shopnc_fwxq_class", whereJsonStr, skip, pageSize);
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
	 * 获取所有需求信息(悬赏)
	 */
	@RequiresRoles("0")
	public void getAllRequirement() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 发送请求
		 */
		String result = find("shopnc_xq", "", skip, pageSize, "-createdAt");
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
	 * 会员发布需求(悬赏)
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void publishRequirement() {
	
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求名称(前台提供)
		 */
		String xq_name = BmobAPI.getStringValueFromJSONObject(jo,"xq_name");
		/**
		 * 获取用户资料
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(jo,"member_id");
		/**
		 * 获取需求分类编号(前台提供)
		 */
		Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(jo,"fc_id");
		/**
		 * 获取需求分类名称
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fwxq_class", "fc_id", fc_id);
		String xqClassInfo = findOne("shopnc_fwxq_class", objectId);
		String fc_name = (String) BmobAPI.jsonStrToMap(xqClassInfo).get("fc_name");
		/**
		 * 需求添加时间
		 */
		long xq_addtime = System.currentTimeMillis() / 1000;
		/**
		 * 获取需求悬赏价格(前台提供)
		 */
		Integer xq_price =BmobAPI.getIntegerValueFromJSONObject(jo,"xq_price");
		/**
		 * 获取账户余额
		 */
		/**
		 * 获取objectId
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		/**
		 * 获取会员信息
		 */
		
		String xq_starttimeString=BmobAPI.getStringValueFromJSONObject(jo, "xq_starttimeString");
		
		String xq_endtimeString=BmobAPI.getStringValueFromJSONObject(jo, "xq_endtimeString");
		
		String pattern="yyyy-MM-dd HH:mm";
		
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		Date d=null;
		
		Long xq_starttime;
		
		Long xq_endtime;
		try {
			d=sdf.parse(xq_starttimeString);
			xq_starttime=d.getTime()/1000;
			d=sdf.parse(xq_endtimeString);
			xq_endtime=d.getTime()/1000;
		} catch (ParseException e) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式应该为yyyy-MM-dd HH:mm，否则无效\"}").toString());
			return;
		}
		
		
		/**
		 * 获取工作方式(前台提供)
		 */
		Integer xq_work_style = BmobAPI.getIntegerValueFromJSONObject(jo,"xq_work_style");
		/**
		 * 获取一级地区id(前台提供)
		 */
		Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(jo,"areaid_1");
		/**
		 * 获取二级地区id(前台提供)
		 */
		Integer areaid_2 = BmobAPI.getIntegerValueFromJSONObject(jo,"areaid_2");

		String xq_area = mergeAddressToAreaInfo(areaid_1, areaid_2);
		/**
		 * 获取年龄(前台提供)
		 */
		Integer xq_age = BmobAPI.getIntegerValueFromJSONObject(jo,"xq_age");
		
		Integer expenditure_type=BmobAPI.getIntegerValueFromJSONObject(jo, "expenditure_type"); 
		
		/**
		 * 获取性别(前台提供)
		 */
		Integer xq_sex = BmobAPI.getIntegerValueFromJSONObject(jo,"xq_sex");
		/**
		 * 获取需求描述(前台提供)
		 */
		String xq_body = BmobAPI.getStringValueFromJSONObject(jo,"xq_body");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_name", xq_name);
		paramMap.put("member_id", member_id);
		paramMap.put("fc_id", fc_id);
		paramMap.put("fc_name", fc_name);
		paramMap.put("xq_state", 1);// 1上架
		paramMap.put("xq_addtime", xq_addtime);
		paramMap.put("xq_price", xq_price);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("xq_area", xq_area);
		paramMap.put("xq_body", xq_body);
		paramMap.put("xq_verify", 10);// 10审核中
		paramMap.put("xq_commend", 0);// 需求推荐 1是，0否，默认为0
		paramMap.put("xq_freight", 0);// 报销交通费 0为不报交通费
		paramMap.put("xq_vat", 0);// 是否开具增值税发票 1是，0否
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向需求公共表发送请求
		 */
		String result = insert("shopnc_xq_common", jsonStr);
		/**
		 * 解析result字符串
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		objectId = (String) resultMap.get("objectId");
		/**
		 * 获取服务公共表id
		 */
		String xqCommonInfo = findOne("shopnc_xq_common", objectId);
		Map<String, Object> xqCommonInfoMap = BmobAPI.jsonStrToMap(xqCommonInfo);
		Integer xq_commonid = (Integer) xqCommonInfoMap.get("xq_commonid");
		/**
		 * 向服务表发送请求
		 */
		paramMap.put("xq_starttime", xq_starttime);
		paramMap.put("xq_endtime", xq_endtime);
		paramMap.put("xq_work_style", xq_work_style);
		paramMap.put("xq_age", xq_age);
		paramMap.put("xq_sex", xq_sex);
		paramMap.put("xq_commonid", xq_commonid);
		paramMap.put("visitNum", 0);
		paramMap.put("expenditure_type", expenditure_type);
		jsonStr = BmobAPI.mapToJSONStr(paramMap);
		result = insert("shopnc_xq", jsonStr);
		/**
		 * 返回结果
		 */
		
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			
			new AccountFundsController().writerLog(member_id, "系统", JsonResult.LG_TYPE_ORDER_PAY_REQUIREMENT,true, xq_price);
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 会员需求高级查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void searchRequirementByCondition() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取需求类型(可以为空)
		 */
		Integer fc_id = params.getInteger("fc_id");
		/**
		 * 获取起始时间(前台提供)
		 */
		Integer xq_starttime = BmobAPI.getIntegerValueFromJSONObject(params, "xq_starttime");
		/**
		 * 获取结束时间(前台提供)
		 */
		Integer xq_endtime = BmobAPI.getIntegerValueFromJSONObject(params, "xq_endtime");
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
		Integer xq_work_style = params.getInteger("fw_work_style");
		/**
		 * 获取一级地区id(前台提供)
		 */
		Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(params, "areaid_1");
		/**
		 * 拼接bql
		 */
		String bql = "select * from shopnc_xq";
		String where = "";
		if (fc_id != -1) {
			where += " and fc_id=" + fc_id;
		}
		if (areaid_1 != -1) {
			where += " and areaid_1=" + areaid_1;
		}
		if (xq_work_style != -1) {
			where += " and xq_work_style=" + xq_work_style;
		}
		if (maxReward != -1) {
			where += " and fw_price<=" + maxReward;
		}
		if (minReward != -1) {
			where += " and fw_price>=" + minReward;
		}
		if (xq_starttime != -1) {
			where += " and xq_starttime>=" + xq_starttime;
		}
		if (xq_endtime != -1) {
			where += " and xq_starttime<=" + xq_endtime;
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
	 * 需求详细信息查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getRequirementById() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求id(前台提供)
		 */
		Integer xq_id = BmobAPI.getIntegerValueFromJSONObject(params, "xq_id");

		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		/**
		 * 查询需求信息
		 */
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该需求不存在\"}").toString());
			return;
		}
		String result = findOne("shopnc_xq", objectId);
		/**
		 * 如果当前用户不是在浏览自己的服务详情，那么给浏览量加1
		 */
		JSONObject jo = JSONObject.parseObject(result);
		Integer member_id1 = jo.getInteger("member_id");
		if (member_id1 != member_id | member_id == null | member_id1 == null) {
			String jsonStr = "{\"visitNum\":{\"__op\":\"Increment\",\"amount\":1}}";
			update("shopnc_xq", objectId, jsonStr);
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
	 * 需求上架
	 */
	@Deprecated
	public void putawayRequirement() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求编号(前台提供)
		 */
		Integer xq_id = BmobAPI.getIntegerValueFromJSONObject(params, "xq_id");
		/**
		 * 获取需求公共表编号
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		String xqInfo = findOne("shopnc_xq", objectId);
		Map<String, Object> xqInfoMap = BmobAPI.jsonStrToMap(xqInfo);
		Integer xq_commonid = (Integer) xqInfoMap.get("xq_commonid");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_verify", 1);// 通过审核
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向需求基础表发送请求
		 */
		String result = update("shopnc_xq", objectId, paramStr);
		objectId = BmobAPI.getObjectIdById("shopnc_xq_common", "xq_commonid", xq_commonid);
		/**
		 * 向需求公共表发送请求
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_xq_common", "xq_commonid", xq_commonid);
		result = update("shopnc_xq_common", objectId, paramStr);
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
	 * 需求下架
	 */
	@Deprecated
	public void putOffRequirement() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求编号(前台提供)
		 */
		Integer xq_id = BmobAPI.getIntegerValueFromJSONObject(params, "xq_id");
		/**
		 * 获取需求公共表编号
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		String xqInfo = findOne("shopnc_xq", objectId);
		Map<String, Object> xqInfoMap = BmobAPI.jsonStrToMap(xqInfo);
		Integer xq_commonid = (Integer) xqInfoMap.get("xq_commonid");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_state", 0);// 需求状态改为下架
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向需求基础表发送请求
		 */
		String result = update("shopnc_xq", objectId, paramStr);
		/**
		 * 向需求公共表发送请求
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_xq_common", "xq_commonid", xq_commonid);
		result = update("shopnc_xq_common", objectId, paramStr);
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
	 * 修改需求
	 */
	public void updateRequirement() {
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer xq_id = BmobAPI.getIntegerValueFromJSONObject(jo,"xq_id");
		
		String xqObject =BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		
		if(xqObject!=null){
			
			String du=findOne("shopnc_xq", xqObject);
			JSONObject xqObjectJson=JSONObject.parseObject(du);
			Object o=xqObjectJson.get("xq_storage");
			if(o!=null&&(Integer)(o)==0){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此需求已生成订单不能修改\"}").toString());
				return;
			}
			
		}
		
		/**
		 * 获取需求名称(前台提供)
		 */
		String xq_name = BmobAPI.getStringValueFromJSONObject(jo,"xq_name");
		/**
		 * 获取需求广告词(前台提供)
		 */
//		String xq_jingle = BmobAPI.getStringValueFromJSONObject(jo,"xq_jingle");
		/**
		 * 获取需求分类编号(前台提供)
		 */
		Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(jo,"fc_id");
		/**
		 * 获取需求分类名称
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fwxq_class", "fc_id", fc_id);
		String xqClassInfo = findOne("shopnc_fwxq_class", objectId);
		String fc_name = (String) BmobAPI.jsonStrToMap(xqClassInfo).get("fc_name");
		
		
		/**
		 * 需求添加时间
		 */
		long xq_addtime = System.currentTimeMillis() / 1000;
		/**
		 * 获取需求悬赏价格(前台提供)
		 * TODO 修改服务时 价格不能修改
		 */
//		String proi=BmobAPI.getStringValueFromJSONObject(jo,"xq_price");
//		if(proi!=null&&!"".equals(proi)){
//		
//			Double xq_price = Double.parseDouble(proi);
//		}
		/**
		 * 获取起始时间,结束时间(前台提供)
		 */
		String xq_starttimeString=BmobAPI.getStringValueFromJSONObject(jo, "xq_starttimeString");
		
		String xq_endtimeString=BmobAPI.getStringValueFromJSONObject(jo, "xq_endtimeString");
		
		String pattern="yyyy-MM-dd HH:mm";
		
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		Date d=null;
		
		Long xq_starttime;
		
		Long xq_endtime;
		try {
			d=sdf.parse(xq_starttimeString);
			xq_starttime=d.getTime()/1000;
			d=sdf.parse(xq_endtimeString);
			xq_endtime=d.getTime()/1000;
		} catch (ParseException e) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式应该为yyyy-MM-dd HH:mm，否则无效\"}").toString());
			return;
		}
		/**
		 * 获取工作方式(前台提供)
		 */
		Integer xq_work_style = BmobAPI.getIntegerValueFromJSONObject(jo,"xq_work_style");
		/**
		 * 获取一级地区id(前台提供)
		 */
		Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(jo,"areaid_1");
		/**
		 * 获取二级地区id(前台提供)
		 */
		Integer areaid_2 = BmobAPI.getIntegerValueFromJSONObject(jo,"areaid_2");

		String xq_area = mergeAddressToAreaInfo(areaid_1, areaid_2);
		/**
		 * 获取年龄(前台提供)
		 */
		Integer xq_age = BmobAPI.getIntegerValueFromJSONObject(jo,"xq_age");
		
		Integer expenditure_type=BmobAPI.getIntegerValueFromJSONObject(jo, "expenditure_type");
		
		/**
		 * 获取性别(前台提供)
		 */
		Integer xq_sex = BmobAPI.getIntegerValueFromJSONObject(jo,"xq_sex");
		/**
		 * 获取需求描述(前台提供)
		 */
		String xq_body = BmobAPI.getStringValueFromJSONObject(jo,"xq_body");
		/**
		 * 封装参数
		 */
		/**
		 * 姚永鹏修改
		 */

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_name", xq_name);
//		paramMap.put("xq_jingle", xq_jingle);
		paramMap.put("fc_id", fc_id);
		paramMap.put("fc_name", fc_name);
		paramMap.put("xq_state", 1);// 1上架
		paramMap.put("xq_addtime", xq_addtime);
//		paramMap.put("xq_price", xq_price);
		paramMap.put("xq_starttime", xq_starttime);
		paramMap.put("xq_endtime", xq_endtime);
		paramMap.put("xq_age", xq_age);
		paramMap.put("xq_work_style", xq_work_style);
		paramMap.put("xq_sex", xq_sex);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("xq_area", xq_area);
		paramMap.put("xq_body", xq_body);
		paramMap.put("xq_verify", 10);// 10审核中
		paramMap.put("xq_commend", 0);// 需求推荐 1是，0否，默认为0
		paramMap.put("xq_freight", 0);// 报销交通费 0为不报交通费
		paramMap.put("xq_vat", 0);// 是否开具增值税发票 1是，0否  
		paramMap.put("expenditure_type", expenditure_type);

		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取objectId
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_xq", objectId, jsonStr);
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
	 * 此方法已过时<br/>
	 * 修改需求的需求类型
	 */
	@Deprecated
	public void updateRequirementType() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求id
		 */
		Integer xq_id = BmobAPI.getIntegerValueFromJSONObject(params, "xq_id");
		/**
		 * 获取需求类别id
		 */
		Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(params, "fc_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fc_id", fc_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_xq", objectId, paramStr);
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
	 * FIXME (重要标记)保留了两个字段 <br/>
	 * 修改需求类型
	 */
	
	@Deprecated
	public void updateFwxqClass() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求类型id
		 */
		Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(params, "fc_id");
		/**
		 * 获取服务或需求类型名称(前台提供)
		 */
		String fc_name = params.getString("fc_name");
		// 以下两个字段保留
		// `type_id` int(10) unsigned NOT NULL COMMENT '服务或需求类型id',
		// `type_name` varchar(100) NOT NULL COMMENT '服务或需求类型名称',
		/**
		 * 获取父id(前台提供)
		 */
		Integer fc_parent_id = BmobAPI.getIntegerValueFromJSONObject(params, "fc_parent_id");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fc_name", fc_name);
		paramMap.put("fc_parent_id", fc_parent_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_fwxq_class", "fc_id", fc_id);
		/**
		 * 发送请求
		 */
		String result = update("shopnc_fwxq_class", objectId, paramStr);
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
	 * 查询会员发布的需求
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getRequirementByMemberId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取会员id(前台提供)
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
		String result = find("shopnc_xq", paramStr, skip, pageSize, "-createdAt");
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
	 * 会员需求高级查询(需要按照时间排序)
	 */
	public void searchRequirementByConditionByTime() {
		/**
		 * 获取参数(以下参数都需要前台提供)
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取起始时间
		 */
		Integer xq_starttime = BmobAPI.getIntegerValueFromJSONObject(params, "xq_starttime");
		/**
		 * 获取结束时间
		 */
		Integer xq_endtime = BmobAPI.getIntegerValueFromJSONObject(params, "xq_endtime");
		/**
		 * 获取工作方式
		 */
		Integer xq_work_style = BmobAPI.getIntegerValueFromJSONObject(params, "xq_work_style");
		/**
		 * 获取一级地区id
		 */
		Integer areaid_1 = BmobAPI.getIntegerValueFromJSONObject(params, "areaid_1");
		/**
		 * 获取二级地区id
		 */
		Integer areaid_2 = BmobAPI.getIntegerValueFromJSONObject(params, "areaid_2");
		/**
		 * 获取年龄
		 */
		Integer xq_age = BmobAPI.getIntegerValueFromJSONObject(params, "xq_age");
		/**
		 * 获取性别
		 */
		Integer xq_sex = BmobAPI.getIntegerValueFromJSONObject(params, "xq_sex");
		/**
		 * 获取是否排序
		 */
		Integer is_sequence = BmobAPI.getIntegerValueFromJSONObject(params, "is_sequence");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_starttime", xq_starttime);
		paramMap.put("xq_endtime", xq_endtime);
		paramMap.put("xq_work_style", xq_work_style);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("xq_age", xq_age);
		paramMap.put("xq_sex", xq_sex);
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 判断是否需要排序，将序还是升序
		 */
		String result = "";
		if ("1".equals(is_sequence)) {// 升序
			result = BmobAPI.findAllSortByColumn("shopnc_xq", "xq_addtime", false);
		} else if ("0".equals(is_sequence)) {// 不排序
			result = findAll("shopnc_xq", jsonStr);
		} else if ("2".equals(is_sequence)) {// 将序
			result = BmobAPI.findAllSortByColumn("shopnc_xq", "xq_addtime", true);
		} else {
			throw new RuntimeException("参数传递错误！");
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
	 * 根据服需求类别获取所有需求
	 */
	public void getAllRequirementByFwxqClass() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求类别编号(前台提供)
		 */
		Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(params, "fc_id");
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
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
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("fc_id", fc_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_xq", paramStr, skip, pageSize, "-createdAt");
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
	 * 根据需求状态查询(上架|下架)
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllRequirementByState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取会员编号(前台提供)
		 */
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		/**
		 * 获取需求状态(前台提供)
		 */
		Integer xq_state = BmobAPI.getIntegerValueFromJSONObject(params, "xq_state");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_state", xq_state);
		paramMap.put("member_id", member_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_xq", paramStr, skip, pageSize, "-createdAt");
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
	 * 按地区查询需求
	 */
	@Deprecated
	public void getAllRequirementByAreaId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
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
		String result = find("shopnc_xq", paramStr, skip, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
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
	 * 姚永鹏添加
	 * 修改需求上下架 0下架    1上架
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateRequirementState() {

		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求编号(前台提供)
		 */
		Integer xq_id = BmobAPI.getIntegerValueFromJSONObject(params, "xq_id");

		Integer xq_state = BmobAPI.getIntegerValueFromJSONObject(params, "xq_state");
		/**
		 * 获取需求公共表编号
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		String xqInfo = findOne("shopnc_xq", objectId);
		Map<String, Object> xqInfoMap = BmobAPI.jsonStrToMap(xqInfo);
		Integer xq_commonid = (Integer) xqInfoMap.get("xq_commonid");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_state", xq_state);// 需求状态改为下架
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向需求基础表发送请求
		 */
		String result = update("shopnc_xq", objectId, paramStr);
		/**
		 * 向需求公共表发送请求
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_xq_common", "xq_commonid", xq_commonid);
		result = update("shopnc_xq_common", objectId, paramStr);
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
	 * 姚永鹏添加
	 * 修改需求审核状态
	 */

	@RequiresRoles("0")
	public void updateRequirementVerifyState() {

		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取需求编号(前台提供)
		 */
		Integer xq_id = BmobAPI.getIntegerValueFromJSONObject(params, "xq_id");

		Integer xq_verify = BmobAPI.getIntegerValueFromJSONObject(params, "xq_verify");
		/**
		 * 获取需求公共表编号
		 */
		String objectId = BmobAPI.getObjectIdById("shopnc_xq", "xq_id", xq_id);
		String xqInfo = findOne("shopnc_xq", objectId);
		Map<String, Object> xqInfoMap = BmobAPI.jsonStrToMap(xqInfo);
		Integer xq_commonid = (Integer) xqInfoMap.get("xq_commonid");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_verify", xq_verify);// 修改需求状态
		
		
		if(xq_verify==0){
			
			String xq_verifyReason=BmobAPI.getStringValueFromJSONObject(params, "xq_verifyReason");
			
			paramMap.put("xq_verifyReason", xq_verifyReason);
		}
		
		/**
		 * 向需求基础表发送请求
		 */
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		String result = update("shopnc_xq", objectId, paramStr);
		/**
		 * 向需求公共表发送请求
		 */
		objectId = BmobAPI.getObjectIdById("shopnc_xq_common", "xq_commonid", xq_commonid);
		result = update("shopnc_xq_common", objectId, paramStr);
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
	 * 姚永鹏修改
	 * 按地区查询需求
	 */

	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllRequirementByAreaId1() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取地区编号(前台提供)
		 */
		Integer area_id = BmobAPI.getIntegerValueFromJSONObject(params, "area_id");
		Long nowDate=System.currentTimeMillis()/1000;
		
		String jsonStr="{\"xq_state\":1,\"xq_verify\":1,\"xq_storage\":{\"$ne\":0},\"xq_endtime\":{\"$gt\":"+nowDate+"}}";
		
		String result = find("shopnc_xq", jsonStr, skip, pageSize, "-createdAt");
		
		JSONArray wn=new JSONArray();
		
		if(result.indexOf("results\":[]")==-1){
			
			JSONObject jo=(JSONObject) JSONObject.parse(result);
			
			JSONArray ja=jo.getJSONArray("results");
			
			JSONObject jm;
			int i=0;
			for(i=0;i<ja.size();i++){
				
				jm=(JSONObject) ja.get(i);
				Integer member_id=jm.getInteger("member_id");
				
				String object=BmobAPI.getObjectIdById("loveyou_member","member_id",member_id);
				
				if(object!=null&&!"".equals(object)&&object.indexOf("Not Found:(")==-1){
					
					String memberInfo=BmobAPI.findOne("loveyou_member", object);
					
					JSONObject du=JSONObject.parseObject(memberInfo);
					
					String avator=du.getString("member_avator");
					
					String member_name=du.getString("member_name");
					
					jm.put("member_avator", avator);
					
					jm.put("member_name", member_name);
					
					wn.add(jm);
				}
			}
		}
		result="{\"results\":"+wn.toJSONString()+"}";
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
	 * 关键字检索服务
	 * 
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void searchRequirementByKeyword() {
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
		
		//TODO
		Long nowDate=System.currentTimeMillis()/1000;
		String bql = "select * from shopnc_xq where xq_state=1 and xq_verify=1 and xq_endtime>"+nowDate+" and xq_storage!=0 and xq_name like  '%" + keyword+"%' "+"or  xq_state=1 and xq_verify=1 and xq_endtime>"+nowDate+"  and xq_storage!=0 and fc_name like '%" + keyword+"%'";    //yyp 添加
				//+ "%' or store_id=(select store_id from loveyou_store where store_name like '%" + keyword + "%' or store_banner like '%"+keyword+"%')";
				// bql = "select * from shopnc_fw where store_id=(select
				// store_id from loveyou_store where store_name like
				// '%"+keyword+"%')";
				/**
				 * 发送请求
				 */
		
		//paramMap.put("xq_state", 1);
		//paramMap.put("xq_verify", 1);
		
		String result = findBQL(bql);

		/**
		 * 返回结果
		 */
		
		JSONArray wn=new JSONArray();
		
		if(result.indexOf("results\":[]")==-1){
			
			JSONObject jo=(JSONObject) JSONObject.parse(result);
			
			JSONArray ja=jo.getJSONArray("results");
			
			JSONObject jm;
			int i=0;
			for(i=0;i<ja.size();i++){
				
				jm=(JSONObject) ja.get(i);
				Integer member_id=jm.getInteger("member_id");
				
				String object=BmobAPI.getObjectIdById("loveyou_member","member_id",member_id);
				
				if(object!=null&&!"".equals(object)&&object.indexOf("Not Found:(")==-1){
					
					String memberInfo=BmobAPI.findOne("loveyou_member", object);
					
					JSONObject du=JSONObject.parseObject(memberInfo);
					
					String avator=du.getString("member_avator");
					
					String member_name=du.getString("member_name");
					
					jm.put("member_avator", avator);
					
					jm.put("member_name", member_name);
					
					wn.add(jm);
				}
			}
		}
		result="{\"results\":"+wn.toJSONString()+"}";
		System.out.println(result);
		if (result.indexOf("[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	@RequiresRoles("0")
	public void getAllRequirementByVerifyState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取地区编号(前台提供)
		 */
		Integer verify_state = BmobAPI.getIntegerValueFromJSONObject(params, "xq_verify");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("xq_verify", verify_state);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_xq", paramStr, skip, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("results\":[") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}

	/**
	 * 筛选功能 yyp 加会员名称查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getRequirementByCondition(){
		
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
			jsonWhere.put("xq_work_style", work_style);
		}
		if(priceMin!=null&&priceMin>=0&&priceMax!=null&&priceMax>priceMin){
			
			String priceStr="{\"$gte\":"+priceMin+",\"$lte\":"+priceMax+"}";
			JSONObject jw=JSONObject.parseObject(priceStr);
			jsonWhere.put("xq_price", jw);
		}
		
		Long nowDate=System.currentTimeMillis()/1000;
		String jsonStr1="{\"$gt\":"+nowDate+"}";
		JSONObject jw3=JSONObject.parseObject(jsonStr1);
		jsonWhere.put("xq_endtime", jw3);
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
				
				jsonStr="{\"$gt\":"+nowDate+",\"$gte\":"+origin+",\"$lte\":"+terminal+"}";
				JSONObject jw2=JSONObject.parseObject(jsonStr);

				jsonWhere.put("xq_starttime", jw);
				jsonWhere.put("xq_endtime", jw2);
			}
			
		}
		jsonWhere.put("xq_state", 1);
		jsonWhere.put("xq_verify", 1);
		String jsonStr="{\"$ne\":0}";
		JSONObject jw=JSONObject.parseObject(jsonStr);
		jsonWhere.put("xq_storage", jw);
		System.out.println(jsonWhere.toJSONString());
		
		String result="";
		if (skip>=0){
			result=find("shopnc_xq", jsonWhere.toJSONString(),skip,pageSize,"-createdAt");
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
		
		System.out.println("array"+resultArray);
		if(resultArray.toJSONString().indexOf("[")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,resultArray.toJSONString()).toString());
			return;
		}else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"操作失败\"}").toString());
		}
	}

	/**
	 * 根据用户id和审核状态查询 
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getRequirementByVerifyStateAndMemberId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取获取页码(前台提供，非必填)
		 */
		Integer pageNum = BmobAPI.getIntegerValueFromJSONObject(params, "page");
		if (pageNum == null && params.getString("page") == null) {
			pageNum = 1;
		}
		/**
		 * 获取每页显示数据条数(前台提供，非必填)
		 */
		Integer pageSize = BmobAPI.getIntegerValueFromJSONObject(params, "pageSize");
		if (pageSize == null && params.getString("pageSize") == null) {
			pageSize = BmobAPI.getPageSize();
		}
		/**
		 * 从第几条数据开始显示
		 */
		int skip = (pageNum - 1) * pageSize;
		/**
		 * 获取地区编号(前台提供)
		 */
		Integer verify_state = BmobAPI.getIntegerValueFromJSONObject(params, "xq_verify");
		/**
		 * 封装参数
		 */
		
		Integer member_id = BmobAPI.getIntegerValueFromJSONObject(params, "member_id");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("member_id", member_id);
		paramMap.put("xq_verify", verify_state);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("shopnc_xq", paramStr, skip, pageSize, "-createdAt");
		/**
		 * 返回结果
		 */
		if (result.indexOf("results\":[") == -1) {
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
