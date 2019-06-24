package com.xjt.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xjt.util.JsonUtil;

/**
 * 推送工具
 * @author Administrator
 *
 */
public class PushUtil {
     
	 /**
	  * 向用户发送消息
	  * @param userId
	  * @param msg
	  * @return
	  */
	 public static boolean sendMsgToUser(Long userId,PushMsg msg){
		 if (userId!=null&&msg!=null) {
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
				 if (endPointServer.getUserId()!=null&&userId.equals(endPointServer.getUserId())) {
					 try {
						 //msg.setSessionId(endPointServer.getSession().getId());
						 endPointServer.getSession().getBasicRemote().sendText(JsonUtil.toJson(msg));
						 return true;
					 } catch (IOException e) {
						  e.printStackTrace();
						  return false;
					 }    
				 }
					
			}
		 }
		 return false;
	 }
	 
	 /**
	  * 向SID发送消息
	  * @param userId
	  * @param msg
	  * @return
	  */
	 public static boolean sendMsgToSid(String sessionId,String msg){
		 if (sessionId!=null&&sessionId.length()>0&&msg!=null&&msg.length()>0) {
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
				 if (sessionId.equals(endPointServer.getSession().getId())) {
					 try {
						 endPointServer.getSession().getBasicRemote().sendText(msg);
					 } catch (IOException e) {
						  e.printStackTrace();
						  return false;
					 }    
				 }
					
			}
		 }
		 return true;
	 }
	 
	 /**
	  * 广播发送消息
	  * @param msg
	  * @return
	  */
	 public static boolean toBroadCastMsg(String msg){
		 if (msg!=null&&msg.length()>0) {
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
					 if (endPointServer.getUserId()!=null) {
						 try {
							 endPointServer.getSession().getBasicRemote().sendText(msg);
						 } catch (IOException e) {
								e.printStackTrace();
								//return false;
						 }	 
					 }	
			}
		 }
		 return true;
	 }
	 
	 /**
	  * 用户关联session
	  * @param sessionId
	  * @param userId
	  * @return
	  */
	 public static boolean userIdConnectSid(String sessionId,Long userId){
		 if (sessionId!=null&&sessionId.length()>0&&userId!=null) {
			 clearByUserId(userId);
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
				 if (sessionId.equals(endPointServer.getSession().getId())) {
					  endPointServer.setUserId(userId); 
					  return true;
				 }		
			}
		 }
		 return false;
	 }
	 
	 /**
	  * 用户退出关联session
	  * @param sessionId
	  * @param userId
	  * @return
	  */
	 public static boolean userIdUnConnectSid(Long userId){
		 if (userId!=null) {
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
				 if (endPointServer.getUserId()!=null&&userId.equals(endPointServer.getUserId())) {
					 endPointServer.setUserId(null);; 
					  return true;
				}
			}
		 }
		 return false;
	 }
	 /**
	  * 清除已存在的记录
	  * @param userId
	  */
	 public static void clearByUserId(Long userId){
		 if (userId!=null) {
			 List<EndPointServer> moveList=new ArrayList<EndPointServer>();
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
				 if (endPointServer.getUserId()!=null&&endPointServer.getUserId().equals(userId)) {
					 moveList.add(endPointServer);
				}
			 }
			 if (moveList!=null&&moveList.size()>0) {
				 EndPointServer.sessionList.removeAll(moveList);
			}
		}
		 
	 }
	 
	 /**
	  * 检查用户是否在线
	  * @param sessionId
	  * @param userId
	  * @return
	  */
	 public static String checkUserConnectState(Long userId){
		 if (userId!=null) {
			 for (EndPointServer endPointServer : EndPointServer.sessionList) {
				 if (endPointServer.getUserId()!=null&&endPointServer.getUserId().equals(userId)) {
					  return endPointServer.getSession().getId();
				 }		
			}
		 }
		 return null;
	 }
	 
	
}
