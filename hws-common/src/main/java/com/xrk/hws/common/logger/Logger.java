package com.xrk.hws.common.logger;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.xrk.hws.common.exceptions.HwsException;


/**
 * 类：应用日志工具.
 *
 * <br>==========================
 * <br> 公司:广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com><lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月8日
 * <br>==========================
 */
public final class Logger 
{

    /**
     * 强制使用java.util.logging (默认第一次使用log4j).
     */
    public static boolean forceJuli = false;

    /**
     * log4j应用日志.
     */
    public static org.apache.log4j.Logger log4j;

    /**
     * 使用java.util.logging.
     */
    public static java.util.logging.Logger juli = java.util.logging.Logger.getLogger("hws");

    /**
     * 是否手动配置.
     */
    public static boolean configuredManually = false;

    /**
     * 日志初始化.
     */
    public static void init(String basePath) 
    {
        if (log4j != null) 
        {
            return;
        }

        // 应用程序的日志配置文件.log4j.properties
//        String log4jPath = Hws.configuration.getProperty("application.log.path");
        String log4jPath = null;        
        String log4jPathXml = String.format("%s/%s", basePath, "log4j.xml");
        File file = new File(log4jPathXml);
        if(file.exists())
        {
        	log4jPath = log4jPathXml;
        }
        else
        {
        	String log4jPathProp = String.format("%s/%s", basePath, "log4j.properties");
        	file = new File(log4jPathProp);
        	if(file.exists())
        	{
        		log4jPath = log4jPathProp;
        	}
        }
        
        if (log4jPath != null) 
        {
            configuredManually = true;
            boolean isXMLConfig = log4jPath.endsWith(".xml");
            if (isXMLConfig) 
            {
                DOMConfigurator.configureAndWatch(log4jPath, 60000);
            } 
            else 
            {
                PropertyConfigurator.configureAndWatch(log4jPath, 60000);
            }
            log4j = org.apache.log4j.Logger.getLogger(Logger.class);
        }

        // hws框架的日志文件配置log4j.properties
        URL log4jConf = null;
        if (log4jPath == null) 
        {
            log4jConf = Logger.class.getResource("/log4j.properties");
            if (log4jConf != null) 
            {
            	configuredManually = true;
            	PropertyConfigurator.configureAndWatch(log4jConf.getPath(), 60000);
            } 
            else 
            {
                Properties shutUp = new Properties();
                shutUp.setProperty("log4j.rootLogger", "OFF");
                PropertyConfigurator.configure(shutUp);
            }
        }
        
        if(log4jPath != null || log4jConf != null)
        {
        	log4j = LogManager.getLogger(Logger.class);
        }
    }
    
    public static org.apache.log4j.Logger getLogger(Class classtype)
    {
    	return LogManager.getLogger(classtype);
    }
    
    public static org.apache.log4j.Logger getLogger(String classtype)
    {
    	return LogManager.getLogger(classtype);
    }

    public static void setUp(String level) {
        if (forceJuli || log4j == null) {
            Logger.juli.setLevel(toJuliLevel(level));
        } else {
            Logger.log4j.setLevel(org.apache.log4j.Level.toLevel(level));
        }
    }

    static java.util.logging.Level toJuliLevel(String level) {
        java.util.logging.Level juliLevel = java.util.logging.Level.INFO;
        if (level.equals("ERROR") || level.equals("FATAL")) {
            juliLevel = java.util.logging.Level.SEVERE;
        }
        if (level.equals("WARN")) {
            juliLevel = java.util.logging.Level.WARNING;
        }
        if (level.equals("DEBUG")) {
            juliLevel = java.util.logging.Level.FINE;
        }
        if (level.equals("TRACE")) {
            juliLevel = java.util.logging.Level.FINEST;
        }
        if (level.equals("ALL")) {
            juliLevel = java.util.logging.Level.ALL;
        }
        if (level.equals("OFF")) {
            juliLevel = java.util.logging.Level.OFF;
        }
        return juliLevel;
    }

    public static boolean isDebugEnabled() 
    {
        if (forceJuli || log4j == null) 
        {
            return juli.isLoggable(java.util.logging.Level.FINE);
        } 
        else 
        {
            return log4j.isDebugEnabled();
        }
    }

    public static boolean isTraceEnabled() 
    {
        if (forceJuli || log4j == null) 
        {
            return juli.isLoggable(java.util.logging.Level.FINEST);
        } 
        else 
        {
            return log4j.isTraceEnabled();
        }
    }

    public static boolean isEnabledFor(String level) 
    {
        org.apache.log4j.Level log4jLevel = org.apache.log4j.Level.toLevel(level);

        if (forceJuli || log4j == null) 
        {
            java.util.logging.Level julLevel = toJuliLevel(log4jLevel.toString());
            return juli.isLoggable(julLevel);
        } 
        else 
        {
            return log4j.isEnabledFor(log4jLevel);
        }

    }

