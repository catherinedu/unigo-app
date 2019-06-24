package com.xjt.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 json封装类
*/
public class JsonUtil {
	public static Gson GSON = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static <T>  T fromJson(String src , java.lang.reflect.Type type) {
        return GSON.fromJson(src, type);
    }
    
    public static String toJson(Object o) {
        return GSON.toJson(o);
    }
    
    public static void toJsonp(HttpServletResponse response,String callback,Object o){  //返回jsonp格式数据  
		try {
			PrintWriter out = response.getWriter();
			out.println(callback+"("+GSON.toJson(o)+")");
		    out.flush();  
		    out.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static List removeRepetition(List lists){	//去重复
		HashSet h = new HashSet(lists);
		lists.clear();
		lists.addAll(h);
		return lists;
	}
    
    
}
