package com.xjt.util.alioss;

import java.net.URL;
import java.util.Date;

import com.aliyun.oss.OSSClient;

/**
 * 阿里云OSS实例初始化
 * @author Administrator
 *
 */
public class AliOSSClient {
	
	// endpoint以杭州为例，其它region请按实际情况填写
	private static String endpoint = null;
	// accessKey请登录https://ak-console.aliyun.com/#/查看
	private static String accessKeyId = null;
	private static String accessKeySecret = null;
	
	/**
	 * 初始化参数
	 * @param endpoint
	 * @param accessKeyId
	 * @param accessKeySecret
	 */
	public AliOSSClient(String endpoint, String accessKeyId,
			String accessKeySecret) {
		super();
		this.endpoint = endpoint;
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = accessKeySecret;	
	}
	
	/**
	 * 创建OSSClient实例
	 * @return
	 */
	public static OSSClient getOSSClient(){
		if (endpoint!=null&&accessKeyId!=null&&accessKeySecret!=null) {
			return new OSSClient(endpoint, accessKeyId,accessKeySecret);
		}else {
			return null;
		}
	}
	
	/**
	 * 查询资源url
	 * @param bucketName
	 * @param key
	 * @param date
	 * @return
	 */
	public static URL getObjectUrl(String bucketName,String key ,Date date){
		OSSClient ossClient=getOSSClient();
		if (ossClient!=null) {
			// Object是否存在
			boolean found = ossClient.doesObjectExist(bucketName, key);
			if (found) {
				URL url=ossClient.generatePresignedUrl(bucketName, key, date);
				ossClient.shutdown();	
				return url;
			}
		}
		return null;
	}
	
	
	
	public String getEndpoint() {
		return endpoint;
	}


	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}


	public String getAccessKeyId() {
		return accessKeyId;
	}


	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}


	public String getAccessKeySecret() {
		return accessKeySecret;
	}


	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}


	public static void main(String[] args) {
		AliOSSClient aliOSSClient=new AliOSSClient("http://oss-cn-hangzhou.aliyuncs.com", "LTAI9FPTx52V14Gt", "wDctH73Ss5RnTvaB7MryRToUl018YD ");
	}
	
	
}
