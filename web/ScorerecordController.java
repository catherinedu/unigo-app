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
import com.xjt.service.ScorerecordService;
import com.xjt.entity.Scorerecord;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Scorerecord控制层
* @author Administrator
*/
@Controller
public class ScorerecordController{

		@Autowired
		private ScorerecordService scorerecordService;

		/**
		 *添加或编辑Scorerecord
		*/
		@ResponseBody
		@RequestMapping("addOrEditScorerecord")
		public String addOrEditScorerecord(ModelMap modelMap,HttpSession session,Long id,Long userId,String reason,Integer num){
			Scorerecord scorerecord=null;
			if (id!=null) {
				scorerecord=scorerecordService.get(id);
			}else {
				scorerecord=new Scorerecord();
				scorerecord.setGmtCreated(new Date());
			}
			if (scorerecord!=null) {
				if (userId!=null){
					scorerecord.setUserId(userId);
				}
				if (reason!=null&&reason.length()>0){
					scorerecord.setReason(reason);
				}
				if (num!=null){
					scorerecord.setNum(num);
				}
				scorerecordService.saveOrUpdate(scorerecord);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除Scorerecord
		*/
		@ResponseBody
		@RequestMapping("delScorerecord")
		public String delScorerecord(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Scorerecord scorerecord=scorerecordService.get(id);
				if (scorerecord!=null) {
					scorerecordService.delete(scorerecord);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Scorerecord
		*/
		@ResponseBody
		@RequestMapping("getScorerecord")
		public String getScorerecord(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=scorerecordService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Scorerecord> scorerecordList= (List<Scorerecord>) page.getList();
			if (scorerecordList!=null&&scorerecordList.size()>0) {
				pageCount=scorerecordService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, scorerecordList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询Scorerecord
		*/
		@ResponseBody
		@RequestMapping("getScorerecordById")
		public String getScorerecordById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Scorerecord scorerecord=scorerecordService.get(id);
				if (scorerecord!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, scorerecord);//jsonObject	json对象
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
		 * 查询所有Scorerecord
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllScorerecord")
		public String getAllScorerecord(ModelMap modelMap,HttpSession session){
			List<Scorerecord> scorerecordList= scorerecordService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, scorerecordList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
