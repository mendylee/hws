package com.xrk.hws.dal.db.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;
import javax.xml.ws.WebServiceException;

import com.xrk.hws.common.utils.StringUtils;
import com.xrk.hws.dal.common.ClustClient;
import com.xrk.hws.dal.common.Command;
import com.xrk.hws.dal.common.CommandTemplate;
import com.xrk.hws.dal.common.Database;
import com.xrk.hws.dal.core.CommandType;
import com.xrk.hws.dal.core.DBServer;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.hws.dal.core.Formater;
import com.xrk.hws.dal.core.PhysicDatabase;
import com.xrk.hws.dal.exception.DalException;
import com.xrk.hws.dal.jdbc.SQL;
import com.xrk.hws.dal.utils.DataSourceUtil;
import com.xrk.hws.dal.utils.ReflectUtil;

/**
 * 类: postgresql客户端实现.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月28日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class PgsqldbClient extends ClustClient
{
	private static DataSource dataSource;

	@Override
    public int toBeReady()
    {
		int ret  = 0;
		Iterator<Entry<Integer, DBServer>> iter = this.servers.entrySet().iterator(); 
		while (iter.hasNext()) 
		{ 
			Entry<Integer, DBServer> entry = iter.next(); 
			DBServer server = entry.getValue();
			try  
			{
				dataSource = DataSourceUtil.createDruidDataSource(server);
			}
            catch (Exception e) 
            {
                System.out.printf("Fail connect to  postgresql server %s %d",server.getHost(),server.getPort());
                ret = 1;
            }	
		} 
	    return ret;
    }
	
	@Override
    public <T> Command insertOne(Database db, DataSet set, T data, CommandTemplate temp,boolean upDuplicated, Map<String, Object> extParams)
    {
		PgsqldbCommand command = null;
		if (temp.getType() == CommandType.INSETONE)
		{
			command = this.getCommand(db, set);
			if(command != null)
			{
				String sql = ReflectUtil.putObject(CommandType.INSETONE, set.getMyClass(), data);
				command.setSql(sql);
				command.setUpInsert(upDuplicated);
			}
		}
	    return command;
    }

	@Override
    public int insertOne(PhysicDatabase database, DataSet dataSet, Command command) throws DalException
    {
		Statement  stmt = null;
		int ret = 0;
		
		String sql = ((PgsqldbCommand) command).getSql();
		if(sql != null && sql.length() > 0)
		{
			Connection connection = null;
			try
			{
				if(((PgsqldbCommand)command).isDupUpdate() == false)
				{	
					connection = dataSource.getConnection();
					stmt = (Statement) connection.createStatement();
					ret =  stmt.executeUpdate(sql);
				}
			}
			catch(SQLException e)
			{
	        	String message = "status: " + e.getSQLState() + ", [sql]: " + sql;
	        	DalException er = new DalException(message, e);
	        	er.logError(e, message);
	        	throw er;
			}
			finally
			{
				try {
					connection.close();
				} catch (SQLException e) {
		        	String message = "close connection fail!";
		        	DalException er = new DalException(message, e);
		        	er.logError(e, message);
		        	throw er;
				}
			}
		}
	    return ret;
    }

    @Override
    public Command findOne(Database db, DataSet set, CommandTemplate temp, Object[] params)
    {
    	PgsqldbCommand command = null;
    	
		if (temp.getType() == CommandType.FINDONE)
		{
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater groupbyFormat = temp.getFormat(Command.GROUPBY_FORMAT);
	    	Formater havingFormat  = temp.getFormat(Command.HAVING_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	Formater fieldsByFormat = temp.getFormat(Command.FIELDS_FORMAT);
	    	
	    	String query = ReflectUtil.formatPrintable(queryFormat, params);
	    	String groupby = ReflectUtil.formatPrintable(groupbyFormat, null);
	    	String having  = ReflectUtil.formatPrintable(havingFormat, null);
	    	String orderBy = ReflectUtil.formatPrintable(orderByFormat, null);
	    	String fieldses = ReflectUtil.formatPrintable(fieldsByFormat, null);
	    	
	    	command = this.getCommand(db, set);
			if(command != null)
			{
				command.setTemp(temp);
				command.setQuery(query);
				command.setGroupby(groupby);
				command.setHaving(having);
				command.setOrderBy(orderBy);
				command.setFields(fieldses);
			}
		}
	    return command;
    }
    
	@Override
    public <T> T findOne(final PhysicDatabase db, final DataSet set, final Command command)
    {
		T data = null;
		StringBuilder builder = null;
		Statement  stmt = null;
		
		builder = new StringBuilder();
		String sql = new SQL(){
			{
				SELECT(((PgsqldbCommand) command).getFields() == null ? "*" : ((PgsqldbCommand) command).getFields());
				FROM(set.getMyClass().getTableName());
				WHERE(((PgsqldbCommand) command).getQuery());
				if(((PgsqldbCommand) command).getGroupby() != null)
				{
					GROUP_BY(((PgsqldbCommand) command).getGroupby());
				}
				if(((PgsqldbCommand) command).getHaving() != null)
				{
					HAVING(((PgsqldbCommand) command).getHaving());
				}
				if(((PgsqldbCommand) command).getOrderBy() != null)
				{
					ORDER_BY(((PgsqldbCommand) command).getOrderBy());
				}
			}
		}.usingAppender(builder).toString();
		
		if(sql != null && sql.length() > 0)
		{
			Connection connection = null;
			try
			{
				connection = dataSource.getConnection();
				stmt = (Statement) connection.createStatement();
				ResultSet rs =  stmt.executeQuery(sql);
				while(rs.next())
				{
					data =  ReflectUtil.getObject(set.getMyClass(), rs);	
				}
			}
			catch(SQLException e)
			{
	        	String message = "status: " + e.getSQLState() + ", [sql]: " + sql;
	        	DalException er = new DalException(message, e);
	        	er.logError(e, message);
	        	throw er;
			}
			finally
			{
				try {
					connection.close();
				} catch (SQLException e) {
		        	String message = "close connection fail!";
		        	DalException er = new DalException(message, e);
		        	er.logError(e, message);
		        	throw er;
				}
			}
		}
		return data;
    }
	
	

	@Override
    public Command findMulti(Database db, DataSet set, CommandTemplate temp,Object[] queryParams, Object[] orderbyParams)
    {
    	PgsqldbCommand command = null;
    	
		if (temp.getType() == CommandType.MFIND)
		{
			Formater fieldsFormat = temp.getFormat(Command.FIELDS_FORMAT);
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater groupbyFormat = temp.getFormat(Command.GROUPBY_FORMAT);
	    	Formater havingFormat  = temp.getFormat(Command.HAVING_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	
	    	String fields = ReflectUtil.formatPrintable(fieldsFormat, null);
	    	String query = ReflectUtil.formatPrintable(queryFormat, queryParams);
	    	String groupby = ReflectUtil.formatPrintable(groupbyFormat, null);
	    	String having  = ReflectUtil.formatPrintable(havingFormat, null);
	    	String orderBy = ReflectUtil.formatPrintable(orderByFormat, orderbyParams);
	    	
	    	command = this.getCommand(db, set);
			if(command != null)
			{
				command.setTemp(temp);
				command.setFields(fields);
				command.setQuery(query);
				command.setGroupby(groupby);
				command.setHaving(having);
				command.setOrderBy(orderBy);
			}
		}
	    return command;
    }

	@Override
    public Command findMulti(Database db, DataSet set, CommandTemplate temp,Object[] queryParams, Object[] orderbyParams, long limit)
    {
    	PgsqldbCommand command = null;
    	
		if (temp.getType() == CommandType.MFIND)
		{
			Formater fieldsFormat = temp.getFormat(Command.FIELDS_FORMAT);
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater groupbyFormat = temp.getFormat(Command.GROUPBY_FORMAT);
	    	Formater havingFormat  = temp.getFormat(Command.HAVING_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	
	    	String fields = ReflectUtil.formatPrintable(fieldsFormat, null);
	    	String query = ReflectUtil.formatPrintable(queryFormat, queryParams);
	    	String groupby = ReflectUtil.formatPrintable(groupbyFormat, null);
	    	String having  = ReflectUtil.formatPrintable(havingFormat, null);
	    	String orderBy = ReflectUtil.formatPrintable(orderByFormat, orderbyParams);
	    	
	    	command = this.getCommand(db, set);
			if(command != null)
			{
				command.setTemp(temp);
				command.setFields(fields);
				command.setQuery(query);
				command.setGroupby(groupby);
				command.setHaving(having);
				command.setOrderBy(orderBy);
				command.setLimit(limit);
			}
		}
	    return command;
    }

	@Override
    public Command findMulti(final Database db, final DataSet set, final CommandTemplate temp,final Object[] queryParams, 
                             final Object[] orderbyParams, final long offset, final long limit)
    {
    	PgsqldbCommand command = null;
    	
		if (temp.getType() == CommandType.MFIND)
		{
			Formater fieldsFormat = temp.getFormat(Command.FIELDS_FORMAT);
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater groupbyFormat = temp.getFormat(Command.GROUPBY_FORMAT);
	    	Formater havingFormat  = temp.getFormat(Command.HAVING_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	
	    	String fields = ReflectUtil.formatPrintable(fieldsFormat, null);
	    	String query 	= ReflectUtil.formatPrintable(queryFormat, queryParams);
	    	String groupby = ReflectUtil.formatPrintable(groupbyFormat, null);
	    	String having  = ReflectUtil.formatPrintable(havingFormat, null);
	    	String orderBy  = ReflectUtil.formatPrintable(orderByFormat, orderbyParams);
	    	
	    	command = this.getCommand(db, set);
			if(command != null)
			{
				command.setTemp(temp);
				command.setFields(fields);
				command.setQuery(query);
				command.setGroupby(groupby);
				command.setHaving(having);
				command.setOrderBy(orderBy);
				command.setOffset(offset);
				command.setLimit(limit);
			}
		}
	    return command;
    }
	
	@Override
    public <T> List<T> findMulti(final PhysicDatabase db, final DataSet set, final Command command)
    {
		StringBuilder builder = null;
		Statement  stmt = null;
		List<T> datas = null;
		T data = null;
		
		builder = new StringBuilder();
		new SQL(){
			{
				SELECT(((PgsqldbCommand) command).getFields() == null ? "*" : ((PgsqldbCommand) command).getFields());
				FROM(set.getMyClass().getTableName());
				if (!StringUtils.isEmpty(((PgsqldbCommand) command).getQuery()))
				{
					WHERE(((PgsqldbCommand) command).getQuery());
				}
				if(((PgsqldbCommand) command).getGroupby() != null)
				{
					GROUP_BY(((PgsqldbCommand) command).getGroupby());
				}
				if(((PgsqldbCommand) command).getHaving() != null)
				{
					HAVING(((PgsqldbCommand) command).getHaving());
				}
				if(((PgsqldbCommand) command).getOrderBy() != null)
				{
					ORDER_BY(((PgsqldbCommand) command).getOrderBy());
				}
				
			}
		}.usingAppender(builder).toString();
		
		if(((PgsqldbCommand) command).getOffset() > 0 && ((PgsqldbCommand) command).getLimit() > 0)
		{
			builder.append(" limit ")
				   .append(((PgsqldbCommand) command).getOffset())
				   .append(" offset ")
				   .append(((PgsqldbCommand) command).getLimit());
		}
		else if(((PgsqldbCommand) command).getLimit() > 0)
		{
			builder.append(" limit ").append(((PgsqldbCommand) command).getLimit());
		}
		String sql = builder.toString();
		if(sql != null && sql.length() > 0)
		{
			Connection connection = null;
			try
			{
				connection = dataSource.getConnection();
				stmt = (Statement) connection.createStatement();
				ResultSet rs =  stmt.executeQuery(sql);
				datas = new ArrayList<T>();
				while(rs.next())
				{
					data =  ReflectUtil.getObject(set.getMyClass(), rs);	
					datas.add(data);
				}
			}
			catch(SQLException e)
			{
	        	String message = "status: " + e.getSQLState() + ", [sql]: " + sql;
	        	DalException er = new DalException(message, e);
	        	er.logError(e, message);
	        	throw er;
			}
			finally
			{
				try {
					connection.close();
				} catch (SQLException e) {
		        	String message = "close connection fail!";
		        	DalException er = new DalException(message, e);
		        	er.logError(e, message);
		        	throw er;
				}
			}
		}
		return datas;
    }
	
	@Override
    public <T> Command insertMulti(Database db, DataSet set, List<T> datas, CommandTemplate template, boolean upDuplicated,Map<String, Object> extParams)
	{    
		PgsqldbCommand command = null;
		if (template.getType() == CommandType.MINSET && datas != null && datas.size() > 0)
		{
			command = this.getCommand(db, set);
			if(command != null)
			{
				List<String> sqls = new ArrayList<String>(datas.size());
				for (T data : datas)
				{
					String sql = ReflectUtil.putObject(CommandType.INSETONE,set.getMyClass(), data);
					if (sql != null)
					{
						sqls.add(sql);
					}
					else
					{
						this.freeCommand(db, set, command);
						return null;
					}
				}
				command.setSqls(sqls);
				command.setTemp(template);
				command.setUpInsert(upDuplicated);
			}
		}
	    return command;
    }
	
	@Override
    public int insertMulti(PhysicDatabase database, DataSet dataSet, Command command)
    {
		Statement  stmt = null;
		int ret = 0;
		
		List<String> sqls = ((PgsqldbCommand) command).getSqls();
		if(sqls != null && sqls.size() > 0)
		{
			if(((PgsqldbCommand)command).isDupUpdate() == false)
			{	
				Connection connection = null;
				try
				{
					connection = dataSource.getConnection();
					stmt = (Statement) connection.createStatement();
					for(String sql:sqls)
					{
						ret +=  stmt.executeUpdate(sql);
					}
				}
				catch(SQLException e)
				{
		        	String message = "status: " + e.getSQLState() + ", [sqls]: " + sqls;
		        	DalException er = new DalException(message, e);
		        	er.logError(e, message);
		        	throw er;
				}
				finally
				{
					try 
					{
						connection.close();
					} 
					catch (SQLException e) 
					{
			        	String message = "close connection fail!";
			        	DalException er = new DalException(message, e);
			        	er.logError(e, message);
			        	throw er;
					}
				}
			}
		}
	    return ret;
    }

	@Override
    public Command deleteMulti(Database db, DataSet set, CommandTemplate temp, Object[] params)
    {
		PgsqldbCommand command = null;
		if (temp.getType() == CommandType.DELETE)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			String query = ReflectUtil.formatPrintable(queryFormat, params);
			if (query != null)
			{
				command = this.getCommand(db, set);
				if (command != null)
				{
					command.setTemp(temp);
					command.setQuery(query);
				}
			}
		}
	    return command;
    }

	@Override
    public int deleteMulti(PhysicDatabase db, final DataSet set, final Command command)
    {
		Statement  stmt = null;
		StringBuilder builder = null;
		int ret = 0;
		
		builder = new StringBuilder();
		String sql = new SQL(){
				{
					DELETE_FROM(set.getMyClass().getTableName());
					if (!StringUtils.isEmpty(((PgsqldbCommand) command).getQuery()))
					{
						WHERE(((PgsqldbCommand) command).getQuery());
					}
				}
		}.usingAppender(builder).toString();
		
		if(sql != null && sql.length() > 0)
		{
			Connection connection = null;
			try
			{
				connection = dataSource.getConnection();
				stmt = (Statement) connection.createStatement();
				ret =  stmt.executeUpdate(sql);
			}
			catch(SQLException e)
			{
	        	String message = "status: " + e.getSQLState() + ", [sql]: " + sql;
	        	DalException er = new DalException(message, e);
	        	er.logError(e, message);
	        	throw er;
			}
			finally
			{
				try 
				{
					connection.close();
				} 
				catch (SQLException e) 
				{
		        	String message = "close connection fail!";
		        	DalException er = new DalException(message, e);
		        	er.logError(e, message);
		        	throw er;
				}
			}
		}
	    return ret;
    }
	
	@Override
    public Command updateOne(Database db, DataSet set, CommandTemplate temp, Object[] confParams, Object[] upParams, boolean upInsert, boolean multi)
    {
		PgsqldbCommand command = null;
		
		if (temp.getType() == CommandType.UPDATE)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			Formater updateFormat = temp.getFormat(Command.UPDATE_FORMAT);
			
			String query  = ReflectUtil.formatPrintable(queryFormat, confParams);
			String update = ReflectUtil.formatPrintable(updateFormat, upParams);
			
			if (update != null)
			{
				command = this.getCommand(db, set);
				if (command != null)
				{
					command.setTemp(temp);
					command.setSet(set);
					command.setQuery(query);
					command.setUpdate(update);
					command.setUpInsert(upInsert);
					command.setMulti(multi);
				}
			}
		}
		return command;
    }
	
	@Override
    public Command updateOne(Database db, DataSet set, CommandTemplate temp, Object[] confParams, Map<String, Object> upKVs, boolean upInsert)
    {
		PgsqldbCommand command = null;
		
		if (temp.getType() == CommandType.UPDATE)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			String query  = ReflectUtil.formatPrintable(queryFormat, confParams);
			String update = ReflectUtil.formatUpdateAuto(set, temp, upKVs);
			
			if (update != null)
			{
				command = this.getCommand(db, set);
				if (command != null)
				{
					command.setTemp(temp);
					command.setSet(set);
					command.setQuery(query);
					command.setUpdate(update);
					command.setUpInsert(upInsert);
				}
			}
		}
		return command;
    }

	@Override
    public int updateOne(PhysicDatabase db, final DataSet set, final Command command)
    {
		Statement  stmt = null;
		StringBuilder builder = null;
		int ret = 0;
		
		builder = new StringBuilder();
		String sql = new SQL(){
			{
				UPDATE(set.getMyClass().getTableName());
				SET(((PgsqldbCommand) command).getUpdate());
				WHERE(((PgsqldbCommand) command).getQuery());
			}
		}.usingAppender(builder).toString();
		
		if(sql != null && sql.length() > 0)
		{
			Connection connection = null;
			try
			{
				connection = dataSource.getConnection();
				stmt = (Statement) connection.createStatement();
				ret =  stmt.executeUpdate(sql);
			}
			catch(SQLException e)
			{
	        	String message = "status: " + e.getSQLState() + ", [sql]: " + sql;
	        	DalException er = new DalException(message, e);
	        	er.logError(e, message);
	        	throw er;
			}
			finally
			{
				try 
				{
					connection.close();
				} 
				catch (SQLException e) 
				{
		        	String message = "close connection fail!";
		        	DalException er = new DalException(message, e);
		        	er.logError(e, message);
		        	throw er;
				}
			}
		}
	    return ret;
    }
	
	
	@Override
	public Command count(Database db, DataSet set, CommandTemplate temp,Object[] queryParams)
    {
		PgsqldbCommand command = null;
		
		if (temp.getType() == CommandType.COUNT)
		{
			Formater fieldsFormat = temp.getFormat(Command.FIELDS_FORMAT);
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			
			String fields = ReflectUtil.formatPrintable(fieldsFormat, null);
			String query  = ReflectUtil.formatPrintable(queryFormat, queryParams);
			
			if (query != null)
			{
				command = this.getCommand(db, set);
				if (command != null)
				{
					command.setTemp(temp);
					command.setFields(fields);
					command.setQuery(query);
				}
			}
		}
		return command;
    }

	@Override
    public long count(PhysicDatabase db, final DataSet set, final Command command)
    {
		Statement  stmt = null;
		StringBuilder builder = null;
		ResultSet result = null;
		int ret = 0;
		
		builder = new StringBuilder();
		String sql = new SQL(){
			{
				SELECT(((PgsqldbCommand) command).getFields() == null ? "count(*)" : "count(" + ((PgsqldbCommand) command).getFields()+ ")");
				FROM(set.getMyClass().getTableName());
				WHERE(((PgsqldbCommand) command).getQuery());
			}
		}.usingAppender(builder).toString();
		
		if(sql != null && sql.length() > 0)
		{
			Connection connection = null;
			try
			{
				connection = dataSource.getConnection();
				stmt = (Statement) connection.createStatement();
	            result = stmt.executeQuery(sql);
	            return result.next() ? result.getInt(1) : 0;
			}
			catch(SQLException e)
			{
	        	String message = "status: " + e.getSQLState() + ", [sql]: " + sql;
	        	DalException er = new DalException(message, e);
	        	er.logError(e, message);
	        	throw er;
			}
			finally
			{
				try 
				{
					connection.close();
				} 
				catch (SQLException e) 
				{
		        	String message = "close connection fail!";
		        	DalException er = new DalException(message, e);
		        	er.logError(e, message);
		        	throw er;
				}
			}
		}
	    return ret;
    }
	
	

	@Override
    public void freeCommand(Database database, DataSet dataSet, Command command)
    {
		
    }

	private PgsqldbCommand getCommand(Database database, DataSet set)
	{
		PgsqldbCommand com = new PgsqldbCommand();
		com.setSet(set);
		return com;
	}

	@Override
    public int close()
    {
		try 
		{
			Connection connection = dataSource.getConnection();
			if(connection != null)
			{
	            connection.close();
			}
		} 
		catch (WebServiceException |SQLException e1) 
		{
			e1.printStackTrace();
		}
		return 0;
    }

}
