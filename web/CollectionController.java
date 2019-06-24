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

import com.xjt.post.PostIdleinfo;
import com.xjt.post.PostThread;
import com.xjt.service.CollectionService;
import com.xjt.service.IdleinfoService;
import com.xjt.service.JobinfoService;
import com.xjt.service.PostDataService;
import com.xjt.service.RentinfoService;
import com.xjt.entity.Collection;
import com.xjt.entity.Idleinfo;
import com.xjt.entity.PostData;
import com.xjt.entity.User;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Collection控制层
* @author Administrator
*/
@Controller
public class CollectionController{

		@Autowired
		private CollectionService collectionService;
		@Autowired
		private RentinfoService rentinfoService;
		@Autowired
		private IdleinfoService idleinfoService;
		@Autowired
		private JobinfoService jobinfoService;
		@Autowired
		private PostDataService postDataService;
		/**
		 *添加或编辑Collection
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditCollection")
		public String addOrEditCollection(ModelMap modelMap,HttpSession session,Long id,Integer type,Long infoId,Long userId){
			User user=(User) session.getAttribute("user");
			userId=user.getId();
			if (infoId!=null&&type!=null) {
				Collection collection=checkUserCollection(type, infoId, userId);
				if (collection==null) {
					collection=new Collection();
					collection.setGmtCreated(new Date());
					collection.setUserId(user.getId());
					collection.setType(type);
					collection.setInfoId(infoId);
					collectionService.saveOrUpdate(collection);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
					if (type==1) {//收藏闲置
						/*PostData postData=PostIdleinfo.postCollectIdelInfo(infoId, userId);*//**post对接 收藏闲置*//*
						savePostData(postData);*/
						Idleinfo idleinfo=new Idleinfo();
						idleinfo.setId(infoId);
						PostThread postUserThread=new PostThread("postCheckIdelInfo", user,idleinfo, postDataService); //开启线程
						postUserThread.start();
					}
				}else {
					collectionService.delete(collection);//已取消
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.CANCEL);//msg=2  //取消
				}
				
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=1
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
		 * 查询用户是否收藏
		 * @param type
		 * @param infoId
		 * @param userId
		 * @return
		 */
		public Collection checkUserCollection(Integer type,Long infoId,Long userId){
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("type", type);
			params.put("infoId", infoId);
			params.put("userId", userId);
			return collectionService.getByParam(params);
		}

		/**
		 *删除Collection
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/delCollection")
		public String delCollection(ModelMap modelMap,HttpSession session,Long id){
			User user=(User) session.getAttribute("user");
			if (id!=null) {
				Collection collection=collectionService.get(id);
				if (collection!=null&&user.getId().equals(collection.getUserId())) {
					collectionService.delete(collection);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0
				}	
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Collection
		*/
		@ResponseBody
		@RequestMapping("getCollection")
		public String getCollection(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=collectionService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Collection> collectionList= (List<Collection>) page.getList();
			if (collectionList!=null&&collectionList.size()>0) {
				pageCount=collectionService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, collectionList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 分页查询Collection
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userGetCollection")
		public String userGetCollection(ModelMap modelMap,HttpSession session,Integer type,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			User user=(User) session.getAttribute("user");
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=new HashMap<String, Object>();
			prams.put("userId", user.getId());
			if (type!=null) {
				prams.put("type", type);
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
			Page page=collectionService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Collection> collectionList= (List<Collection>) page.getList();
			if (collectionList!=null&&collectionList.size()>0) {
				pageCount=collectionService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				getCollectionInfo(collectionList);
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, collectionList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询收藏的信息
		 * @param collectionList
		 */
		public void getCollectionInfo(List<Collection> collectionList){
			for (Collection collection : collectionList) {
				if (collection.getType()==0) {//租房
					collection.setCollectionInfo(rentinfoService.get(collection.getInfoId()));
				}else if (collection.getType()==1) {//闲置
					collection.setCollectionInfo(idleinfoService.get(collection.getInfoId()));
				}else if (collection.getType()==2) {//求职
					collection.setCollectionInfo(jobinfoService.get(collection.getInfoId()));
				}
			}
		}

		/**
		 *根据id查询Collection
		*/
		@ResponseBody
		@RequestMapping("getCollectionById")
		public String getCollectionById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Collection collection=collectionService.get(id);
				if (collection!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, collection);//jsonObject	json对象
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
		 * 查询所有Collection
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllCollection")
		public String getAllCollection(ModelMap modelMap,HttpSession session){
			List<Collection> collectionList= collectionService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, collectionList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
