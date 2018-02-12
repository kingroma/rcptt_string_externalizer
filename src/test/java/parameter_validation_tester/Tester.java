package parameter_validation_tester;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.java.parameter.ParameterValidation;
import com.java.project.RcpttProject;
import com.java.property.ProductProperties;
import com.java.rcptt.RObject;
import com.java.replacement.ReplacementHelper;
import com.java.util.ProgramData;
import com.java.util.UserInputData;

public class Tester {
	//실행 : alt shift x + t
	
	@Test
	public void 유효성_검사_KEY_VALUE(){
		String inputKey = "김정진`~!@,#$%^&*()><?|\\/plugins plugin .Alpha .properties _ko_KR _ko";
		String key = ParameterValidation.getValidationKey(inputKey);
		assertEquals(key , "..");
		
		
		String inputValue = "  \\n 완료(&F)";
		String value = ParameterValidation.getValidationValue(inputValue);
		assertEquals(value , "\\n 완료(F)");
	}
	
	@Test
	public void 프로퍼티스_파일_읽기_테스트(){
		UserInputData.getInstance();
		ProductProperties readProperties = new ProductProperties(UserInputData.CTPath);
		readProperties.read();	
	}
	
	@Test
	public void 프로젝트_파일_읽기_테스트(){
		UserInputData.getInstance();
		RcpttProject project = new RcpttProject(UserInputData.ProjectPath);
		project.read();
	}
	
	@Test
	public void 치환_테스트(){
		ProgramData.getInstance();
		
		RcpttProject project = new RcpttProject("TestSet");
		project.read();
		
		assertEquals(project.getObjects().size(),2);
		
		ProgramData.getInstance().setProject(project);
		
		RObject ctx = new RObject("TestSet/parameter.ctx");
		ctx.read();
		assertEquals(ctx.getEclScript(),null);
		
		ReplacementHelper helper = new ReplacementHelper();
		helper.replce();
		
		
	}
}
