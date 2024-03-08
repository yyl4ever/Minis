package com.minis.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @param <T>
 */
public interface ResultSetExtractor<T> {

	// 将 resultSet 数据集映射为一个集合对象
	T extractData(ResultSet rs) throws SQLException;

}

