package com.java.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.java.project.RcpttProject;
import com.java.property.ProductProperties;
import com.java.rcptt.RObject;
import com.java.replacement.ReplacementHelper;
import com.java.util.Converter;
import com.java.util.Logger;
import com.java.util.MyTimer;
import com.java.util.ProgramData;
import com.java.util.UserInputData;


/**
 *
 * <pre>
 * <b>프로세스 순서</b>
 * 0. rcptt++.ini 파일을 읽는다
 * 1. CT에서 모든 .properties파일을 읽는다 [ Property ]
 * 2. 유저가 결정한 (rcptt++.ini에서) 특정한 parameter.ctx 파일을 읽는다 [ RObject ]
 * 3. rcptt 프로젝트를 읽는다. [ Project ]
 * 4. 치환한다  [ Substitution ]
 *
 * </pre>
 *
 * @see ReplacementHelper
 */
public class Controller {

	/**
	 * 총 프로그램이 실행되는 시간을 구하기 위하여 생성합니다.
	 */
	private MyTimer mainTimer = new MyTimer();

	/**
	 * 파라미터 ctx 파일을 가지고 있을 RObject 이며
	 * 추후 저장을 위하여 Controller에서 가지고있습니다.
	 */
	private RObject parameterCtx;

	/**
	 * process 의 시작을 하는 메소드입니다.
	 * 생성 후 process 메소드를 호출해야지 실행이 됩니다.
	 */
	public void process() {
		mainTimer.start();
		Logger.write("rcptt 문자열 스크립트 자동 치환 시스템 시작");
		Logger.write("");

 		// process 1 사용자로부터 입력받은 parameterCtx 경로에서 확인되는
		// parameter를 ProgramData에 있는 parameterMap에 저장합니다.
		readParameterCtx(); // process 1
		
		// process 2  사용자로부터 입력받은 CT 경로에서 확인되는
		// 모든 .properties 파일을 읽어와 ProgramData에 있는
		// parameterMap 에 추가합니다.
		readProperties(); // process 2

		// process 3 사용자로부터 입력받은 RCPTT Project 경로에서 확인되는
		// 모든 testcase와 ctx 파일을 읽어와 ProgramData에 있는
		// Project에 저장합니다.
		readProject(); // process 3

		// process 4
		// process 1과 process2에서 읽어온 parameter를 확인하여
		// process 3에서 읽은 testcase 파일에 있는 문자열을 $key
		// 형식으로 치환합니다.
		replaceStringToKey(); // process 4

		// 마지막으로 parameterMap에있는 모든 정보들을 저장합니다.
		lastSave();


		Logger.write("");
		Logger.write("rcptt 문자열 스크립트 자동 치환 시스템 끝");

		mainTimer.end();

	}

	/**
	 * <pre>
	 * <b>process 1</b>
	 * 유저가 입력한 parameter 경로에서 parameter 를 읽어온 후
	 * 읽은 후 ProgramData에 있는 parameterMap에 저장합니다.
	 * </pre>
	 * @see Parameter
	 */
	private void readParameterCtx() { // process 1
		Logger.write("\nprocess 1 \n read prameter ctx file \n " + UserInputData.ParameterCtxPath);

		parameterCtx= new RObject(UserInputData.ParameterCtxPath);
		
	}

	/**
	 * <pre>
	 * <b>process 2</b>
	 * 유저가 입력한 CT 경로에서
	 * 모든 .properties파일을 읽는다
	 * 읽은 후 ProgramData에 있는 parameterMap에 저장합니다.
	 * </pre>
	 * @see ProductProperties
	 *
	 */
	private void readProperties() {
		Logger.write("\nprocess 2 \n read properties file \n " + UserInputData.CTPath);

		ProductProperties productProperties = new ProductProperties(UserInputData.CTPath); // Property property
		productProperties.read();
	}



	/**
	 * <pre>
	 * <b>process 3 </b>
	 * 유저가 입력한 RCPTT project 경로에서
	 * 모든 .ctx(ecl script)파일과 .test(testcase)파일을 읽어옵니다.
	 * </pre>
	 * @see __Project
	 */
	private void readProject() {
		String projectPath = UserInputData.ProjectPath;
		Logger.write("\nprocess 3\n read RCPTT project folder\n " + projectPath);
		RcpttProject rcpttPrj = new RcpttProject(projectPath);
		ProgramData.getInstance().setProject(rcpttPrj);
		rcpttPrj.read();
		Logger.write(" RCPTT 프로젝트에서 읽어온 파일 갯수 : " + ProgramData.getInstance().getProject().getObjects().size());
	}

	/**
	 * <pre>
	 * <b>process 4</b>
	 * 이전 process 들로부터 모아온 정보(parameter들)로
	 * project에 저장된 모든 ecl script를 확인하여
	 * key로 변경가능한지 확인 후 파일을 저장합니다.
	 * </pre>
	 * @see ReplacementHelper
	 */
	private void replaceStringToKey() {
		Logger.write("\nprocess 4 \n substitution");

		readProductProperties();

		ReplacementHelper replacementHelper = new ReplacementHelper(); // Substitution substitution
		replacementHelper.replce();
	}

	/**
	 * <pre>
	 * 마지막으로 파라미터를 사용자로부터 받은 parameter ctx 파일에
	 * 저장한다.
	 * </pre>
	 */
	private void lastSave() {
		Logger.write("\n parameter의 총 갯수 : "+ProgramData.getInstance().getParameterMap().size());
		Logger.write(" 최종 저장중 ..");

		parameterCtx.save();

	}

	/**
	 * CT제품에서 확인되지 않았던 문자열을
	 * 다른 외부화를 통하여 추가하는 방법입니다.
	 * 파일 이름은 defaultParameter.properties 입니다.
	 * 사용자가 추가하고 싶은 문자열이 있을경우 이 properties jar파일안에
	 * 입력해주시면 됩니다.
	 */
	private void readProductProperties(){
		String defaultParameterFileName = "defaultParameter.properties"; // 파일 이름

		InputStream is = null;
		Properties p = null;
		File defaultParameterFile = new File(defaultParameterFileName);

		if(defaultParameterFile.exists()){
			try {
				is = new FileInputStream(defaultParameterFile);
				p = new Properties();
				p.load(is);
				for(Object obj : p.keySet()){
					String key = (String)obj;
					ProgramData.getInstance().addParameter("default_name_"+key, Converter.convertKoreanToUnicode(p.getProperty(key)));
				}
				is.close();
			} catch (IOException exception) {
				Logger.write("read defaultParameter.properties error");
			}
		}else{
			Logger.write("create defaultparameter.properties");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(defaultParameterFile);
				fos.write("".getBytes());
				fos.close();
			} catch (IOException exception) {
				Logger.write("read defaultParameter.properties output stream error");
			}
		}
	}


}
