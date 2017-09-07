package com.loveyou.webController.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loveyou.bmob.restapi.Bmob;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 
 * @author yyp
 *
 */
public class JsonToBmob extends Bmob {

	public static int j=2;
	public static String objectId;
	
	/**
	 * 读取本地Json文件的数据并写入到bmob云
	 */
	public void writeData(){

		String str=readFileByChars("C:\\Users\\Administrator\\Desktop\\goods.json");
		
		JSONObject jo=(JSONObject) JSONObject.parse(str);
		final JSONArray ja=jo.getJSONArray("RECORDS");
		//System.out.println(ja);
		System.out.println(ja.size());
//		int m=ja.size()%2;
//		int x=ja.size()/2;
//		final int a;
//		if(m>0){
//			a=x;
//		}else{
//			a=x;
//		}
//		System.out.println(m);
//		System.out.println("+++++++++++++++++++分线程开始");
//		Thread lo=new Thread(new Runnable(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//				for(int i=0;i<a;i++){
//					
//				String nu=	insert("loveyou_goods", ja.getJSONObject(i).toJSONString());
//				System.out.println("oooo1:"+nu);
//				}
//			}});
//		
//		
//		Thread lo1=new Thread(new Runnable(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//				for(int i=a;i<ja.size();i++){
//					
//					String un=insert("loveyou_goods", ja.getJSONObject(i).toJSONString());
//					System.out.println("oooo1:"+un);
//				}
//			}
//			
//		});
				
		for(int i=0;i<ja.size();i++){
			
			String un=insert("loveyou_goods", ja.getJSONObject(i).toJSONString());
			System.out.println("oooo1:"+i+un);
		}
		System.out.println("++++++++++");
		renderText("成功");
	}
	/**
	 * 读取Json格式子串并转成List
	 */
	
	public void writeDataByList(){
		
		ArrayList<String> str=readFileByCharsToList("C:\\Users\\Administrator\\Desktop\\oc.json");
		
		int i=0;
		for(i=0;i<str.size();i++){
		
			JSONObject jo=(JSONObject) JSONObject.parse(str.get(i));
			
			System.out.println("jo:"+jo.toJSONString());

		}
		renderText("success");
		
	}
	
	/**
	 * 上传到bmob云端数据库
	 */
	public void uploadImage(){
		
		if(j<61){
			j++;
			String url="";
			if(j>4){
			//List<Map<String, Object>> maps = super.uploadMoreBmobListMap2();
				List<Map<String,Object>> maps=super.uploadMoreBmobListMap();
				try{
				
					url =(String) maps.get(0).get("url");
				}catch(IndexOutOfBoundsException e){
					redirect("http://127.0.0.1:8011/writeData/uploadImage");
					
				}
				if(url!=null&&!"".equals(url))
					BmobAPI.update("loveyou_goods", objectId, "{\"goods_image\":\""+url+"\"}");
			}
			objectId =BmobAPI.getObjectIdById("loveyou_goods", "goods_id", j);
			
			String mn=BmobAPI.findOne("loveyou_goods", objectId);
			
			String gi="";
			
			if(mn.indexOf("objectId")!=-1){
			
				JSONObject jo=JSONObject.parseObject(mn);
				
				gi=jo.getString("goods_name");
				
				System.out.println(gi);
			}
			
			
			
			String um="<html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body><div id='container'><div id='header'><h3>";
					
			String mu="</h3></div><div id='count'><form action='/writeData/uploadImage' method='post' enctype='multipart/form-data'><p>image name: <input type='file' name='fname' /></p><input type='submit' value='Submit' /></form>";
			
			String ou="</div></div></body></html>";
			System.out.println(um+gi+mu+ou);
			renderHtml(um+gi+mu+ou);
			
		}
	}
	
