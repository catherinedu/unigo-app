package com.xjt.web;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
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

import com.xjt.service.CollectionService;
import com.xjt.service.JobinfoService;
import com.xjt.service.UsersubscriptionService;
import com.xjt.chat.PushMsg;
import com.xjt.chat.PushUtil;
import com.xjt.entity.Jobinfo;
import com.xjt.entity.Rentinfo;
import com.xjt.entity.User;
import com.xjt.entity.Usersubscription;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Jobinfo控制层
* @author Administrator
*/
@Controller
public class JobinfoController{

		@Autowired
		private JobinfoService jobinfoService;
		@Autowired
		private CollectionService collectionService;
		@Autowired
		private UsersubscriptionService  usersubscriptionService;
		/**
		 *添加或编辑Jobinfo
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/addOrEditJobinfo")
		public String addOrEditJobinfo(ModelMap modelMap,HttpSession session,Long id,String sendmsg,Integer type,Float lowsalary,Float hightsalary,String nature,String address,Double longitud,Double latitude,String excImg,String industry,String title,String area,String salary,String companyImg,String companyName,String degreeRequired,String workExperience,String tabInfo,String introduce,String companyProfile,String jobDuty){
			Jobinfo jobinfo=null;
			if (id!=null) {
				jobinfo=jobinfoService.get(id);
			}else {
				jobinfo=new Jobinfo();
				jobinfo.setGmtCreated(new Date());
				jobinfo.setReadNum(0);
				jobinfo.setDisNum(0);
			}
			if (jobinfo!=null) {
				if (type!=null){
					jobinfo.setType(type);
				}
				if (title!=null&&title.length()>0){
					jobinfo.setTitle(title);
				}
				if (area!=null&&area.length()>0){
					jobinfo.setArea(area);
				}
				jobinfo.setSalary(salary);
				jobinfo.setLowsalary(lowsalary);
				jobinfo.setHightsalary(hightsalary);
				if (companyImg!=null&&companyImg.length()>0){
					jobinfo.setCompanyImg(companyImg);
				}
				if (excImg!=null&&excImg.length()>0){
					jobinfo.setExcImg(excImg);
				}
				if (industry!=null&&industry.length()>0) {
					jobinfo.setIndustry(industry);
				}
				if (nature!=null&&nature.length()>0) {
					jobinfo.setNature(nature);
				}
				if (companyName!=null&&companyName.length()>0){
					jobinfo.setCompanyName(companyName);
				}
				if (degreeRequired!=null&&degreeRequired.length()>0){
					jobinfo.setDegreeRequired(degreeRequired);
				}
				if (workExperience!=null&&workExperience.length()>0){
					jobinfo.setWorkExperience(workExperience);
				}
				if (tabInfo!=null&&tabInfo.length()>0){
					jobinfo.setTabInfo(tabInfo);
				}
				if (introduce!=null&&introduce.length()>0){
					jobinfo.setIntroduce(introduce);
				}
				if (companyProfile!=null&&companyProfile.length()>0){
					jobinfo.setCompanyProfile(companyProfile);
				}
				if (jobDuty!=null&&jobDuty.length()>0){
					jobinfo.setJobDuty(jobDuty);
				}
				if (longitud!=null) {
					jobinfo.setLongitud(longitud);
				}
				if (latitude!=null) {
					jobinfo.setLatitude(latitude);
				}
				if (address!=null&&address.length()>0) {
					jobinfo.setAddress(address);
				}
				jobinfoService.saveOrUpdate(jobinfo);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
				if(sendmsg!=null&&sendmsg.length()>0){//推送求职特招消息
					sendSubMsg(sendmsg);
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 向已订阅的求职的用户推送消息
		 * @param sendmsg
		 */
		private void sendSubMsg(String sendmsg){
			PushMsg pushMsg=new PushMsg(3, null, null, null, null, sendmsg, 0);
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("subType", 2);
			List<Usersubscription> usersubscriptions =  usersubscriptionService.getByParams(params); //查询订阅的用户
			if (usersubscriptions!=null&&usersubscriptions.size()>0) {
				for (Usersubscription usersubscription : usersubscriptions) {
					PushUtil.sendMsgToUser(usersubscription.getUserId(), pushMsg);
				}
			}
		}

