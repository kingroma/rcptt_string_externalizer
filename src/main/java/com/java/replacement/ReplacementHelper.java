package com.java.replacement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;


import com.java.parameter.ParameterValidation;
import com.java.rcptt.RObject;
import com.java.util.ErrorMessage;
import com.java.util.Logger;
import com.java.util.ProgramData;
import com.java.util.UserInputData;

/**
 * 주요 기능인 치환 기능입니다.
 * 모든 eclscript를 확인하여 읽어온 parameter 로 매핑하여
 * 확인되는 key가 있을경우 key로 치환합니다.
 * 확인이 되지않은 문자열들은 그대로 둡니다.
 * ( 치환 확인 부분에 하드코딩이 들어가있습니다. ) 
 * @author suresoft
 */
public class ReplacementHelper {
	
	/**
	 * RCPTT 에서 몇개의 문자열이 확인 되었는지 저장하는 int 형 변수입니다.
	 */
	private int rcpttValueAmount;

	/**
	 * 몇개의 문자열에서 몇개의 치환이 되었는지 확인하는 int 형 변수입니다.
	 */
	private int replaceCount;

	/**
	 * 한줄 한줄 읽을때 확인하기 위한 boolean 형식의 변수입니다.
	 */
	private boolean isChange;

	/**
	 * 1차 치환이 종료된 후 2차 치환시 그 문자열이 경로 형식의 문자열인지
	 * 아닌지 확인하는 정규식 표현입니다.
	 */
	private final static String PATH_REGEX = "^[a-zA-Z]:\\\\.*[a-zA-Z0-9가-힣]"; // 1차 치환이 종료된 후 2차 치환시 그 문자열이 path인지 아닌지 확인하는 정규식입니다.
	
	/**
	 * 한글 확인 정규식입니다.
	 */
	private final static String KOREAN_REGEX = ".*[가-힣].*";

	/**
	 * " + "  
	 * "ab" + "cd" 제거 를 위한 정규식입니다
	 * 원래는 abcd 인 문장이 rcptt에서 긴 문자열이 존재할경우 나뉘어지기 때문에
	 * 나뉜 부분을 합치기 위함입니다.
	 */
	private final static String REMOVE_PLUS_SIGN_ECL_REGEX = "\"\n\t* *\\+ *\t*\"";
	
	/**
	 * 확장자명을 담고있는 properties의 파일 이름입니다.
	 */
	private final static String ExtensionListFileName = "extensionList.properties";

	/**
	 * 영문 치환시 정상적으로 적용이 되지않은 부분을 사용자가 임의로 추가하는 properties 파일입니다.
	 */
	private final static String ForceChangeParameterEnFileName ="forceChangeParameterEn.properties";
	
	/**
	 * 위의 ExtensionListFileName 에서 확인된 확장자 명을 담고 있는 ArrayList 입니다.
	 */
	private ArrayList<String> extensionStringList;

	/**
	 * 생성 과 동시에 Extension 파일을 읽습니다.
	 */
	public ReplacementHelper() {
		readForceChangeParameterEnProperties();
		readExtensionList();
	}
	
	/**
	 * Test 용이며 첫 시작시 
	 * init 하는내용 없이 시작하는것이 필요하여 
	 * 만들었습니다. 
	 */
	public ReplacementHelper(boolean bool){
		//Test용
	}
	
	/**
	 * <pre>
	 * 읽어온파일에서 ecl script를 가지고와서
	 * 치환 프로세스 진행합니다.
	 * 치환이 이루어졌을경우 testcase 혹은 ecl script를 덮어쓰기로 저장합니다.
	 * </pre>
	 */
	public void replce() {
		for(RObject rObj : ProgramData.getInstance().getProject().getObjects()){
			String eclScript = rObj.getEclScript();
			
			if((eclScript != null) && (eclScript.length()>0)){
				rObj.setECLCode(this.replceCheck(removePlusSignEcl(eclScript)));
				if(UserInputData.Language.equals("ko")){
					rObj.addContexts(ProgramData.getInstance().getParameterCtxId());
					rObj.deleteContexts(ProgramData.getInstance().getParameterEnCtxId());
					rObj.save();
				}else{
					rObj.addContexts(ProgramData.getInstance().getParameterEnCtxId());
					rObj.deleteContexts(ProgramData.getInstance().getParameterCtxId());
					rObj.save();
				}
			}

		}

//		Logger.write(" RCPTT에서 확인된 문자열 총 갯수 : " + rcpttValueAmount + " / 변환된 갯수 : "+replaceCount	+ " / 퍼센트 : " + Math.round((replaceCount*100.0)/(rcpttValueAmount*1.0)) 	+ " %");
	}
	
