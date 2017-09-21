package kr.pe.sdh.core.util;

import java.util.Random;

/**
 * 임의의 문자열 생성
 * @author 성동훈
 * @since 2017-01-05
 * @version 1.0
 * @see
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017.01.05  성동훈  최초 생성
 *
 * </pre>
 */
public class RandomString {
	
	private static final char[] chars;
	static {
		StringBuilder buffer = new StringBuilder();
		for (char ch = '0'; ch <= '9' ; ++ch) buffer.append(ch);
		for (char ch = 'a'; ch <= 'z' ; ++ch) buffer.append(ch);
		for (char ch = 'A'; ch <= 'Z' ; ++ch) buffer.append(ch);
		chars = buffer.toString().toCharArray();
	}
	private static final char[] chars2;
	static {
		StringBuilder buffer = new StringBuilder();
		for (char ch = '0'; ch <= '9' ; ++ch) buffer.append(ch);
		for (char ch = 'a'; ch <= 'z' ; ++ch) buffer.append(ch);
		for (char ch = 'A'; ch <= 'Z' ; ++ch) buffer.append(ch);
		buffer.append("!@#$%^*-_=+");
		chars2 = buffer.toString().toCharArray();
	}
	/**
	 * 입력한 자릿수만큼의 임의의 문자열을 리턴함. 
	 * 영문소문자, 영문대문자, 숫자조합
	 * @param length
	 * @return
	 */
	public static String random(int length){
		if (length < 1)
			throw new IllegalArgumentException("length < 1: " + length);

		StringBuilder randomString = new StringBuilder();
		Random random = new Random();

		for (int i=0; i < length; i++){
			randomString.append(chars[random.nextInt(chars.length)]);
		}
		return randomString.toString();
	}
}
