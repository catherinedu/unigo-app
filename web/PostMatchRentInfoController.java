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

import com.xjt.service.PostMatchRentInfoService;
import com.xjt.service.RentinfoService;
import com.xjt.service.UsersubscriptionService;
import com.xjt.chat.PushMsg;
import com.xjt.chat.PushUtil;
import com.xjt.entity.PostMatchRentInfo;
import com.xjt.entity.Rentinfo;
import com.xjt.entity.User;
import com.xjt.entity.Usersubscription;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* post保存的订阅租房信息控制层
* @author Administrator
*/
@Controller
public class PostMatchRentInfoController{

		@Autowired
		private PostMatchRentInfoService postMatchRentInfoService;
		@Autowired
		private UsersubscriptionService usersubscriptionService;
		@Autowired
		private RentinfoService rentinfoService;
		
		/**
		 *添加或编辑post保存的订阅租房信息
		*/
		@ResponseBody
		@RequestMapping("addOrEditPostMatchRentInfo")
		public String addOrEditPostMatchRentInfo(ModelMap modelMap,HttpSession session,Long id,Long userId,Integer readState,Long infoId,String title,Integer price,Integer roomNumber,Integer area,Integer elevator,Integer faceto,Integer fitment,String city,String district,String location,String address,String release_time,Integer period,Integer gender,String imgs,String videoUrl,String introduce){
			if(userId!=null){
				PostMatchRentInfo postMatchRentInfo=null;
				if (id!=null) {
					postMatchRentInfo=postMatchRentInfoService.get(id);
				}else {
					postMatchRentInfo=new PostMatchRentInfo();
					postMatchRentInfo.setReadState(0);
				}
				if (postMatchRentInfo!=null) {
					postMatchRentInfo.setUserId(userId);
					if (infoId!=null){
						postMatchRentInfo.setInfoId(infoId);
					}
					if (title!=null&&title.length()>0){
						postMatchRentInfo.setTitle(title);
					}
					if (price!=null){
						postMatchRentInfo.setPrice(price);
					}
					if (roomNumber!=null){
						postMatchRentInfo.setRoomNumber(roomNumber);
					}
					if (area!=null){
						postMatchRentInfo.setArea(area);
					}
					if (elevator!=null){
						postMatchRentInfo.setElevator(elevator);
					}
					if (faceto!=null){
						postMatchRentInfo.setFaceto(faceto);
					}
					if (fitment!=null){
						postMatchRentInfo.setFitment(fitment);
					}
					if (city!=null&&city.length()>0){
						postMatchRentInfo.setCity(city);
					}
					if (district!=null&&district.length()>0){
						postMatchRentInfo.setDistrict(district);
					}
					if (location!=null&&location.length()>0){
						postMatchRentInfo.setLocation(location);
					}
					if (address!=null&&address.length()>0){
						postMatchRentInfo.setAddress(address);
					}
					if (release_time!=null&&release_time.length()>0){
						postMatchRentInfo.setRelease_time(release_time);
					}
					if (period!=null){
						postMatchRentInfo.setPeriod(period);
					}
					if (gender!=null){
						postMatchRentInfo.setGender(gender);
					}
					if (imgs!=null&&imgs.length()>0){
						postMatchRentInfo.setImgs(imgs);
					}
					if (videoUrl!=null&&videoUrl.length()>0){
						postMatchRentInfo.setVideoUrl(videoUrl);
					}
					if (introduce!=null&&introduce.length()>0){
						postMatchRentInfo.setIntroduce(introduce);
					}
					postMatchRentInfoService.saveOrUpdate(postMatchRentInfo);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
					//消息提醒
					sendSubMsg(userId);
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 有最新租房订阅消息时  推送
		 * @param userId
		 */
		public void sendSubMsg(Long userId){
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("subType", 0);
			params.put("userId", userId);
			List<Usersubscription> usersubscriptions =  usersubscriptionService.getByParams(params); //查询订阅的用户
			if (usersubscriptions!=null&&usersubscriptions.size()>0) {
				PushMsg pushMsg=new PushMsg(4, null, null, null, null, "您有最新的租房订阅消息", 0);
				for (Usersubscription usersubscription : usersubscriptions) {
					PushUtil.sendMsgToUser(usersubscription.getUserId(), pushMsg);
				}
			}
		}
		
		/**
		 *删除post保存的订阅租房信息
		*/
		@ResponseBody
		@RequestMapping("delPostMatchRentInfo")
		public String delPostMatchRentInfo(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				PostMatchRentInfo postMatchRentInfo=postMatchRentInfoService.get(id);
				if (postMatchRentInfo!=null) {
					postMatchRentInfoService.delete(postMatchRentInfo);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询post保存的订阅租房信息
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/getPostMatchRentInfo")
		public String getPostMatchRentInfo(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=postMatchRentInfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<PostMatchRentInfo> postMatchRentInfoList= (List<PostMatchRentInfo>) page.getList();
			if (postMatchRentInfoList!=null&&postMatchRentInfoList.size()>0) {
				pageCount=postMatchRentInfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				postGetRentinfo(postMatchRentInfoList);
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, postMatchRentInfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * post信息转换称rentInfo
		 * @param postMatchRentInfoList
		 */
		private void postGetRentinfo(List<PostMatchRentInfo> postMatchRentInfoList){
			Rentinfo rentinfo=null;
			for (PostMatchRentInfo postMatchRentInfo : postMatchRentInfoList) {
				if (postMatchRentInfo!=null) {
					if (postMatchRentInfo.getInfoId()!=null) {
						rentinfo=rentinfoService.get(postMatchRentInfo.getInfoId());
					}else {
						rentinfo=new Rentinfo(postMatchRentInfo);
					}
					postMatchRentInfo.setRentinfo(rentinfo);
				}
			}
		}

		/**
		 *根据id查询post保存的订阅租房信息
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/getPostMatchRentInfoById")
		public String getPostMatchRentInfoById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				User user=(User) session.getAttribute("user");
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("userId", user.getId());
				params.put("id", id);
				PostMatchRentInfo postMatchRentInfo=postMatchRentInfoService.getByParam(params);
				if (postMatchRentInfo!=null) {
					Rentinfo rentinfo=null;
					if (postMatchRentInfo.getInfoId()!=null) {
						 rentinfo=rentinfoService.get(postMatchRentInfo.getInfoId());
					}else {
						rentinfo=new Rentinfo(postMatchRentInfo);
					}
					postMatchRentInfo.setRentinfo(rentinfo);;
					modelMap.put(CommonInfoUtil.jSONOBJECT, postMatchRentInfo);//jsonObject	json对象
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
		 * 查询所有post保存的订阅租房信息
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllPostMatchRentInfo")
		public String getAllPostMatchRentInfo(ModelMap modelMap,HttpSession session){
			List<PostMatchRentInfo> postMatchRentInfoList= postMatchRentInfoService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, postMatchRentInfoList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
