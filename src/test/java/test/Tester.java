package test;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.java.parameter.ParameterValidation;
import com.java.property.ProductProperties;
import com.java.rcptt.RObject;
import com.java.rcptt.RObjectContent;
import com.java.rcptt.RcpttResourceType;
import com.java.replacement.ReplacementHelper;
import com.java.util.Converter;
import com.java.util.ProgramData;
import com.java.util.UserInputData;

public class Tester {
	// jUnit 실행 단축키 : alt shift x + t
	
	@Test
	/**
	 * 현재 파일에 있는 TestSet에서 
	 * Test_CT_ko_.properties파일을 읽어 옵니다.
	 * 읽어 와 key value로 나눈 뒤 
	 * 정상적으로 map에 잘들어갔는지 확인해봅니다.
	 */
	public void 프로퍼티_파일_읽기(){
		String path = "C:\\Users\\suresoft\\eclipse-workspace2\\RcpttProject\\TestSet";
		UserInputData.getInstanceNoIniEnv();
		UserInputData.CTPath=path;
		UserInputData.Language="ko";
		
		
		ProductProperties properties = new ProductProperties(path);
		properties.read();
		
		Map<String,String> map = ProgramData.getInstance().getParameterMap();
		
		assertEquals(map.get("properties_kim"),"Test_CT__jeongjin");
	}
	
	@Test
	/**
	 * 확인 대상 [[ ParameterValidation ]]
	 * key와 value값의 유효성검사 후 나온 값을 확인합니다.
	 * key의경우 특수문자 / 한글 / 정해진 패턴이 사라지거나 변경되어야합니다.
	 * value의 경우 trim() 메소드가 적용되며 단축키 문자열인경우 & 가 사라져야합니다.
	 */
	public void 유효성_검사_KEY_VALUE_테스트(){
		String inputKey = "김정진`~!@,#$%^&*()><?|\\/  .Alpha .properties _ko_KR _ko";
		String key = ParameterValidation.getValidationKey(inputKey);
		assertEquals(key , "..");
		
		
		String inputValue = "  \\n 완료(&F)";
		String value = ParameterValidation.getValidationValue(inputValue);
		assertEquals(value , "\\n 완료(F)");
	}

	@Test
	/**
	 * 확인 대상 [[ RObject ]]
	 * Test.test ecl script를 읽어와 ecl script의 내용이 일치하는지 확인합니다
	 * 현재 들어있는 내용은  [ \nget-button $rcptt\n\n ] 입니다.
	 */
	public void ROject_테스트(){
		RObject obj = new RObject("TestSet/Test.test");
		obj.read();
		assertEquals(obj.getEclScript(),"\nget-button $rcptt \"suresofttech\"\n\n\n");
		
	}

	@Test
	/**
	 * 확인 대상 [[ RObjectContent ]]
	 * content내용을 넣어 저장과정에 나오는 포멧이 잘 적용되었는지를 확인합니다
	 */
	public void RObjectContent_테스트(){
		/**
		------=_.content-0a7243a0-75d3-3d5f-9791-539de0e5b7ac
		Content-Type: text/ecl
		Entry-Name: .content

		get-button $rcptt

		------=_.content-0a7243a0-75d3-3d5f-9791-539de0e5b7ac--
		 */
		RObjectContent content = new RObjectContent(
				"------=_.content-0a7243a0-75d3-3d5f-9791-539de0e5b7ac",
				"Content-Type: text/ecl",
				"Entry-Name: .content",
				"\n\njeongjinkim\n\n",
				"------=_.content-0a7243a0-75d3-3d5f-9791-539de0e5b7ac--",
				false);
		assertEquals(content.getType(),RcpttResourceType.ECL);
		assertEquals(content.output(),
				"------=_.content-0a7243a0-75d3-3d5f-9791-539de0e5b7ac\n"
				+ "Content-Type: text/ecl\nEntry-Name: .content\n"
				+ "\n\njeongjinkim\n\n"
				+ "------=_.content-0a7243a0-75d3-3d5f-9791-539de0e5b7ac--\n");
	}

