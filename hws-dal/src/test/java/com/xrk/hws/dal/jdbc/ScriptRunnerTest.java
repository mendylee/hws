package com.xrk.hws.dal.jdbc;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;

import com.xrk.hws.common.io.Resources;
import com.xrk.hws.dal.BaseDataTest;
import com.xrk.hws.dal.datasource.pooled.PooledDataSource;
import com.xrk.hws.dal.datasource.unpooled.UnpooledDataSource;

/**
 * ScriptRunnerTest: ScriptRunnerTest.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年6月5日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class ScriptRunnerTest extends BaseDataTest
{
	@Test
	@Ignore("This fails with HSQLDB 2.0 due to the create index statements in the schema script")
	public void shouldRunScriptsBySendingFullScriptAtOnce() throws Exception
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		Connection conn = ds.getConnection();
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setSendFullScript(true);
		runner.setAutoCommit(true);
		runner.setStopOnError(false);
		runner.setErrorLogWriter(null);
		runner.setLogWriter(null);
		runJPetStoreScripts(runner);
		assertProductsTableExistsAndLoaded();
	}
	
	@Test
	public void shouldRunScriptsUsingConnection() throws Exception
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		Connection conn = ds.getConnection();
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setAutoCommit(true);
		runner.setStopOnError(false);
		runner.setErrorLogWriter(null);
		runner.setLogWriter(null);
		runJPetStoreScripts(runner);
		assertProductsTableExistsAndLoaded();
	}

	@Test
	public void shouldRunScriptsUsingProperties() throws Exception
	{
		Properties props = Resources.getResourceAsProperties(JPETSTORE_PROPERTIES);
		DataSource dataSource = new UnpooledDataSource(props.getProperty("driver"),
		                                               props.getProperty("url"),
		                                               props.getProperty("username"),
		                                               props.getProperty("password"));
		ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
		runner.setAutoCommit(true);
		runner.setStopOnError(false);
		runner.setErrorLogWriter(null);
		runner.setLogWriter(null);
		runJPetStoreScripts(runner);
		assertProductsTableExistsAndLoaded();
	}

	@Test
	public void shouldReturnWarningIfEndOfLineTerminatorNotFound() throws Exception
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		Connection conn = ds.getConnection();
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setAutoCommit(true);
		runner.setStopOnError(false);
		runner.setErrorLogWriter(null);
		runner.setLogWriter(null);

		String resource = "org/apache/ibatis/jdbc/ScriptMissingEOLTerminator.sql";
		Reader reader = Resources.getResourceAsReader(resource);

		try {
			runner.runScript(reader);
			fail("Expected script runner to fail due to missing end of line terminator.");
		}
		catch (Exception e) {
			assertTrue(e.getMessage().contains("end-of-line terminator"));
		}
	}

	@Test
	public void commentAferStatementDelimiterShouldNotCauseRunnerFail() throws Exception
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		Connection conn = ds.getConnection();
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setAutoCommit(true);
		runner.setStopOnError(true);
		runner.setErrorLogWriter(null);
		runner.setLogWriter(null);
		runJPetStoreScripts(runner);

		String resource = "org/apache/ibatis/jdbc/ScriptCommentAfterEOLTerminator.sql";
		Reader reader = Resources.getResourceAsReader(resource);

		try {
			runner.runScript(reader);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void shouldReturnWarningIfNotTheCurrentDelimiterUsed() throws Exception
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		Connection conn = ds.getConnection();
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setAutoCommit(false);
		runner.setStopOnError(true);
		runner.setErrorLogWriter(null);
		runner.setLogWriter(null);

		String resource = "org/apache/ibatis/jdbc/ScriptChangingDelimiterMissingDelimiter.sql";
		Reader reader = Resources.getResourceAsReader(resource);

		try {
			runner.runScript(reader);
			fail("Expected script runner to fail due to the usage of invalid delimiter.");
		}
		catch (Exception e) {
			assertTrue(e.getMessage().contains("end-of-line terminator"));
		}
	}

	@Test
	public void changingDelimiterShouldNotCauseRunnerFail() throws Exception
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		Connection conn = ds.getConnection();
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setAutoCommit(false);
		runner.setStopOnError(true);
		runner.setErrorLogWriter(null);
		runner.setLogWriter(null);
		runJPetStoreScripts(runner);

		String resource = "org/apache/ibatis/jdbc/ScriptChangingDelimiter.sql";
		Reader reader = Resources.getResourceAsReader(resource);

		try {
			runner.runScript(reader);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testLogging() throws Exception
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		Connection conn = ds.getConnection();
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setAutoCommit(true);
		runner.setStopOnError(false);
		runner.setErrorLogWriter(null);
		runner.setSendFullScript(false);
		StringWriter sw = new StringWriter();
		PrintWriter logWriter = new PrintWriter(sw);
		runner.setLogWriter(logWriter);

		Reader reader = new StringReader("select userid from account where userid = 'j2ee';");
		runner.runScript(reader);

		assertEquals(
		        "select userid from account where userid = 'j2ee'"
		                + System.getProperty("line.separator")
		                + System.getProperty("line.separator") + "USERID\t"
		                + System.getProperty("line.separator") + "j2ee\t"
		                + System.getProperty("line.separator"), sw.toString());
	}

	@Test
	public void testLoggingFullScipt() throws Exception
	{
		DataSource ds = createUnpooledDataSource(JPETSTORE_PROPERTIES);
		Connection conn = ds.getConnection();
		ScriptRunner runner = new ScriptRunner(conn);
		runner.setAutoCommit(true);
		runner.setStopOnError(false);
		runner.setErrorLogWriter(null);
		runner.setSendFullScript(true);
		StringWriter sw = new StringWriter();
		PrintWriter logWriter = new PrintWriter(sw);
		runner.setLogWriter(logWriter);

		Reader reader = new StringReader("select userid from account where userid = 'j2ee';");
		runner.runScript(reader);

		assertEquals(
		        "select userid from account where userid = 'j2ee';"
		                + System.getProperty("line.separator")
		                + System.getProperty("line.separator") + "USERID\t"
		                + System.getProperty("line.separator") + "j2ee\t"
		                + System.getProperty("line.separator"), sw.toString());
	}

	private void runJPetStoreScripts(ScriptRunner runner) throws IOException, SQLException
	{
		runScript(runner, JPETSTORE_DDL);
		runScript(runner, JPETSTORE_DATA);
	}

	private void assertProductsTableExistsAndLoaded() throws IOException, SQLException
	{
		PooledDataSource ds = createPooledDataSource(JPETSTORE_PROPERTIES);
		try 
		{
			Connection conn = ds.getConnection();
//			SqlRunner executor = new SqlRunner(conn);
//			List<Map<String, Object>> products = executor.selectAll("SELECT * FROM PRODUCT");
//			assertEquals(16, products.size());
		}
		finally 
		{
			ds.forceCloseAll();
		}
	}
}
