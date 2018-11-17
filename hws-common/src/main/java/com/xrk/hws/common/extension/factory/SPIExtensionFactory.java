package com.xrk.hws.common.extension.factory;

import com.xrk.hws.common.extension.ExtensionFactory;
import com.xrk.hws.common.extension.ExtensionLoader;
import com.xrk.hws.common.extension.SPI;

/**
 * 类: SPI扩展点实现.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月3日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class SPIExtensionFactory implements ExtensionFactory
{

	@Override
    public <T> T getExtension(Class<T> type, String name)
    {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) 
        {
            ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(type);
            if (loader.getSupportedExtensions().size() > 0) 
            {
                return loader.getAdaptiveExtension();
            }
        }
        return null;
    }

}
