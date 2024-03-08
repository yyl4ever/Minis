package com.minis.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Java泛型的嵌套使用
 * 在ResultSetExtractor接口中定义的<T>表示的是对结果集每一行数据映射后生成的对象类型。例如，如果你的数据库查询结果是一些用户信息，那么T可能就是User类型。
 *
 * 而RowMapperResultSetExtractor在实现ResultSetExtractor时，将<T>替换为<List<T>>，意味着它处理的结果不是一个单独的对象T，而是一个对象列表List<T>。这意味着这个类的作用是将整个结果集的所有行都映射成对应的T类型对象，并将这些对象收集到一个List中返回。
 *
 * @param <T>
 */
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {
	private final RowMapper<T> rowMapper;

	/**
	 *
	 * @param rowMapper 由用户自己去实现，自定义
	 */
	public RowMapperResultSetExtractor(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	@Override
	public List<T> extractData(ResultSet rs) throws SQLException {
		List<T> results = new ArrayList<>();
		int rowNum = 0;
		while (rs.next()) {
			results.add(this.rowMapper.mapRow(rs, rowNum++));
		}
		return results;
	}

}
