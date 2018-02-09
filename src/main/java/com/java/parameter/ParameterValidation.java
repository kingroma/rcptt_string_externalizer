package com.java.parameter;

import java.util.regex.Pattern;

import com.java.util.UserInfo;

/**
 * ProgramData 클래스에 Parameter를 넣을때
 * key 와 value 와 path 값의 유효성 체크를 위한 클래스
 * @author suresoft
 *
 */
public class ParameterValidation {
	/**
	 * 단축키 [[ 완료(&F) ]] 와 같은 문자열에서 & 를 제거하기 위한 정규식입니다.
	 */
	private static final String VALUE_SHORTCUT_REGEX = ".*\\(&[A-Z]\\).*";

	/**
	 * key에 한글이 들어있는지 , RCPTT 에서 인식하지 못하는 특수문자가 있는지 띄어쓰기가 있는지 등을 확인합니다.
	 */
	private static final String KEY_VALIDATION_REGEX = "[가-힣 \t`~!@#$%^&*()+=\\-,:?<>|]";
	
	/**
	 * 생성자 숨기기
	 */
	private ParameterValidation(){

	}


	/**
	 * key 의 유효성을 검사하는 메소드
	 * 1. RCPTT에서 인식못하는 특수 문자를 제거합니다.
	 * 2. 한글을 제거합니다.
	 * @param key
	 * @return
	 */
	public static String getValidationKey(String key){
		key = getBasicValidation(key);

		return key;
	}

	/**
	 * Value의 유효성을 검사하는 메소드
	 * [[ 완료(&F) ]] 같은 단축키 기능을 하는 문자열은 [[ & ]] 를 제거해줍니다.
	 * @param value
	 * @return
	 */
	public static String getValidationValue(String value){
		value = value.trim();
		if(value.contains("&")){
			if(Pattern.matches(VALUE_SHORTCUT_REGEX,value)){// 먼저 (&F) 와 같은 패턴을 검사합니다
				value = value.replace("&", "");
			}
		}

		value = value.replace("\n", "\\n"); // properties에서 불러오는 뉴 라인을 변경하기위하여 추가하였습니다.

		return value;
	}

	/**
	 * Path의 유효성을 검사하는 메소드
	 * 1. 경로에서 기본 경로를 제거합니다.
	 * 2. v1223.342같은 버전 문자열 페턴을 제거합니다.
	 * 3.
	 * @param path
	 * @return
	 */
	public static String getValidationPath(String path){

		path = path.substring(UserInfo.getInstance().getCTPath().length()+1, path.length());

		path = removeVersion(path);

		path = getBasicValidation(path);

		return path;
	}


	/**
	 * RCPTT에서 확인이 어려운 특수문자와
	 * 몇몇가지의 문자열패턴을 제거합니다.
	 * @param str
	 * @return
	 */
	private static String getBasicValidation(String str){
		
		str = str.replace("plugins", "");
		str = str.replace("plugin", "");
		str = str.replace(".Alpha", "");
		str = str.replace(".properties", "");
		str = str.replace("_ko_KR", "");
		str = str.replace("_ko", "");
		
		//특수문자 , 한글등 제거
		str = str.replaceAll(KEY_VALIDATION_REGEX,"");
		
		str = str.replace("\\", ".");
		str = str.replace("/", ".");

		return str;
	}

	/**
	 * 경로를 받아와 경로에 있는 문자열중 v123123.123
	 * 버전과 관련되어있는 문자열을 제거하는 메소드
	 * 하드코딩 되어있습니다.
	 * @param str
	 * @return
	 */
	private static String removeVersion(String str){
		// org.eclipse.ui.views.nl_ko_4.6.0.v20161126060001.jar-org/eclipse/ui/internal/views/properties/messages_ko.properties
		// 버전이 들어가있는 형태는 위와 같습니다.

		if (str.contains(".jar")) { // jar파일인 경우의 조건입니다.
			str = str.replace(".jar", ""); // 먼저 .jar 문자열을 없앱니다.

			int startPoint = str.indexOf("_");
			int endPoint = str.indexOf("-");
			// _ko_4.6.0.v20161126060001.jar-
			// 위의 ko 와 4.6.0 등의 문자열을 버리기 위하여 진행합니다.

			if ((startPoint >= 0) && (endPoint > 0)) {// 만약에 해당되는게 있을 경우에
				str = str.replace(str.substring(startPoint, endPoint), "");
			}

			endPoint = str.indexOf("-");
			int lastPoint = str.lastIndexOf("/");
			// -org/eclipse/ui/internal/views/properties
			// 위의 문자열을 버리기위하여 진행합니다.

			if ((lastPoint >= 0) && (endPoint > 0)) {// 만약에 해당되는게 있으면
				str = str.replace(str.substring(endPoint, lastPoint), "");
			}

			return "jar_" + str;

		} else { // jar 파일이 아닌경우
					// com.codescroll.gp.log_1.0.0.201709271840\OSGI-INF\l10n\bundle_ko_KR.properties
			int startPoint = str.indexOf("_"); // 위에 ' _ ' 시작 뒤는 버전이 나오므로 시작
												// 포인트는 ' _ '
			int endPoint = str.indexOf("\\");
			// 마지막 포인트는 ' \ '  >>   _1.0.0.201709271840 삭제

			// [[ - ]] ~~~ [[ \\ ]] 의부븐을 삭제합니다.
			if ((startPoint > 0) && (endPoint > 0)) {
				return str.replace(str.substring(startPoint, endPoint), "");
			}
		}

		return str;
	}
}
