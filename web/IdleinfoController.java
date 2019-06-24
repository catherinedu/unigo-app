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

import com.xjt.post.PostIdleinfo;
import com.xjt.post.PostThread;
import com.xjt.post.PostUser;
import com.xjt.post.PostUtil;
import com.xjt.post.entity.ItemScores;
import com.xjt.post.entity.Items;
import com.xjt.service.CollectionService;
import com.xjt.service.IdleinfoService;
import com.xjt.service.PostDataService;
import com.xjt.service.ScorerecordService;
import com.xjt.service.UserService;
import com.xjt.entity.Collection;
import com.xjt.entity.Idleinfo;
import com.xjt.entity.PostData;
import com.xjt.entity.Scorerecord;
import com.xjt.entity.User;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.LogUtil;
import com.xjt.util.Page;

/**
* Idleinfo控制层
* @author Administrator
*/
@Controller
public class IdleinfoController{

		@Autowired
		private IdleinfoService idleinfoService;
		@Autowired
		private UserService userService;
		@Autowired
		private CollectionService collectionService;
		@Autowired
		private PostDataService postDataService;
		@Autowired
		private ScorerecordService scorerecordService;
		/**
		 *添加或编辑Idleinfo
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditIdleinfo")
		public String addOrEditIdleinfo(ModelMap modelMap,HttpSession session,Long id,Long userId,String address,String title,String area,Integer price,String readImgs,String imgs,String videoUrl,String introduce,String type,Double longitud,Double latitude,Integer showState){
			User user=(User) session.getAttribute("user");
			Idleinfo idleinfo=null;
			if (id!=null) {
				idleinfo=idleinfoService.get(id);
			}else {
				idleinfo=new Idleinfo();
				idleinfo.setGmtCreated(new Date());
				idleinfo.setUserId(user.getId());
				idleinfo.setDisNum(0);
				idleinfo.setReadNum(0);
			}
			if (idleinfo!=null&&user.getId().equals(idleinfo.getUserId())) {
				if (title!=null&&title.length()>0){
					idleinfo.setTitle(title);
				}
				if (area!=null&&area.length()>0){
					idleinfo.setArea(area);
				}
				if (price!=null){
					idleinfo.setPrice(price);
				}
				if (imgs!=null&&imgs.length()>0){
					idleinfo.setImgs(imgs);
				}
				if (readImgs!=null&&readImgs.length()>0) {
					idleinfo.setReadImgs(readImgs);
				}
				if (videoUrl!=null&&videoUrl.length()>0){
					idleinfo.setVideoUrl(videoUrl);
				}
				if (introduce!=null&&introduce.length()>0){
					idleinfo.setIntroduce(introduce);
				}
				if (type!=null&&type.length()>0){
					idleinfo.setType(type);
				}
				if (longitud!=null){
					idleinfo.setLongitud(longitud);
				}
				if (latitude!=null){
					idleinfo.setLatitude(latitude);
				}
				if (showState!=null){
					idleinfo.setShowState(showState);
				}
				if (address!=null&&address.length()>0) {
					idleinfo.setAddress(address);
				}
				idleinfoService.saveOrUpdate(idleinfo);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
				
				//PostData postData=null;
				if (id!=null) {
					//postData=PostIdleinfo.postEditIdelInfo(idleinfo);/**编辑 post对接*/
					PostThread postUserThread=new PostThread("postEditIdelInfo", idleinfo, postDataService); //开启线程
					postUserThread.start();
				}else {
					//postData=PostIdleinfo.postAddIdelInfo(idleinfo);/**添加 post对接*/
					PostThread postUserThread=new PostThread("postAddIdelInfo", idleinfo, postDataService); //开启线程
					postUserThread.start();
				}
				//savePostData(postData);
				
				if (id==null) {
					Integer score=user.getScore();		//发布闲置 添加10积分
					Integer giveScore=10;
					if (score==null||score<0) {
						score=0;
					}
					if (user.getCheckState()!=null&&user.getCheckState()==2) {//高级认证1.5倍积分
						giveScore=(int) (giveScore*1.5);
					}
					user.setScore(score+giveScore);
					userService.saveOrUpdate(user);
					Scorerecord scorerecord=new Scorerecord(user.getId(), "发布闲置", giveScore, new Date());
					scorerecordService.save(scorerecord);
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 保存PostData
		 * @param postData
		 */
		public void savePostData(PostData postData){
			if (postData!=null) {
				postDataService.save(postData);
			}
		}
		
		/**
		 *删除Idleinfo
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delIdleinfo")
		public String delIdleinfo(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Idleinfo idleinfo=idleinfoService.get(id);
				if (idleinfo!=null) {
					idleinfoService.delete(idleinfo);
					/*PostData postData=PostIdleinfo.postDelIdelInfo(idleinfo);*//**删除 post对接*//*
					savePostData(postData);*/
					PostThread postUserThread=new PostThread("postDelIdelInfo", idleinfo, postDataService); //开启线程
					postUserThread.start();
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 *用户删除自己的Idleinfo
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userdelIdleinfo")
		public String userdelIdleinfo(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				User user=(User) session.getAttribute("user");
				Idleinfo idleinfo=idleinfoService.get(id);
				if (idleinfo!=null&&idleinfo.getUserId().equals(user.getId())) {
					idleinfoService.delete(idleinfo);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
					/*PostData postData=PostIdleinfo.postDelIdelInfo(idleinfo);*//**删除 post对接*//*
					savePostData(postData);*/
					PostThread postUserThread=new PostThread("postDelIdelInfo", idleinfo, postDataService); //开启线程
					postUserThread.start();
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=-1 参数错误
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Idleinfo
		*/
		@ResponseBody
		@RequestMapping("webgetIdleinfo")
		public String webgetIdleinfo(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,Double longitud,Double latitude,String type,String area,String des,String despms,String pms,String start,String end) throws ParseException {
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
				sql="and  area='"+area+"' ";
			}
			if (type!=null&&type.length()>0) {
				prams.put("type", type);
				sql="and  type='"+type+"' ";
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
			Page page=idleinfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Idleinfo> idleinfoList= (List<Idleinfo>) page.getList();
			if (idleinfoList!=null&&idleinfoList.size()>0) {
				pageCount=idleinfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				for (Idleinfo idleinfo : idleinfoList) {
					setUserInfo(idleinfo);
				}
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, idleinfoList);		//jsonObjectList json对象集合
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
			String sql="SELECT id FROM idleinfo "+whereSql+"  ORDER BY getdistance("+latitude+","+longitud+",latitude,longitud)  "+des+" LIMIT "+(pageIndex-1)*pageNum+","+pageNum;
			List<Map<String, Object>> listmap = idleinfoService.getBySQL(sql, null);
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
		 * 分页查询Idleinfo
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/getIdleinfo")
		public String getIdleinfo(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=idleinfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Idleinfo> idleinfoList= (List<Idleinfo>) page.getList();
			if (idleinfoList!=null&&idleinfoList.size()>0) {
				pageCount=idleinfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, idleinfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Idleinfo
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userGetIdleinfo")
		public String userGetIdleinfo(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=idleinfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Idleinfo> idleinfoList= (List<Idleinfo>) page.getList();
			if (idleinfoList!=null&&idleinfoList.size()>0) {
				pageCount=idleinfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, idleinfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		/**
		 *根据id查询Idleinfo
		*/
		@ResponseBody
		@RequestMapping("getIdleinfoById")
		public String getIdleinfoById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Idleinfo idleinfo=idleinfoService.get(id);
				if (idleinfo!=null) {
					Integer readNum=idleinfo.getReadNum();
					if (readNum==null||readNum<0) {
						readNum=0;
					}
					idleinfo.setReadNum(readNum+1);
					idleinfoService.saveOrUpdate(idleinfo);
					setUserInfo(idleinfo);
					if(session.getAttribute("user")!=null){
						checkCollection(idleinfo,(User)session.getAttribute("user"));
					}
					modelMap.put(CommonInfoUtil.jSONOBJECT, idleinfo);//jsonObject	json对象
					modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );
					if (session.getAttribute("user")!=null) {
						User user=(User) session.getAttribute("user");
						/*PostData postData=PostIdleinfo.postCheckIdelInfo(idleinfo.getId(), user.getId());
						savePostData(postData);*/
						PostThread postUserThread=new PostThread("postCheckIdelInfo", user,idleinfo, postDataService); //开启线程
						postUserThread.start();
					}	
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询用户信息
		 * @param idleinfo
		 */
		public void setUserInfo(Idleinfo idleinfo){
			User user=userService.get(idleinfo.getUserId());
			if (user!=null) {
				idleinfo.setUserImg(user.getImg());
				idleinfo.setUserName(user.getName());
				idleinfo.setUserPhone(user.getPhone());
				if (user.getWechatShow()!=null&&user.getWechatShow()==1) {
					idleinfo.setWetChat(user.getWechat());
				}
			}
			
		}
		/**
		 * 检查是否收藏
		 */
		public void checkCollection(Idleinfo idleinfo,User user){
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("infoId", idleinfo.getId());
			params.put("userId", user.getId());
			params.put("type", 1);
			if (collectionService.getCount(params)>0) {
				idleinfo.setIsColletion(1);
			}else {
				idleinfo.setIsColletion(0);
			}
		}

		/**
		 * 查询所有Idleinfo
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllIdleinfo")
		public String getAllIdleinfo(ModelMap modelMap,HttpSession session){
			List<Idleinfo> idleinfoList= idleinfoService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, idleinfoList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 对用户进行闲置信息推荐
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetPostRecIdle")
		public String userGetPostRecIdle(ModelMap modelMap,HttpSession session,Long id){
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.NULLDATA);//默认空数据
			ItemScores tItemScores=null;
			if (session.getAttribute("user")!=null) {
				User user=(User) session.getAttribute("user");
				tItemScores=PostUser.postUserRecInfo(user);
			}else if (id!=null) {
				tItemScores=PostIdleinfo.postGetLikeIdelInfo(id);
			}
			if (tItemScores!=null&&tItemScores.getItemScores()!=null&&tItemScores.getItemScores().size()>0) {
				List<Long> ids=new ArrayList<Long>();
				for (Items items : tItemScores.getItemScores()) {
					if (!items.getItem().contains("i")) {
						ids.add(Long.valueOf(items.getItem()));
					}
				}
				if (ids.size()>0) {
					List<Idleinfo> idleinfoList=idleinfoService.getByIds(ids);
					if (idleinfoList!=null&&idleinfoList.size()>0) {
						modelMap.put(CommonInfoUtil.jSONOBJECTLIST, idleinfoList);		//jsonObjectList json对象集合
						modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);	
					}	
				}
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 根据闲置信息id查询相似推荐信息
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getPostRecLikeIdle")
		public String getPostRecLikeIdle(ModelMap modelMap,HttpSession session,Long id){
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.NULLDATA);//默认空数据
			ItemScores tItemScores=PostIdleinfo.postGetLikeIdelInfo(id);
			if (tItemScores!=null&&tItemScores.getItemScores()!=null&&tItemScores.getItemScores().size()>0) {
				List<Long> ids=new ArrayList<Long>();
				for (Items items : tItemScores.getItemScores()) {
					if (!items.getItem().contains("i")) {
						ids.add(Long.valueOf(items.getItem()));
					}
				}
				if (ids.size()>0) {
					List<Idleinfo> idleinfoList=idleinfoService.getByIds(ids);
					if (idleinfoList!=null&&idleinfoList.size()>0) {
						modelMap.put(CommonInfoUtil.jSONOBJECTLIST, idleinfoList);		//jsonObjectList json对象集合
						modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);	
					}	
				}
			}
			return JsonUtil.toJson(modelMap);
		}

}
