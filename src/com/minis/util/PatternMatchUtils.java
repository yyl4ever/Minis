package com.minis.util;

public abstract class PatternMatchUtils {

	/**
	 * 用给定的模式匹配字符串，* 代表若干字符
	 * Match a String against the given pattern, supporting the following simple
	 * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy" matches (with an
	 * arbitrary number of pattern parts), as well as direct equality.
	 * @param pattern the pattern to match against
	 * @param str the String to match
	 * @return whether the String matches the given pattern
	 */
	public static boolean simpleMatch( String pattern,  String str) {
		if (pattern == null || str == null) {
			return false;
		}

		// 是否包含 *
		int firstIndex = pattern.indexOf('*');
		if (firstIndex == -1) {
			return pattern.equals(str);
		}

		// 首字符为 *，即 *XX 格式
		if (firstIndex == 0) {
			// *，匹配全部
			if (pattern.length() == 1) {
				return true;
			}
			// 查找下一个
			int nextIndex = pattern.indexOf('*', 1);
			if (nextIndex == -1) {// 没有下一个*，说明后续不必匹配了，直接 endsWith 判断
				return str.endsWith(pattern.substring(1));
			}
			// 截取两个 * 之间的部分
			String part = pattern.substring(1, nextIndex);
			if (part.isEmpty()) {
				// **，移到后面的模式匹配
				return simpleMatch(pattern.substring(nextIndex), str);
			}
			// 查找子串
			int partIndex = str.indexOf(part);
			while (partIndex != -1) {
				if (simpleMatch(pattern.substring(nextIndex), str.substring(partIndex + part.length()))) {
					return true;
				}
				partIndex = str.indexOf(part, partIndex + 1);
			}
			return false;
		}

		// 对不是 * 开头的模式，前面精确匹配，后面的子串重新递归匹配
		return (str.length() >= firstIndex &&
				pattern.substring(0, firstIndex).equals(str.substring(0, firstIndex)) &&
				simpleMatch(pattern.substring(firstIndex), str.substring(firstIndex)));
	}

	/**
	 * Match a String against the given patterns, supporting the following simple
	 * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy" matches (with an
	 * arbitrary number of pattern parts), as well as direct equality.
	 * @param patterns the patterns to match against
	 * @param str the String to match
	 * @return whether the String matches any of the given patterns
	 */
	public static boolean simpleMatch( String[] patterns, String str) {
		if (patterns != null) {
			for (String pattern : patterns) {
				if (simpleMatch(pattern, str)) {
					return true;
				}
			}
		}
		return false;
	}

}

