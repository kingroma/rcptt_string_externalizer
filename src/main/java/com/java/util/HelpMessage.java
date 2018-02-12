package com.java.util;

/**
 * 시작시 parameter 가 -help 로 들어왔을경우 
 * 내용을 출력하며 종료합니다.
 * @author suresoft
 */
public class HelpMessage {
	
	/**
	 * 메시지를 저장할 Stringbuilder입니다.
	 */
	private StringBuilder helpMessageStringBuilder;
	
	/**
	 * 생성과 동시에 stringbuilder의 내용을 저장합니다.
	 */
	public HelpMessage(){
		this.setStringBuilder();
	}
	
	/**
	 * messsage의 내용을 출력합니다.
	 */
	public void msg(){
		Logger.write(helpMessageStringBuilder.toString());
	}
	
	/**
	 * stringbuilder를 이용하여 
	 * 도움을 주는 내용들을 넣습니다.
	 */
	private void setStringBuilder(){
		
		String term = "\n\n=========================================================================\n\n";
		
		this.helpMessageStringBuilder = new StringBuilder();
		
		helpMessageStringBuilder.append(term);
		
		helpMessageStringBuilder.append("--- Help --- \n");
		helpMessageStringBuilder.append("\t\tRCPTT 문자열 스크립트 외부화 \n");
		helpMessageStringBuilder.append("\t\t\t\t\t\t김정진 인턴 / 정성일 선임님\n");
		helpMessageStringBuilder.append("\n");
		
		helpMessageStringBuilder.append(term);
		
		helpMessageStringBuilder.append("먼저  [rcptt++.ini] 파일이 작성되어 있어야 합니다. (프로그램이 있는 폴더 위치에 ini 파일이 없을시 자동생성)\n");
		helpMessageStringBuilder.append("유저로부터 프로그램이 지정된 경로를 확인하기 위하여 작성되어야 하며\n");
		helpMessageStringBuilder.append("내용은 아래와 같습니다.");
		
		helpMessageStringBuilder.append(term);
		
		helpMessageStringBuilder.append("#-- RCPTT 자동화 기본 경로 설정 파일 --\n");
		helpMessageStringBuilder.append("\n");
		helpMessageStringBuilder.append("#language=ko, ko_KR, en_US  언어 선택\n");
		helpMessageStringBuilder.append("language=ko\n");
		helpMessageStringBuilder.append("\n");
		helpMessageStringBuilder.append("#CodeScrollFilePath=CS Path 코드스크롤 경로\n");
		helpMessageStringBuilder.append("CodeScrollFilePath=\n");
		helpMessageStringBuilder.append("\n");
		helpMessageStringBuilder.append("#ProjectPath=ecl 스크립트가 있는 파일 경로\n");
		helpMessageStringBuilder.append("ProjectPath=\n");
		helpMessageStringBuilder.append("\n");
		helpMessageStringBuilder.append("#ParameterCtxPath=파라미터가 들어있는 혹은 빈 ctx 파일 경로\n");
		helpMessageStringBuilder.append("ParameterCtxPath=\n");
		helpMessageStringBuilder.append("\n");
		helpMessageStringBuilder.append("#  << - 이 들어간 경우는 주석");
		
		helpMessageStringBuilder.append(term);
		
		helpMessageStringBuilder.append("프로그램 실행시 인자로 받을 경우엔\n");
		helpMessageStringBuilder.append("\n");
		helpMessageStringBuilder.append("1. language\n");
		helpMessageStringBuilder.append("2. CodeScrollFilePath\n");
		helpMessageStringBuilder.append("3. ProjectPath\n");
		helpMessageStringBuilder.append("4. ParameterCtxPath\n");
		helpMessageStringBuilder.append("순서대로 입력해주셔야 합니다.\n");
		helpMessageStringBuilder.append("\n");
		
		helpMessageStringBuilder.append(term);
		
		helpMessageStringBuilder.append("#-- RCPTT++ 자동화 프로세스 동작 순서\n");
		helpMessageStringBuilder.append("1. properties 파일 읽어오기\n");
		helpMessageStringBuilder.append("2. 파라미터가 들어있는 ctx파일 읽어오기\n");
		helpMessageStringBuilder.append("3. RCPTT Project 읽어오기\n");
		helpMessageStringBuilder.append("4. 치환 진행하며 치환된 파일은 저장하기\n");
		helpMessageStringBuilder.append("프로세스 종료");
		
		helpMessageStringBuilder.append(term);
		
		helpMessageStringBuilder.append("#-- RCPTT++ 사용자 지정 파라미터 설정\n");
		helpMessageStringBuilder.append("파일 이름 : defaultParameter.properties\n");
		helpMessageStringBuilder.append("(만약 파일이 없을경우 프로그램 폴더에 자동 새로 생성됩니다.)\n");
		helpMessageStringBuilder.append("\n");
		helpMessageStringBuilder.append("properties파일이며 CT제품에서 확인되지 않는 새로운 키를 넣기 위한 파일입니다.\n");
		helpMessageStringBuilder.append("properties양식대로 key = value 넣어주시면 치환시 자동 사용됩니다.\n");
		helpMessageStringBuilder.append("예 ) kim=jeong jin");
		
		helpMessageStringBuilder.append(term);
		
		helpMessageStringBuilder.append("#-- RCPTT++ 추가 치환시 파일 확장자명 설정\n");
		helpMessageStringBuilder.append("파일 이름 : extensionList.properties\n");
		helpMessageStringBuilder.append("(만약 파일이 없을경우 프로그램 폴더에 자동 새로 생성됩니다.)");
		helpMessageStringBuilder.append("\n");
		helpMessageStringBuilder.append("\n key=value 형식으로 properties 파일과 똑같이 작성해주시면 됩니다.");
		helpMessageStringBuilder.append("\n 예 ) source_c=.c");
		
		
	}
}
