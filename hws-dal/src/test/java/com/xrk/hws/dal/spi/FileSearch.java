package com.xrk.hws.dal.spi;

import java.util.List;

public class FileSearch implements Search
{
	@Override
	public List<Doc> search(String keyword)
	{
        System.out.println("now use file system search. keyword:" + keyword);  
        return null; 
	}

}
