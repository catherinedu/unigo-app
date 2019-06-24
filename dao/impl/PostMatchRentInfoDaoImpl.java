package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.PostMatchRentInfoDao;
import com.xjt.entity.PostMatchRentInfo;

@Repository
public class PostMatchRentInfoDaoImpl extends BaseDaoImpl<PostMatchRentInfo> implements PostMatchRentInfoDao{

    @Override    protected Class<PostMatchRentInfo> getEntityClass() {
       // TODO Auto-generated method stub
		return PostMatchRentInfo.class;
	 }

}