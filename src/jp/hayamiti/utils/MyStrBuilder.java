package jp.hayamiti.utils;

final public class MyStrBuilder {
	/**
	 * 引き数の値を足し合わせて一つの文字列に変換する
	 * @param <T>
	 * @param len
	 * @param items
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final public static <T> String build(int len, T ...items) {
		StringBuilder sb = new StringBuilder(len);
		for(T item: items) {
			sb.append(item);
		}
		return sb.toString();
	}
}
