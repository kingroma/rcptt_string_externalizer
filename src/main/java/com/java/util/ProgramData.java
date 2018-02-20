package com.java.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
	 * 치환시 testcase들에서 ProgramData안의 getParaterCtxId 를 불러 아이디를 링크합니다. 
	 */
	private String parameterCtxId;
	
	/**
	 * testcase에서 parameterEn.ctx 파일을 링크하기 위해서는
	 * parameterEn.ctx파일의 고유한 id를 가지고 있어야 하며 그 아이디 값을
	 * testcase안의 contextId를 추가하여 링크하여야 합니다.
	 * 링크를 하기 위해서 parameterEn.ctx 파일의 고유 id 값을 ProgramData에서 가지고있습니다.
	 * 치환시 testcase들에서 ProgramData안의 getParaterEnCtxId 를 불러 아이디를 링크합니다.
	 */
	private String parameterEnCtxId;
	
	/**
	 * Properties파일에서 읽은 한글 parameter들과
	 * parameter.ctx파일에서 읽은 파라미터를 총 저장하는 Map형식의 변수입니다.
	 */
	private Map<String, String> parameterMap;
	
	/**
	 * Properties파일에서 읽은 영문 혹은 default parameter들과
	 * parameterEn.ctx파일에서 읽은 파라미터를 총 저장하는 Map형식의 변수입니다.  
	 */
	private Map<String,String> parameterMapEn;
	
	/**
	 * {0} , {1} , {2} 를 확인하기 위한
	 * 정규식 포현입니다
	 * {0} 김정진1 {1} 김정진2 {2}김정진3{3}
	 * 결과 : 
	 * "" , " 김정진1 " , " 김정진2 " , "김정진3" 
	 */	
	private final String COTAINS_RANDOM_VALUE_REGEX = ".*\\{[0-9]\\}.*";
	
	/**
	 * 특수문자를 확인하는 정규식 표현입니다.
	 */
	private final String SPECIAL_CHAR_REGEX = ".*[!@#$%^&*()_-`~+/,.?><].*";
	
	/**
	 * 첫 클래스가 생성되었을때
	 * parameterMap 을 새로 생성합니다.
	 */
	private ProgramData(){
		parameterMap = new HashMap<String, String>();		
		parameterMapEn = new HashMap<String,String>();
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
	
	/**
	 * parameter map에 파라미터를 추가하는 전역 메소드입니다.
	 * key 와 value의 유효성 검사 후 추가를 합니다.
	 * @param key
	 * @param value
	 */
	public void addParameter(String key,String value , boolean isKorean){
		key = ParameterValidation.getValidationKey(key);
		value = Converter.convertUnicodeToKorean(ParameterValidation.getValidationValue(value));
		
		if(valueCheck(value)){
			if(isKorean){
				parameterMap.put(value , key);
			}else{
				parameterMapEn.put(key, value);
			}
		}
		if(isKorean){
			checkSpecialCharAndPutParameterIntoMap(key,value,true);
		}else{
			checkSpecialCharAndPutParameterIntoMap(key,value,false);
		}
	}
	
	/**
	 * parameter map 에 파라미터를 추가하는 
	 * 전역 메소드입니다.
	 * validation을 거친 후 
	 * {0} , {1} , {2} 를 확인하기 위한 메소드를 거친 후 
	 * 추가됩니다.
	 * @param key
	 * @param value
	 * @param path
	 * @param isKorean
	 */
	public void addParameter(String key , String value , String path, boolean isKorean){
		key = ParameterValidation.getValidationPath(path)+"_"+ParameterValidation.getValidationKey(key);
		String valueOriginal = Converter.convertUnicodeToKorean(ParameterValidation.getValidationValueOriginal(value));
		value = Converter.convertUnicodeToKorean(ParameterValidation.getValidationValue(value));
		
		if(value.matches(COTAINS_RANDOM_VALUE_REGEX)){
			ArrayList<String> splitList = ParameterValidation.getValidationRandomValue(value);
			
			int index = 0;
			for(String splitString : splitList){
				if(valueCheck(splitString)){
					if(isKorean){
						parameterMap.put(splitString, key+"_"+index);
					}else{
						parameterMapEn.put(key+"_"+index,splitString);
					}
					index++;
				}
			}
		}
		else if(valueCheck(value)){
			if(isKorean){
				parameterMap.put(value,key);	
			}else{
				parameterMapEn.put(key , value);
			}
			
			if(!valueOriginal.equals(value)){
				if(isKorean){
					parameterMap.put(valueOriginal , key+"_Original");
				}else{
					parameterMapEn.put(key+"_Original" , valueOriginal);
				}
			}
			if(isKorean){
				checkSpecialCharAndPutParameterIntoMap(key,value,true);
			}else{
				checkSpecialCharAndPutParameterIntoMap(key,value,false);
			}
			
		}
		
	}
	
	/**
	 * 특수문자를 확인하는경우이며
	 * RCPTT에서 + / 와 같은 문자열들이
	 * \\+ \\/ 와같이 변형이 되는경우가 있습니다
	 * 그런경우를 추가하여 파라미터에 넣습니다.
	 */
	private void checkSpecialCharAndPutParameterIntoMap(String key, String value , boolean isKorean){
		if(value.matches(SPECIAL_CHAR_REGEX)){
			value = value.replace("\\", "");
			StringBuilder valueSpecialString = new StringBuilder();
			boolean specialCheck = false;
			for(char specialChar : value.toCharArray()){
				specialCheck = Pattern.matches(SPECIAL_CHAR_REGEX, specialChar+"");
				if(!specialCheck)
					valueSpecialString.append(specialChar);
				else{
					valueSpecialString.append("\\\\");
					valueSpecialString.append(specialChar);
				}
			}
			
			if(isKorean){
				parameterMap.put(valueSpecialString.toString(), key+"_AddBackSlash");
			}else{
				parameterMapEn.put(key+"_AddBackSlash" , valueSpecialString.toString());
			}
		}
	}
	
	/**
	 * 파라미터 맵에서 동일한 value를 가진 키를 반환합니다.
	 * 없을 경우 null을 반환합니다.
	 * @param value
	 * @return
	 */
	public String searchKey(String value){ //parameterMap에서는 한글로 가지고 있습니다.
		return parameterMap.get(Converter.convertUnicodeToKorean(value));
	}
	
	/**
	 * ' ' (스페이스) 와 '\\' 의 value를 가진 키를 없애기 위하여 입니다.
	 * @param str
	 * @return
	 */
	private boolean valueCheck(String str){
		return (!str.isEmpty() && !str.equals("\\"));
	}
	
	/**
	 * Get Set
	 * @return
	 */
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

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}
	public Map<String, String> getParameterMapEn() {
		return parameterMapEn;
	}

	public String getParameterEnCtxId() {
		return parameterEnCtxId;
	}

	public void setParameterEnCtxId(String parameterEnCtxId) {
		this.parameterEnCtxId = parameterEnCtxId;
	}
	
}	
