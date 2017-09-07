package com.loveyou.webController.userinfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.user.PersonAlbumController;

/**
 * 个人信息控制器
 * 
 * @ClassName: UserInfoController
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author 姚永鹏
 * 
 * @date 2016年6月17日 下午4:28:56
 *
 * 
 */
public class UserInfoController extends Bmob {

	
	/**
	 * TODO 设置支付密码（member表增加一个字段MD5加密16位置）
	 */
	@Deprecated
	public void setPaymentPassword(){
		
		JSONObject params=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		/**
		 * 获取支付密码（前台提供）
		 */
		Integer paymentPassword=params.getInteger("payment_password");
		Integer memberId=params.getInteger("member_id");
		System.out.println("-----______________________"+paymentPassword);
		
		/**
		 * 调用MD5加密类进行加密
		 */
		String password_=MD5_Produce.MD5(paymentPassword.toString());
		
		/**
		 * 封装参数
		 */
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("payment_password", password_);
		
		String paramStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 
		 * 获取用户实名信息表中ObjectId
		 */
		String objectId = BmobAPI.getObjectIdById("loveyou_member", "member_id", memberId);
		
		/**
		 * 发送请求
		 */
		//String result = update("loveyou_goods_common", , );
		
//		System.out.println(password_);
//		JSONObject jj=new JSONObject();
//		jj.put("password", password);
		
		
		
		//renderJson(jj);
	}
	
