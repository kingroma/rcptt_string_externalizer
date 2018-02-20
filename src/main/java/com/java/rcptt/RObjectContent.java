package com.java.rcptt;



import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.java.util.Converter;
import com.java.util.ProgramData;
import com.java.util.UserInputData;

/**
 * <pre>
 * ctx파일 혹은 test 파일에서 content 정보를 담는 class
 * 하드코딩 되어있습니다.
 * 만약 RCPTT API 를 사용 가능하게 될 경우
 * 변경 예정입니다.
 * @author suresoft
 */
public class RObjectContent {
	/**
	* 포멧의 형식대로 파싱합니다
	* 추후 확인되는 RCPTT API 있을경우 변경 예정입니다.
	* (하드코딩) 
	*/
	
	private String startLine ;
	private String contentType ;
	private String entryName ;
	private String text ;
	private String endLine;
	private RcpttResourceType type;
	private boolean isParameter;
	private String path;

	/**
	 * 생성되었을때 그대로 변수를 저장합니다.
	 * @param start_line -시작 라인
	 * @param content_type - 콘탠츠 타입
	 * @param entry_name - 엔르티 네임
	 * @param text - 내용
	 * @param end_line - 시작라인에다 --
	 * @param isParameter - 파라미터 ctx파일인지 아닌지
	 */
	public RObjectContent(String start_line , String content_type , String entry_name , String text , String end_line, boolean isParameter) {
		this.startLine = start_line;
		this.contentType = content_type;
		this.entryName = entry_name;
		this.text = text;
		this.endLine = end_line;
		this.isParameter = isParameter;
		this.setType();
		
	}
	
	/**
	 * 파라미터의 경로를 확인하기 위하여 생성자를 2개로 받았습니다.
	 * 필요없는경우 위의 생성자로 생성이 됩니다.
	 * @param start_line
	 * @param content_type
	 * @param entry_name
	 * @param text
	 * @param end_line
	 * @param isParameter
	 * @param path
	 */
	public RObjectContent(String start_line , String content_type , String entry_name , String text , String end_line, boolean isParameter,String path) {
		this.startLine = start_line;
		this.contentType = content_type;
		this.entryName = entry_name;
		this.text = text;
		this.endLine = end_line;
		this.isParameter = isParameter;
		this.setType();
		this.path = path;
	}

	
	/**
	 * RObject에서 저장할때 ( save() ) content의 포멧대로
	 * String 값을 반환하는 메소드입니다.
	 * @return
	 */
	public String output() {

		StringBuilder outputStringBuilder = new StringBuilder();
		String newLine = "\n";
		outputStringBuilder.append(startLine);
		outputStringBuilder.append(newLine);
		outputStringBuilder.append(contentType);
		outputStringBuilder.append(newLine);
		outputStringBuilder.append(entryName);
		outputStringBuilder.append(newLine);


		if(isParameter) { // 만약에 이 오브젝트가 parameter인경우  파라미터의 정보를 담는다.
			if(this.path.equals(UserInputData.ParameterCtxPath)){
				outputStringBuilder.append(newLine);
				
				for(Object obj : ProgramData.getInstance().getParameterMap().keySet()){
					String key = (String)obj;
					String value = ProgramData.getInstance().searchKey(key);
	
					if(value!=null){
						outputStringBuilder.append(value);
						outputStringBuilder.append("=");
						outputStringBuilder.append(Converter.convertKoreanToUnicode(key));
						outputStringBuilder.append(newLine);
					}
				}
				outputStringBuilder.append(newLine);
			}else if(this.path.equals(UserInputData.ParameterCtxEnPath)){
				outputStringBuilder.append(newLine);
				
				Map<String,String> param = ProgramData.getInstance().getParameterMap();
				Map<String,String> param2 = ProgramData.getInstance().getParameterMapEn();
				Map<String,String> saveParameter = new HashMap<String,String>();
				Set<String> keys = param.keySet();
				for(String k : keys){
					String str = param.get(k);
					String str2 = param2.get(str.replace(".nl", ""));
					
					if(str2!=null){
						saveParameter.put(str,str2);
					}
				}
				for(String temp : saveParameter.keySet()){
					outputStringBuilder.append(temp);
					outputStringBuilder.append("=");
					outputStringBuilder.append(saveParameter.get(temp));
					outputStringBuilder.append("\n");
				}
				
				
				outputStringBuilder.append(newLine);
			}
		}else { // 파라미터 정보가아닐경우 eclscript, testcase 인경우
			outputStringBuilder.append(text);
		}

		outputStringBuilder.append(endLine);
		outputStringBuilder.append(newLine);

		return outputStringBuilder.toString();
	}
	
	/**
	* content의 타입을 결정하며
	* enum 을 사용하여 정합니다.
	* 이것이 무슨 파일인지 분류 (not yet = -1 / testcase = 0 / parameter = 1 / ecl = 2 / description = 3
	*										TYPE_TESTCASE   TYPE_PARAMETER  TYPE_ECL  TYPE_DESCRIPTION
	*/
	private void setType() {
		if (this.contentType.equals("Content-Type: text/ecl")) { // ecl 인경우
			this.type = RcpttResourceType.ECL;
		}
		else if (this.contentType.equals("Content-Type: text/plain")) { // description 인경우
			this.type = RcpttResourceType.DESCRIPTION;
		}
		else if (this.contentType.equals("Content-Type: text/properties")) { // parameter 인경우
			this.type = RcpttResourceType.PARAMETER;
		}
	}

	
	
	/**
	 * get set
	 * @return
	 */
	public String getStart_line() {
		return startLine;
	}

	public void setStart_line(String start_line) {
		this.startLine = start_line;
	}

	public String getContent_type() {
		return contentType;
	}

	public void setContent_type(String content_type) {
		this.contentType = content_type;
	}

	public String getEntry_name() {
		return entryName;
	}

	public void setEntry_name(String entry_name) {
		this.entryName = entry_name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getEnd_line() {
		return endLine;
	}

	public void setEnd_line(String end_line) {
		this.endLine = end_line;
	}

	public boolean isParameter() {
		return isParameter;
	}

	public void setParameter(boolean isParameter) {
		this.isParameter = isParameter;
	}

	public RcpttResourceType getType() {
		return type;
	}

	public void setType(RcpttResourceType type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}


}
