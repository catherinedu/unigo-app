package com.xjt.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.mysql.jdbc.log.Log;
import com.xjt.entity.User;
import com.xjt.util.JsonUtil;
import com.xjt.util.LogUtil;


@ServerEndpoint(value="/echo")
public class EndPointServer {
	
	public static  List<EndPointServer>  sessionList= new ArrayList<EndPointServer>();
	private Session  session ;
	private Long userId;
	
	 @OnOpen
	 public void onOpen(Session session,EndpointConfig config) throws IOException {
		//HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		this.session = session;
		sessionList.add(this);
		this.session.getBasicRemote().sendText(JsonUtil.toJson(new PushMsg(0,session.getId())));
	}
	 
	 @OnMessage
	 public void onMessage(Session session,String message) {
	 }

	 @OnError
	 public void onError(Throwable t) {
		 //以下代码省略...
		 LogUtil.Info("onError="+t);
	 }
	 
	 @OnClose
	 public void onClose(Session session, CloseReason reason) {
		 //以下代码省略...
		 sessionList.remove(this);
		 //System.out.println(userId+"退出链接");
	 }

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	} 
	 
	 
	 
	 
}
