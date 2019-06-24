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

import com.xjt.util.LogUtil;

/**
 * 微信模版消息工具类
 * @author Administrator
 *
 */
public class WeCharModelMessageUtil {
	
	
	private static String toUrl;
	
	
	
	public WeCharModelMessageUtil(String toUrl) {
		this.toUrl = toUrl;
	}

	/**
	 * 发送发货成功模板消息
	 * @return
	 */
	public static String toSendOrderModelMessage(String openId,String expressCompany,String expressNum){
		String accessToken=WechatUtil.getAccessToken();
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
							"\"template_id\":\"ysx6-KR27DZSRizwkwCnbpvsJnvaBuUm90Tr5MPmL5Y\","+
							"\"url\":\""+toUrl+"\","+ 
							"\"data\":{"+
								"\"first\": {"+
								"\"value\":\"亲,您的订单已被处理!\","+
								"\"color\":\"#173177\""+
								"},"+
								"\"delivername\": {"+
								"\"value\":\""+expressCompany+"\","+
								"\"color\":\"#173177\""+
								"},"+
								"\"ordername\": {"+
								"\"value\":\""+expressNum+"\","+
								"\"color\":\"#173177\""+
								"},"+
								"\"remark\":{"+
								"\"value\":\"点击查看订单\","+
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
	        LogUtil.Info("模版消息调用结果:"+responseStr);
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
}