	@Test
	/**
	 * 확인 대상 [[ Converter ]]
	 * 유니코드 -> 한글 
	 * 한글 -> 유니코드가 잘되는지 확인합니다.
	 */
	public void 유니코드_테스트(){
		String unicode = Converter.convertKoreanToUnicode("김정진");
		unicode = Converter.convertUnicodeToKorean("김정진");
		
		assertEquals(unicode,"김정진");
	}
	
	@Test
	/**
	 * 파라미터인 key와 value를 추가하였을때
	 * 생성되는 데이터를 확인하는 테스트 메소드입니다.
	 */
	public void 파라미터_유효성_검사_및_추가_데이터_생성_확인(){
		//초기 initEnv()
		ProgramData program = ProgramData.getInstance();
		UserInputData user = UserInputData.getInstanceNoIniEnv();
		String key = "";
		String value = "";
		user.CTPath="";

		//=======================================================================//
		
		/**
		 * key에는 한글과 특수문자 [ ! / ] 가 사라져야합니다.
		 * value에서는 특수문자가 있을 경우 추가적인 [ \\ ] 백슬래쉬가 추가되며
		 * value에서 슬래쉬 [ / ] 를 만났을 경우 split하여 나뉘어 지는 key도 있어야합니다.
		 */
		key = "김정진! C/C++ 프로젝트";
		value = "김정진! C/C++ 프로젝트";
		Map<String,String> parameterMap = program.getParameterMap();
		program.addParameter(key, value," path",true);

//		>> 결과 <<
//		path_C.C / 김정진! C/C++ 프로젝트
//		path_C.C_AddSplitSlash_1 / C++ 프로젝트
//		path_C.C_AddSplitSlashAndBackSlash_1 / C\\+\\+ 프로젝트
//		path_C.C_AddBackSlash / 김정진\\! C\\/C\\+\\+ 프로젝트
//		path_C.C_AddSplitSlashAndBackSlash_0 / 김정진\\! C\\
//		path_C.C_AddSplitSlash_0 / 김정진! C
		
		assertEquals(parameterMap.get("김정진! C/C++ 프로젝트"),
				"path_C.C");
		assertEquals(parameterMap.get("C++ 프로젝트"),
				"path_C.C_AddSplitSlash_1");
		assertEquals(parameterMap.get("C\\\\+\\\\+ 프로젝트"),
				"path_C.C_AddSplitSlashAndBackSlash_1");
		assertEquals(parameterMap.get("김정진\\\\! C\\\\/C\\\\+\\\\+ 프로젝트"),
				"path_C.C_AddBackSlash" );
		assertEquals(parameterMap.get("김정진\\\\! C\\\\"),
				"path_C.C_AddSplitSlashAndBackSlash_0");
		assertEquals(parameterMap.get("김정진! C"),
				"path_C.C_AddSplitSlash_0");

		//=======================================================================//
		
		/**
		 * [ ; ] 가 있을경우 2개를 나누어 map에서 저장합니다.
		 */
		key = "goodbad";
		value = "좋음;나쁨";
		program.addParameter(key, value," path",true);
		
//		>> 결과 <<
//		path_goodbad_Semicolon_1 / 나쁨
//		path_goodbad_Semicolon_0 / 좋음
		assertEquals(parameterMap.get("나쁨"),"path_goodbad_Semicolon_1");
		assertEquals(parameterMap.get("좋음"),"path_goodbad_Semicolon_0");

		//=======================================================================//
		
		/**
		 * {0}김정진1''{1}''김정진2{2}
		 * 이런 경우 {0} 에는 CT제품에서 사용자의 추가 행동에따라 입력되는 글자로
		 * ''{[0-9]}'' 이러한 경우 [ ' ]<< 양쪽에서 한개만 없애주면 됩니다. 
		 * 필요가 없음으로 삭제후 {0}-{9} 기준으로 split하여 나누어 가지고있습니다
		 * 이러한경우를 테스트합니다.
		 */
		key = "RandomValue1";
		value = "{0}김정진1''{1}''김정진2{2}김정진3\"{3}\" 김정진4";
		program.addParameter(key, value," path",true);
		
//		>> 결과 <<
//		path_RandomValue1_ContainRandomValue_RemoveNewLine0 / 김정진1'
//		path_RandomValue1_ContainRandomValue_RemoveNewLine1 / '김정진2
//		path_RandomValue1_ContainRandomValue_RemoveNewLine2 / 김정진3"
//		path_RandomValue1_ContainRandomValue_RemoveNewLine3 / " 김정진4
		
		assertEquals(parameterMap.get("김정진1'"),
				"path_RandomValue1_ContainRandomValue_RemoveNewLine0");
		assertEquals(parameterMap.get("'김정진2"),
				"path_RandomValue1_ContainRandomValue_RemoveNewLine1");
		assertEquals(parameterMap.get("김정진3\""),
				"path_RandomValue1_ContainRandomValue_RemoveNewLine2");
		assertEquals(parameterMap.get("\" 김정진4"),
				"path_RandomValue1_ContainRandomValue_RemoveNewLine3");

		//=======================================================================//
		
		/**
		 * get-label 확인 value 
		 * get-label의 경우 \n 이 없이 화면에 표출되어 assertion으로
		 * 확인할경우 문자열에 \n이 다 없어집니다
		 * 그것을 방지하기 위하여 \n을 제거하여 parametermap에 추가합니다.
		 */
		key = "get_label";
		value = "김정진\\n김정진1\\n김정진2\\n";
		program.addParameter(key, value," path",true);
		
//		>> 결과 <<
//		path_get_label_Original_And_RemoveNewLine / 김정진김정진1김정진2
//		path_get_label / 김정진\n김정진1\n김정진2\n
		
		assertEquals(parameterMap.get("김정진김정진1김정진2"),"path_get_label_Original_And_RemoveNewLine");
		assertEquals(parameterMap.get("김정진\\n김정진1\\n김정진2\\n"),"path_get_label");

		//=======================================================================//
		
		/**
		 * 특수문자가 있을경우 특수문자 앞에 [ \\ 슬래쉬 ] 를 추가하여
		 * parameterMap에 추가합니다
		 * RCPTT에서 -path 혹은 특수한 경우 모든 특수문자에 \\ 가 추가되어 들어간 경우를
		 * 방지하기 위함 입니다.
		 */
		key = "specialCharacter~!@#$%^&*()_+`=-\\,?><";
		value = "~!@#$%^&*()_+`=-\\,?><";
		program.addParameter(key, value," path",true);
		
//		>> 결과 <<
//		path_specialCharacter_._AddBackSlash / \\~\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\+\\`\\=\\-\\,\\?\\>\\<
//		path_specialCharacter_. / ~!@#$%^&*()_+`=-\,?><

		assertEquals(
				parameterMap.get("\\\\~\\\\!\\\\@\\\\#\\\\$\\\\%\\\\^\\\\&\\\\*\\\\(\\\\)\\\\_\\\\+\\\\`\\\\=\\\\-\\\\,\\\\?\\\\>\\\\<")
				,"path_specialCharacter_._AddBackSlash");
		assertEquals(parameterMap.get("~!@#$%^&*()_+`=-\\,?><"),"path_specialCharacter_.");
		
		
		
//		총 map에 들어있는 내용 출력 
//		for(String index : parameterMap.keySet()){
//			System.out.println(parameterMap.get(index) + " / " + index);
//		}
	}

