package com.loveyou.webController.common;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.loveyou.bmob.restapi.Bmob;

/** 
* @ClassName: BmobInterceptor 
* @Description: (这个类的作用是:每次请求前先注册bmob ) 
* @author  ggj
* @date 2016-6-2 上午5:48:08 
*  
*/
public class BmobInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		
		
		
		Bmob.initBmob("887295b3ab21109e27b6e5bdc6740b9d",
				"717c74a321efa0e5617030c1e7e008ba");
		
		//inv.getController().getResponse().addHeader("Access-Control-Allow-Origin", "*");
		HttpServletResponse resp = inv.getController().getResponse();
		ServletRequest request = inv.getController().getRequest();
		// "*"存在风险，建议指定可信任的域名来接收响应信息，如"http://www.sosoapi.com"

//		request.setAttribute("Accept", "application/json");
		resp.addHeader("Access-Control-Allow-Origin", "*");
		// 如果存在自定义的header参数，需要在此处添加，逗号分隔
		resp.addHeader(
				"Access-Control-Allow-Headers",
				"Origin, No-Cache, X-Requested-With, "
						+ "If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, "
						+ "Content-Type, X-E4M-With");
		resp.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT ");
		
		inv.invoke();
	}
	/*public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse resp = (HttpServletResponse)response;
		//"*"存在风险，建议指定可信任的域名来接收响应信息，如"http://www.sosoapi.com"
		resp.addHeader("Access-Control-Allow-Origin", "*");
		//如果存在自定义的header参数，需要在此处添加，逗号分隔
		resp.addHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, "
				+ "If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, "
				+ "Content-Type, X-E4M-With");
		resp.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");  
		
		chain.doFilter(request, response);
	}*/
}
