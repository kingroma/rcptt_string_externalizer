package com.java.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 하드코딩이 들어가있습니다.
 * 파일이 없을경우 help 내용을 추가하여 
 * 파일을 생성하는 클래스입니다.
 * 
 * 생성해야할 파일은
 *  1. defaultparameter.properties 
 *   >>  properties에서 확인되지 않은 key값을 사용자가 추가
 *   
 *  2. extensionList.properties 
 *   >> *.확장자 를 담고있는 파일이며 치환시 extensionList에 있는 key와 RCPTT의 문자열이 동일할경우 치환을 합니다.
 *  
 *  3. rcptt++.ini
 *   >> 기본적인 경로를 담을 파일입니다 ( RCPTT project 경로 , parameterCtx 파일 경로 , CT 파일 경로  )
 *   
 * @author suresoft
 *
 */
public class CheckProgramIniAndPropertiesFile {

	/**
	 * ini 파일에 들어갈 기본 핼프 내용입니다.
	 * settingStringBuilder() 호출을 통하여 생성됩니다.
	 */
	private static StringBuilder iniHelpText;
	
	/**
	 * defaultParameter 파일에 들어갈 기본 헬프 내용입니다. 
	 */
	private static StringBuilder extensionListHelpText;
	private static StringBuilder defaultParameterHelpText;
	private static StringBuilder defaultParameterEnHelpText;
	private static StringBuilder forceChangeParameterEnHelpText;
	
	private static ArrayList<String> nameList;	
	private static String extensionListFileName = "extensionList.properties";
	private static String defaultParameterFilename = "defaultParameter.properties";
	private static String defaultParameterEnFilename = "defaultParameterEn.properties";
	private static String foceChangeParameterEn = "forceChangeParameterEn.properties";
	
	/**
	 * 생성자 private
	 */
	private CheckProgramIniAndPropertiesFile(){
	}
	
