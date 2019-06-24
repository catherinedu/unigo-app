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

import com.xjt.service.SysVersionService;
import com.xjt.entity.SysVersion;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* 系统版本控制层
* @author Administrator
*/
@Controller
public class SysVersionController{

		@Autowired
		private SysVersionService sysVersionService;

		/**
		 *添加或编辑系统版本
		*/
		@ResponseBody
		@RequestMapping("addOrEditSysVersion")
		public String addOrEditSysVersion(ModelMap modelMap,HttpSession session,Long id,String terminal,String version){
			SysVersion sysVersion=null;
			if (id!=null) {
				sysVersion=sysVersionService.get(id);
			}else {
				sysVersion=new SysVersion();
				sysVersion.setGmtCreated(new Date());
			}
			if (sysVersion!=null) {
				if (terminal!=null&&terminal.length()>0){
					sysVersion.setTerminal(terminal);
				}
				if (version!=null&&version.length()>0){
					sysVersion.setVersion(version);
				}
				sysVersionService.saveOrUpdate(sysVersion);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除系统版本
		*/
		@ResponseBody
		@RequestMapping("delSysVersion")
		public String delSysVersion(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				SysVersion sysVersion=sysVersionService.get(id);
				if (sysVersion!=null) {
					sysVersionService.delete(sysVersion);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询系统版本
		*/
		@ResponseBody
		@RequestMapping("getSysVersion")
		public String getSysVersion(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=sysVersionService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<SysVersion> sysVersionList= (List<SysVersion>) page.getList();
			if (sysVersionList!=null&&sysVersionList.size()>0) {
				pageCount=sysVersionService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, sysVersionList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询系统版本
		*/
		@ResponseBody
		@RequestMapping("getSysVersionById")
		public String getSysVersionById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				SysVersion sysVersion=sysVersionService.get(id);
				if (sysVersion!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, sysVersion);//jsonObject	json对象
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
		 * 查询所有系统版本
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllSysVersion")
		public String getAllSysVersion(ModelMap modelMap,HttpSession session){
			List<SysVersion> sysVersionList= sysVersionService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, sysVersionList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查看最新系统版本
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getNewSysVersion")
		public String getNewSysVersion(ModelMap modelMap,HttpSession session,String terminal){
			if (terminal!=null&&terminal.length()>0) {
				Map<String, Object> prams=new HashMap<String, Object>();
				Map<String, Object> sortPram=new HashMap<String, Object>();
				prams.put("terminal", terminal);
				sortPram.put("desc", "id");
				List<SysVersion> sysVersionList=(List<SysVersion>) sysVersionService.getByPrams(prams, sortPram, null, 0, 1, null, null).getList();
				if (sysVersionList!=null&&sysVersionList.size()>0) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, sysVersionList.get(0));//jsonObject json对象
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);	
				}else{
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.NULLDATA);	
				}
			}else{
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.PARAMERROR);	
			}
			return JsonUtil.toJson(modelMap);
		}
}
