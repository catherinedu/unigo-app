package com.xjt.web;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjt.service.ActivityarticleService;
import com.xjt.entity.Activityarticle;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Activityarticle控制层
* @author Administrator
*/
@Controller
public class ActivityarticleController{

		@Autowired
		private ActivityarticleService activityarticleService;

		/**
		 *添加或编辑Activityarticle
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/addOrEditActivityarticle")
		public String addOrEditActivityarticle(ModelMap modelMap,HttpSession session,Long id,String title,String introduce,String img,String content,Integer readNum,String author,Integer number,Integer discussNum){
			Activityarticle activityarticle=null;
			if (id!=null) {
				activityarticle=activityarticleService.get(id);
			}else {
				activityarticle=new Activityarticle();
				activityarticle.setGmtCreated(new Date());
				readNum=0;
				discussNum=0;
				number=65535;
			}
			if (activityarticle!=null) {
				if (title!=null&&title.length()>0){
					activityarticle.setTitle(title);
				}
				if (introduce!=null&&introduce.length()>0) {
					activityarticle.setIntroduce(introduce);
				}
				if (img!=null&&img.length()>0){
					activityarticle.setImg(img);
				} 
				if (content!=null&&content.length()>0){
					activityarticle.setContent(content);
				}
				if (readNum!=null){
					activityarticle.setReadNum(readNum);
				}
				if (discussNum!=null){
					activityarticle.setDiscussNum(discussNum);
				}
				if (author!=null&&author.length()>0){
					activityarticle.setAuthor(author);
				}
				if (number!=null){
					activityarticle.setNumber(number);
				}
				activityarticleService.saveOrUpdate(activityarticle);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除Activityarticle
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delActivityarticle")
		public String delActivityarticle(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Activityarticle activityarticle=activityarticleService.get(id);
				if (activityarticle!=null) {
					activityarticleService.delete(activityarticle);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Activityarticle
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/getActivityarticle")
		public String getActivityarticle(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=null;
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "id");
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
				searchPram.put("title", pms);
				searchPram.put("content", pms);
				searchPram.put("author", pms);
			}
			Map<String, Object> startTime=null;
			if(start!=null&&start.length()>0){
				startTime=new HashMap<String, Object>();
				startTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(start+" 00:00:00"));
			}
			Map<String, Object> endTime=null;
			if(end!=null&&end.length()>0){
				endTime=new HashMap<String, Object>();
				endTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(end+" 23:59:59"));
			}
			Page page=activityarticleService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Activityarticle> activityarticleList= (List<Activityarticle>) page.getList();
			if (activityarticleList!=null&&activityarticleList.size()>0) {
				pageCount=activityarticleService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, activityarticleList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 用户查询活动文章
		 * @param modelMap
		 * @param session
		 * @param pageIndex
		 * @param pageNum
		 * @param pageCount
		 * @param des
		 * @param pms
		 * @param start
		 * @param end
		 * @return
		 * @throws ParseException
		 */
		@ResponseBody
		@RequestMapping("appGetActivityarticle")
		public String appGetActivityarticle(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=null;
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "id");
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
				searchPram.put("title", pms);
				searchPram.put("content", pms);
				searchPram.put("author", pms);
			}
			Map<String, Object> startTime=null;
			if(start!=null&&start.length()>0){
				startTime=new HashMap<String, Object>();
				startTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(start+" 00:00:00"));
			}
			Map<String, Object> endTime=null;
			if(end!=null&&end.length()>0){
				endTime=new HashMap<String, Object>();
				endTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(end+" 23:59:59"));
			}
			Page page=activityarticleService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Activityarticle> activityarticleList= (List<Activityarticle>) page.getList();
			if (activityarticleList!=null&&activityarticleList.size()>0) {
				pageCount=activityarticleService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				for (Activityarticle activityarticle : activityarticleList) {
					activityarticle.setContent(null);
					activityarticle.setTimeStr(DateUtil.getDateLenthStr(activityarticle.getGmtCreated(), new Date()));
					activityarticle.setGmtCreated(null);
				}
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, activityarticleList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询Activityarticle
		*/
		@ResponseBody
		@RequestMapping("getActivityarticleById")
		public String getActivityarticleById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Activityarticle activityarticle=activityarticleService.get(id);
				if (activityarticle!=null) {
					Integer readNum=activityarticle.getReadNum();//添加阅读量
					if (readNum==null||readNum<0) {
						readNum=0;
					}
					activityarticle.setReadNum(readNum+1);
					activityarticleService.saveOrUpdate(activityarticle);
					modelMap.put(CommonInfoUtil.jSONOBJECT, activityarticle);//jsonObject	json对象
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 查询所有Activityarticle
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllActivityarticle")
		public String getAllActivityarticle(ModelMap modelMap,HttpSession session){
			List<Activityarticle> activityarticleList= activityarticleService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, activityarticleList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
