package com.java.replacement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import com.java.parameter.ParameterValidation;
import com.java.rcptt.RObject;
import com.java.util.Logger;
import com.java.util.ProgramData;

/**
 * 주요 기능인 치환 기능입니다.
 * 모든 eclscript를 확인하여 읽어온 parameter 로 매핑하여
 * 확인되는 key가 있을경우 key로 치환합니다.
 * 확인이 되지않은 문자열들은 그대로 둡니다.
 * @author suresoft
 *
 */
public class ReplacementHelper {

	/**
	 * RCPTT 에서 몇개의 문자열이 확인 되었는지 저장하는 int 형 변수입니다.
	 */
	private int amount;

	/**
	 * 몇개의 문자열에서 몇개의 치환이 되었는지 확인하는 int 형 변수입니다.
	 */
	private int count;

	/**
	 * 한줄 한줄 읽을때 확인하기 위한 boolean 형식의 변수입니다.
	 */
	private boolean isChange;

	/**
	 * 치환이 되어 바뀐것이 있는지 확인하며
	 * 치환이 된것이 한개라도 있을 경우 추후 저장하기 위한 boolean 형식의 변수 입니다.
	 */
	private boolean needSave;

	/**
	 * 1차 치환이 종료된 후 2차 치환시 그 문자열이 경로 형식의 문자열인지
	 * 아닌지 확인하는 정규식 표현입니다.
	 */
	private static final String PATH_REGEX = "^[a-zA-Z]:\\\\.*[a-zA-Z]"; // 1차 치환이 종료된 후 2차 치환시 그 문자열이 path인지 아닌지 확인하는 정규식입니다.

	/**
	 * 확장자명을 담고있는 properties의 파일 이름입니다.
	 */
	private static final String ExtensionListFileName = "extensionList.properties";

	/**
	 * 위의 ExtensionListFileName 에서 확인된 확장자 명을 담고 있는 ArrayList 입니다.
	 */
	private ArrayList<String> extensionList;

	/**
	 * 생성 과 동시에 Extension 파일을 읽습니다.
	 */
	public ReplacementHelper() {
		readExtensionList();
	}


	/**
	 * <pre>
	 * 읽어온파일에서 ecl script를 가지고와서
	 * 치환 프로세스 진행합니다.
	 * 치환이 이루어졌을경우 testcase 혹은 ecl script를 덮어쓰기로 저장합니다.
	 * </pre>
	 */
	public void replce() {
		for(RObject obj : ProgramData.getInstance().getProject().getObjects()){
			String eclScript = obj.getEclScript();

			if(eclScript != null && !eclScript.equals("")){
				obj.setECLCode(this.replceCheck(eclScript));

				if(needSave){
					obj.addContexts(ProgramData.getInstance().getParameterCtxId());
					obj.save();
					needSave = false;
				}
			}

		}

		Logger.write(" 총 갯수 : " + amount + " /  변환된 갯수 : "+count + " / 퍼센트 : "+Math.round((count*100.0)/(amount*1.0))+" %");
	}

