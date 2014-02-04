package com.example.common.sql;

import org.junit.Assert;
import org.junit.Test;

import com.example.common.TestCaseBase;

public class SqlTokenizerTest extends TestCaseBase {

	@Test
	public void test1() {
		SqlTokenizer st; 
		
		st = new SqlTokenizer("");
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("aaa");
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("aaa");
		Assert.assertEquals("aaa", st.nextToken().getText() );
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("-- ");
		Assert.assertEquals(SqlTokenizer.Token.COMMENT, st.nextToken().getType() );
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("-- \n");
		Assert.assertEquals(SqlTokenizer.Token.COMMENT, st.nextToken().getType() );
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("aaa 'test' test");
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.STRING, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("aaa 't''est' test");
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.STRING, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("aaa 't--''est' test");
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.STRING, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("aaa -- 't--''est' test");
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.COMMENT, st.nextToken().getType() );
		Assert.assertFalse(st.hasMoreTokens() );

		st = new SqlTokenizer("aaa -- 't--'\n'est' test");
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.COMMENT, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.STRING, st.nextToken().getType() );
//		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertEquals(SqlTokenizer.Token.COMMAND_TEXT, st.nextToken().getType() );
		Assert.assertFalse(st.hasMoreTokens() );
	}
}