package com.xjt.dao.impl;

import org.springframework.stereotype.Repository;

import com.xjt.dao.PostDataDao;
import com.xjt.entity.PostData;

@Repository
public class PostDataDaoImpl extends BaseDaoImpl<PostData> implements PostDataDao{

    @Override    protected Class<PostData> getEntityClass() {
       // TODO Auto-generated method stub
		return PostData.class;
	 }

}