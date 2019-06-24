package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.UsersubscriptionDao;
import com.xjt.entity.Usersubscription;

@Repository
public class UsersubscriptionDaoImpl extends BaseDaoImpl<Usersubscription> implements UsersubscriptionDao{

    @Override    protected Class<Usersubscription> getEntityClass() {
       // TODO Auto-generated method stub
		return Usersubscription.class;
	 }

}