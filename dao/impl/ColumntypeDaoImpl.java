package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.ColumntypeDao;
import com.xjt.entity.Columntype;

@Repository
public class ColumntypeDaoImpl extends BaseDaoImpl<Columntype> implements ColumntypeDao{

    @Override    protected Class<Columntype> getEntityClass() {
       // TODO Auto-generated method stub
		return Columntype.class;
	 }

}