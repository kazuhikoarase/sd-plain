package com.example.common.sql;


import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.example.common.TestCaseBase;


public class TableRowFactoryTest extends TestCaseBase {

	@Test 
	public void test1() {
		MyTbl.Row t = TableRowFactory.create(MyTbl.Row.class);
		
		t.setA("A");
		Assert.assertEquals("A", t.getA() );
		Assert.assertEquals("A", t.getValue("a") );
		Assert.assertTrue(t.isSet("a") );
		Assert.assertFalse(t.isSet("b") );
	}

	@Test 
	public void test2() {
		MyTbl.Row t = MyTbl.getInstance().newRow();
		t.setA("z'z");
		t.setB("xx");
		t.setD("dd");
		t.setC1("xxx");
		t.setC2(BigDecimal.ZERO);
		t.setC3(5);
		
		SqlString s1 = new SqlString("select A,B,C,D,C1,C2,C3 from TABNAME where 1=1");
		SqlString s2 = new SqlString("select A,B,C,D,C1,C2,C3 from TABNAME where A='z''z' and D='dd'");
		SqlString s3 = new SqlString("insert into TABNAME (A,B,D,C1,C2,C3) values ('z''z','xx','dd','xxx',0,5)");
		SqlString s4 = new SqlString("update TABNAME set B='xx',C1='xxx',C2=0,C3=5 where A='z''z' and D='dd'");
		SqlString s5 = new SqlString("delete from TABNAME where A='z''z' and D='dd'");

		Assert.assertEquals(s1, MyTbl.getInstance().buildSelectSql(new SqlString("where 1=1") ) );
		Assert.assertEquals(s2, MyTbl.getInstance().buildSelectRowSql(t) );
		Assert.assertEquals(s3, MyTbl.getInstance().buildInsertRowSql(t) );
		Assert.assertEquals(s4, MyTbl.getInstance().buildUpdateRowSql(t) );
		Assert.assertEquals(s5, MyTbl.getInstance().buildDeleteRowSql(t) );

	}
	
	static class MyTbl extends Table<MyTbl.Row> {

		private MyTbl() {
			super(Row.class);
		}
		
		private static final MyTbl instance = new MyTbl();
		public static MyTbl getInstance() {
			return instance;
		}

		public interface Row extends TableRow {
			String getA();
			void setA(String a);
			String getB();
			void setB(String b);
			String getC();
			void setC(String c);
			String getD();
			void setD(String d);
			String getC1();
			void setC1(String c1);
			BigDecimal getC2();
			void setC2(BigDecimal c2);
			Integer getC3();
			void setC3(Integer c3);
		}

		@Override
		protected TableMetaData getTableMetaData() {
			return new TableMetaData("SCHEM", "TABNAME");
		}

		@Override
		protected ColumnMetaData[] getColumnMetaData() {
			return new ColumnMetaData[] {
				new ColumnMetaData("A", "a", String.class, true),
				new ColumnMetaData("B", "b", String.class, false),
				new ColumnMetaData("C", "c", String.class, false),
				new ColumnMetaData("D", "d", String.class, true),
				new ColumnMetaData("C1", "c1", String.class, false),
				new ColumnMetaData("C2", "c2", BigDecimal.class, false),
				new ColumnMetaData("C3", "c3", Integer.class, false)
			};
		}
	}
}