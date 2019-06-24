package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.RentinfoDao;
import com.xjt.entity.Rentinfo;

@Repository
public class RentinfoDaoImpl extends BaseDaoImpl<Rentinfo> implements RentinfoDao{

    @Override    protected Class<Rentinfo> getEntityClass() {
       // TODO Auto-generated method stub
		return Rentinfo.class;
	 }

}