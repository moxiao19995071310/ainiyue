package com.loveyou.webController.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;

/**
 * 商品管理控制器
 * 
 * @ClassName: ProductController
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月7日 下午8:05:49
 *
 * 
 */
public class ProductController extends Bmob {

	/**
	 * 添加类别
	 */
	public void addProductType() {
		/**
		 * 上传图片
		 */
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
		String fileName = "";
		String url = "";
		if (!maps.isEmpty()) {
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
		 * 获取商品名称
		 */
		String gc_name = getPara("gc_name");
		/**
		 * 获取类型编号
		 */
		Integer type_id = getParaToInt("type_id");
		/**
		 * 获取类型名称
		 */
		String type_name = getPara("type_name");
		/**
		 * 获取父类别编号
		 */
		Integer gc_parent_id = getParaToInt("gc_parent_id");
		/**
		 * 是否排序
		 */
		Integer gc_sort = getParaToInt("gc_sort");
		/**
		 * 获取名称
		 */
		String gc_title = getPara("gc_title");
		/**
		 * 获取关键词
		 */
		String gc_keywords = getPara("gc_keywords");
		/**
		 * 获取描述
		 */
		String gc_description = getPara("gc_description");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("gc_name", gc_name);
		paramMap.put("type_id", type_id);
		paramMap.put("type_name", type_name);
		paramMap.put("gc_parent_id", gc_parent_id);
		paramMap.put("gc_sort", gc_sort);
		paramMap.put("gc_title", gc_title);
		paramMap.put("gc_keywords", gc_keywords);
		paramMap.put("gc_description", gc_description);
		if (!"".equals(url)) {
			paramMap.put("fw_image", url);
		}
		if (!"".equals(fileName)) {
			paramMap.put("image_relativelypath", fileName);
		}
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 插入数据
		 */
		String result = insert("loveyou_goods_class", paramStr);
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
	 * 一级商品类别查询
	 */
	public void getFirstClassGoodsType() {
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
		 * 封装条件参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("gc_parent_id", 0);
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_goods_class", whereJsonStr, skip, pageSize);
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
	 * 二级商品类别查询
	 */
	public void getSecondClassGoodsType() {
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
		 * 获取一级商品类别id(前台提供)
		 */
		Integer gc_parent_id = BmobAPI.getIntegerValueFromJSONObject(params, "gc_id");
		/**
		 * 封装条件参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("gc_parent_id", gc_parent_id);
		String whereJsonStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 发送请求
		 */
		System.out.println(whereJsonStr);
		String result = find("loveyou_goods_class", whereJsonStr, skip, pageSize);
		System.out.println(result);
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
	 * 获取商品列表
	 */
	public void getAllGoodsList() {
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
		 * 发送请求
		 */
		String result = find("loveyou_goods", "",skip, pageSize,"-createdAt");

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
	 * 商品详细信息查询
	 */
	public void getGoodsById() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取商品id(前台提供)
		 */
//		Integer goods_id = params.("goods_id");
		
		Integer goods_id=BmobAPI.getIntegerValueFromJSONObject(params, "goods_id");
		/**
		 * 获取objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
		/**
		 * 查询商品信息
		 */
		String resultw = findOne("loveyou_goods", objectId);
		
		/**
		 * 点击次数加1
		 */
		JSONObject jom = JSONObject.parseObject(resultw);
		String objectid = jom.getString("objectId");
		String jsonStr = "{\"goods_click\":{\"__op\":\"Increment\",\"amount\":1}}";
		update("loveyou_goods", objectid, jsonStr);

		
		/**
		 * 返回结果
		 */
		if (resultw.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, resultw).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, resultw).toString());
		}
	}

	/**
	 * 根据类别查询商品
	 */
	public void getGoodsListByType() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取商品类别(前台提供)
		 */
		Integer gc_id = params.getInteger("gc_id");
		/**
		 * 封装条件参数
		 */
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("gc_id", gc_id);
		paramsMap.put("goods_state", 1);
		paramsMap.put("goods_verify", 1);
		String paramStr = BmobAPI.mapToJSONStr(paramsMap);
		/**
		 * 查询商品信息
		 */
		String result = findAll("loveyou_goods", paramStr);
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
	 * 获取某个店铺的所有商品（要分页）
	 */
	public void getGoodsListByStoreId() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取店铺编号(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
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
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("store_id", store_id);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_goods", paramStr, skip, pageSize,"-createdAt");
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
	 * 获取某个店铺的所有上架或者下架的商品（要分页）
	 */
	public void getGoodsListByStoreIdAndGoodsState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取商品状态(前台提供)
		 */
		Integer goods_state = BmobAPI.getIntegerValueFromJSONObject(params, "goods_state");
		/**
		 * 获取店铺编号(前台提供)
		 */
		Integer store_id = BmobAPI.getIntegerValueFromJSONObject(params, "store_id");
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
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("store_id", store_id);
		paramMap.put("goods_state", goods_state);
		paramMap.put("goods_verify", 1);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_goods", paramStr, skip, pageSize,"-createdAt");
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
	 * 根据商品状态查询商品
	 */
	public void getAllGoodsByGoodsState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取商品状态(前台提供)
		 */
		Integer goods_state = BmobAPI.getIntegerValueFromJSONObject(params, "goods_state");
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
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_state", goods_state);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_goods", paramStr, skip, pageSize);
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
	 * 添加商品
	 */
	public void addGoods() {
//		/**
//		 * 上传图片
//		 */
//		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
//		String fileName = "";
//		String url = "";
//		String imageDetailsPath = "";
//		String imageDetailsUrl = "";
//		if (!maps.isEmpty()) {
//			/**
//			 * 获取上传文件的文件名
//			 */
//			fileName = (String) maps.get(0).get("relativelypath");
//			fileName = fileName.replace("\\", "/");
//			/**
//			 * 获取上传文件的url
//			 */
//			url = (String) maps.get(0).get("url");
//			url = url.replace("\\", "/");
//			for (int i = 1; i < maps.size(); i++) {
//				String path = "," + maps.get(i).get("relativelypath");
//				path = path.replace("\\", "/");
//				imageDetailsPath += path;
//				String url2 = "," + maps.get(i).get("url");
//				url2 = url2.replace("\\", "/");
//				imageDetailsUrl += url2;
//			}
//		}
		
		/**
		 * 上传图片
		 */
		
		List<Map<String,Object>> maps=super.uploadMoreBmobListMap2();
		
		String goods_image = null, imageDetailsPath= null;
		String goods_imageRealPath=null,imageDetailsRealPath=null;
		int i=0;
		for (i=0;i<maps.size();i++){
			String paramtename=(String) maps.get(i).get("parametername");
			if(paramtename.equals("goods_image")){
				goods_image=(String) maps.get(i).get("url");
				goods_imageRealPath=(String) maps.get(i).get("relativelypath");
				goods_imageRealPath=goods_imageRealPath.replace("\\", "/");
			}else if(paramtename.equals("imageDetailsPath")){
				imageDetailsPath=(String) maps.get(i).get("url");
				imageDetailsRealPath=(String) maps.get(i).get("relativelypath");
				imageDetailsRealPath=imageDetailsRealPath.replace("\\", "/");
			}
		}
		/**
		 * 获取商品名称(前台提供)
		 */
		String goods_name = getPara("goods_name");
		/**
		 * 获取商品广告词(前台提供)
		 */
		String goods_jingle = getPara("goods_jingle");
		/**
		 * 获取店铺编号(前台提供)
		 */
		Integer store_id = getParaToInt("store_id");
		/**
		 * 获取店铺名称
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		String storeInfo = findOne("loveyou_store", objectId);
		Map<String, Object> storeInfoMap = BmobAPI.jsonStrToMap(storeInfo);
		String store_name = (String) storeInfoMap.get("store_name");
		/**
		 * 获取规格名称(前台获取)
		 */
		String spec_name = getPara("spec_name");
		/**
		 * 获取规格值(前台获取)
		 */
		String spec_value = getPara("spec_value");
		/**
		 * 获取商品分类编号(前台提供)
		 */
		Integer gc_id = getParaToInt("gc_id");
		/**
		 * 获取商品分类名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_goods_class", "gc_id", gc_id);
		String goodsClassInfo = findOne("loveyou_goods_class", objectId);
		Map<String, Object> goodsClassInfoMap = BmobAPI.jsonStrToMap(goodsClassInfo);
		String gc_name = (String) goodsClassInfoMap.get("gc_name");
//		/**
//		 * 获取品牌编号(前台提供)
//		 */
//		Integer brand_id = getParaToInt("brand_id");
//		/**
//		 * 获取品牌名称
//		 */
//		objectId = BmobAPI.getObjectIdById("loveyou_brand", "brand_id", brand_id);
//		String brandInfo = findOne("loveyou_brand", objectId);
//		Map<String, Object> brandInfoMap = BmobAPI.jsonStrToMap(brandInfo);
//		String brand_name = (String) brandInfoMap.get("brand_name");
		/**
		 * 获取商品属性(前台提供)
		 */
		String goods_attr = getPara("goods_attr");
		/**
		 * 获取商品内容(前台提供)
		 */
		String goods_body = getPara("goods_body");
		/**
		 * 获取类型编号(前台提供)
		 */
		Integer type_id = getParaToInt("type_id");
		/**
		 * 获取商品价格(前台提供)
		 */
		Double goods_price = Double.parseDouble(getPara("goods_price"));
		/**
		 * 获取市场价(前台提供)
		 */
		Double goods_marketprice = Double.parseDouble(getPara("goods_marketprice"));
		/**
		 * 获取商品成本价(前台提供)
		 */
		Double goods_costprice = Double.parseDouble(getPara("goods_costprice"));
		/**
		 * 获取商品折扣(前台提供)
		 */
		System.out.println(getPara("goods_discount"));
		Double goods_discount = Double.parseDouble(getPara("goods_discount"));
		/**
		 * 获取运费模板编号(前台提供)
		 */
		Integer transport_id = getParaToInt("transport_id");
		/**
		 * 获取运费模板名称
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_transport", "id", transport_id);
		String transportInfo = findOne("loveyou_transport", objectId);
		Map<String, Object> transportInfoMap = BmobAPI.jsonStrToMap(transportInfo);
		String transport_title = (String) transportInfoMap.get("title");
		/**
		 * 是否推荐该商品(前台提供，1是，0否)
		 */
		Integer goods_commend = getParaToInt("goods_commend");
		/**
		 * 运费(前台提供)
		 */
		Double goods_freight = Double.parseDouble(getPara("goods_freight"));
		/**
		 * 是否开具增值税发票 1是，0否(前台提供)
		 */
		Integer goods_vat = getParaToInt("goods_vat");
		/**
		 * 店铺分类编号
		 */
		Integer goods_stcids = (Integer) storeInfoMap.get("sc_id");
		/**
		 * 获取商家编号(前台提供)
		 */
		String goods_serial = getPara("goods_serial");
		/**
		 * 获取商品规格序列化(前台提供)
		 */
		String goods_spec = getPara("goods_spec");
		/**
		 * 商品编辑时间
		 */
		long goods_addtime = System.currentTimeMillis() / 1000;
		/**
		 * 获取一级地区id(前台提供)
		 */
		Integer areaid_1 = getParaToInt("areaid_1");
		/**
		 * 获取二级地区id(前台提供)
		 */
		Integer areaid_2 = getParaToInt("areaid_2");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_name", goods_name);
		paramMap.put("goods_jingle", goods_jingle);
		paramMap.put("gc_id", gc_id);
		paramMap.put("gc_name", gc_name);
		paramMap.put("store_id", store_id);
		paramMap.put("store_name", store_name);
		paramMap.put("spec_name", spec_name);
		paramMap.put("spec_value", spec_value);
//		paramMap.put("brand_id", brand_id);
//		paramMap.put("brand_name", brand_name);
		paramMap.put("type_id", type_id);
		paramMap.put("goods_image", goods_image);
		paramMap.put("image_relativelypath", goods_imageRealPath);
		paramMap.put("goods_attr", goods_attr);
		paramMap.put("goods_body", goods_body);
		paramMap.put("goods_state", 1);// 商品状态,默认正常
		paramMap.put("goods_stateremark", "");// 违规原因,默认为空
		paramMap.put("goods_verify", 10);// 审核状态,默认审核中
		paramMap.put("goods_verifyremark", "");// 商品审核失败原因,默认为空
		paramMap.put("goods_lock", 0);// 商品锁定,默认未锁
		paramMap.put("goods_addtime", goods_addtime);
		paramMap.put("goods_price", goods_price);
		paramMap.put("goods_marketprice", goods_marketprice);
		paramMap.put("goods_costprice", goods_costprice);
		paramMap.put("goods_discount", goods_discount);
		paramMap.put("goods_serial", goods_serial);
		paramMap.put("transport_id", transport_id);
		paramMap.put("transport_title", transport_title);
		paramMap.put("goods_commend", goods_commend);
		paramMap.put("goods_freight", goods_freight);
		paramMap.put("goods_vat", goods_vat);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		// paramMap.put("plateid_top", 0);// 顶部关联板式,默认为0
		// paramMap.put("plateid_bottom", 0);// 底部关联板式,默认为0
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		System.out.println("paramStr:" + paramStr);
		String result = insert("loveyou_goods_common", paramStr);
		System.out.println("result:" + result);
		/**
		 * 返回结果
		 */
		if (result.indexOf("objectId") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}
		/**
		 * 获取商品公共表id
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		System.out.println(resultMap);
		objectId = (String) resultMap.get("objectId");
		System.out.println(objectId);
		String goodsCommonInfo = findOne("loveyou_goods_common", objectId);
		Map<String, Object> goodsCommonInfoMap = BmobAPI.jsonStrToMap(goodsCommonInfo);
		System.out.println(goodsCommonInfoMap);
		Integer goods_commonid = (Integer) goodsCommonInfoMap.get("goods_commonid");
		System.out.println(goods_commonid);
		/**
		 * 获取商品库存(前台提供)
		 */
		Integer goods_storage = getParaToInt("goods_storage");
		/**
		 * 封装参数
		 */
		paramMap.clear();
		paramMap.put("goods_commonid", goods_commonid);
		paramMap.put("goods_name", goods_name);
		paramMap.put("goods_jingle", goods_jingle);
		paramMap.put("store_id", store_id);
		paramMap.put("store_name", store_name);
		paramMap.put("gc_id", gc_id);
//		paramMap.put("brand_id", brand_id);
		paramMap.put("goods_price", goods_price);
		paramMap.put("goods_marketprice", goods_marketprice);
//姚永鹏添加
		paramMap.put("goods_body", goods_body);

		paramMap.put("goods_serial", goods_serial);
		paramMap.put("goods_click", 0);// 商品点击数量，默认为0
		paramMap.put("goods_salenum", 0);// 商品销售数量，默认为0
		paramMap.put("goods_collect", 0);// 商品收藏数量，默认为0
		paramMap.put("goods_spec", goods_spec);
		paramMap.put("goods_storage", goods_storage);
		paramMap.put("goods_image", goods_image);
		paramMap.put("image_relativelypath", goods_imageRealPath);
		paramMap.put("goods_state", 1);// 商品状态，默认正常
		paramMap.put("goods_verify", 10);// 商品审核状态，默认审核中
		paramMap.put("goods_addtime", goods_addtime);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("transport_id", transport_id);
		paramMap.put("goods_freight", goods_freight);
		paramMap.put("goods_vat", goods_vat);
		paramMap.put("goods_commend", goods_commend);
		paramMap.put("goods_stcids", goods_stcids);
		paramMap.put("image_details_path", imageDetailsRealPath);
		paramMap.put("image_details_url", imageDetailsPath);
		paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		result = insert("loveyou_goods", paramStr);
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
	 * 编辑商品
	 */
	public void editGoods() {
//		/**
//		 * 上传图片
//		 */
//		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
//		/**
//		 * 判断是否上传文件
//		 */
//		String fileName = "";
//		String url = "";
//		if (!maps.isEmpty()) {
//			/**
//			 * 获取上传文件的文件名
//			 */
//			fileName = BmobAPI.getSingleUploadFileName(maps);
//			/**
//			 * 获取上传文件的url
//			 */
//			url = BmobAPI.getSingleUploadFileUrl(maps);
//		}
		
		/**
		 * 上传图片
		 */
		
		List<Map<String,Object>> maps=super.uploadMoreBmobListMap2();
		
		String goods_image = null;
		String goods_imageRealPath=null;
		int i=0;
		if (null!=maps&&!maps.equals("")) {	
			
			for (i=0;i<maps.size();i++){
				String paramtename=(String) maps.get(i).get("parametername");
				if(paramtename.equals("goods_image")){
					goods_image=(String) maps.get(i).get("url");
					goods_imageRealPath=(String) maps.get(i).get("relativelypath");
					goods_imageRealPath=goods_imageRealPath.replace("\\", "/");
				}
			}
		}

		
		/**
		 * 获取商品编号(前台提供)
		 */
		Integer goods_id = getParaToInt("goods_id");
		/**
		 * 获取商品公共表id
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
		String goodsInfo = findOne("loveyou_goods", objectId);
		Map<String, Object> goodsInfoMap = BmobAPI.jsonStrToMap(goodsInfo);
		Integer goods_commonid = (Integer) goodsInfoMap.get("goods_commonid");
		/**
		 * 获取商品名称(前台提供)
		 */
		String goods_name = getPara("goods_name");
		/**
		 * 获取商品广告词(前台提供)
		 */
		String goods_jingle = getPara("goods_jingle");
		/**
		 * 获取商品分类编号(前台提供)
		 */
		Integer gc_id = getParaToInt("gc_id");
		/**
		 * 获取品牌编号(前台提供)
		 */
		Integer brand_id = getParaToInt("brand_id");
		/**
		 * 获取商品价格(前台提供)
		 */
		Double goods_price = Double.parseDouble(getPara("goods_price"));
		/**
		 * 获取市场价(前台提供)
		 */
		Double goods_marketprice = Double.parseDouble(getPara("goods_marketprice"));
		/**
		 * 获取商家编号(前台提供)
		 */
		String goods_serial = getPara("goods_serial");
		/**
		 * 获取商品规格序列化(前台提供)
		 */
		String goods_spec = getPara("goods_spec");
		/**
		 * 商品编辑时间
		 */
		long goods_edittime = System.currentTimeMillis() / 1000;
		/**
		 * 获取一级地区id(前台提供)
		 */
		Integer areaid_1 = getParaToInt("areaid_1");
		/**
		 * 获取二级地区id(前台提供)
		 */
		Integer areaid_2 = getParaToInt("areaid_2");
		/**
		 * 运费(前台提供)
		 */
		Double goods_freight = Double.parseDouble(getPara("goods_freight"));

		String goods_body=getPara("goods_body");
		
		
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_commonid", goods_commonid);
		paramMap.put("goods_name", goods_name);
		paramMap.put("goods_jingle", goods_jingle);
		paramMap.put("gc_id", gc_id);
		paramMap.put("brand_id", brand_id);
		paramMap.put("goods_price", goods_price);
		paramMap.put("goods_marketprice", goods_marketprice);
		paramMap.put("goods_serial", goods_serial);
		paramMap.put("goods_spec", goods_spec);
//姚永鹏添加
		if(goods_body!=null)
		paramMap.put("goods_body", goods_body);

		paramMap.put("goods_edittime", goods_edittime);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("goods_freight", goods_freight);
		//if (!maps.isEmpty()) {
		if (null!=maps&&!maps.equals("")) {	
			paramMap.put("goods_image", goods_image);
			paramMap.put("image_relativelypath", goods_imageRealPath);
		}
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取商品表的objectId
		 */
		objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
		/**
		 * 发送请求
		 */
		String result = update("loveyou_goods", objectId, paramStr);
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
	 * 根据审核状态查询商品(分页显示)
	 */
	public void searchGoodsByVerifyState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取审核状态(前台提供)
		 */
		Integer goods_verify = BmobAPI.getIntegerValueFromJSONObject(params, "goods_verify");
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
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_verify", goods_verify);
		paramMap.put("goods_state", 1);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		String result = find("loveyou_goods", paramStr, skip, pageSize,"-updatedAt,-createdAt");
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
	 * 修改商品状态
	 */
	public void updateGoodsState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取商品编号
		 */
		Integer goods_id = BmobAPI.getIntegerValueFromJSONObject(params, "goods_id");
		/**
		 * 获取商品状态
		 */
		Integer goods_state = BmobAPI.getIntegerValueFromJSONObject(params, "goods_state");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_state", goods_state);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取商品表的objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
		/**
		 * 发送请求
		 */
		String result = update("loveyou_goods", objectId, paramStr);
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
	 * 商品审核状态修改
	 */
	public void updateGoodsVerifyState() {
		/**
		 * 获取参数
		 */
		JSONObject params = BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 获取商品编号(前台提供)
		 */
		Integer goods_id = BmobAPI.getIntegerValueFromJSONObject(params, "goods_id");
		/**
		 * 获取商品审核状态(前台提供)
		 */
		Integer goods_verify = BmobAPI.getIntegerValueFromJSONObject(params, "goods_verify");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goods_verify", goods_verify);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 获取商品表的objectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);
		/**
		 * 发送请求
		 */
		String result = update("loveyou_goods", objectId, paramStr);
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
	 * 根据商品二级类别查询商品
	 */
	public void getGoodsBySecondClass(){
		
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
		 * 获取地区编号(前台提供)
		 */
		Integer secondClass = BmobAPI.getIntegerValueFromJSONObject(params, "secondClass");
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("gc_id", secondClass);
		paramMap.put("goods_verify", 1);
		paramMap.put("goods_state", 1);
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 发送请求
		 */
		System.out.println("paramStr");
		String result = find("loveyou_goods", paramStr, skip, pageSize, "-addtime");
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
	 * 删除商品
	 */
	public void deleteGoods(){
		
		/**
		 * 获取前台传递参数
		 */
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer goods_id=BmobAPI.getIntegerValueFromJSONObject(jo, "goods_id");
		/**
		 * 获取ObjectId
		 */
		String ObjectId=BmobAPI.getObjectIdById("loveyou_goods", "goods_id", goods_id);

		String result = delete("loveyou_goods", ObjectId);
		
		if (result.indexOf("ok") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}

	}
}
