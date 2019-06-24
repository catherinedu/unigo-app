package com.xjt.util.wechat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.xjt.model.WaChatOrderPayObject;


public class  WeChatXmlUtil {
	
	/**
	 * 解析xml获取 prepay_id
	 * @param xml
	 * @return
	 */
	public static String readXmlGetPrepayId(String xml) {
        Map<String, String> map = new HashMap<String, String>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            @SuppressWarnings("unchecked")
            List<Element> list = rootElt.elements();// 获取根节点下所有节点
            for (Element element : list) { // 遍历节点
                map.put(element.getName(), element.getText()); // 节点的name为map的key，text为map的value
                if(element.getName().equals("prepay_id")){
                	return element.getText();
                }
                
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/**
	 * 解析返回的订单支付数据
	 * @param xml
	 * @return
	 */
	public static WaChatOrderPayObject readXmlGetOderPayObject(String xml) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            WaChatOrderPayObject waChatOrderPayObject=new WaChatOrderPayObject();
            @SuppressWarnings("unchecked")
            List<Element> list = rootElt.elements();// 获取根节点下所有节点
            for (Element element : list) { // 遍历节点
                if(element.getName().equals("return_code")){
                	 waChatOrderPayObject.setReturn_code(element.getText());
                }else if(element.getName().equals("return_msg")){
                	waChatOrderPayObject.setReturn_msg(element.getText());
                }else if(element.getName().equals("appid")){
                	waChatOrderPayObject.setAppid(element.getText());
                }else if(element.getName().equals("mch_id")){
                	waChatOrderPayObject.setMch_id(element.getText());
                }else if(element.getName().equals("nonce_str")){
                	waChatOrderPayObject.setNonce_str(element.getText());
                }else if(element.getName().equals("sign")){
                	waChatOrderPayObject.setSign(element.getText());
                }else if(element.getName().equals("result_code")){
                	waChatOrderPayObject.setResult_code(element.getText());
                }else if(element.getName().equals("err_code")){
                	waChatOrderPayObject.setErr_code(element.getText());
                }else if(element.getName().equals("err_code_des")){
                	waChatOrderPayObject.setErr_code_des(element.getText());
                }else if(element.getName().equals("openid")){
                	waChatOrderPayObject.setOpenid(element.getText());
                }else if(element.getName().equals("is_subscribe")){
                	waChatOrderPayObject.setIs_subscribe(element.getText());
                }else if(element.getName().equals("trade_type")){
                	waChatOrderPayObject.setTrade_type(element.getText());
                }else if(element.getName().equals("bank_type")){
                	waChatOrderPayObject.setBank_type(element.getText());
                }else if(element.getName().equals("total_fee")){
                	if (element.getText()!=null) {
                		waChatOrderPayObject.setTotal_fee(Integer.valueOf(element.getText()));
                	}
                }else if(element.getName().equals("fee_type")){
                	waChatOrderPayObject.setFee_type(element.getText());
                }else if(element.getName().equals("transaction_id")){
                	waChatOrderPayObject.setTransaction_id(element.getText());
                }else if(element.getName().equals("out_trade_no")){
                	waChatOrderPayObject.setOut_trade_no(element.getText());
                }else if(element.getName().equals("attach")){
                	waChatOrderPayObject.setAttach(element.getText());
                }else if(element.getName().equals("time_end")){
                	waChatOrderPayObject.setTime_end(element.getText());
                }else if(element.getName().equals("trade_state")){
                	waChatOrderPayObject.setTrade_state(element.getText());
                }else if(element.getName().equals("trade_state_desc")){
                	waChatOrderPayObject.setTrade_state_desc(element.getText());
                }else if(element.getName().equals("cash_fee")){
                	if (element.getText()!=null) {
                		waChatOrderPayObject.setCash_fee(Integer.valueOf(element.getText()));
					}
                }else if(element.getName().equals("prepay_id")){
                		waChatOrderPayObject.setPrepay_id(element.getText());
                }else if(element.getName().equals("code_url")){
            		waChatOrderPayObject.setCode_url(element.getText());
            }
            }
            return waChatOrderPayObject;
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
