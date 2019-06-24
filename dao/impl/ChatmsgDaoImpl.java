package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.ChatmsgDao;
import com.xjt.entity.Chatmsg;

@Repository
public class ChatmsgDaoImpl extends BaseDaoImpl<Chatmsg> implements ChatmsgDao{

    @Override    protected Class<Chatmsg> getEntityClass() {
       // TODO Auto-generated method stub
		return Chatmsg.class;
	 }

}