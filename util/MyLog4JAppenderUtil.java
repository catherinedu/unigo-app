package com.xjt.util;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

/**
 * 重写log4J的输出等级 使info中不输出error
 * @author Administrator
 *
 */
public class MyLog4JAppenderUtil extends DailyRollingFileAppender {  
    
  @Override  
  public boolean isAsSevereAsThreshold(Priority priority) {    
        //只判断是否相等，而不判断优先级     
      return this.getThreshold().equals(priority);    
  }    
}  
