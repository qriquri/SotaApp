package jp.hayamiti.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MyStrBuilderTest {

	@Nested
	class 引き数を足し合わせて一つの文字列にする{
		@Test
		void _xとuをたすとxuになる() {
			// 実行
			String result = MyStrBuilder.build(2, "x", "u");
//			System.out.println(result);
			// 検証 前が期待値
			assertEquals("xu", result);

		}

		@Test
		void _長さを間違えて指定してもaとuをたすとauになる() {
			// 実行
			String result = MyStrBuilder.build(1, "a", "u");
//			System.out.println(result);
			// 検証 前が期待値
			assertEquals("au", result);
		}

		@Test
		void _1とuをたすと1uになる() {
			// 実行
			String result = MyStrBuilder.build(2, 1, "u");
//			System.out.println(result);
			// 検証 前が期待値
			assertEquals("1u", result);
		}

		@Test
		void _1とaとuをたすと1uになる() {
			// 実行
			String result = MyStrBuilder.build(2, 1, "a", "u");
//			System.out.println(result);
			// 検証 前が期待値
			assertEquals("1au", result);
		}
	}
}
