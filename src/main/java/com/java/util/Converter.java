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
	
	private static StringBuilder sb = null; // 문자열을 저장하고 있는 변수
	private static char ch; // 문자열중 한개를 확인할 변수
	private static String hex; // 문자의 hex 값
	
	private  Converter() {
		iniEnv();
	}
	
	public void iniEnv(){
		
	}
	
	//한국어를 유니코드로
	public static String convertKoreanToUnicode(String str) {
		sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			
			if ((ch >= 0x0020) && (ch <= 0x007e)) {// 유니코드로 변환할 필요가 있는 문자열인지 판단
				sb.append(ch); // 아닌 경우.
			} else { // 변경해야 하는 경우.
				sb.append("\\u");
				hex = Integer.toHexString(str.charAt(i) & 0xFFFF); // 문자의 Hex 값

				for (int j = 0; j < 4 - hex.length(); j++)// 네 자리를 맞추기 위해 0 추가
					sb.append("0");

				sb.append(hex.toLowerCase());
			}
		}
		return (new String(sb));
	}
	
	//유니코드를 한국어로
	public static String convertUnicodeToKorean(String str) {
		sb = new StringBuilder();
		int len = str.length();
		
		for (int i = 0; i < len; i++) {
			ch = str.charAt(i);
			if (ch == '\\') // 유니코드 첫시작 문자열은 \
				if (i + 1 < len) {
					if (str.charAt(i + 1) == 'u') { // 유니코드의 다음 시작문자열은 u
						try {
							sb.append((char) Integer.parseInt(str.substring(i + 2, i + 6), 16));
							i += 5;
						} catch (Exception exception) {
							i += 5;
						}
						continue;
					}
				}
			sb.append(ch);
		}
		return sb.toString();
	}
}
