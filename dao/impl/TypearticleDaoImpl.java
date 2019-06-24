package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.TypearticleDao;
import com.xjt.entity.Typearticle;

@Repository
public class TypearticleDaoImpl extends BaseDaoImpl<Typearticle> implements TypearticleDao{

    @Override    protected Class<Typearticle> getEntityClass() {
       // TODO Auto-generated method stub
		return Typearticle.class;
	 }

}