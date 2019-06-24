package com.xjt.util.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xjt.model.AliPayQueryResponse;
import com.xjt.util.JsonUtil;
import com.xjt.util.LogUtil;

/**
 * 支付宝支付工具类
 * @author Administrator
 *
 */
public class AlipayUtil {
	
	public static String URL=null;	//支付宝网关（固定）
	public static String APP_ID=null;	//APPID即创建应用后生成
	public static String APP_PRIVATE_KEY=null;//开发者应用私钥，由开发者自己生成
	public static String FORMAT=null;	//参数返回格式，只支持json
	public static String CHARSET=null;	//请求和签名使用的字符编码格式，支持GBK和UTF-8
	public static String ALIPAY_PUBLIC_KEY=null;	//支付宝公钥，由支付宝生成
	public static String SIGN_TYPE=null;		//商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
	public static String ReturnUrl=null;//回跳地址
	public static String NotifyUrl=null;//支付成功通知地址
	public static String PID=null;//实际收款账号，一般填写商户PID即可
	
	
	public static AlipayClient alipayClient=null;

	public AlipayUtil(String URL,String APP_ID,String APP_PRIVATE_KEY,String FORMAT,String CHARSET,String ALIPAY_PUBLIC_KEY,String SIGN_TYPE,
			String ReturnUrl,String NotifyUrl,String PID) {
		this.URL=URL;
		this.APP_ID=APP_ID;
		this.APP_PRIVATE_KEY=APP_PRIVATE_KEY;
		this.FORMAT=FORMAT;
		this.CHARSET=CHARSET;
		this.ALIPAY_PUBLIC_KEY=ALIPAY_PUBLIC_KEY;
		this.SIGN_TYPE=SIGN_TYPE;
		this.ReturnUrl=ReturnUrl;
		this.NotifyUrl=NotifyUrl;
		this.PID=PID;
		alipayClient=new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
	}
	
	/**
	 * H5发起支付获取form支付页面
	 * @param out_trade_no	订单号
	 * @param amount		金额
	 * @param subject		支付提示信息
	 * @return
	 * @throws AlipayApiException
	 */
	public static String  h5PayGetFormHtml(String out_trade_no,Float amount,String subject){
	    try {
	    	LogUtil.Info("开始H5发起支付获取form html");
			AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
		    alipayRequest.setReturnUrl(AlipayUtil.ReturnUrl);
		    alipayRequest.setNotifyUrl(AlipayUtil.NotifyUrl);//在公共参数中设置回跳和通知地址
		    alipayRequest.setBizContent("{" +
		        "    \"out_trade_no\":\""+out_trade_no+"\"," +  //商户订单号，需要保证不重复
		        "    \"total_amount\":\""+amount+"\"," +	//金额
		        "    \"subject\":\""+subject+"\"," +	//订单标题
		        "    \"seller_id\":\""+AlipayUtil.PID+"\"," +
		        "    \"product_code\":\"QUICK_WAP_PAY\"" +
		        "  }");//填充业务参数
			String form = AlipayUtil.alipayClient.pageExecute(alipayRequest).getBody();
			LogUtil.Info("H5发起支付获取form html成功");
			return form;
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 主动查询订单状态
	 * @param out_trade_no
	 * @return
	 */
	public static AliPayQueryResponse queryTrade(String out_trade_no){
		try {
			LogUtil.Info("开始发起订单查询[订单号="+out_trade_no+"]");
			AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
			request.setBizContent("{" +
			"    \"out_trade_no\":\""+out_trade_no+"\""+
			"  }");//设置业务参数
			 AlipayTradeQueryResponse response = alipayClient.execute(request);
			 String body=response.getBody();
			 LogUtil.Info("订单查询返回结果["+body+"]");
			 if (body!=null) {
				 LogUtil.Info("开始JSON对象数据解析");
					AliPayQueryResponse  aliPayQueryResponse=JsonUtil.fromJson(body, AliPayQueryResponse.class);
					if (aliPayQueryResponse!=null) {
						LogUtil.Info("JSON对象数据="+aliPayQueryResponse.toString());
					}else {
						LogUtil.Info("JSON对象数据=NULL");
					}
					 return aliPayQueryResponse;
			 }
			
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.Info("订单查询出现异常"+e);
		}	
		return null;
	}
	
	
	 
}
