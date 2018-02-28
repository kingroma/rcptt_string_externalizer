package com.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <pre>
 * 
 *  유저에게서 받아온 변수를 저장할 Class 입니다.
 * 
 *  #RCPTT 자동화 기본 경로 설정 파일 
 *	
 *	language= (예 = ko)
 *	CodeScrollFilePath= (예 = C:\Program Files (x86)\CodeScroll Controller Tester 3.0)
 *	ProjectPath= (예 = C:\Users\suresoft\rcpttWorkspace\RCPTT_individual_test)
 *	ParameterCtxPath=(예 = C:\Users\suresoft\rcpttWorkspace\RCPTT_individual_test\RCPTT_QA\p.ctx)
 *	
 *	#language=ko,ko_KR,en_US  언어 선택
 *	#CodeScrollFilePath=CS Path 코드스크롤 경로
 *	#ProjectPath=ecl 스크립트가 있는 파일 경로
 *	#ParameterCtxPath=파라미터가 들어있는 혹은 빈 ctx 파일 경로
 * </pre>
 * 
 * @author suresoft
 */
public class UserInputData {	
	/**
	 * UserInfo 변수를 가지고 있을 INSTANCE 입니다.
	 */
	private static UserInputData INSTANCE;
	
	/**
	 * rcptt++.ini 파일의 이름을 UserData에서 가지고 있습니다.
	 */
	public static String iniFileName = "rcptt++.ini";
	
	/**
	 * rcptt++.ini 파일에서 language 내용을 담고있을 String 형 변수입니다.
	 */
	public static String Language;
	
	/**
	 * rcptt++.ini  파일에서 parameter ctx path 내용을 담고 있을 String 형 변수입니다
	 */
	public static String ParameterCtxPath;
	
	/**
	 * rcptt++.ini  파일에서 parameter en ctx path 내용을 담고 있을 String 형 변수입니다
	 */
	public static String ParameterCtxEnPath;
	
	/**
	 * rcptt++.ini  파일에서 CT 제품의 path 내용을 담고 있을 String 형 변수입니다
	 */
	public static String CTPath;
	
	/**
	 * rcptt++.ini  파일에서 RCPTT project path 내용을 담고 있을 String 형 변수입니다
	 */
	public static String ProjectPath;
	
	/**
	 * rcptt++.ini에서 읽어온 정보들을 map형식으로 가지고 있습니다.
	 * get 으로 반환 가능합니다.
	 */
	private Map<String, String> iniInfoMap;
	
	/**
	 * UserInfo 생성과 동시에 infoEnv() 메소드 호출합니다.
	 */
	private UserInputData(boolean bool) {
		if(!bool){
			initEnv();
		}
	}
	
	/**
	 * Instance를 반환하며 
	 * Instance == null 일경우 새로 생성 
	 * @return
	 */
	public static UserInputData getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UserInputData(false);
		}

		return INSTANCE;
	}
	
	public static UserInputData getInstanceNoIniEnv(){
		if (INSTANCE == null) {
			INSTANCE = new UserInputData(true);
		}

		return INSTANCE;
	}

	/**
	 * UserInfo 생성이 되었을때 ini 파일을 읽기 위하여 
	 * 메소드가 호출됩니다.
	 * 기본 default 이름은 rcptt++.ini 파일입니다.
	 * ini 파일을 읽어 iniInfoMap에 properties 파일의 내용을 추가합니다.
	 * 추후 다른 클래스에서 경로들을 확인해야할 때가 있기 때문에 메소드를 추가하였습니다.
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
				ErrorMessage.getInstance().printErrorMessage("UserInfoData.read.properties.error");
			}finally{
				try {
					if(is!=null){
						is.close();
					}
					p = null;
				} catch (IOException exception2) {
					ErrorMessage.getInstance().printErrorMessage("UserInfoData.read.properties.error");
				}
			}
		}
		
		Language = searchIniInfo("Language");
		ProjectPath = searchIniInfo("ProjectPath");
		ParameterCtxPath = searchIniInfo("ParameterCtxPath");
		ParameterCtxEnPath = searchIniInfo("ParameterCtxEnPath");
		CTPath = searchIniInfo("CodeScrollFilePath");
	}
	
	/**
	 * get set
	 */
	private String searchIniInfo(String key){
		return iniInfoMap.get(key);
	}

}
