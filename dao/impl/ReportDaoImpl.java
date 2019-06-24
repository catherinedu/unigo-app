package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.ReportDao;
import com.xjt.entity.Report;

@Repository
public class ReportDaoImpl extends BaseDaoImpl<Report> implements ReportDao{

    @Override    protected Class<Report> getEntityClass() {
       // TODO Auto-generated method stub
		return Report.class;
	 }

}