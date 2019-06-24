package com.xjt.web;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xjt.service.MenuclickService;
import com.xjt.entity.Menuclick;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.FileUploadUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.Page;

/**
* 菜单点击信息控制层
* @author Administrator
*/
@Controller
public class MenuclickController{

		@Autowired
		private MenuclickService menuclickService;

		/**
		 *添加或编辑菜单点击信息
		 * @throws ParseException 
		*/
		@ResponseBody
		@RequestMapping("addOrEditMenuclick")
		public String addOrEditMenuclick(ModelMap modelMap,HttpSession session,Integer type) throws ParseException{
			if (type!=null) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("type", type);
				params.put("gmtCreated", DateUtil.simpdfyMd.parse(DateUtil.simpdfyMd.format(new Date())));
				Menuclick menuclick=menuclickService.getByParam(params);
				if (menuclick==null) {
					menuclick=new Menuclick(type, 0, new Date());
				}
				menuclick.setNum(menuclick.getNum()+1);
				menuclickService.saveOrUpdate(menuclick);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *删除菜单点击信息
		*/
		@ResponseBody
		@RequestMapping("delMenuclick")
		public String delMenuclick(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Menuclick menuclick=menuclickService.get(id);
				if (menuclick!=null) {
					menuclickService.delete(menuclick);
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询菜单点击信息
		*/
		@ResponseBody
		@RequestMapping("getMenuclick")
		public String getMenuclick(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
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
			Page page=menuclickService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<Menuclick> menuclickList= (List<Menuclick>) page.getList();
			if (menuclickList!=null&&menuclickList.size()>0) {
				pageCount=menuclickService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, menuclickList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 *根据id查询菜单点击信息
		*/
		@ResponseBody
		@RequestMapping("getMenuclickById")
		public String getMenuclickById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				Menuclick menuclick=menuclickService.get(id);
				if (menuclick!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, menuclick);//jsonObject	json对象
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
		 * 查询所有菜单点击信息
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getAllMenuclick")
		public String getAllMenuclick(ModelMap modelMap,HttpSession session){
			List<Menuclick> menuclickList= menuclickService.getByParams(null);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, menuclickList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询昨天点击量
		 * @param modelMap
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getYesterdayClickDataInfo")
		public String getYesterdayClickDataInfo(ModelMap modelMap){
			Date today=new Date();//今天
			Date yestoday=DateUtil.getCalendarByAdd(today, Calendar.DAY_OF_MONTH, -1);//昨天
			String yestodaystr=DateUtil.simpdfyMd.format(yestoday);
			List<Object> dataList=new ArrayList<Object>();
			for (int i = 0; i < 7; i++) {
				dataList.add(getRegClickCountByType(yestodaystr, yestodaystr, i));
			}
			modelMap.put("dataList", dataList);
			return JsonUtil.toJson(modelMap);
		}
		
		
		
		/**
		 * 查询某天数内的点击数据量
		 * @param modelMap
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getMenuClickDataInfoByDayNum")
		public String getMenuClickDataInfoByDayNum(ModelMap modelMap,HttpSession session,Integer dayNum,Date checkDate){
			if (dayNum!=null&&dayNum>0) {
				List<Object> dataList0=new ArrayList<Object>();//0 租房
				List<Object> dataList1=new ArrayList<Object>();//1求职
				List<Object> dataList2=new ArrayList<Object>();//2闲置
				List<Object> dataList3=new ArrayList<Object>();//3搜索
				List<Object> dataList4=new ArrayList<Object>();//4订阅分类
				List<Object> dataList5=new ArrayList<Object>();//5发布
				List<Object> dataList6=new ArrayList<Object>();//6我的
				List<String> ymdList=new ArrayList<String>();//日期
				if (checkDate==null) {
					checkDate=DateUtil.getCalendarByAdd(new Date(), Calendar.DAY_OF_MONTH, 1);
				}
				Date today=DateUtil.getCalendarByAdd(checkDate, Calendar.DAY_OF_MONTH, dayNum*-1);
				for (int i = 0; i <dayNum; i++) {
					String sdate=DateUtil.simpdfyMd.format(today);
					dataList0.add(getRegClickCountByType(sdate, sdate, 0));
					dataList1.add(getRegClickCountByType(sdate, sdate, 1));
					dataList2.add(getRegClickCountByType(sdate, sdate, 2));
					dataList3.add(getRegClickCountByType(sdate, sdate, 3));
					dataList4.add(getRegClickCountByType(sdate, sdate, 4));
					dataList5.add(getRegClickCountByType(sdate, sdate, 5));
					dataList6.add(getRegClickCountByType(sdate, sdate, 6));
					ymdList.add(sdate);
					today=DateUtil.getCalendarByAdd(today, Calendar.DAY_OF_MONTH, 1);
				}
				modelMap.put("dataList0", dataList0);
				modelMap.put("dataList1", dataList1);
				modelMap.put("dataList2", dataList2);
				modelMap.put("dataList3", dataList3);
				modelMap.put("dataList4", dataList4);
				modelMap.put("dataList5", dataList5);
				modelMap.put("dataList6", dataList6);
				modelMap.put("ymdList", ymdList);
				session.setAttribute("dataList0", dataList0);
				session.setAttribute("dataList1", dataList1);
				session.setAttribute("dataList2", dataList2);
				session.setAttribute("dataList3", dataList3);
				session.setAttribute("dataList4", dataList4);
				session.setAttribute("dataList5", dataList5);
				session.setAttribute("dataList6", dataList6);
				session.setAttribute("ymdList", ymdList);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询两个时间段菜单点击数据
		 * @param modelMap
		 * @param dayNum
		 * @return
		 * @throws ParseException 
		 */
		@ResponseBody
		@RequestMapping("getMenuClickDataInfoByTowDay")
		public String getMenuClickDataInfoByTowDay(ModelMap modelMap,HttpSession session,String start,String end) throws ParseException{
			if (start!=null&&start.length()>0&&end!=null&&end.length()>0) {
				if(start!=null&&end!=null){
					Integer dayNum=DateUtil.getDateDayLenth(start, end);
					return getMenuClickDataInfoByDayNum(modelMap,session, dayNum, DateUtil.getCalendarByAdd(DateUtil.simpdfyMd.parse(end), Calendar.DAY_OF_MONTH, 1));
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);
			}
			return JsonUtil.toJson(modelMap);
		}
		
		
		/**
		 * 根据点击类型查询点击量
		 * @param sData
		 * @param eDate
		 * @param type
		 * @return
		 */
		public Object getRegClickCountByType(String sData,String eDate,Integer type){
			String sql="SELECT sum(num) FROM `menuclick`  where  gmtCreated>='"+sData+"' and gmtCreated<='"+eDate+"'  and type="+type;//注册数量
			List<Map<String, Object>> listmap = menuclickService.getBySQL(sql, null);
			if(listmap!=null){
				Map<String, Object> map=listmap.get(0);
				for (String key : map.keySet()) {
					if(map.get(key)!=null){
						return map.get(key);
					}
				}
			}
			return 0;
		}
		
		
		/**
		 * 导出excel
		 * @param modelMap
		 * @param session
		 * @param request
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getOutExcelClick")
		public String getOutExcelClick(ModelMap modelMap,HttpSession session,HttpServletRequest request){
			if (session.getAttribute("ymdList")!=null) {
				List<Object> dataList0=(List<Object>) session.getAttribute("dataList0");
				List<Object> dataList1=(List<Object>) session.getAttribute("dataList1");
				List<Object> dataList2=(List<Object>) session.getAttribute("dataList2");
				List<Object> dataList3=(List<Object>) session.getAttribute("dataList3");
				List<Object> dataList4=(List<Object>) session.getAttribute("dataList4");
				List<Object> dataList5=(List<Object>) session.getAttribute("dataList5");
				List<Object> dataList6=(List<Object>) session.getAttribute("dataList6");
				List<String> ymdList=(List<String>) session.getAttribute("ymdList");
				String url=tocreatedExcel(request, ymdList, dataList0, dataList1, dataList2, dataList3,dataList4,dataList5,dataList6);
				if (url!=null&&url.length()>0) {
					modelMap.put("url", url);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//1
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//0
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLSESSION);//-2
			}
			return JsonUtil.toJson(modelMap);
		}
		
		public String tocreatedExcel(HttpServletRequest request,List<String> ymdList,List<Object> dataList0,List<Object> dataList1,List<Object> dataList2,List<Object> dataList3,List<Object> dataList4,List<Object> dataList5,List<Object> dataList6){
			try {
				String fileName="menuclickinfo.xls";
				String fileUrl=FileUploadUtil.getUploadUrl(request, "filedownload"+File.separator+fileName);//生成路径
				WritableWorkbook workbook;
				workbook = Workbook.createWorkbook(new File(fileUrl));
				WritableSheet sheet1=workbook.createSheet("Sheet1", 0);
				WritableFont font=new WritableFont(WritableFont.TIMES);
				WritableCellFormat format=new WritableCellFormat();
				Label label=new Label(0, 0, "时间", format);
				sheet1.addCell(label);
				label=new Label(1, 0, "租房点击数", format);
				sheet1.addCell(label);
				label=new Label(2, 0, "求职点击数", format);
				sheet1.addCell(label);
				label=new Label(3, 0, "闲置点击数", format);
				sheet1.addCell(label);
				label=new Label(4, 0, "搜索点击数", format);
				sheet1.addCell(label);
				label=new Label(5, 0, "订阅分类点击数", format);
				sheet1.addCell(label);
				label=new Label(6, 0, "发布点击数", format);
				sheet1.addCell(label);
				label=new Label(7, 0, "我的菜单点击数", format);
				sheet1.addCell(label);
				for (int i = 0; i < ymdList.size(); i++) {
					String ymd=ymdList.get(i);
					label=new Label(0, i+1, ymd, format);
					sheet1.addCell(label);
					
					Object obj=dataList0.get(i);
					label=new Label(1, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=dataList1.get(i);
					label=new Label(2, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=dataList2.get(i);
					label=new Label(3, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=dataList3.get(i);
					label=new Label(4, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=dataList4.get(i);
					label=new Label(5, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=dataList5.get(i);
					label=new Label(6, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=dataList6.get(i);
					label=new Label(7, i+1, obj.toString(), format);
					sheet1.addCell(label);
				}
				workbook.write();
				workbook.close();
				String requestUrl=FileUploadUtil.getRequestUrl(request);
				fileUrl=requestUrl+"/filedownload/"+fileName;
				return fileUrl;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "";
		}
		
}
