package com.minis.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
	// 将 ResultSet 的一行数据映射为一个对象
	T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
