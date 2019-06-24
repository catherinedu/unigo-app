package com.xjt.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.xjt.util.CommonInfoUtil;
import com.xjt.util.FileUploadUtil;
import com.xjt.util.JsonUtil;


@Controller
public class UploadFileController {
	
	@RequestMapping("fileUpload")
	public void fileUpload(@RequestParam(value = "info_file") MultipartFile files,
            HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=UTF-8");
		String url="";
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		    CommonsMultipartFile multipartFile = (CommonsMultipartFile) multipartRequest.getFile("info_file");
			String fileName = multipartFile.getOriginalFilename();
			String returnUrlStr=FileUploadUtil.fileUpload(request.getSession().getServletContext().getRealPath("/"), fileName, multipartFile.getBytes());
			String requestUrl=request.getRequestURL().toString();
			requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			url=requestUrl+"/"+returnUrlStr;
		    System.out.println("fielurl============"+url);
		} catch (Exception e) {
			url="error";
			System.err.println(e);
		}
		response.getWriter().write(url);
	} 
	
	/**
	 * 上传视频并得到大小
	 * @param files
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("fileUploadAndGetSize")
	public String fileUploadAndGetSize(@RequestParam(value = "info_file") MultipartFile files,
            HttpServletRequest request, HttpServletResponse response,ModelMap modelMap) throws IOException{
		response.setContentType("text/html;charset=UTF-8");
		String url="";
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		    CommonsMultipartFile multipartFile = (CommonsMultipartFile) multipartRequest.getFile("info_file");
			String fileName = multipartFile.getOriginalFilename();
			FileUploadUtil.fileUpload(modelMap,request.getSession().getServletContext().getRealPath("/"), fileName, multipartFile.getBytes());
			String returnUrlStr=(String) modelMap.get("url");
			String requestUrl=request.getRequestURL().toString();
			requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			url=requestUrl+"/"+returnUrlStr;
			modelMap.put("url", url);
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//1
		    System.out.println("fielurl============"+url);
		} catch (Exception e) {
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.ERROR);//-1
		}
		return JsonUtil.toJson(modelMap);
	} 
	
	/**
	 * 图片压缩上传
	 * @param files
	 * @param request
	 * @param response
	 * @param width
	 * @param height
	 * @param comRate
	 * @throws IOException
	 */
	@RequestMapping("compressionImgUpload")
	public void compressionImgUpload(@RequestParam(value = "info_file") MultipartFile files,
            HttpServletRequest request, HttpServletResponse response,Integer width,Integer height,Integer comRate,Integer mark) throws IOException{
		response.setContentType("text/html;charset=UTF-8");
		String url="";
		if (width==null) {
			width=150;
		}
		if (height==null) {
			height=150;
		}
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		    CommonsMultipartFile multipartFile = (CommonsMultipartFile) multipartRequest.getFile("info_file");
			String fileName = multipartFile.getOriginalFilename();
			String returnUrlStr=FileUploadUtil.imgUpload(request.getSession().getServletContext().getRealPath("/"), fileName, multipartFile.getInputStream(), width, height,comRate,mark);
			String requestUrl=request.getRequestURL().toString();
			requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			url=requestUrl+"/"+returnUrlStr;
		    System.out.println("fielurl============"+url);
		} catch (Exception e) {
			url="error";
			System.err.println(e);
		}
		response.getWriter().write(url);
	} 
	
	/**
	 * web端压缩图片上传
	 * @param files
	 * @param request
	 * @param response
	 * @param width
	 * @param height
	 * @param comRate
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("webCompressionImgUpload")
	public String webCompressionImgUpload(@RequestParam(value = "info_file") MultipartFile files,
            HttpServletRequest request, HttpServletResponse response,Integer width,Integer height,Integer comRate,Integer mark) throws IOException{
		response.setContentType("text/html;charset=UTF-8");
		String url="";
		 Map<String, Object> msgMap=new HashMap<String, Object>();
		 msgMap.put("msg", -5);
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		    CommonsMultipartFile multipartFile = (CommonsMultipartFile) multipartRequest.getFile("info_file");
			String fileName = multipartFile.getOriginalFilename();
			String returnUrlStr=FileUploadUtil.imgUpload(request.getSession().getServletContext().getRealPath("/"), fileName, multipartFile.getInputStream(), width, height,comRate,mark);
			String requestUrl=request.getRequestURL().toString();
			requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			url=requestUrl+"/"+returnUrlStr;
		    System.out.println("url============"+url);
			msgMap.put("url", url);
			
			returnUrlStr=FileUploadUtil.realFileUpload(request.getSession().getServletContext().getRealPath("/"), "r"+fileName, multipartFile.getBytes());
			requestUrl=request.getRequestURL().toString();
			requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			url=requestUrl+"/"+returnUrlStr;
			System.out.println("realurl============"+url);
			msgMap.put("realurl", url);
			msgMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
		} catch (Exception e) {
			url="error";
			System.err.println(e);
		}
		return JsonUtil.toJson(msgMap);
	} 
	
	
	/**
	 * 编辑器上传图片
	 * @param files
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("filesUpload")
	public void filesUpload(@RequestParam(value = "files") MultipartFile files,
            HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=UTF-8");
		String url="";
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		    CommonsMultipartFile multipartFile = (CommonsMultipartFile) multipartRequest.getFile("files");
			String fileName = multipartFile.getOriginalFilename();
			String returnUrlStr=FileUploadUtil.fileUpload(request.getSession().getServletContext().getRealPath("/"), fileName, multipartFile.getBytes());
			String requestUrl=request.getRequestURL().toString();
			requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			url=requestUrl+"/"+returnUrlStr;
		    System.out.println("fielurl============"+url);
		} catch (Exception e) {
			url="error";
			System.err.println(e);
		}
		response.getWriter().write(url);
	} 
	
	/**
	 * app上传图片
	 * @param files
	 * @param request
	 * @param response
	 * @param width
	 * @param height
	 * @param modelMap
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("appCompressionImgUpload")
	public String appCompressionImgUpload(@RequestParam(value = "info_file") MultipartFile files,
            HttpServletRequest request, HttpServletResponse response,Integer width,Integer height,ModelMap modelMap,Integer comRate,Integer mark) throws IOException{
		response.setContentType("text/html;charset=UTF-8");
		String url="";
		if (width==null) {
			width=150;
		}
		if (height==null) {
			height=150;
		}
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		    CommonsMultipartFile multipartFile = (CommonsMultipartFile) multipartRequest.getFile("info_file");
			String fileName = multipartFile.getOriginalFilename();
			String returnUrlStr=FileUploadUtil.imgUpload(request.getSession().getServletContext().getRealPath("/"), fileName, multipartFile.getInputStream(), width, height,comRate,mark);
			String requestUrl=request.getRequestURL().toString();
			requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			url=requestUrl+"/"+returnUrlStr;
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
			modelMap.put("url", url);
		    System.out.println("fielurl============"+url);
		} catch (Exception e) {
			url="error";
			System.err.println(e);
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.ERROR);
		}
		return JsonUtil.toJson(modelMap);
	} 
	
	/**
	 * 上传base64图片
	 * @param request
	 * @param img
	 * @return
	 */
	@ResponseBody
	@RequestMapping("uploadImgByBase64")
	public String uploadImgByBase64(ModelMap modelMap,HttpServletRequest request,String img,Integer mark){
		if (img!=null&&img.length()>0) {
			String url=FileUploadUtil.uploadBase64Img(request, img,mark);
			if (url!=null&&url.length()>0) {
				modelMap.put("url", url);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);
			}
		}else {
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);
		}
		return JsonUtil.toJson(modelMap);
	}
	
}
