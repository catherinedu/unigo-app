package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.IdleinfoDao;
import com.xjt.entity.Idleinfo;

@Repository
public class IdleinfoDaoImpl extends BaseDaoImpl<Idleinfo> implements IdleinfoDao{

    @Override    protected Class<Idleinfo> getEntityClass() {
       // TODO Auto-generated method stub
		return Idleinfo.class;
	 }

}