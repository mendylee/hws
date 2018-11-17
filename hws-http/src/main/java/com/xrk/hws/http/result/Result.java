package com.xrk.hws.http.result;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import com.xrk.hws.http.context.HttpContext;

/**
 * 抽象类: 响应结果.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public abstract class Result extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public Result()
	{
        super();
    }
	
	public abstract void apply(HttpContext ctx);
	
	protected void setContentType(FullHttpResponse response,String contentType)
	{
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
	}
	
	protected String getEncoding()
	{
		return CharsetUtil.UTF_8.name();
	}

    public Result( String desc)
    {
        super(desc);
    }

    public Result(String desc, Throwable cause)
    {
        super(desc, cause);
    }

    public Result(Throwable cause)
    {
        super(cause);
    }

    public Throwable fillInStackTrace() 
    {
        return null;
    }
}
