package com.java.util;

/**
 * <pre>
 * 
 * <b>유니코드 변환 Class 입니다.</b>
 * koreanToUnicode(String) return String 
 * 한국어 -> 유니코드
 * 
 * unicodeToKorean(String) return String 
 * 유니코드 -> 한국어 
 * <pre>
 * 
 * @author suresoft
 * 
 */
public class Converter {
	
	/**
	 * 문자열을 저장하고 있을 변수입니다.
	 */
	private static StringBuilder converterStringBuilder = null;
	
	/**
	 * 문자열을 한글자씩 확인하고 있을 변수입니다.
	 */
	private static char currentChar;
	
	/**
	 * 문자열의 hex값입니다.
	 */
	private static String hex; // 문자의 hex 값
	
	private  Converter() { }
	

	/**
	 * 한국어를 유니코드로 변경하는 메소드입니다.
	 * @param korean
	 * @return
	 */
	public static String convertKoreanToUnicode(String korean) {
		converterStringBuilder = new StringBuilder();
		for (int i = 0; i < korean.length(); i++) {
			currentChar = korean.charAt(i);
			
			if ((currentChar >= 0x0020) && (currentChar <= 0x007e)) {// 유니코드로 변환할 필요가 있는 문자열인지 판단
				converterStringBuilder.append(currentChar); // 아닌 경우.
			} else { // 변경해야 하는 경우.
				converterStringBuilder.append("\\u");
				hex = Integer.toHexString(korean.charAt(i) & 0xFFFF); // 문자의 Hex 값

				for (int j = 0; j < 4 - hex.length(); j++)// 네 자리를 맞추기 위해 0 추가
					converterStringBuilder.append("0");

				converterStringBuilder.append(hex.toLowerCase());
			}
		}
		return converterStringBuilder.toString();
	}
	
	/**
	 * 유니코드를 한국어로 변경하는 메소드입니다.
	 * @param unicode
	 * @return
	 */
	public static String convertUnicodeToKorean(String unicode) {
		converterStringBuilder = new StringBuilder();
		int len = unicode.length();
		
		for (int i = 0; i < len; i++) {
			currentChar = unicode.charAt(i);
			if (currentChar == '\\') // 유니코드 첫시작 문자열은 \
				if (i + 1 < len) {
					if (unicode.charAt(i + 1) == 'u') { // 유니코드의 다음 시작문자열은 u
						try {
							converterStringBuilder.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
							i += 5;
						} catch (Exception exception) {
							i += 5;
						}
						continue;
					}
				}
			converterStringBuilder.append(currentChar);
		}
		return converterStringBuilder.toString();
	}
}
