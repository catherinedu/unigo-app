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

import com.xjt.service.SysmsgService;
import com.xjt.service.UserService;
import com.xjt.chat.PushMsg;
import com.xjt.chat.PushUtil;
import com.xjt.entity.Sysmsg;
import com.xjt.entity.User;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Sysmsg控制层
* @author Administrator
*/
@Controller
public class SysmsgController{

		@Autowired
		private SysmsgService sysmsgService;
		@Autowired
		private UserService userService;
		/**
		 *添加或编辑Sysmsg
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/addOrEditSysmsg")
		public String addOrEditSysmsg(ModelMap modelMap,HttpSession session,String title,String content,String ids,Integer type){
			if (title!=null&&content!=null&&type!=null&&(type==0||(type==1&&ids!=null&&ids.length()>0))) {
				//查询用户
				List<User> users=null;
				if (type==0) {//发送所有用户
					users=userService.getByParams(null);
				}else if (type==1){//发送多个用户
					List<Long> idsLongs=new ArrayList<Long>();
					for (String id : ids.split(",")) {
						try {
							idsLongs.add(Long.valueOf(id));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					if (idsLongs.size()>0) {
						users=userService.getByIds(idsLongs);
					}
				}
				//根据用户生成记录 并推送消息
				Sysmsg sysmsg=null;
				PushMsg pushMsg=null;
				for (User user : users) {
					sysmsg=new Sysmsg(user.getId(), title, content, 0, new Date());
					sysmsgService.save(sysmsg);
					pushMsg=new PushMsg(2, null,null, null, null, content,0);
					PushUtil.sendMsgToUser(user.getId(), pushMsg);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除Sysmsg
		*/
		@ResponseBody
		@RequestMapping("delSysmsg")
		public String delSysmsg(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Sysmsg sysmsg=sysmsgService.get(id);
				if (sysmsg!=null) {
					sysmsgService.delete(sysmsg);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Sysmsg
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userGetSysmsg")
		public String userGetSysmsg(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			User user=(User) session.getAttribute("user");
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=new HashMap<String, Object>();
			prams.put("userId", user.getId());
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put("desc", "id");
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
			Page page=sysmsgService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Sysmsg> sysmsgList= (List<Sysmsg>) page.getList();
			if (sysmsgList!=null&&sysmsgList.size()>0) {
				pageCount=sysmsgService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, sysmsgList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
				sysmsgService.getBySQL("UPDATE sysmsg SET readState=1 where userId="+user.getId()); //更新已读状态
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Sysmsg
		*/
		@ResponseBody
		@RequestMapping("getSysmsg")
		public String getSysmsg(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=sysmsgService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Sysmsg> sysmsgList= (List<Sysmsg>) page.getList();
			if (sysmsgList!=null&&sysmsgList.size()>0) {
				pageCount=sysmsgService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, sysmsgList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询Sysmsg
		*/
		@ResponseBody
		@RequestMapping("getSysmsgById")
		public String getSysmsgById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Sysmsg sysmsg=sysmsgService.get(id);
				if (sysmsg!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, sysmsg);//jsonObject	json对象
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
		 * 查询所有Sysmsg
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllSysmsg")
		public String getAllSysmsg(ModelMap modelMap,HttpSession session){
			List<Sysmsg> sysmsgList= sysmsgService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, sysmsgList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
