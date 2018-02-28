package com.java.util;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 내용을 출력하는 클래스입니다. 
 * Print 혹은 Log를 한곳에서 남기기
 * @author suresoft
 */
public class Logger {
	private static StringBuilder collisionMessage = new StringBuilder();
	private Logger() {
	}
	
	/**
	 * 내용을 출력하거나
	 * log 파일에 담습니다.
	 * @param text
	 */
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
	
	/**
	 * ProgramData 에서 사용됩니다.
	 * 파라미터가 추가될때 중복 코드가 어디서
	 * 발생하는지 ParameterCollsionMsg.txt 파일에 로그를 남깁니다.
	 */
	public static void collisionMsg(String key , String otherKey , String value){
		//Logger.collisionMsg(key+" <---> "+otherKey+" text : "+value);
		//if(!otherKey.contains("_Original") || !key.contains("_Original")){
		if(!key.equals(otherKey) && !otherKey.contains("_Original") && !key.contains("_Original")){
			collisionMessage.append("collision : ");
			collisionMessage.append(key);
			collisionMessage.append(" <-----> ");
			collisionMessage.append(otherKey);
			collisionMessage.append(" / value : ");
			collisionMessage.append(value);
			collisionMessage.append("\n");
		}
	}
	
	/**
	 * 모아둔 중복체크 관련 내용을 
	 * Controller Class 에서 마지막으로 save 합니다.
	 */
	public static void collisionMsgSave(){
		String path = "parameterCollisionMsg.txt";
		try {
			FileOutputStream fos = new FileOutputStream(new File(path));
			fos.write(collisionMessage.toString().getBytes());
			fos.close();
		}catch(Exception exception) {
			
		}
	}
}
