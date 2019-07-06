package com.ourslook.guower.utils.pay.alipay.util;

import com.ourslook.guower.utils.XaUtils;
import com.ourslook.guower.utils.pay.alipay.config.AlipayConfig;
import com.ourslook.guower.utils.pay.alipay.sign.RSA;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @ClassName: OrderInfoUtil2_0  2016.10月以后支付宝新增
 * @Description:
 * @author duandazhi
 * @date 2016/11/21 下午6:53
 */ 
public class OrderInfoUtil2_0 {
	
	/**
	 * 构造授权参数列表
	 * 
	 * @param pid
	 * @param app_id
	 * @param target_id
	 * @return
	 */
	public static Map<String, String> buildAuthInfoMap(String pid, String app_id, String target_id) {
		Map<String, String> keyValues = new HashMap<String, String>();

		// 商户签约拿到的app_id，如：2013081700024223
		keyValues.put("app_id", app_id);

		// 商户签约拿到的pid，如：2088102123816631
		keyValues.put("pid", pid);

		// 服务接口名称， 固定值
		keyValues.put("apiname", "com.alipay.account.auth");

		// 商户类型标识， 固定值
		keyValues.put("app_name", "mc");

		// 业务类型， 固定值
		keyValues.put("biz_type", "openservice");

		// 产品码， 固定值
		keyValues.put("product_id", "APP_FAST_LOGIN");

		// 授权范围， 固定值
		keyValues.put("scope", "kuaijie");

		// 商户唯一标识，如：kkkkk091125
		keyValues.put("target_id", target_id);

		// 授权类型， 固定值
		keyValues.put("auth_type", "AUTHACCOUNT");

		// 签名类型
		keyValues.put("sign_type", "RSA");

		return keyValues;
	}

	/**
	 * 构造支付订单参数列表  App支付请求参数说明 https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.x7kkCI&treeId=204&articleId=105465&docType=1
	 * @param orderNO 订单号,用app自己的订单规则
	 * @param total_amount  支付的总金额,double 单位为元，精确到小数点后两位，取值范围[0.01,100000000]
	 * @param subject 商品的标题/交易标题/订单标题/订单关键字等。大乐透; 最大128字符
	 * @param body 对一笔交易的【具体描述信息】。如果是多种商品，请将商品描述字符串累加传给body。eg:Iphone6 16G 最大128字符
	 * @param notify_url 支付宝的同步回调接口;异步回调地址在支付宝网站上面配置
	 * @return
	 */
	public static Map<String, String> buildOrderParamMap(String orderNO, String total_amount, String subject, String body, String notify_url) {

		if (XaUtils.isEmpty(subject)) {
			subject = "";
		} else {
			if (subject.length() >= 128) {
				subject = subject.substring(0, 128);
			}
		}

		if (XaUtils.isEmpty(body)) {
			body = "";
		} else {
			if (body.length() >= 128) {
				body = body.substring(0, 128);
			}
		}

		Map<String, String> keyValues = new HashMap<String, String>();

		keyValues.put("app_id", AlipayConfig.APPID);

		keyValues.put("biz_content", "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\""+ total_amount +"\",\"subject\":\""+ subject +"\",\"body\":\""+ body +"\",\"out_trade_no\":\"" + orderNO +  "\"}");
		
		keyValues.put("charset", "utf-8");

		keyValues.put("method", AlipayConfig.Api.ALIPAY_TRADE_APP_PAY);

		keyValues.put("sign_type",  AlipayConfig.merchant_sign_type);

		keyValues.put("timestamp",  UtilDate.getDateFormatter());//发送请求的时间，格式"yyyy-MM-dd HH:mm:ss" "2016-07-29 16:55:53"

		keyValues.put("version", "1.0");

        keyValues.put("notify_url", notify_url);
		
		return keyValues;
	}
	
	/**
	 * 构造支付订单参数信息
	 * 
	 * @param map
	 * 支付订单参数
	 * @return
	 */
	public static String buildOrderParam(Map<String, String> map) {
		List<String> keys = new ArrayList<String>(map.keySet());

		// key排序
		Collections.sort(keys);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.size() - 1; i++) {
			String key = keys.get(i);
			String value = map.get(key);
			sb.append(buildKeyValue(key, value, true));
			sb.append("&");
		}

		String tailKey = keys.get(keys.size() - 1);
		String tailValue = map.get(tailKey);
		sb.append(buildKeyValue(tailKey, tailValue, true));

		return sb.toString();
	}
	
	/**
	 * 拼接键值对
	 * 
	 * @param key
	 * @param value
	 * @param isEncode
	 * @return
	 */
	private static String buildKeyValue(String key, String value, boolean isEncode) {
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append("=");
		if (isEncode) {
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				sb.append(value);
			}
		} else {
			sb.append(value);
		}
		return sb.toString();
	}
	
	/**
	 * 对支付参数信息进行签名
	 * 
	 * @param map
	 *            待签名授权信息
	 * 
	 * @return
	 */
	public static String getSign(Map<String, String> map, String rsaKey)  {
		List<String> keys = new ArrayList<String>(map.keySet());
		// key排序
		Collections.sort(keys);

		StringBuilder authInfo = new StringBuilder();
		for (int i = 0; i < keys.size() - 1; i++) {
			String key = keys.get(i);
			String value = map.get(key);
			authInfo.append(buildKeyValue(key, value, false));
			authInfo.append("&");
		}

		String tailKey = keys.get(keys.size() - 1);
		String tailValue = map.get(tailKey);
		authInfo.append(buildKeyValue(tailKey, tailValue, false));

		String oriSign = RSA.sign(authInfo.toString(), rsaKey, "UTF-8");
		String encodedSign = "";

		try {
			encodedSign = URLEncoder.encode(oriSign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "sign=" + encodedSign;
	}

}
