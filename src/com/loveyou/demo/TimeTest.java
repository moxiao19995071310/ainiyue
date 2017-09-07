package com.loveyou.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** 
* @ClassName: TimeTest 
* @Description:  (这个类的作用是: 时间戳24小时时间格式) 
* @author  ggj
* @date 2016-6-9 下午10:29:29 
* Java8中时间日期库的20个常用使用示例  http://www.open-open.com/code/view/1454504082323
* 
* java 日期格式时间24小时制         
*  http://blog.csdn.net/flfna/article/details/6457327
*  
*  JAVA获取时间戳，哪个更快    http://tangmingjie2009.iteye.com/blog/1543166
*  java 时间戳与日期字符串相互转换  http://huangqiqing123.iteye.com/blog/2163722  $$$$$$$
*  Java时间戳转化为今天、昨天、明天（字符串格式）
*  http://www.open-open.com/code/view/1435301895825
*  
*  java使用new Date()和System.currentTimeMillis()获取当前时间戳
*  http://www.cnblogs.com/wuchen/archive/2012/06/30/2570746.html
*  DateUtils.java 日期处理相关工具类  http://www.open-open.com/code/view/1430990010601
*  java 日期时间工具类
*  http://www.open-open.com/code/view/1420038162218 
*/
public class TimeTest {

	public static void testSystem(long times) {// use 188
		for (int i = 0; i < times; i++) {
			long currentTime = System.currentTimeMillis();
		}
	}

	public static void testCalander(long times) {// use 6299
		for (int i = 0; i < times; i++) {
			long currentTime = Calendar.getInstance().getTimeInMillis();
		}
	}

	public static void testDate(long times) {
		for (int i = 0; i < times; i++) {
			long currentTime = new Date().getTime();
		}

	}

	private static long _TEN_THOUSAND = 10000;
/*
	public static void main(String[] args) {
		long times = 1000 * _TEN_THOUSAND;
		long t1 = System.currentTimeMillis();
		testSystem(times);
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);
		testCalander(times);
		long t3 = System.currentTimeMillis();
		System.out.println(t3 - t2);
		testDate(times);
		long t4 = System.currentTimeMillis();
		System.out.println(t4 - t3);
	}*/
	
	
	
	public static void main(String[] args) {
		//import java.text.ParseException;
		//import java.text.SimpleDateFormat;
		//import java.util.Date;
    	 /*try {
			  String time = "2011/07/29 14:50:11";
			//获取系统时间
			System.out.println("time==="+time);
			 SimpleDateFormat  time2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
			 System.out.println("time2=="+time2.format(time));
			Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(time);
			System.out.println("date::"+date.getTime());
			long unixTimestamp = date.getTime()/1000;
			System.out.println(unixTimestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		
		 System.out.println("data==>"+new Date());
		Long time=new Date().getTime();
	    // 24 小时设置  
		Long time2=System.currentTimeMillis();
		Long time3=System.currentTimeMillis()/1000;
		 System.out.println("获取当前 时间戳方法1="+time);
		System.out.println("获取当前 时间戳方法2::"+time2);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		//格式里的时如果用hh表示用12小时制，HH表示用24小时制。MM必须是大写!
		//http://blog.csdn.net/flfna/article/details/6457327
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
		SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy年MM月dd日_HH_mm_ss");
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		String date = sdf.format(new Date(time3*1000));
		String date2 = sdf2.format(new Date(time3*1000));
		String date3 = sdf3.format(new Date(time3*1000));
		String date4 = sdf4.format(new Date(time3*1000));
		String date5 = sdf5.format(new Date(time3*1000));
		System.out.println("转回时间12制=="+date);
		System.out.println("转回时间24制=="+date2);
		System.out.println("转回时间24制=="+date3);
		System.out.println("转回时间24制=="+date4);
		System.out.println("转回时间24制5=="+date5);
		
		// 示例2 如何在Java 8中获取当前的年月日
		//http://www.open-open.com/code/view/1454504082323
		/*
		LocalDate today = LocalDate.now(); 
		int year = today.getYear(); 
		int month = today.getMonthValue(); 
		int day = today.getDayOfMonth(); 
		System.out.printf("Year : %d Month : %d day : %d \t %n", year, month, day); */

		
	}

}
