package com.example.common.sql;


import org.junit.Assert;
import org.junit.Test;

import com.example.common.TestCaseBase;

public class SqlStringTest extends TestCaseBase {

	// 単一引用符のエスケープ
	@Test
	public void test1() {
		SqlString sql = new SqlString("where user_name=${0}").
			format("puke 'n' puke");
		SqlString s = new SqlString("where user_name='puke ''n'' puke'");
		Assert.assertEquals(s, sql);
	}

	// 単一引用符のエスケープ 2
	@Test
	public void test2() {
		SqlString sql = new SqlString("where ${a} '${0} is text' user_name=' '${0} or user_addr=${0}").
			format("puke 'n' puke");
		SqlString s = new SqlString("where ${a} '${0} is text' user_name=' ''puke ''n'' puke' or user_addr='puke ''n'' puke'");
		Assert.assertEquals(s, sql);
	}

	// 同じ添え字を二回使う
	@Test
	public void test3() {
		SqlString sql = new SqlString("where user_name=${0} or user_name=${1} or user_addr=${0}").
			format("puke 'n' puke", "Puke 'n' Puke");
		SqlString s = new SqlString("where user_name='puke ''n'' puke' or user_name='Puke ''n'' Puke' or user_addr='puke ''n'' puke'");
		Assert.assertEquals(s, sql);
	}

	// 前方一致
	@Test
	public void test4() {
		SqlString sql = new SqlString("where user_name like ${0, like=start} or user_name=${1} or user_addr=${0}").
			format("puke 'n' %puke", "Puke 'n' Puke");
		SqlString s = new SqlString("where user_name like 'puke ''n'' \\%puke%' or user_name='Puke ''n'' Puke' or user_addr='puke ''n'' %puke'");
		Assert.assertEquals(s, sql);
	}

	// 後方一致
	@Test
	public void test5() {
		SqlString sql = new SqlString("where user_name like ${0, like=end} or user_name=${1} or user_addr=${0}").
			format("puke 'n' %puke", "Puke 'n' Puke");
		SqlString s = new SqlString("where user_name like '%puke ''n'' \\%puke' or user_name='Puke ''n'' Puke' or user_addr='puke ''n'' %puke'");
		Assert.assertEquals(s, sql);
	}

	// 中間一致
	@Test
	public void test6() {
		SqlString sql = new SqlString("where user_name like ${0, like=middle} or user_name=${1} or user_addr=${0}").
			format("puke 'n' %puke", "Puke 'n' Puke");
		SqlString s = new SqlString("where user_name like '%puke ''n'' \\%puke%' or user_name='Puke ''n'' Puke' or user_addr='puke ''n'' %puke'");
		Assert.assertEquals(s, sql);
	}

	// SqlName(スキーマ名、予約後等) 単一引用符で括られない
	@Test
	public void test7() {
		SqlString sql = new SqlString("where ${0}.user_name").
			format(new SqlName("puke") );
		SqlString s = new SqlString("where puke.user_name");
		Assert.assertEquals(s, sql);
	}
}
