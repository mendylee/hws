package com.xrk.hws.server.classloading.hash;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xrk.hws.server.vfs.VirtualFile;
/**
 * 类：类状态hash创建器.
 *
 * <br>==========================
 * <br> 公司:广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月8日
 * <br>==========================
 */
public class ClassStateHashCreator 
{
	/**
	 * 类定义器模式匹配.
	 */
    private final Pattern classDefFinderPattern = Pattern.compile("\\s+class\\s([a-zA-Z0-9_]+)\\s+");

    /**
     * 类文件定义
     */
    private static class FileWithClassDefs 
    {
        private final File file;
        private final long size;
        private final long lastModified;
        private final String classDefs;

        private FileWithClassDefs(File file, String classDefs) 
        {
            this.file = file;
            this.classDefs = classDefs;
            size = file.length();
            lastModified = file.lastModified();
        }

        public boolean fileNotChanges( ) 
        {
            return size == file.length() && lastModified == file.lastModified();
        }

        public String getClassDefs() 
        {
            return classDefs;
        }
    }

    /**
     * 类文件定义缓存.
     */
    private final Map<File, FileWithClassDefs> classDefsInFileCache = new HashMap<File, FileWithClassDefs>();

    public synchronized int computePathHash(List<VirtualFile> paths) 
    {
        StringBuffer buf = new StringBuffer();
        for (VirtualFile virtualFile : paths) 
        {
            scan(buf, virtualFile);
        }
        return buf.toString().hashCode();
    }

    private void scan(StringBuffer buf, VirtualFile current) 
    {
        if (!current.isDirectory())
        {
            if (current.getName().endsWith(".java")) 
            {
                buf.append( getClassDefsForFile(current));
            }
        } 
        else if (!current.getName().startsWith(".")) 
        {
            for (VirtualFile virtualFile : current.list()) 
            {
                scan(buf, virtualFile);
            }
        }
    }

    private String getClassDefsForFile( VirtualFile current ) 
    {

        File realFile = current.getRealFile();
        // 第一次从缓存中查找
        FileWithClassDefs fileWithClassDefs = classDefsInFileCache.get( realFile );
        if( fileWithClassDefs != null && fileWithClassDefs.fileNotChanges() ) 
        {
            return fileWithClassDefs.getClassDefs();
        }
        StringBuilder buf = new StringBuilder();
        Matcher matcher = classDefFinderPattern.matcher(current.contentAsString());
        buf.append(current.getName());
        buf.append("(");
        while (matcher.find()) 
        {
            buf.append(matcher.group(1));
            buf.append(",");
        }
        buf.append(")");
        String classDefs = buf.toString();

        // 存储至缓存中
        classDefsInFileCache.put( realFile, new FileWithClassDefs(realFile, classDefs));
        return classDefs;
    }

}
