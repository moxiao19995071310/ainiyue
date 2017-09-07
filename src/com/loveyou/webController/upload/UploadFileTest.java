package com.loveyou.webController.upload;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jfinal.aop.Before;
import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobInterceptor;

/**
 * 上传测试
 * 
 * @ClassName: UploadFileTest
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月15日 下午2:01:08
 *
 * 
 */
@Before(BmobInterceptor.class)
public class UploadFileTest extends Bmob {

	/**
	 * 单个文件上传测试
	 */
	public void uploadSingleFile() {
		System.out.println("==========================================");
		List<UploadEntity> uploadEntities = super.upload1();
		for (UploadEntity uploadEntity : uploadEntities) {
			System.out.println("ids:\t" + uploadEntity.getIds());
			System.out.println("filename:\t" + uploadEntity.getFilename());
			System.out.println("originalfilename:\t" + uploadEntity.getOriginalfilename());
			System.out.println("path:\t" + uploadEntity.getPath());
			System.out.println("relativelypath:\t" + uploadEntity.getRelativelypath());
			System.out.println("------------------------------------------------------");
		}
	}

	public void uploadSingleFile2() {
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
		for (Map<String, Object> map : maps) {
			for (Entry<String, Object> entry : map.entrySet()) {
				System.out.println(entry.getKey() + "====" + entry.getValue());
			}
		}
		renderJson(maps);
	}

	public void uploadSingleFile3() {
		List<Map<String, Object>> maps = super.uploadMoreBmobListMap();
		Map<String, Object> map = maps.get(0);
		String url = (String) map.get("url");
		String fileName = (String) map.get("filename");
		System.out.println("url:" + url);
		System.out.println("fileName:" + fileName);
		System.out.println("======================================================================");
		String username = getPara("username");
		String password = getPara("username");
		System.out.println("username:" + username);
		System.out.println("password:" + password);

	}

}
