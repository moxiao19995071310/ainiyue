package com.loveyou.webController.voice;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;
import com.loveyou.webController.common.JsonResult;
import com.loveyou.webController.common.JsonToBmob;
import com.loveyou.webController.common.RepeatStyle;
import pay.weixin.test.HttpUtil;

/**
 * 这个类的作用是微信语音和微信文字消息管理，
 * @author yyp
 *
 */
public class VoiceController extends Bmob {

	private static Logger log = Logger.getLogger(VoiceController.class);
	
	public static String access_token;
	
	public static String WEIXINPATH=null;
	
	public VoiceController(){
	
		String ticketd=(String) JFinal.me().getServletContext().getAttribute("ticket");
		if(ticketd==null){
			String ticket="";
			String appid=PropKit.get("appId");
			String appSecret=PropKit.get("AppSecret");
			
			String url1 ="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appSecret;
			
			String access_token1=HttpUtil.sendGet(url1, "UTF-8");
			
			JSONObject jo=JSONObject.parseObject(access_token1);
			
			String access=jo.getString("access_token");
			
			//初始化access_token
			VoiceController.access_token=access;
		
			if(access!=null&&access.length()>10){
				
				url1="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access+"&type=jsapi";
				
				ticket=HttpUtil.sendGet(url1, "UTF-8");
				
				jo=JSONObject.parseObject(ticket);
				
				String du=jo.getString("ticket");
				
				if(du!=null&&du.length()>5){
					ticket=du;
		//			hs.setAttribute("ticket", ticket);
					
					JFinal.me().getServletContext().setAttribute("ticket", ticket);
					System.out.println("设置tick成功");
				}
				
			}
		
			//加载application
			
			log.error("application"+"1");
			
			updateTicketByApplication();
		}
		
		if(WEIXINPATH==null){
			try {
				WEIXINPATH = new File(getClass().getClassLoader().getResource("").toURI()).getPath();
			} catch (URISyntaxException e2) {
				// TODO Auto-generated catch bloc
				System.out.println("weixinPath设置失败");
			}
			
			WEIXINPATH=WEIXINPATH.replace("\\", "/");
			WEIXINPATH=WEIXINPATH.replace("/WEB-INF/classes", "/upload/love_you2/weixinVoice/");
			WEIXINPATH.replace("\\", "/");
		}
	}
	

	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();
	
		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("AppSecret"));
	
		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey",
				"setting it in config file"));
		return ac;
	}


	/**
	 * 获取微信参数config
	 */
	public void getWeixinParam(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		if(jo==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"url为空\"}").toString());
			return;
		}
		
		String url=BmobAPI.getStringValueFromJSONObject(jo, "url");
		
		if(url==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"url为空\"}").toString());
			return;
		}
		
		ApiConfigKit.putApiConfig(getApiConfig());
		
		String ticket="";
