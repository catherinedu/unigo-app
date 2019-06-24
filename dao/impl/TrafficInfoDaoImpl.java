package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.TrafficInfoDao;
import com.xjt.entity.TrafficInfo;

@Repository
public class TrafficInfoDaoImpl extends BaseDaoImpl<TrafficInfo> implements TrafficInfoDao{

    @Override    protected Class<TrafficInfo> getEntityClass() {
       // TODO Auto-generated method stub
		return TrafficInfo.class;
	 }

}