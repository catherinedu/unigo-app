package com.xjt.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;

import sun.misc.BASE64Decoder;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
public class FileUploadUtil {
	
	/**
	 * 文件上传
	 * @param fileUrl
	 * @param fileName
	 * @param fileArr
	 * @return
	 * @throws IOException
	 */
	public static String fileUpload(String fileUrl,String fileName,byte [] fileArr) throws IOException{
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		String formatYear=new SimpleDateFormat("yyyy").format(new Date());
		String formatDate=new SimpleDateFormat("MMdd").format(new Date());
		String formatTime=new SimpleDateFormat("HHmmss").format(new Date());
		fileName=formatTime+"."+fileType;
		String severUrl="upload"+File.separator+formatYear+File.separator+formatDate+File.separator+fileName;//服务器的相对地址
		String localUrl=fileUrl+severUrl;//保存在计算机上的地址
		File file =new File(localUrl);
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		FileCopyUtils.copy(fileArr, file);
		return  "upload/"+formatYear+"/"+formatDate+"/"+fileName;
	}
	
	/**
	 * 原文件上传
	 * @param fileUrl
	 * @param fileName
	 * @param fileArr
	 * @return
	 * @throws IOException
	 */
	public static String realFileUpload(String fileUrl,String fileName,byte [] fileArr) throws IOException{
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		String formatYear=new SimpleDateFormat("yyyy").format(new Date());
		String formatDate=new SimpleDateFormat("MMdd").format(new Date());
		String formatTime=new SimpleDateFormat("HHmmss").format(new Date());
		fileName="r"+formatTime+"."+fileType;
		String severUrl="upload"+File.separator+formatYear+File.separator+formatDate+File.separator+fileName;//服务器的相对地址
		String localUrl=fileUrl+severUrl;//保存在计算机上的地址
		File file =new File(localUrl);
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		FileCopyUtils.copy(fileArr, file);
		markImage(fileUrl, localUrl);//添加水印
		return  "upload/"+formatYear+"/"+formatDate+"/"+fileName;
	}
	
