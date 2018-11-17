package com.xrk.hws.common.extension;

/**
 * ExtensionFactory: ExtensionFactory.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月3日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public interface ExtensionFactory
{
    /**
     * 获取扩展点.
     * 
     * @param type 	对象类型.
     * @param name 	对象名称.
     * @return 		对象实例.
     */
    <T> T getExtension(Class<T> type, String name);
}
