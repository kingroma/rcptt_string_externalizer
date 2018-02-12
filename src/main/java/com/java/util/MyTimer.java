package com.java.util;

import java.util.Date;

/**
 * 프로세스 진행 시간을 확인하는 class 입니다.
 * @author suresoft
 *
 */
public class MyTimer {
	/**
	 * 시작 시간을 담을 Date 형 변수
	 */
	private Date startTime = null;
	
	/**
	 * 끝나는 시간을 담을 Date 형 변수 
	 */
	private Date endTime = null;
	
	/**
	 * 시작과 끝의 차를 담을 long형 변수
	 */
	private long lTime ; 
	
	/**
	 * 타이머 형식으로 정지 시작에서 그 차를 담을 long형 변수입니다.
	 */
	private long addTime;
	
	
	public MyTimer() {
		
	}
	
	/**
	 * 타이머 시작 
	 */
	public void start() {//타이머 시작
		this.startTime = new Date();
	}
	
	/**
	 * 타이머 끝 
	 */
	public void end() {//타이머 끝
		this.endTime = new Date();
		this.lTime = endTime.getTime() - startTime.getTime();
		
		Logger.write("TIME : " + lTime / 1000.0 + "(s)");
	}
	
	/**
	 * 타이 머시작
	 */
	public void startAdd(){
		this.startTime = new Date();
	}
	
	/** 
	 * 타이머 정지 
	 */
	public void pauseAdd(){
		this.endTime = new Date();
		this.addTime +=  endTime.getTime() - startTime.getTime();
	}
	
	/**
	 * 총 시간 출력
	 */
	public void printAdd(){
		Logger.write("TIME : " + addTime / 1000.0 + "(s)");
	}
	
	
}
