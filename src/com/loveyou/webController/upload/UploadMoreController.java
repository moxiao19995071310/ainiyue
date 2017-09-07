package com.loveyou.webController.upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobInterceptor;

/**
 * @ClassName: UploadMoreController
 * @Description: (这个类的作用是: bmob 多图文件上传)
 * @author ggj
 * @date 2016-6-9 下午10:18:17 参考资料:http://www.oschina.net/question/1475616_224274
 */
@Before(BmobInterceptor.class)
public class UploadMoreController extends Bmob {
	public void index() {
		renderText("Hello JFinal World.");
	}

	/**
	 * 单图上传
	 */
	public void upload() {
		UploadFile f = this.getFile();
		long size = f.getFile().length();
		StringBuffer sb = new StringBuffer();
		sb.append("文件长度:").append(size);
		sb.append("\n绝对路径:").append(f.getFile().getAbsolutePath());
		sb.append("\nsavedir 保存目录:").append(f.getUploadPath());
		/* sb.append("\nsavedir 保存目录:").append(f.getSaveDirectory()); */
		sb.append("\n文件名称getFileName:").append(f.getFileName());
		sb.append("\n文件名称getOriginalFileName:").append(f.getOriginalFileName());
		sb.append("\n文件名称:").append(f.getParameterName());
		f.getFileName();
		renderText(sb.toString());

	}

	/**
	 *
	 * @author ggj
	 * @Description: (本方法的作用是:多图上传测试 不要与父类同名upload1_  )
	 * @date 2016-6-9 下午8:43:06
	 */
	public List<UploadEntity> upload1_() {
		Integer maxPostSize = 10485760;
		// 指定路径
		List<UploadFile> files = this.getFiles("//LOVE_YOU", maxPostSize, "UTF-8");
		// 创建一个文件上传对象
		// new UploadEntity(ids, filename, originalfilename, path);

		// 线程安全适合多线程(用可变字符串装)
		StringBuffer sb = new StringBuffer();
		//List<UploadEntity> uploadEntities = new ArrayList<UploadEntity>();
		//调用父类的文件上传方法
		List<UploadEntity> uploadEntities = super.upload1();
		long size = 0L;
		String uploadEntityJson = "";
		String fileName = "";
		String originalFileName = "";
		String path = "";
		String relativelypath = "";
		int i = files.size();
		// 用集合来装文件上传文件
		for (UploadFile uploadFile : files) {
			size = uploadFile.getFile().length();
			// 文件名
			fileName = uploadFile.getFileName();
			// 原文件名
			originalFileName = uploadFile.getOriginalFileName();
			// 存放路径
			path = uploadFile.getFile().getAbsolutePath();
			sb.append("文件长度:").append(size);
			sb.append("\n绝对路径:").append(uploadFile.getFile().getAbsolutePath());
			// 获取相对路径
			String filepath = uploadFile.getFile().getAbsolutePath();
			relativelypath = filepath.substring(filepath.lastIndexOf("upload") - 1);
			/*
			 * sb.append("\nsavedir 保存目录:"
			 * ).append(uploadFile.getSaveDirectory());
			 */
			sb.append("\nsavedir 保存目录:").append(uploadFile.getUploadPath());
			sb.append("\n文件名称getFileName:").append(uploadFile.getFileName());
			sb.append("\n文件名称getOriginalFileName:").append(uploadFile.getOriginalFileName());
			sb.append("\n文件名称getParameterName:").append(uploadFile.getParameterName());
			UploadEntity uploadEntity = new UploadEntity(i-- + "", fileName, originalFileName, path, relativelypath);
			uploadEntities.add(uploadEntity);
		}
		return uploadEntities;
	}

	/**
	 *
	 * @author ggj
	 * @Description: (本方法的作用是:[bmob云 多图上传都到云] 注意List Map 定义时就先初始化免得报null )
	 * @date 2016-6-9 下午9:51:37
	 */
	public void uploadMoreBmob() {
		List<UploadEntity> uploadEntities = super.upload1();
		System.out.println("获取到的上传文件::" + uploadEntities);
		String path = "";
		List<Map<String, Object>> listMap = new ArrayList<>();
		for (Iterator iterator = uploadEntities.iterator(); iterator.hasNext();) {
			UploadEntity uploadEntity = (UploadEntity) iterator.next();
			path = uploadEntity.getPath();
			// 循环传送
			Bmob.uploadFile2(path);
			// 字符串转json 对象 传统方法1
			/*
			 * JSONObject uploadjson =
			 * JSONObject.parseObject(Bmob.uploadFile2(path)) ; // list 或者Map
			 * 或者数组 []来装
			 * fastjson序列化::http://www.cnblogs.com/goody9807/p/4244862.html
			 * Map<String, Object> map= new HashMap<String, Object>();
			 * map.put("url", uploadjson.get("url")); map.put("filename",
			 * uploadjson.get("filename")); listMap.add(map);
			 */

			/**
			 * 字符串转json 对象 升级方法2
			 */
			Map<String, Object> map = new HashMap<String, Object>();
			map = JSONObject.parseObject(Bmob.uploadFile2(path));
			// list 或者Map 或者数组 []来装
			// fastjson序列化::http://www.cnblogs.com/goody9807/p/4244862.html
			listMap.add(map);
			// TODO 字符串转json 对象 升级方法3待实现
			/*
			 * System.out.println("url==="+ uploadjson.get("url")); Map<String,
			 * Object> map=JSON.parseObject(Bmob.uploadFile2(path),Map.class);
			 * System.out.println("map---->"+map); listMap=
			 * JSON.parseObject(Bmob.uploadFile2(path), new
			 * TypeReference<List<Map<String,Object>>>(){});
			 * System.out.println("listMap--->"+ listMap);
			 */
		}
		// TODO ...........
		renderJson(listMap);

	}

	/**
	 *
	 * @author ggj
	 * @Description: (本方法的作用是: [bmob云 多图上传都到云 增加相对路径返回参数!!] )
	 * @date 2016-6-9 下午10:07:58
	 */
	public void uploadMoreBmob2() {
		List<UploadEntity> uploadEntities = super.upload1();
		if (null != uploadEntities) {

			System.out.println("获取到的上传文件::" + uploadEntities);
			String path = "";
			List<Map<String, Object>> listMap = new ArrayList<>();
			for (Iterator iterator = uploadEntities.iterator(); iterator
					.hasNext();) {
				UploadEntity uploadEntity = (UploadEntity) iterator.next();
				path = uploadEntity.getPath();

				Map<String, Object> map = new HashMap<String, Object>();
				// BMob 传送文件 图片文件不能有()括号
				map = JSONObject.parseObject(Bmob.uploadFile2(path));
				map.put("relativelypath", uploadEntity.getRelativelypath());
				listMap.add(map);
			}

			renderJson(listMap);
			// renderJson(uploadMoreBmobListMap());
		}
		renderText("你没有上传文件");

	}

}