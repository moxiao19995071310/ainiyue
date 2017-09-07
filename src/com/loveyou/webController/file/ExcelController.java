package com.loveyou.webController.file;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.ExcelUtil;

/**
 * 导入导出Excel控制器
 * 
 * @ClassName: ExcelController
 * 
 * @Description: TODO(这个类的作用是：导入导出Excel)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月30日 下午4:51:32
 *
 * 
 */
public class ExcelController extends Bmob {

	/**
	 * 导入Excel数据
	 */
	public void importExcel() {
		try {
			InputStream in = null;
			in = new FileInputStream(getFile().getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
