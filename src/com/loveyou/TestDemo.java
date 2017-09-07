package com.loveyou;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.Test;

import com.loveyou.bmob.restapi.Bmob;
import com.loveyou.webController.common.BmobAPI;

import net.coobird.thumbnailator.Thumbnails;

public class TestDemo extends Bmob {

	public void test() throws Exception {
		String code = "001W0eKp1DcnKt0LUVJp1fPeKp1W0eKV";
		String codeInfo = BmobAPI.getInfoByCode(code);
		String openId = BmobAPI.getOpenId(codeInfo);
		String accessToken = BmobAPI.getAccess_token(codeInfo);
		System.out.println("openId:" + openId);
		System.out.println("accessToken:" + accessToken);
	}

	public void test1() throws Exception {
		String accessToken = "d1Lp_LwM6VIRM2qKVErNW1af6aC9k6HDz5M6VmccY9sQNr7g8Ab8xvsKWfBBOqusSXop4MlQwIorUe8Yq65yjQTzJkRXuour-9dMTX48pNACPBfAJAHUG";
		String openId = "o_E-7wYsjpc246occNX0STjxCBfU";
		String userInfo = BmobAPI.getUserInfo(accessToken, openId);
		System.out.println("userInfo:" + userInfo);
	}

	@Test
	public void test2() throws Exception {
		File uploadFile = getFile().getFile();
		File smallImage = new File("smallImage.png");
		FileInputStream fis = new FileInputStream(uploadFile);
		FileOutputStream fos = new FileOutputStream("bigImage.png");
		int len = 0;
		byte[] bts = new byte[1024];
		while ((len = fis.read(bts)) != -1) {
			fos.write(bts, 0, len);
		}
		fis.close();
		fos.close();
		Thumbnails.of(uploadFile).size(400, 250).toFile(smallImage);
		renderText("hello");
		
	}

}
