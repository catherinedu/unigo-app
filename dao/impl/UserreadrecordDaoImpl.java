package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.UserreadrecordDao;
import com.xjt.entity.Userreadrecord;

@Repository
public class UserreadrecordDaoImpl extends BaseDaoImpl<Userreadrecord> implements UserreadrecordDao{

    @Override    protected Class<Userreadrecord> getEntityClass() {
       // TODO Auto-generated method stub
		return Userreadrecord.class;
	 }

}