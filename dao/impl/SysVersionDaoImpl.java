package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.SysVersionDao;
import com.xjt.entity.SysVersion;

@Repository
public class SysVersionDaoImpl extends BaseDaoImpl<SysVersion> implements SysVersionDao{

    @Override    protected Class<SysVersion> getEntityClass() {
       // TODO Auto-generated method stub
		return SysVersion.class;
	 }

}