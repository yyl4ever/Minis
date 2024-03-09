package com.minis.batis;

import javax.sql.DataSource;

import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.PreparedStatementCallback;

/**
 * SqlSession 是对 jdbcTemplate 的包装
 */
public class DefaultSqlSession implements SqlSession{
	/**
	 * TODO 拓展：setDatasource()允许动态设置数据源。datasource变成两个，一个是readDatasource，一个是writeDatasource，实现读写分离
	 * private DataSource readDataSource;
	 * private DataSource writeDataSource;
	 */
//	private DataSource dataSource;
//	
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}
//
//	public DataSource getDataSource() {
//		return this.dataSource;
//	}

	/**
	 * TODO 拓展 读写分离的设计：每一次用SqlSession执行SQL语句的时候，都判断一下SQL类型，如果是read，则设置readDatasource，否则设置writeDatasource
	 */
// 	public Object selectOne(String sqlid, Object[] args, PreparedStatementCallback pstmtcallback) {
// 		int sqltype = this.sqlSessionFactory.getMapperNode(sqlid).getSqlType();
// 		if (sqltype==0)  {//read
// 			jdbcTemplate.setDatasource(readDataSource);
// 		}
// 		return jdbcTemplate.query(sql, args, pstmtcallback);
// 	}

	JdbcTemplate jdbcTemplate;
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	SqlSessionFactory sqlSessionFactory;
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}
	
	@Override
	public Object selectOne(String sqlid, Object[] args, PreparedStatementCallback pstmtcallback) {
		System.out.println(sqlid);
		String sql = this.sqlSessionFactory.getMapperNode(sqlid).getSql();
		System.out.println(sql);
		
		return jdbcTemplate.query(sql, args, pstmtcallback);
	}
	
	private void buildParameter(){
	}
	
	private Object resultSet2Obj() {
		return null;
	}

}