	/**
	 * 获取锁粉的资料。
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getFansInfo(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String memberObject=BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		
		if(memberObject!=null){
			
			JSONObject result=new JSONObject();
			
			String memberInfo=findOne("loveyou_member", memberObject);
			
			JSONObject memberI=JSONObject.parseObject(memberInfo);
			
			Integer invitedNum=memberI.getInteger("invitedNum");
			
			String phoneNumber=memberI.getString("phone_number");
			
			Integer count=0;
				count=BmobAPI.count("loveyou_weixinUser", "{\"inviteNum\":\""+phoneNumber+"\"}");
			
			result.put("invitedNum", invitedNum);
			
			result.put("weixinInvitedNum", count);
			
			count=BmobAPI.count("loveyou_weixinUser", "{\"inviteNum\":\""+phoneNumber+"\",\"member_id\":{\"$gt\":15}}");
			result.put("realInvitedNum", count);
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result.toJSONString()).toString());
			return;
			
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该用户不存在\"}").toString());
	}
	/**
	 * 获取邀请人信息
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public  void getMyInviter(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String Object=BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		
		if(Object!=null){
			
			String memberInfo=findOne("loveyou_member", Object);
			String inviterPhone="";
			try{
				
				JSONObject memberI=JSONObject.parseObject(memberInfo);
				inviterPhone=memberI.getString("inviteNum");
				
			}catch(Exception e){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您的信息有错误，\"}").toString());
				return;
			}
			
			if(inviterPhone==null){
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您的信息中没有邀请人，可能您是注册时未填写邀请人，或者您不是通过通过微信关注好友\"}").toString());
				return;
			}
			
			if(inviterPhone.length()==11){
				
				String inviterObject=BmobAPI.getObjectIdById("loveyou_member", "phone_number", inviterPhone);
				
				String inviterInfo=findOne("loveyou_member", inviterObject);
				
				try{
					
					JSONObject memberInviter=JSONObject.parseObject(inviterInfo);
					memberInviter.remove("objectId");
					memberInviter.remove("member_id");
					memberInviter.remove("available_predeposit");
					memberInviter.remove("user_type");
					
					inviterInfo=memberInviter.toJSONString();
				}catch(Exception e){
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您的邀请人信息有错误，\"}").toString());
					return;
				}
				
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,inviterInfo).toString());
				return;
				
			}else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您的信息中没有邀请人，可能您是注册时未填写邀请人，或者您不是通过通过微信关注好友\"}").toString());
				return;
			}
		}
	}
	
	/**
	 * 通过用户Id查询用户个人资料展示给别人
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public  void getPersonInfo(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String Object=BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		
		if(Object!=null){
			
			String memberInfo=findOne("loveyou_member", Object);
			
			String inviterInfo=null;
				
				try{
					
					JSONObject memberInviter=JSONObject.parseObject(memberInfo);
					memberInviter.remove("objectId");
					memberInviter.remove("member_id");
					memberInviter.remove("available_predeposit");
					memberInviter.remove("user_type");
					
					//获取个人相册中已设默认的图片
					memberInviter.putAll(new PersonAlbumController().getDefaultAlbum(member_id));
					
					String objectId=BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);
					
					System.out.println(objectId);
					
					if(objectId!=null){
						String identityInfo=findOne("identity_authentica", objectId);
						
						JSONObject du=JSONObject.parseObject(identityInfo);
						
						System.out.println(du);
						
						du.remove("idcard");
						du.remove("front_photo");
						du.remove("pay_password");
//						du.remove("back_photo");
						du.remove("createdAt");
						du.remove("back_photo");
						du.remove("id");
						memberInviter.putAll(du);
						
						String user_birthday=du.getString("user_birthday");
						
						if(user_birthday!=null&&user_birthday.lastIndexOf("-")==6){
							user_birthday=user_birthday.replaceFirst("-", "-0");
						}
						if(user_birthday!=null){
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
							
							Date maDate=sdf.parse(user_birthday);
							
							Date m=new Date();
							
							long day=(m.getTime()-maDate.getTime())/(24*60*60*1000)+1;
							
							int b=(int) (day/365);
							
							System.out.println(b);
							memberInviter.put("age", b);
						}
						
					}
					
					inviterInfo=memberInviter.toJSONString();
				}catch(Exception e){
					
					System.out.println(e.getMessage());
					e.printStackTrace();
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您个人信息有错误，\"}").toString());
					return;
				}
				
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,inviterInfo).toString());
				return;
				
			}else{
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"此用户ID不存在\"}").toString());
				return;
			}
	}

	
	public  JSONObject getPersonInfo(Integer member_id){	
		
		String Object=BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		
		if(Object!=null){
			
			String memberInfo=findOne("loveyou_member", Object);
				
				try{
					
					JSONObject memberInviter=JSONObject.parseObject(memberInfo);
					memberInviter.remove("objectId");
					memberInviter.remove("member_id");
					memberInviter.remove("available_predeposit");
					memberInviter.remove("user_type");
					
					//获取个人相册中已设默认的图片
					memberInviter.putAll(new PersonAlbumController().getDefaultAlbum(member_id));
					
					String objectId=BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);
					
					if(objectId!=null){
						String identityInfo=findOne("identity_authentica", objectId);
						
						JSONObject du=JSONObject.parseObject(identityInfo);
						
						du.remove("idcard");
						du.remove("front_photo");
						du.remove("pay_password");
						du.remove("back_photo");
						du.remove("createdAt");
						du.remove("back_photo");
						du.remove("id");
						memberInviter.putAll(du);
						
						String user_birthday=du.getString("user_birthday");
						
						if(user_birthday!=null&&user_birthday.lastIndexOf("-")==6){
							user_birthday=user_birthday.replaceFirst("-", "-0");
						}
						if(user_birthday!=null){
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
							
							Date maDate=sdf.parse(user_birthday);
							
							Date m=new Date();
							
							long day=(m.getTime()-maDate.getTime())/(24*60*60*1000)+1;
							
							int b=(int) (day/365);
							
							System.out.println(b);
							memberInviter.put("age", b);
						}
						
						return memberInviter;
					}
					
				}catch(Exception e){
					
					System.out.println(e.getMessage());
					return null;
					
				}
				
				
			}
		return null;
	}

	
	
	/**
	 * 查询个人信息列表并审核
	 */
	@RequiresRoles("0")
	public  void getAllPersonInfo(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo,"pageSize");
		
		Integer skip=0;
		
