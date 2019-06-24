package com.xjt.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjt.service.ColumntypeService;
import com.xjt.service.TypearticleService;
import com.xjt.service.UserreadrecordService;
import com.xjt.service.UsersubscriptionService;
import com.xjt.entity.Columntype;
import com.xjt.entity.User;
import com.xjt.entity.Userreadrecord;
import com.xjt.entity.Usersubscription;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Columntype控制层
* @author Administrator
*/
@Controller
public class ColumntypeController{

		@Autowired
		private ColumntypeService columntypeService;
		@Autowired
		private UsersubscriptionService usersubscriptionService;
		@Autowired
		private TypearticleService typearticleService;
		@Autowired
		private UserreadrecordService userreadrecordService;
		/**
		 *添加或编辑Columntype
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/addOrEditColumntype")
		public String addOrEditColumntype(ModelMap modelMap,HttpSession session,Long id,Integer type,String name,String img){
			Columntype columntype=null;
			if (type!=null&&name!=null&&name.length()>0&&img!=null&&img.length()>0) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("type", type);
				Columntype columntypeExist=columntypeService.getByParam(params);
				if (id!=null) {
					columntype=columntypeService.get(id);
					if (columntype.getType()!=type&&columntypeExist!=null) {//修改时分类判断分类是否已经存在
						columntype=null;
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.OBJECTEXIST);//msg=-4  已存在数据
					}
				}else {
					if (columntypeExist==null) {
						columntype=new Columntype();
					}else {
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.OBJECTEXIST);//msg=-4  已存在数据
					}
				}
				if (columntype!=null) {
					columntype.setType(type);
					columntype.setName(name);
					columntype.setImg(img);
					columntypeService.saveOrUpdate(columntype);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1
			}
			
			
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除Columntype
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delColumntype")
		public String delColumntype(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Columntype columntype=columntypeService.get(id);
				if (columntype!=null) {
					columntypeService.delete(columntype);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Columntype
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/getColumntype")
		public String getColumntype(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=null;
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "id");
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
			}
			Map<String, Object> startTime=null;
			if(start!=null&&start.length()>0){
				startTime=new HashMap<String, Object>();
				startTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(start+" 00:00:00"));
			}
			Map<String, Object> endTime=null;
			if(end!=null&&end.length()>0){
				endTime=new HashMap<String, Object>();
				endTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(end+" 23:59:59"));
			}
			Page page=columntypeService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Columntype> columntypeList= (List<Columntype>) page.getList();
			if (columntypeList!=null&&columntypeList.size()>0) {
				pageCount=columntypeService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, columntypeList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询订阅分类
		*/
		@ResponseBody
		@RequestMapping("appGetColumntype")
		public String appGetColumntype(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			User user=null;
			Map<String, Object> prams=null;
			if (session.getAttribute("user")!=null) {//用户已登录 查询用户是否有编辑过订阅分类 有则查询用户已订阅的分类
			    user=(User) session.getAttribute("user");
				List<Long> ids=getColumnTypeByUser(user);
				if (ids!=null) {
					prams=new HashMap<String, Object>();
					prams.put("id", ids);
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
					return JsonUtil.toJson(modelMap);
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				return JsonUtil.toJson(modelMap);
			}
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "id");
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
			}
			Map<String, Object> startTime=null;
			if(start!=null&&start.length()>0){
				startTime=new HashMap<String, Object>();
				startTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(start+" 00:00:00"));
			}
			Map<String, Object> endTime=null;
			if(end!=null&&end.length()>0){
				endTime=new HashMap<String, Object>();
				endTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(end+" 23:59:59"));
			}
			Page page=columntypeService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Columntype> columntypeList= (List<Columntype>) page.getList();
			if (columntypeList!=null&&columntypeList.size()>0) {
				pageCount=columntypeService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				/*if (user!=null) {//如果已登录  查询用户文章查看数量
					for (Columntype columntype : columntypeList) {
						editReadArticleNum(user,columntype);
					}
				}*/
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, columntypeList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 编辑未读文章数量
		 * @param columntype
		 */
		public void editReadArticleNum(User user,Columntype columntype){
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("typeId", columntype.getId());
			Integer articleNum=typearticleService.getCount(params);//查询总文章数量
			if (articleNum!=null&&articleNum>0) {
				params.put("userId", user.getId());
				Integer realNum=userreadrecordService.getCount(params);//用户已读数量
				if (realNum!=null) {
					columntype.setNoReadArticleNum(articleNum-realNum);//总文章数量-已读数量=未读数量
				}else {
					columntype.setNoReadArticleNum(articleNum);
				}
			}else{
				columntype.setNoReadArticleNum(0);
			}
				
		}
		
		/**
		 * 查询用户已订阅的分类id
		 * @param user
		 * @return
		 */
		public List<Long> getColumnTypeByUser(User user){
			List<Long> ids=null;
			if (user!=null) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("userId", user.getId());
				List<Usersubscription> usersubscriptions= usersubscriptionService.getByParams(params);
				if (usersubscriptions!=null&&usersubscriptions.size()>0) {
					ids=new ArrayList<Long>();
					for (Usersubscription usersubscription : usersubscriptions) {
						ids.add(usersubscription.getTypeId());
					}
				}
			}
			return ids;
		}
		
		/**
		 *根据id查询Columntype
		*/
		@ResponseBody
		@RequestMapping("getColumntypeById")
		public String getColumntypeById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Columntype columntype=columntypeService.get(id);
				if (columntype!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, columntype);//jsonObject	json对象
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 查询所有Columntype
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/appgetAllColumntype")
		public String getAllColumntype(ModelMap modelMap,HttpSession session){
			User user=(User) session.getAttribute("user");
			List<Long> subIds=getColumnTypeByUser(user);//查询已订阅的分类id
			List<Columntype> columntypeList= columntypeService.getByParams(null);//查询所以分类\
			if (subIds!=null&&subIds.size()>0) {//如果用户已经订阅过分类   判断并标识后 返回app
				for (Columntype columntype : columntypeList) {
					columntype.setUserIsSubscription(0);
					for (Long id : subIds) {
						if (id.equals(columntype.getId())) {
							columntype.setUserIsSubscription(1);//标记
							subIds.remove(id);//删除已处理的数据  减少基本操作数量
							break;
						}
					}
				}
			}
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, columntypeList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
