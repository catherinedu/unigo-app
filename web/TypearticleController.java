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
import com.xjt.service.TypearticleService;
import com.xjt.service.UserreadrecordService;
import com.xjt.entity.Columntype;
import com.xjt.entity.Typearticle;
import com.xjt.entity.User;
import com.xjt.entity.Userreadrecord;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* Typearticle控制层
* @author Administrator
*/
@Controller
public class TypearticleController{

		@Autowired
		private TypearticleService typearticleService;
		@Autowired
		private UserreadrecordService userreadrecordService;
		@Autowired
		private ColumntypeService columntypeService;
		/**
		 *添加或编辑Typearticle
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/addOrEditTypearticle")
		public String addOrEditTypearticle(ModelMap modelMap,HttpSession session,Long id,Long typeId,String title,String introduce,String content,String img,Integer readNum,Integer discussNum,String author,Integer number){
			Typearticle typearticle=null;
			if (id!=null) {
				typearticle=typearticleService.get(id);
			}else {
				typearticle=new Typearticle();
				typearticle.setGmtCreated(new Date());
				readNum=0;
				discussNum=0;
				number=65535;
			}
			if (typearticle!=null) {
				if (typeId!=null){
					typearticle.setTypeId(typeId);
				}
				if (title!=null&&title.length()>0){
					typearticle.setTitle(title);
				}
				if (introduce!=null&&introduce.length()>0) {
					typearticle.setIntroduce(introduce);
				}
				if (content!=null&&content.length()>0){
					typearticle.setContent(content);
				}
				if (img!=null&&img.length()>0){
					typearticle.setImg(img);
				}
				if (readNum!=null){
					typearticle.setReadNum(readNum);
				}
				if (discussNum!=null){
					typearticle.setDiscussNum(discussNum);
				}
				if (author!=null&&author.length()>0){
					typearticle.setAuthor(author);
				}
				if (number!=null){
					typearticle.setNumber(number);
				}
				typearticleService.saveOrUpdate(typearticle);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除Typearticle
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delTypearticle")
		public String delTypearticle(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Typearticle typearticle=typearticleService.get(id);
				if (typearticle!=null) {
					typearticleService.delete(typearticle);
					String sql="DELETE  FROM userreadrecord WHERE articleId="+id;//删除已阅信息
					userreadrecordService.getBySQL(sql);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Typearticle
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/getTypearticle")
		public String getTypearticle(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
				searchPram.put("content", pms);
				searchPram.put("author", pms);
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
			Page page=typearticleService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Typearticle> typearticleList= (List<Typearticle>) page.getList();
			if (typearticleList!=null&&typearticleList.size()>0) {
				pageCount=typearticleService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				for (Typearticle typearticle : typearticleList) {
					Columntype columntype=columntypeService.get(typearticle.getTypeId());
					if (columntype!=null) {
						typearticle.setType(columntype.getName());
					}else {
						typearticle.setType("");
					}
				}
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, typearticleList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询Typearticle
		*/
		@ResponseBody
		@RequestMapping("appGetTypearticle")
		public String appGetTypearticle(ModelMap modelMap,HttpSession session,Long typeId,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=null;
			if (typeId!=null) {
				prams=new HashMap<String, Object>();
				prams.put("typeId", typeId);
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
			Page page=typearticleService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Typearticle> typearticleList= (List<Typearticle>) page.getList();
			if (typearticleList!=null&&typearticleList.size()>0) {
				pageCount=typearticleService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, typearticleList);		//jsonObjectList json对象集合
				for (Typearticle typearticle : typearticleList) {
					typearticle.setContent(null);
					typearticle.setTimeStr(DateUtil.getDateLenthStr(typearticle.getGmtCreated(), new Date()));
					typearticle.setGmtCreated(null);
				}
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		
		
		/**
		 *根据id查询Typearticle
		*/
		@ResponseBody
		@RequestMapping("getTypearticleById")
		public String getTypearticleById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Typearticle typearticle=typearticleService.get(id);
				if (typearticle!=null) {
					Integer readNum=typearticle.getReadNum();
					if (readNum==null||readNum<0) {
						readNum=0;
					}
					typearticle.setReadNum(readNum+1);
					typearticleService.saveOrUpdate(typearticle);
					if (session.getAttribute("user")!=null) {
						User user=(User) session.getAttribute("user");
						addReadRecord(user, typearticle);
					}
					modelMap.put(CommonInfoUtil.jSONOBJECT, typearticle);//jsonObject	json对象
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
		 * 添加文章阅读记录
		 * @param user
		 * @param typearticle
		 */
		public void addReadRecord(User user,Typearticle typearticle){
			if (user!=null&&typearticle!=null) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("userId", user.getId());
				params.put("articleId", typearticle.getId());
				if (userreadrecordService.getCount(params)<=0) {
					Userreadrecord userreadrecord=new Userreadrecord(typearticle.getTypeId(), typearticle.getId(), new Date(), user.getId());
					userreadrecordService.save(userreadrecord);
				}
			}
		}
		
		/**
		 * 查询所有Typearticle
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllTypearticle")
		public String getAllTypearticle(ModelMap modelMap,HttpSession session){
			List<Typearticle> typearticleList= typearticleService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, typearticleList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
