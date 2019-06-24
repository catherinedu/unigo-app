package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.DiscussDao;
import com.xjt.entity.Discuss;

@Repository
public class DiscussDaoImpl extends BaseDaoImpl<Discuss> implements DiscussDao{

    @Override    protected Class<Discuss> getEntityClass() {
       // TODO Auto-generated method stub
		return Discuss.class;
	 }

}