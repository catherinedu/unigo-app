package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.UserMatchRentDao;
import com.xjt.entity.UserMatchRent;

@Repository
public class UserMatchRentDaoImpl extends BaseDaoImpl<UserMatchRent> implements UserMatchRentDao{

    @Override    protected Class<UserMatchRent> getEntityClass() {
       // TODO Auto-generated method stub
		return UserMatchRent.class;
	 }

}