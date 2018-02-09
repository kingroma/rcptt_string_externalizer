package com.java.util;

/**
 * 시작시 parameter 가 -help 로 들어왔을경우
 * not yet
 * @author suresoft
 *
 */
public class Help {
	
	private StringBuilder sb;
	public Help(){
		this.setStringBuilder();
	}
	public void msg(){
		Logger.write(sb.toString());
	}
	
	
	private void setStringBuilder(){
		
		this.sb = new StringBuilder();
		
		sb.append("\t\tRCPTT 문자열 스크립트 외부화 \n");
		sb.append("\t\t\t\t\t\t김정진 인턴 / 정성일 선임님\n");
		sb.append("\n");
		
		sb.append("먼저  [rcptt++.ini] 파일이 작성되어 있어야 합니다. (프로그램이 있는 폴더 위치에 ini 파일이 없을시 자동생성)\n");
		sb.append("유저로부터 프로그램이 지정된 경로를 확인하기 위하여 작성되어야 하며\n");
		sb.append("내용은 아래와 같습니다.\n");
		sb.append("\n");
		
		sb.append("#-- RCPTT 자동화 기본 경로 설정 파일 --\n");
		sb.append("\n");
		sb.append("#language=ko, ko_KR, en_US  언어 선택\n");
		sb.append("language=ko\n");
		sb.append("\n");
		sb.append("#CodeScrollFilePath=CS Path 코드스크롤 경로\n");
		sb.append("CodeScrollFilePath=");
		sb.append("\n");
		sb.append("#ProjectPath=ecl 스크립트가 있는 파일 경로\n");
		sb.append("ProjectPath=\n");
		sb.append("\n");
		sb.append("#ParameterCtxPath=파라미터가 들어있는 혹은 빈 ctx 파일 경로\n");
		sb.append("ParameterCtxPath=\n");
		sb.append("\n");
		sb.append("#  << - 이 들어간 경우는 주석");
		
		
		sb.append("프로세스 동작 순서\n");
		sb.append("1. properties 파일 읽어오기\n");
		sb.append("2. 파라미터가 들어있는 ctx파일 읽어오기\n");
		sb.append("3. RCPTT Project 읽어오기\n");
		sb.append("4. 치환 진행하며 치환된 파일은 저장하기\n");
		sb.append("프로세스 종료");
		
		sb.append("\n");
		sb.append("사용자 지정 파라미터 설정\n");
		sb.append("파일 이름 : defaultParameter.properties\n");
		sb.append("(만약 파일이 없을경우 프로그램 폴더에 자동 새로 생성됩니다.)\n");
		sb.append("\n");
		sb.append("properties파일이며 CT제품에서 확인되지 않는 Properties를 넣기 위한 파일입니다.\n");
		sb.append("properties양식대로 key = value 넣어주시면 치환시 자동 사용됩니다.\n");
		sb.append("\n");
		
	}
}
