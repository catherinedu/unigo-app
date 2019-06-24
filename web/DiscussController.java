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
import com.xjt.service.DiscussService;
import com.xjt.service.IdleinfoService;
import com.xjt.service.JobinfoService;
import com.xjt.service.RentinfoService;
import com.xjt.service.ScorerecordService;
import com.xjt.service.TypearticleService;
import com.xjt.service.UserService;
import com.xjt.entity.Activityarticle;
import com.xjt.entity.Discuss;
import com.xjt.entity.Idleinfo;
import com.xjt.entity.Jobinfo;
import com.xjt.entity.Rentinfo;
import com.xjt.entity.Scorerecord;
import com.xjt.entity.Typearticle;
import com.xjt.entity.User;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Discuss控制层
* @author Administrator
*/
@Controller
public class DiscussController{

		@Autowired
		private DiscussService discussService;
		@Autowired
		private UserService userService;
		@Autowired
		private  TypearticleService typearticleService;
		@Autowired
		private ActivityarticleService activityarticleService;
		@Autowired
		private IdleinfoService idleinfoService;
		@Autowired
		private RentinfoService rentinfoService;
		@Autowired
		private JobinfoService jobinfoService;
		@Autowired
		private ScorerecordService scorerecordService;
		
		/**
		 *添加或编辑Discuss
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditDiscuss")
		public  String addOrEditDiscuss(ModelMap modelMap,HttpSession session,Long id,Long userId,Long infoId,Integer type,Integer disType,Long repDisId,String content){
			if(type!=null&&content!=null&&infoId!=null){
				User user=(User) session.getAttribute("user");
				Discuss discuss=new Discuss();
				discuss.setGmtCreated(new Date());
				discuss.setUserId(user.getId());
				discuss.setType(type);
				discuss.setInfoId(infoId);
				discuss.setDisType(disType);
				discuss.setRepDisId(repDisId);
				discuss.setContent(content);
				discussService.saveOrUpdate(discuss);
				editDisNum(type, infoId);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 编辑积分
		 * @param userId
		 */
		private void editUserScore(Long userId){
			User user=userService.get(userId);
			Integer score=user.getScore();		//获取评论 添加10积分
			Integer giveScore=10;
			if (score==null||score<0) {
				score=0;
			}
			if (user.getCheckState()!=null&&user.getCheckState()==2) {//高级认证1.5倍积分
				giveScore=(int) (giveScore*1.5);
			}
			user.setScore(score+giveScore);
			userService.saveOrUpdate(user);
			Scorerecord scorerecord=new Scorerecord(user.getId(), "获得评论", giveScore, new Date());
			scorerecordService.save(scorerecord);
		}
		
		/**
		 * 编辑评论次数
		 * @param type
		 * @param infoId
		 */
		public void editDisNum(Integer type,Long infoId){
			if (type==3) { //订阅文章
				Typearticle typearticle=typearticleService.get(infoId);
				if (typearticle!=null) {
					Integer disNum=typearticle.getDiscussNum();
					if (disNum==null||disNum<0) {
						disNum=0;
					}
					typearticle.setDiscussNum(disNum+1);
					typearticleService.saveOrUpdate(typearticle);
				}
			}else if (type==4) {//活动文章
				Activityarticle activityarticle=activityarticleService.get(infoId);
				if (activityarticle!=null) {
					Integer disNum=activityarticle.getDiscussNum();
					if (disNum==null||disNum<0) {
						disNum=0;
					}
					activityarticle.setDiscussNum(disNum+1);
					activityarticleService.saveOrUpdate(activityarticle);
				}
			}else if (type==0) {//租房
				Rentinfo rentinfo=rentinfoService.get(infoId);
				if (rentinfo!=null) {
					Integer disNum=rentinfo.getDisNum();
					if (disNum==null||disNum<0) {
						disNum=0;
					}
					rentinfo.setDisNum(disNum+1);
					rentinfoService.saveOrUpdate(rentinfo);
					editUserScore(rentinfo.getUserId());
				}
			}else if (type==1) {//闲置
				Idleinfo idleinfo=idleinfoService.get(infoId);
				if (idleinfo!=null) {
					Integer disNum=idleinfo.getDisNum();
					if (disNum==null||disNum<0) {
						disNum=0;
					}
					idleinfo.setDisNum(disNum+1);
					idleinfoService.saveOrUpdate(idleinfo);
					editUserScore(idleinfo.getUserId());
				}
			}else if (type==2) {//求职
				Jobinfo jobinfo=jobinfoService.get(infoId);
				if (jobinfo!=null) {
					Integer disNum=jobinfo.getDisNum();
					if (disNum==null||disNum<0) {
						disNum=0;
					}
					jobinfo.setDisNum(disNum+1);
					jobinfoService.saveOrUpdate(jobinfo);
				}
			}
		}
		
		/**
		 *删除Discuss
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delDiscuss")
		public String delDiscuss(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Discuss discuss=discussService.get(id);
				if (discuss!=null) {
					discussService.delete(discuss);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Discuss
		*/
		@ResponseBody
		@RequestMapping("getDiscuss")
		public String getDiscuss(ModelMap modelMap,HttpSession session,Long infoId,Integer type,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=null;
			if (infoId!=null) {
				prams=new HashMap<String, Object>();
				prams.put("infoId", infoId);
			}
			if (type!=null) {
				if (prams==null) {
					prams=new HashMap<String, Object>();
				}
				prams.put("type", type);
			}
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
			Page page=discussService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Discuss> discussList= (List<Discuss>) page.getList();
			if (discussList!=null&&discussList.size()>0) {
				pageCount=discussService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				editDiscussUserInfo(discussList);
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, discussList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 编辑评论的用户信息
		 * @param discuss
		 */
		public void  editDiscussUserInfo(List<Discuss> discussList){
			User user=null;
			Date now=new Date();
			for (Discuss discuss : discussList) {
				if (discuss!=null&&discuss.getUserId()!=null) {
					user=userService.get(discuss.getUserId());
					if (user!=null) {
						if (user.getName()!=null) {
							discuss.setUserName(user.getName());
						}else {
							discuss.setUserName(user.getUid().toString());
						}
						discuss.setUserImg(user.getImg());
						discuss.setTimeStr(DateUtil.getDateLenthStr(discuss.getGmtCreated(), now));
					}
					if (discuss.getDisType()!=null&&discuss.getDisType()==1&&discuss.getRepDisId()!=null) {//回复
						Discuss repDiscuss=discussService.get(discuss.getRepDisId());
						if (repDiscuss!=null) {
							user=userService.get(repDiscuss.getUserId());
							if (user!=null) {
								discuss.setRepUserName(user.getName());
							}
						}
					}
				}
			}
		}
		
		/**
		 *根据id查询Discuss
		*/
		@ResponseBody
		@RequestMapping("getDiscussById")
		public String getDiscussById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Discuss discuss=discussService.get(id);
				if (discuss!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, discuss);//jsonObject	json对象
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
		 * 查询所有Discuss
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllDiscuss")
		public String getAllDiscuss(ModelMap modelMap,HttpSession session){
			List<Discuss> discussList= discussService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, discussList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
