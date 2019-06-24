package com.xjt.dao;

import java.util.List;
import java.util.Map;

import com.xjt.util.Page;

public interface BaseDao<T> {
	public Long save(T t);
	public Integer saveInt(T t);
	public void saveOrUpdate(T t);
	public void saveOrUpdateAll(List<T> t);
	public void deleteAll(List<T> t);
	public T get(Long id);
	public T get(Integer id);
	public T getObject(Map<String, Object> prams);
	public T getObject(Map<String, Object> prams,Map<String, Object> sortPram);
	public List<T> getObjectList(Map<String, Object> prams);
	public List<T> getByPrams(Map<String, Object> prams, Map<String, Object> sortPram,Map<String, Object> searchPram);
	public Integer getCount(Map<String, Object> prams);
	public int getByPrams(Map<String, Object> prams, Map<String, Object> searchPram); 
	public Page getByPrams(Map<String, Object> prams, Map<String, Object> sortPram, Map<String, Object> searchPram, Integer iDisplayStart, Integer iDisplayLength);
	public void update(T t);
	public int updateByHQL(String hql, Map<String, Object> prams); 
	public List<T> getByHQL(String hql, Map<String, Object> prams);
	public List<T> getByHQL(String hql, Map<String, Object> prams, Integer star, Integer length);
	public List<Map<String, Object>> getBySQL(String sql, Map<String, Object> prams);
	public List<Map<String, Object>> getBySQL(String sql, Map<String, Object> prams, Integer star, Integer pageSize);
	public int getServiceCountByType(Map<String, Object> prams);
	public void del(Long id);
	public void del(T t);
	public Page getByPrams(Map<String, Object> prams, Map<String, Object> sortPram, Map<String, Object> searchPram, Integer iDisplayStart, Integer iDisplayLength,
			Map<String, Object>  startTime,Map<String, Object> endTime);
	public Page getByPramsX(Map<String, Object> prams, Map<String, Object> sortPram, Map<String, Object> searchPram, Integer iDisplayStart, Integer iDisplayLength,
			Map<String, Object>  startTime,Map<String, Object> endTime);
	public Integer getCount(Map<String, Object> prams,Map<String, Object> searchPram,Map<String, Object>  startTime,Map<String, Object> endTime);
	public Integer getCountX(Map<String, Object> prams,Map<String, Object> searchPram,Map<String, Object>  startTime,Map<String, Object> endTime);
	public Integer getCount(Map<String, Object> prams,Map<String, Object> searchPram);
	public void getBySQL(String sql);
		
}
