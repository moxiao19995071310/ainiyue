package pay.weixin.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/** 
* @ClassName: ReadXML 
* @Description: (这个类的作用是：将xml转为Map) 
* @author ggj
* @date 2016年6月30日 上午2:19:13 
* 本类资料来源 http://jingyan.baidu.com/article/bad08e1eebeefe09c951216b.html
* 把xml转换成map其他资料参考 http://ziyu-1.iteye.com/blog/469003
*  
*/
public class ReadXML {
	public static Map<String, String> getXmlElmentValue(String xml) {
		// System.out.print(xml);
		Map<String, String> map = new HashMap<String, String>();

		try {
			Document doc = DocumentHelper.parseText(xml);
			Element el = doc.getRootElement();

			return recGetXmlElementValue(el, map);
		} catch (DocumentException e) {
			e.printStackTrace();

			return null;
		}
	}

	private static Map<String, String> recGetXmlElementValue(Element ele,
			Map<String, String> map) {
		List<Element> eleList = ele.elements();

		if (eleList.size() == 0) {
			map.put(ele.getName(), ele.getTextTrim());
			// System.out.println("map1==="+map);
			return map;
		} else {
			for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();) {
				org.dom4j.Element innerEle = iter.next();
				recGetXmlElementValue(innerEle, map);
				// System.out.println("map2==="+recGetXmlElementValue(innerEle,
				// map));
			}

			return map;
		}
	}

	public static void main(String[] args) {
		String xml = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[]]></return_msg><mch_appid><![CDATA[wx0667d13434a471a0]]></mch_appid><mchid><![CDATA[1348107201]]></mchid><device_info><![CDATA[013467007045764]]></device_info><nonce_str><![CDATA[5zxbt0heq9a6fmlj]]></nonce_str><result_code><![CDATA[SUCCESS]]></result_code><partner_trade_no><![CDATA[1116]]></partner_trade_no><payment_no><![CDATA[1000018301201606300751775469]]></payment_no><payment_time><![CDATA[2016-06-30 00:44:25]]></payment_time></xml>";
		// getXmlElmentValue(xml);
		System.out.println(getXmlElmentValue(xml));

	}
}