		if(page!=null&&pageSize!=null&&page>1&&pageSize>0){
			
			skip=(page-1)*pageSize;
		}
		
		String result=find("identity_authentica", "{\"verfiy_state\":{\"$ne\":1}}", skip, pageSize, "-createdAt");
		
		JSONArray wn=new JSONArray();
		
		if(result.indexOf("results\":[]")==-1){
		
			JSONObject jo1=(JSONObject) JSONObject.parse(result);
			
			JSONArray ja=jo1.getJSONArray("results");
			
			JSONObject jm;
			int i=0;
			for(i=0;i<ja.size();i++){
				
				jm=(JSONObject) ja.get(i);
				Integer member_id=jm.getInteger("member_id");
				
				String objectId=BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
				if(objectId!=null){
					
					String ru=findOne("loveyou_member", objectId);
					
					JSONObject reu=JSONObject.parseObject(ru);
					
					jm.put("username", reu.getString("phone_number"));
					
					jm.put("member_avator", reu.getString("member_avator"));
				}
				
				if(jm!=null)
					wn.add(jm);	
			}
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,wn.toJSONString()).toString());
			return;	
		}			
				
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"没有用户\"}").toString());
		
	}

	/**
	 * 修改用户实名信息审核状态
	 */
	@RequiresRoles("0")
	public void updateUserVrifyState(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String userName=BmobAPI.getStringValueFromJSONObject(jo, "username");
		
		Integer vrifyState=BmobAPI.getIntegerValueFromJSONObject(jo, "vrify_state");
		
		String objectId=BmobAPI.getObjectIdById("loveyou_user", "username", userName);
		
		if(objectId!=null){
			
			String memberObject=BmobAPI.getObjectIdById("loveyou_member", "phone_number", userName);
			
			if(memberObject!=null){
			
				String memberInfo=findOne("loveyou_member", memberObject);
				
				JSONObject memberJo=JSONObject.parseObject(memberInfo);
				
				Integer member_id=memberJo.getInteger("member_id");
				
				//用户实名信息表
				String idInfo=BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);
				
				if(idInfo!=null){
				
					String result=	update("identity_authentica", idInfo, "{\"verfiy_state\":1}");
					
					if(result.indexOf("At")!=-1){
						
						result=update("loveyou_user", objectId, "{\"system_type\":"+vrifyState+"}");
						
						if(result.indexOf("At")!=-1){
							renderJson(new JsonResult(JsonResult.STATE_SUCCESS ,result).toString());
							return;
						}
					}
				}
			}
			
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL ,"{\"msg\":\"用户不存在\"}").toString());
		return;
	}
	
	/**
	 * 判断用户实名信息是否真实 -yyp
	 */
	
	public void checkUserVrifyState(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String userName=BmobAPI.getStringValueFromJSONObject(jo, "username");
		
		String objectId=BmobAPI.getObjectIdById("loveyou_user", "username", userName);
		
		if(objectId!=null){
			
			String result=findOne("loveyou_user", objectId);
			
			JSONObject jor=JSONObject.parseObject(result);
			
			Integer systemType=jor.getInteger("system_type");
			
			boolean b=false;
			
			if(systemType<=1){
				b=true;
			}
			
			if(result.indexOf("At")!=-1){
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS ,"{\"isCheckIdentity\":"+b+"}").toString());
				return;
			}
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL ,"{\"msg\":\"用户不存在\"}").toString());
		return;
	}
	
	
	/**
	 * 修改新手指引是否查看状态
	 */
	public void updateLookGuideState(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id =BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String objectId =BmobAPI.getObjectIdById("loveyou_member", "member_id", member_id);
		
		if(objectId==null){
		
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户ID不能为空或不存在\"}").toString());
			return;
		}
		
		String result=update("loveyou_member", objectId, "{\"guide_look_state\":1}");
		
		if(result.indexOf("At")!=-1){
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			return;
		}
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"操作失败\"}").toString());
		
	}
}
