package com.xjt.web;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
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

import com.xjt.post.PostUser;
import com.xjt.post.PostThread;
import com.xjt.post.PostUtil;
import com.xjt.service.PostDataService;
import com.xjt.service.SysmsgService;
import com.xjt.service.UserService;
import com.xjt.chat.PushMsg;
import com.xjt.chat.PushUtil;
import com.xjt.entity.PostData;
import com.xjt.entity.Sysmsg;
import com.xjt.entity.User;
import com.xjt.util.CommonInfoUtil;
import com.xjt.util.DateUtil;
import com.xjt.util.FileUploadUtil;
import com.xjt.util.JsonUtil;
import com.xjt.util.LogUtil;
import com.xjt.util.MD5;
import com.xjt.util.ObjectUtil;
import com.xjt.util.Page;
import com.xjt.util.SmsUtil;

/**
* User控制层
* @author Administrator
*/
@Controller
public class UserController{

		@Autowired
		private UserService userService;
		@Autowired
		private PostDataService postDataService;
		@Autowired
		private SysmsgService sysmsgService;

		/**
		 *添加或编辑User
		 * @throws ParseException 
		*/
		@ResponseBody
		@RequestMapping("userGetJsonInfo/addOrEditUser")
		public String addOrEditUser(ModelMap modelMap,HttpServletRequest request,HttpSession session,Integer mark,Long id,String birthday,String email,String img,String bgImg,String name,String introduce,Integer sex,String wechat,String school,Integer schoolYear,String degree,String schoolEmail,String certificateImg,String idNum,String idImg,Integer phoneShow,Integer wechatShow) throws ParseException{
			User user=(User) session.getAttribute("user");
			if (user!=null) {
				if (email!=null&&email.length()>0){
					user.setEmail(email);
				}
				if (img!=null&&img.length()>0){
					img=FileUploadUtil.uploadBase64Img(request, img,mark);
					user.setImg(img);
				}
				if (bgImg!=null&&bgImg.length()>0){
					//bgImg=FileUploadUtil.uploadBase64Img(request, bgImg);
					user.setBgImg(bgImg);
				}
				if (name!=null&&name.length()>0){
					user.setName(name);
				}
				if (introduce!=null&&introduce.length()>0){
					user.setIntroduce(introduce);
				}
				if (sex!=null){
					user.setSex(sex);
				}
				if (wechat!=null&&wechat.length()>0){
					user.setWechat(wechat);
				}
				if (school!=null&&school.length()>0){
					user.setSchool(school);
				}
				if (schoolYear!=null){
					user.setSchoolYear(schoolYear);
				}
				if (degree!=null&&degree.length()>0){
					user.setDegree(degree);
				}
				if (schoolEmail!=null&&schoolEmail.length()>0){
					user.setSchoolEmail(schoolEmail);
				}
				if (certificateImg!=null&&certificateImg.length()>0){
					//certificateImg=FileUploadUtil.uploadBase64Img(request, certificateImg);
					user.setCertificateImg(certificateImg);
					user.setCertificateCheckState(0);
				}
				if (idNum!=null&&idNum.length()>0){
					user.setIdNum(idNum);
				}
				if (idImg!=null&&idImg.length()>0){
					//idImg=FileUploadUtil.uploadBase64Img(request, idImg);
					user.setIdImg(idImg);
					user.setIdCheckState(0);
				}
				if (phoneShow!=null){
					user.setPhoneShow(phoneShow);
				}
				if (wechatShow!=null){
					user.setWechatShow(wechatShow);
				}
				if (birthday!=null&&birthday.length()>0) {
					user.setBirthday(DateUtil.simpdfyMd.parse(birthday));
				}
				userService.saveOrUpdate(user);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
				/*PostData postData=PostUser.postEditUserInfo(user);*//**post 对接*//*
				savePostData(postData);*/
				PostThread postUserThread=new PostThread("postEditUserInfo", user, postDataService); //开启线程
				postUserThread.start();
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
		 *删除User
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/delUser")
		public String delUser(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				User user=userService.get(id);
				if (user!=null) {
					userService.delete(user);
					/*PostData postData=PostUser.postDelUserInfo(user);*//**post 对接*//*
					savePostData(postData);*/
					PostThread postUserThread=new PostThread("postDelUserInfo", user, postDataService); //开启线程
					postUserThread.start();
				}
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1	操作完成
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}

		/**
		 * 分页查询User
		*/
		@ResponseBody
		@RequestMapping("sysUserGetInfo/getUser")
		public String getUser(ModelMap modelMap,HttpSession session,Integer pageIndex,Integer isCheck,Integer pageNum,Integer pageCount,String des,String pms,String start,String end) throws ParseException {
			if (pageIndex==null) {
				pageIndex=1;
			}
			if (pageNum==null) {
				pageNum=10;
			}
			Map<String, Object> prams=null;
			if (isCheck!=null&&isCheck==0) {
				List<Long> ids=getCheckUserIds();
				if (ids!=null&&ids.size()>0) {
					prams=new HashMap<String, Object>();
					prams.put("id", ids);
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
					return JsonUtil.toJson(modelMap);
				}
			}
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "id");
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
				searchPram.put("phone", pms);
				searchPram.put("name", pms);
				searchPram.put("wechat", pms);
				searchPram.put("school", pms);
				searchPram.put("degree", pms);
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
			Page page=userService.getByPrams(prams, sortPram, searchPram, (pageIndex-1)*pageNum, pageNum, startTime, endTime);
			List<User> userList= (List<User>) page.getList();
			if (userList!=null&&userList.size()>0) {
				pageCount=userService.getCount(prams, searchPram, startTime, endTime);
				modelMap.put(CommonInfoUtil.PAGECOUNT, page.getCount(pageCount, pageNum));//pageCount  总页数
				modelMap.put(CommonInfoUtil.PAGEINDEX, pageIndex);			//pageIndex     查询页数
				for (User user : userList) {
					ObjectUtil.ToChangeNullToEmpty(user);
				}
				modelMap.put(CommonInfoUtil.jSONOBJECTLIST, userList);		//jsonObjectList json对象集合
				modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS);
				modelMap.put(CommonInfoUtil.ALLCOUINT,pageCount);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询未审核的用户
		 * @return
		 */
		private List<Long> getCheckUserIds(){
			List<Long> ids=null;
			String sql="SELECT id from `user` where certificateCheckState=0 or idCheckState=0";
			List<Map<String, Object>> listmaps=userService.getBySQL(sql, null);
			if(listmaps!=null&&listmaps.size()>0){
				ids=new ArrayList<Long>();
				for (Map<String, Object> map : listmaps) {
					BigInteger bigInteger=(BigInteger) map.get("id");
					ids.add(bigInteger.longValue());
				}
			}
			return ids;
		}

		/**
		 *根据id查询User
		*/
		@ResponseBody
		@RequestMapping("getUserById")
		public String getUserById(ModelMap modelMap,HttpSession session,Long id){
			if (id!=null) {
				User user=userService.get(id);
				if (user!=null) {
					modelMap.put(CommonInfoUtil.jSONOBJECT, user);//jsonObject	json对象
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
		 * 查询所有User
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("sysUserGetInfo/getAllUser")
		public String getAllUser(ModelMap modelMap,HttpSession session,String pms){
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
				searchPram.put("phone", pms);
				searchPram.put("email", pms);
				searchPram.put("name", pms);
				searchPram.put("wechat", pms);
				searchPram.put("school", pms);
				searchPram.put("degree", pms);
				searchPram.put("schoolEmail", pms);
				searchPram.put("idNum", pms);
			}
			List<User> userList= userService.getByPrams(null, null, searchPram);
			modelMap.put(CommonInfoUtil.jSONOBJECTLIST, userList);		//jsonObjectList json对象集合
			modelMap.put(CommonInfoUtil.JSONMSG,CommonInfoUtil.SUCCESS );	
			return JsonUtil.toJson(modelMap);
		}
		
		
		/**
		 * 注册发送验证码
		 * @param modelMap
		 * @param session
		 * @param phone
		 * @return
		 */
		@ResponseBody
		@RequestMapping("regSendCode")
		public String regSendCode(ModelMap modelMap,HttpSession session,String phone,String areaCode){
			if (phone!=null&&phone.length()>0&&areaCode!=null&&areaCode.length()>0) {
				if (!checkUserPhone(phone)) {//如果手机号码未被注册
					String code=SmsUtil.getRandom(4);
					String content="【由你】验证码是:"+code+",请在10分钟内输入验证码，完成注册哦!———爱你的UNIGO";
					if (SmsUtil.cloundSendInternationMessage(content, areaCode+phone)) {
						session.setAttribute("regPhone", phone);
						session.setAttribute("regCode", code);
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
					}else {
						modelMap.put(CommonInfoUtil.JSONMSG, -2);
					}
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.OBJECTEXIST);//msg=-4 对象已存在
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 忘记密码发送验证码
		 * @param modelMap
		 * @param session
		 * @param phone
		 * @return
		 */
		@ResponseBody
		@RequestMapping("editPwdSendCode")
		public String editPwdSendCode(ModelMap modelMap,HttpSession session,String phone,String msgPhone,String areaCode){
			if (phone!=null&&phone.length()>0) {
				if (checkUserPhone(phone)) {//如果手机号码存在
					String code=SmsUtil.getRandom(4);
					String content="【由你】验证码是:"+code+",请在10分钟内输入验证码，完成修改哦!———爱你的UNIGO";
					if (SmsUtil.cloundSendInternationMessage(content, areaCode+phone)) {
						session.setAttribute("edtiPhone", phone);
						session.setAttribute("edtiCode", code);
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
					}else {
						modelMap.put(CommonInfoUtil.JSONMSG, -2);
					}
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0  手机号不存在
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 用户注册
		 * @param modelMap
		 * @param session
		 * @param phone
		 * @param pwd
		 * @param email
		 * @param code
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userToRegister")
		public String userToRegister(ModelMap modelMap,HttpSession session,String phone,String pwd,String email,String code){
			if (phone!=null&&phone.length()>0&&email!=null&&email.length()>0&&pwd!=null&&pwd.length()>0&&code!=null&&code.length()>0) {
				if (!checkUserPhone(phone)) {
					if (session.getAttribute("regPhone")!=null&&session.getAttribute("regCode")!=null) {
						String sessionPhone=(String) session.getAttribute("regPhone");
						String sessionCode=(String) session.getAttribute("regCode");
						if (!phone.equals(sessionPhone)) {
							modelMap.put(CommonInfoUtil.JSONMSG, -6);//验证手机号码不一致
							return JsonUtil.toJson(modelMap);
						}
						if (!code.equals(sessionCode)) {
							modelMap.put(CommonInfoUtil.JSONMSG, -7);//验证码错误
							return JsonUtil.toJson(modelMap);
						}
						pwd=MD5.getPwdMd5(pwd);
						User user=new User(sessionPhone, pwd, email, new Date(), 0, 0, 1, 1,-1,-1);
						user.setImg("http://unigotech.com/UNIGO/bg/image/morentouxiang.png");
						userService.save(user);
						getUserToken(user);
						getUserUid(user);
						userService.saveOrUpdate(user);
						session.setAttribute("user", user);
						modelMap.put("token", user.getToken());
						modelMap.put("id", user.getId());
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1  成功
						
						/*PostData postData=PostUser.postAddUserInfo(user);*//** post对接*//*
						savePostData(postData);*/
						PostThread postUserThread=new PostThread("postAddUserInfo", user, postDataService); //开启线程
						postUserThread.start();
					}else {
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLOBJECT);//msg=-5 未进行短信验证
					}
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.OBJECTEXIST);//msg=-4 对象已存在
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		
		
		/**
		 * 用户修改验证码
		 * @param modelMap
		 * @param session
		 * @param phone
		 * @param pwd
		 * @param code
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userEditPwd")
		public String userEditPwd(ModelMap modelMap,HttpSession session,String phone,String pwd,String code){
			if (phone!=null&&phone.length()>0&&pwd!=null&&pwd.length()>0&&code!=null&&code.length()>0) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("phone", phone);
				User user=userService.getByParam(params);
				if (user!=null) {
					if (session.getAttribute("edtiPhone")!=null&&session.getAttribute("edtiCode")!=null) {
						String sessionPhone=(String) session.getAttribute("edtiPhone");
						String sessionCode=(String) session.getAttribute("edtiCode");
						if (!phone.equals(sessionPhone)) {
							modelMap.put(CommonInfoUtil.JSONMSG, -6);//验证手机号码不一致
							return JsonUtil.toJson(modelMap);
						}
						if (!code.equals(sessionCode)) {
							modelMap.put(CommonInfoUtil.JSONMSG, -7);//验证码错误
							return JsonUtil.toJson(modelMap);
						}
						pwd=MD5.getPwdMd5(pwd);
						user.setPwd(pwd);
						getUserToken(user);
						userService.saveOrUpdate(user);
						session.setAttribute("user", user);
						modelMap.put("token", user.getToken());
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1  成功
					}else {
						modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLOBJECT);//msg=-5 未进行短信验证
					}
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//手机号码未注册
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		
		/**
		 * 更新用户token
		 * @param user
		 * @return
		 */
		public void getUserToken(User user){
			String token=user.getId()+new Date().getTime()+"";
			token=MD5.getMD5(token.getBytes());
			token=MD5.getMD5(token.getBytes());
			user.setToken(token);
			
		}
		
		/**
		 * 计算uid
		 * @param user
		 * @return
		 */
		public  void getUserUid(User user){
			String uid=user.getId().toString();
			if (uid.length()<6) {
				for (int i = uid.length(); i < 6; i++) {
					uid+="0";
				}
			}
			try {
				user.setUid(Integer.valueOf(uid));
				user.setName(uid.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		/**
		 * 根据手机号码查询用户是否存在
		 * @param phone
		 * @return
		 */
		public boolean checkUserPhone(String phone){
			if (phone!=null&&phone.length()>0) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("phone", phone);
				if (userService.getCount(params)>0) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * 用户登录
		 * @param modelMap
		 * @param session
		 * @param phone
		 * @param pwd
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userLogin")
		public  String userLogin(ModelMap modelMap,HttpSession session,String phone,String pwd,String sessionId){
			if (phone!=null&&phone.length()>0&&pwd!=null&&pwd.length()>0) {
				Map<String, Object> params=new HashMap<String, Object>();
				params.put("phone", phone);
				params.put("pwd", MD5.getPwdMd5(pwd));
				User user=userService.getByParam(params);
				if (user!=null) {
					//登录成功 顶下线通知
					sendSubMsg(user.getId());
					
					getUserToken(user);//更新token
					session.setAttribute("user", user);
					modelMap.put("token", user.getToken());
					modelMap.put("userId", user.getId().toString());
					modelMap.put("img", user.getImg());
					userService.saveOrUpdate(user);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1  成功
					if (sessionId!=null&&sessionId.length()>0) {
						PushUtil.userIdConnectSid(sessionId, user.getId());//链接sessionId
					}
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0  用户不存在
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 登录推送
		 * @param userId
		 */
		public void sendSubMsg(Long userId){
			PushMsg pushMsg=new PushMsg(6, null, null, null, null, "您的帐号已在其他设备登录,如非本人操作,请及时修改密码!", 0);
			PushUtil.sendMsgToUser(userId,pushMsg);
			PushUtil.userIdUnConnectSid(userId);
		}
		
		/**
		 * 退出登录
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userLoginOut")
		public  String userLoginOut(ModelMap modelMap,HttpSession session){
			session.removeAttribute("user");//清楚浏览器缓存
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1  成功
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 退出登录取消关联会话
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userLoginOutUnConnectSid")
		public  String userUnConnectSid(ModelMap modelMap,HttpSession session){
			User user=(User) session.getAttribute("user");
			PushUtil.userIdUnConnectSid(user.getId());
			session.removeAttribute("user");//清楚浏览器缓存
			modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1  成功
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 用户链接聊天sessionId
		 * @param modelMap
		 * @param session
		 * @param sessionId
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/userConnectChatSession")
		public  String userConnectChatSession(ModelMap modelMap,HttpSession session,String sessionId){
			if (sessionId!=null&&sessionId.length()>0) {
				User user=(User) session.getAttribute("user");
				if (PushUtil.userIdConnectSid(sessionId, user.getId())) {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1  成功
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0  用户不存在
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 检查用户链接状态
		 * @param modelMap
		 * @param session
		 * @return
		 */
		@ResponseBody
		@RequestMapping("userGetJsonInfo/checkUserConnectState")
		public  String checkUserConnectState(ModelMap modelMap,HttpSession session){
			User user=(User) session.getAttribute("user");
			String sessionId=PushUtil.checkUserConnectState(user.getId());
			if (sessionId!=null) {
				modelMap.put("sessionId", sessionId);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1  成功
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0  用户不存在
			}
			return JsonUtil.toJson(modelMap);
		}
		
		
		
		/**
		 * token获取用户数据
		 * @param modelMap
		 * @param session
		 * @param token
		 * @return
		 */
		@ResponseBody
		@RequestMapping("tokenGetUserInfo")
		public  String tokenGetUserInfo(ModelMap modelMap,HttpSession session,String token){
			if (token!=null&&token.length()>0) {
				Map<String, Object> params= new HashMap<String, Object>();
				params.put("token", token);
				User user=userService.getByParam(params);
				if (user!=null) {
					session.setAttribute("user", user);
					modelMap.put(CommonInfoUtil.jSONOBJECT, user);
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);//msg=1  成功
				}else {
					modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0 空
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);//msg=-1 参数错误
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 验证用户身份
		 * @param modelMap
		 * @param session
		 * @param id
		 * @param checkState
		 * @return
		 */
		@ResponseBody
		@RequestMapping("sysUserGetInfo/checkUserState")
		public String checkUserState(ModelMap modelMap,HttpSession session,Long id,Integer checkState,Integer pms){
			if (id!=null&&checkState!=null&&pms!=null) {
				User user=userService.get(id);
				if (user!=null) {
					if (pms==0) {//中级
						if (checkState==1) { //通过
							user.setCertificateCheckState(1);
							user.setCheckState(1);
							user.setCheckTime(new Date());
							//发送系统消息
							sendSysMsg(user.getId(), "您提交的中级认证已审核通过。");
						}else if (checkState==2) { //不通过
							user.setCertificateCheckState(2);
							//发送系统消息
							user.setCertificateImg(null);
							sendSysMsg(user.getId(), "您提交的高级认证审核不通过,请重新提交认证信息。");
						}
					}else if (pms==1){ //高级
						if (checkState==1) { //通过
							user.setIdCheckState(1);
							user.setCheckState(2);
							user.setCheckTime(new Date());
							//发送系统消息
							sendSysMsg(user.getId(), "您提交的高级认证已审核通过。");
						}else if (checkState==2) { //不通过
							user.setIdCheckState(2);
							user.setIdImg(null);
							//发送系统消息
							sendSysMsg(user.getId(), "您提交的高级认证审核不通过,请重新提交认证信息。");
						}
					}
					userService.saveOrUpdate(user);
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
		 * 发送系统消息
		 * @param userId
		 * @param msg
		 */
		private void sendSysMsg(Long userId,String msg){
			Sysmsg sysmsg=new Sysmsg(userId, "认证审核结果", msg, 0, new Date());
			sysmsgService.save(sysmsg);
			PushMsg pushMsg=new PushMsg(2, null,null, null, null, msg,0);
			PushUtil.sendMsgToUser(userId, pushMsg);
		}
		
		
		/**
		 * 查询昨天用户统计信息数据
		 * @param modelMap
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getYesterdayUserDataInfo")
		public String getYesterdayUserDataInfo(ModelMap modelMap){
			Date today=new Date();//今天
			Date yestoday=DateUtil.getCalendarByAdd(today, Calendar.DAY_OF_MONTH, -1);//昨天
			String edate=DateUtil.simpdfyMd.format(yestoday)+" 23:59:59";
			String sdate=DateUtil.simpdfyMd.format(yestoday)+" 00:00:00";
			List<Object> dataList=new ArrayList<Object>();
			dataList.add(getRegUserCount(sdate, edate));	//新注册数量
			dataList.add(getRegUserAllCount(edate));		//总注册数量
			dataList.add(getUserAllCountByCheckState(edate,1));//中级认证数量
			dataList.add(getUserAllCountByCheckState(edate,2));//高级认证数量
			dataList.add(getRegUserAllOnLineTime(edate));
			modelMap.put("dataList", dataList);
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询某天数内的数据量
		 * @param modelMap
		 * @return
		 */
		@ResponseBody
		@RequestMapping("getUserDataInfoByDayNum")
		public String getUserDataInfoByDayNum(ModelMap modelMap,HttpSession session,Integer dayNum,Date checkDate){
			if (dayNum!=null&&dayNum>0) {
				List<Object> newRegDataList=new ArrayList<Object>();//新注册数量
				List<Object> allRegDataList=new ArrayList<Object>();//总注册数量
				List<Object> bomUserDataList=new ArrayList<Object>();//中级认证数量
				List<Object> higUserDataList=new ArrayList<Object>();//高级认证数量
				List<Object> onlineDataList=new ArrayList<Object>();//高级认证数量
				List<String> ymdList=new ArrayList<String>();//日期
				if (checkDate==null) {
					checkDate=DateUtil.getCalendarByAdd(new Date(), Calendar.DAY_OF_MONTH, 1);
				}
				Date today=DateUtil.getCalendarByAdd(checkDate, Calendar.DAY_OF_MONTH, dayNum*-1);
				for (int i = 0; i <dayNum; i++) {
					String sdate=DateUtil.simpdfyMd.format(today)+" 00:00:00";
					String edate=DateUtil.simpdfyMd.format(today)+" 23:59:59";
					newRegDataList.add(getRegUserCount(sdate, edate));	//新注册数量
					allRegDataList.add(getRegUserAllCount(edate));		//总注册数量
					bomUserDataList.add(getUserAllCountByCheckState(edate,1));//中级认证数量
					higUserDataList.add(getUserAllCountByCheckState(edate,2));//高级认证数量
					onlineDataList.add(getRegUserAllOnLineTime(edate));
					ymdList.add(DateUtil.simpdfyMd.format(today));
					today=DateUtil.getCalendarByAdd(today, Calendar.DAY_OF_MONTH, 1);
				}
				modelMap.put("newRegDataList", newRegDataList);
				modelMap.put("allRegDataList", allRegDataList);
				modelMap.put("bomUserDataList", bomUserDataList);
				modelMap.put("higUserDataList", higUserDataList);
				modelMap.put("onlineDataList", onlineDataList);
				modelMap.put("ymdList", ymdList);
				session.setAttribute("newRegDataList", newRegDataList);
				session.setAttribute("allRegDataList", allRegDataList);
				session.setAttribute("bomUserDataList", bomUserDataList);
				session.setAttribute("higUserDataList", higUserDataList);
				session.setAttribute("onlineDataList", onlineDataList);
				session.setAttribute("ymdList", ymdList);
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询两个时间段数据
		 * @param modelMap
		 * @param dayNum
		 * @return
		 * @throws ParseException 
		 */
		@ResponseBody
		@RequestMapping("getUserDataInfoByTowDay")
		public String getUserDataInfoByTowDay(ModelMap modelMap,HttpSession session,String start,String end) throws ParseException{
			if (start!=null&&start.length()>0&&end!=null&&end.length()>0) {
				if(start!=null&&end!=null){
					Integer dayNum=DateUtil.getDateDayLenth(start, end);
					return getUserDataInfoByDayNum(modelMap,session, dayNum, DateUtil.getCalendarByAdd(DateUtil.simpdfyMd.parse(end), Calendar.DAY_OF_MONTH, 1));
				}
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.PARAMERROR);
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 查询某个时间段的注册数量
		 * @param sData
		 * @param eDate
		 * @return
		 */
		public Object getRegUserCount(String sData,String eDate){
			String sql="SELECT count(id) FROM `user`  where gmtCreated>='"+sData+"' and gmtCreated<'"+eDate+"'  ";//注册数量
			List<Map<String, Object>> listmap = userService.getBySQL(sql, null);
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
		 * 查询某个时间段的总注册数量
		 * @param sData
		 * @param eDate
		 * @return
		 */
		public Object getRegUserAllCount(String sData){
			String sql="SELECT count(id) FROM `user`  where   gmtCreated<'"+sData+"'  ";
			List<Map<String, Object>> listmap = userService.getBySQL(sql, null);
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
		 * 查询某个时间的累计用户在线数量
		 * @param sData
		 * @return
		 */
		public Object getRegUserAllOnLineTime(String sData){
			String sql="SELECT sum(onLineTime)/60 FROM `useronline`  where   gmtCreated<'"+sData+"'  ";
			List<Map<String, Object>> listmap = userService.getBySQL(sql, null);
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
		 * 查询某个时间段的审核通过数量
		 * @param sData
		 * @param eDate
		 * @return
		 */
		public Object getUserAllCountByCheckState(String sData,Integer checkState){
			String sql="SELECT count(id) FROM `user`  where   checkTime<'"+sData+"'  and checkState="+checkState;
			List<Map<String, Object>> listmap = userService.getBySQL(sql, null);
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
		@RequestMapping("getOutExcelUser")
		public String getOutExcelUser(ModelMap modelMap,HttpSession session,HttpServletRequest request){
			if (session.getAttribute("ymdList")!=null) {
				List<Object> newRegDataList=(List<Object>) session.getAttribute("newRegDataList");
				List<Object> allRegDataList=(List<Object>) session.getAttribute("allRegDataList");
				List<Object> bomUserDataList=(List<Object>) session.getAttribute("bomUserDataList");
				List<Object> higUserDataList=(List<Object>) session.getAttribute("higUserDataList");
				List<Object> onlineDataList=(List<Object>) session.getAttribute("onlineDataList");
				List<String> ymdList=(List<String>) session.getAttribute("ymdList");
				String url=tocreatedExcel(request, ymdList, newRegDataList, allRegDataList, bomUserDataList, higUserDataList,onlineDataList);
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
		
		public String tocreatedExcel(HttpServletRequest request,List<String> ymdList,List<Object> newRegDataList,List<Object> allRegDataList,List<Object> bomUserDataList,List<Object> higUserDataList,List<Object> onlineDataList){
			try {
				String fileName="userinfo.xls";
				String fileUrl=FileUploadUtil.getUploadUrl(request, "filedownload"+File.separator+fileName);//生成路径
				WritableWorkbook workbook;
				workbook = Workbook.createWorkbook(new File(fileUrl));
				WritableSheet sheet1=workbook.createSheet("Sheet1", 0);
				WritableFont font=new WritableFont(WritableFont.TIMES);
				WritableCellFormat format=new WritableCellFormat();
				Label label=new Label(0, 0, "时间", format);
				sheet1.addCell(label);
				label=new Label(1, 0, "新增用户数", format);
				sheet1.addCell(label);
				label=new Label(2, 0, "累计注册用户数", format);
				sheet1.addCell(label);
				label=new Label(3, 0, "累计通过中级认证用户数", format);
				sheet1.addCell(label);
				label=new Label(4, 0, "累计通过高级认证用户数", format);
				sheet1.addCell(label);
				label=new Label(5, 0, "累计用户在线时间(小时)", format);
				sheet1.addCell(label);
				for (int i = 0; i < ymdList.size(); i++) {
					String ymd=ymdList.get(i);
					label=new Label(0, i+1, ymd, format);
					sheet1.addCell(label);
					
					Object obj=newRegDataList.get(i);
					label=new Label(1, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=allRegDataList.get(i);
					label=new Label(2, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=bomUserDataList.get(i);
					label=new Label(3, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=higUserDataList.get(i);
					label=new Label(4, i+1, obj.toString(), format);
					sheet1.addCell(label);
					
					obj=onlineDataList.get(i);
					label=new Label(5, i+1, obj.toString(), format);
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
		
		/**
		 * 分页查询User
		*/
		@ResponseBody
		@RequestMapping("getOutExcelUserInfo")
		public String getOutExcelUserInfo(HttpServletRequest request,ModelMap modelMap,HttpSession session,String des,String pms,String start,String end) throws ParseException {
			Map<String, Object> prams=null;
			Map<String, Object> sortPram=null;
			if (des!=null&&des.length()>0) {
				sortPram=new HashMap<String, Object>();
				sortPram.put(des, "id");
			}
			Map<String, Object> searchPram=null;
			if (pms!=null&&pms.length()>0) {
				searchPram=new HashMap<String, Object>();
				searchPram.put("phone", pms);
				searchPram.put("name", pms);
				searchPram.put("wechat", pms);
				searchPram.put("school", pms);
				searchPram.put("degree", pms);
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
			Page page=userService.getByPrams(prams, sortPram, searchPram, null, null, startTime, endTime);
			List<User> userList= (List<User>) page.getList();
			if (userList!=null&&userList.size()>0) {
				//导出
				modelMap.put("url",tocreatedUserInfoExcel(request, userList));
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.SUCCESS);
			}else {
				modelMap.put(CommonInfoUtil.JSONMSG, CommonInfoUtil.NULLDATA);//msg=0	空数据
			}
			return JsonUtil.toJson(modelMap);
		}
		
		/**
		 * 导出用户列表信息
		 * @param request
		 * @param users
		 * @return
		 */
		public String tocreatedUserInfoExcel(HttpServletRequest request,List<User> users){
			try {
				String fileName="userinfo.xls";
				String fileUrl=FileUploadUtil.getUploadUrl(request, "filedownload"+File.separator+fileName);//生成路径
				WritableWorkbook workbook;
				workbook = Workbook.createWorkbook(new File(fileUrl));
				WritableSheet sheet1=workbook.createSheet("Sheet1", 0);
				WritableFont font=new WritableFont(WritableFont.TIMES);
				WritableCellFormat format=new WritableCellFormat();
				Label label=new Label(0, 0, "手机号码", format);
				sheet1.addCell(label);
				label=new Label(1, 0, "昵称", format);
				sheet1.addCell(label);
				label=new Label(2, 0, "积分", format);
				sheet1.addCell(label);
				label=new Label(3, 0, "个人简介", format);
				sheet1.addCell(label);
				label=new Label(4, 0, "性别", format);
				sheet1.addCell(label);
				label=new Label(5, 0, "生日", format);
				sheet1.addCell(label);
				label=new Label(6, 0, "微信", format);
				sheet1.addCell(label);
				label=new Label(7, 0, "验证状态", format);
				sheet1.addCell(label);
				label=new Label(8, 0, "学校", format);
				sheet1.addCell(label);
				label=new Label(9, 0, "入学年份", format);
				sheet1.addCell(label);
				label=new Label(10, 0, "学历", format);
				sheet1.addCell(label);
				label=new Label(11, 0, "学校邮箱", format);
				sheet1.addCell(label);
				for (int i = 0; i < users.size(); i++) {
					User user=users.get(i);
					ObjectUtil.ToChangeNullToEmpty(user);
					label=new Label(0, i+1, user.getPhone(), format);
					sheet1.addCell(label);
					label=new Label(1, i+1, user.getName(), format);
					sheet1.addCell(label);
					label=new Label(2, i+1, user.getScore().toString(), format);
					sheet1.addCell(label);
					label=new Label(3, i+1, user.getScore().toString(), format);
					sheet1.addCell(label);
					String sex="";
					if (user.getSex()!=null) {
						if (user.getSex()==0) {
							sex="男";
						}else if (user.getSex()==1) {
							sex="女";
						}
					}
					label=new Label(4, i+1, sex, format);
					sheet1.addCell(label);
					String birthday="";
					if (user.getBirthday()!=null) {
						birthday=DateUtil.simpdfyMd.format(user.getBirthday());
					}
					label=new Label(5, i+1, birthday, format);
					sheet1.addCell(label);
					label=new Label(6, i+1, user.getWechat(), format);
					sheet1.addCell(label);
					String checkState="";
					if (user.getCheckState()!=null) {
						if (user.getCheckState()==0) {
							checkState="未认证";
						}else if (user.getCheckState()==1) {
							checkState="中级";
						}else if (user.getCheckState()==2) {
							checkState="高级 ";
						}
					}
					label=new Label(7, i+1, checkState, format);
					sheet1.addCell(label);
					label=new Label(8, i+1, user.getSchool(), format);
					sheet1.addCell(label);
					String schoolYear="";
					if (user.getSchoolYear()!=null) {
						schoolYear=user.getSchoolYear().toString();
					}
					label=new Label(9, i+1, schoolYear, format);
					sheet1.addCell(label);
					label=new Label(10, i+1, user.getDegree(), format);
					sheet1.addCell(label);
					label=new Label(11, i+1,user.getSchoolEmail(), format);
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
