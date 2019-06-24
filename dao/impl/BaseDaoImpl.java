package com.xjt.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import com.xjt.util.Page;
import com.xjt.dao.BaseDao;

public abstract class BaseDaoImpl<T> implements BaseDao<T> {
	
	protected SessionFactory sessionFactory;

	@Autowired
	protected void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 保存
	 * 
	 * @param t
	 */
	public Long save(T t) {
		return (Long) getSession().save(t);
	}
	
	public Integer saveInt(T t) {
		return (Integer) getSession().save(t);
	}

	/**
	 * 更新
	 * 
	 * @param t
	 */
	public void update(T t) {
		getSession().update(t);
	}

	/**
	 * 添加或更新
	 * 
	 * @param t
	 */
	public void saveOrUpdate(T t) {
		getSession().saveOrUpdate(t);
	}

	/**
	 * 获得id对象
	 * 
	 * @param id
	 * @return
	 */
	public T get(Long id) {
		return get(id, false);
	}
	
	public T get(Integer id) {
		return get(id, false);
	}

	/**
	 * 通过id删除对象
	 * 
	 * @param id
	 */
	public void del(Long id) {
		T t = get(id);
		if (t != null) {
			getSession().delete(t);
		}
	}

	/**
	 * 删除对象
	 */
	public void del(T t) {
		if (t != null) {
			getSession().delete(t);
		}
	}

