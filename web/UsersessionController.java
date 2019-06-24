package com.xjt.web;

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

import com.xjt.service.ChatmsgService;
import com.xjt.service.SysmsgService;
import com.xjt.service.UserService;
import com.xjt.service.UsersessionService;
import com.xjt.entity.Chatmsg;
import com.xjt.entity.Sysmsg;
import com.xjt.entity.User;
import com.xjt.entity.Usersession;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Usersession控制层
* @author Administrator
*/
@Controller
public class UsersessionController{

		@Autowired
		private UsersessionService usersessionService;
		@Autowired
		private UserService userService;
		@Autowired
		private ChatmsgService chatmsgService;
		@Autowired
		private SysmsgService sysmsgService;
		/**
		 *添加或编辑Usersession
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditUsersession")
		public String addOrEditUsersession(ModelMap modelMap,HttpSession session,Long id,Long userId,Long toUserId){
			User user=(User) session.getAttribute("user");
			userId=user.getId();
			if (toUserId!=null&&!userId.equals(toUserId)) {
				Usersession usersession=checkUsersession(userId, toUserId);
				if (usersession==null) {
					usersession=new Usersession(userId, toUserId, new Date(),0,0);
				}else {
					usersession.setGmtCreated(new Date());//已存在刷新时间
				}
				usersessionService.saveOrUpdate(usersession);
				modelMap.put("userSessionId", usersession.getId());//用户sessionId;
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 检查房间是否存在
		 * @param userId
		 * @param toUserId
		 * @return
		 */
		public Usersession checkUsersession(Long userId,Long toUserId){
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("userId", userId);
			params.put("toUserId", toUserId);
			Usersession usersession=usersessionService.getByParam(params);
			if (usersession!=null) {
				if (usersession.getUserDel()!=null&&usersession.getUserDel()==1) {//查询自己是否删除了会话  是则修改删除状态
					usersession.setUserDel(0);
					usersessionService.saveOrUpdate(usersession);
				}
				return usersession;
			}else {
				params=new HashMap<String, Object>();
				params.put("userId", toUserId);
				params.put("toUserId", userId);
				usersession=usersessionService.getByParam(params);
				if (usersession!=null) {
					if (usersession.getToUserIdDel()!=null&&usersession.getToUserIdDel()==1) {//查询自己是否删除了会话  是则修改删除状态
						usersession.setToUserIdDel(0);
						usersessionService.saveOrUpdate(usersession);
					}
					return usersession;
				}
			}
			return null;
		}

