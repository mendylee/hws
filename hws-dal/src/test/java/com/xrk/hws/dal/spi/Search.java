package com.xrk.hws.dal.spi;

import java.util.List;

public interface Search
{
	List<Doc> search(String keyword);
}
