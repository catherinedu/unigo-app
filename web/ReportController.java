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

import com.xjt.service.ReportService;
import com.xjt.entity.Report;
import com.xjt.entity.User;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Report控制层
* @author Administrator
*/
@Controller
public class ReportController{

		@Autowired
		private ReportService reportService;

		/**
		 *添加或编辑Report
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditReport")
		public String addOrEditReport(ModelMap modelMap,HttpSession session,Long id,Integer type,Long infoId,String reason){
			User user=(User) session.getAttribute("user");
			if (type!=null&&reason!=null&&infoId!=null) {
				Report report=new Report();
				report.setGmtCreated(new Date());
				report.setType(type);
				report.setInfoId(infoId);
				report.setUserId(user.getId());
				report.setReason(reason);
				reportService.saveOrUpdate(report);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除Report
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delReport")
		public String delReport(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Report report=reportService.get(id);
				if (report!=null) {
					reportService.delete(report);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Report
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/getReport")
		public String getReport(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
				searchPram.put("reason", pms);
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
			Page page=reportService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Report> reportList= (List<Report>) page.getList();
			if (reportList!=null&&reportList.size()>0) {
				pageCount=reportService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, reportList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询Report
		*/
		@ResponseBody
		@RequestMapping("getReportById")
		public String getReportById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Report report=reportService.get(id);
				if (report!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, report);//jsonObject	json对象
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
		 * 查询所有Report
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllReport")
		public String getAllReport(ModelMap modelMap,HttpSession session){
			List<Report> reportList= reportService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, reportList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
