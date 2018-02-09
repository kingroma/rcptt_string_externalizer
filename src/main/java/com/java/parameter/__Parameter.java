package com.java.parameter;


/**
 * <pre>
 * <b>Parameter</b>
 * key와 value 형식으로 가지고있는 class 
 * 기본으로 들어오는 값은 validation을 거쳐 저장된다.
 * 
 * 1 Parameter(String key, String value)  
 * 	parameter.ctx파일에서 읽어올 경우 path를 확인하기 어려워 key와 value만 입력받는다.
 * 
 * 2 Parameter(String key, String value , String path)
 * 	.properties에서 읽어올 경우 경로를 받아 key에 붙이기 위하여 경로까지 입력받는다.
 * 
 * Parameter가 모여있는 장소는 ProgramConstants에서 ArrayList 한개로 관리한다.
 * 
 * </pre>
 * 
 * @author suresoft
 */
public class __Parameter {

	private String key = "";
	private String value = ""; 
	private String valueKorean = ""; // 
	private boolean path;
	
	public __Parameter(String key, String value) {
		this.key = validationKey(key) ;
		this.value = validationValue(value);
		//this.valueKorean = ProgramData.converter.convertUnicodeToKorean(this.value); // unicode -> 한국어
	}
	
	public __Parameter(String key, String value , String path) {
		this.path = true;
		this.key = validationPath(path)+"_"+validationKey(key) ;
		this.value = validationValue(value);
		//this.valueKorean = ProgramData.converter.convertUnicodeToKorean(this.value); // unicode -> 한국어
	}
	
	
	/**
	 * <pre>
	 * 1. 단축키 [[ 확인(&F) ]] 이런 형식의 value를 가지고있을때 & 없애기 ,
	 * 2. 간혹가다 value에 처음 띄어쓰기가 들어간것을  확인하기 위하여
	 * 
	 * <b>validationValue</b>
	 * 	private String validationValue(String value)
	 * </pre>
	 * 
	 * 
	 */
	private String validationValue(String value) {
		//간혹가다 value에 처음 띄어쓰기가 들어간대가있음
		String ret = value;
		ret = ret.trim();
		
		char current = 0;
		if(ret.contains("&")) {
			for(int i = 0 ; i < ret.length() ; i ++) { // 한글자씩 검사
				current = ret.charAt(i); // 현재 char
				if(current == '(') {  // 단축키 형식 (&F)
					if(i+3 < ret.length()) {
						if(ret.charAt(i+1)=='&') {
							if(ret.charAt(i+3)==')') {
								
								ret = ret.substring(0, i+1)+ret.substring(i+2,ret.length());
								
								break;
							}
						}
					}
				}
			}
			
		}
		
//		Properties 파일에는 \\ 가없음
//		MC/DC 는 MC\\/DC  < 가아님
//		소스 파일 C\\/C++ 프로젝트
//		사용자가 입력한것은 \\ 가 없음
//		물어보기
		
//		수정 
//		
//		"소스 파일 C\\/C++ 프로젝트"  
//		 \\ 가 들어가있는데 "MC/DC" 이거는 \\ 가 안들어가있음
//		
//		if((this.path) && (ret.contains("/"))){
//			
//			String[] splitRet = ret.split("/"); 
//				
//			ret = "";
//					
//			for(int i = 0 ; i < splitRet.length-1 ; i++){
//				ret+=splitRet[i]+"\\\\/";
//			}
//			if(splitRet.length-1>=0){
//				ret+=splitRet[splitRet.length-1];
//			}
//			
//		}
		
		return ret;
	}
	
