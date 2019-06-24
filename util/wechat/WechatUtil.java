package com.xjt.util.wechat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.xjt.model.AccessToken;
import com.xjt.model.BaseObject;
import com.xjt.model.WaChatOrderPayObject;
import com.xjt.model.WeChatUserInfo;
import com.xjt.util.JsonUtil;
import com.xjt.util.LogUtil;
import com.xjt.util.MD5;

/**
 * 微信公众平台工具类
 * @author Administrator
 *
 */
public class WechatUtil {
	
	private static Random random=new Random();
	
	private static String appid=null;			//公众号的appid
	private static String secret=null;
	private static String mch_id=null;			//微信支付分配的商户号
	private static String key=null;				//微信商户平台(pay.weixin.qq.com)→账户设置 →API安全→ 密钥设置
	private static String notifyUrl=null;		//支付成功回调地址
	private static String phoneappid=null;
	
	public  WechatUtil(String appid,String secret,String mch_id,String key,String notifyUrl,String phoneappid){
		this.appid=appid;
		this.secret=secret;
		this.mch_id=mch_id;
		this.key=key;
		this.notifyUrl=notifyUrl;
		this.phoneappid=phoneappid;
	}
	/**
	 * 获取OpenId
	 * @param code
	 * @return
	 */
	public static BaseObject getBaseObject(String code){
			if(code!=null&&appid!=null&&code!=null){
				String urlStr="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
				try {
					URL url = new URL(urlStr);
					HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
					urlConnection.setDoOutput(true);
					urlConnection.setDoInput(true);
					urlConnection.setRequestMethod("GET");
					urlConnection.setUseCaches(false); 
					urlConnection.setInstanceFollowRedirects(true);
					urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
					urlConnection.setRequestProperty("Charset", "UTF-8"); 
					urlConnection.connect();
					DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
			        out.flush();
			        out.close(); 
			        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
			        String line;
			        String responseStr="";
			        while ((line = reader.readLine()) != null){
			        	responseStr+=line;
			        }
			        reader.close();
			        urlConnection.disconnect();
			        LogUtil.Info("getBaseObject获取返回结果"+responseStr);
			        if(responseStr.contains("openid")){
			        	BaseObject baseObject=JsonUtil.fromJson(responseStr, BaseObject.class);
			        	return baseObject;
			        }
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
	}
	
	/**
	 * 获取AccessToken
	 * @return
	 */
	public static String getAccessToken(){
		String urlStr="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setUseCaches(false); 
			urlConnection.setInstanceFollowRedirects(true);
			urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Charset", "UTF-8"); 
			urlConnection.connect();
			DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
	        out.flush();
	        out.close(); 
	        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
	        String line;
	        String responseStr="";
	        while ((line = reader.readLine()) != null){
	        	responseStr+=line;
	        }
	        reader.close();
	        urlConnection.disconnect();
	        if(responseStr.contains("access_token")){
	        	AccessToken accessToken=JsonUtil.fromJson(responseStr, AccessToken.class);
	        	return accessToken.getAccess_token();
	        }
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 发送注册成功模板消息
	 * @return
	 */
	public static String toSendModelMessage(String openId,String phone,String id){
		String dayStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String accessToken=getAccessToken();
		if(accessToken.length()<=0){
			return "";
		}
		System.out.println("accessToken==="+accessToken);
		String urlStr="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false); 
			urlConnection.setInstanceFollowRedirects(true);
			urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlConnection.setRequestProperty("Charset", "UTF-8"); 
			urlConnection.connect();
			DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
			String params="{"+
							"\"touser\":\""+openId+"\","+
							"\"template_id\":\"ZvG8sGRKqo2Hk-OFRkHVLbvaeZTX7DDJFyS3G-0mb4A\","+
							"\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxf316a7b967b8f30d&redirect_uri=http://youi-tech.com/WetChatStore/personalinformation.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect\","+
							"\"data\":{"+
								"\"first\": {"+
								"\"value\":\"客官,恭喜您成功注册为VIP\","+
								"\"color\":\"#173177\""+
								"},"+
								"\"keyword1\":{"+
								"\"value\":\""+phone+"\","+
								"\"color\":\"#173177\""+
								"},"+
								"\"keyword2\":{"+
								"\"value\":\""+id+"\","+
								"\"color\":\"#173177\""+
								"},"+
								"\"keyword3\":{"+
								"\"value\":\""+dayStr+"\","+
								"\"color\":\"#173177\""+
								"},"+
								"\"remark\":{"+
								"\"value\":\"点击完成个人资料\","+
								"\"color\":\"#173177\""+
								"}"+
							 "}"+
							"}";		
			out.write(params.getBytes("UTF-8"));
	        out.flush();
	        out.close(); 
	        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
	        String line;
	        String responseStr="";
	        while ((line = reader.readLine()) != null){
	        	responseStr+=line;
	        }
	        reader.close();
	        urlConnection.disconnect();
	       
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取用户信息
	 * @param openid
	 * @param access_token
	 * @return
	 */
	public static WeChatUserInfo getUserInfo(String openid,String access_token){
		LogUtil.Info("开始获取用户信息:openid="+openid+",access_token="+access_token);
		if(openid!=null&&access_token!=null){
			String urlStr="https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN ";
			try {
				URL url = new URL(urlStr);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("GET");
				urlConnection.setUseCaches(false); 
				urlConnection.setInstanceFollowRedirects(true);
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Charset", "UTF-8"); 
				urlConnection.connect();
				DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		        out.flush();
		        out.close(); 
		        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
		        String line;
		        String responseStr="";
		        while ((line = reader.readLine()) != null){
		        	responseStr+=line;
		        }
		        reader.close();
		        urlConnection.disconnect();
		        System.out.println(responseStr);
		        if(responseStr.contains("openid")){
		        	WeChatUserInfo userInfo=JsonUtil.fromJson(responseStr, WeChatUserInfo.class);
		        	if (userInfo!=null) {
		        		LogUtil.Info("获取用户信息成功:"+userInfo.toString());
					}
		        	return userInfo;
		        }
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.Info("获取用户信息发生异常");
			} 
		}
		return null;
	}
	
	/**
	 * 统一下单获取prepay_id
	 * @param nonceStr	随机字符串
	 * @param outTradeNo 商户系统内部的订单号，可自定义
	 * @param ip		用户端实际ip
	 * @param openid	用户的openid
	 * @param body		弹出支付窗口时的提示内容
	 * @param notifyUrl	接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。 
	 * @param totalFee	订单总金额，单位为分
	 * @return
	 */
	public static String getPrepayId(String nonceStr,String outTradeNo,String ip,String openid,String body,String notifyUrl,Integer totalFee){
		if (mch_id!=null&&mch_id.length()>0&&key!=null&&key.length()>0) {
			LogUtil.Info("开始发起统一下单获取prepay_id");
			try {
				String urlStr="https://api.mch.weixin.qq.com/pay/unifiedorder";
				URL url=new URL(urlStr);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setUseCaches(false); //Post 请求不能使用缓存
				urlConnection.setInstanceFollowRedirects(true);
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Charset", "UTF-8"); 
				urlConnection.connect();
				
				nonceStr=getNonceStr(10);
				String sintrt="appid="+appid+"&body="+body+"&mch_id="+mch_id+"&nonce_str="+nonceStr+"&notify_url="+notifyUrl+"&openid="+openid+"&out_trade_no="+outTradeNo+"&spbill_create_ip="+ip+"&total_fee="+totalFee+"&trade_type=JSAPI&key="+key;
				String sign=MD5.getMD5(sintrt).toUpperCase();
				DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		        String params="<xml>"+
								"<appid>"+appid+"</appid>"+
								"<body>"+body+"</body>"+
								"<mch_id>"+mch_id+"</mch_id>"+
								"<nonce_str>"+nonceStr+"</nonce_str>"+
								"<notify_url>"+notifyUrl+"</notify_url>"+
								"<openid>"+openid+"</openid>"+
								"<out_trade_no>"+outTradeNo+"</out_trade_no>"+
								"<spbill_create_ip>"+ip+"</spbill_create_ip>"+
								"<total_fee>"+totalFee+"</total_fee>"+
								"<trade_type>JSAPI</trade_type>"+
								"<sign>"+sign+"</sign>"+
							"</xml>";		
		        params=new String(params.getBytes("UTF-8"), "ISO-8859-1");//utf-8编码
		        out.writeBytes(params);
		        out.flush();
		        out.close(); 
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
		        
		        String line;
		        String responseStr="";
		        while ((line = reader.readLine()) != null){
		        	responseStr+=line;
		        }
		        LogUtil.Info("统一下单返回信息:"+responseStr);
		        if(responseStr.contains("SUCCESS")){
		        	LogUtil.Info("发起统一下单获取prepay_id成功");
		        	return WeChatXmlUtil.readXmlGetPrepayId(responseStr);	//获取prepay_id
		        }
		        reader.close();
		        urlConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.Info("发起统一下单获取prepay_id出现异常");
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 统一下单获取prepay_id 扫码用
	 * @param nonceStr	随机字符串
	 * @param outTradeNo 商户系统内部的订单号，可自定义
	 * @param ip		用户端实际ip
	 * @param body		弹出支付窗口时的提示内容
	 * @param notifyUrl	接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。 
	 * @param totalFee	订单总金额，单位为分
	 * @return
	 */
	public static String getPrepayIdByScanner(String outTradeNo,String ip,String body,String notifyUrl,Integer totalFee){
		if (mch_id!=null&&mch_id.length()>0&&key!=null&&key.length()>0) {
			LogUtil.Info("开始发起扫码支付统一下单");
			try {
				String urlStr="https://api.mch.weixin.qq.com/pay/unifiedorder";
				URL url=new URL(urlStr);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setUseCaches(false); //Post 请求不能使用缓存
				urlConnection.setInstanceFollowRedirects(true);
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Charset", "UTF-8"); 
				urlConnection.connect();
				
				String nonceStr=getNonceStr(10);
				String sintrt="appid="+appid+"&body="+body+"&mch_id="+mch_id+"&nonce_str="+nonceStr+"&notify_url="+notifyUrl+"&out_trade_no="+outTradeNo+"&spbill_create_ip="+ip+"&total_fee="+totalFee+"&trade_type=NATIVE&key="+key;
				String sign=MD5.getMD5(sintrt).toUpperCase();
				DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		        String params="<xml>"+
								"<appid>"+appid+"</appid>"+
								"<body>"+body+"</body>"+
								"<mch_id>"+mch_id+"</mch_id>"+
								"<nonce_str>"+nonceStr+"</nonce_str>"+
								"<notify_url>"+notifyUrl+"</notify_url>"+
								"<out_trade_no>"+outTradeNo+"</out_trade_no>"+
								"<spbill_create_ip>"+ip+"</spbill_create_ip>"+
								"<total_fee>"+totalFee+"</total_fee>"+
								"<trade_type>NATIVE</trade_type>"+
								"<sign>"+sign+"</sign>"+
							"</xml>";		
		        params=new String(params.getBytes("UTF-8"), "ISO-8859-1");//utf-8编码
		        out.writeBytes(params);
		        out.flush();
		        out.close(); 
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
		        
		        String line;
		        String responseStr="";
		        while ((line = reader.readLine()) != null){
		        	responseStr+=line;
		        }
		        LogUtil.Info("统一下单返回信息:"+responseStr);
		        if(responseStr.contains("SUCCESS")){
		        	LogUtil.Info("发起统一下单获取数据成功");
		        	WaChatOrderPayObject  waChatOrderPayObject=WeChatXmlUtil.readXmlGetOderPayObject(responseStr);	
		        	if (waChatOrderPayObject.getCode_url()!=null) {
		        		LogUtil.Info("waChatOrderPayObject的数据为="+waChatOrderPayObject.toString());
		        		return waChatOrderPayObject.getCode_url();
					}
		        }
		        reader.close();
		        urlConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.Info("发起扫码支付统一下单出现异常");
				return null;
			}
		}
		return null;
	}
	/**
	 * 统一下单获取prepay_id APP用
	 * @param nonceStr
	 * @param outTradeNo
	 * @param ip
	 * @param body
	 * @param notifyUrl
	 * @param totalFee
	 * @return
	 */
	public static String getPrepayIdByAPP(String outTradeNo,String ip,String body,String notifyUrl,Integer totalFee){
		if (mch_id!=null&&mch_id.length()>0&&key!=null&&key.length()>0) {
			LogUtil.Info("开始发起APP支付统一下单");
			try {
				String urlStr="https://api.mch.weixin.qq.com/pay/unifiedorder";
				URL url=new URL(urlStr);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setUseCaches(false); //Post 请求不能使用缓存
				urlConnection.setInstanceFollowRedirects(true);
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Charset", "UTF-8"); 
				urlConnection.connect();
				
				String nonceStr=getNonceStr(10);
				String sintrt="appid="+phoneappid+"&body="+body+"&mch_id="+mch_id+"&nonce_str="+nonceStr+"&notify_url="+notifyUrl+"&out_trade_no="+outTradeNo+"&spbill_create_ip="+ip+"&total_fee="+totalFee+"&trade_type=APP&key="+key;
				String sign=MD5.getMD5(sintrt).toUpperCase();
				DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		        String params="<xml>"+
								"<appid>"+phoneappid+"</appid>"+
								"<body>"+body+"</body>"+
								"<mch_id>"+mch_id+"</mch_id>"+
								"<nonce_str>"+nonceStr+"</nonce_str>"+
								"<notify_url>"+notifyUrl+"</notify_url>"+
								"<out_trade_no>"+outTradeNo+"</out_trade_no>"+
								"<spbill_create_ip>"+ip+"</spbill_create_ip>"+
								"<total_fee>"+totalFee+"</total_fee>"+
								"<trade_type>APP</trade_type>"+
								"<sign>"+sign+"</sign>"+
							"</xml>";		
		        params=new String(params.getBytes("UTF-8"), "ISO-8859-1");//utf-8编码
		        out.writeBytes(params);
		        out.flush();
		        out.close(); 
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
		        
		        String line;
		        String responseStr="";
		        while ((line = reader.readLine()) != null){
		        	responseStr+=line;
		        }
		        LogUtil.Info("统一下单返回信息:"+responseStr);
		        if(responseStr.contains("SUCCESS")){
		        	LogUtil.Info("发起统一下单获取数据成功");
		        	WaChatOrderPayObject  waChatOrderPayObject=WeChatXmlUtil.readXmlGetOderPayObject(responseStr);	
		        	LogUtil.Info("waChatOrderPayObject的数据为="+waChatOrderPayObject.toString());
		        	if (waChatOrderPayObject!=null&&waChatOrderPayObject.getPrepay_id()!=null) {
		        		return getAppPrepayIdSign(waChatOrderPayObject.getPrepay_id());//获取统一下单id后生成签名
					}
		        }
		        reader.close();
		        urlConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.Info("发起APP支付统一下单异常");
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 获取app统一下单的签名
	 * @param prepayid
	 * @return
	 */
	public static String getAppPrepayIdSign(String prepayid){
		if (prepayid!=null) {
			LogUtil.Info("开始生成app支付签名");
			String sintrt="appid="+phoneappid+"&partnerid="+mch_id+"&prepayid="+prepayid+"&package=Sign=WXPay"+"&noncestr="+getNonceStr(10)+"&timestamp="+new Date().getTime()+"&key="+key;
			LogUtil.Info("签名前数据为="+sintrt);
			String sign=MD5.getMD5(sintrt).toUpperCase();
			LogUtil.Info("生成app支付签名结束");
			return sign;
		}
		return null;
	}
	
	
	/**
	 * 查询订单支付状态
	 * @param nonceStr
	 * @param outTradeNo
	 * @param ip
	 * @param openid
	 * @param body
	 * @param notifyUrl
	 * @param totalFee
	 * @return
	 */
	public static WaChatOrderPayObject getOrderPayState(String out_trade_no){
		if (mch_id!=null&&mch_id.length()>0&&appid!=null&&appid.length()>0) {
			LogUtil.Info("开始支付状态查询");
			try {
				String urlStr="https://api.mch.weixin.qq.com/pay/orderquery";
				URL url=new URL(urlStr);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setUseCaches(false); //Post 请求不能使用缓存
				urlConnection.setInstanceFollowRedirects(true);
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Charset", "UTF-8"); 
				urlConnection.connect();
				
				String nonceStr=getNonceStr(10);
				String sintrt="appid="+appid+"&mch_id="+mch_id+"&nonce_str="+nonceStr+"&out_trade_no="+out_trade_no+"&key="+key;
				String sign=MD5.getMD5(sintrt).toUpperCase();
				DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		        String params="<xml>"+
								"<appid>"+appid+"</appid>"+
								"<mch_id>"+mch_id+"</mch_id>"+
								"<nonce_str>"+nonceStr+"</nonce_str>"+
								"<out_trade_no>"+out_trade_no+"</out_trade_no>"+
								"<sign>"+sign+"</sign>"+
							"</xml>";		
		        //params=new String(params.getBytes("UTF-8"), "ISO-8859-1");//utf-8编码
		        out.writeBytes(params);
		        out.flush();
		        out.close(); 
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
		        
		        String line;
		        String responseStr="";
		        while ((line = reader.readLine()) != null){
		        	responseStr+=line;
		        }
		        if(responseStr.contains("SUCCESS")){
		        	LogUtil.Info("发起支付状态查询成功");
		        	LogUtil.Info("返回数据"+responseStr);
		        	return WeChatXmlUtil.readXmlGetOderPayObject(responseStr);	
		        }
		        reader.close();
		        urlConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.Info("发起支付状态查询异常");
				return null;
			}
		}
		return null;
	}
	
	

	public static String getNonceStr(Integer length) { //length表示生成字符串的长度  
	    String baseStr = "abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM0123456789";     
	    Random random = new Random();     
	    StringBuffer stringBuffer = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(baseStr.length());     
	        stringBuffer.append(baseStr.charAt(number));     
	    }     
	    return stringBuffer.toString();     
	 }
	
	 /** 
	   * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址, 
	   * 
	   * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？ 
	   * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。 
	   * 
	   * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130, 
	   * 192.168.1.100 
	   * 
	   * 用户真实IP为： 192.168.1.110 
	   * 
	   * @param request 
	   * @return 
	   */
	  public static String getIpAddress(HttpServletRequest request) { 
		    String ip = request.getHeader("x-forwarded-for"); 
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		      ip = request.getHeader("Proxy-Client-IP"); 
		    } 
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		      ip = request.getHeader("WL-Proxy-Client-IP"); 
		    } 
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		      ip = request.getHeader("HTTP_CLIENT_IP"); 
		    } 
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		      ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
		    } 
		    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		      ip = request.getRemoteAddr(); 
		    } 
		    return ip; 
	  } 
	
	public static String getAppid() {
		return appid;
	}
	public static String getSecret() {
		return secret;
	}
	public static String getMch_id() {
		return mch_id;
	}
	public static String getKey() {
		return key;
	}
	public static String getNotifyUrl() {
		return notifyUrl;
	}
	public static void setNotifyUrl(String notifyUrl) {
		WechatUtil.notifyUrl = notifyUrl;
	}
	
	/**
	 * 查询列表
	 * @param access_token
	 * @param next_openid
	 * @return
	 */
	public static String getUserListInfo(String access_token,String next_openid){
		if (access_token!=null) {
			String urlStr="https://api.weixin.qq.com/cgi-bin/user/get?access_token="+access_token;
			try {
				URL url = new URL(urlStr);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("GET");
				urlConnection.setUseCaches(false); 
				urlConnection.setInstanceFollowRedirects(true);
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Charset", "UTF-8"); 
				urlConnection.connect();
				DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		        out.flush();
		        out.close(); 
		        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
		        String line;
		        String responseStr="";
		        while ((line = reader.readLine()) != null){
		        	responseStr+=line;
		        }
		        reader.close();
		        urlConnection.disconnect();
		        return responseStr;
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.Info("获取用户信息发生异常");
			} 
		
		}
		return null;
	}
	
	public static String getUserDescInfo(String access_token,String openid){
		if (access_token!=null) {
			String urlStr="https://api.weixin.qq.com/cgi-bin/user/info?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
			try {
				URL url = new URL(urlStr);
				HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("GET");
				urlConnection.setUseCaches(false); 
				urlConnection.setInstanceFollowRedirects(true);
				urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConnection.setRequestProperty("Charset", "UTF-8"); 
				urlConnection.connect();
				DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		        out.flush();
		        out.close(); 
		        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
		        String line;
		        String responseStr="";
		        while ((line = reader.readLine()) != null){
		        	responseStr+=line;
		        }
		        reader.close();
		        urlConnection.disconnect();
		        return responseStr;
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.Info("获取用户信息发生异常");
			} 
		
		}
		return null;
	}
	
	public static void main(String[] args) {
		appid="wx04c200ffc679c0e8";
		secret="97c06d2c3fbb041e68d4e73deeb16524";
		String access_token=getAccessToken();
		System.out.println(getUserListInfo(access_token, ""));
		System.err.println(getUserDescInfo(getAccessToken(), "oIJ9jwYXkSwaPQzL0q1cpGRm7WGw"));
		
	}
	
	
}
