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
import com.xjt.service.UserreadrecordService;
import com.xjt.entity.Userreadrecord;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Userreadrecord控制层
* @author Administrator
*/
@Controller
public class UserreadrecordController{

		@Autowired
		private UserreadrecordService userreadrecordService;

		/**
		 *添加或编辑Userreadrecord
		*/
		@ResponseBody
		@RequestMapping("addOrEditUserreadrecord")
		public String addOrEditUserreadrecord(ModelMap modelMap,HttpSession session,Long id,Long typeId,Long articleId){
			Userreadrecord userreadrecord=null;
			if (id!=null) {
				userreadrecord=userreadrecordService.get(id);
			}else {
				userreadrecord=new Userreadrecord();
				userreadrecord.setGmtCreated(new Date());
			}
			if (userreadrecord!=null) {
				if (typeId!=null){
					userreadrecord.setTypeId(typeId);
				}
				if (articleId!=null){
					userreadrecord.setArticleId(articleId);
				}
				userreadrecordService.saveOrUpdate(userreadrecord);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除Userreadrecord
		*/
		@ResponseBody
		@RequestMapping("delUserreadrecord")
		public String delUserreadrecord(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Userreadrecord userreadrecord=userreadrecordService.get(id);
				if (userreadrecord!=null) {
					userreadrecordService.delete(userreadrecord);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Userreadrecord
		*/
		@ResponseBody
		@RequestMapping("getUserreadrecord")
		public String getUserreadrecord(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=userreadrecordService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Userreadrecord> userreadrecordList= (List<Userreadrecord>) page.getList();
			if (userreadrecordList!=null&&userreadrecordList.size()>0) {
				pageCount=userreadrecordService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, userreadrecordList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询Userreadrecord
		*/
		@ResponseBody
		@RequestMapping("getUserreadrecordById")
		public String getUserreadrecordById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Userreadrecord userreadrecord=userreadrecordService.get(id);
				if (userreadrecord!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, userreadrecord);//jsonObject	json对象
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
		 * 查询所有Userreadrecord
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllUserreadrecord")
		public String getAllUserreadrecord(ModelMap modelMap,HttpSession session){
			List<Userreadrecord> userreadrecordList= userreadrecordService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, userreadrecordList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
