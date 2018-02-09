package com.java.util;

import java.util.Date;

/**
 * 프로세스 진행 시간을 확인하는 class 입니다.
 * @author suresoft
 *
 */
public class MyTimer {
	private Date startTime = null;
	private Date endTime = null;
	private long lTime ; 
	private long addTime;
	
	public MyTimer() {
		this.addTime = 0;
	}
	
	public void start() {//타이머 시작
		this.startTime = new Date();
	}
	
	public void end() {//타이머 끝
		this.endTime = new Date();
		this.lTime = endTime.getTime() - startTime.getTime();
		
		Logger.write("TIME : " + lTime / 1000.0 + "(s)");
	}
	
	public void startAdd(){
		this.startTime = new Date();
	}
	
	public void pauseAdd(){
		this.endTime = new Date();
		this.addTime +=  endTime.getTime() - startTime.getTime();
	}
	
	public void printAdd(){
		Logger.write("TIME : " + addTime / 1000.0 + "(s)");
	}
	
	
}
