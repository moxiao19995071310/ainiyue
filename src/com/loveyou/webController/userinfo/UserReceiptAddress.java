package com.loveyou.webController.userinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;

public class UserReceiptAddress extends Bmob {

	/*
	 *  创建买家[悬赏方]收货地址
		修改买家[悬赏方]收货地址
		删除买家[悬赏方]收获地址
		创建卖家[服务方]可服务区域(只针对线下的服务)
		修改卖家[服务方]可服务区域
		删除卖家[服务方]可服务区域
		创建卖家发货地址[出发地址]
		修改卖家发货地址[出发地址]
		删除卖家发货地址[出发地址]
		Buyers 买家
		Seller 卖家 
	 */
	
	/**
	 * 创建买家收货地址
	 * 创建是需要查询收货地址最多只能有5条
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void createBuyerReceiptAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		/**
		 * 拼接area_info
		 */
		String area_info=mergeAddressToAreaInfo(jo.getInteger("area_id"),jo.getInteger("city_id"),jo.getInteger("county_id"));
		jo.put("area_info", area_info);
		jo.remove("county_id");
		/**
		 * 拼接详细地址address
		 */
		String address=overallAddress(area_info,jo.getString("addressDetailedly"));
		jo.put("address", address);
		jo.remove("addressDetailedly");
		/**
		 * 设置用户类型为买家
		 */
		jo.put("user_type", 0);
		
		jo.put("is_default", false);
		
		
		String result=insert("loveyou_address",jo.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}
	
	/**
	 * 修改买家收货地址
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateBuyerReceiptAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		/**
		 * 拼接area_info
		 */
		String area_info=mergeAddressToAreaInfo(jo.getInteger("area_id"),jo.getInteger("city_id"),jo.getInteger("county_id"));
		jo.put("area_info", area_info);
		jo.remove("county_id");
		/**
		 * 拼接详细地址address
		 */
		String address=overallAddress(area_info,jo.getString("addressDetailedly"));
		jo.put("address", address);
		jo.remove("addressDetailedly");
		/**
		 * 设置用户类型为买家
		 */
		jo.put("user_type", 0);
		
		jo.put("is_default", false);
		
		Integer addressId=jo.getInteger("address_id");
		/**
		 * 获取ObjectId
		 */
		String ObjectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", addressId);
		
		String result=update("loveyou_address", ObjectId, jo.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}
	
	/**
	 * 删除该买家收货地址
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void deleteBuyerReceiptAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer addressId=jo.getInteger("address_id");
		/**
		 * 获取ObjectId
		 */
		String ObjectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", addressId);

		String result = delete("loveyou_address", ObjectId);
		