	/**
	 * " + "  
	 * "ab" + "cd" 제거 를 위한 메소드입니다.
	 * 원래는 abcd 인 문장이 rcptt에서 긴 문자열이 존재할경우 나뉘어지기 때문에
	 * 나뉜 부분을 합치기 위함입니다.
	 * REMOVE_PLUS_SIGN_ECL_REGEX > 정규식은 ::  \"\n\t* *\\+ *\t*\"
	 */
	public String removePlusSignEcl(String ecl){
		return ecl.replaceAll(REMOVE_PLUS_SIGN_ECL_REGEX, "");
	}
		
	/**
	 * <pre>
	 * 크게 2가지를 가지고 치환진행을 하는 메소드입니다
	 * 메소드의 파라미터로 받는 [[ String str ]] 
	 * 의 경우 ecl script의 내용입니다.
	 *  
	 * 1차]] properties를 가지고 치환
	 * -> properties 파일과 기존에 있던 parameter들로 치환을 진행합니다. << 1차 치환
	 *
	 * 2차]] 치환이 되지않은 문자열을 가지고 새로 생성할 수 있는 키가 있는지 확인합니다.
	 * -> 남은 문자열을 확인하여 경로인경우 새로운 파라미터를 추가 합니다. << 2차 치환
	 * -> 확장자가 프로젝트 관련일경우 새로운 파라미터를 추가 합니다.
	 * -> format 형식으로 치환이 가능한지 확인 후 변환 합니다.
	 * </pre>
	 * @param inputString
	 * @return
	 */
	private String replceCheck(String inputString) {
		try {
			StringBuilder returnStringBuilder = new StringBuilder(); // 한 라인씩 확인하면서 라인의 내용을 담을 string builder 이며 이 변수를 반환합니다.
			BufferedReader reader = new BufferedReader(new StringReader(inputString));
			String currentLine = null;

			String newLine = "\n";

			while ((currentLine = reader.readLine()) != null) {
				StringBuilder keyStringBuilder = new StringBuilder();
				StringBuilder preLineStringBuilder = new StringBuilder(); // 그전의 라인에 대해 가지고 있습니다.

				boolean isKeyLine = false; // 현재 읽는 부분에서 이부분이 치환을 해야하나는 부분인지 확인하는 flag
				char previousChar = 0; // 그전에 읽은 char를 확인하기 위함 입니다.
				
				for(char currentChar : currentLine.toCharArray()){ // properties파일과 기존에 있던 parameter로 치환 진행합니다.
					if(currentChar=='"') {
						if(!isKeyLine) {
							isKeyLine = true; // 현재 문자열 시작 부분으로 flag를 true시켜줍니다.
						}else {
							if(previousChar == '\\') { // \" 이런 경우가 있으므로 그것을 피하기 위함 입니다.
								keyStringBuilder.append(currentChar);
							}else {
								if(!preLineStringBuilder.toString().contains("[format")  
										&&  !preLineStringBuilder.toString().contains("+")) { // 포멧형인경우와 "ab" + "cd" 인것을 피하기 위함 입니다.
									
									String output = this.findKey(keyStringBuilder.toString()); // 치환 가능한 key를 찾는 기능입니다.

									if(this.isChange) {//만약 치환이 있었을 경우
										currentLine = currentLine.replace("\""+keyStringBuilder+"\"" , output);//치환된 부분을 변경합니다.
									}
									else {//처음 치환이 안 이러우졌을 경우 2차 치환을 진행합니다.
										output = this.findNewKey(keyStringBuilder.toString());

										if(this.isChange){//변경이 이루어 졌는지 확인합니다.
											currentLine = currentLine.replace("\""+keyStringBuilder.toString()+"\"", output);//치환된 부분을 변경
										}else {
											if(preLineStringBuilder.toString().contains("get-label")){
												output = this.getLabelReplace(keyStringBuilder.toString());
												if(this.isChange){
													currentLine = currentLine.replace("\""+keyStringBuilder.toString()+"\"", output);
												}
											}
										}
									}
									if(this.isChange){
										this.replaceCount++;
									}
									rcpttValueAmount++; // 총 확인된 문자열들을 추가합니다.
								}
								
								//set default
								this.isChange = false;
								isKeyLine = false;
								preLineStringBuilder = null;
								preLineStringBuilder = new StringBuilder();
								keyStringBuilder = null;
								keyStringBuilder = new StringBuilder();
							}
						}
					}else {
						if(!isKeyLine) { // 그전 라인들을 저장합니다.							
							preLineStringBuilder.append(currentChar);
						}else { // isKeyLine / flag == true 인경우  key 의 부분을 저장하고 있습니다.
							keyStringBuilder.append(currentChar);
							previousChar = currentChar;
						}
					}
				}
				returnStringBuilder.append(currentLine);
				returnStringBuilder.append(newLine);
			}

			return returnStringBuilder.toString();
		} catch (IOException e) {
			ErrorMessage.getInstance().printErrorMessage("ReplacementHelper.replace.error");
			return inputString;
		}
	}
	
