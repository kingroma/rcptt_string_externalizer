package com.java.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 하드코딩이 들어가있습니다.
 * @author suresoft
 *
 */
public class __CreateProgramIniOrPropertiesFile {

	private static __CreateProgramIniOrPropertiesFile INSTANCE;
	private StringBuilder iniHelpText;
	private StringBuilder defaultParameterHelpText;
	private StringBuilder extensionListHelpText;
	private ArrayList<String> nameList; 
	
	
	public static __CreateProgramIniOrPropertiesFile getInstance(){
		if(INSTANCE == null){
			INSTANCE = new __CreateProgramIniOrPropertiesFile();
		}
		return INSTANCE;
	}
	
	private __CreateProgramIniOrPropertiesFile(){
		initEnv();
	}
	
	private void initEnv(){
		settingStringBuilder();
		
		nameList = new ArrayList<String>();
		
		nameList.add(UserInfo.getInstance().getIniFileName());
		nameList.add(ProgramData.getInstance().getDefaultParameterFilename());
		nameList.add(ProgramData.getInstance().getExtensionListFileName());
	}
	
	public boolean createFile(){
		int ret = 0;
		for(String path : nameList){
			File file = new File(path);
			FileOutputStream fos = null;
			if(!file.exists()){
				Logger.write("\n 경로 : [[ "+path+" ]] 가 존재하지 않아 자동 생성 되었습니다.");
				ret++;
				try {
					fos = new FileOutputStream(file);
					
					if(path.equals(UserInfo.getInstance().getIniFileName())){
						fos.write(iniHelpText.toString().getBytes());
					}else if(path.equals(ProgramData.getInstance().getDefaultParameterFilename())){
						fos.write(defaultParameterHelpText.toString().getBytes());
					}else if(path.equals(ProgramData.getInstance().getExtensionListFileName())){
						fos.write(extensionListHelpText.toString().getBytes());
					}
					
				} catch (IOException exception) {
					Logger.write("create program ini file and properties file error");
				}finally{
					try {
						if(fos!=null){
							fos.close();
						}
					} catch (IOException exception2) {
						Logger.write("create program ini file and properties file error");
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
	
//	defaultParameterHelpText
//	.extensionListHelpText
	private void settingStringBuilder(){
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
		defaultParameterHelpText.append("#-- RCPTT++ properties에서 확인이 어려운 파라미터 추가 --\n");
		defaultParameterHelpText.append("#key=vale 형식으로 properties 파일과 똑같이 작성해주시면 됩니다.\n");
		defaultParameterHelpText.append("#kim=jeoning");
		
		extensionListHelpText = new StringBuilder();
		extensionListHelpText.append("#-- RCPTT++ 추가 치환시 파일 확장자명을 기준으로 파라미터를 추가 --\n");
		extensionListHelpText.append("#key=vale 형식으로 properties 파일과 똑같이 작성해주시면 됩니다.\n");
		extensionListHelpText.append("#c=.c\n");
		
		
	}
}

