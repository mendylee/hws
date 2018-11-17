package com.xrk.hws.dal.spi;

import java.util.List;

public class DatabaseSearch implements Search
{
	@Override
	public List<Doc> search(String keyword)
	{
        System.out.println("now use database search. keyword:" + keyword);  
        return null;  
	}

}
