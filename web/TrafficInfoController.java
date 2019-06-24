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
import com.xjt.service.TrafficInfoService;
import com.xjt.entity.TrafficInfo;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* 交通信息控制层
* @author Administrator
*/
@Controller
public class TrafficInfoController{

		@Autowired
		private TrafficInfoService trafficInfoService;

		/**
		 *添加或编辑交通信息
		*/
		@ResponseBody
		@RequestMapping("addOrEditTrafficInfo")
		public String addOrEditTrafficInfo(ModelMap modelMap,HttpSession session,Long id,Long infoId,Integer trafficType,String stationName,Integer distance){
			TrafficInfo trafficInfo=null;
			if (id!=null) {
				trafficInfo=trafficInfoService.get(id);
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
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除交通信息
		*/
		@ResponseBody
		@RequestMapping("delTrafficInfo")
		public String delTrafficInfo(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				TrafficInfo trafficInfo=trafficInfoService.get(id);
				if (trafficInfo!=null) {
					trafficInfoService.delete(trafficInfo);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询交通信息
		*/
		@ResponseBody
		@RequestMapping("getTrafficInfo")
		public String getTrafficInfo(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=trafficInfoService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<TrafficInfo> trafficInfoList= (List<TrafficInfo>) page.getList();
			if (trafficInfoList!=null&&trafficInfoList.size()>0) {
				pageCount=trafficInfoService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, trafficInfoList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询交通信息
		*/
		@ResponseBody
		@RequestMapping("getTrafficInfoById")
		public String getTrafficInfoById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				TrafficInfo trafficInfo=trafficInfoService.get(id);
				if (trafficInfo!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, trafficInfo);//jsonObject	json对象
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
		 * 查询所有交通信息
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllTrafficInfo")
		public String getAllTrafficInfo(ModelMap modelMap,HttpSession session){
			List<TrafficInfo> trafficInfoList= trafficInfoService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, trafficInfoList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
}
