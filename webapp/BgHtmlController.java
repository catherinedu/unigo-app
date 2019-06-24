package com.xjt.webapp;




import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjt.entity.Activityarticle;
import com.xjt.entity.Columntype;
import com.xjt.entity.Idleinfo;
import com.xjt.entity.Jobinfo;
import com.xjt.entity.Rentinfo;
import com.xjt.entity.Typearticle;
import com.xjt.entity.User;
import com.xjt.service.ActivityarticleService;
import com.xjt.service.ColumntypeService;
import com.xjt.service.IdleinfoService;
import com.xjt.service.JobinfoService;
import com.xjt.service.RentinfoService;
import com.xjt.service.TypearticleService;
import com.xjt.service.UserService;
import com.xjt.util.CommonInfoUtil;


@Controller
@RequestMapping("bg/")
public class BgHtmlController {
	
	@Autowired
	private ColumntypeService columntypeService;
	@Autowired
	private TypearticleService typearticleService;
	@Autowired
	private ActivityarticleService activityarticleService;
	@Autowired
	private JobinfoService jobinfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private RentinfoService rentinfoService;
	@Autowired
	private IdleinfoService idleinfoService;
	
	@RequestMapping("login")
	public String login(){
		return "bg/index";
	}
	
	@RequestMapping("report")
	public String report(){
		return "bg/report";
	}
	
	@RequestMapping("checkuser")
	public String checkuser(){
		return "bg/checkuser";
	}

	@RequestMapping("sendMsg")
	public String sendMsg(){
		return "bg/sendMsg";
	}
	
	
	@RequestMapping("article")
	public String article(){
		return "bg/article";
	}
	
	@RequestMapping("userAdmin")
	public String userAdmin(){
		return "bg/userAdmin";
	}
	
	
	
	@RequestMapping("adminSetup")
	public String adminSetup(){
		return "bg/adminSetup";
	}
	
	@RequestMapping("job")
	public String job(){
		return "bg/job";
	}
	@RequestMapping("operationData")
	public String operationData(){
		return "bg/operationData";
	}
	
	
	@RequestMapping("articleInfo")
	public String articleInfo(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			Typearticle typearticle=typearticleService.get(id);
			if (typearticle!=null) {
				modelMap.put(CommonInfoUtil.jSONOBJECT, typearticle);
			}
		}
		List<Columntype> columntypes= columntypeService.getByParams(null);
		if (columntypes!=null&&columntypes.size()>0) {
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, columntypes);
		}
		return "bg/articleInfo";
	}

	
	@RequestMapping("activity")
	public String activity(){
		return "bg/activity";
	}
	
	
	@RequestMapping("operationData2")
	public String operationData2(){
		return "bg/operationData2";
	}
	
	@RequestMapping("operationData1")
	public String operationData1(){
		return "bg/operationData1";
	}
	
	
	@RequestMapping("activityInfo")
	public String activityInfo(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			Activityarticle activityarticle=activityarticleService.get(id);
			if (activityarticle!=null) {
				modelMap.put(CommonInfoUtil.jSONOBJECT, activityarticle);
			}
		}
		return "bg/activityInfo";
	}

	
	@RequestMapping("classification")
	public String classification(){
		return "bg/classification";
	}
	
	@RequestMapping("classificationInfo")
	public String classificationInfo(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			Columntype columntype=columntypeService.get(id);
			if (columntype!=null) {
				modelMap.put(CommonInfoUtil.jSONOBJECT, columntype);
			}
		}
		return "bg/classificationInfo";
	}
	
	@RequestMapping("jobDiscuss")
	private  String jobdiscuss(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			modelMap.put("infoId", id);
		}
		System.out.println("233");
		return "bg/jobDiscuss";
	}
	
	
	@RequestMapping("jobInfo")
	public String jobInfo(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			Jobinfo jobinfo=jobinfoService.get(id);
			if (jobinfo!=null) {
				modelMap.put(CommonInfoUtil.jSONOBJECT, jobinfo);
			}
		}
		return "bg/jobInfo";
	}
	
	@RequestMapping("reportIdle")
	public String reportIdle(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			Idleinfo idleinfo=idleinfoService.get(id);
			if (idleinfo!=null) {
				if (idleinfo.getUserId()!=null) {
					User user=userService.get(idleinfo.getUserId());
					if (user!=null) {
						idleinfo.setUserName(user.getName());
					}else {
						idleinfo.setUserName("");
					}
				}
				String[] imgs=null;
				if (idleinfo.getImgs()!=null) {
					imgs=idleinfo.getImgs().split(",");
				}
				modelMap.put("imgs", imgs);
				modelMap.put(CommonInfoUtil.jSONOBJECT, idleinfo);
			}
		}
		return "bg/reportIdle";
	}
	
	@RequestMapping("reportRoom")
	public String reportRoom(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			Rentinfo rentinfo=rentinfoService.get(id);
			if (rentinfo!=null) {
				if (rentinfo.getUserId()!=null) {
					User user=userService.get(rentinfo.getUserId());
					if (user!=null) {
						rentinfo.setUserName(user.getName());
					}else {
						rentinfo.setUserName("");
					}
				}
				String[] imgs=null;
				if (rentinfo.getImgs()!=null) {
					imgs=rentinfo.getImgs().split(",");
				}
				modelMap.put("imgs", imgs);
				String[] tabs=null;
				if (rentinfo.getTabInfo()!=null) {
					tabs=rentinfo.getTabInfo().split(",");
				}
				modelMap.put("tabs", tabs);
				modelMap.put(CommonInfoUtil.jSONOBJECT, rentinfo);
			}
		}
		return "bg/reportRoom";
	}
	
	
	@RequestMapping("jobInfo2")
	public String jobInfo2(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			Jobinfo jobinfo=jobinfoService.get(id);
			if (jobinfo!=null) {
				modelMap.put(CommonInfoUtil.jSONOBJECT, jobinfo);
			}
		}
		return "bg/jobInfo2";
	}
	
	@RequestMapping("userAdminEdit")
	public  String userAdminEdit(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			User user=userService.get(id);
			if (user!=null) {
				modelMap.put(CommonInfoUtil.jSONOBJECT, user);
			}
		}
		return "bg/userAdminEdit";
	}
	
	@RequestMapping("userAdminInfo")
	public  String userAdminInfo(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			modelMap.put("userId", id);
		}
		return "bg/userAdminInfo";
	}
	
	@RequestMapping("userAdminInfo2")
	public  String userAdminInfo2(ModelMap modelMap,HttpSession session,Long id){
		if (id!=null) {
			modelMap.put("userId", id);
		}
		return "bg/userAdminInfo2";
	}
	

}
