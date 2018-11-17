package com.xrk.hws.common.exceptions;

import java.util.List;

/**
 * 接口: 异常资源附件.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月24日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface SourceAttachment
{
	/**
	 * 获取源文件.  
	 *    
	 * @return
	 */
	public String getSourceFile();

	/**
	 * 获取源码字符列表.
	 *    
	 * @return
	 */
	public List<String> getSource();

	/**
	 * 获取行号.
	 *    
	 * @return
	 */
	public Integer getLineNumber();
}
