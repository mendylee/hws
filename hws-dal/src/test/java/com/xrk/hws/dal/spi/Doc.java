package com.xrk.hws.dal.spi;

public class Doc
{
	private int id;
	private String name;
	private String content;
	
	public Doc() 
	{
		
    }

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	@Override
    public String toString()
    {
	    return new StringBuilder().append("Id:").append(this.getId())
	    						  .append(",name:").append(this.getName())
	    						  .append(",content:").append(this.getContent()).toString();
    }
	
	
}
