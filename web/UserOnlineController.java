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

import com.mysql.jdbc.log.Log;
import com.xjt.service.ScorerecordService;
import com.xjt.service.UserOnlineService;
import com.xjt.service.UserService;
import com.xjt.entity.Scorerecord;
import com.xjt.entity.User;
import com.xjt.entity.UserOnline;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* 用户在线时间统计控制层
* @author Administrator
*/
@Controller
public class UserOnlineController{

		@Autowired
		private UserOnlineService userOnlineService;
		@Autowired
		private ScorerecordService scorerecordService;
		@Autowired
		private UserService userService;
		/**
		 *添加或编辑用户在线时间统计
		 * @throws ParseException 
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userUpLine")
		public String userUpLine(ModelMap modelMap,HttpSession session) throws ParseException{
			User user=(User) session.getAttribute("user");
			UserOnline userOnline=getTodayUserOnline(user.getId());
			if (userOnline==null) {
				userOnline=new UserOnline(user.getId(), 0, new Date());
				Integer score=user.getScore();		//首次登录加积分 30
				Integer giveScore=30;
				if (score==null||score<0) {
					score=0;
				}
				if (user.getCheckState()!=null&&user.getCheckState()==2) {//高级认证1.5倍积分
					giveScore=(int) (giveScore*1.5);
				}
				user.setScore(score+giveScore);
				userService.saveOrUpdate(user);
				Scorerecord scorerecord=new Scorerecord(user.getId(), "每日登录", giveScore, new Date());
				scorerecordService.save(scorerecord);
			}
			userOnline.setUpTime(new Date());//修改上线时间
			userOnlineService.saveOrUpdate(userOnline);
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 用户下线
		 * @param modelMap
		 * @param session
		 * @return
		 * @throws ParseException
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userDownLine")
		public String userDownLine(ModelMap modelMap,HttpSession session) throws ParseException{
			User user=(User) session.getAttribute("user");
			UserOnline userOnline=getTodayUserOnline(user.getId());
			if (userOnline!=null) {
				userOnline.setDownTime(new Date());
				Integer onlinetime=(int) ((userOnline.getDownTime().getTime()-userOnline.getUpTime().getTime())/1000);  //计算在线秒数
				userOnline.setOnLineTime(onlinetime);
				userOnlineService.saveOrUpdate(userOnline);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询今天是否已经有上线记录
		 * @param userId
		 * @return
		 * @throws ParseException
		 */
		private UserOnline getTodayUserOnline(Long userId) throws ParseException{
			UserOnline userOnline=null;
			String today=DateUtil.simpdfyMd.format(new Date());
			Map<String, Object> prams=new HashMap<String, Object>();
			prams.put("userId", userId);
			Map<String, Object> startTime=new HashMap<String, Object>();
			startTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(today+" 00:00:00"));
			Map<String, Object> endTime=new HashMap<String, Object>();
			endTime.put("gmtCreated",  DateUtil.simpdfyMdHms.parse(today+" 23:59:59"));
			List<UserOnline> userOnlines=(List<UserOnline>) userOnlineService.getByPrams(prams, null, null, 0, 1, startTime, endTime).getList();
			if (userOnlines!=null&&userOnlines.size()>0) {	//如果今天已经有记录了
				userOnline=userOnlines.get(0);
			}
			return userOnline;
		}

		/**
		 *删除用户在线时间统计
		*/
		@ResponseBody
		@RequestMapping("delUserOnline")
		public String delUserOnline(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				UserOnline userOnline=userOnlineService.get(id);
				if (userOnline!=null) {
					userOnlineService.delete(userOnline);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询用户在线时间统计
		*/
		@ResponseBody
		@RequestMapping("getUserOnline")
		public String getUserOnline(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=userOnlineService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<UserOnline> userOnlineList= (List<UserOnline>) page.getList();
			if (userOnlineList!=null&&userOnlineList.size()>0) {
				pageCount=userOnlineService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, userOnlineList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询用户在线时间统计
		*/
		@ResponseBody
		@RequestMapping("getUserOnlineById")
		public String getUserOnlineById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				UserOnline userOnline=userOnlineService.get(id);
				if (userOnline!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, userOnline);//jsonObject	json对象
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
		 * 查询所有用户在线时间统计
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllUserOnline")
		public String getAllUserOnline(ModelMap modelMap,HttpSession session){
			List<UserOnline> userOnlineList= userOnlineService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, userOnlineList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