	/**
	 * 图片上传  返回路径和图片大小
	 * @param modelMap
	 * @param fileUrl
	 * @param fileName
	 * @param fileArr
	 * @throws IOException
	 */
	public static void fileUpload(ModelMap modelMap,String fileUrl,String fileName,byte [] fileArr) throws IOException{
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);;
		String formatYear=new SimpleDateFormat("yyyy").format(new Date());
		String formatDate=new SimpleDateFormat("MMdd").format(new Date());
		String formatTime=new SimpleDateFormat("HHmmss").format(new Date());
		fileName=formatTime+"."+fileType;
		String severUrl="upload"+File.separator+formatYear+File.separator+formatDate+File.separator+fileName;//服务器的相对地址
		String localUrl=fileUrl+severUrl;//保存在计算机上的地址
		File file =new File(localUrl);
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		FileCopyUtils.copy(fileArr, file);
		modelMap.put("size", file.length());
		modelMap.put("url", "upload/"+formatYear+"/"+formatDate+"/"+fileName);
	}
	
	/**
	 * 图片压缩上传
	 * @param fileUrl
	 * @param fileName
	 * @param fileStream
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static String imgUpload(String fileUrl,String fileName,InputStream fileStream,Integer width,Integer height,Integer comRate,Integer mark) throws IOException{
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);;
		String formatYear=new SimpleDateFormat("yyyy").format(new Date());
		String formatDate=new SimpleDateFormat("MMdd").format(new Date());
		String formatTime=new SimpleDateFormat("HHmmss").format(new Date());
		fileName=formatTime+"."+fileType;
		String severUrl="upload"+File.separator+formatYear+File.separator+formatDate+File.separator+fileName;//服务器的相对地址
		String localUrl=fileUrl+severUrl;//保存在计算机上的地址
		File file =new File(localUrl);
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		compressImage(fileStream, width, height, comRate, localUrl);
		/**添加水印*/
		if (mark!=null) {
			markImage(fileUrl, localUrl);
		}
		
		return "upload/"+formatYear+"/"+formatDate+"/"+fileName; //服务器的相对地址
	}
	
	/**
	 * 压缩图片
	 * @param fileStream   文件流
	 * @param width		      目的宽度
	 * @param height       目的高度
	 * @param comRate      压缩的宽高比例
	 * @param saveUrl      保存的路径
	 * @throws IOException
	 */
	public static void compressImage(InputStream fileStream,Integer width,Integer height,Integer comRate,String saveUrl) throws IOException{
		Image srcFile = ImageIO.read(fileStream);
		if (comRate!=null&&comRate!=0) {
			if (srcFile.getWidth(null)>600) {
				 width = srcFile.getWidth(null)/comRate;  
			     height =srcFile.getHeight(null)/comRate;  
			}
		}else if (width==null&&height==null){
			width=150;
			height=150;
		}
		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
		FileOutputStream out = new FileOutputStream(saveUrl);
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
		/** 压缩质量 */
		jep.setQuality(1f, true);
		encoder.encode(tag, jep);
		out.close();
	}
	
	/**
	 * 创建文件路径
	 * @param fileUrl
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String getFileUploadUrl(HttpServletRequest request,String fileName) throws IOException{
		String fileUrl=request.getSession().getServletContext().getRealPath("/");
		String formatYear=new SimpleDateFormat("yyyy").format(new Date());
		String formatDate=new SimpleDateFormat("MMdd").format(new Date());
		String formatTime=new SimpleDateFormat("HHmmss").format(new Date());
		fileName=formatTime+"/"+fileName;
		String url="upload/"+formatYear+"/"+formatDate+"/"+fileName;
		File file =new File(fileUrl+url);
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
		}
		
		return fileUrl+url;
	}
	
	/**
	 * 获取文件工程路径
	 * @param request
	 * @param url
	 * @return
	 */
	public  static String getProjectFileUrl(HttpServletRequest request,String url){
		String requestUrl=request.getRequestURL().toString();
		String fileUrl=request.getSession().getServletContext().getRealPath("/");
		requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
		url=requestUrl+"/"+url.substring(fileUrl.length());
		return url;
	}
	
	/**
	 * 获取工程路径
	 * @param request
	 * @param fileName
	 * @return
	 */
	public static String getUploadUrl(HttpServletRequest request,String fileName){
		String url=fileName;
		File file =new File(request.getSession().getServletContext().getRealPath("/")+File.separator+url);
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
		}
		return file.getPath();
	}
	
	/**
	 * 获取服务器路径
	 * @param request
	 * @return
	 */
	public static String  getRequestUrl(HttpServletRequest request){
		String requestUrl=request.getRequestURL().toString();
		requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
		return requestUrl;
	}
	
	
	/**
	 * Base64图片上传
	 * @param fileUrl
	 * @param fileName
	 * @param fileArr
	 * @return
	 * @throws IOException
	 */
	public static String fileUploadBase64Img(String fileUrl,String fileName,byte [] fileArr,Integer mark) throws IOException{
		String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
		String formatYear=new SimpleDateFormat("yyyy").format(new Date());
		String formatDate=new SimpleDateFormat("MMdd").format(new Date());
		String formatTime=new SimpleDateFormat("HHmmss").format(new Date());
		fileName=formatTime+"."+fileType;
		String severUrl="upload"+File.separator+formatYear+File.separator+formatDate+File.separator+fileName;//服务器的相对地址
		String localUrl=fileUrl+severUrl;//保存在计算机上的地址
		File file =new File(localUrl);
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		FileCopyUtils.copy(fileArr, file);
		/**添加水印*/
		if (mark!=null) {
			markImage(fileUrl, localUrl);
		}
		return  "upload/"+formatYear+"/"+formatDate+"/"+fileName;
	}
	
	/**
	 * 解析base64图片
	 * @param imgBytes
	 * @return
	 */
	public static  String uploadBase64Img(HttpServletRequest request,String imgBytes,Integer mark){
		if (imgBytes.contains("data:image/jpeg;base64,")) {
			imgBytes=imgBytes.substring("data:image/jpeg;base64,".length(), imgBytes.length());
		}
		BASE64Decoder decoder = new BASE64Decoder();  
		try {
			 byte[] b = decoder.decodeBuffer(imgBytes);
			 for(int i=0;i<b.length;++i) {  
	             if(b[i]<0){
	            	 //调整异常数据  
	                 b[i]+=256;  
	             }  
	         } 
			 String returnUrlStr=fileUploadBase64Img(request.getSession().getServletContext().getRealPath("/"), "x.png", b,mark);
			 String requestUrl=request.getRequestURL().toString();
			 requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			 if (requestUrl.contains("userGetJsonInfo")) {
				 requestUrl=requestUrl.substring(0, requestUrl.lastIndexOf("/"));
			 }
			 String url=requestUrl+"/"+returnUrlStr;
			 System.out.println(url);
			 return url;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return null;
	}
	
	/**
	 * 添加水印
	 * @param fileUrl
	 * @param localUrl
	 */
	public static  void markImage(String fileUrl,String localUrl){
		String iconPath=fileUrl+"bg"+File.separator+"image"+File.separator+"shuiyinlogo.png";//水印图片地址   工程路径/bg/image/shuiyinlogo.png
		String comIconPath=fileUrl+"bg"+File.separator+"image"+File.separator+new Date().getTime()+"comshuiyinlogo.png"; 
		ImageRemarkUtil.rateMarkImageByIcon(iconPath, comIconPath,localUrl, localUrl,null);
		//ImageRemarkUtil.pressImage(iconPath, localUrl, 4, 0.7f);
	}
	
	
}
