package com.xjt.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.xjt.dao.UsersessionDao;
import com.xjt.entity.Usersession;

@Repository
public class UsersessionDaoImpl extends BaseDaoImpl<Usersession> implements UsersessionDao{

    @Override    protected Class<Usersession> getEntityClass() {
       // TODO Auto-generated method stub
		return Usersession.class;
	 }
    
	public List<Usersession> getByPrams2(Long userId, Map<String, Object> sortPram){
		Criteria criteria = getSession().createCriteria(getEntityClass());
		//设置参数
		if (userId != null) {
			criteria.add(Restrictions.or(
					Restrictions.and(
							Restrictions.eq("userId", userId), Restrictions.eq("userDel", 0)
					),
				    Restrictions.and(
				    		Restrictions.eq("toUserId", userId), Restrictions.eq("toUserIdDel", 0)
				    )
						   				)
						);
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

}