    public static void trace(String message, Object... args) 
    {
        if (forceJuli || log4j == null) 
        {
            try 
            {
                juli.finest(format(message, args));
            } 
            catch (Throwable ex) 
            {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } 
        else 
        {
            try 
            {
                log4j.trace(format(message, args));
            } 
            catch (Throwable ex) 
            {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void debug(String message, Object... args) {
        if (!isDebugEnabled()) {
            return;
        }

        if (forceJuli || log4j == null) {
            try {
                juli.fine(format(message, args));
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                log4j.debug(format(message, args));
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void debug(Throwable e, String message, Object... args) {
        if (!isDebugEnabled()) {
            return;
        }

        if (forceJuli || log4j == null) {
            try {
                if (!niceThrowable(org.apache.log4j.Level.DEBUG, e, message, args)) {
                    juli.log(Level.CONFIG, format(message, args), e);
                }
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                if (!niceThrowable(org.apache.log4j.Level.DEBUG, e, message, args)) {
                    log4j.debug(format(message, args), e);
                }
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void event(String logger, String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                java.util.logging.Logger.getLogger("hws." + logger).info(format(message, args));
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                org.apache.log4j.Logger.getLogger("hws." + logger).info(format(message, args));
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }
    
    /**
     * 判断event logger是否可用
     * @param logger
     * @return
     */
    public static boolean isEventEnabled(String logger) {
        if (forceJuli || log4j == null) {
            try {
                return java.util.logging.Logger.getLogger("hws." + logger).isLoggable(Level.INFO);
            } catch (Throwable ex) {
            	juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
                return false;
            }
        } else {
            try {
            	return (org.apache.log4j.LogManager.exists("hws." + logger) == null) ? false : true;
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
                return false;
            }
        }
    }

    public static void info(String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                juli.info(format(message, args));
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                log4j.info(format(message, args));
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void info(Throwable e, String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                if (!niceThrowable(org.apache.log4j.Level.INFO, e, message, args)) {
                    juli.log(Level.INFO, format(message, args), e);
                }
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                if (!niceThrowable(org.apache.log4j.Level.INFO, e, message, args)) {
                    log4j.info(format(message, args), e);
                }
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void warn(String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                juli.warning(format(message, args));
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                log4j.warn(format(message, args));
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void warn(Throwable e, String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                if (!niceThrowable(org.apache.log4j.Level.WARN, e, message, args)) {
                    juli.log(Level.WARNING, format(message, args), e);
                }
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                if (!niceThrowable(org.apache.log4j.Level.WARN, e, message, args)) {
                    log4j.warn(format(message, args), e);
                }
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void error(String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                juli.severe(format(message, args));
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                log4j.error(format(message, args));
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void error(Throwable e, String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                if (!niceThrowable(org.apache.log4j.Level.ERROR, e, message, args)) {
                    juli.log(Level.SEVERE, format(message, args), e);
                }
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                if (!niceThrowable(org.apache.log4j.Level.ERROR, e, message, args)) {
                    log4j.error(format(message, args), e);
                }
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void fatal(String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                juli.severe(format(message, args));
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                log4j.fatal(format(message, args));
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    public static void fatal(Throwable e, String message, Object... args) {
        if (forceJuli || log4j == null) {
            try {
                if (!niceThrowable(org.apache.log4j.Level.FATAL, e, message, args)) {
                    juli.log(Level.SEVERE, format(message, args), e);
                }
            } catch (Throwable ex) {
                juli.log(Level.SEVERE, "Oops. Error in Logger !", ex);
            }
        } else {
            try {
                if (!niceThrowable(org.apache.log4j.Level.FATAL, e, message, args)) {
                    log4j.fatal(format(message, args), e);
                }
            } catch (Throwable ex) {
                log4j.error("Oops. Error in Logger !", ex);
            }
        }
    }

    static boolean niceThrowable(org.apache.log4j.Level level, Throwable e, String message, Object... args) {
        if (e instanceof Exception) {

            Throwable toClean = e;
            for (int i = 0; i < 5; i++) {
                List<StackTraceElement> cleanTrace = new ArrayList<StackTraceElement>();
                for (StackTraceElement se : toClean.getStackTrace()) {
                    if (se.getClassName().startsWith("hws.server.HwsHandler$NettyInvocation")) {
                        cleanTrace.add(new StackTraceElement("Invocation", "HTTP Request", "HWS", -1));
                        break;
                    }
                    if (se.getClassName().startsWith("hws.server.HwsHandler$SslNettyInvocation")) {
                        cleanTrace.add(new StackTraceElement("Invocation", "HTTP Request", "HWS", -1));
                        break;
                    }
                    if (se.getClassName().startsWith("hws.server.HwsHandler") &&
                            se.getMethodName().equals("messageReceived")) {
                        cleanTrace.add(new StackTraceElement("Invocation", "Message Received", "HWS", -1));
                        break;
                    }
                    if (se.getClassName().startsWith("sun.reflect.")) {
                        continue; 
                    }
                    if (se.getClassName().startsWith("java.lang.reflect.")) {
                        continue; 
                    }
                    if (se.getClassName().startsWith("com.mchange.v2.c3p0.")) {
                        continue; 
                    }
                    if (se.getClassName().startsWith("scala.tools.")) {
                        continue; 
                    }
                    if (se.getClassName().startsWith("scala.collection.")) {
                        continue; 
                    }
                    cleanTrace.add(se);
                }
                toClean.setStackTrace(cleanTrace.toArray(new StackTraceElement[cleanTrace.size()]));
                toClean = toClean.getCause();
                if (toClean == null) {
                    break;
                }
            }

            StringWriter sw = new StringWriter();

            if (e instanceof HwsException) {
                HwsException hwsException = (HwsException) e;
                PrintWriter errorOut = new PrintWriter(sw);
                errorOut.println("");
                errorOut.println("");
                errorOut.println("@" + hwsException.getId());
                errorOut.println(format(message, args));
                errorOut.println("");
                if (hwsException.isSourceAvailable()) {
                    errorOut.println(hwsException.getErrorTitle() + " (In " + hwsException.getSourceFile()
                            + " around line " + hwsException.getLineNumber() + ")");
                } else {
                    errorOut.println(hwsException.getErrorTitle());
                }
                errorOut.println(hwsException.getErrorDescription().replaceAll("</?\\w+/?>", "").replace("\n", " "));
            } else {
                sw.append(format(message, args));
            }

            try {
                if (forceJuli || log4j == null) {
                    juli.log(toJuliLevel(level.toString()), sw.toString(), e);
                } else {
                    log4j.log(level, sw.toString(), e);
                }
            } catch (Exception e1) {
                log4j.error("Oops. Error in Logger !", e1);
            }
            return true;
        }
        return false;
    }

	static String format(String msg, Object... args)
	{
		try 
		{
			if (args != null && args.length > 0) 
			{
				return String.format(msg, args);
			}
			return msg;
		}
		catch (Exception e) 
		{
			return msg;
		}
	}

	static class CallInfo
	{
		public String className;
		public String methodName;

		public CallInfo() 
		{
			
		}

		public CallInfo(String className, String methodName) 
		{
			this.className = className;
			this.methodName = methodName;
		}
	}

	static String getCallerClassName()
	{
		final int level = 4;
		return getCallerClassName(level);
	}

	static String getCallerClassName(final int level)
	{
		CallInfo ci = getCallerInformations(level);
		return ci.className;
	}

	static CallInfo getCallerInformations(int level)
	{
		StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
		StackTraceElement caller = callStack[level];
		return new CallInfo(caller.getClassName(), caller.getMethodName());
	}

    public static class JuliToLog4jHandler extends Handler 
    {

		public void publish(LogRecord record)
		{
			org.apache.log4j.Logger log4j = getTargetLogger(record.getLoggerName());
			Priority priority = toLog4j(record.getLevel());
			log4j.log(priority, toLog4jMessage(record), record.getThrown());
		}

		static org.apache.log4j.Logger getTargetLogger(String loggerName)
		{
			return org.apache.log4j.Logger.getLogger(loggerName);
		}

		public static org.apache.log4j.Logger getTargetLogger(Class<?> clazz)
		{
			return getTargetLogger(clazz.getName());
		}

        private String toLog4jMessage(LogRecord record) 
        {
            String message = record.getMessage();
            try 
            {
                Object parameters[] = record.getParameters();
                if (parameters != null && parameters.length != 0) 
                {
                    if (message.indexOf("{0}") >= 0
                            || message.indexOf("{1}") >= 0
                            || message.indexOf("{2}") >= 0
                            || message.indexOf("{3}") >= 0) 
                    {
                        message = MessageFormat.format(message, parameters);
                    }
                }
            } 
            catch (Exception ex) 
            {
            }
            return message;
        }

		private org.apache.log4j.Level toLog4j(java.util.logging.Level level)
		{
			if (java.util.logging.Level.SEVERE == level) {
				return org.apache.log4j.Level.ERROR;
			}
			else if (java.util.logging.Level.WARNING == level) {
				return org.apache.log4j.Level.WARN;
			}
			else if (java.util.logging.Level.INFO == level) {
				return org.apache.log4j.Level.INFO;
			}
			else if (java.util.logging.Level.OFF == level) {
				return org.apache.log4j.Level.TRACE;
			}
			return org.apache.log4j.Level.TRACE;
		}

		@Override
		public void flush()
		{
		}

		@Override
		public void close()
		{
		}
    }

}