	/**
	 * get-label 치환 
	 * get-labe의경우 rcptt에서 \n이없어지는 경향이있어서
	 * 그부분을 확인하는 메소드입니다.
	 * @param input
	 * @return
	 */
	private String getLabelReplace(String input){
		for(String keySet : ProgramData.getInstance().getParameterMap().keySet()){
			String tempKeySet = keySet.replace("\\n", "");
			if(input.equals(tempKeySet)){
				String key = ProgramData.getInstance().getParameterMap().get(keySet);
				if(!tempKeySet.equals(key)){
					addParameter("Get_Label_"+key,tempKeySet);
					this.isChange = true;
					replaceCount++;
					return "$Get_Label_"+key;
				}
			}
		}
		return "\""+input+"\"";
	}
	
	/**
	 * 입력된 key값과 모든 파라미터값을 확인하여 치환가능한경우
	 * key값을 리턴
	 * @param key
	 * @return
	 */
	public String findKey(String key) {;
		String ret = ProgramData.getInstance().searchKey(key);

		if(ret!=null){
//			this.replaceCount++;
			this.isChange = true;
			return "$" + ret;
		}

		return "\"" + key + "\"";
	}

	/**
	 * <pre>
	 * 1 path 일경우 path를 키값으로 두어 치환합니다.
	 * 2 .h.c 같이 확장자 명이 소스 파일일경우 파일의 경로를 key로 치환합니다.
	 * 3 format형식일 경우 포멧형식에 맞게 치환합니다.
	 * 알맞게 key 리턴
	 * @param key
	 * @return
	 */
	public String findNewKey(String key) {
		/**
		 * key가 경로파일인 경우를 확인합니다
		 * C:\\ 같은 경우를 기준으로합니다.
		 * 정규식 : ^[a-zA-Z]:\\\\.*[a-zA-Z0-9가-힣]
		 * 1차 치환이 종료된 후 2차 치환시 그 문자열이 path인지 아닌지 확인하는 정규식입니다. 
		 */
		if(Pattern.matches(PATH_REGEX, key)){
			String validationKey = ParameterValidation.getValidationKey("path_"+key);
			addParameter(validationKey,key);
			this.isChange = true;
			return "$"+validationKey;
		}

		/**
		 * 파일 확장자명을 외부파일 ( exteionList.properties ) 에서 
		 * 확인되는 확장자명을 기준으로 새로운 key를 만들어
		 * 치환을 합니다.
		 * .h .c 등을 찾는것을 목적으로 합니다.
		 */
		if (key.contains(".")) {
			for(String extension : extensionStringList){
				if(key.endsWith(extension)){
					String validationKey = ParameterValidation.getValidationKey("extension_"+key);
					addParameter(validationKey,key);
					this.isChange = true;
					return "$"+validationKey;
				}
			}
		}
		
		/**
		 * format 형식으로 치환하는 부분입니다.
		 * 기준은 [[ / ]] 로 나누고 있으며 나눈 value들을 가지고 
		 * 키를 찾습니다. 
		 * 만약에 키를 찾을시 format의 형식은
		 * [format "%s%s" $key1 key2] 로 변형이 됩니다.
		 */
		if(key.contains("/")) { // 슬래쉬를 기준으로 나누어 key를 확인합니다.
			boolean isFormatChange = false; // 포멧으로 변형이 되었을경우를 확인하기 위하여
			StringBuilder formKeyList = new StringBuilder(); // 변형이 이루어졌으때 문자열에서는 %s 로 저장하고 오른쪽 에는 $key 형식으로 가지고 있기위하여
			StringBuilder formStr = new StringBuilder(); // 변형하는 문장을 저장하기 위하여
			//확인과정
			String[] splitKey = key.split("/");
			String previousMiniKey = "";
			String resultFindKey = null;
			
			for(String miniKey : splitKey){ // ' / ' 를 기준으로 miniKey로 나누어 findKey 메소드를 이용하여 확인합니다.
				if(miniKey.endsWith("\\\\")){
					previousMiniKey += miniKey+"/";
				}else{
					resultFindKey = this.findKey(previousMiniKey+miniKey);
					if(this.isChange){
						formStr.append("%s/");
						formKeyList.append(" "+resultFindKey);
						this.isChange = false;
						isFormatChange = true;
						previousMiniKey = "";
					}else {
						formStr.append(previousMiniKey+miniKey+"/");
					}
				}
			}
			if(isFormatChange) { // 만약에 포멧형성이 가능한경우 형태를 생성하여 반환합니다.
				this.isChange = true;
				if(formStr.toString().endsWith("/")){
					return "[format \"" + formStr.toString().substring(0, formStr.length()-1) + "\" " + formKeyList.toString() + "]";
				}
				return "[format \"" + formStr.toString() + "\" " + formKeyList.toString() + "]";
				
			}else{
				
			}
			
		}
		
		/**
		 * 영문 치환시 한국어가 있으면 안됨으로 무식하게 모든 key를 확인하여 치환이
		 * 가능한지 확인합니다.
		 */
		if(key.matches(KOREAN_REGEX)){ // 무식하게 다
			Map<String,String> map = ProgramData.getInstance().getParameterMap();
			
			Set<String> keySets = map.keySet();
			String keyTemp = "";
			for(String keySet : keySets){
				if(key.contains(keySet)){
					keyTemp = key.replace(keySet, "%s");
					if(!keyTemp.matches(KOREAN_REGEX)){
						String value = map.get(keySet);
						this.isChange = true;
						return "[format \"" + keyTemp + "\" $" + value + "]";
					}
				}
			}
			
			/**
			 * 만약 위에서 확인이 안됬을경우
			 * 하나하나 나누어서 확인해봅니다.
			 */
			String regex = "[a-zA-Z0-9]";
			String[] split = key.split(regex);
			ArrayList<String> splitKey = new ArrayList<String>();
			boolean isNotNull = true;
			for(String aSplit : split){
				keyTemp = map.get(aSplit);
				if (!aSplit.isEmpty()) {
					if (keyTemp != null) {
						splitKey.add(keyTemp);
					} else {
						isNotNull = false;
						break;
					}
				}
			}
			if(isNotNull){
				for(String aSplit : split){
					if(!aSplit.isEmpty()){
						key = key.replace(aSplit, "%s");
					}
				}
				String outputFormat = "";
				for(String aSplitKey : splitKey){
					outputFormat += " $"+aSplitKey;
				}
				this.isChange = true;
				return "[format \"" + key + "\"" + outputFormat + "]";
			}
			
			/**
			 * 띄어쓰기로 검사합니다.
			 * [[ 확인 체크 ]] 와같은 문자열이 있을경우 띄어쓰기를 기준으로 전체가 바뀔 경우 
			 * format형식으로 치환합니다. 
			 */
			split = key.split(" ");
			splitKey = new ArrayList<String>();
			isNotNull = true;
			for(String aSplit : split){
				keyTemp = map.get(aSplit);
				if(!aSplit.isEmpty()){
					if(keyTemp!=null){
						splitKey.add(keyTemp);
					}else {
						isNotNull = false;
						break;
					}
				}
			}
			if(isNotNull){
				for(String aSplit : split){
					if(!aSplit.isEmpty()){
						key = key.replace(aSplit, "%s");
					}
				}
				String outputFormat = "";
				for(String aSplitKey : splitKey){
					outputFormat += " $"+aSplitKey;
				}
				this.isChange = true;
				return "[format \"" + key + "\"" + outputFormat + "]";
			}
			
			/**
			 * 전체다 확인 마지막으로 
			 * 가격이 비싸지만 한칸씩 확인하는 방법 .
			 */	
			StringBuilder allKoreanChecker = new StringBuilder();
			StringBuilder currentKorean = new StringBuilder();
			StringBuilder collectKey = new StringBuilder();
			String mapValue = "";
			boolean isStart = false;
			for(char current : key.toCharArray()){
				if((current+"").matches(".*[가-힣].*") && isStart == false){
					isStart = true;
				}else if (isStart == false){
					allKoreanChecker.append(current);
				}
				if(isStart){
					currentKorean.append(current);
					mapValue = map.get(currentKorean.toString());
					if(mapValue!=null){
						allKoreanChecker.append("%s");
						collectKey.append(" $"+mapValue);
						currentKorean = new StringBuilder();
						isStart = false;
					}
				}
			}
			allKoreanChecker.append(currentKorean.toString());
			if(!allKoreanChecker.toString().matches(KOREAN_REGEX)){
				//전체의 한국어 문자열이 변형이 이루어지지 않았을경우 return하지 않습니다.
				this.isChange = true;
				return "[format \"" + allKoreanChecker.toString() + "\"" + collectKey.toString() + "]";
			}
		}
		// 아닌경우 처음에 들어왔던 key 그대로 다시 반환합니다.
		this.isChange = false;
		return "\"" + key + "\"";
	}