	/**
	 * extensionList 파일 과
	 * defaultParameter 파일 과 
	 * ini 파일이 존재하는지 확인하는 과정입니다.
	 *  
	 * @return
	 */
	public static boolean createFile(){
		settingStringBuilder();
		
		nameList = new ArrayList<String>();
		
		nameList.add(UserInputData.iniFileName);
		nameList.add(defaultParameterFilename);
		nameList.add(extensionListFileName);
		nameList.add(defaultParameterEnFilename);
		nameList.add(foceChangeParameterEn);
		
		int ret = 0;
		for(String path : nameList){
			File file = new File(path);
			FileOutputStream fos = null;
			if(!file.exists()){
				Logger.write("\n\n [ERROR] [[ "+path+" ]] 가 존재하지 않아 자동 생성 되었습니다.\n\n");
				ret++;
				try {
					fos = new FileOutputStream(file);
					
					if(path.equals(UserInputData.iniFileName)){
						ErrorMessage.getInstance().printErrorMessage(iniHelpText.toString());
						fos.write(iniHelpText.toString().getBytes());
					}else if(path.equals(defaultParameterFilename)){
						ErrorMessage.getInstance().printErrorMessage(defaultParameterHelpText.toString());
						fos.write(Converter.convertKoreanToUnicode(defaultParameterHelpText.toString()).getBytes());
					}else if(path.equals(extensionListFileName)){
						ErrorMessage.getInstance().printErrorMessage(extensionListHelpText.toString());
						fos.write(Converter.convertKoreanToUnicode(extensionListHelpText.toString()).getBytes());
					}else if(path.equals(defaultParameterEnFilename)){
						ErrorMessage.getInstance().printErrorMessage(defaultParameterEnHelpText.toString());
						fos.write(Converter.convertKoreanToUnicode(defaultParameterEnHelpText.toString()).getBytes());
					}else if(path.equals(foceChangeParameterEn)){
						ErrorMessage.getInstance().printErrorMessage(forceChangeParameterEnHelpText.toString());
						fos.write(Converter.convertKoreanToUnicode(forceChangeParameterEnHelpText.toString()).getBytes());
					}
					
				} catch (IOException exception) {
					ErrorMessage.getInstance().printErrorMessage("CheckProgramIniAndPropertiesFile.IO.error");
				}finally{
					try {
						if(fos!=null){
							fos.close();
						}
					} catch (IOException exception2) {
						ErrorMessage.getInstance().printErrorMessage("CheckProgramIniAndPropertiesFile.IO.error");
					}
				}
			}
		}
		if(ret==0){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * -help 를 입력시 텍스트 내용을 새팅합니다.
	 * 1. ini 파일 
	 * 2. defaultParameter.properties 파일
	 * 3. extensionList.properties 파일 
	 * 이 3가지의 설명을 담고있습니다. 
	 */
	private static void settingStringBuilder(){
		iniHelpText = new StringBuilder();
		
		iniHelpText.append("\n");
		iniHelpText.append("#-- RCPTT 자동화 기본 경로 설정 파일 --\n");
		iniHelpText.append("\n");
		iniHelpText.append("#language=ko, ko_KR, en_US  언어 선택\n");
		iniHelpText.append("language=ko\n");
		iniHelpText.append("\n");
		iniHelpText.append("#CodeScrollFilePath=CS Path 코드스크롤 경로\n");
		iniHelpText.append("CodeScrollFilePath=");
		iniHelpText.append("\n");
		iniHelpText.append("#ProjectPath=ecl 스크립트가 있는 파일 경로\n");
		iniHelpText.append("ProjectPath=\n");
		iniHelpText.append("\n");
		iniHelpText.append("#ParameterCtxPath=파라미터가 들어있는 혹은 빈 ctx 파일 경로\n");
		iniHelpText.append("ParameterCtxPath=\n");
		iniHelpText.append("\n");
		
		defaultParameterHelpText = new StringBuilder();
		defaultParameterHelpText.append("# -- RCPTT++ properties에서 확인이 어려운 파라미터 추가 --\n");
		defaultParameterHelpText.append("# key=vale 형식으로 properties 파일과 똑같이 작성해주시면 됩니다.\n");
		defaultParameterHelpText.append("# kim=jeong jin");
		
		extensionListHelpText = new StringBuilder();
		extensionListHelpText.append("# -- RCPTT++ 추가 치환시 파일 확장자명을 기준으로 파라미터를 추가 --\n");
		extensionListHelpText.append("# key=vale 형식으로 properties 파일과 똑같이 작성해주시면 됩니다.\n");
		extensionListHelpText.append("# c=.c\n");
		
		defaultParameterEnHelpText = new StringBuilder();
		defaultParameterEnHelpText.append("#-- RCPTT++ 추가 치환시 파일 확장자명 설정\n");
		defaultParameterEnHelpText.append("#파일 이름 : extensionList.properties\n");
		defaultParameterEnHelpText.append("#(만약 파일이 없을경우 프로그램 폴더에 자동 새로 생성됩니다.)\n");
		defaultParameterEnHelpText.append("#key=value 형식으로 properties 파일과 똑같이 작성해주시면 됩니다.\n");
		defaultParameterEnHelpText.append("#예 ) source_c=.c\n");

		forceChangeParameterEnHelpText = new StringBuilder();
		forceChangeParameterEnHelpText.append("#-- RCPTT++ 영문 치환시 사용자가 key value 값을 변경하는 설정\n");
		forceChangeParameterEnHelpText.append("#파일 이름 : forceChangeParameterEn.properties\n");
		forceChangeParameterEnHelpText.append("#(만약 파일이 없을경우 프로그램 폴더에 자동 새로 생성됩니다.)\n");
		forceChangeParameterEnHelpText.append("#key=value 형식으로 properties 파일과 똑같이 작성해주시면 됩니다.\n");
		forceChangeParameterEnHelpText.append("#예 ) key=value\n");
	}
}

