package com.xjt.chat;

/**
 * 推送消息
 * @author Administrator
 *
 */
public class PushMsg {
	
	private Integer type; 		//消息类型  0初始化 1聊天 2系统消息 3求职订阅新消息提醒  4租房订阅新消息提醒   5闲置订阅新消息提醒  6登录下线通知
	private String sessionId;	//会话id
	private Long userSessionId;	//聊天时的会话id(房间id)
	private Long userId;		//说话的用户id
	private String uerName;		//用户名
	private String content;		//内容
	private Integer msgType;	//消息类型 0文本 1图片 2语音 3视频
	public PushMsg() {
		super();
	}
	public PushMsg(Integer type, String sessionId) {
		super();
		this.type = type;
		this.sessionId = sessionId;
	}
	
	public PushMsg(Integer type, String sessionId,String content,Integer msgType) {
		super();
		this.type = type;
		this.sessionId = sessionId;
		this.content=content;
		this.msgType=msgType;
	}
	
	public PushMsg(Integer type, String sessionId,Long userSessionId, Long userId, String uerName,
			String content,Integer msgType) {
		super();
		this.type = type;
		this.sessionId = sessionId;
		this.userSessionId=userSessionId;
		this.userId = userId;
		this.uerName = uerName;
		this.content = content;
		this.msgType=msgType;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUerName() {
		return uerName;
	}
	public void setUerName(String uerName) {
		this.uerName = uerName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getUserSessionId() {
		return userSessionId;
	}
	public void setUserSessionId(Long userSessionId) {
		this.userSessionId = userSessionId;
	}
	public Integer getMsgType() {
		return msgType;
	}
	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}
	
	
}