//		JFinal.me().getServletContext().setAttribute("ticket", "kgt8ON7yVITDhtdwci0qeXCizO1KAukdfbO-VtpX_WXWxeimzC7ozPLMOW_y2g5y90Gn0DPG4CTunFl6seHDwg");
		String ticketis=(String) JFinal.me().getServletContext().getAttribute("ticket");
		
		if (ticketis!=null&&!ticketis.equals("")){
			ticket=ticketis;
		}else{
			ticket=(String) JFinal.me().getServletContext().getAttribute("ticket");
		}
		
		String jsapi_ticket="";
		
		if(!ticket.equals("")) jsapi_ticket=ticket;
		
		String nonce_str = create_nonce_str();

		String timestamp = create_timestamp();
		// 这里参数的顺序要按照 key 值 ASCII 码升序排序
		//注意这里参数名必须全部小写，且必须有序
		String  str = "jsapi_ticket=" + jsapi_ticket +
        "&noncestr=" + nonce_str +
        "&timestamp=" + timestamp +
        "&url=" + url;

		String signature = pay.weixin.test.HttpUtil.SHA1(str);

		JSONObject result=new JSONObject();
		
		result.put("appId", ApiConfigKit.getApiConfig().getAppId());
		result.put("nonceStr", nonce_str);
		result.put("timestamp", timestamp);
		result.put("url", url);
		result.put("signature", signature);
		result.put("jsapi_ticket", jsapi_ticket);
		

		renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result.toJSONString()).toString());
		

	}
	
	private static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	private static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 定时更换application
	 * timer
	 */
	
	private void updateTicketByApplication(){
		
		Timer timer=new Timer();
		
		TimerTask tk=new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
					String ticket="";
					
					//通过url获取access_token
					String appid=PropKit.get("appId");
					String appSecret=PropKit.get("AppSecret");
					
					String url1 ="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appSecret;
					
					String access_token1=HttpUtil.sendGet(url1, "UTF-8");
					
					JSONObject jo=JSONObject.parseObject(access_token1);
					
					String access=jo.getString("access_token");
					
					new VoiceController();
					//初始化
					VoiceController.access_token=access;
					
					log.error("已设置access_token"+access);
					
					if(access!=null&&access.length()>10){
						
						url1="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access+"&type=jsapi";
						
						ticket=HttpUtil.sendGet(url1, "UTF-8");
						
						jo=JSONObject.parseObject(ticket);
						
						String du=jo.getString("ticket");
						
						if(du!=null&&du.length()>5){
							ticket=du;
//							hs.setAttribute("ticket", ticket);
							
							JFinal.me().getServletContext().setAttribute("ticket", ticket);
							
							System.out.println("设置ticket成功");
						}
						
					}
				
			}
			
		};
		// access_token 微信端说有7200秒但并没有所以设置为1小时即重新获取
		timer.schedule(tk, 7200000,7200000);
	}
	
	/**
	 * 上传录音到本地服务器测试
	 */

	public void uploadVoice(){
		
		JSONObject  jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		String mediaId=BmobAPI.getStringValueFromJSONObject(jo, "serverId");
		
		if(access_token==null||access_token.equals("")){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"access_token为空，调试错误\"}").toString());
			return;
		}
		
		String url="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+access_token+"&media_id="+mediaId;
		
		String path=null;
		try {
			path = new File(getClass().getClassLoader().getResource("").toURI()).getPath();
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"获取本机路径错误\"}").toString());
			return;
		}
		path=path.replace("\\", "/");
		path=path.replace("/WEB-INF/classes", "/upload/love_you2/");
		path.replace("\\", "/");

		String voiceFileName=mediaId+"weixinVoice.amr";
		
		String voiceFilePath=path+"weixinVoice";
		
		try {
			JsonToBmob.download(url, voiceFileName, voiceFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"下载失败\"}").toString());
			return;
		}
	
		renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"msg\":\"上传成功\"}").toString());
		
	}
	
	/**
	 * 调用接口，使用该接口可以在服务器端调用，通过传递mediaId,下载已经传到微信服务器中的录音文件，并保存到本地以及bmob云
	 * @param mediaId
	 * 微信服务器文件名唯一标识
	 */
	public String uploadVoiceToNative(String mediaId){
		
		if(access_token==null||access_token.equals("")){
			
			return "access_token为空，或失败";
		}
		
		System.out.println(access_token);
		
		String url="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+access_token+"&media_id="+mediaId;
		
		String jsono=pay.weixin.test.HttpUtil.sendGet(url,"UTF-8");
		JSONObject jo=null;
		try{
			jo=JSONObject.parseObject(jsono);
		}catch(Exception e){
			
		}
		
		if(jo!=null&&jo.get("errcode")!=null){
		
			//通过url获取access_token
			String appid=PropKit.get("appId");
			String appSecret=PropKit.get("AppSecret");
			
			String url1 ="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appSecret;
			
			String access_token1=HttpUtil.sendGet(url1, "UTF-8");
			
			JSONObject jod=JSONObject.parseObject(access_token1);
			
			String access=jod.getString("access_token");
			
			new VoiceController();
			//初始化
			VoiceController.access_token=access;
			
			log.error("已设置access_token"+access);
			
			url="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+access_token+"&media_id="+mediaId;
		}
		
		
		String path=null;
		try {
			path = new File(getClass().getClassLoader().getResource("").toURI()).getPath();
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			
			return "获取本地路径失败";
		}
		path=path.replace("\\", "/");
		path=path.replace("/WEB-INF/classes", "/upload/love_you2/");
		path.replace("\\", "/");

		Random rand=new Random();
		
		//随机截取字符串
		int du=rand.nextInt(63-15)+1;
		String voiceFileName=mediaId.substring(du, du+15)+".amr";
		
		String voiceFilePath=path+"weixinVoice";
		
		try {
			
			JsonToBmob.download(url, voiceFileName, voiceFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "下载失败";
		}
	

		return voiceFilePath+"/"+voiceFileName;
	}
	
	public void demo(){
		
		String str=getPara("m");
		String um=uploadVoiceToNative(str);
		renderText(um+"  "+VoiceController.access_token);
	}
	
	/**
	 * 上传音频文件到云服务器bmob
	 * @param fullPath
	 * 
	 * @return
	 * 上传结果
	 */
	public String uploadVoiceToBmob(String fullPath){

		return Bmob.uploadFile3(fullPath);
	}
	
	/**
	 * 上传文件接口
	 */
	public void uploadFileDemo(){
		
		UploadFile uf= getFile();
		
		File m=uf.getFile();
		
		String result=uploadVoiceToBmob(m.getAbsolutePath());
		
		renderText(result);
	}
	
	/**
	 * 发布需求通过语音
	 */
	
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void publishRequirementByVoice(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		JSONObject data=new JSONObject();
		
		String mediaId=BmobAPI.getStringValueFromJSONObject(jo, "mediaId");
		
		String wen=uploadVoiceToNative(mediaId);
		
		if(wen.indexOf(".amr")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件上传出错，请检查\"}").toString());
			return;
		}
		
		String mp3Path=wen.replaceFirst(".amr", ".mp3");
		mp3Path=mp3Path.replaceFirst("weixinVoice", "voiceMp3");
		
		
		
		if(!amrToMp3(wen, mp3Path)){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件转换出错，请检查\"}").toString());
			return;
		}
		String result=uploadVoiceToBmob(mp3Path);
		if(result.indexOf("filename")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"文件上传到云端服务器出错\"}").toString());
			return;
		}
			
			JSONObject ou=JSONObject.parseObject(result);
			String fileName=ou.getString("filename");
			String url=ou.getString("url");
			
			data.put("file_name", fileName);
			data.put("file_url", url);
		
		
		String localId=BmobAPI.getStringValueFromJSONObject(jo, "localId");
		
		//语音存根表中语音类型为需求
		String voice_type=JsonResult.VOICE_TYPE_REQUIREMENT;
		
		Integer memberId=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		Long nowDate=System.currentTimeMillis();
		
		String nowTime=nowDate.toString();
		
		data.put("mediaId", mediaId);
		
		data.put("localId", localId);
		
		data.put("voice_type", voice_type);
		
		data.put("member_id", memberId);
		
		data.put("addTime", nowTime);
		
		String du=insert("loveyou_voice", data.toJSONString());
		
		if(du.indexOf("At")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"数据错误，不能插入数据库\"}").toString());
			return;
		}
		
		//服务期间花费谁来出
		Integer expenditure_type=BmobAPI.getIntegerValueFromJSONObject(jo, "expenditure_type");
		
		Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(jo, "fc_id");
		
		Integer xq_price =BmobAPI.getIntegerValueFromJSONObject(jo, "xq_price");
		
		Integer areaid_1=BmobAPI.getIntegerValueFromJSONObject(jo, "areaid_1");
		
		Integer areaid_2=BmobAPI.getIntegerValueFromJSONObject(jo, "areaid_2");

		String xq_area=mergeAddressToAreaInfo(areaid_1, areaid_2);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
	
		paramMap.put("fc_id", fc_id);
		paramMap.put("member_id", memberId);
		paramMap.put("xq_state", 1);// 1上架
		paramMap.put("xq_addtime", nowDate/1000);
		paramMap.put("xq_price", xq_price);
		paramMap.put("areaid_1", areaid_1);
		paramMap.put("areaid_2", areaid_2);
		paramMap.put("xq_area", xq_area);
		paramMap.put("xq_verify", 10);// 10审核中
		paramMap.put("xq_commend", 0);// 需求推荐 1是，0否，默认为0
		paramMap.put("xq_freight", 0);// 报销交通费 0为不报交通费
		paramMap.put("xq_vat", 0);// 是否开具增值税发票 1是，0否
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向需求公共表发送请求
		 */
		result = insert("shopnc_xq_common", jsonStr);
		System.out.println(result);
		/**
		 * 解析result字符串
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		String objectId = (String) resultMap.get("objectId");
		/**
		 * 获取服务公共表id
		 */
		String xqCommonInfo = findOne("shopnc_xq_common", objectId);
		Map<String, Object> xqCommonInfoMap = BmobAPI.jsonStrToMap(xqCommonInfo);
		Integer xq_commonid = (Integer) xqCommonInfoMap.get("xq_commonid");
		/**
		 * 向服务表发送请求
		 */
		paramMap.put("xq_commonid", xq_commonid);
		paramMap.put("visitNum", 0);
		//yyp 添加
		paramMap.put("localId", localId);
		paramMap.put("voice_url", url);
		paramMap.put("expenditure_type", expenditure_type);
		
		String latitude=BmobAPI.getStringValueFromJSONObject(jo, "latitude");
		
		Integer latitude_real=Integer.parseInt(latitude.substring(0, latitude.indexOf(".")));
		
		if(latitude_real>=90){ 
			Integer latitude_real_new=90-latitude_real;
			latitude=latitude.replaceAll(latitude_real.toString()+".", latitude_real_new.toString()+".");
		}
		
		String longitude=BmobAPI.getStringValueFromJSONObject(jo, "longitude");
		
		String du1="\"location\":{\"__type\":\"GeoPoint\",\"latitude\":"+latitude+",\"longitude\":"+longitude+"}";
		
		
		jsonStr = BmobAPI.mapToJSONStr(paramMap);
		
		result = insert("shopnc_xq", jsonStr.substring(0, jsonStr.length()-1)+","+du1+"}");
		
		if(result.indexOf("At")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"请求参数不合法\"}").toString());
			return;
		}else{
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,du).toString());
		}
		
	}
	
	/**
	 * 大厅需求查询
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void requirementHallShow(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer fc_id=BmobAPI.getIntegerValueFromJSONObject(jo, "fc_id");
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		String latitude=BmobAPI.getStringValueFromJSONObject(jo, "latitude");
		
		if(latitude==null||latitude.equals("")){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"位置信息为空不能查询\"}").toString());
			return;
		}
		
		Integer latitude_real=Integer.parseInt(latitude.substring(0, latitude.indexOf(".")));
		/**
		 *  如果纬度大于90，即取负数，bmob不能储存大于90的纬度
		 */
		if(latitude_real>=90){ 
			Integer latitude_real_new=90-latitude_real;
			latitude=latitude.replaceAll(latitude_real.toString()+".", latitude_real_new.toString()+".");
		}
		
		String longitude=BmobAPI.getStringValueFromJSONObject(jo, "longitude");
		
		if(longitude==null||longitude.equals("")){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"位置信息为空不能查询\"}").toString());
			return;
		}
		String maxDestanceInKm=BmobAPI.getStringValueFromJSONObject(jo, "maxDestanceInKm");
		
		Integer skip=0;
		
		if(page!=null&&pageSize!=null){
			
			skip=(page-1)*pageSize;
		}else{
			pageSize=1;
		}
		
		String fc_string="\"fc_id\":"+fc_id+",";
		if(fc_id==null){
			
			fc_string ="";
		}
		

		//查询附近
		
		/**
		 *  "location": {
	            "$nearSphere": {
	                "__type": "GeoPoint",
	                "latitude": 30.0,
	                "longitude": -20.0
	            },
        		"$maxDistanceInKilometers": 10.0
        	}
		 */
		
		String result=find("shopnc_xq", "{\"xq_verify\":1,\"xq_state\":1,\"localId\":{\"$exists\":true},"+fc_string+"\"location\": {\"$nearSphere\": {\"__type\": \"GeoPoint\",\"latitude\": "+latitude+",\"longitude\":"+longitude+"},\"$maxDistanceInKilometers\": "+maxDestanceInKm+"}}", skip, pageSize,"-createdAt");
		
		if(result.indexOf("[")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"条件错误，请重新输入\"}").toString());
			return;
		}else{
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			
		}
		
	}
	/**
	 * 发布服务语音
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void publishServiceByVoice(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		JSONObject data=new JSONObject();
		
		String mediaId=BmobAPI.getStringValueFromJSONObject(jo, "mediaId");
		
		String wen=uploadVoiceToNative(mediaId);
		System.out.println(wen);
		
		if(wen.indexOf(".amr")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件上传出错，请检查\"}").toString());
			return;
		}
	
		String mp3Path=wen.replaceFirst(".amr", ".mp3");
		mp3Path=mp3Path.replaceFirst("weixinVoice", "voiceMp3");
		
		if(!amrToMp3(wen, mp3Path)){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件转换出错，请检查\"}").toString());
			return;
		}
		String result=uploadVoiceToBmob(mp3Path);
		
		if(result.indexOf("filename")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"文件上传到云端服务器出错\"}").toString());
			return;
		}
			
			JSONObject ou=JSONObject.parseObject(result);
			String fileName=ou.getString("filename");
			String url=ou.getString("url");
			
			data.put("file_name", fileName);
			data.put("file_url", url);
		
		
		String localId=BmobAPI.getStringValueFromJSONObject(jo, "localId");
		
		//语音存根表中语音类型为需求
		String voice_type=JsonResult.VOICE_TYPE_SERVICE;
		
		Integer memberId=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String member=BmobAPI.getObjectIdById("loveyou_member", "member_id", memberId);
		
		if(member==null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户信息不匹配\"}").toString());
			return;
		}
		String memberInfo=findOne("loveyou_member", member);
		
		if(memberInfo.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户信息不匹配\"}").toString());
			return;
		}
		
		JSONObject memberObject=JSONObject.parseObject(memberInfo);
		
		Integer store_id=memberObject.getInteger("store_id");
		
		Long nowDate=System.currentTimeMillis();
		
		String nowTime=nowDate.toString();
		
		data.put("mediaId", mediaId);
		
		data.put("localId", localId);
		
		data.put("voice_type", voice_type);
		
		data.put("member_id", memberId);
		
		data.put("addTime", nowTime);
		
		String du=insert("loveyou_voice", data.toJSONString());
		
		if(du.indexOf("At")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"数据错误，不能插入数据库\"}").toString());
			return;
		}
		
		Integer fc_id = BmobAPI.getIntegerValueFromJSONObject(jo, "fc_id");
		
		long fw_addtime = System.currentTimeMillis() / 1000;
		/**
		 * 获取服务价格(前台提供)
		 */
		Integer fw_price = BmobAPI.getIntegerValueFromJSONObject(jo, "fw_price");
		/**
		 * 获取起始时间(前台提供)
		 */
		/**
		 * 参照时间
		 */
		String fw_keyday=BmobAPI.getStringValueFromJSONObject(jo, "fw_keyday");
		
		String pattern2="yyyy-MM-dd";
		SimpleDateFormat sdfKeyday =new SimpleDateFormat(pattern2);
		
		Date d=new Date();
		try {
			d=sdfKeyday.parse(fw_keyday);
		} catch (ParseException e1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"时间格式错误，请更改后重试\"}").toString());
			return;
		}
		
		String fw_startHour=BmobAPI.getStringValueFromJSONObject(jo, "fw_startHour");
		/**
		 * 获取结束时间(前台提供)
		 */
