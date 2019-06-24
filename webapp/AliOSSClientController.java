package com.xjt.webapp;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.LogUtil;
import com.xjt.util.alioss.AliOSSClient;

/**
 * 
 * @author Administrator
 *
 */
@Controller
public class AliOSSClientController {
	
	
	/**
	 * 查询所有bucket
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("sysUserGetInfo/getAllBucket")
	public String getAllBucket(ModelMap modelMap,HttpSession session){
		OSSClient ossClient=AliOSSClient.getOSSClient();
		if (ossClient!=null) {
			List<Bucket> buckets = ossClient.listBuckets();
			if (buckets!=null&&buckets.size()>0) {
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, buckets);		
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);
			}
			ossClient.shutdown();
		}else {
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLOBJECT);//-4
		}
		return JsonUtil.toJson(modelMap);
	}
	
	/**
	 * 根据bucket查询文件
	 * @param modelMap
	 * @param session
	 * @param bucket
	 * @return
	 */
	@ResponseBody
	@RequestMapping("sysUserGetInfo/getAllFileByBucket")
	public String getAllFileByBucket(ModelMap modelMap,HttpSession session,String bucketName){
		if (bucketName!=null&&bucketName.length()>0) {
			OSSClient ossClient=AliOSSClient.getOSSClient();
			if (ossClient!=null) {
				ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
				ObjectListing objectListing = ossClient.listObjects(listObjectsRequest.withMaxKeys(1000));
				List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
				if (sums!=null&&sums.size()>0) {
					modelMap.put(CommonInfoUtil.jSONOBJECTLIST, sums);		
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
				}
				ossClient.shutdown();
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLOBJECT);//-4
			}
		}else {
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//-1
		}
		return JsonUtil.toJson(modelMap);
	}
	
	/**
	 * 获取文件url
	 * @param modelMap
	 * @param session
	 * @param bucketName
	 * @param key
	 * @param time
	 * @return
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping("sysUserGetInfo/getOSSFileUrl")
	public String getOSSFileUrl(ModelMap modelMap,HttpSession session,String bucketName,String key,Integer time) throws IOException{
		if (bucketName!=null&&bucketName.length()>0&&key!=null&&key.length()>0) {
			Date date = new Date(new Date().getTime() + time*1000);//1000为秒
			URL url=AliOSSClient.getObjectUrl(bucketName, key, date);
			modelMap.put("url", url);
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
		}else {
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//-1
		}
		return JsonUtil.toJson(modelMap);
	}
}
