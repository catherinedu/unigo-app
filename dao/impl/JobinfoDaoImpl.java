package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.JobinfoDao;
import com.xjt.entity.Jobinfo;

@Repository
public class JobinfoDaoImpl extends BaseDaoImpl<Jobinfo> implements JobinfoDao{

    @Override    protected Class<Jobinfo> getEntityClass() {
       // TODO Auto-generated method stub
		return Jobinfo.class;
	 }

}