//		String  fw_endtime = getPara("fw_endtime");
		
		String fw_endHour=BmobAPI.getStringValueFromJSONObject(jo,"fw_endHour");
		
		Integer expenditure_type=BmobAPI.getIntegerValueFromJSONObject(jo, "expenditure_type");
		
		String pattern="yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		
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
		paramMap.put("member_id", memberId);
		paramMap.put("fc_id", fc_id);
//		paramMap.put("fc_name", fc_name);
		paramMap.put("fw_price", fw_price);
		paramMap.put("store_id", store_id);
		paramMap.put("fw_state", 1);// 默认上架
		paramMap.put("fw_verify", 10);// 默认审核中

		//yyp 添加
		paramMap.put("fw_startHour", fw_startHour);
		paramMap.put("fw_endHour", fw_endHour);
		paramMap.put("fw_starttime", fw_starttime);
		paramMap.put("fw_endtime", fw_endtime);
		paramMap.put("fw_area", fw_area);//服务区域存序列化数组(也可以存经纬度的值)
		paramMap.put("fw_repeat_style", fw_repeat_style);
		paramMap.put("fw_repeat_endtime", fw_repeat_endtime/1000);

		paramMap.put("voice_url", url);
		String jsonStr = BmobAPI.mapToJSONStr(paramMap);
		/**
		 * 向服务公共表发送请求
		 */
		System.out.println(jsonStr);
		
		result = insert("shopnc_fb_classtotal", jsonStr);
		System.out.println(result);
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"参数错误，不能插入到数据库\"}").toString());
			return;
		}
		/**
		 * 解析result字符串
		 */
		Map<String, Object> resultMap = BmobAPI.jsonStrToMap(result);
		String objectId = (String) resultMap.get("objectId");
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
		paramMap.put("member_id", memberId);
		paramMap.put("store_id", store_id);
		
		objectId =BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		if(objectId==null){
		
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"store_id不存在\"}").toString());
			return;
		}
		du=findOne("loveyou_store", objectId);
		
		JSONObject jd=JSONObject.parseObject(du);
		
		String store_name=jd.getString("store_name");
		
		paramMap.put("store_name", store_name);
		
		paramMap.put("fc_id", fc_id);
		
		objectId =BmobAPI.getObjectIdById("shopnc_fwxq_class", "fc_id", fc_id);
		if(objectId==null){
		
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"fc_id不存在\"}").toString());
			return;
		}
		du=findOne("shopnc_fwxq_class", objectId);
		
		jd=JSONObject.parseObject(du);
		
		String fc_name=jd.getString("fc_name");
		
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
		paramMap.put("fb_id", fb_id);// 发布编号
		paramMap.put("fw_click", 0);// 点击次数，默认为0
		paramMap.put("fw_salenum", 0);// 销售数量，默认为0
		paramMap.put("fw_collect", 0);// 收藏数量，默认为0
		
		//yyp 添加
		paramMap.put("localId", localId);
		paramMap.put("voice_url", url);
		paramMap.put("expenditure_type", expenditure_type);
		
		String latitude=BmobAPI.getStringValueFromJSONObject(jo, "latitude");
		
		String longitude=BmobAPI.getStringValueFromJSONObject(jo, "longitude");
		
		jsonStr = BmobAPI.mapToJSONStr(paramMap);
		
		paramMap.put("latitude", latitude);
		
		paramMap.put("longitude", longitude);
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
				
				
				result = sendPublishServiceRequest(fw_starttime, fw_endtime, paramMap, jsonStr);
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
					result = sendPublishServiceRequest(startTime/1000, endTime/1000, paramMap, jsonStr);
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
							
							result = sendPublishServiceRequest(startTime/1000, endTime/1000, paramMap, jsonStr);
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
					result = sendPublishServiceRequest(startTime/1000, endTime/1000, paramMap, jsonStr);
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
						result = sendPublishServiceRequest(startTime/1000, endTime/1000, paramMap, jsonStr);
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
	 * 服务大厅
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void serviceHallShow(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer fc_id=BmobAPI.getIntegerValueFromJSONObject(jo, "fc_id");
		
		Integer page=BmobAPI.getIntegerValueFromJSONObject(jo, "page");
		
		Integer pageSize=BmobAPI.getIntegerValueFromJSONObject(jo, "pageSize");
		
		String latitude=BmobAPI.getStringValueFromJSONObject(jo, "latitude");
		
		Integer latitude_real=Integer.parseInt(latitude.substring(0, latitude.indexOf(".")));
		/**
		 *  如果纬度大于90，即取负数，bmob不能储存大于90的纬度
		 */
		if(latitude_real>=90){ 
			Integer latitude_real_new=90-latitude_real;
			latitude=latitude.replaceAll(latitude_real.toString()+".", latitude_real_new.toString()+".");
		}
		
		String longitude=BmobAPI.getStringValueFromJSONObject(jo, "longitude");
		
		String maxDestanceInKm=BmobAPI.getStringValueFromJSONObject(jo, "maxDestanceInKm");
		
		Integer skip=0;
		
		if(page!=null&&pageSize!=null){
			
			skip=(page-1)*pageSize;
		}else{
			pageSize=1;
		}
		
		String fc_string="\"fc_id\":"+fc_id+",";
		if(fc_id==null){
			
			fc_string ="";
		}
		
		//查询附近
		
		/**
		 *  "location": {
	            "$nearSphere": {
	                "__type": "GeoPoint",
	                "latitude": 30.0,
	                "longitude": -20.0
	            },
        		"$maxDistanceInKilometers": 10.0
        	}
		 */
		
		String result=find("shopnc_fw", "{\"fw_verify\":1,\"fw_state\":1,\"localId\":{\"$exists\":true},"+fc_string+"\"location\": {\"$nearSphere\": {\"__type\": \"GeoPoint\",\"latitude\": "+latitude+",\"longitude\":"+longitude+"},\"$maxDistanceInKilometers\": "+maxDestanceInKm+"}}", skip, pageSize,"-createdAt");
		
		if(result.indexOf("[")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"条件错误，请重新输入\"}").toString());
			return;
		}else{
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,result).toString());
			
		}
		
	}
	
	/**
	 * 申请创建个人店铺店铺带语音和实名认证一起提交
	 */
	@RequiresRoles(value={"1","0","2"},logical=Logical.OR)
	public void individualCreateStore(){
		
		List<Map<String, Object>> maps= uploadMoreBmobListMap2();
		
		Integer member_id=getParaToInt("member_id");
		
		String membeerObject=BmobAPI.getObjectIdById("loveyou_store", "member_id", member_id);
		
		if(membeerObject!=null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"当前账户已创建店铺，不能重复创建\"}").toString());
			return;
		}
		
		JSONObject jo=new JSONObject();//店铺json对象
		JSONObject person=new JSONObject();//个人信息资料
		
		String back = null;
		int i=0;
		for (i=0;i<maps.size();i++){
			String paramtename=(String) maps.get(i).get("parametername");
			if(paramtename.equals("back")){
				back=(String) maps.get(i).get("url");
				person.put("back_photo", back);
				jo.put("store_label_person", back);
			}
		}
		
		String mediaId=getPara("mediaId");
		
		String wen=uploadVoiceToNative(mediaId);
		
		if(wen.indexOf(".amr")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件上传出错，请检查\"}").toString());
			return;
		}
		
		String mp3Path=wen.replaceFirst(".amr", ".mp3");
		mp3Path=mp3Path.replaceFirst("weixinVoice", "voiceMp3");
		
		if(!amrToMp3(wen, mp3Path)){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件转换出错，请检查\"}").toString());
			return;
		}
		
		String result=uploadVoiceToBmob(mp3Path);
		
		if(result.indexOf("filename")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"文件上传到云端服务器出错\"}").toString());
			return;
		}
			
		JSONObject ou=JSONObject.parseObject(result);
		String fileName=ou.getString("filename");
		String url=ou.getString("url");
			
		jo.put("name", fileName);
		jo.put("voice_url", url);
		
		
		String localId=getPara("localId");
		
		jo.put("localId", localId);
		
		/**
		 * 获取store_name
		 */
		String storeName=getPara("store_name");
		jo.put("store_name", storeName);
		
		
		jo.put("member_id",member_id);	
		person.put("member_id", member_id);
		//member_name
		/**
		 * 获取会员姓名(前台提供)
		 */
		String name = getPara("name");
		person.put("name", name);
		/**
		 * 获取会员身份证号码(前台提供)
		 */
		String idcard = getPara("idcard");
		person.put("idcard", idcard);
		/**
		 * 获取性别
		 */
		Integer gender = getParaToInt("gender");//1为男，2为女
		person.put("gender", gender);
		
		/**
		 * 添加用户生日，身高，是否婚否
		 */
		String user_birthday=getPara("user_birthday");//yyyy-MM-dd
		
		 //yyyy-MM-dd
		 if(user_birthday!=null&&user_birthday.lastIndexOf("-")==6)
			 user_birthday=user_birthday.replaceFirst("-", "-0");
		
		Integer stature=getParaToInt("stature");//身高单位cm
		
		Integer bachelordom=getParaToInt("backelordom"); //1为单身，0为否
		
		person.put("user_birthday", user_birthday);
		
		person.put("stature", stature);
		
		person.put("bachelordom", bachelordom);
		
		//星座
		String constellation=JsonToBmob.verdictConstellation(user_birthday);
		
		if(constellation.equals("false")){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"日期格式错误\"}").toString());
			return;
		}
		
		person.put("constellation", constellation);
		
		jo.put("seller_name", name);
		
		jo.put("store_owner_card", idcard);
		
		jo.put("store_state", 2);//店铺状态1为默认开启，(0关闭，1开启，2审核中),
		
		String store_banner=getPara("store_banner");//店铺横幅
		jo.put("store_banner", store_banner);
		String store_zy=getPara("store_zy");//主营商品
		jo.put("store_zy",store_zy==null?"":store_zy);
		String description=getPara("description");//描述
		jo.put("description", description==null?"":description);
		result=insert("identity_authentica", person.toJSONString());
		if(result.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,result).toString());
			return;
		}
		
		result=insert("loveyou_store", jo.toJSONString());
		
		/** 获取刚创建的店铺ID
		 */
		JSONObject jm=JSONObject.parseObject(result);
		String objectId=jm.getString("objectId");
		String m=findOne("loveyou_store", objectId);
		JSONObject js=JSONObject.parseObject(m);
		Integer store_id=js.getInteger("store_id");
		
		/** 插入到member 表中
		 */
		ArrayList<String> list =BmobAPI.getAllObjectIdById("loveyou_member", "{\"member_id\":"+member_id+"}");
		if(null!=list)
			for(String object:list){
				BmobAPI.update("loveyou_member", object, "{\"store_id\":"+store_id+"}");
			}
		else{
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		}

		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result)
					.toString());
		}
		
	}
	
	
	/**
	 * 修改店铺信息及个人信息
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void updateIndividualStore(){
		
		JSONObject param=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		JSONObject jo=new JSONObject();//店铺json对象
	
		Integer store_id=BmobAPI.getIntegerValueFromJSONObject(param, "store_id");
		
		String objectId=BmobAPI.getObjectIdById("loveyou_store", "store_id", store_id);
		
		if(objectId==null){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您要修改的店铺不存在\"}").toString());
			return;
		}
		
		String mediaId=BmobAPI.getStringValueFromJSONObject(param, "mediaId");
		
		String wen=uploadVoiceToNative(mediaId);
		
		if(wen.indexOf(".amr")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件上传出错，请检查\"}").toString());
			return;
		}
		
		String mp3Path=wen.replaceFirst(".amr", ".mp3");
		mp3Path=mp3Path.replaceFirst("weixinVoice", "voiceMp3");
		
		if(!amrToMp3(wen, mp3Path)){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件转换出错，请检查\"}").toString());
			return;
		}
		
		String result=uploadVoiceToBmob(mp3Path);
		
		if(result.indexOf("filename")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"文件上传到云端服务器出错\"}").toString());
			return;
		}
			
		JSONObject ou=JSONObject.parseObject(result);
		String fileName=ou.getString("filename");
		String url=ou.getString("url");
			
		jo.put("name", fileName);
		jo.put("voice_url", url);
		
		
		String localId=BmobAPI.getStringValueFromJSONObject(param, "localId");
		
		jo.put("localId", localId);
		
		/**
		 * 获取store_name
		 */
		String storeName=BmobAPI.getStringValueFromJSONObject(param,"store_name");
		jo.put("store_name", storeName);
				
		jo.put("store_state", 2);//店铺状态1为默认开启，(0关闭，1开启，2审核中),
		
		String store_banner=BmobAPI.getStringValueFromJSONObject(param,"store_banner");//店铺横幅
		jo.put("store_banner", store_banner);
		String store_zy=BmobAPI.getStringValueFromJSONObject(param,"store_zy");//主营商品
		jo.put("store_zy",store_zy==null?"":store_zy);
		String description=BmobAPI.getStringValueFromJSONObject(param, "description");//描述
		jo.put("description", description==null?"":description);
		
		
		
		result=update("loveyou_store", objectId,jo.toJSONString());

		if (result.indexOf("updatedAt") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result)
					.toString());
		}
		
	}
	
	/**
	 * 查询用户店铺信息及用户个人信息
	 */
	@RequiresRoles(value={"1","0"},logical=Logical.OR)
	public void getStoreAndPersionInfo(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer member_id =BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		String membeerObject=BmobAPI.getObjectIdById("loveyou_store", "member_id", member_id);
		
		String du=BmobAPI.findOne("loveyou_store", membeerObject);
		
		if(du.indexOf("objectId")!=-1){
			
			JSONObject info=JSONObject.parseObject(du);
			
			String objectId=BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);
			
			du=BmobAPI.findOne("identity_authentica", objectId);
			
			if(du.indexOf("objectId")!=-1){
				
				JSONObject personInfo=JSONObject.parseObject(du);
				
				String name=personInfo.getString("name");
				
				personInfo.put("name", name.replace(name.length()>=3?name.substring(1, 2):name.substring(0,1).equals(name.substring(1,2))?name.substring(0, 2):name.substring(0,1), !name.substring(0,1).equals(name.substring(1,2))?"*":name.substring(0,1)+"~"));
				
				personInfo.put("idcard", personInfo.getString("idcard").replace(personInfo.getString("idcard").substring(10, 15), "*****"));
				
				info.remove("store_label_person");
				
				info.remove("store_owner_card");
				
				info.remove("name");
				
				info.remove("seller_name");
				
				info.putAll(personInfo);
						
				renderJson(new JsonResult(JsonResult.STATE_SUCCESS,info.toJSONString()).toString());
				return;
				
			}
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"用户信息不全或未填入信息\"}").toString());
		
	}
	
	/**
	 * 修改用户店铺及用户个人信息
	 */
	@RequiresRoles(value={"1","0","2","3"},logical=Logical.OR)
	public void updateStoreAndPersonInfo(){
		
		List<Map<String, Object>> maps= uploadMoreBmobListMap2();
		
		Integer member_id=getParaToInt("member_id");
		
		String membeerObject=BmobAPI.getObjectIdById("loveyou_store", "member_id", member_id);
		
		if(membeerObject==null){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"当前账户未创建店铺，请先创建店铺\"}").toString());
			return;
		}
		
		JSONObject jo=new JSONObject();//店铺json对象
		JSONObject person=new JSONObject();//个人信息资料
		
		String back = null;
		int i=0;
		for (i=0;i<maps.size();i++){
			String paramtename=(String) maps.get(i).get("parametername");
			if(paramtename.equals("back")){
				back=(String) maps.get(i).get("url");
				person.put("back_photo", back);
				jo.put("store_label_person", back);
			}
		}
		
		String mediaId=getPara("mediaId");
		
		String wen=uploadVoiceToNative(mediaId);
		
		if(wen.indexOf(".amr")==-1){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件上传出错，请检查\"}").toString());
			return;
		}
		
		String mp3Path=wen.replaceFirst(".amr", ".mp3");
		mp3Path=mp3Path.replaceFirst("weixinVoice", "voiceMp3");
		
		if(!amrToMp3(wen, mp3Path)){
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"该语音文件转换出错，请检查\"}").toString());
			return;
		}
		
		String result=uploadVoiceToBmob(mp3Path);
		
		if(result.indexOf("filename")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"文件上传到云端服务器出错\"}").toString());
			return;
		}
			
		JSONObject ou=JSONObject.parseObject(result);
		String fileName=ou.getString("filename");
		String url=ou.getString("url");
			
		jo.put("name", fileName);
		jo.put("voice_url", url);
		
		
		String localId=getPara("localId");
		
		jo.put("localId", localId);
		
		/**
		 * 获取store_name
		 */
		String storeName=getPara("store_name");
		jo.put("store_name", storeName);
		
		
		jo.put("member_id",member_id);	
		person.put("member_id", member_id);
		//member_name
		/**
		 * 获取会员姓名(前台提供)
		 */
		String name = getPara("name");
		person.put("name", name);
		/**
		 * 获取会员身份证号码(前台提供)
		 */
		String idcard = getPara("idcard");
		person.put("idcard", idcard);
		/**
		 * 获取性别
		 */
		Integer gender = getParaToInt("gender");//1为男，2为女
		person.put("gender", gender);
		
		/**
		 * 添加用户生日，身高，是否婚否
		 */
		String user_birthday=getPara("user_birthday");//yyyy-MM-dd
		
		 //yyyy-MM-dd
		 if(user_birthday!=null&&user_birthday.lastIndexOf("-")==6)
			 user_birthday=user_birthday.replaceFirst("-", "-0");
		
		Integer stature=getParaToInt("stature");//身高单位cm
		
		Integer bachelordom=getParaToInt("backelordom"); //1为单身，0为否
		
		person.put("user_birthday", user_birthday);
		
		person.put("stature", stature);
		
		person.put("bachelordom", bachelordom);
		
		//星座
		String constellation=JsonToBmob.verdictConstellation(user_birthday);
		
		if(constellation.equals("false")){
			renderJson(new JsonResult(JsonResult.STATE_FAIL, "{\"msg\":\"日期格式错误\"}").toString());
			return;
		}
		
		person.put("constellation", constellation);
		
		/**
		 * 修改审核状态为未审核
		 */
		person.put("verfiy_state", 2);
		
		jo.put("seller_name", name);
		
		jo.put("store_owner_card", idcard);
		
		jo.put("store_state", 2);//店铺状态1为默认开启，(0关闭，1开启，2审核中),
		
		String store_banner=getPara("store_banner");//店铺横幅
		jo.put("store_banner", store_banner);
		String store_zy=getPara("store_zy");//主营商品
		jo.put("store_zy",store_zy==null?"":store_zy);
		String description=getPara("description");//描述
		jo.put("description", description==null?"":description);
		
		String objectId=BmobAPI.getObjectIdById("identity_authentica", "member_id", member_id);
		
		result=update("identity_authentica",objectId, person.toJSONString());
		if(result.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,result).toString());
			return;
		}
		
		result=update("loveyou_store",membeerObject, jo.toJSONString());
		
		if(result.indexOf("At")==-1){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,result).toString());
			return;
		}
		
		/**
		 * 获取参数用户名 （前台提供）
		 */
		String username=getPara("username");
		
		String userObjectId=BmobAPI.getObjectIdById("loveyou_user", "username", username);
		
		result=update("loveyou_user",userObjectId, "{\"system_type\":2}"); //修改用户审核状态
		
		if (result.indexOf("At") == -1) {
			renderJson(new JsonResult(JsonResult.STATE_FAIL, result).toString());
			return;
		} else {
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS, result)
					.toString());
		}
		
	}
	
	/**
	 *  组合地址
	 * @param areaId
	 * @param cityId
	 * @return
	 */
	private String mergeAddressToAreaInfo(int areaId, int cityId) {

		String objectId = BmobAPI.getObjectIdById("loveyou_area", "area_id", areaId);
		String oneRow1 = findOne("loveyou_area", objectId);
		JSONObject jo = JSONObject.parseObject(oneRow1);
		String address = "";
		address = address + jo.getString("area_name") + "  ";

		String objectId1 = BmobAPI.getObjectIdById("loveyou_area", "area_id", cityId);
		String oneRow2 = findOne("loveyou_area", objectId1);
		jo = JSONObject.parseObject(oneRow2);
		address = address + jo.getString("area_name") + "  ";

		return address;
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
	private String sendPublishServiceRequest(Long fw_starttime, Long fw_endtime,
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
		
		String latitude=(String) paramMap.get("latitude");
		String longitude=(String) paramMap.get("longitude");
		System.out.println("latitude"+latitude);
		Integer latitude_real=Integer.parseInt(latitude.substring(0, latitude.indexOf(".")));
		
		if(latitude_real>=90){ 
			Integer latitude_real_new=90-latitude_real;
			latitude=latitude.replaceAll(latitude_real.toString()+".", latitude_real_new.toString()+".");
		}
		
		String du1="\"location\":{\"__type\":\"GeoPoint\",\"latitude\":"+latitude+",\"longitude\":"+longitude+"}";

		paramMap.remove("latitude");
		
		paramMap.remove("longitude");
		
		jsonStr = BmobAPI.mapToJSONStr(paramMap);
		
		jsonStr=jsonStr.substring(0, jsonStr.length()-1)+","+du1+"}";
		
		result = insert("shopnc_fw", jsonStr);
		return result;
	}
	
	public void awrToMp3Demo(){
		
//		UploadFile uf= getFile();
//		File f=uf.getFile();
		
		String fileName=getPara("FileName");
		
		String path=null;
		try {
			path = new File(getClass().getClassLoader().getResource("").toURI()).getPath();
		} catch (URISyntaxException e2) {
			// TODO Auto-generated catch block
			
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"获取本机路径错误\"}").toString());
			return;
		}
		
		path=path.replace("\\", "/");
		path=path.replace("/WEB-INF/classes", "/upload/love_you2/weixinVoice/");
		path.replace("\\", "/");
		
		String amrStr=path+fileName;
		
		String mp3Str=path.replaceFirst("weixinVoice", "voiceMp3")+"www.mp3";
		
		
		if(amrToMp3(amrStr, mp3Str)){
			renderText("yes");
		}else{
		
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"demo\"}").toString());
		}
	}
	/**
	 * 将amr的微信录音文件转换成MP3格式
	 * @param amrAbsolutePath
	 * amr 绝对路径
	 * @param mp3AbsolutePath
	 * mp3 绝度路径
	 * @return
	 * 转换成功还失败。。
	 */
	private boolean amrToMp3(String amrAbsolutePath,String mp3AbsolutePath){
		
			Process process = null;
	        boolean bool=false; 
	        Runtime run=null;
			try{
	              File mp3 = new File(mp3AbsolutePath);
	              if(mp3.exists()){
	                 mp3.delete();
	              }
	              run=Runtime.getRuntime();
	              if(File.separator.equals("/")){
	            	  process =run.exec("/var/tomcat/default/temp/jave-1/./ffmpeg -i " + amrAbsolutePath + " " + mp3AbsolutePath);
	              }else{
	            	  process =run.exec("C:/Users/moxiao/AppData/jave-1/ffmpeg -i " + amrAbsolutePath + " " + mp3AbsolutePath);
	              }
	             int b= process.waitFor();
	             process.destroy();	
	             run.freeMemory();
	             if(b==0){
	            	 	bool=true;
	            	 	return true;
	            	 }
	           }catch (Exception ex){
	        	   return bool;
	           }
	           
	       return bool;
	}
}

