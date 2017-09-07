package com.loveyou.webController.login;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.Token;
import com.baomidou.kisso.web.interceptor.KissoAbstractInterceptor;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * 登录拦截器
 * 
 * @ClassName: LoginInterceptor
 * 
 * @Description: TODO(这个类的作用是：登录权限拦截)
 * 
 * @author Hu Xiaobo
 * 
 * @date 2016年6月22日 上午10:45:11
 *
 * 
 */
public class LoginInterceptor extends KissoAbstractInterceptor implements Interceptor {

	private static final Logger logger = Logger.getLogger("SSOJfinalInterceptor");

	@Override
	public void intercept(Invocation inv) {
		System.out.println("进入拦截器");
		/**
		 * 正常执行
		 */
		HttpServletRequest request = inv.getController().getRequest();
		HttpServletResponse response = inv.getController().getResponse();
		Token token = SSOHelper.getToken(request);
		// 登录请求直接放行
		if (request.getRequestURL().indexOf("login") != -1) {
			inv.invoke();
		}
		if (token == null) {
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				/*
				 * Handler 处理 AJAX 请求
				 */
				getHandlerInterceptor().preTokenIsNullAjax(request, response);
			} else if ("APP".equals(request.getHeader("PLATFORM"))) {
				/*
				 * Handler 处理 APP接口调用 请求
				 * 没有修改kisso核心代码，直接使用Ajax的认证判断方式，如果未认证，返回401状态码
				 */
				getHandlerInterceptor().preTokenIsNullAjax(request, response);
				logger.info("request from APP invoke");
			} else {
				try {
					logger.fine("logout. request url:" + request.getRequestURL());
					SSOHelper.clearRedirectLogin(request, response);
					response.sendRedirect("/login.html");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			/*
			 * 正常请求，request 设置 token 减少二次解密
			 */
			request.setAttribute(SSOConfig.SSO_TOKEN_ATTR, token);
			inv.invoke();
		}
	}

}
