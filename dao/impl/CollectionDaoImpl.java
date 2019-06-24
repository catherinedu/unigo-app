package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.CollectionDao;
import com.xjt.entity.Collection;

@Repository
public class CollectionDaoImpl extends BaseDaoImpl<Collection> implements CollectionDao{

    @Override    protected Class<Collection> getEntityClass() {
       // TODO Auto-generated method stub
		return Collection.class;
	 }

}