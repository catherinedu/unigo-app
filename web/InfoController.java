package com.xjt.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjt.entity.CloseInfo;
import com.xjt.entity.Idleinfo;
import com.xjt.service.IdleinfoService;
import com.xjt.service.JobinfoService;
import com.xjt.service.RentinfoService;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
 * 信息控制层
 * @author Administrator
 *
 */
@Controller
public class InfoController {
	
	@Autowired
	private IdleinfoService idleinfoService;
	@Autowired
	private RentinfoService rentinfoService;
	@Autowired
	private JobinfoService jobinfoService;
	
	/**
	 * 查询附近信息
	 * @param modelMap
	 * @param session
	 * @param pageIndex
	 * @param pageNum
	 * @param pageCount
	 * @param longitud
	 * @param latitude
	 * @return
	 * @throws ParseException
	 */
	@ResponseBody
	@RequestMapping("getCloseInfo")
	public String getCloseInfo(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,Double longitud,Double latitude) throws ParseException {
		if (pageIndex==null) {
			pageIndex=1;
		}
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
				String sql="SELECT a.id,a.gmtCreated,a.type,a.imgs,getdistance("+latitude+","+longitud+",latitude,longitud) AS juli"+
								/*	"ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN(("+latitude+"*PI()/180-a.latitude*PI()/180)/2),2)"+
																		"+COS("+latitude+"*PI()/180)*COS(a.latitude*PI()/180)"+
																		"*POW(SIN(("+longitud+"*PI()/180-a.longitud*PI()/180)/2),2))) * 1000) AS juli"+*/
							 " FROM "+
							  " ( "+
								"SELECT id,longitud,latitude,gmtCreated,2 as type,excImg as imgs  FROM jobinfo "+
							    " UNION  ALL"+
								" SELECT id,longitud,latitude,gmtCreated,0 as type, imgs   FROM rentinfo where showState=0 " +
								" UNION  ALL"+
								" SELECT id,longitud,latitude,gmtCreated,1 as type ,imgs FROM idleinfo where showState=0  ) a "+
							" HAVING juli<5000 "+
							" ORDER BY a.gmtCreated desc, juli LIMIT "+(pageIndex-1)*pageNum+","+pageNum;
				List<Map<String, Object>> listmap = jobinfoService.getBySQL(sql, null);
				if(listmap!=null&&listmap.size()>0){
					String key;
					List<CloseInfo> closeInfos=new ArrayList<CloseInfo>();
					for (Map<String, Object> map : listmap) {
						closeInfos.add(new CloseInfo(map.get("id"), map.get("type"), map.get("imgs"), map.get("gmtCreated"), map.get("juli")));
					}
					modelMap.put(CommonInfoUtil.jSONOBJECTLIST, closeInfos);
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
