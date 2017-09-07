package com.loveyou.webController.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * @ClassName: MakeOrderNum 
 * @CreateTime 2015年11月17日16:28:47
 * @author : mayi [ggj 引用]
 * @Description: 订单号生成工具，生成非重复订单号，理论上限1毫秒1000个，可扩展 
 * 【订单号生成工具（实现一）基于对象锁 -】 http://blog.csdn.net/lqclh502/article/details/48416777 来源
 * 【java 过滤器filter防sql注入】 http://blog.csdn.net/lqclh502/article/details/47775809 来源
 */  
public class MakeOrderNumUtil {  
    /** 
     * 锁对象，可以为任意对象 
     */  
    private static Object lockObj = "lockerOrder";  
    /** 
     * 订单号生成计数器 
     */  
    private static long orderNumCount = 0L;  
    /** 
     * 每毫秒生成订单号数量最大值 
     */  
    private int maxPerMSECSize=1000;  
    /** 
     * 生成非重复订单号，理论上限1毫秒1000个，可扩展 
     * @param tname 测试用 
     */  
    
    /**
     *@return
     *@author  ggj
     *@Description: TODO(本方法的作用是:对外提供生成订单的方法 ) 
     *@date 2016-6-4 上午10:32:12
     */
    public  String  makeOrderNum() {  
    	String orderNum = "";  
    	try {  
    		// 最终生成的订单号  
    		synchronized (lockObj) {  
    			// 取系统当前时间作为订单号变量前半部分，精确到毫秒  
    			long nowLong = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));  
    			// 计数器到最大值归零，可扩展更大，目前1毫秒处理峰值1000个，1秒100万  
    			if (orderNumCount > maxPerMSECSize) {  
    				orderNumCount = 0L;  
    			}  
    			//组装订单号  
    			String countStr=maxPerMSECSize +orderNumCount+"";  
    			orderNum=nowLong+countStr.substring(1);   
    			orderNumCount++;  
    		}  
    	} catch (Exception e) {  
    		e.printStackTrace();  
    	}
		return orderNum;  
    }  
    
    /** 
     * 生成非重复订单号，理论上限1毫秒1000个，可扩展 
     * @param tname 测试用 
     */  
    private void makeOrderNum(String tname) {  
        try {  
            // 最终生成的订单号  
          String orderNum = "";  
            synchronized (lockObj) {  
                // 取系统当前时间作为订单号变量前半部分，精确到毫秒  
                long nowLong = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));  
                // 计数器到最大值归零，可扩展更大，目前1毫秒处理峰值1000个，1秒100万  
                if (orderNumCount > maxPerMSECSize) {  
                    orderNumCount = 0L;  
                }  
                //组装订单号  
                String countStr=maxPerMSECSize +orderNumCount+"";  
                 orderNum=nowLong+countStr.substring(1); 
                orderNumCount++;  
                System.out.println(orderNum + "--" + Thread.currentThread().getName() + "::" + tname );  
                // Thread.sleep(1000);  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /**
     * @param args
     *@author  ggj
     *@Description: TODO(本方法的作用是: 测试多线程调用订单号生成工具 ) 
     *@date 2016-6-4 上午10:00:44
     */
	public static void main(String[] args) {

		try {
			for (int i = 0; i < 200; i++) {
				Thread t1 = new Thread(new Runnable() {
					public void run() {
						MakeOrderNumUtil makeOrder = new MakeOrderNumUtil();
						makeOrder.makeOrderNum("a");
					}
				}, "at" + i);
				t1.start();

				Thread t2 = new Thread(new Runnable() {
					public void run() {
						MakeOrderNumUtil makeOrder = new MakeOrderNumUtil();
						makeOrder.makeOrderNum("b");
					}
				}, "bt" + i);
				t2.start();
			}
			//测试
			/*MakeOrderNumUtil makeOrder = new MakeOrderNumUtil();
			System.out.println("当前的订单号为="+makeOrder. makeOrderNum());*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}