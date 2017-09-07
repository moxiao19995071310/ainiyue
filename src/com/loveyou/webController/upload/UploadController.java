package com.loveyou.webController.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.loveyou.bmob.restapi.Bmob;

/**
 * @ClassName: UploadController
 * @Description: (这个类的作用是:文件上传)
 * @author ggj
 * @date 2016-6-9 上午11:06:59
 * 
 */
public class UploadController extends Controller {

	/**
	 * 文件存放路径
	 */
	private String pathType;

	/**
	 * 文件上传处理1
	 */
	public void upload() {
		Integer maxPostSize = 10485760;
		// PathKit.getWebRootPath()+"/ggj2" jfinal 2.2 已经默认啦
		// List<UploadFile> files =this.getFiles(PathKit.getWebRootPath()+"/ggj2", maxPostSize,"UTF-8");

		List<UploadFile> files = this.getFiles("/"+"loveyou", maxPostSize);
		// 线程安全适合多线程(用可变字符串装)
		StringBuffer sb = new StringBuffer();
		List<UploadEntity> uploadEntities = new ArrayList<UploadEntity>();
		long size = 0L;
		String uploadEntityJson = "";
		String fileName = "";
		String originalFileName = "";
		String path = "";
		String relativelypath = "";
		int i = files.size();
		// 用集合来装文件上传文件
		for (UploadFile upfile : files) {
			size = upfile.getFile().length();
			// 原文件名
			originalFileName = upfile.getOriginalFileName();
			// 修改文件名 TODO

			// 上传后文件名
			fileName = upfile.getFileName();
			// 存放路径
			path = upfile.getFile().getAbsolutePath();
			sb.append("文件长度:").append(size);
			sb.append("\n绝对路径:").append(upfile.getFile().getAbsolutePath());
			// 获取相对路径
			String filepath = upfile.getFile().getAbsolutePath();
			relativelypath = filepath.substring(filepath.lastIndexOf("upload") - 1);
			// uploadFile.getSaveDirectory() 方法过时啦
			// sb.append("\nsavedir 保存目录:").append(uploadFile.getSaveDirectory());
			sb.append("\nsavedir 保存目录:").append(upfile.getUploadPath());
			sb.append("\n文件名称getFileName:").append(upfile.getFileName());
			sb.append("\n文件名称getOriginalFileName:").append(
					upfile.getOriginalFileName());
			sb.append("\n文件名称getParameterName:").append(
					upfile.getParameterName());
			UploadEntity uploadEntity = new UploadEntity(i-- + "", fileName,
					originalFileName, path,relativelypath);
			uploadEntities.add(uploadEntity);
		}
		uploadEntityJson = JSON.toJSONString(uploadEntities);
		renderJson(uploadEntityJson);
		// renderText(sb.toString());
	}
	public UploadEntity upload1() {
		Integer maxPostSize = 10485760;
		
		List<UploadFile> files = this.getFiles("/"+"loveyou", maxPostSize);
		List<UploadEntity> uploadEntities = new ArrayList<UploadEntity>();
		long size = 0L;
		String uploadEntityJson = "";
		String fileName = "";
		String originalFileName = "";
		String path = "";
		String relativelypath = "";
		UploadEntity uploadEntity=null;
		int i = files.size();
		// 用集合来装文件上传文件
		for (UploadFile upfile : files) {
			size = upfile.getFile().length();
			// 原文件名
			originalFileName = upfile.getOriginalFileName();
			// 修改文件名 TODO
			
			// 上传后文件名
			fileName = upfile.getFileName();
			// 存放路径
			path = upfile.getFile().getAbsolutePath();
			// 获取相对路径
			String filepath = upfile.getFile().getAbsolutePath();
			relativelypath = filepath.substring(filepath.lastIndexOf("upload") - 1);
			
			  uploadEntity = new UploadEntity(i-- + "", fileName,
					originalFileName, path,relativelypath);
			uploadEntities.add(uploadEntity);
		}
		uploadEntityJson = JSON.toJSONString(uploadEntities);
		renderJson(uploadEntityJson);
		// renderText(sb.toString());
		return uploadEntity;
	}
 
	public  List<UploadEntity> upload2() {
    	Integer  maxPostSize=10485760;
    	//指定路径
    	List<UploadFile> files = this.getFiles("/"+"LOVE_YOU", maxPostSize, "UTF-8");
    	//创建一个文件上传对象
    	//new UploadEntity(ids, filename, originalfilename, path);
    	
    	
    	//线程安全适合多线程(用可变字符串装)
    	StringBuffer sb = new StringBuffer();
    	List<UploadEntity> uploadEntities =new ArrayList<UploadEntity>();
    	long size=0L;
    	String uploadEntityJson="";
    	String fileName="";
    	String originalFileName="";
    	String path="";
    	String relativelypath = "";
    	int i=files.size();
    	//用集合来装文件上传文件
    	for (UploadFile uploadFile : files) {
    		size = uploadFile.getFile().length();
    		uploadEntityJson=uploadFile.getParameterName();
    		//文件名
    		fileName=uploadFile.getFileName();
    		//原文件名 
    		originalFileName=uploadFile.getOriginalFileName();
    		//存放路径 
    		path=uploadFile.getFile().getAbsolutePath();
    		sb.append("文件长度:").append(size);
    		sb.append("\n绝对路径:").append(uploadFile.getFile().getAbsolutePath().replace("\\", "/"));
    		// 获取相对路径
    		String filepath = uploadFile.getFile().getAbsolutePath().replace("\\", "/");
    		relativelypath = filepath.substring(filepath.lastIndexOf("upload") - 1);
    		/* sb.append("\nsavedir 保存目录:").append(uploadFile.getSaveDirectory());*/
    		sb.append("\nsavedir 保存目录:").append(uploadFile.getUploadPath());
    		sb.append("\n文件名称getFileName:").append( uploadFile.getFileName());
    		sb.append("\n文件名称getOriginalFileName:").append(uploadFile.getOriginalFileName());
    		sb.append("\n文件名称getParameterName:").append(uploadFile.getParameterName());
    		UploadEntity   uploadEntity=  new UploadEntity(i--+"", fileName, originalFileName, path,relativelypath,uploadEntityJson);  
    		uploadEntities.add(uploadEntity);
    	}
		return uploadEntities;
    }

	/**
	 *
	 *@author  ggj
	 *@Description: (本方法的作用是:bmbo 文件上传 ) 
	 *@date 2016-6-9 下午8:10:26
	 */
	public void uploadBmob() {
		 //文件绝对路径
		System.out.println("文件绝对路径"+upload1().getPath());
		try {
			renderJson(Bmob.uploadFile2(upload1().getPath()));
		} catch (Exception e) {
			renderJson(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
