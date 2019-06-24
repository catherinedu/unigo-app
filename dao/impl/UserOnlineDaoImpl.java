package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.UserOnlineDao;
import com.xjt.entity.UserOnline;

@Repository
public class UserOnlineDaoImpl extends BaseDaoImpl<UserOnline> implements UserOnlineDao{

    @Override    protected Class<UserOnline> getEntityClass() {
       // TODO Auto-generated method stub
		return UserOnline.class;
	 }

}