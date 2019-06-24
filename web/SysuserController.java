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

import com.xjt.service.SysuserService;
import com.xjt.entity.Sysuser;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.MD5;
import com.xjt.util.Page;

/**
* Sysuser控制层
* @author Administrator
*/
@Controller
public class SysuserController{

		@Autowired
		private SysuserService sysuserService;

		/**
		 *添加或编辑Sysuser
		*/
		@ResponseBody
		@RequestMapping("addOrEditSysuser")
		public String addOrEditSysuser(ModelMap modelMap,HttpSession session,Long id,String account,String pwd){
			Sysuser sysuser=null;
			if (id!=null) {
				sysuser=sysuserService.get(id);
			}else {
				sysuser=new Sysuser();
			}
			if (sysuser!=null) {
				if (account!=null&&account.length()>0){
					sysuser.setAccount(account);
				}
				if (pwd!=null&&pwd.length()>0){
					sysuser.setPwd(pwd);
				}
				sysuserService.saveOrUpdate(sysuser);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除Sysuser
		*/
		@ResponseBody
		@RequestMapping("delSysuser")
		public String delSysuser(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Sysuser sysuser=sysuserService.get(id);
				if (sysuser!=null) {
					sysuserService.delete(sysuser);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Sysuser
		*/
		@ResponseBody
		@RequestMapping("getSysuser")
		public String getSysuser(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=sysuserService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Sysuser> sysuserList= (List<Sysuser>) page.getList();
			if (sysuserList!=null&&sysuserList.size()>0) {
				pageCount=sysuserService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, sysuserList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询Sysuser
		*/
		@ResponseBody
		@RequestMapping("getSysuserById")
		public String getSysuserById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Sysuser sysuser=sysuserService.get(id);
				if (sysuser!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, sysuser);//jsonObject	json对象
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
		 * 查询所有Sysuser
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllSysuser")
		public String getAllSysuser(ModelMap modelMap,HttpSession session){
			List<Sysuser> sysuserList= sysuserService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, sysuserList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
		
		
		/**
		 * 后台管理员登录
		 * @param modelMap
		 * @param session
		 * @param account
		 * @param pwd
		 * @param code
		 * @return
		 */
		@ResponseBody
		@RequestMapping("sysLogin")
		public String sysLogin(ModelMap modelMap,HttpSession session,String account,String pwd,String code){
			if (account!=null&&account.length()>0&&pwd!=null&&pwd.length()>0&&code!=null&&code.length()>0&&session.getAttribute("validateCode")!=null) {
				String validateCode=(String) session.getAttribute("validateCode");
				if (validateCode.toLowerCase().equals(code.toLowerCase())) {
					pwd=MD5.getPwdMd5(pwd);
					Map<String, Object> params=new HashMap<String, Object>();
					params.put("account", account);
					params.put("pwd", pwd);
					Sysuser sysuser= sysuserService.getByParam(params);
					if (sysuser!=null) {
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS); //1
						session.setAttribute("sysuser", sysuser);
					}else {
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//0
					}
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NOPERMISSIONS);//-6
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		@RequestMapping("bg/sysLoingOut")
		public  String sysLoingOut(ModelMap modelMap,HttpSession session){
			session.removeAttribute("sysuser");
			return "bg/index";
		}
		
		
		/**
		 * 修改密码
		 * @param modelMap
		 * @param session
		 * @param oldpwd
		 * @param newpwd
		 * @return
		 */
		@ResponseBody
		@RequestMapping("sysUserGetInfo/editpwd")
		public  String editpwd(ModelMap modelMap,HttpSession session,String oldpwd,String newpwd){
			if (oldpwd!=null&&newpwd!=null&&oldpwd.length()>0&&newpwd.length()>0) {
				Sysuser sysuser=(Sysuser) session.getAttribute("sysuser");
				oldpwd=MD5.getPwdMd5(oldpwd);
				if (sysuser.getPwd().equals(oldpwd)) {
					newpwd=MD5.getPwdMd5(newpwd);
					sysuser.setPwd(newpwd);
					sysuserService.saveOrUpdate(sysuser);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS); //1
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA); //0
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
}
