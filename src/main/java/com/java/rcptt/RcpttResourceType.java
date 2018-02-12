package com.java.rcptt;

/**
 * RCPTT 파일안의 타입을 나누기 하여
 * enum 형식을 사용하였습니다.
 * @author suresoft
 */
public enum RcpttResourceType {
	DEFAULT, 
	TESTCASE, // Testcase 인 경우 
	ECL,  //  ECL 스크립트 인 경우 
	PARAMETER,  // 파라미터 인 경우 
	DESCRIPTION  // 설명 인 경우 
}
