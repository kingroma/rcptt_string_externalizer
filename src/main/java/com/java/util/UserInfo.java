package com.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * <pre>
 * 
 *  유저에게서 받아온 변수를 저장할 Class 입니다.
 * 
 *  #RCPTT 자동화 기본 경로 설정 파일 
 *	
 *	
 *	language=ko
 *	CodeScrollFilePath=C:\Program Files (x86)\CodeScroll Controller Tester 3.0
 *	ProjectPath=C:\Users\suresoft\rcpttWorkspace\RCPTT_individual_test
 *	ParameterCtxPath=C:\Users\suresoft\rcpttWorkspace\RCPTT_individual_test\RCPTT_QA\p.ctx
 *	
 *	
 *	#language=ko,ko_KR,en_US  언어 선택
 *	#CodeScrollFilePath=CS Path 코드스크롤 경로
 *	#ProjectPath=ecl 스크립트가 있는 파일 경로
 *	#ParameterCtxPath=파라미터가 들어있는 혹은 빈 ctx 파일 경로
 * </pre>
 * 
 * @author suresoft
 *
 */
public class UserInfo {
	private static UserInfo INSTANCE;
	
	private String iniFileName = "rcptt++.ini";
	private HashMap<String, String> iniInfoMap;
	
	private UserInfo() {
		initEnv();
	}

	public static UserInfo getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UserInfo();
		}

		return INSTANCE;
	}


	/**
	 * 
	 */
	private void initEnv() {
		iniInfoMap = new HashMap<String,String>();
		
		Properties p = null;
		InputStream is = null;
		
		File iniFile = new File(iniFileName);
		if(iniFile.exists()){ // ini 파일 
			try {
				p = new Properties();
				is = new FileInputStream(iniFile);
				p.load(is);
				for(Object obj : p.keySet()){
					String key = (String)obj;
					iniInfoMap.put(key, p.getProperty(key));
				}
			} catch (IOException exception) {
				Logger.write("UserInfo read properties Error");
			}finally{
				try {
					if(is!=null)
						is.close();
					p = null;
				} catch (IOException exception2) {
					Logger.write("UserInfo read properties Error");
				}
			}
		}
		
	}
	
	private String getiniInfo(String key){
		return iniInfoMap.get(key);
	}

	public String getLanguage() {
		return getiniInfo("Language");
	}

	public String getCTPath() {
		return getiniInfo("CodeScrollFilePath");
	}

	public String getProjectPath() {
		return getiniInfo("ProjectPath");
	}

	public String getParameterCtxPath() {
		return getiniInfo("ParameterCtxPath");
	}

	public String getIniFileName() {
		return iniFileName;
	}

	
}
