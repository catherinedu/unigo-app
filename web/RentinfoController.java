package com.xjt.web;

import java.math.BigInteger;
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

import com.xjt.post.PostThread;
import com.xjt.service.CollectionService;
import com.xjt.service.RentinfoService;
import com.xjt.service.ScorerecordService;
import com.xjt.service.TrafficInfoService;
import com.xjt.service.UserService;
import com.xjt.entity.Rentinfo;
import com.xjt.entity.Scorerecord;
import com.xjt.entity.TrafficInfo;
import com.xjt.entity.User;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Rentinfo控制层
* @author Administrator
*/
@Controller
public class RentinfoController{

		@Autowired
		private RentinfoService rentinfoService;
		@Autowired
		private UserService userService;
		@Autowired
		private CollectionService collectionService;
		@Autowired
		private TrafficInfoService trafficInfoService;
		@Autowired
		private ScorerecordService scorerecordService;
		/**
		 *添加或编辑Rentinfo
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditRentinfo")
		public String addOrEditRentinfo(ModelMap modelMap,HttpSession session,Long trafficInfoId,Integer trafficType,String stationName, Integer distance,Long id,String addressCode,String city,String address,Long userId,String title,String area,Integer price,String readImgs,String imgs,String videoUrl,String introduce,String tabInfo,String rentWay,String location,String houseType,String roomSize,String decorate,String defectSex,Double longitud,Double latitude,Integer showState){
			User user=(User) session.getAttribute("user");
			userId=user.getId();
			Rentinfo rentinfo=null;
			if (id!=null) {
				rentinfo=rentinfoService.get(id);
			}else {
				rentinfo=new Rentinfo();
				rentinfo.setGmtCreated(new Date());
				rentinfo.setUserId(userId);
				rentinfo.setDisNum(0);
				rentinfo.setReadNum(0);
			}
			if (rentinfo!=null&&user.getId().equals(rentinfo.getUserId())) {
				if (title!=null&&title.length()>0){
					rentinfo.setTitle(title);
				}
				if (city!=null&&city.length()>0) {
					rentinfo.setCity(city);
				}
				if (area!=null&&area.length()>0){
					rentinfo.setArea(area);
				}
				if (price!=null){
					rentinfo.setPrice(price);
				}
				if (imgs!=null&&imgs.length()>0){
					rentinfo.setImgs(imgs);
				}
				if (readImgs!=null&&readImgs.length()>0) {
					rentinfo.setReadImgs(readImgs);
				}
				if (videoUrl!=null&&videoUrl.length()>0){
					rentinfo.setVideoUrl(videoUrl);
				}
				if (introduce!=null&&introduce.length()>0){
					rentinfo.setIntroduce(introduce);
				}
				if (tabInfo!=null&&tabInfo.length()>0){
					rentinfo.setTabInfo(tabInfo);
				}
				if (rentWay!=null&&rentWay.length()>0){
					rentinfo.setRentWay(rentWay);
				}
				if (location!=null&&location.length()>0){
					rentinfo.setLocation(location);
				}
				if (houseType!=null&&houseType.length()>0){
					rentinfo.setHouseType(houseType);
				}
				if (roomSize!=null&&roomSize.length()>0){
					rentinfo.setRoomSize(roomSize);
				}
				if (decorate!=null&&decorate.length()>0){
					rentinfo.setDecorate(decorate);
				}
				if (defectSex!=null&&defectSex.length()>0){
					rentinfo.setDefectSex(defectSex);
				}
				if (longitud!=null){
					rentinfo.setLongitud(longitud);
				}
				if (latitude!=null){
					rentinfo.setLatitude(latitude);
				}
				if (showState!=null){
					rentinfo.setShowState(showState);
				}
				if (address!=null&&address.length()>0) {
					rentinfo.setAddress(address);
				}
				if (addressCode!=null&&addressCode.length()>0) {
					rentinfo.setAddressCode(addressCode);
				}
				rentinfoService.saveOrUpdate(rentinfo);
				TrafficInfo trafficInfo=editTrafficInfo(trafficInfoId, rentinfo.getId(), trafficType, stationName, distance);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
				
				/**post删除租房*/
				if (id!=null) {
					postDelRentInfo(rentinfo);
				}
				/**post对接租房*/
				postRentInfo(rentinfo, trafficInfo);
				
