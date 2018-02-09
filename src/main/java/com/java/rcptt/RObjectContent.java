package com.java.rcptt;



import com.java.util.Converter;
import com.java.util.ProgramData;

/**
 * <pre>
 * ctx파일 혹은 test 파일에서 content 정보를 담는 class
 * 하드코딩 되어있습니다.
 * 만약 RCPTT API 를 사용 가능하게 될 경우
 * 변경 예정입니다.
 * @author suresoft
 *
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



	//------=_.parameters.context-2e023de5-3294-36a9-ac1d-6701f05a40ee
	//Content-Type: text/properties
	//Entry-Name: .parameters.context

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
	 * RObject에서 저장할때 ( save() ) content의 포멧대로
	 * String 값을 반환하는 메소드입니다.
	 * @return
	 */
	public String output() {

		StringBuilder sb = new StringBuilder();
		String newLine = "\n";
		sb.append(startLine);
		sb.append(newLine);
		sb.append(contentType);
		sb.append(newLine);
		sb.append(entryName);
		sb.append(newLine);


		if(isParameter) { // 만약에 이 오브젝트가 parameter인경우  파라미터의 정보를 담는다.
			sb.append(newLine);

			for(Object obj : ProgramData.getInstance().getParameterMap().keySet()){
				String key = (String)obj;
				String value = ProgramData.getInstance().searchKey(key);

				if(value!=null){
					sb.append(value);
					sb.append("=");
					sb.append(Converter.convertKoreanToUnicode(key));
					sb.append(newLine);
				}
			}
			sb.append(newLine);
		}else { // 파라미터 정보가아닐경우 eclscript, testcase 인경우
			sb.append(text);
		}

		sb.append(endLine);
		sb.append(newLine);

		return sb.toString();
	}

	/**
	* content의 타입을 결정하며
	* enum 을 사용하여 정합니다.
	* 이것이 무슨 파일인지 분류 (not yet = -1 / testcase = 0 / parameter = 1 / ecl = 2 / description = 3
	*										TYPE_TESTCASE   TYPE_PARAMETER  TYPE_ECL  TYPE_DESCRIPTION
	*/
	private void setType() {
		//ecl 인경우
		if (this.contentType.equals("Content-Type: text/ecl")) {
			this.type = RcpttResourceType.ECL;
		}
		// description 인경우
		else if (this.contentType.equals("Content-Type: text/plain")) {
			this.type = RcpttResourceType.DESCRIPTION;
		}

		// parameter 인경우
		else if (this.contentType.equals("Content-Type: text/properties")) {
			this.type = RcpttResourceType.PARAMETER;
		}
	}

	
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


}