		/**
		 *删除Jobinfo
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delJobinfo")
		public String delJobinfo(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Jobinfo jobinfo=jobinfoService.get(id);
				if (jobinfo!=null) {
					jobinfoService.delete(jobinfo);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Jobinfo
		*/
		@ResponseBody
		@RequestMapping("webgetJobinfo")
		public String webgetJobinfo(ModelMap modelMap,HttpSession session,String nature,String industry,Double longitud,Double latitude,Float lowsalary,Float hightsalary,Integer pageIndex,Integer pageNum,Integer pageCount,String area,String des,String despms,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			String sql="";
			Map<String, Object> prams=null;
			if (area!=null&&area.length()>0) {
				prams=new HashMap<String, Object>();
				prams.put("area", area);
				sql="  area='"+area+"' ";
			}
			if (nature!=null&&nature.length()>0) {//工作性质
				if (prams==null) {
					prams=new HashMap<String, Object>();
				}
				prams.put("nature", nature);
				if (sql.length()>0) {
					sql+=" and ";
				}
				sql+="  nature='"+nature+"' ";
			}
			if (industry!=null&&industry.length()>0) {//行业类别
				if (prams==null) {
					prams=new HashMap<String, Object>();
				}
				prams.put("industry", industry);
				if (sql.length()>0) {
					sql+=" and ";
				}
				sql+="  industry='"+industry+"' ";
			}
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "readNum"); //默认按阅读量排序
			}
			if (despms!=null&&despms.length()>0) {
				sortPram=new HashMap<String, Object>();
				if (despms.equals("distance")) {//根据距离排序
					if (longitud!=null&&latitude!=null) {
						List<Long> ids=getDistance(longitud, latitude, pageIndex, pageNum, des,sql);
						if (ids!=null) {
							if (prams==null) {
								prams=new HashMap<String, Object>();
							}
							prams.put("id", ids);
						}
					}
				}else {
					sortPram.put(des, despms); 
				}
				
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
				searchPram.put("title", pms);
				searchPram.put("companyName", pms);
				searchPram.put("area", pms);
				searchPram.put("industry", pms);
				searchPram.put("nature", pms);
			}
			Map<String, Object> startTime=null;
			if(lowsalary!=null){
				startTime=new HashMap<String, Object>();
				startTime.put("lowsalary",  lowsalary);
			}
			Map<String, Object> endTime=null;
			if(hightsalary!=null){
				endTime=new HashMap<String, Object>();
				endTime.put("hightsalary",  hightsalary);
			}
			Page page=jobinfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Jobinfo> jobinfoList= (List<Jobinfo>) page.getList();
			if (jobinfoList!=null&&jobinfoList.size()>0) {
				pageCount=jobinfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, jobinfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}	
		
		/**
		 * 根据距离查询信息
		 */
		public List<Long> getDistance(Double longitud,Double latitude,Integer pageIndex,Integer pageNum,String des,String whereSql){
			if (whereSql.length()>0) {
				whereSql=" where "+whereSql;
			}
			String sql="SELECT id FROM jobinfo "+whereSql+" ORDER BY getdistance("+latitude+","+longitud+",latitude,longitud)  "+des+" LIMIT "+(pageIndex-1)*pageNum+","+pageNum;
			List<Map<String, Object>> listmap = jobinfoService.getBySQL(sql, null);
			if(listmap!=null&&listmap.size()>0){
				List<Long> ids=new ArrayList<Long>();
				for (Map<String, Object> map : listmap) {
					ids.add((Long)map.get("id"));
				}
				return ids;
			}
			return null;
		}
		
		/**
		 * 分页查询Jobinfo
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/sysgetJobinfo")
		public String sysgetJobinfo(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
				searchPram.put("companyName", pms);
				searchPram.put("area", pms);
				searchPram.put("industry", pms);
				searchPram.put("nature", pms);
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
			Page page=jobinfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Jobinfo> jobinfoList= (List<Jobinfo>) page.getList();
			if (jobinfoList!=null&&jobinfoList.size()>0) {
				pageCount=jobinfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, jobinfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 *根据id查询Jobinfo
		*/
		@ResponseBody
		@RequestMapping("getJobinfoById")
		public String getJobinfoById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Jobinfo jobinfo=jobinfoService.get(id);
				if (jobinfo!=null) {
					Integer readNum=jobinfo.getReadNum();
					if (readNum==null||readNum<0) {
						readNum=0;
					}
					jobinfo.setReadNum(readNum+1);
					jobinfoService.saveOrUpdate(jobinfo);
					if(session.getAttribute("user")!=null){
						checkCollection(jobinfo, (User)session.getAttribute("user"));
					}
					modelMap.put(CommonInfoUtil.jSONOBJECT, jobinfo);//jsonObject	json对象
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
		 * 检查是否收藏
		 */
		public void checkCollection(Jobinfo jobinfo,User user){
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("infoId", jobinfo.getId());
			params.put("userId", user.getId());
			params.put("type", 2);
			if (collectionService.getCount(params)>0) {
				jobinfo.setIsColletion(1);
			}else {
				jobinfo.setIsColletion(0);
			}
		}
		/**
		 * 查询所有Jobinfo
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllJobinfo")
		public String getAllJobinfo(ModelMap modelMap,HttpSession session){
			List<Jobinfo> jobinfoList= jobinfoService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, jobinfoList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询求职看了又看
		 * @param modelMap
		 * @param session
		 * @param pageNum
		 * @param type
		 * @return
		 * @throws ParseException
		 */
		@ResponseBody
		@RequestMapping("webgetAlikeJobinfo")
		public String webgetAlikeJobinfo(ModelMap modelMap,HttpSession session,Integer pageNum,Integer type) throws ParseException {
			if (type!=null) {
				String sql="SELECT id  from `jobinfo` ORDER BY RAND() LIMIT "+pageNum;
				List<Map<String, Object>> maps=jobinfoService.getBySQL(sql, null);
				if (maps!=null&&maps.size()>0) {
					List<Long> ids=new ArrayList<Long>();
					for (Map<String, Object> map : maps) {
						if (map.get("id")!=null) {
							BigInteger bid=(BigInteger) map.get("id");
							ids.add(bid.longValue());
						}
					}
					Map<String, Object> params=new HashMap<String, Object>();
					params.put("id", ids);
					List<Jobinfo> jobinfoList= jobinfoService.getByParams(params);
					modelMap.put(CommonInfoUtil.jSONOBJECTLIST, jobinfoList);		//jsonObjectList json对象集合
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);	
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.NULLDATA);	//0
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.PARAMERROR);	//-1
			}
			return JsonUtil.toJson(modelMap);
		}
}
