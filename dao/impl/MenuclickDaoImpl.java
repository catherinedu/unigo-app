package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.MenuclickDao;
import com.xjt.entity.Menuclick;

@Repository
public class MenuclickDaoImpl extends BaseDaoImpl<Menuclick> implements MenuclickDao{

    @Override    protected Class<Menuclick> getEntityClass() {
       // TODO Auto-generated method stub
		return Menuclick.class;
	 }

}