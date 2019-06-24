package com.xjt.webapp;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjt.chat.EndPointServer;
import com.xjt.chat.PushMsg;
import com.xjt.chat.PushUtil;
import com.xjt.entity.Idleinfo;
import com.xjt.entity.Usersubscription;
import com.xjt.service.IdleinfoService;
import com.xjt.service.UsersubscriptionService;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.LogUtil;
import com.xjt.util.Page;

/**
 * 推送控制层
 * @author Administrator
 *
 */
@Controller
public class PushController {
	
	@Autowired
	private UsersubscriptionService usersubscriptionService;
	@Autowired
	private IdleinfoService idleinfoService;
	 /**
	  * 查询所有在线人员
	  * @param modelMap
	  * @param session
	  * @return
	  */
	 @ResponseBody
	 @RequestMapping("getAllOnline")
	 public String getAllOnline(ModelMap modelMap,HttpSession session){
		 List<Map<String, String>> sessionIds=new ArrayList<Map<String, String>>();
		 Map<String, String> map=null;
		 for (EndPointServer endPointServer : EndPointServer.sessionList) {
			 map=new HashMap<String, String>();
			 if (endPointServer.getUserId()!=null) {
				 map.put(endPointServer.getSession().getId(), endPointServer.getUserId().toString());
			 }else {
				 map.put(endPointServer.getSession().getId(), "");
			 }
			 sessionIds.add(map);
		}
		 modelMap.put("ids", sessionIds);
		 return JsonUtil.toJson(modelMap);
	 }
	 
	 /**
	  * 向用户发送消息
	  * @param modelMap
	  * @param session
	  * @param sid
	  * @param msg
	  * @return
	  */
	 @ResponseBody
	 @RequestMapping("toSendMsg")
	 public String toSendMsg(ModelMap modelMap,HttpSession session,Long userId,String msg,Integer msgType){
		 if (userId!=null&&msg!=null&&msg.length()>0&&msgType!=null) {
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
				 System.out.println(endPointServer.getSession());
				 if (endPointServer.getUserId()!=null&&userId.equals(endPointServer.getUserId())) {
					 	try {
						    PushMsg pushMsg=new PushMsg(0,endPointServer.getSession().getId(),msg,msgType);
						    endPointServer.getSession().getBasicRemote().sendText(JsonUtil.toJson(pushMsg));
							modelMap.put("msg", 1);//发送成功
						} catch (IOException e) {
							// TODO Auto-generated catch block
							modelMap.put("msg", 0);//发送失败
							e.printStackTrace();
						}
				 }
					
			}
		 }else {
			 modelMap.put("msg", -1);//参数错误
		}
		
		 return JsonUtil.toJson(modelMap);
	 }
	 
	 /**
	  * 广播发送消息
	  * @param modelMap
	  * @param session
	  * @param msg
	  * @return
	  */
	 @ResponseBody
	 @RequestMapping("toBroadCastMsg")
	 public String toBroadCastMsg(ModelMap modelMap,HttpSession session,String msg,Integer msgType){
		 if (msg!=null&&msg.length()>0&&msgType!=null) {
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
				 try {
					 PushMsg pushMsg=new PushMsg(0,endPointServer.getSession().getId(),msg,msgType);
					 	endPointServer.getSession().getBasicRemote().sendText(JsonUtil.toJson(pushMsg));
						modelMap.put("msg", 1);//发送成功
					} catch (IOException e) {
						// TODO Auto-generated catch block
						modelMap.put("msg", 0);//发送失败
						e.printStackTrace();
					}	 		
			}
		 }else {
			 modelMap.put("msg", -1);//参数错误
		}
		
		 return JsonUtil.toJson(modelMap);
	 }
	 
	 /**
	  * 每晚10点定时发送闲置订阅提醒
	 * @throws ParseException 
	  */
	  public void  sendSubMsg() throws ParseException{
		  LogUtil.Info("整点定时方法执行");
		  Map<String, Object> params=new HashMap<String, Object>();
		  Map<String, Object> startTime=new HashMap<String, Object>();
		  Map<String, Object> endTime=new HashMap<String, Object>();
		  //下架30天后还在上架的闲置信息  
		  params.put("showState", 0);
		  endTime.put("gmtCreated",  DateUtil.getCalendarByAdd(new Date(), Calendar.DAY_OF_MONTH, -30));
		  List<Idleinfo> idleinfos=(List<Idleinfo>) idleinfoService.getByPrams(params, null, null, null, null, startTime, endTime).getList();
		  if (idleinfos!=null&&idleinfos.size()>0) {
			 for (Idleinfo idleinfo : idleinfos) {
				 idleinfo.setShowState(1);
			 }
			 idleinfoService.saveOrUpdateAll(idleinfos);
		  }
		  
		  //查询今天闲置发布数量 发送订阅提醒消息
		  String today=DateUtil.simpdfyMd.format(new Date());
		  startTime=new HashMap<String, Object>();
		  startTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(today+" 00:00:00"));
		  endTime=new HashMap<String, Object>();
		  endTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(today+" 23:59:59"));
		  Integer count=idleinfoService.getCountX(null, null, startTime, endTime);
		  String msg="";
		  if(count==0){
			  msg="有人发布了闲置好物，还不来看看";
		  }else {
			  msg="有"+count+"人发布了闲置好物，还不来看看";
		  }
		  PushMsg pushMsg=new PushMsg(5, null, null, null, null,msg, 0);
		  params=new HashMap<String, Object>();
		  params.put("subType", 1);
		  usersubscriptionService.getByParams(params);
		  List<Usersubscription> usersubscriptions =  usersubscriptionService.getByParams(params); //查询订阅的用户
			if (usersubscriptions!=null&&usersubscriptions.size()>0) {
				for (Usersubscription usersubscription : usersubscriptions) {
					PushUtil.sendMsgToUser(usersubscription.getUserId(), pushMsg);
				}
			}
	  }
	
}
