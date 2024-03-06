package com.minis.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DefaultObjectMapper implements ObjectMapper{
	String dateFormat = "yyyy-MM-dd";
	DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
	/**
	 * "#" 是一个数字占位符，可以被任何数字替换。如果缺少数字，则不会显示任何字符。
	 * "," 用于分组分隔符，将整数部分的数字分成组（根据地区可能为千位、百万位等）。
	 * "0" 是另一种数字占位符，但如果缺少数字，会显示一个零代替。
	 * "." 是小数点符号，此点后的所有内容被认为是小数部分。
	 * "00" 表示小数部分应有两位数字。如果实际数字的小数位少于两位，将会添加尾随零。
	 * 例如，如果你调用 decimalFormatter.format(1234567.89)，它将返回 "1,234,567.89"。而如果你调用 decimalFormatter.format(1234.5)，则会返回 "1,234.50"。
	 */
	String decimalFormat = "#,##0.00";
	DecimalFormat decimalFormatter = new DecimalFormat(decimalFormat);

	public DefaultObjectMapper() {
	}
	
	@Override
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		this.datetimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
	}

	@Override
	public void setDecimalFormat(String decimalFormat) {
		this.decimalFormat = decimalFormat;
		this.decimalFormatter = new DecimalFormat(decimalFormat);
	}

	@Override
	public String writeValuesAsString(Object obj) {
		String sJsonStr = "{";
		
		Class<?> clz = obj.getClass();
		
		Field[] fields = clz.getDeclaredFields();
		for (Field field : fields) {
			String sField = "";
			Object value = null;
			Class<?> type = null;
			// 对象对应字段的名称
			String name = field.getName();
			String strValue = "";
			try {
				field.setAccessible(true);
				// 反射获取对象对应字段的值
				value = field.get(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			type = field.getType();
			// 可以扩展更多的数据类型
			if (value instanceof Date) {
				LocalDate localDate = ((Date)value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				strValue = localDate.format(this.datetimeFormatter);
			}
			else if (value instanceof BigDecimal || value instanceof Double || value instanceof Float){
				strValue = this.decimalFormatter.format(value);
			}
			else {
				strValue = value.toString();
			}
			// 拼接 json
			if (sJsonStr.equals("{")) {
				sField = "\"" + name + "\":\"" + strValue + "\"";
			}
			else {
				sField = ",\"" + name + "\":\"" + strValue + "\"";	
			}
			
			sJsonStr += sField;
		}
		
		sJsonStr += "}";
		
		return sJsonStr;
	}

}
