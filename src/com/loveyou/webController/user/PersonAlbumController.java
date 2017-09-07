package com.loveyou.webController.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;

public class PersonAlbumController extends Bmob {

	/**
	 *上传图片，最多上传30张图片，用户可以删除，可以设置默认
	 */
	/**
	 * 查看用户相册
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void lookMePhotoAlbum(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		if(member_id!=null&&member_id>0){
			
			String objectId=BmobAPI.getObjectIdById("loveyou_photo","member_id", member_id);
			
			if(objectId==null){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"相册为空\"}").toString());
				return;
			}
			
			String result=findOne("loveyou_photo", objectId);
			
			JSONObject du=JSONObject.parseObject(result);
			
//			for(int i=1;i<31;i++){
//				System.out.println(i);
//				if(!du.containsKey("p"+i))
//					break;
//				
//				System.out.println(du.get("p"+i));
//				
//				if(du.get("p"+i)!=null&&du.get("p"+i).equals(null)){
//					du.remove("p"+i);
//				}
//			}
			
			if(du.toJSONString().indexOf("objectId")==-1){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\",\"查询错误\"}").toString());
				return;
			}else{
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,du.toJSONString()).toString());
				return;
			}
			
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\",\"查询错误\"}").toString());
		
	}
	
	
	/**
	 * 用户上传图片 单个上传或者批量上传
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void uploadPhoto(){
		
		List<Map<String,Object>> maps=uploadMoreBmobListMap2();
		
		if(maps==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"上传图片为空\"}").toString());
			return;
		}
		
		Integer member_id;
		System.out.println("dd"+getPara("member_id"));
		try{
				member_id=Integer.parseInt(getPara("member_id"));
		}catch(NumberFormatException e){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"member_id非数字").toString());
			return;
		}
		String objectId=BmobAPI.getObjectIdById("loveyou_photo", "member_id", member_id);
		
		if(objectId==null){
			
			String result=insert("loveyou_photo", "{\"member_id\":"+member_id+"}");
			
			if(result.indexOf("objectId")!=-1){
				
				JSONObject resultJo=JSONObject.parseObject(result);
				objectId=resultJo.getString("objectId");
			}else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"网络超时1\"}").toString());
				return;
			}
		}
		
		String du=findOne("loveyou_photo", objectId);
		
		if(du.indexOf("objectId")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"网络超时\"}").toString());
			return;
		}
		
		JSONObject duPhotoObject=JSONObject.parseObject(du);
		
		System.out.println(duPhotoObject);
		String key="p";
		Integer sequence=1;
		/**
		 * objectId,createAt,updatedAt,member_id,is_default
		 */
		int dufalutValue=3;
		String is_default=null;
		if(!duPhotoObject.containsKey("is_default")){
			dufalutValue-=1;
			
			is_default="p1";
		}
		List<String> list=new ArrayList<String>();
		
		for(int m=1;m<duPhotoObject.size()-dufalutValue;m++){
			
			sequence=m;
			String keyTo=key+sequence.toString();
			
			if(duPhotoObject.containsKey(keyTo)){
				
				if(duPhotoObject.getString(keyTo)==null||duPhotoObject.getString(keyTo).equals("")){
					list.add(keyTo);
				}
				System.out.println("this is list:"+keyTo);
				continue;
			}else{
				break;
				
			}
		}
		
		System.out.println("sequence:"+sequence);
		JSONObject photoObject=new JSONObject();
		System.out.println(maps);
		if(maps!=null&&maps.size()>0){
			
			for(int i=0;i<maps.size();i++){
				
				if(list.size()>i){
					photoObject.put(list.get(i), maps.get(i).get("url"));
				}else{
				
					photoObject.put(key+sequence.toString(), maps.get(i).get("url"));
					sequence++;
				}
			}
		}
		if(is_default!=null)
			photoObject.put("is_default", is_default);
		
		photoObject.put("verify_state", 0);
		
		System.out.println(photoObject);
		String result=update("loveyou_photo", objectId, photoObject.toJSONString());
		
