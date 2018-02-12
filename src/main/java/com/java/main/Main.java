package com.java.main;

import com.java.controller.Controller;
import com.java.util.HelpMessage;
import com.java.util.CheckProgramIniAndPropertiesFile;

/**0  /
 * <pre>
 * <b>RCPTT 문자열 스크립트 자동 치환 시스템 </b>
 * rcptt++
 * </pre>
 * @author 김정진 인턴 / 정성일 선임님
 */
public class Main {
	// TODO : 코드 정리 코드 삭제, 커밋   // RCPTT API 사용하여 ctx, test 파일 읽기

	// TODO 다 확인하고 나서 . 
	// 1. exe, ini 카피, 스크립트 같이 포함하여서 git hub로 올리기
	// 2. 메인빌드 하면 ini 파일하나랑 그외 파일 외부화된것이랑 exe랑 같이 올라오게 .
	// 3. 결과물이 허드슨에 붙였을때 정상 작동하는지 확인해야함
	
	/**
	 * 메인 프로그램 시작 루트 
	 * @param args
	 */
	public static void main(String[] args) {
		if(CheckProgramIniAndPropertiesFile.createFile()){
			if(args.length==0) {
				Controller controller = new Controller();
				controller.process();
			}
			else if(args[0].equals("-help")) {
				HelpMessage help = new HelpMessage();
				help.msg();
			}
		}else{
			HelpMessage help = new HelpMessage();
			help.msg();
		}
	}
}




