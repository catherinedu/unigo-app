package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.UserDao;
import com.xjt.entity.User;

@Repository
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao{

    @Override    protected Class<User> getEntityClass() {
       // TODO Auto-generated method stub
		return User.class;
	 }

}