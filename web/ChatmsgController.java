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

import com.sun.swing.internal.plaf.basic.resources.basic;
import com.xjt.service.ChatmsgService;
import com.xjt.service.UserService;
import com.xjt.service.UsersessionService;
import com.xjt.chat.PushMsg;
import com.xjt.chat.PushUtil;
import com.xjt.entity.Chatmsg;
import com.xjt.entity.User;
import com.xjt.entity.Usersession;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Chatmsg控制层
* @author Administrator
*/
@Controller
public class ChatmsgController{

		@Autowired
		private ChatmsgService chatmsgService;
		@Autowired
		private UsersessionService usersessionService;
		@Autowired
		private UserService userService;
		
		/**
		 *添加或编辑Chatmsg
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditChatmsg")
		public String addOrEditChatmsg(ModelMap modelMap,HttpSession session,Long sessionId,Long userId,String content,Integer msgType){
			User user=(User) session.getAttribute("user");
			userId=user.getId();
			if (sessionId!=null&&userId!=null&&content!=null&&msgType!=null) {
				Chatmsg chatmsg=new Chatmsg(sessionId, userId,msgType,content,0, new Date());
				chatmsgService.saveOrUpdate(chatmsg);
				Long otherUserId=getSessionOtherUserId(sessionId, userId);
				if (otherUserId!=null) {
					PushMsg pushMsg=new PushMsg(1, null,sessionId, otherUserId, user.getName(), content,msgType);
					PushUtil.sendMsgToUser(otherUserId, pushMsg);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1
			}
			return JsonUtil.toJson(modelMap);
		}

		
		/**
		 * 查询同一个会话房间另一个用户id
		 * @param sessionId
		 * @param userId
		 * @return
		 */
		private Long getSessionOtherUserId(Long sessionId,Long userId){
			Usersession usersession=usersessionService.get(sessionId);
			if (usersession!=null) {
				if (usersession.getUserId().equals(userId)) {
					if (usersession.getToUserIdDel()!=null&&usersession.getToUserIdDel()==1) {//判断另一个用户是否删除了会话  是则修改删除状态
						usersession.setToUserIdDel(0);
						usersessionService.saveOrUpdate(usersession);
					}
					return usersession.getToUserId();
				}else if(usersession.getToUserId().equals(userId)){
					if (usersession.getUserDel()!=null&&usersession.getUserDel()==1) {//判断另一个用户是否删除了会话  是则修改删除状态
						usersession.setUserDel(0);
						usersessionService.saveOrUpdate(usersession);
					}
					return usersession.getUserId();
				}
			}
			return null;
		}

		/**
		 *删除Chatmsg
		*/
		@ResponseBody
		@RequestMapping("delChatmsg")
		public String delChatmsg(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Chatmsg chatmsg=chatmsgService.get(id);
				if (chatmsg!=null) {
					chatmsgService.delete(chatmsg);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Chatmsg
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/getChatmsg")
		public String getChatmsg(ModelMap modelMap,HttpSession session,Long sessionId,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (sessionId!=null) {
				User user=(User) session.getAttribute("user");
				Usersession usersession=checkUserPms(user.getId(), sessionId);
				if (usersession!=null) {
					if (pageIndex==null) {
						pageIndex=1;
					}
					if (pageNum==null) {
						pageNum=10;
					}
					Map<String, Object> prams=new HashMap<String, Object>();
					prams.put("sessionId", sessionId);
					Map<String, Object> sortPram=new HashMap<String, Object>();
					sortPram.put("desc", "id");
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
					Page page=chatmsgService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
					List<Chatmsg> chatmsgList= (List<Chatmsg>) page.getList();
					if (chatmsgList!=null&&chatmsgList.size()>0) {
						pageCount=chatmsgService.getCount(prams, searchPram, startTime, endTime);
						modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
						modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
						modelMap.put(CommonInfoUtil.jSONOBJECTLIST, chatmsgList);		//jsonObjectList json对象集合
						editReadState(chatmsgList,user.getId());
						modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
						modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
					}else {
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
					}
					modelMap.put("userImg",usersession.getUserImg());
					modelMap.put("userName",usersession.getUserName());
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NOPERMISSIONS);//-6
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 编辑阅读状态
		 * @param chatmsgList
		 */
		public void editReadState(List<Chatmsg> chatmsgList,Long userId){
			for (Chatmsg chatmsg : chatmsgList) {
				if (chatmsg.getReadState()!=null&&chatmsg.getReadState()==0&&!chatmsg.getUserId().equals(userId)) {//判断是否是对方发的消息 并且我没查看 
					chatmsg.setReadState(1);
					chatmsgService.saveOrUpdate(chatmsg);
				}
			}
		}

		/**
		 * 查询用户信息
		 * @param chatmsgList
		 */
		private void getchatmsgListUserInfo(List<Chatmsg> chatmsgList){
			List<Long> userIds=new ArrayList<Long>();
			for (Chatmsg chatmsg : chatmsgList) {
				userIds.add(chatmsg.getUserId());
			}
			if (userIds.size()>0) {
				List<User> userList=userService.getByIds(userIds);
				for (User user : userList) {
					for (Chatmsg chatmsg : chatmsgList) {
						if (chatmsg.getUserId().equals(user.getId())) {
							chatmsg.setUserName(user.getName());
							chatmsg.setUserImg(user.getImg());
							break;
						}
					}
				}
			}
		}
		
		/**
		 * 检查房间权限
		 * @param userId
		 * @param sessionId
		 * @return
		 */
		public Usersession checkUserPms(Long userId,Long sessionId){
			Usersession usersession=usersessionService.get(sessionId);
			if (usersession!=null) {
				if (usersession.getUserId().equals(userId)||usersession.getToUserId().equals(userId)) {
					User user=null;
					if (usersession.getUserId().equals(userId)){
						user=userService.get(usersession.getToUserId());
					}else if (usersession.getToUserId().equals(userId)) {
						user=userService.get(usersession.getUserId());
					}
					if (user!=null) {
						usersession.setUserName(user.getName());
						usersession.setUserImg(user.getImg());
					}
					return usersession;
				}
			}
			return null;
		}
		
		/**
		 *根据id查询Chatmsg
		*/
		@ResponseBody
		@RequestMapping("getChatmsgById")
		public String getChatmsgById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Chatmsg chatmsg=chatmsgService.get(id);
				if (chatmsg!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, chatmsg);//jsonObject	json对象
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
		 * 查询所有Chatmsg
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllChatmsg")
		public String getAllChatmsg(ModelMap modelMap,HttpSession session){
			List<Chatmsg> chatmsgList= chatmsgService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, chatmsgList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
