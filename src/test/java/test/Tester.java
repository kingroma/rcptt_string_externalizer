package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.java.parameter.ParameterValidation;
import com.java.project.RcpttProject;
import com.java.property.ProductProperties;
import com.java.rcptt.RObject;
import com.java.rcptt.RObjectContent;
import com.java.rcptt.RcpttResourceType;
import com.java.replacement.ReplacementHelper;
import com.java.util.Converter;
import com.java.util.ProgramData;
import com.java.util.UserInputData;

public class Tester {
	//실행 : alt shift x + t
	
	/**
	 * 확인 대상 [[ UserInputData ]] 
	 * rcptt++.ini 파일을 잘 읽었는지 확인합니다.
	 *  
	 */
	@Test
	public void UserInputData_테스트(){
		UserInputData.getInstance();
		assertEquals(UserInputData.CTPath,"C:/Program Files (x86)/CodeScroll Controller Tester 3.0/plugins");
		assertEquals(UserInputData.Language,"ko");
		assertEquals(UserInputData.ProjectPath,"C:/Users/suresoft/rcpttWorkspace/RCPTT");
		assertEquals(UserInputData.ParameterCtxPath,"C:/Users/suresoft/rcpttWorkspace/RCPTT/RCPTT_QA/p.ctx");
	}
	
	/**
	 * 
	 * 확인 대상 [[ ProgramData ]]
	 * 파라미터 추가 후 서칭할때 validation을 거쳐 정확한 key가 나오는지 확인합니다.
	 */
	@Test
	public void ProgramData_테스트(){
		ProgramData.getInstance();
//		ProgramData.getInstance().addParameter("qwe정진", "qwe김",true);
		assertEquals(ProgramData.getInstance().searchKey("qwe김"),"qwe");
	}
	
	/**
	 * 확인 대상 [[ ParameterValidation ]]
	 * key와 value값의 유효성검사 후 나온 값을 확인합니다.
	 * key의경우 특수문자 / 한글 / 정해진 패턴이 사라지거나 변경되어야합니다.
	 * value의 경우 trim() 메소드가 적용되며 단축키 문자열인경우 & 가 사라져야합니다.
	 */
	@Test
	public void 유효성_검사_KEY_VALUE_테스트(){
		String inputKey = "김정진`~!@,#$%^&*()><?|\\/plugins plugin .Alpha .properties _ko_KR _ko";
		String key = ParameterValidation.getValidationKey(inputKey);
		assertEquals(key , "..");
		
		
		String inputValue = "  \\n 완료(&F)";
		String value = ParameterValidation.getValidationValue(inputValue);
		assertEquals(value , "\\n 완료(F)");
	}
	
	/**
	 * 확인대상 [[ ProductProperties ]]
	 * TestSet파일에 있는 properties파일이 잘 읽히는지 확인합니다.
	 * 확인 후 parameterMap의 정보가 담겼는지
	 * key와 value가 잘 나뉘었는지 한개의 케이스로 확인합니다.
	 */
	@Test
	public void 프로퍼티스_파일_읽기_테스트(){
		UserInputData.getInstance();
		ProductProperties readProperties = new ProductProperties("TestSet");
		readProperties.read();	
		assertNotNull(ProgramData.getInstance().getParameterMap());
		assertEquals(ProgramData.getInstance().searchKey("kim"),"_jeongjin");
		assertEquals(ProgramData.getInstance().getParameterMap().size(),3);
	}
	
	/**
	 * 확인 대상 [[ RcpttProject ]]
	 * TestSet폴더에 있는 ctx파일과 test 케이스들을 확인합니다
	 * 몇개가 들어있는지 확인합니다.
	 * parameter ctx파일은 ecl script가 아니므로 null이 되어야 합니다.
	 */
	@Test
	public void 프로젝트_파일_읽기_테스트(){
		
		RcpttProject project = new RcpttProject("TestSet");
		project.read();
		assertEquals(project.getObjects().size(),2);
		assertEquals(project.getObjects().get(0).getEclScript(),null);
	}
	
	/**
	 * 확인 대상 [[ ReplacementHelper ]]
	 * 치환 적용이 잘되는지를 확인합니다.
	 */
	@Test
	public void 치환_테스트(){
		ProgramData.getInstance();
		
		RcpttProject project = new RcpttProject("TestSet");
		project.read();
		ProgramData.getInstance().setProject(project);
		
		RObject ctx = new RObject("TestSet/parameter.ctx");
		ctx.read();
		assertEquals(ctx.getEclScript(),null);
		
		ReplacementHelper helper = new ReplacementHelper();
		helper.replce();	
		assertEquals(helper.getRcpttValueAmount(),1);
	}
	
	/**
	 * 확인 대상 [[ RObject ]]
	 * Test.test ecl script를 읽어와 ecl script의 내용이 일치하는지 확인합니다
	 * 현재 들어있는 내용은  [ \nget-button $rcptt\n\n ] 입니다.
	 */
	@Test
	public void ROject_테스트(){
		RObject obj = new RObject("TestSet/Test.test");
		obj.read();
		assertEquals(obj.getEclScript(),"\nget-button $rcptt \"suresofttech\"\n\n\n");
		
	}
	
	/**
	 * 확인 대상 [[ RObjectContent ]]
	 * content내용을 넣어 저장과정에 나오는 포멧이 잘 적용되었는지를 확인합니다
	 */
	@Test
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
	
	/**
	 * 확인 대상 [[ Converter ]]
	 * 유니코드 -> 한글 
	 * 한글 -> 유니코드가 잘되는지 확인합니다.
	 */
	@Test
	public void 유니코드_테스트(){
		String unicode = Converter.convertKoreanToUnicode("김정진");
		unicode = Converter.convertUnicodeToKorean("김정진");
		
		assertEquals(unicode,"김정진");
	}
	
	
}