	/**
	 * <pre>
	 * 1차]] properties를 가지고 치환
	 * -> properties 파일과 기존에 있던 parameter들로 치환을 진행합니다. << 1차 치환
	 *
	 * 2차]] 치환이 되지않은 문자열을 가지고 새로 생성할 수 있는 키가 있는지 확인합니다.
	 * -> 남은 문자열을 확인하여 경로인경우 새로운 파라미터를 추가 합니다. << 2차 치환
	 * -> 확장자가 프로젝트 관련일경우 새로운 파라미터를 추가 합니다.
	 * -> format 형식으로 치환이 가능한지 확인 후 변환 합니다.
	 * </pre>
	 * @param str
	 * @return
	 */
	private String replceCheck(String str) {
		try {
			StringBuilder returnStringBuilder = new StringBuilder(); // 한 라인씩 확인하면서 라인의 내용을 담을 string builder 이며 이 변수를 반환합니다.
			BufferedReader reader = new BufferedReader(new StringReader(str));
			String line = null;

			String new_line = "\n";

			while ((line = reader.readLine()) != null) {
				StringBuilder key = new StringBuilder();
				StringBuilder preLine = new StringBuilder(); // 그전의 라인에 대해 가지고 있습니다.

				// 현재 읽는 부분에서 이부분이 치환을 해야하나는 부분인지 확인하는 flag
				boolean isKeyLine = false;

				// 그전에 읽은 char를 확인하기 위함 입니다.
				char previousChar = 0;

				// properties파일과 기존에 있던 parameter로 치환 진행합니다.
				for(char currentChar : line.toCharArray()){
					if(currentChar=='"') {
						if(!isKeyLine) {
							isKeyLine = true; // 현재 문자열 시작 부분으로 flag를 true시켜줍니다.
						}else {
							if((line.length() > 0) && (previousChar == '\\') ) {
								// \" 이런 경우가 있으므로 그것을 피하기 위함 입니다.
								key.append(currentChar);
							}else {
								if(!preLine.toString().contains("[format")  &&  !preLine.toString().contains("+")) { // 포멧형인경우와 "ab" + "cd" 인것을 피하기 위함 입니다.

									String output = this.findKey(key.toString()); // 치환 가능한 key를 찾는 기능입니다.

									if(this.isChange) {//만약 치환이 있었을 경우
										line = line.replace("\""+key+"\"" , output);//치환된 부분을 변경합니다.

										//첫시작이 $key 인경우 RCPTT 에서는 에러가 발생하여 그것을 없애주기 위한 if 문입니다.
										if(preLine.toString().equals("\t")){
											if(returnStringBuilder.charAt(returnStringBuilder.length()-1)=='\n'){
												returnStringBuilder.deleteCharAt(returnStringBuilder.length()-1);
											}
										}
									}
									else {//처음 치환이 안 이러우졌을 경우 2차 치환을 진행합니다.
										output = this.findNewKey(key.toString());

										if(this.isChange){//변경이 이루어 졌는지 확인합니다.
											line = line.replace("\""+key+"\"", output);//치환된 부분을 변경
										}
									}
									amount++; // 총 확인된 문자열들을 추가합니다.
								}

								//set default
								this.isChange = false;
								isKeyLine = false;
								preLine = null;
								preLine = new StringBuilder();
								key = null;
								key = new StringBuilder();
							}
						}
					}else {
						if(!isKeyLine) {
							// 그전 라인들을 저장합니다.
							preLine.append(currentChar);
						}else {
							// isKeyLine / flag == true 인경우  key 의 부분을 저장하고 있습니다.
							key.append(currentChar);
							previousChar = currentChar;
						}
					}
				}
				returnStringBuilder.append(line);
				returnStringBuilder.append(new_line);
			}

			return returnStringBuilder.toString();
		} catch (IOException e) {
			System.out.println("substitution err");
			return str;
		}



	}

	/**
	 * 입력된 key값과 모든 파라미터값을 확인하여 치환가능한경우
	 * key값을 리턴
	 * @param key
	 * @return
	 */
	private String findKey(String key) {

		String ret = ProgramData.getInstance().searchKey(key);

		if(ret!=null){
			this.count++;
			this.isChange = true;
			this.needSave = true;
			return "$" + ret;
		}

		return "\"" + key + "\"";
	}


	/**
	 * <pre>
	 * 1 path 일경우 path를 키값으로 두어 치환
	 * 2 .h.c 같이 확장자 명이 소스 파일일경우 파일의 경로를 key로 치환
	 * 3 format형식일 경우 포멧형식에 맞게 치환
	 * 알맞게 key 리턴
	 * @param key
	 * @return
	 */

