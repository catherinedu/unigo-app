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

import com.xjt.post.PostRentInfo;
import com.xjt.post.PostThread;
import com.xjt.post.PostUtil;
import com.xjt.service.UserMatchRentService;
import com.xjt.entity.User;
import com.xjt.entity.UserMatchRent;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.LogUtil;
import com.xjt.util.Page;

/**
* 用户订阅租房信息控制层
* @author Administrator
*/
@Controller
public class UserMatchRentController{

		@Autowired
		private UserMatchRentService userMatchRentService;

		/**
		 *添加或编辑用户订阅租房信息
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditUserMatchRent")
		public String addOrEditUserMatchRent(ModelMap modelMap,HttpSession session,Long id,Long u_id,Integer minPrice,Integer maxPrice,Integer roomNumber,Integer minArea,Integer maxArea,Integer elevator,Integer faceto,Integer fitment,String city,String district,String location,Integer period,Integer gender,Integer trafficType,String stationName){
			User user=(User) session.getAttribute("user");
			u_id=user.getId();
			UserMatchRent userMatchRent=null;
			if (id!=null) {
				userMatchRent=userMatchRentService.get(id);
			}else {
				userMatchRent=new UserMatchRent();
			}
			if (userMatchRent!=null) {
				if (u_id!=null){
					userMatchRent.setU_id(u_id);
				}
				if (minPrice!=null){
					userMatchRent.setMinPrice(minPrice);
				}
				if (maxPrice!=null){
					userMatchRent.setMaxPrice(maxPrice);
				}
				if (roomNumber!=null){
					userMatchRent.setRoomNumber(roomNumber);
				}
				if (minArea!=null){
					userMatchRent.setMinArea(minArea);
				}
				if (maxArea!=null){
					userMatchRent.setMaxArea(maxArea);
				}
				if (elevator!=null){
					userMatchRent.setElevator(elevator);
				}
				if (faceto!=null){
					userMatchRent.setFaceto(faceto);
				}
				if (fitment!=null){
					userMatchRent.setFitment(fitment);
				}
				if (city!=null&&city.length()>0){
					userMatchRent.setCity(city);
				}
				if (district!=null&&district.length()>0){
					userMatchRent.setDistrict(district);
				}
				if (location!=null&&location.length()>0){
					userMatchRent.setLocation(location);
				}
				if (period!=null){
					userMatchRent.setPeriod(period);
				}
				if (gender!=null){
					userMatchRent.setGender(gender);
				}
				if (trafficType!=null){
					userMatchRent.setTrafficType(trafficType);
				}
				if (stationName!=null&&stationName.length()>0){
					userMatchRent.setStationName(stationName);
				}
				userMatchRentService.saveOrUpdate(userMatchRent);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
				/**post对接用户订阅*/
				postUserMatchRent(userMatchRent);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * post对接用户订阅
		 * */
		private void postUserMatchRent(UserMatchRent userMatchRent){
			if(userMatchRent.getCity()!=null){ //换code
				if (userMatchRent.getCity().equals("香港")) {
					userMatchRent.setCity("01");
				}
			}
			if(userMatchRent.getDistrict()!=null){ //换code
				userMatchRent.setDistrict(checkDistrictCode(userMatchRent.getDistrict()));
			}
			if(userMatchRent.getLocation()!=null){ //换code
				userMatchRent.setLocation(checkLocationCodePms(userMatchRent.getLocation()));
			}
			PostThread postThread=new PostThread("postUserMatchRent",userMatchRent);
			postThread.start();
			/*String jsonbody=JsonUtil.toJson(userMatchRent);
			LogUtil.Info("订阅租房数据信息");
			LogUtil.Info("jsonbody="+jsonbody);
			String response=PostUtil.toUrl(PostUtil.matchrentjsonurl, jsonbody);
			LogUtil.Info("返回结果="+response);*/
		}

		/**
		 * 根据地区查询知识库 返回对应的code
		 * @param pms
		 * @return
		 */
		public String checkDistrictCode(String pms){
			if (pms!=null) {
				String sql="SELECT a.`district_code` from geography_hk a  where district ='"+pms+"' limit 0,1 ";
				List<Map<String, Object>> mapsList=userMatchRentService.getBySQL(sql, null);
				if(mapsList!=null&&mapsList.size()>0){
					Map<String, Object> map=mapsList.get(0);
					if(map.get("district_code")!=null){
						return map.get("district_code").toString();
					}	
				}
			}
			return null;
		}
		
		/**
		 * 根据location查询知识库 返回对应的code
		 * @param pms
		 * @return
		 */
		public String checkLocationCodePms(String pms){
			if (pms!=null) {
				String sql="SELECT a.`location_code` from geography_hk a  where location ='"+pms+"' limit 0,1 ";
				List<Map<String, Object>> mapsList=userMatchRentService.getBySQL(sql, null);
				if(mapsList!=null&&mapsList.size()>0){
					Map<String, Object> map=mapsList.get(0);
					if(map.get("location_code")!=null){
						return map.get("location_code").toString();
					}	
				}
			}
			return null;
		}
		
		
		/**
		 *删除用户订阅租房信息
		*/
		@ResponseBody
		@RequestMapping("delUserMatchRent")
		public String delUserMatchRent(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				UserMatchRent userMatchRent=userMatchRentService.get(id);
				if (userMatchRent!=null) {
					userMatchRentService.delete(userMatchRent);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询用户订阅租房信息
		*/
		@ResponseBody
		@RequestMapping("getUserMatchRent")
		public String getUserMatchRent(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=userMatchRentService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<UserMatchRent> userMatchRentList= (List<UserMatchRent>) page.getList();
			if (userMatchRentList!=null&&userMatchRentList.size()>0) {
				pageCount=userMatchRentService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, userMatchRentList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询用户订阅租房信息
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/getUserMatchRentByUser")
		public String getUserMatchRentById(ModelMap modelMap,HttpSession session){
			User user=(User) session.getAttribute("user");
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("u_id", user.getId());
			UserMatchRent userMatchRent=userMatchRentService.getByParam(params);
			if (userMatchRent!=null) {
				modelMap.put(CommonInfoUtil.jSONOBJECT, userMatchRent);//jsonObject	json对象
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 查询所有用户订阅租房信息
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllUserMatchRent")
		public String getAllUserMatchRent(ModelMap modelMap,HttpSession session){
			List<UserMatchRent> userMatchRentList= userMatchRentService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, userMatchRentList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
