package com.java.util;

import java.util.HashMap;

import com.java.parameter.ParameterValidation;
import com.java.project.RcpttProject;

/**
 * 프로그램에서 사용할 전역 변수를 저장하는 Class 입니다.
 * @author suresoft
 *
 */
//public static ArrayList<Parameter> parameters = new ArrayList<Parameter>();
public class ProgramData {
	// program 에서 전역변수로 사용될 변수를 저장
	
	private static ProgramData INSTANCE;
	
	private RcpttProject project;
	private String parameterCtxId;
	private HashMap<String, String> parameterMap;
	
	private String extensionListFileName = "extensionList.properties";
	private String defaultParameterFilename = "defaultParameter.properties";
	
	private ProgramData(){
		parameterMap = new HashMap<String, String>();
		
	}
	
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

	public String getExtensionListFileName() {
		return extensionListFileName;
	}

	public void setExtensionListFileName(String extensionListFileName) {
		this.extensionListFileName = extensionListFileName;
	}

	public String getDefaultParameterFilename() {
		return defaultParameterFilename;
	}

	public void setDefaultParameterFilename(String defaultParameterFilename) {
		this.defaultParameterFilename = defaultParameterFilename;
	}

	
	
	
}	