		if(result.indexOf("updatedAt")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"上传图片错误\"}").toString());
			return;
		}else{
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
		}
		
	}
	
	/**
	 * 设置默认,最多设置三个默认封面
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void setIsDefalut(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		/**
		 * 样式 为 p1,p2,p3,p4
		 */
		String is_default=BmobAPI.getStringValueFromJSONObject(jo, "is_default");
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		if(member_id!=null){
			
			String objectId=BmobAPI.getObjectIdById("loveyou_photo", "member_id", member_id);
			
			if(objectId==null){
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该用户id不存在，\"}").toString());
				return;
			}
			
			if(is_default!=null&&is_default.length()>1){
				
				if((is_default.indexOf(",")==-1&&is_default.matches("^p[0-3]{0,1}\\d{1}$"))||
						(is_default.indexOf(",")>1&&is_default.lastIndexOf(",")<4&&is_default.matches("^p[0-3]{0,1}\\d{1},p[0-3]{0,1}\\d{1}$"))||
							(is_default.lastIndexOf(",")>4&&is_default.lastIndexOf(",")<8&&is_default.matches("^p[0-3]{0,1}\\d{1},p[0-3]{0,1}\\d{1},p[0-3]{0,1}\\d{1}$"))){
					
					String result=update("loveyou_photo", objectId, "{\"is_default\":\""+is_default+"\"}");
					
					if(result.indexOf("updatedAt")==-1){
						
						renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"修改失败\"}").toString());
						return;
					}else{
						renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
						return;
					}
				}
			}
			
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户信息有误不能修改\"}").toString());
	}
	
	/**
	 * 获取自己设置为默认的图片列表
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getDefaultAlbum(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String objectId=BmobAPI.getObjectIdById("loveyou_photo", "member_id", member_id);
		
		if(objectId!=null){
			
			String result=BmobAPI.findOne("loveyou_photo", objectId);
			
			JSONObject du=JSONObject.parseObject(result);
			
			String is_default=du.getString("is_default");
			
			//解析字符串
			//TODO
			
			String isOne=null;
			String isTwo=null;
			String isThree=null;
			int i=1;
			int q=0;
			do{
				q=is_default.indexOf(",");
				if(q>1){
					if(i==1)
						isOne=is_default.substring(0,q);
					if(i==2)
						isTwo=is_default.substring(0,q);
					
					is_default=is_default.substring(q+1);
				}
				
	 			i++;	
			}while(q!=-1);
			isThree=is_default.equals("")?null:is_default;
			
			JSONObject re;
			if(isThree!=null){
				re=new JSONObject();
				re.put(isThree, du.getString(isThree));
				
				if(isOne!=null){
					re.put(isOne, du.getString(isOne));
				}
				if(isTwo!=null){
					re.put(isTwo, du.getString(isTwo));
				}
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,re.toJSONString()).toString());
				return;
			}
				
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"还没有设置默认的封面\"}").toString());
		
	}
	
	
	/**
	 * 批量删除图片
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void deletePhoto(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String delete_value=BmobAPI.getStringValueFromJSONObject(jo, "delete_value");
		
		String objectId=BmobAPI.getObjectIdById("loveyou_photo", "member_id", member_id);
		
		if(objectId==null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该用户不存在\"}").toString());
			return;
		}
		
		String result=findOne("loveyou_photo", objectId);
		
		JSONObject du=JSONObject.parseObject(result);
		
		JSONObject write=new JSONObject();
		//解析字符串
		//TODO
		
		String isOne=null;
		int q=0;
		do{
			q=delete_value.indexOf(",");
			if(q>1){
					isOne=delete_value.substring(0,q);
					
					if(du.containsKey(isOne))
						write.put(isOne, "");
				delete_value=delete_value.substring(q+1);
			}
			
		}while(q!=-1);
		if(du.containsKey(delete_value))
		write.put(delete_value,"");
		String content=write.toJSONString().replaceAll("\"\"", "null");
		result=update("loveyou_photo", objectId, content);
		if(result.indexOf("updatedAt")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"修改失败\"}").toString());
			return;
		}else{
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
		}
	}
	
	/**
	 * 获取相册中设默认的图片
	 *
	 */
	
	public JSONObject getDefaultAlbum(Integer member_id){
		
		String objectId=BmobAPI.getObjectIdById("loveyou_photo", "member_id", member_id);
		
		JSONObject memberInviter=new JSONObject();
		
		if(objectId!=null){
			
			String result=BmobAPI.findOne("loveyou_photo", objectId);
			
			JSONObject du=JSONObject.parseObject(result);
			
			Integer verify_state=du.getInteger("verify_state");
			
			if(verify_state==null||!verify_state.equals("1")){
				
				return memberInviter;
			}
			
			String is_default=du.getString("is_default");
			
			Integer photoNum=0;
			//解析字符串
			//TODO
			
			String isOne=null;
			String isTwo=null;
			String isThree=null;
			int i=1;
			int q=0;
			do{
				if(is_default==null)
					break;
				q=is_default.indexOf(",");
				if(q>1){
					if(i==1)
						isOne=is_default.substring(0,q);
					if(i==2)
						isTwo=is_default.substring(0,q);
					
					is_default=is_default.substring(q+1);
				}
				
	 			i++;	
			}while(q!=-1);
			isThree=is_default!=null&&is_default.equals("")?null:is_default;
			
			if(isThree!=null){
				
				//设置每次查询到的默认个人相册都是p1 p2 p3
				
				memberInviter.put("p1", du.getString(isThree));
				photoNum=photoNum+1;
				if(isOne!=null){
					memberInviter.put("p2", du.getString(isOne));
					photoNum=photoNum+1;
				}
				if(isTwo!=null){
					memberInviter.put("p3", du.getString(isTwo));
					photoNum=photoNum+1;
				}
			}
			memberInviter.put("photoNum", photoNum);
		}
		
		return memberInviter;
	}
	
	/**
	 * 相册审核 查询所有人的相册
	 */
	public void getAllPsersonAlbum(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String way=BmobAPI.getStringValueFromJSONObject(jo, "way");
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		Integer skip=0;
		
		if(page!=null&&pageSize!=null&&page>1&&pageSize>0){
			
			skip=(page-1)*pageSize;
		}
		
		String result="";
		/**
		 * way 为空  查询所有人的相册 
		 * way="0" 查询所有未审核的人的相册
		 * way="1" 查询所有已审核的人的相册
		 */
		if(way==null){
			
			result=find("loveyou_photo", "", skip, pageSize, "-createdAt");
			
		}else if(way.equals("0")){
			
			result=find("loveyou_photo", "{\"verify_state\":{\"$ne\":1}}", skip, pageSize, "-createdAt");
		}else if(way.equals("1")){
			
			result=find("loveyou_photo", "{\"verify_state\":{\"$in\":[1]}}", skip, pageSize, "-createdAt");
		}
		
		if(result.indexOf("[")!=-1){
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			return;
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"查询失败或参数错误\"}").toString());
	}
	
	/**
	 * 更改相册审核状态
	 */
	
	public void updateAlbumState(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String objectId=BmobAPI.getObjectIdById("loveyou_photo", "member_id", member_id);
		
		if(objectId==null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户不不存在或未创建相册\"}").toString());
			return;
		}
		
		String result=update("loveyou_photo", objectId, "{\"verify_state\":1}");
		
		if(result.indexOf("At")!=-1){
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			return;
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"操作失败或参数错误\"}").toString());
	}
}
