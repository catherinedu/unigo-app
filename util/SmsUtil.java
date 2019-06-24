package com.xjt.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import com.xjt.model.SmsObj;

/**
 * 短信发送工具类
 * @author Administrator
 *
 */
public class SmsUtil {
	
	
	
	public  static Boolean sendMessage(String content,String phone){
		//return cloundSendMessage(content, phone);
		try {
			String urlStr="http://sms1.ronglaids.com/sms.aspx";
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
			
	        DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
	        String params="account="+URLEncoder.encode("翔泰腾网络", "UTF-8");	
			params+="&userid=7901";
	        params+="&password=6688";
	        params+="&action=send";
	        params+="&mobile="+phone;
	        params+="&content="+URLEncoder.encode(content, "UTF-8");

	        out.writeBytes(params);
	        out.flush();
	        out.close(); 
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
	        
	        String line;
	        String responseStr="";
	        while ((line = reader.readLine()) != null){
	        	responseStr+=line;	
	        }
	        LogUtil.Info("调用短信接口返回数据["+responseStr+"]");
	        reader.close();
	        urlConnection.disconnect();
	        if(responseStr.contains("Success")){
	        	return true;
	        }
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return false;
	}
	
	/**
	 * 云通讯接口
	 * @param content
	 * @param phone
	 * @return
	 */
	public  static Boolean cloundSendMessage(String content,String phone){
		
		try {
			String urlStr="http://sms.253.com/msg/send/json";
			URL url=new URL(urlStr);
			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false); //Post 请求不能使用缓存
			urlConnection.setInstanceFollowRedirects(true);
			urlConnection.setRequestProperty("Content-Type","application/json;charset=utf-8");
			urlConnection.setRequestProperty("Charset", "UTF-8"); 
			urlConnection.connect();
			
	        DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
	        SmsObj smsObj=new SmsObj("N6775179", "SYCv9VnoNA1426", URLEncoder.encode(content, "UTF-8"), phone);
	        String params=JsonUtil.toJson(smsObj);
	        LogUtil.Info("post数据："+params);
	        out.writeBytes(params);
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
	        LogUtil.Info("短信发送返回数据："+responseStr);
	        if(responseStr.contains("\"code\":\"0\"")){
	        	return true;
	        }
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return false;
	}
	
	/**
	 * 发送国际短信
	 * @param content
	 * @param phone
	 * @return
	 */
	public  static Boolean cloundSendInternationMessage(String content,String phone){
		
		try {
			String urlStr="http://intapi.253.com/send/json";
			URL url=new URL(urlStr);
			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false); //Post 请求不能使用缓存
			urlConnection.setInstanceFollowRedirects(true);
			urlConnection.setRequestProperty("Content-Type","application/json;charset=utf-8");
			urlConnection.setRequestProperty("Charset", "UTF-8"); 
			urlConnection.connect();
			
	        DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
	        SmsObj smsObj=new SmsObj("I5223127", "tvg2ofwCqKb1f2",content, null,phone);
	        String params=JsonUtil.toJson(smsObj);
	        LogUtil.Info("post数据："+params);
	        out.write(params.getBytes("utf-8"));
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
	        LogUtil.Info("短信发送返回数据："+responseStr);
	        if (responseStr!=null) {
	        	SmsObj  reponseObj =JsonUtil.fromJson(responseStr, SmsObj.class);
	        	if (reponseObj.getCode().equals("0")) {
	        		return true;
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return false;
	}
	

	
	public  static String getRandom(Integer num){
		////int code=new Random().nextInt(9999-1000+1)+1000;	//4位数验证码
		String code="";
		Random random=new Random();
		for (int i = 0; i < num; i++) {
			code+=random.nextInt(10);
		}
		return code;
	}
	
	public static void main(String[] args) {
		String resp="{\"code\": \"0\", \"error\":\"\", \"msgid\":\"17080317501000379875\"}";
		if(resp.contains("\"code\":\"0\"")){
			System.out.println(true);
		}else {
			System.out.println(false);
		}
		//SmsUtil.cloundSendInternationMessage("【由你】您的手机验证码为:"+233+"。请不要泄露验证码给其他人。如非本人操作，可不用理会。", "18520040243");
		
	}
	

	
}
