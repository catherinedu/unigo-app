package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.SysuserDao;
import com.xjt.entity.Sysuser;

@Repository
public class SysuserDaoImpl extends BaseDaoImpl<Sysuser> implements SysuserDao{

    @Override    protected Class<Sysuser> getEntityClass() {
       // TODO Auto-generated method stub
		return Sysuser.class;
	 }

}