	/**
	 * ExtensionListFileName으로부터
	 * 확장자명을 담을 properties 파일을 읽어와
	 * extensionList에 ArrayList로 저장합니다.
	 */
	private void readExtensionList(){
		Properties p = null;
		InputStream is = null;

		File extensionListFile = new File(ExtensionListFileName);
		extensionStringList = new ArrayList<String>();

		if(extensionListFile.exists()){
			try {
				is = new FileInputStream(extensionListFile);
				p = new Properties();
				p.load(is);
				for(Object obj : p.keySet()){
					String key = (String)obj;
					String value = p.getProperty(key);
					extensionStringList.add(value);

				}
				is.close();
			} catch (IOException e) {
				ErrorMessage.getInstance().printErrorMessage("ReplacementHelper.read.defaultParameter.error");
			}
		}else{
			Logger.write("create defaultparameter.properties");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(ExtensionListFileName);
				fos.write("".getBytes());
				fos.close();

			} catch (IOException exception) {
				ErrorMessage.getInstance().printErrorMessage("ReplacementHelper.read.defaultParameter.error");
			}finally{
				try {
					if(fos!=null){
						fos.close();
					}
				} catch (IOException exception2) {
					ErrorMessage.getInstance().printErrorMessage("ReplacementHelper.read.defaultParameter.error");
				}
			}
		}
	}
	
	/**
	 * 한국어에서 영문테스트를 위하여
	 * RCPTT에서 실행하였을때 결과값이 다른경우
	 * 사용자가 force 치환 합니다.
	 */
	private void readForceChangeParameterEnProperties(){
		Properties p = null;
		InputStream is = null;

		File forceChangeParameterEnFile = new File(ForceChangeParameterEnFileName);
		
		if(forceChangeParameterEnFile.exists()){
			try {
				is = new FileInputStream(forceChangeParameterEnFile);
				p = new Properties();
				p.load(is);
				for(Object obj : p.keySet()){
					String key = (String)obj;
					String value = p.getProperty(key);
					ProgramData.getInstance().addParameter(key,value,false);
//					System.out.println(key + " ------ " + value);
				}
				is.close();
			} catch (IOException e) {
				ErrorMessage.getInstance().printErrorMessage("ReplacementHelper.read.defaultParameter.error");
			}
		}else{
			Logger.write("create defaultparameter.properties");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(ForceChangeParameterEnFileName);
				fos.write("".getBytes());
				fos.close();

			} catch (IOException exception) {
				ErrorMessage.getInstance().printErrorMessage("ReplacementHelper.read.defaultParameter.error");
			}finally{
				try {
					if(fos!=null){
						fos.close();
					}
				} catch (IOException exception2) {
					ErrorMessage.getInstance().printErrorMessage("ReplacementHelper.read.defaultParameter.error");
				}
			}
		}
	}

	/**
	 * Program Data 에 있는 parameterMap에 접근하여
	 * 파라미터를 추가해줍니다 ( 영문 , 한글 ) 
	 * @param key
	 * @param value
	 */
	private void addParameter(String key , String value){
		ProgramData.getInstance().addParameter(key,value,true); // 한글 파라미터 추가 
		ProgramData.getInstance().addParameter(key,value,false); // 영문 파라미터 추가 
		
	}
	
	/**
	 * get set
	 */
	public int getRcpttValueAmount() {
		return rcpttValueAmount;
	}


	public void setRcpttValueAmount(int rcpttValueAmount) {
		this.rcpttValueAmount = rcpttValueAmount;
	}


	public int getReplaceCount() {
		return replaceCount;
	}


	public void setReplaceCount(int replaceCount) {
		this.replaceCount = replaceCount;
	}
}
