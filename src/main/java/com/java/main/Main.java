package com.java.main;

import com.java.controller.Controller;
import com.java.util.HelpMessage;
import com.java.util.MyTimer;
import com.java.util.CheckProgramIniAndPropertiesFile;

/**
 * <pre>
 * <b>RCPTT 문자열 스크립트 자동 치환 시스템</b>
 * </pre>
 * @author 김정진 인턴 / 정성일 선임님
 * 기본 찾아보기
 * 
 */
public class Main {
	
	
	
	// TODO 다음주 월요일 까지
	// TODO git.eclipse.org/c/rcptt/org.eclipse.rcptt.git
	// TODO equals 너무 많이씁니다.11618 22719
	// TODO 다 확인하고 나서 . 
	// exe
	// ini 카피
	// 스크립트 같이 포함하여서 git hub로 올리기 
	// 메인빌드 하면
	// ini 파일하나랑 그외 파일 외부화된것이랑 exe랑 같이 올라오게 .
	// 결과물이 허드슨에 붙였을때 정상 작동하는지 확인해야함 
	// TODO naming
	// TODO [RCPTT API 적용이 가능하면 상관없음] 이거 하나로 묶어서 진행할 수 있게 해보기
	// 두번 읽어서 하는것보다 한번 읽어서 진행하기
	// 함수안에서 모든것을 하는게 좋으며 중간에 에러가있을경우 멈춰야함.
	// 코드 리딩 이 어려움 언제 어디서 바꾸는지 확인이 어려움
	// read return 해서 하는게 좋을것 같습니다.
	// 정확히 파악 해야함
	// 추정은 여러가지 해보고 확인해보아야함
	// 띄어쓰기는 뉴 라인 여러개 확인해보기
	
	
	/**
	 * 메인 프로그램 루트
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




