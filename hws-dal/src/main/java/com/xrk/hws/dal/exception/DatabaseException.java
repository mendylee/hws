package com.xrk.hws.dal.exception;

import java.util.List;

import com.xrk.hws.common.exceptions.HwsException;
import com.xrk.hws.common.exceptions.SourceAttachment;

/**
 * 类: 数据库异常.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月26日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class DatabaseException extends HwsException implements SourceAttachment
{
    private static final long serialVersionUID = 1L;
    
    String sourceFile;
    
    List<String> source;
    
    Integer line;
    
    public DatabaseException(String message) 
    {
        super(message, null);
    }

    public DatabaseException(String message, Throwable cause) 
    {
//        super(message, cause);
//        StackTraceElement element = getInterestingStrackTraceElement(cause);
//        if(element != null) 
//        {
//            ApplicationClass applicationClass = Hws.classes.getApplicationClass(element.getClassName());
//            if (applicationClass.javaFile != null) 
//            {
//                sourceFile = applicationClass.javaFile.relativePath();
//            }
//            if (applicationClass.javaSource != null) 
//            {
//                source = Arrays.asList(applicationClass.javaSource.split("\n"));
//            }
//            line = element.getLineNumber();
//        }
    }

    @Override
    public String getErrorDescription() 
    {
        return String.format("A database error occured : <strong>%s</strong>", getMessage());
    }
    
    @Override
    public boolean isSourceAvailable() 
    {
        return sourceFile != null;
    }
    
    public String getSourceFile() 
    {
        return sourceFile;
    }

    public List<String> getSource() 
    {
        return source;
    }

    public Integer getLineNumber() 
    {
        return line;
    }

	@Override
    public String getErrorTitle()
    {
		return "Database error";
    }

}
