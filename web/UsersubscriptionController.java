package com.xjt.web;

import java.text.ParseException;
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
import com.xjt.service.UsersubscriptionService;
import com.xjt.entity.Columntype;
import com.xjt.entity.User;
import com.xjt.entity.Usersubscription;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Usersubscription控制层
* @author Administrator
*/
@Controller
public class UsersubscriptionController{

		@Autowired
		private UsersubscriptionService usersubscriptionService;
		@Autowired
		private ColumntypeService columntypeService;
		/**
		 *用户添加订阅
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditUsersubscription")
		public String addOrEditUsersubscription(ModelMap modelMap,HttpSession session,Long typeId,String typeIds){
			if (typeIds!=null&&typeIds.length()>0) {
				User user=(User) session.getAttribute("user");
				String []  ids=typeIds.split(",");
				Columntype columntype=null;
				for (String id : ids) {
					typeId=Long.valueOf(id);
					if (checkusersubscription(typeId, user.getId())) {
						columntype=columntypeService.get(typeId);
						if (columntype!=null) {
							Usersubscription usersubscription=new Usersubscription();
							usersubscription.setTypeId(typeId);
							usersubscription.setUserId(user.getId());
							usersubscription.setSubType(columntype.getType());
							usersubscriptionService.saveOrUpdate(usersubscription);
						}
					}
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1  参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 检查用户是否已订阅
		 * @param typeId
		 * @param userId
		 * @return
		 */
		public boolean checkusersubscription(Long typeId,Long userId){
			if (typeId!=null&&userId!=null) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("typeId", typeId);
				params.put("userId", userId);
				if (usersubscriptionService.getCount(params)>0) {//已添加订阅
					return false;
				}
			}
			return true;
		}
		
		/**
		 *删除 取消订阅
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/delUsersubscription")
		public String delUsersubscription(ModelMap modelMap,HttpSession session,Long typeId,String typeIds){
			if (typeIds!=null) {
				User user=(User) session.getAttribute("user");
				String sql="DELETE from usersubscription where userId="+user.getId()+" and typeId IN ("+typeIds+")";
				usersubscriptionService.getBySQL(sql);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Usersubscription
		*/
		@ResponseBody
		@RequestMapping("getUsersubscription")
		public String getUsersubscription(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=usersubscriptionService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Usersubscription> usersubscriptionList= (List<Usersubscription>) page.getList();
			if (usersubscriptionList!=null&&usersubscriptionList.size()>0) {
				pageCount=usersubscriptionService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, usersubscriptionList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询Usersubscription
		*/
		@ResponseBody
		@RequestMapping("getUsersubscriptionById")
		public String getUsersubscriptionById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Usersubscription usersubscription=usersubscriptionService.get(id);
				if (usersubscription!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, usersubscription);//jsonObject	json对象
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
		 * 查询所有Usersubscription
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllUsersubscription")
		public String getAllUsersubscription(ModelMap modelMap,HttpSession session){
			List<Usersubscription> usersubscriptionList= usersubscriptionService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, usersubscriptionList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