	/**
	 * 查询对象
	 * @param id
	 * @param lock 查询对象方式
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T get(Long id, boolean lock) {
		T entity;
		if (lock) {
			//LockMode.UPGRADE:涓嶇缂撳瓨涓槸鍚﹀瓨鍦ㄥ璞�,鎬绘槸閫氳繃select璇彞鍒版暟鎹簱涓姞杞借瀵硅薄
			entity = (T) getSession().get(getEntityClass(), id, LockMode.UPGRADE);
		} else {
			entity = (T) getSession().get(getEntityClass(), id);
		}
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	protected T get(Integer id, boolean lock) {
		T entity;
		if (lock) {
			//LockMode.UPGRADE:涓嶇缂撳瓨涓槸鍚﹀瓨鍦ㄥ璞�,鎬绘槸閫氳繃select璇彞鍒版暟鎹簱涓姞杞借瀵硅薄
			entity = (T) getSession().get(getEntityClass(), id, LockMode.UPGRADE);
		} else {
			entity = (T) getSession().get(getEntityClass(), id);
		}
		return entity;
	}
	
	
	public Integer getCount(Map<String, Object> prams) {
		Criteria criteria = getSession().createCriteria(getEntityClass());
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object obj = prams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		return ((Number) criteria.setProjection(Projections.rowCount())
				.uniqueResult()).intValue();
	}

	/**
	 * 根据条件查询对象列表
	 */
	public List<T> getObjectList(Map<String, Object> eqParams) {
		Criteria criteria = getSession().createCriteria(getEntityClass());
		if (eqParams != null) {
			if (!eqParams.isEmpty()) {
				Set<String> keys = eqParams.keySet();
				for (String paramKey : keys) {
					Object obj = eqParams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		return criteria.list();
	}
	
	/**
	 * 根据条件查询对象
	 */
	public T getObject(Map<String, Object> eqParams) {
		Criteria criteria = getSession().createCriteria(getEntityClass());
		if (eqParams != null) {
			if (!eqParams.isEmpty()) {
				Set<String> keys = eqParams.keySet();
				for (String paramKey : keys) {
					Object paramVal = eqParams.get(paramKey);
					criteria.add(Restrictions.eq(paramKey, paramVal));
				}
			}
		}
		criteria.setMaxResults(1);
		return (T) criteria.uniqueResult();
	}
	
	public T getObject(Map<String, Object> eqParams,Map<String, Object> sortPram){
		Criteria criteria = getSession().createCriteria(getEntityClass());
		if (eqParams != null) {
			if (!eqParams.isEmpty()) {
				Set<String> keys = eqParams.keySet();
				for (String paramKey : keys) {
					Object paramVal = eqParams.get(paramKey);
					criteria.add(Restrictions.eq(paramKey, paramVal));
				}
			}
		}
		
		if (sortPram != null) {
			if (!sortPram.isEmpty()) {
				Set<String> keys = sortPram.keySet();
				for (String paramKey : keys) {
					Object paramVal = sortPram.get(paramKey);
					if(paramKey.equals("desc")){
						criteria.addOrder(Order.desc(paramVal.toString()));
					}else if(paramKey.equals("asc")){
						criteria.addOrder(Order.asc(paramVal.toString()));
					}
				}
			}
		}
		
		criteria.setMaxResults(1);
		return (T) criteria.uniqueResult();
	}

	abstract protected Class<T> getEntityClass();

	public List<T> getByPrams(Map<String, Object> prams, Map<String, Object> sortPram,
			Map<String, Object> searchPram){
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//设置参数
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object paramVal = prams.get(paramKey);
					criteria.add(Restrictions.eq(paramKey, paramVal));
				}
			}
		}
		//模糊查询
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));	
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}	
		//设置排序
		if (sortPram != null) {
			if (!sortPram.isEmpty()) {
				Set<String> keys = sortPram.keySet();
				for (String paramKey : keys) {
					Object paramVal = sortPram.get(paramKey);
					if(paramKey.equals("desc")){
						criteria.addOrder(Order.desc(paramVal.toString()));
					}else if(paramKey.equals("asc")){
						criteria.addOrder(Order.asc(paramVal.toString()));
					}
				}
			}
		}
		return criteria.list();
	}
	
	public Integer getCount(Map<String, Object> prams,Map<String, Object> searchPram) {
		Criteria criteria = getSession().createCriteria(getEntityClass());
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object paramVal = prams.get(paramKey);
					criteria.add(Restrictions.eq(paramKey, paramVal));
				}
			}
		}
		//模糊查询
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));	
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}
		return ((Number) criteria.setProjection(Projections.rowCount())
				.uniqueResult()).intValue();
	}
	
	/**
	 * 按条件查询,排序,分页
	 */
	public Page getByPrams(Map<String, Object> prams, Map<String, Object> sortPram, Map<String, Object> searchPram, Integer iDisplayStart, Integer iDisplayLength){
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//设置参数
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object obj = prams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		//模糊查询
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));	
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}
		//设置排序
		if (sortPram != null) {
			if (!sortPram.isEmpty()) {
				Set<String> keys = sortPram.keySet();
				for (String paramKey : keys) {
					Object paramVal = sortPram.get(paramKey);
					if(paramKey.equals("desc")){
						criteria.addOrder(Order.desc(paramVal.toString()));
					}else if(paramKey.equals("asc")){
						criteria.addOrder(Order.asc(paramVal.toString()));
					}
				}
			}
		}
		//设置页码
		if(iDisplayStart!=null){
			criteria.setFirstResult(iDisplayStart);
		}
		
		if(iDisplayLength!=null){
			criteria.setMaxResults(iDisplayLength);
		}
		
		Page page = new Page();
		page.setList(criteria.list());
		page.setTotalCount(getByPrams(prams, searchPram));
		if(iDisplayLength!=null){
			page.setPageSize(iDisplayLength);
		}
		return page;
	}
	
	/**
	 * 按条件查询总数
	 */
	public int getByPrams(Map<String, Object> prams, Map<String, Object> searchPram){
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//设置参数
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object obj = prams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		//模糊查询
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));	
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}
		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
	
	/**
	 * 按条件查询总数
	 */
	public int getByPramsX(Map<String, Object> prams, Map<String, Object> searchPram){
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//设置参数
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object obj = prams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}
		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
	
	/**
	 * 根据HQL查询
	 */
	public List<T> getByHQL(String hql, Map<String, Object> prams) {
		Query query = getSession().createQuery(hql);
		// 匹配参数
		if (prams != null) {
			Set<String> keys = prams.keySet();
			for (String paramKey : keys) {
				Object obj = prams.get(paramKey);
				if (obj instanceof Collection<?>) {
					query.setParameterList(paramKey, (Collection<?>)obj);
				} else if (obj instanceof Object[]) {
					query.setParameterList(paramKey, (Object[]) obj);
				} else {
					query.setParameter(paramKey, obj);
				}
			}
		}
		return query.list();
	}
	
	public List<T> getByHQL(String hql, Map<String, Object> prams, Integer star, Integer length) {
		Query query = getSession().createQuery(hql);
		// 匹配参数
		if (prams != null) {
			Set<String> keys = prams.keySet();
			for (String paramKey : keys) {
				Object obj = prams.get(paramKey);
				if (obj instanceof Collection<?>) {
					query.setParameterList(paramKey, (Collection<?>)obj);
				} else if (obj instanceof Object[]) {
					query.setParameterList(paramKey, (Object[]) obj);
				} else {
					query.setParameter(paramKey, obj);
				}
			}
		}
		query.setFirstResult(star);
		query.setMaxResults(length);
		return query.list();
	}
	
	/**
	 * 根据HQL更新
	 */
	public int updateByHQL(String hql, Map<String, Object> prams) {
		Query query = getSession().createQuery(hql);
		// 匹配参数
		if (prams != null) {
			Set<String> keys = prams.keySet();
			for (String paramKey : keys) {
				Object paramVal = prams.get(paramKey);
				query.setParameter(paramKey, paramVal);
			}
		}
		return query.executeUpdate();
	}
	
	public List<Map<String, Object>> getBySQL(String sql, Map<String, Object> prams, Integer star, Integer length) {
		Query query = getSession().createSQLQuery(sql).setResultTransformer(
				Transformers.ALIAS_TO_ENTITY_MAP);
		// 匹配参数
		if (prams != null) {
			Set<String> keys = prams.keySet();
			for (String paramKey : keys) {
				Object paramVal = prams.get(paramKey);
				query.setParameter(paramKey, paramVal);
			}
		}
		if(star!=null){
			query.setFirstResult(star);
		}
		if(length!=null){
			query.setMaxResults(length);
		}
		return query.list();
	}
	
	public List<Map<String, Object>> getBySQL(String sql, Map<String, Object> prams) {
		Query query = getSession().createSQLQuery(sql).setResultTransformer(
				Transformers.ALIAS_TO_ENTITY_MAP);
		// 匹配参数
		if (prams != null) {
			Set<String> keys = prams.keySet();
			for (String paramKey : keys) {
				Object paramVal = prams.get(paramKey);
				query.setParameter(paramKey, paramVal);
			}
		}
		return query.list();
	}
	
	public int getServiceCountByType(Map<String, Object> prams) {
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//设置参数
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object paramVal = prams.get(paramKey);
					criteria.add(Restrictions.eq(paramKey, paramVal));
				}
			}
		}
		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
	
	//@Override
	public void saveOrUpdateAll(List<T> t) {
		Session session = getSession();
		for(int i=0; i<t.size(); i++){
			session.saveOrUpdate(t.get(i));
			if(i % 20 == 0){
				session.flush();
				session.clear();
			}else if(i == t.size()-1){
				session.flush();
				session.clear();
			}
		}
	}
	
	//@Override
	public void deleteAll(List<T> t) {
		Session session = getSession();
		for(int i=0; i<t.size(); i++){
			session.delete(t.get(i));
			if(i % 20 == 0){
				session.flush();
				session.clear();
			}else if(i == t.size()-1){
				session.flush();
				session.clear();
			}
		}
	}
	
	/**
	 * 按时间段、条件查询,排序,分页
	 */
	public Page getByPrams(Map<String, Object> prams, Map<String, Object> sortPram, Map<String, Object> searchPram, Integer iDisplayStart, Integer iDisplayLength,
							Map<String, Object>  startTime,Map<String, Object> endTime){
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//设置参数
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object obj = prams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		
		//模糊查询
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));	
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}
		//设置排序
		if (sortPram != null) {
			if (!sortPram.isEmpty()) {
				Set<String> keys = sortPram.keySet();
				for (String paramKey : keys) {
					Object paramVal = sortPram.get(paramKey);
					if(paramKey.equals("desc")){
						criteria.addOrder(Order.desc(paramVal.toString()));
					}else if(paramKey.equals("asc")){
						criteria.addOrder(Order.asc(paramVal.toString()));
					}
				}
			}
		}
		//查询指定时间之后的记录  
		if(startTime!=null){
			if (!startTime.isEmpty()) {
				Set<String> keys = startTime.keySet();
				for (String paramKey : keys) {
					Object paramVal = startTime.get(paramKey);
					criteria.add(Restrictions.ge(paramKey,paramVal));  
				}
			}
			
		}                        
		 //查询指定时间之前的记录     
		if(endTime!=null)   {   
			if (!endTime.isEmpty()) {
				Set<String> keys = endTime.keySet();
				for (String paramKey : keys) {
					Object paramVal = endTime.get(paramKey);
					criteria.add(Restrictions.le(paramKey,paramVal));  
				}
			}
		}		
		//设置页码
		if(iDisplayStart!=null){
			criteria.setFirstResult(iDisplayStart);
		}
		if(iDisplayLength!=null){
			criteria.setMaxResults(iDisplayLength);
		}
		
		Page page = new Page();
		page.setList(criteria.list());
		page.setTotalCount(getByPrams(prams, searchPram));
		if (iDisplayLength!=null) {
			page.setPageSize(iDisplayLength);
		}
		
		return page;
	}
	
	
	/**
	 * 关联数据    按时间段、条件查询,排序,分页
	 */
	public Page getByPramsX(Map<String, Object> prams, Map<String, Object> sortPram, Map<String, Object> searchPram, Integer iDisplayStart, Integer iDisplayLength,
							Map<String, Object>  startTime,Map<String, Object> endTime){
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//设置参数
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object obj = prams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		//模糊查询
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
						/*Criterion cri = Restrictions
				                .sqlRestriction(key+" in "+objString);  //sql拼接
					 disjunction.add(cri);*/
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}
		//设置排序
		if (sortPram != null) {
			if (!sortPram.isEmpty()) {
				Set<String> keys = sortPram.keySet();
				for (String paramKey : keys) {
					Object paramVal = sortPram.get(paramKey);
					if(paramKey.equals("desc")){
						criteria.addOrder(Order.desc(paramVal.toString()));
					}else if(paramKey.equals("asc")){
						criteria.addOrder(Order.asc(paramVal.toString()));
					}
				}
			}
		}
		//查询指定时间之后的记录  
		if(startTime!=null){
			if (!startTime.isEmpty()) {
				Set<String> keys = startTime.keySet();
				for (String paramKey : keys) {
					Object paramVal = startTime.get(paramKey);
					criteria.add(Restrictions.ge(paramKey,paramVal));  
				}
			}
			
		}                        
		 //查询指定时间之前的记录     
		if(endTime!=null)   {   
			if (!endTime.isEmpty()) {
				Set<String> keys = endTime.keySet();
				for (String paramKey : keys) {
					Object paramVal = endTime.get(paramKey);
					criteria.add(Restrictions.le(paramKey,paramVal));  
				}
			}
		}		
		//设置页码
		if(iDisplayStart!=null){
			criteria.setFirstResult(iDisplayStart);
		}
		if(iDisplayLength!=null){
			criteria.setMaxResults(iDisplayLength);
		}
		
		Page page = new Page();
		page.setList(criteria.list());
		page.setTotalCount(getByPramsX(prams, searchPram));
		if (iDisplayLength!=null) {
			page.setPageSize(iDisplayLength);
		}
		
		return page;
	}
	
	/**
	 * 根据时间段查询总数
	 * @param prams
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Integer getCount(Map<String, Object> prams,Map<String, Object> searchPram,Map<String, Object>  startTime,Map<String, Object> endTime) {
		Criteria criteria = getSession().createCriteria(getEntityClass());
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object obj = prams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		//模糊查询
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));	
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}
		//查询指定时间之后的记录  
		if(startTime!=null){
			if (!startTime.isEmpty()) {
				Set<String> keys = startTime.keySet();
				for (String paramKey : keys) {
					Object paramVal = startTime.get(paramKey);
					criteria.add(Restrictions.ge(paramKey,paramVal));  
				}
			}
			
		}    		                    
		
		 //查询指定时间之前的记录     
		if(endTime!=null)   {   
			if (!endTime.isEmpty()) {
				Set<String> keys = endTime.keySet();
				for (String paramKey : keys) {
					Object paramVal = endTime.get(paramKey);
					criteria.add(Restrictions.le(paramKey,paramVal));  
				}
			}
		}
		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
	
	
	/**
	 * 根据时间段查询总数
	 * @param prams
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Integer getCountX(Map<String, Object> prams,Map<String, Object> searchPram,Map<String, Object>  startTime,Map<String, Object> endTime) {
		Criteria criteria = getSession().createCriteria(getEntityClass());
		if (prams != null) {
			if (!prams.isEmpty()) {
				Set<String> keys = prams.keySet();
				for (String paramKey : keys) {
					Object obj = prams.get(paramKey);
					if (obj instanceof Collection<?>) {
						criteria.add(Restrictions.in(paramKey, (Collection<?>)obj));
					} else if (obj instanceof Object[]) {
						criteria.add(Restrictions.in(paramKey, (Object[])obj));
					} else {
						criteria.add(Restrictions.eq(paramKey, obj));
					}
				}
			}
		}
		
		//模糊查询
		if (searchPram != null) {
			if (!searchPram.isEmpty()) {
				Set<String> keys = searchPram.keySet();
				Disjunction disjunction=Restrictions.disjunction();
				for (String key : keys) {
					Object obj = searchPram.get(key);
					if (obj instanceof Collection<?>) {
						disjunction.add(Restrictions.in(key, (Collection<?>)obj));	
					} else if (obj instanceof Object[]) {
						disjunction.add(Restrictions.in(key, (Object[])obj));
					} else {
						disjunction.add(Restrictions.like(key, obj.toString(), MatchMode.ANYWHERE));
					}
				}
				criteria.add(disjunction);
			}
		}
		
		//查询指定时间之后的记录  
		if(startTime!=null){
			if (!startTime.isEmpty()) {
				Set<String> keys = startTime.keySet();
				for (String paramKey : keys) {
					Object paramVal = startTime.get(paramKey);
					criteria.add(Restrictions.ge(paramKey,paramVal));  
				}
			}
			
		}    		                    
		
		 //查询指定时间之前的记录     
		if(endTime!=null)   {   
			if (!endTime.isEmpty()) {
				Set<String> keys = endTime.keySet();
				for (String paramKey : keys) {
					Object paramVal = endTime.get(paramKey);
					criteria.add(Restrictions.le(paramKey,paramVal));  
				}
			}
		}
		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
	
	public void getBySQL(String sql) {
		Query query = getSession().createSQLQuery(sql);
		query.executeUpdate();
	}
}
