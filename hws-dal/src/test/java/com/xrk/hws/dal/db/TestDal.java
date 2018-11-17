package com.xrk.hws.dal.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xrk.hws.dal.DalManager;
import com.xrk.hws.dal.core.DataSet;

/**
 * TestMongo: TestMongo.java.
 *
 * <br>==========================
 * <br> 公司：广州向日葵信息科技有限公司
 * <br> 开发：lijp<lijingping@xiangrikui.com>
 * <br> 版本：1.0
 * <br> 创建时间：2015年4月27日
 * <br> JDK版本：1.7
 * <br>==========================
 */
public class TestDal
{
	private static  DalManager dalManager = null;
	
	public static void testPgsql()
	{
		DataSet dataSet;
		Province entity = new Province();
		entity.setName("湖南省");
		entity.setInitials("HN");
		entity.setIsBiz(1);
		entity.setOrderNo(1);
		
		dataSet = dalManager.getDataSet(Province.class);
		
		if(dataSet == null)
		{
			System.out.printf("Fail to get my %s dataset\r\n",TestEntity.class.getName());
			return;	
		}
		//新增数据
		if(dataSet.insertOne("insertone",entity,true,null) == 0)
		{
			System.out.println("Fail to insert data");
			return;						
		}
		
		//查找数据
		entity = dataSet.findOne("findone", new Object[]{0});
		if(entity == null)
		{
			System.out.println("Fail to find data");
			return;						
		}
		
		System.out.println("findone:Enity.a = "+entity.getName()+"Enity.b = "+entity.getInitials());
		
		//批量查找数据
		List<Province> entities = dataSet.findMulti("mfind", new Object[]{1}, null,1,3);
		for(Province entry : entities)
		{
			if(entry == null)
			{
				System.out.println("Fail to find data");
				return;						
			}
			System.out.println("findMulti: Enity.a = "+entry.getName()+"Enity.b = "+entry.getInitials());
		}
		
		// 更新数据
		dataSet.updateOne("update", new Object[]{new Integer(1)},new Object[]{new String("湖南省")},true,true);	
		
		// 删除数据
		int effectRow = dataSet.deleteMulti("delete", new Object[]{new Integer(2)});
		System.out.println(effectRow);
	}
	
	public static void testMysql()
	{
		DataSet dataSet;
		Province entity = new Province();
		entity.setId(2);
		entity.setName("江西省");
		entity.setInitials("JX");
		entity.setIsBiz(1);
		entity.setOrderNo(1);
		
		dataSet = dalManager.getDataSet(Province.class);
		
		if(dataSet == null)
		{
			System.out.printf("Fail to get my %s dataset\r\n",TestEntity.class.getName());
			return;	
		}
		//新增数据
		if(dataSet.insertOne("insertone",entity,true,null) == 0)
		{
			System.out.println("Fail to insert data");
			return;						
		}
		
		System.out.println("Enity.a = "+entity.getName()+"Enity.b = "+entity.getInitials());
	}
	
	public static  void testMongo()
	{
		DataSet dataSet;
		TestEntity entity = new TestEntity();
		TestMemberEntity member = new TestMemberEntity();
		
		member.setI(10);
		member.setK(new ArrayList<String>());
		member.getK().add("test");		

		entity.setA(1234);
		entity.setB("3456");
		entity.setC(new String[]{"ttt","ddd"});
		entity.setD(new ArrayList<Double>(2));
		entity.getD().add(23.12012);
		entity.getD().add(130.1202121);
		
		entity.setMember(member);
		
		entity.getM().put("test", member);
		

		dataSet = dalManager.getDataSet(TestEntity.class);
		if(dataSet == null)
		{
			System.out.printf("Fail to get my %s dataset\r\n",TestEntity.class.getName());
			return;			
		}	
		//新增数据
		if(dataSet.insertOne("insertone",entity,true,null) == 0)
		{
			System.out.println("Fail to insert data");
			return;						
		}
		
		
		//查找数据
		Object[] query = new Object[1];
		query[0] = new Integer(1234);
		entity = dataSet.findOne("findone", query);
		if(entity == null)
		{
			System.out.println("Fail to find data");
			return;						
		}		

	    System.out.println("Enity.a = "+entity.getA()+"Enity.b = "+entity.getB());
	    
	    //修改数据
		query[0] = new Integer(1234);
		Object[] update = new Object[1];
		update[0] = new String("5678");
		dataSet.updateOne("update", query,update,true,true);	
		
		query[0] = new Integer(1234);
		entity = dataSet.findOne("findone", query);
		if(entity == null)
		{
			System.out.println("Fail to find data");
			return;						
		}		

	    System.out.println("Enity.a = "+entity.getA()+"Enity.b = "+entity.getB());
		
		query[0] = new Integer(1234);
		HashMap<String,Object> upKVs = new HashMap<String,Object>();
		upKVs.put("b", "456789");
		dataSet.updateOne("update", query,upKVs,true,true);
		
		query[0] = new Integer(1234);
		entity = dataSet.findOne("findone", query);
		if(entity == null)
		{
			System.out.println("Fail to find data");
			return;						
		}		

	    System.out.println("Enity.a = "+entity.getA()+"Enity.b = "+entity.getB());
	    
	    //删除数据
	    dataSet.deleteMulti("delete", new Object[]{new Integer(1234),new String("456789")});
	}
	
	public static  void testMc()
	{
		TestMcEntity entity = new TestMcEntity();
		
		entity.setA(1234);
		entity.setB(34.56);
		entity.setC("abcddee");
		entity.setD(new String[]{"abc","efg"});
		entity.setE(new ArrayList<String>());
		entity.getE().add("efg");
		entity.getE().add("ijk");
		entity.setMember(new TestMemberEntity());
		entity.getMember().setK(new ArrayList<String>());
		entity.getMember().getK().add("efg");
		
		DataSet ds = dalManager.getDataSet(TestMcEntity.class);
		HashMap<String,Object> extParas = new HashMap<String,Object>();
		extParas.put(DataSet.EXPARA_EXPTIME,new Integer(500));
		ds.insertOne("insertone", entity, true,extParas);
		
		entity = ds.findOne("findone", new Object[]{new Integer(1234)});
		
		System.out.printf("entity: a: %d,m.k:%s\r\n",entity.getA(),entity.getMember().getK());

		ds.deleteMulti("delete", new Object[]{new Integer(1234)});
	}
	
	
	public static void main(String[] args) 
	{	
		dalManager = DalManager.getInstance();
		
		if(dalManager == null)
		{
			System.out.println("Get dal instance error");
			return;			
		}
		if(DalManager.init("configs.xml") != 0)
		{
			System.out.println("Load config error");
			return;			
		}
		TestDal.testMongo();
//		TestDal.testMc();
//		TestDal.testMysql();
//		TestDal.testPgsql();
	}
}
