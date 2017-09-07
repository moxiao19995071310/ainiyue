package com.loveyou.webController.common;

import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author yyp
 *
 */
public class DrawUtil extends BmobAPI {

	/**
	 * 抽奖
	 */
	public void selectDraw(){
		
		Random rand = new Random();
		
		/**
		 * 一等奖中奖概率  1/50000
		 * 二等奖中奖概率  1/1000
		 * 三等奖中奖概率  1/500
		 */
		Integer select=rand.nextInt(100000);
		
			System.out.println(select);
			
			String reminder="";
			
		if(select>9999&&select<10001){
			reminder="那么恭喜你，肿了一等奖";
//			System.out.println("那么恭喜你，肿了一等奖");
		}else if(select>=10001&&select<10110){
			reminder="恭喜你中了2等奖";
//			System.out.println("恭喜你中了2等奖");
		}else if(select>=10110&&select<10310){
			reminder="恭喜你肿了3等奖";
//			System.out.println("恭喜你肿了3等奖");
		}else if(select>=10310&&select<50000){
			reminder="谢谢参与";
//			System.out.println("谢谢参与");
		}else if(select>=50000&&select<60000){
			reminder="不要灰心";
//			System.out.println("不要灰心");
		}else if(select>=60000&&select<70000){
			reminder="祝您好运";
//			System.out.println("祝您好运");
		}else if(select>=70000&&select<85000){
			reminder="神马也没有";
//			System.out.println("神马也没有");
		}else if(select>=85000&&select<100000){
			reminder="运气先攒着";
//			System.out.println("运气先攒着");
		}else if(select<=10000){
			reminder="你的运气可能都花在遇见另一半上";
//			System.out.println("你的运气可能都花在遇见另一半上");
		}else{
			reminder="这运气都不中奖，怎么可能";
		}
		
		JSONObject jo=new JSONObject();
		jo.put("draw", select);
		jo.put("reminder", reminder);
		
		renderJson(new JsonResult(JsonResult.STATE_SUCCESS,jo.toJSONString()).toString());
		return;
		
	}
	
	/**
	 * 开始抽奖创建奖品，并且打乱100个人的顺序
	 */
	public void startLottery(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer memberId=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		Integer skip=BmobAPI.getIntegerValueFromJSONObject(jo, "skip");
		
		if(memberId==null||memberId<16||skip==null||skip<0){
			renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您的信息有误，不能启动抽奖\"}").toString());
			return;
		}
		//封装参数
		
		String ObjectId=getObjectIdById("loveyou_member", "member_id", memberId);
		
		String memberInfo=findOne("loveyou_member", ObjectId);
		
		JSONObject memberI=JSONObject.parseObject(memberInfo);
		
		String inviteNum=memberI.getString("inviteNum");
		
		String result=find("loveyou_member", "{\"inviteNum\":\""+inviteNum+"\"}", skip, 100);
		
		if(result.indexOf("results\":[{")!=-1){
			
			JSONObject resu=(JSONObject) JSONObject.parse(result);
			
			JSONArray ja=resu.getJSONArray("results");
			
			if(ja.size()!=100){
				
				renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"你邀请的人数不足100人，不可以进行抽奖活动\"}").toString());
				return;
			}
			
			//新建一个数组对象，并打乱这个数组的1-100个数的位置，使用了random.netInt()方法
			int sequence[]=new int[100];
			
			int i=0;
			do{
				sequence[i]=getNo_repeat(sequence);
				i++;
			}while(sequence[99]==0);
			
			//随机生成中奖号码，并判断中奖人
			Random rand=new Random();
			
			int fisrtPrize=rand.nextInt(100)+1;
			
			int secondPrize=rand.nextInt(100)+1;
			
			while(fisrtPrize==secondPrize){
				secondPrize=rand.nextInt(100)+1;
			}
			int thirdPrize=rand.nextInt(100)+1;
			
			while(fisrtPrize==thirdPrize||secondPrize==thirdPrize)
				thirdPrize=rand.nextInt(100)+1;
			
			for(int j=0;j<ja.size();j++){
				
				JSONObject jd=ja.getJSONObject(j);
				
				String raffleMember=jd.getString("objectId");
				
				
				jd.clear();
				jd.put("number", sequence[j]);
				
				if(sequence[j]==fisrtPrize)
					jd.put("lottery", 1);
				else if(sequence[j]==secondPrize)
					jd.put("lottery", 2);
				else if(sequence[j]==thirdPrize)
					jd.put("lottery", 3);
				
				
				String updateResult=update("loveyou_member", raffleMember, jd.toJSONString());
				
				if(updateResult.indexOf("At")==-1){
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"很抱歉，此次抽奖失败，这是系统后台问题，请联系管理员优化算法\"}").toString());
					return;
				}
			}
			
			renderJson(new JsonResult(JsonResult.STATE_SUCCESS,"{\"msg\":\"抽奖完成,奖品已生成，可以派发被邀请信息，已可以抽奖\"}").toString());
			return;
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"你暂时还不能抽奖，你邀请的人数暂未达到100人\"}").toString());
		
	}
	
	//查看自己是否中奖
	
	public void checkLottery(){
		
		JSONObject jo=BmobAPI.getJSONObjectFromRequest(this.getRequest());
		
		Integer memberId=BmobAPI.getIntegerValueFromJSONObject(jo, "member_id");
		
		if(memberId!=null&&memberId>15){
			
			String objectId=getObjectIdById("loveyou_member","member_id",memberId);
			
			if(objectId!=null){
				
				String lotteryInfo=findOne("loveyou_member", objectId);
				
				JSONObject lotteryI=JSONObject.parseObject(lotteryInfo);
				
				Integer number=lotteryI.getInteger("number");
				
				Integer lottery=lotteryI.getInteger("lottery");
				
				if(number==null||number<1){
					renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"因为抽奖人的人数不足100人所以暂时不能开奖，请浏览其他\"}").toString());
					return;
				}
				
				JSONObject jr=new JSONObject();
				
				if(lottery==null||lottery>3&&lottery<0){
					jr.put("msg", "您的运气可能都花在遇见另一半上");
					jr.put("lottery", 0);
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,jr.toJSONString()).toString());
					return;
				
				}
				//如果中了一等奖
				if(lottery.equals(1)){
					
					jr.put("msg", "恭喜中了一等奖");
					jr.put("lottery", 1);
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,jr.toJSONString()).toString());
					return;
					
				}else if(lottery.equals(2)){ //中了2等奖
					jr.put("msg", "恭喜中了二等奖");
					jr.put("lottery", 2);
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,jr.toJSONString()).toString());
					return;

				}else if(lottery.equals(3)){ //中了三等奖
					jr.put("msg", "恭喜中了三等奖");
					jr.put("lottery", 1);
					renderJson(new JsonResult(JsonResult.STATE_SUCCESS,jr.toJSONString()).toString());
					return;

				}
			}
			
		}
		
		renderJson(new JsonResult(JsonResult.STATE_FAIL,"{\"msg\":\"您的账号信息有误，不能查询到你的账户，请检查\"}").toString());
		
		
	}
	/**
	 * 获取一个与此数组中任意一数都不相同的数
	 */
	private int getNo_repeat(int[] b){
			
		Random rand = new Random();
		int i=0;
		int x=b.length;
		int num=rand.nextInt(100)+1;
		for(i=0;i<x;i++){
			if(b[i]==0){
				break;
			}
			if(num==b[i]){
				num=rand.nextInt(100)+1;
				i=0;
			}
			
		}
			
		return num;
	}
}