	public void readrData(){
		
		String count=BmobAPI.findAll("loveyou_goods");
		
		String path="C:\\Users\\Administrator\\Desktop\\oc.json";
		boolean mn=readFileByCharm(path, count);
		if(mn)
		renderText("成功！");
		else
		renderText("失败");
	}
	
	/**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    private String readFileByChars(String fileName) {
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
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    private ArrayList<String> readFileByCharsToList(String fileName) {
        File file = new File(fileName);

        Reader reader = null;
        String row="";
        int b=0;
        int w=0;
        ArrayList<String> al=new ArrayList<String>();
        
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
                	if(w>10){
                		if(m=='{')
                			b=1;
                		if(m=='}')
                			b=2;
                	}
                	if(b==1){
                		row=row+new Character(m);
                	}
                	if(b==2) {
                		row=row+"}";
                		System.out.println("*****"+row+"****");
                		if(!row.equals("}")){
                		
                			al.add(row);
                		}
                		
                		row="";
                		b=0;
                	}
                	w++;
                }
              
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        
        return al;
        
    }
    
	/**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    private Boolean readFileByCharm(String fileName,String count) {
        
    	File file = new File(fileName);
    	if(!file.exists()){
    		try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	FileWriter fw=null;
        try {
            //System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
        	
        	fw=new FileWriter(file);
            fw.write(count);;
            fw.flush();
            
        }catch(IOException e){
        	e.printStackTrace();
        }
        
        return true;
    }
    
    /**
     *  http://takeme.iteye.com/blog/1683380
     * 
     * @param urlString
     * 		请求网址
     * @param filename
     * 		保存文件名称
     * @param savePath
     * 		下载到本地的路径
     * @throws Exception
     * 		抛弃异常
     */
    public static void download(String urlString, String filename,String savePath) throws IOException {  
        // 构造URL  
        URL url = new URL(urlString);  
        // 打开连接  
        URLConnection con = url.openConnection();  
        //设置请求超时为5s  
        con.setConnectTimeout(15*1000);  
        // 输入流  
        
        InputStream is = con.getInputStream();  
      
        // 1K的数据缓冲  
        byte[] bs = new byte[1024];  
        // 读取到的数据长度  
        int len;  
        // 输出的文件流  
       File sf=new File(savePath);  
       if(!sf.exists()){  
           sf.mkdirs();  
       }  
       OutputStream os = new FileOutputStream(sf.getPath()+"/"+filename);  
        // 开始读取  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
        // 完毕，关闭所有链接  
        os.close();  
        is.close();  
    }
    
    /**
     * 删除文件
     */
    public static boolean DeleteFolder(String sPath) {  
       boolean flag = false;  
        File file = new File(sPath);  
        // 判断目录或文件是否存在  
        if (!file.exists()) {  // 不存在返回 false  
            return flag;  
        } else {  
            // 判断是否为文件  
            if (file.isFile()) {  // 为文件时调用删除文件方法  
                return file.delete();  
            } else {  // 为目录时调用删除目录方法  
                return file.delete();  
            }  
        }  
    }  

