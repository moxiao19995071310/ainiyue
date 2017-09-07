
package com.loveyou.webController.login;

import com.baomidou.kisso.SSOHelper;
import com.jfinal.core.Controller;

/**
 * 退出登录控制器
 * 
 * @ClassName: LogoutController
 * 
 * @Description: TODO(这个类的作用是：)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月22日 上午10:47:25
 *
 * 
 */
public class LogoutController extends Controller {

	/**
	 * 退出登录
	 */
	public void logout() {
		SSOHelper.clearLogin(getRequest(), getResponse());
		redirect("login");
	}
}
