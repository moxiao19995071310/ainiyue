package com.loveyou.webController.common;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;

import com.alibaba.fastjson.JSONObject;


public class ShiroRealm extends AuthorizingRealm {

	/**
	 * 授权查询回调函数
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {

		String objectId= (String) arg0.fromRealm(getName()).iterator().next();
		
		if(objectId!=null&&!objectId.equals("")){
			
			String userInfo=BmobAPI.findOne("loveyou_user", objectId);
			
			JSONObject jo=JSONObject.parseObject(userInfo);
		
			SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
				
			Integer system_type=jo.getInteger("system_type");
			
			info.addRole(system_type.toString());
				
			return info;
		}
		
		
		return null;
	}

	/**
	 *认证回调函数 
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {

		UsernamePasswordToken token=(UsernamePasswordToken) arg0;
		
		String password =String.valueOf(token.getPassword());
		
		//bmob端的user用户，
		
		String objectId =BmobAPI.getObjectIdById("loveyou_user", "username", token.getUsername());
		
		if(objectId!=null&&!objectId.equals("")){
			
			String userInfo=BmobAPI.findOne("loveyou_user", objectId);
			
			JSONObject jo=JSONObject.parseObject(userInfo);
			
			if(password.equals(jo.getString("password"))){
				
				Session session =SecurityUtils.getSubject().getSession();
				session.setAttribute("username",jo.getString("username"));
				return new SimpleAuthenticationInfo(objectId, jo.getString("password"), getName());
			}
		}
		
		return null;
	}

}