		/**
		 *删除Usersession
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/delUsersession")
		public String delUsersession(ModelMap modelMap,HttpSession session,Long sessionId){
			User user=(User) session.getAttribute("user");
			if (sessionId!=null) {
				Usersession usersession=usersessionService.get(sessionId);
				if (usersession!=null&&(usersession.getUserId().equals(user.getId())||usersession.getToUserId().equals(user.getId()))) {
					if (usersession.getUserId().equals(user.getId())) {
						usersession.setUserDel(1);
					}else if (usersession.getToUserId().equals(user.getId())){
						usersession.setToUserIdDel(1);
					}
					usersessionService.saveOrUpdate(usersession);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NOPERMISSIONS);//msg=-6  无权限    
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 *删除所有Usersession
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/delAllUsersession")
		public String delAllUsersession(ModelMap modelMap,HttpSession session){
			User user=(User) session.getAttribute("user");
			List<Usersession> usersessionList= usersessionService.getByPrams2(user.getId(), null);
			if (usersessionList!=null&&usersessionList.size()>0) {
				for (Usersession usersession : usersessionList) {
					if (usersession.getUserId().equals(user.getId())) {
						usersession.setUserDel(1);
					}else if (usersession.getToUserId().equals(user.getId())){
						usersession.setToUserIdDel(1);
					}
				}
				usersessionService.saveOrUpdateAll(usersessionList);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0
			}
				
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Usersession
		*/
		@ResponseBody
		@RequestMapping("getUsersession")
		public String getUsersession(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=usersessionService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Usersession> usersessionList= (List<Usersession>) page.getList();
			if (usersessionList!=null&&usersessionList.size()>0) {
				pageCount=usersessionService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, usersessionList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询Usersession
		*/
		@ResponseBody
		@RequestMapping("getUsersessionById")
		public String getUsersessionById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Usersession usersession=usersessionService.get(id);
				if (usersession!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, usersession);//jsonObject	json对象
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
		 * 查询所有Usersession
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/getAllUsersession")
		public String getAllUsersession(ModelMap modelMap,HttpSession session){
			User user=(User) session.getAttribute("user");
			Map<String, Object> sortPram=new HashMap<String, Object>();
			sortPram.put("desc", "gmtCreated");
			List<Usersession> usersessionList= usersessionService.getByPrams2(user.getId(), sortPram);
			editUsersessionInfo(usersessionList, user.getId());
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("userId", user.getId());
			Sysmsg sysmsg=sysmsgService.getByParam(params, sortPram);
			if (sysmsg!=null) {
				modelMap.put("sysmsg", sysmsg);//返回系统消息
			}
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, usersessionList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);	
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 编辑会话信息
		 * @param usersessionList
		 * @param userId
		 */
		private void editUsersessionInfo(List<Usersession> usersessionList,Long userId){
			User user=null;
			Chatmsg chatmsg=null;
			Map<String, Object> params=null;
			Map<String, Object> sortPram=null;
			for (Usersession usersession : usersessionList) {
				params=new HashMap<String, Object>();
				params.put("sessionId", usersession.getId());
				sortPram=new HashMap<String, Object>();
				sortPram.put("desc", "id");
				chatmsg=chatmsgService.getByParam(params, sortPram);	//查询房间最后一次会话消息
				if (chatmsg!=null) {
					usersession.setLastContent(chatmsg.getContent());
				}
				if (userId.equals(usersession.getUserId())) {		//查询另一个用户的信息
					user=userService.get(usersession.getToUserId());
				}else if (userId.equals(usersession.getToUserId())){
					user=userService.get(usersession.getUserId());
				}
				if (user!=null) {									
					usersession.setUserName(user.getName());
					usersession.setUserImg(user.getImg());
					params.put("userId", user.getId());		//查询另一个用户发出来的 当前用户未读信息数量
					params.put("readState", 0);
					usersession.setNoReadNum(chatmsgService.getCount(params));
				}
			}
		}
		
		/**
		 * 查询某个会话（房间），当前用户未读消息数量
		 * @param modelMap
		 * @param session
		 * @param id
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/getUsersessionNoReadNum")
		public String getUsersessionNoReadNum(ModelMap modelMap,HttpSession session,Long sessionId){
			User user=(User) session.getAttribute("user");
			if (sessionId!=null) {
				Usersession usersession=usersessionService.get(sessionId);
				if (usersession!=null) {
					Map<String, Object>params=new HashMap<String, Object>();//查询另一个用户发出来的 当前用户未读信息数量
					if (user.getId().equals(usersession.getUserId())) {
						params.put("userId", usersession.getToUserId());		
					}else if (user.getId().equals(usersession.getToUserId())){
						params.put("userId", usersession.getUserId());
					}
					params.put("readState", 0);
					modelMap.put("noReadNum", chatmsgService.getCount(params));
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				}
			}else{
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		
		/**
		 * 编辑某个会话（房间），当前用户未读消息状态
		 * @param modelMap
		 * @param session
		 * @param id
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/editUsersessionNoRead")
		public String editUsersessionNoRead(ModelMap modelMap,HttpSession session,Long sessionId){
			User user=(User) session.getAttribute("user");
			if (sessionId!=null) {
				Usersession usersession=usersessionService.get(sessionId);
				if (usersession!=null) {
					Map<String, Object>params=new HashMap<String, Object>();//查询另一个用户发出来的 当前用户未读信息数量
					if (user.getId().equals(usersession.getUserId())) {
						params.put("userId", usersession.getToUserId());		
					}else if (user.getId().equals(usersession.getToUserId())){
						params.put("userId", usersession.getUserId());
					}
					params.put("readState", 0);
					List<Chatmsg> chatmsgs =chatmsgService.getByParams(params);
					if (chatmsgs!=null&&chatmsgs.size()>0) {
						for (Chatmsg chatmsg : chatmsgs) {
							chatmsg.setReadState(1);
						}
						chatmsgService.saveOrUpdateAll(chatmsgs);
					}
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				}
			}else{
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
}
