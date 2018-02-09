package com.java.util;

import java.util.HashMap;

import com.java.parameter.ParameterValidation;
import com.java.project.RcpttProject;

/**
 * 프로그램에서 사용할 전역 변수를 저장하는 Class 입니다.
 * @author suresoft
 */
public class ProgramData {
	/**
	 * 싱글톤 패턴이며 ProgramData를 가지고 있을 변수  
	 */
	private static ProgramData INSTANCE;
	
	/**
	 * Project 파일 하나만 필요하므로 ProgramData에서 하나를 가지고 있습니다.
	 * set get 으로 추가 혹은 반환이 가능합니다.
	 */
	private RcpttProject project;
	
	/**
	 * testcase에서 parameter.ctx 파일을 링크하기 위해서는 
	 * parameter.ctx파일의 고유한 id 를 가지고 있어야 하며 그 아이디 값을
	 * testcase안의 contextId를 추가하여 링크하여야 합니다.
	 * 링크를 하기위해서 parameter.ctx파일의 고유 id 값을 ProgramData에서 가지고있습니다. 
	 */
	private String parameterCtxId;
	
	/**
	 * Properties파일에서 읽은 parameter들과
	 * parameter.ctx파일에서 읽은 파라미터를 총 저장하는 Map형식의 변수입니다.
	 */
	private HashMap<String, String> parameterMap;

	/**
	 * 첫 클래스가 생성되었을때
	 * parameterMap 을 새로 생성합니다.
	 */
	private ProgramData(){
		parameterMap = new HashMap<String, String>();		
	}
	
	/**
	 * ProgramData Instance를 반환합니다.
	 * @return
	 */
	public static ProgramData getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ProgramData();
		}
		return INSTANCE;
	}
	
	public RcpttProject getProject() {
		return project;
	}

	public void setProject(RcpttProject project) {
		this.project = project;
	}

	public String getParameterCtxId() {
		return parameterCtxId;
	}

	public void setParameterCtxId(String parameterCtxId) {
		this.parameterCtxId = parameterCtxId;
	}

	public HashMap<String, String> getParameterMap() {
		return parameterMap;
	}

	
	public void addParameter(String key, String value){
		key = ParameterValidation.getValidationKey(key);
		value = Converter.convertUnicodeToKorean(ParameterValidation.getValidationValue(value));
		
		if(valueChecker(value)){
			parameterMap.put(value , key);
		}
	}
	
	public void addParameter(String key , String value , String path){
		key = ParameterValidation.getValidationPath(path)+"_"+ParameterValidation.getValidationKey(key);
		value = Converter.convertUnicodeToKorean(ParameterValidation.getValidationValue(value));
		
		if(valueChecker(value)){
			parameterMap.put(value , key);
		}
		
	}
	
	public String searchKey(String value){
		//parameterMap에서는 한글로 가지고 있습니다.
		return parameterMap.get(Converter.convertUnicodeToKorean(value));
	}
	
	private boolean valueChecker(String str){
		return (!str.equals("") && !str.equals("\\"));
	}


	
	
	
}	
