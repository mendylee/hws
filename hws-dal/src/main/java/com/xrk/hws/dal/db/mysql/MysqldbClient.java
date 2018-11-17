package com.xrk.hws.dal.db.mysql;

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

import com.mysql.jdbc.PreparedStatement;
import com.xrk.hws.dal.common.ClustClient;
import com.xrk.hws.dal.common.Command;
import com.xrk.hws.dal.common.CommandTemplate;
import com.xrk.hws.dal.common.Database;
import com.xrk.hws.dal.core.CommandType;
import com.xrk.hws.dal.core.DBServer;
import com.xrk.hws.dal.core.DataSet;
import com.xrk.hws.dal.core.Formater;
import com.xrk.hws.dal.core.PhysicDatabase;
import com.xrk.hws.dal.db.pgsql.PgsqldbCommand;
import com.xrk.hws.dal.exception.DalException;
import com.xrk.hws.dal.jdbc.SQL;
import com.xrk.hws.dal.utils.DataSourceUtil;
import com.xrk.hws.dal.utils.ReflectUtil;

/**
 * 类: Mysql客户端.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年5月21日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class MysqldbClient extends ClustClient
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
                System.out.printf("Fail connect to  mysql server %s %d",server.getHost(),server.getPort());
                ret = 1;
            }	
		} 
	    return ret;
    }
	
	@Override
    public <T> Command insertOne(Database db, DataSet set, T data, CommandTemplate temp,boolean upDuplicated, Map<String, Object> extParams)
    {
		MysqldbCommand command = null;
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
		PreparedStatement ps = null;
		int ret = 0;
		
		String sql = ((MysqldbCommand) command).getSql();
		if(sql != null && sql.length() > 0)
		{
			Connection connection = null;
			try
			{
				if(((MysqldbCommand)command).isDupUpdate() == false)
				{	
					connection = dataSource.getConnection();
					ps = (PreparedStatement) connection.prepareStatement(sql);
					ret =  ps.executeUpdate();
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
    public Command findOne(Database db, DataSet set, CommandTemplate temp, Object[] params)
    {
    	MysqldbCommand command = null;
    	
		if (temp.getType() == CommandType.FINDONE)
		{
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	Formater fieldsByFormat = temp.getFormat(Command.FIELDS_FORMAT);
	    	
	    	String query = ReflectUtil.formatPrintable(queryFormat, params);
	    	String orderBy = ReflectUtil.formatPrintable(orderByFormat, null);
	    	String fieldses = ReflectUtil.formatPrintable(fieldsByFormat, null);
	    	
	    	command = this.getCommand(db, set);
			if(command != null)
			{
				command.setTemp(temp);
				command.setQuery(query);
				command.setOrderBy(orderBy);
				command.setFields(fieldses);
			}
		}
	    return command;
    }
    
	@Override
    public <T> T findOne(PhysicDatabase db, final DataSet set, final Command command)
    {
		T data = null;
		StringBuilder builder = null;
		Statement  stmt = null;
		
		builder = new StringBuilder();
		String sql = new SQL(){
			{
				SELECT(((MysqldbCommand) command).getFields() == null ? "*" : ((MysqldbCommand) command).getFields());
				FROM(set.getMyClass().getTableName());
				WHERE(((MysqldbCommand) command).getQuery());
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
		return data;
    }

	@Override
    public Command findMulti(Database db, DataSet set, CommandTemplate temp,Object[] queryParams, Object[] orderbyParams)
    {
    	MysqldbCommand command = null;
    	
		if (temp.getType() == CommandType.MFIND)
		{
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	
	    	String query = ReflectUtil.formatPrintable(queryFormat, queryParams);
	    	String orderBy = ReflectUtil.formatPrintable(orderByFormat, orderbyParams);
	    	
	    	command = this.getCommand(db, set);
			if(command != null)
			{
				command.setTemp(temp);
				command.setQuery(query);
				command.setOrderBy(orderBy);
			}
		}
	    return command;
    }

	@Override
    public Command findMulti(Database db, DataSet set, CommandTemplate temp,Object[] queryParams, Object[] orderbyParams, long limit)
    {
    	MysqldbCommand command = null;
    	
		if (temp.getType() == CommandType.MFIND)
		{
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	
	    	String query = ReflectUtil.formatPrintable(queryFormat, queryParams);
	    	String orderBy = ReflectUtil.formatPrintable(orderByFormat, orderbyParams);
	    	
	    	command = this.getCommand(db, set);
			if(command != null)
			{
				command.setTemp(temp);
				command.setQuery(query);
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
    	MysqldbCommand command = null;
    	
		if (temp.getType() == CommandType.MFIND)
		{
	    	Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
	    	Formater orderByFormat = temp.getFormat(Command.ORDERBY_FORMAT);
	    	
	    	String query = ReflectUtil.formatPrintable(queryFormat, queryParams);
	    	String orderBy = ReflectUtil.formatPrintable(orderByFormat, orderbyParams);
	    	
	    	command = this.getCommand(db, set);
			if(command != null)
			{
				command.setTemp(temp);
				command.setQuery(query);
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
				SELECT(((MysqldbCommand) command).getFields() == null ? "*" : ((MysqldbCommand) command).getFields());
				FROM(set.getMyClass().getTableName());
				WHERE(((MysqldbCommand) command).getQuery());
			}
		}.usingAppender(builder).toString();
		
		if(((MysqldbCommand) command).getOffset() > 0 && ((MysqldbCommand) command).getLimit() > 0)
		{
			builder.append(" limit ")
				   .append(((PgsqldbCommand) command).getOffset())
				   .append(",")
				   .append(((PgsqldbCommand) command).getLimit());
		}
		else
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
		return datas;
    }
	
	@Override
    public <T> Command insertMulti(Database db, DataSet set, List<T> datas, CommandTemplate template, boolean upDuplicated,Map<String, Object> extParams)
	{    
		MysqldbCommand command = null;
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
		
		List<String> sqls = ((MysqldbCommand) command).getSqls();
		if(sqls != null && sqls.size() > 0)
		{
			if(((MysqldbCommand)command).isDupUpdate() == false)
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
		MysqldbCommand command = null;
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
					WHERE(((MysqldbCommand) command).getQuery());
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
		MysqldbCommand command = null;
		
		if (temp.getType() == CommandType.UPDATE)
		{
			Formater queryFormat = temp.getFormat(Command.QUERY_FORMAT);
			Formater updateFormat = temp.getFormat(Command.UPDATE_FORMAT);
			
			String query = ReflectUtil.formatPrintable(queryFormat, confParams);
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
		MysqldbCommand command = null;
		
		if (temp.getType() == CommandType.UPDATE)
		{
			Formater queryFormat = temp.getFormat(Command.UPDATE_FORMAT);
			String query = ReflectUtil.formatPrintable(queryFormat, confParams);
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
				SET(((MysqldbCommand) command).getUpdate());
				WHERE(((MysqldbCommand) command).getQuery());
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
    public long count(PhysicDatabase db, final DataSet set, final Command command)
    {
		Statement  stmt = null;
		StringBuilder builder = null;
		ResultSet result = null;
		int ret = 0;
		
		builder = new StringBuilder();
		String sql = new SQL(){
			{
				SELECT(((MysqldbCommand) command).getFields() == null ? "count(*)" : ((MysqldbCommand) command).getFields());
				FROM(set.getMyClass().getTableName());
				WHERE(((MysqldbCommand) command).getQuery());
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

	private MysqldbCommand getCommand(Database database, DataSet set)
	{
		MysqldbCommand com = new MysqldbCommand();
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
