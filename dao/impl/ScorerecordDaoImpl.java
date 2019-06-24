package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.ScorerecordDao;
import com.xjt.entity.Scorerecord;

@Repository
public class ScorerecordDaoImpl extends BaseDaoImpl<Scorerecord> implements ScorerecordDao{

    @Override    protected Class<Scorerecord> getEntityClass() {
       // TODO Auto-generated method stub
		return Scorerecord.class;
	 }

}