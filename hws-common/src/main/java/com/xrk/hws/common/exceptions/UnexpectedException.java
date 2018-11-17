package com.xrk.hws.common.exceptions;

/**
 * 类: 未知异常.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class UnexpectedException extends HwsException
{
    private static final long serialVersionUID = 1L;

	public UnexpectedException(String message) 
	{
        super(message);
    }

    public UnexpectedException(Throwable exception) 
    {
        super("Unexpected Error", exception);
    }

    public UnexpectedException(String message, Throwable cause) 
    {
        super(message, cause);
    }

    @Override
    public String getErrorTitle() 
    {
        if(getCause() == null) 
        {
            return "Unexpected error";
        }
        return String.format("Oops: %s", getCause().getClass().getSimpleName());
    }

    @Override
    public String getErrorDescription() 
    {
        if (getCause() != null && getCause().getClass() != null) 
        {
            return String.format("An unexpected error occured caused by exception <strong>%s</strong>:<br/> <strong>%s</strong>", 
            					 getCause().getClass().getSimpleName(), getCause().getMessage());
        } 
        else 
        {
            return String.format("Unexpected error : %s", getMessage());
        }
    }
}