				if (id==null) {
					Integer score=user.getScore();		//发布租房 添加10积分
					Integer giveScore=10;
					if (score==null||score<0) {
						score=0;
					}
					if (user.getCheckState()!=null&&user.getCheckState()==2) {//高级认证1.5倍积分
						giveScore=(int) (giveScore*1.5);
					}
					user.setScore(score+giveScore);
					userService.saveOrUpdate(user);
					Scorerecord scorerecord=new Scorerecord(user.getId(), "发布租房", giveScore, new Date());
					scorerecordService.save(scorerecord);
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * post对接添加租房信息
		 * @param rentinfo
		 * @param trafficInfo
		 */
		private void postRentInfo(Rentinfo rentinfo,TrafficInfo trafficInfo){
			/*PostRentInfo postRentInfo=new PostRentInfo(rentinfo, trafficInfo);;
			String jsonbody=JsonUtil.toJson(postRentInfo);
			LogUtil.Info("提交租房数据信息");
			LogUtil.Info("jsonbody="+jsonbody);
			String response=PostUtil.toUrl(PostUtil.rentjsonurl, jsonbody);
			LogUtil.Info("返回结果="+response);*/
			PostThread postThread=new PostThread("postRentInfo", rentinfo, trafficInfo);
			postThread.start();
		}
		
		/**
		 * post对接删除租房信息
		 * @param rentinfo
		 * @param trafficInfo
		 */
		private void postDelRentInfo(Rentinfo rentinfo){
			/*String urlStr=PostUtil.delrentjsonurl+rentinfo.getId();
			LogUtil.Info("删除租房信息url"+urlStr);
			String response=PostUtil.delelteToUrl(urlStr, null);
			LogUtil.Info("返回结果="+response);*/
			PostThread postThread=new PostThread("postDelRentInfo", rentinfo);
			postThread.start();
		}
		
		
		
		/**
		 * 编辑交通信息
		 * @param trafficInfoId
		 * @param infoId
		 * @param trafficType
		 * @param stationName
		 * @param distance
		 */
		private TrafficInfo editTrafficInfo(Long trafficInfoId,Long infoId,Integer trafficType,String stationName,Integer distance){
			TrafficInfo trafficInfo=null;
			if (trafficInfoId!=null) {
				trafficInfo=trafficInfoService.get(trafficInfoId);
			}else {
				trafficInfo=new TrafficInfo();
			}
			if (trafficInfo!=null) {
				if (infoId!=null){
					trafficInfo.setInfoId(infoId);
				}
				if (trafficType!=null){
					trafficInfo.setTrafficType(trafficType);
				}
				if (stationName!=null&&stationName.length()>0){
					trafficInfo.setStationName(stationName);
				}
				if (distance!=null){
					trafficInfo.setDistance(distance);
				}
				trafficInfoService.saveOrUpdate(trafficInfo);
			}
			return trafficInfo;
		}
		
		/**
		 * 
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/editRentinfoShowState")
		public String editRentinfoShowState(ModelMap modelMap,HttpSession session,Long id,Long userId,Integer showState){
			User user=(User) session.getAttribute("user");
			userId=user.getId();
			Rentinfo rentinfo=null;
			if (id!=null&&showState!=null) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("id", id);
				params.put("userId", userId);
				rentinfo=rentinfoService.getByParam(params);
				if (rentinfo!=null) {
					rentinfo.setShowState(showState);
					rentinfoService.saveOrUpdate(rentinfo);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=0	空数据
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 *删除Rentinfo
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delRentinfo")
		public String delRentinfo(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Rentinfo rentinfo=rentinfoService.get(id);
				if (rentinfo!=null) {
					rentinfoService.delete(rentinfo);
					/**post删除租房*/
					if (id!=null) {
						postDelRentInfo(rentinfo);
					}
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *用户删除自己的Rentinfo
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userdelRentinfo")
		public String userdelRentinfo(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				User user=(User) session.getAttribute("user");
				Rentinfo rentinfo=rentinfoService.get(id);
				if (rentinfo!=null&&user.getId().equals(rentinfo.getUserId())) {
					rentinfoService.delete(rentinfo);
					/**post删除租房*/
					postDelRentInfo(rentinfo);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * web端查询租房信息
		 */
		@ResponseBody
		@RequestMapping("webGetRentinfo")
		public String webGetRentinfo(ModelMap modelMap,HttpSession session,Integer showState,Integer pageIndex,Integer pageNum,Integer pageCount,Double longitud,Double latitude,String area,String houseType,String defectSex,String des,String despms,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			String sql="";
			Map<String, Object> prams=new HashMap<String, Object>();
			prams.put("showState", 0);
			sql="  showState=0 ";
			if (area!=null&&area.length()>0) {
				prams.put("area", area);
				sql=" and area='"+area+"' ";
			}
			if (houseType!=null&&houseType.length()>0) {
				prams.put("houseType", houseType);
				sql=" and houseType='"+houseType+"' ";
			}
			if (defectSex!=null&&defectSex.length()>0) {
				prams.put("defectSex", defectSex);
				if (sql.length()>0) {
					sql=" and "+sql;
				}
				sql="  defectSex='"+defectSex+"' ";
			}
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "readNum"); //默认按阅读量排序
			}
			if (despms!=null&&despms.length()>0) {
				sortPram=new HashMap<String, Object>();
				if (despms.equals("distance")) {//根据距离排序
					if (longitud!=null&&latitude!=null) {
						List<Long> ids=getDistance(longitud, latitude, pageIndex, pageNum, des,sql);
						if (ids!=null) {
							if (prams==null) {
								prams=new HashMap<String, Object>();
							}
							prams.put("id", ids);
						}
					}
				}else {
					sortPram.put(des, despms);
				}
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
				searchPram.put("title", pms);
				searchPram.put("address", pms);
				searchPram.put("area", pms);
				searchPram.put("introduce", pms);
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
			Page page=rentinfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Rentinfo> rentinfoList= (List<Rentinfo>) page.getList();
			if (rentinfoList!=null&&rentinfoList.size()>0) {
				pageCount=rentinfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				for (Rentinfo rentinfo : rentinfoList) {
					setUserInfo(rentinfo);
				}
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, rentinfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 根据距离查询信息
		 */
		public List<Long> getDistance(Double longitud,Double latitude,Integer pageIndex,Integer pageNum,String des,String whereSql){
			if (whereSql.length()>0) {
				whereSql=" where "+whereSql;
			}
			String sql="SELECT id FROM rentinfo "+whereSql+" ORDER BY getdistance("+latitude+","+longitud+",latitude,longitud)  "+des+" LIMIT "+(pageIndex-1)*pageNum+","+pageNum;
			List<Map<String, Object>> listmap = rentinfoService.getBySQL(sql, null);
			if(listmap!=null&&listmap.size()>0){
				List<Long> ids=new ArrayList<Long>();
				for (Map<String, Object> map : listmap) {
					ids.add((Long)map.get("id"));
				}
				return ids;
			}
			return null;
		}
		
		/**
		 * 分页查询Rentinfo
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/getRentinfo")
		public String getRentinfo(ModelMap modelMap,HttpSession session,Long userId,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=null;
			if (userId!=null) {
				prams=new HashMap<String, Object>();
				prams.put("userId", userId);
			}
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "id");
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
				searchPram.put("title", pms);
				searchPram.put("introduce", pms);
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
			Page page=rentinfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Rentinfo> rentinfoList= (List<Rentinfo>) page.getList();
			if (rentinfoList!=null&&rentinfoList.size()>0) {
				pageCount=rentinfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, rentinfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 用户分页查询自己的Rentinfo
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userGetRentinfo")
		public String userGetRentinfo(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=rentinfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Rentinfo> rentinfoList= (List<Rentinfo>) page.getList();
			if (rentinfoList!=null&&rentinfoList.size()>0) {
				pageCount=rentinfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, rentinfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 *根据id查询Rentinfo
		*/
		@ResponseBody
		@RequestMapping("getRentinfoById")
		public String getRentinfoById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Rentinfo rentinfo=rentinfoService.get(id);
				if (rentinfo!=null) {
					Integer readNum=rentinfo.getReadNum();//修改阅读数量
					if (readNum==null||readNum<0) {
						readNum=0;
					}
					rentinfo.setReadNum(readNum+1);
					rentinfoService.saveOrUpdate(rentinfo);
					setUserInfo(rentinfo);
					modelMap.put(CommonInfoUtil.jSONOBJECT, rentinfo);//jsonObject	json对象
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		public void setUserInfo(Rentinfo rentinfo){
			User user=userService.get(rentinfo.getUserId());
			if (user!=null) {
				rentinfo.setUserImg(user.getImg());
				rentinfo.setUserName(user.getName());
				rentinfo.setUserPhone(user.getPhone());
				if (user.getWechatShow()!=null&&user.getWechatShow()==1) {
					rentinfo.setWetChat(user.getWechat());
				}
				checkCollection(rentinfo, user);
			}
		}
		
		/**
		 * 检查是否收藏
		 */
		public void checkCollection(Rentinfo rentinfo,User user){
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("infoId", rentinfo.getId());
			params.put("userId", user.getId());
			params.put("type", 0);
			if (collectionService.getCount(params)>0) {
				rentinfo.setIsColletion(1);
			}else {
				rentinfo.setIsColletion(0);
			}
		}
		
		/**
		 * 查询所有Rentinfo
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllRentinfo")
		public String getAllRentinfo(ModelMap modelMap,HttpSession session){
			List<Rentinfo> rentinfoList= rentinfoService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, rentinfoList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 模糊查询地理
		 * @param modelMap
		 * @param pms
		 * @return
		 */
		@ResponseBody
		@RequestMapping("checkLocationByPms")
		public String checkLocationByPms(ModelMap modelMap,String pms,Integer pageIndex,Integer pageNum){
			if (pms!=null) {
				String sql="SELECT a.location from geography_hk a  where location LIKE '%"+pms+"%' group by location limit "+(pageIndex-1)*pageNum+","+pageNum;
				List<Map<String, Object>> mapsList=rentinfoService.getBySQL(sql, null);
				if(mapsList!=null&&mapsList.size()>0){
					List<String> xiaoquList=new ArrayList<String>();
					for (Map<String, Object> map : mapsList) {
						if(map.get("location")!=null){
							xiaoquList.add(map.get("location").toString());
						}
					}
					modelMap.put(CommonInfoUtil.jSONOBJECTLIST, xiaoquList);		//jsonObjectList json对象集合
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);	
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.NULLDATA);	//空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.PARAMERROR);		//参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 模糊查询区
		 * @param modelMap
		 * @param pms
		 * @return
		 */
		@ResponseBody
		@RequestMapping("checkQuByPms")
		public String checkQuByPms(ModelMap modelMap,String pms,Integer pageIndex,Integer pageNum){
			if (pms!=null) {
				String sql="SELECT a.district from geography_hk a  where district LIKE '%"+pms+"%' group by district limit "+(pageIndex-1)*pageNum+","+pageNum;
				List<Map<String, Object>> mapsList=rentinfoService.getBySQL(sql, null);
				if(mapsList!=null&&mapsList.size()>0){
					List<String> xiaoquList=new ArrayList<String>();
					for (Map<String, Object> map : mapsList) {
						if(map.get("district")!=null){
							xiaoquList.add(map.get("district").toString());
						}
					}
					modelMap.put(CommonInfoUtil.jSONOBJECTLIST, xiaoquList);		//jsonObjectList json对象集合
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);	
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.NULLDATA);	//空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.PARAMERROR);		//参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 模糊查询站名
		 * @param modelMap
		 * @param pms
		 * @return
		 */
		@ResponseBody
		@RequestMapping("checkStationByPms")
		public String checkStationByPms(ModelMap modelMap,String pms,Integer pageIndex,Integer pageNum){
			if (pms!=null) {
				String sql="SELECT a.station_name from traffic_hk a  where station_name LIKE '%"+pms+"%' group by station_name limit "+(pageIndex-1)*pageNum+","+pageNum;
				List<Map<String, Object>> mapsList=rentinfoService.getBySQL(sql, null);
				if(mapsList!=null&&mapsList.size()>0){
					List<String> xiaoquList=new ArrayList<String>();
					for (Map<String, Object> map : mapsList) {
						if(map.get("station_name")!=null){
							xiaoquList.add(map.get("station_name").toString());
						}
					}
					modelMap.put(CommonInfoUtil.jSONOBJECTLIST, xiaoquList);		//jsonObjectList json对象集合
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);	
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.NULLDATA);	//空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.PARAMERROR);		//参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		
		/**
		 * 根据地区查询知识库 返回对应的code
		 * @param pms
		 * @return
		 */
		public String checkAddressByPms(String pms){
			if (pms!=null) {
				String sql="SELECT a.`code` from geography_hk a  where village ='"+pms+"' limit 0,1 ";
				List<Map<String, Object>> mapsList=rentinfoService.getBySQL(sql, null);
				if(mapsList!=null&&mapsList.size()>0){
					Map<String, Object> map=mapsList.get(0);
					if(map.get("code")!=null){
						return map.get("code").toString();
					}	
				}
			}
			return null;
		}
		
		/**
		 * 随机查询租房看了又看(附近的信息)
		 * @param modelMap
		 * @param session
		 * @param pageNum
		 * @param longitud
		 * @param latitude
		 * @return
		 * @throws ParseException
		 */
		@ResponseBody
		@RequestMapping("getRentAlikeInfo")
		public String getRentAlikeInfo(ModelMap modelMap,HttpSession session,Integer pageNum,Double longitud,Double latitude) throws ParseException {
			if (pageNum==null) {
				pageNum=10;
			}
		/*	if (longitud==null) {
				longitud=113.269325d;
			}
			if (latitude==null) {
				latitude=23.131147d;
			}*/
			if (latitude!=null&&longitud!=null) {
					String sql="SELECT id from rentinfo where getdistance("+latitude+","+longitud+",latitude,longitud) <5000 ORDER BY RAND() LIMIT 0,"+pageNum;
					List<Map<String, Object>> listmap = rentinfoService.getBySQL(sql, null);
					if(listmap!=null&&listmap.size()>0){
						List<Long> ids=new ArrayList<Long>();
						for (Map<String, Object> map : listmap) {
							if (map.get("id")!=null) {
								BigInteger bigInteger=(BigInteger) map.get("id");
								ids.add(bigInteger.longValue());
							}
						}
						Map<String, Object> params=new HashMap<String, Object>();
						params.put("id", ids);
						List<Rentinfo>  rentinfos=rentinfoService.getByParams(params);
						modelMap.put(CommonInfoUtil.jSONOBJECTLIST, rentinfos);
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS); //1
					}else {
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//0
					}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//-1
			}
			return JsonUtil.toJson(modelMap);
		}
		
}
