package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.ActivityarticleDao;
import com.xjt.entity.Activityarticle;

@Repository
public class ActivityarticleDaoImpl extends BaseDaoImpl<Activityarticle> implements ActivityarticleDao{

    @Override    protected Class<Activityarticle> getEntityClass() {
       // TODO Auto-generated method stub
		return Activityarticle.class;
	 }

}