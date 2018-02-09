package com.java.util;




/**
 * Print 혹은 Log를 한곳에서 남기기
 * 
 * @author suresoft
 */
public class Logger {
	
	private Logger() {
	}
	
	public static void write(String text) {
		System.out.println(text);
		System.out.flush();
		//만약에 output으로 내놓을꺼면 주석 해제
		
//		try {
//			FileOutputStream fos = new FileOutputStream(new File(path),true);	
//			fos.write((time.current()+text+"\n").getBytes());
//			fos.close();
//		}catch(Exception exception) {
//		}
		
	}
	
}
