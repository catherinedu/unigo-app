package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.SysmsgDao;
import com.xjt.entity.Sysmsg;

@Repository
public class SysmsgDaoImpl extends BaseDaoImpl<Sysmsg> implements SysmsgDao{

    @Override    protected Class<Sysmsg> getEntityClass() {
       // TODO Auto-generated method stub
		return Sysmsg.class;
	 }

}