		if (result.indexOf("ok") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 获取某用户的所有地址（member_id,角色ID）
	 * TODO 待完善，需要角色ID
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllAddressByMemberId(){
		
		/**
		 * 获取前台传递参数
		 */
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String result =Bmob.findAll("loveyou_address", jo.toJSONString());

		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 统计当前登录用户的收货地址个数（需要参数member_id,角色ID）
	 * TODO 待完善
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void countBuyerRAddress(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		int count=BmobAPI.count("loveyou_address", jo.toJSONString());
		
		String result="{\"count\":"+count+"}";
		
		if (count<0) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 设置用户默认收货地址(需要参数 address_id,角色ID)
	 * TODO 待完善
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void setDefaultAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer userType=jo.getInteger("user_type");
		
		Integer addressId=jo.getInteger("address_id");
		
		String ObjectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", addressId);
		
		/**
		 * 获取同一用户的所有收货地址,并取消该用户的默认收货地址
		 */
		String oneRow =findOne("loveyou_address", ObjectId);
		
		JSONObject jo2=JSONObject.parseObject(oneRow);
		
		Integer memberId=jo2.getInteger("member_id");
		
		/**
		 * 获取角色ID
		 */
		//Integer  
		
		ArrayList<String> list=BmobAPI.getAllObjectIdById("loveyou_address","{\"member_id\":"+memberId+",\"user_type\":"+userType+"}");
		
		for(String objectId:list){
			
			BmobAPI.update("loveyou_address", objectId, "{\"is_default\":false}");
		}
		
		/**
		 * 将用户传入的收货地址Id 改为默认收货地址
		 */
		String result=BmobAPI.update("loveyou_address", ObjectId, "{\"is_default\":true}");
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 创建卖家发货地址
	 * 创建是需要查询地址最多只能有5条
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void createSellerDeliveryAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		/**
		 * 拼接area_info
		 */
		String area_info=mergeAddressToAreaInfo(jo.getInteger("area_id"),jo.getInteger("city_id"),jo.getInteger("county_id"));
		jo.put("area_info", area_info);
		jo.remove("county_id");
		/**
		 * 拼接详细地址address
		 */
		String address=overallAddress(area_info,jo.getString("addressDetailedly"));
		jo.put("address", address);
		jo.remove("addressDetailedly");
		/**
		 * 设置用户类型为卖家
		 */
		jo.put("user_type", 1);
		
		jo.put("is_default", false);
		
		String result=insert("loveyou_address",jo.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}
	
	/**
	 * 修改卖家发货地址
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateSellerDeliveryAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer addressId=jo.getInteger("address_id");
		/**
		 * 拼接area_info
		 */
		String area_info=mergeAddressToAreaInfo(jo.getInteger("area_id"),jo.getInteger("city_id"),jo.getInteger("county_id"));
		jo.put("area_info", area_info);
		jo.remove("county_id");
		/**
		 * 拼接详细地址address
		 */
		String address=overallAddress(area_info,jo.getString("addressDetailedly"));
		jo.put("address", address);
		jo.remove("addressDetailedly");
		/**
		 * 获取ObjectId
		 */
		String ObjectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", addressId);
		
		String result=update("loveyou_address", ObjectId, jo.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}
	
	/**
	 * 删除该卖家发货地址
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void deleteSellerDeliveryAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer addressId=jo.getInteger("address_id");
		/**
		 * 获取ObjectId
		 */
		String ObjectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", addressId);

		String result = delete("loveyou_address", ObjectId);
		
		if (result.indexOf("ok") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 创建卖家服务地址范围
	 * 创建是需要查询发货地址最多只能有5条
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void createSellerServiceAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		/**
		 * 拼接area_info
		 */
		String area_info=mergeAddressToAreaInfo(jo.getInteger("area_id"),jo.getInteger("city_id"),jo.getInteger("county_id"));
		jo.put("area_info", area_info);
		jo.remove("county_id");
		/**
		 * 拼接详细地址address
		 */
		String address=overallAddress(area_info,jo.getString("addressDetailedly"));
		jo.put("address", address);
		jo.remove("addressDetailedly");
		/**
		 * 设置用户类型为卖家
		 */
		jo.put("user_type", 10);
		
		jo.put("is_default", false);
		
		String result=insert("loveyou_address",jo.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}
	
	/**
	 * 修改卖家服务地址范围
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateSellerServiceAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer addressId=jo.getInteger("address_id");
		
		/**
		 * 拼接area_info
		 */
		String area_info=mergeAddressToAreaInfo(jo.getInteger("area_id"),jo.getInteger("city_id"),jo.getInteger("county_id"));
		jo.put("area_info", area_info);
		jo.remove("county_id");
		/**
		 * 拼接详细地址address
		 */
		String address=overallAddress(area_info,jo.getString("addressDetailedly"));
		jo.put("address", address);
		jo.remove("addressDetailedly");
		/**
		 * 获取ObjectId
		 */
		String ObjectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", addressId);
		
		String result=update("loveyou_address", ObjectId, jo.toJSONString());
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
		
	}
	
	/**
	 * 删除该卖家服务地址范围
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void deleteSellerServiceAddress(){
		
		/**
		 * 获取前台传递参数
		 */
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer addressId=jo.getInteger("address_id");
		/**
		 * 获取ObjectId
		 */
		String ObjectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", addressId);

		String result = delete("loveyou_address", ObjectId);
		
		if (result.indexOf("ok") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 直接查询所有省级
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllProvincial(){
		
		String result=findAll("loveyou_area", "{\"area_deep\":1}");
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 通过省级ID，provincial_id查询此省级所有的下一级市
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllCity(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		int area_parent_id=jo.getInteger("provincial_id");
		
		String result=findAll("loveyou_area", "{\"area_deep\":2,\"area_parent_id\":"+area_parent_id+"}");
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**
	 * 通过城市ID，city_id查询此城市所有的下一级区县
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getAllCounty(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		int area_parent_id=jo.getInteger("city_id");
		
		String result=findAll("loveyou_area", "{\"area_deep\":3,\"area_parent_id\":"+area_parent_id+"}");
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result).toString());
		}
	}
	
	/**TODO
	 * 合并省级，市级，县级，及详细信息形成字符串address
	 * @return
	 */
	private  String overallAddress(String area_info,String addressDetailedly){
		
		return area_info+"  "+addressDetailedly;
	}
	
	/**
	 * 合并地址创建成area_info字段
	 * @param areaId
	 * 省级ID
	 * @param cityId
	 * 城市ID
	 * @return
	 * 	user_info 包括省，市，区/县
	 */
	
	private  String mergeAddressToAreaInfo(int areaId,int cityId,int countyId){
		
		String objectId=BmobAPI.getObjectIdById("loveyou_area", "area_id", areaId);
		String oneRow1=findOne("loveyou_area", objectId);
		JSONObject jo=JSONObject.parseObject(oneRow1);
		String address="";
		address=address+jo.getString("area_name")+"  ";
		
		String objectId1=BmobAPI.getObjectIdById("loveyou_area", "area_id", cityId);
		String oneRow2=findOne("loveyou_area", objectId1);
		 jo=JSONObject.parseObject(oneRow2);
		 address=address+jo.getString("area_name")+"  ";
		
		String objectId2=BmobAPI.getObjectIdById("loveyou_area", "area_id", countyId);
		String oneRow3=findOne("loveyou_area", objectId2);
		 jo=JSONObject.parseObject(oneRow3);
		 address=address+jo.getString("area_name");
		return address;
	}
	
	/**
	 * 根据address_id获取user_info
	 * @param address_id
	 * @return
	 */
	private String  getUserSimplifyAddress(int address_id){
		
		String objectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", address_id);
		String oneRow1=findOne("loveyou_address", objectId);
		JSONObject jo=JSONObject.parseObject(oneRow1);
		String address="";
		address=address+jo.getString("user_info");
		return address;
	}
	
	private  String getCounty(int address_id){
		
		String objectId=BmobAPI.getObjectIdById("loveyou_address", "address_id", address_id);
		String oneRow1=findOne("loveyou_address", objectId);
		JSONObject jo=JSONObject.parseObject(oneRow1);
		String areaInfo=jo.getString("area_info");
		int a=areaInfo.lastIndexOf(" ");
		return areaInfo.substring(a+1, areaInfo.length());
	}
	
	/**
	 * 从桌面读取json文件，写入到云端数据库
	 */
	public void writeData(){

		String str=readFileByChars("C:\\Users\\Administrator\\Desktop\\shopnc_area.json");
		
		JSONObject jo=(JSONObject) JSONObject.parse(str);
		JSONArray ja=jo.getJSONArray("RECORDS");
		//System.out.println(ja);
		
		for(int i=0;i<ja.size();i++){
			
			insert("loveyou_area", ja.getJSONObject(i).toJSONString());
		}
	}
	
	/**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static String readFileByChars(String fileName) {
        File file = new File(fileName);
        String str="";
        Reader reader = null;
        try {
            //System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
 
            while ((tempchar = reader.read()) != -1) {
            	
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
            	char m=(char) tempchar;
                if (m != '\r') {
//                	if(j>10){
//                		if(m=='{')
//                		b=1;
//                		if(m=='}')
//                		b=2;
//                	}
//                	if(b==1){
//                		row=row+new Character(m);
//                	}
//                	if(b==2) {
//                		row=row+"}";
//                		al.add(row);
//                		b=0;
//                	}
                	str=str+new Character(m).toString();
                	
                }
              
            }
           // System.out.println(str);
           // System.out.println(al);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        
        return str;
        
    }
    /**
     *  根据地址ID获取地址
     */
    @RequiresRoles(value={"1","0"},logical=Logical.OR)
    public void getAddress(){
    	
    	JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
    	
    	Integer address =jo.getInteger("address_id");
    	
    	if(address!=null&&address>0){
    		
    		String objectId=BmobAPI.getObjectIdById("loveyou_address","address_id",address);
    		if(objectId!=null){
    			String result=BmobAPI.findOne("loveyou_address", objectId);
    		
    			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
    			return;
    		}
    		
    	}
    	
    	renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该收货地址不存在\"}"));
    	return; 
    }
}
