package com.xjt.dao;

import java.util.List;
import java.util.Map;

import com.xjt.entity.Usersession;

public interface UsersessionDao extends BaseDao<Usersession>{

	public List<Usersession> getByPrams2(Long userId, Map<String, Object> sortPram);
}