	private String findNewKey(String key) {

		if(Pattern.matches(PATH_REGEX, key)){
			String validationKey = ParameterValidation.getValidationKey("path_"+key);
			ProgramData.getInstance().addParameter(validationKey, key);
			this.isChange = true;
			this.needSave = true;
			count++;
			return "$"+validationKey;
		}

		// 외부화된 인터페이스
		//코드 수정이아니라 외부화 진행하여 확인하기 .
		//.h .c 등을 찾는것

		if (key.contains(".")) {
			for(String extension : extensionList){
				if(key.endsWith(extension)){
					String validationKey = ParameterValidation.getValidationKey("extension_"+key);
					ProgramData.getInstance().addParameter(validationKey, key);
					this.isChange = true;
					this.needSave = true;
					count++;
					return "$"+validationKey;
				}
			}
		}


		//format 형식으로 치환하는 부분입니다.
		//형식 : [format "%s%s" $key1 $key2]
		if(key.contains("/")) { // 슬래쉬를 기준으로 나누어 key를 확인합니다.
			boolean isFormatChange = false; // 포멧으로 변형이 되었을경우를 확인하기 위하여
			StringBuilder formKeyList = new StringBuilder(); // 변형이 이루어졌으때 문자열에서는 %s 로 저장하고 오른쪽 에는 $key 형식으로 가지고 있기위하여
			StringBuilder formStr = new StringBuilder(); // 변형하는 문장을 저장하기 위하여
			//확인과정
			String[] splitKey = key.split("/");
			String previousMiniKey = null;
			for(String miniKey : splitKey){ // ' / ' 를 기준으로 miniKey로 나누어 findKey 메소드를 이용하여 확인합니다.
				String output = this.findKey(miniKey);
				if(this.isChange){
					formStr.append("%s/");
					formKeyList.append(" "+output);
					this.isChange = false;
					isFormatChange = true;
					previousMiniKey = null;
				}
				else{ // 만약 ' / ' 로 나누어도 확인되지 않은경우 그전 miniKey와 현재 miniKey를 합쳐 또한번 검사를 합니다.
					if(previousMiniKey!=null){
						output = this.findKey(previousMiniKey+"/"+miniKey);
						if(this.isChange){
							//만약 치환이 되었을 경우 그전 append 한 문자열을 제거해 줍니다.
							formStr = formStr.delete(formStr.length()-previousMiniKey.length()-1, formStr.length());

							formStr.append("%s/");
							formKeyList.append(" "+output);
							this.isChange = false;
							isFormatChange = true;
							previousMiniKey = null;
						}else{
							previousMiniKey = miniKey;
							formStr.append(miniKey+"/");
						}
					}else{
						previousMiniKey = miniKey;
						formStr.append(miniKey+"/");
					}
				}
			}
			if(isFormatChange) { // 만약에 포멧형성이 가능한경우 형태를 생성하여 반환합니다.
				this.isChange = true;
				return "[format \"" + formStr.toString().substring(0, formStr.length()-1) + "\" " + formKeyList.toString() + "]";
			}
		}
		// 아닌경우 처음에 들어왔던 key를 다시 반환합니다.
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
		extensionList = new ArrayList<String>();

		if(extensionListFile.exists()){
			try {
				is = new FileInputStream(extensionListFile);
				p = new Properties();
				p.load(is);
				for(Object obj : p.keySet()){
					String key = (String)obj;
					String value = p.getProperty(key);
					extensionList.add(value);

				}
				is.close();
			} catch (IOException e) {
				Logger.write("read defaultParameter.properties error");
			}
		}else{
			Logger.write("create defaultparameter.properties");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(ExtensionListFileName);
				fos.write("".getBytes());
				fos.close();

			} catch (IOException exception) {
				Logger.write("read defaultParameter.properties output stream error");
			}finally{
				try {
					if(fos!=null)
						fos.close();
				} catch (IOException exception2) {
					Logger.write("read defaultParameter.properties output stream error");
				}
			}
		}
	}
}
