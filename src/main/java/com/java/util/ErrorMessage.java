package com.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Error 메시지 출력 입니다.
 * errorMessage.properties를 읽어와 내용을 출력합니다.
 * @author suresoft
 *
 */
public class ErrorMessage {
	
	/**
	 * properties 에서 내용을 가지고와 
	 * 아래의 Map에 저장하고 있습니다.
	 */
	private Map<String,String> errorMessageMap;
	
	/**
	 * 싱글톤 패턴의 INSTANCE입니다.
	 */
	private static ErrorMessage INSTANCE;
	
	/**
	 * messageProperties의 경로
	 */
	private String path = "src/main/java/com/java/resource/errorMessage.properties";
	
	/**
	 * 생성자 숨김과 동시에 initEnv() 메소드를 불러옵니다 
	 */
	private ErrorMessage(){
		initEnv();
	}
	
	/**
	 * Properties 파일을 읽어오와
	 * Map 에 저장합니다.
	 */
	private void initEnv(){
		Properties properties = null;
		InputStream is = null;
		
		try {
			properties = new Properties();
			is = new FileInputStream(new File(path));
			errorMessageMap = new HashMap<String,String>();
			
			properties.load(is);
			
			for(Object obj : properties.keySet()){
				String key = (String)obj;
				errorMessageMap.put(key , properties.getProperty(key));
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
		}finally{
			try {
				if(is!=null){
					is.close();
				}	
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}
		}
		
	}
	
	/**
	 * ErrorMessage 형 Instance를 반환합니다.
	 * @return
	 */
	public static ErrorMessage getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ErrorMessage();
		}
		return INSTANCE;
	}
	
	/**
	 * Map에서 key를 찾아 
	 * Error 메시지를 출력합니다.
	 * @param key
	 */
	public void printErrorMessage(String key){
		Logger.write("[[ ERROR MESSAGE ]] ");
		Logger.write(errorMessageMap.get(key));
	}
	
	
}