	/**
	 * <pre>
	 * org.eclipse.ui.views.nl_ko_4.6.0.v20161126060001.jar-org/eclipse/ui/internal/views/properties/messages_ko.properties
	 * 이런 형식의 key에서 _4.6.0.v20161126060001.jar 을 없애고
	 * 중복값 제거를 위해
	 *  
	 * <b>removeVersion</b>
	 * 	String removeVersion(String str)
	 * </pre>
	 * 
	 * @param 
	 */
	private String removeVersion(String str) {
		//org.eclipse.ui.views.nl_ko_4.6.0.v20161126060001.jar-org/eclipse/ui/internal/views/properties/messages_ko.properties
		
		if(str.contains(".jar")) { // jar 파일인경우 
			str = str.replace(".jar", ""); // 먼저 .jar 없앰
			
			int start_point = str.indexOf("_");
			int end_point = str.indexOf("-");
			//_ko_4.6.0.v20161126060001.jar-
			// 위의 이것을 버리기위하여
			
			
			if((start_point>=0) && (end_point>0)){//만약에 해당되는게 있으면
				str = str.replace(str.substring(start_point, end_point),"");
			}
			
			end_point = str.indexOf("-");
			int last_point = str.lastIndexOf("/");
			//-org/eclipse/ui/internal/views/properties
			//위의 것을 버리기위하여
			
			
			if((last_point>=0) && (end_point>0)){//만약에 해당되는게 있으면
				str = str.replace(str.substring(end_point, last_point),"");
			}
			
			return "jar_"+str; // 리턴
			
		}
		else { // jar 파일이 아닌경우 
			//com.codescroll.gp.log_1.0.0.201709271840\OSGI-INF\l10n\bundle_ko_KR.properties
			int start_point = str.indexOf("_"); // 위에 ' _ ' 시작 뒤는 버전이 나오므로 시작 포인트는 ' _ '
			int end_point = str.indexOf("\\"); // 마지막 포인트는 ' \ '   결론 >> _1.0.0.201709271840  삭제
			
			if((start_point>0) && (end_point>0)){
				return str.replace(str.substring(start_point, end_point),"");
			}
		}
		
		return str;
	}
	
	/**
	 * <pre>
	 * 특수문자 제거 혹은 내가 직접 걸러내어야 겠다고 생각한 단어들
	 * </pre>
	 */
	private String validation(String str) {
		str = str.replace("plugins", "");
		str = str.replace("plugin", "");
		str = str.replace(".Alpha", "");
		str = str.replace(".properties", "");
		str = str.replace("_ko_KR", "");
		str = str.replace("_ko", "");

		str = str.replace("*", "_");
		str = str.replace("(", "");
		str = str.replace(")", "");
		str = str.replace(":", "");
		str = str.replace("-", "");
		str = str.replace("<", "");
		str = str.replace(">", "");
		str = str.replace(" ", "");
		str = str.replace("?", "");
		str = str.replace("!", "");
		str = str.replace("	", "");
		str = str.replace(",", "");
		str = str.replace("+", "");
		str = str.replace("[", "");
		str = str.replace("]", "");
		
		str = str.replace("\\", ".");
		str = str.replace("/", ".");
		
		return str;
	}
	
	/**
	 * <pre>
	 * 1. key안에 한글이 있을경우
	 * 2. 위의 기본 validation을 거치기 위하여
	 * 
	 * <b>validationkey</b>
	 * 	private String validationKey(String key)
	 * </pre>
	 * 
	 */
	private String validationKey(String key) {
		key = this.validation(key);
		
		//한글 들어가있는지 체크 
		char current = 0;
		StringBuilder checkKorean = new StringBuilder();
		for(int i = 0 ; i < key.length() ; i ++) {
			current = key.charAt(i);
			if ((current >= 0x0020) && (current <= 0x007e)) {
				//한글이 아닌경우 pass
				checkKorean.append(current);
			}else {
				checkKorean.append(".");
			}
		}
		key = checkKorean.toString();
		return key;
	}
	
	
	/**
	 * 받은 경로에서 불필요한 요소를 삭제하기 위하여 
	 * 
	 */
	private String validationPath(String path) {
		
		//첫 경로 삭제
		//path = path.substring(UserInfo.CodeScrollFilePath.length()+1, path.length());
		
		// 버전삭제
		path = removeVersion(path);
		
		//기본 버려야할것들
		path = this.validation(path);
		
		return path;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		//this.setValue_korean(ProgramData.converter.convertUnicodeToKorean(this.value));
					
	}

	public String getValue_korean() {
		return valueKorean;
	}

	public void setValue_korean(String value_korean) {
		this.valueKorean = value_korean;
	}

	public boolean getPath() {
		return path;
	}

	public void setPath(boolean path) {
		this.path = path;
	}

	
	
	
	
}