    /**
     * 
     *  @param background
     * 	背景图片路径
     *  @param weixinHead
     * 	微信头像路径
     *  @param shareTwoDimension
     * 	用户二维码路径
     *  @param resultPath
     * 	合成后图片路径
     * 	@param weixinName
     * 	微信用户名
     *  @return
     * 	合成是否成功
     * 
     */
    public boolean  executeCompound(String background,String weixinHead,String shareTwoDimension,String resultPath,String weixinName){
		
		try {
            
                    InputStream back=new FileInputStream(background);
                    
                    InputStream weixinHeadStream=new FileInputStream(weixinHead);
                    
                    InputStream shareTwoDimensionStream=new FileInputStream(shareTwoDimension);

                    BufferedImage image=ImageIO.read(back);
                    BufferedImage image2=ImageIO.read(weixinHeadStream);
                    BufferedImage image3=ImageIO.read(shareTwoDimensionStream);
                    
                    Graphics g=image.getGraphics();
                    
                    g.drawImage(image2,image.getWidth()-640-300,image.getHeight()-640-900,400,400,null);
                    g.drawImage(image3,image.getWidth()-image3.getWidth()-240,image.getHeight()-image3.getHeight()-400,image3.getWidth()+10,image3.getHeight()+10,null);
                    
                    //设置颜色。
                    g.setColor(Color.BLACK);
                    
                    
                    //最后一个参数用来设置字体的大小
                    Font f = new Font("宋体",Font.BOLD,85);
                    
                    g.setFont(f);
             
                    //图片的为位置控制：    修改代码中的: g.drawImage(img,5,330,null);  5(x) 和 330(y)
                    //内容和字显示的位置：      g.drawString("默哀555555。。。。。。。",10,30);
                    //10,20 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
                    g.drawString(weixinName,560,760);
                    
                    g.dispose();

                    OutputStream outImage=new FileOutputStream(resultPath);
                    ImageIO.write(image, "jpg", outImage);
//                    JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(outImage);
                    
                    
                    back.close();
                    shareTwoDimensionStream.close();
                    weixinHeadStream.close();
                    outImage.close();
                    System.out.println("合成完成");
                    
                    return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return false;
	}

    /**
     * 获取本机Ip
     */
    
    	public static String getIpAddr() throws SocketException, UnknownHostException {
    			
    		String computerName="WIN-FRBPJUEDD6N";
    		for (InetAddress it : InetAddress.getAllByName(computerName)) {
    				
    			String ip=ipv4OrIpv6(it);
    			if(ip.indexOf("192.168.1.")!=-1){
    				
    				ip=ip.replace(computerName+"/", "");
    				return ip;
    			}
    				
    		}
    		return null;
    	}
    	
    	private static String ipv4OrIpv6(InetAddress ita) {
    		String[] itn = ita.toString().split("/");
    		String str = itn[1];
    		if (str.length() > 16) {
    			return "IPv6\t" + ita.toString();
    		}
    		return ita.toString();
    	}
    	
    	//动态生成验证码
    	
    	public static String getVerifyNum(){
    		
    		//生成6位
    		
//    		Double d=Math.random();
//    		
//    		int m=(int) (d*10);
//    		
//    		System.out.println(m);
    		
    		Random d=new Random();
    		
    		String result="";
    		int b=0;
    		
    		for(int i=0;i<6;i++){
    			
    			b=d.nextInt(10);
    			result+=b;
    		}

    		return result;
    	}
    	
    	/**
    	 * 判断用户星座
    	 */
    	
    	public static String verdictConstellation(String birthday){
    		
    		 final  int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22 };
    		 final  String[] constellationArr = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };

    		 String MM=null;
    		 int month=0;
    		 String dd=null;
    		 int day=0;
    		 try{
    		 
    			 //yyyy-MM-dd
	    		 if(birthday.lastIndexOf("-")==6)
	    			 birthday=birthday.replaceFirst("-", "-0");
	    		 
	    		 MM=birthday.substring(5, 7);
	    		
	    		 month=Integer.parseInt(MM);
	    		 
	    		 dd=birthday.substring(birthday.length()-2,birthday.length());
	
	    		 day=Integer.parseInt(dd);
    		 
    		 }catch(Exception e){
    			 return "false";
    		 }
    		return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    	}
    	
    	/**
    	 * 生成随机字符串
    	 */
    	public static String getRandomString(int length) { //length表示生成字符串的长度
    	    String base = "abcdefghijklmnopqrstuvwxyzabcdEFGHIJKLMNOPQRSTUVWXYZ0123456789";   
    	    Random random = new Random();   
    	    StringBuffer sb = new StringBuffer();   
    	    for (int i = 0; i < length; i++) {   
    	        int number = random.nextInt(base.length());   
    	        sb.append(base.charAt(number));   
    	    }   
    	    return sb.toString();   
    	 }   

}