	@Test
	/**
	 * class ReplacementHelper에서 
	 * 정상적으로 잘 치환되는지 확인합니다.
	 */
	public void 치환_테스트() {
		UserInputData user = UserInputData.getInstanceNoIniEnv();
		ProgramData program = ProgramData.getInstance();
		ReplacementHelper replace = new ReplacementHelper(false);

		//=======================================================================//
		
		/**
		 * 정상적으로 치환되어 >> 결과 <<가 
		 * [ $replace_test_1 ] 이 되는지 확인합니다. 
		 */
		String key = "replace_test_1";
		String value = "qwe";
		program.addParameter(key, value, true);
		
		assertEquals(replace.findKey("qwe"),"$replace_test_1");
		
		//=======================================================================//
		
		/**
		 * {0}안녕하세요{1}슈어소프트{2}
		 * 경우 => 
		 * 1. 안녕하세요
		 * 2. 슈어소프트
		 * 이렇게 2가지로 나뉘어 키가 되고
		 * 나눈 키가 잘 replace 되는지 확인합니다.
		 */
		key = "replace_test_2";
		value = "{0}안녕하세요{1}슈어소프트{2}"; 
		program.addParameter(key, value, " path", true);
		
		assertEquals(replace.findNewKey("안녕하세요") , 
				"[format \"%s\" $path_replace_test_2_ContainRandomValue_RemoveNewLine0]");
		assertEquals(replace.findNewKey("슈어소프트") , 
				"[format \"%s\" $path_replace_test_2_ContainRandomValue_RemoveNewLine1]");

		//=======================================================================//
		
		/**
		 * [ / ] 를 기준으로 나누는 key로 
		 * [ 김 ] [ 정 ] [ 진 ] 으로 나뉩니다.
		 * 그래서 김 , 정 , 진 의 키가 잘 적용되어
		 * replace 되는지 확인합니다.
		 */
		key = "replace_test_3";
		value = "김/정/진";
		program.addParameter(key, value, " path", true);

//		>> 결과 <<
//		$path_replace_test_3_AddSplitSlash_0
//		$path_replace_test_3_AddSplitSlash_1
//		$path_replace_test_3_AddSplitSlash_2
		
		assertEquals(replace.findKey("김") , "$path_replace_test_3_AddSplitSlash_0");
		assertEquals(replace.findKey("정") , "$path_replace_test_3_AddSplitSlash_1");
		assertEquals(replace.findKey("진") , 
				"$path_replace_test_3_AddSplitSlashAndBackSlash_2");

		//=======================================================================//
		
		/**
		 * 새로운 키가 생성되는지 확인합니다.
		 * extensionList.properties파일을 확인 한 후 
		 * 진행됩니다.
		 */
		replace = new ReplacementHelper();
		
		value = "KimJeongJin.c";
		assertEquals(replace.findNewKey(value), "$extension_KimJeongJin.c");

		//=======================================================================//
		
		/**
		 * 경로 정규 표현식 : ^[a-zA-Z]:\\\\.*[a-zA-Z0-9가-힣]
		 * 경로로 확인되는 경우 새로운 키로 만들어 치환합니다.
		 */
		value = "E:\\Sure\\Soft\\Tech";
		assertEquals(replace.findNewKey(value),"$path_E.Sure.Soft.Tech");

		//=======================================================================//
		
		/**
		 * 치환 테스트 의경우 전체 단어는 없지만 
		 * 띄어쓰기를 기준으로 나누어 key value값을 확인합니다. 
		 */
		key = "replace_test_4";	value = "치환";
		program.addParameter(key, value, " path", true);
		key = "replace_test_5";	value = "테스트";
		program.addParameter(key, value, " path", true);
		
		assertEquals(replace.findNewKey("치환 테스트"),
				"[format \"%s %s\" $path_replace_test_4 $path_replace_test_5]");

		//=======================================================================//
		
		/**
		 * 한글의 시작문자부터 한글자식 대조해보면서 확인합니다.
		 * 만약 변환이 가능한경우 [format "%s%s" $key1 $key2 ] 형식으로
		 * 반환받습니다.
		 */
		assertEquals(replace.findNewKey("치환테스트"),
				"[format \"%s%s\" $path_replace_test_4 $path_replace_test_5]");

		//=======================================================================//
		
		/**
		 * "ab"+"cd"의 경우 +를 없애고 
		 * 두개의 문자열을 합치는 메소드를 테스트 해봅니다.
		 */
		String text = "\"김정진\"\n\t\t\t\t\t\t\t\t\t\t + \t\t\t\t\t\t\t\t\t\t\"김정진2\"";
		assertEquals(replace.removePlusSignEcl(text),"\"김정진김정진2\"");
	}	
}
