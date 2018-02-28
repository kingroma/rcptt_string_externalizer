package com.java.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.java.rcptt.RObject;
import com.java.util.ErrorMessage;
import com.java.util.UserInputData;

/**
 * <b>Project</b>
 * 사용자로부터 입력받은 RCPTT프로젝트에서 
 * 모든 RCPTT Object를 확인후 프로그램이 보관  
 * 
 * <b>Project(String)</b>
 * 	public Project(String path)
 * @author suresoft
 *
 */
public class RcpttProject {
	/**
	 * root경로를 담고 있을 String 형의 변수 
	 */
	private String rootPath;
	
	/**
	 * Project 루트 경로에서부터 모든 .test 파일과 .ctx파일을 담고 있을 ArrayList RObject 형식의 변수입니다.
	 */
	private ArrayList<RObject> rObjects; 
	 
	/**
	 * 생성시 path를 rootPath에 저장하며
	 * ArrayList RObject를 새로 생성한다.
	 * @param rootPath
	 */
	public RcpttProject(String rootPath) {
		
		this.rootPath = rootPath;
		this.rObjects = new ArrayList<RObject>();
	}
	
	/**
	 * 생성하였을때 받은 경로에서 부터 
	 * 모든 testcase와 ctx 파일을 읽어 ArrayList RObject 에 저장합니다.
	 */
	public void read() {
		try (Stream<Path> paths = Files.walk(Paths.get(rootPath))) {
		    paths
		        .filter(Files::isRegularFile)
		    	.forEach(this::setRObject);
		} catch (IOException exception) {
			ErrorMessage.getInstance().printErrorMessage("RcpttProject.read.properties.error");
		}
	}

	/**
	 * 각각의 파일의 확장자 명과 파라미터 파일이 아닌경우
	 * 오브젝트로 추가합니다.
	 * @param path
	 */
	private void setRObject(Path path){
		File file = path.toFile();
		
		if ((checkExtension(file)) && (!isParameterCtxFile(file) && (!isParameterCtxEnFile(file)))) { // 확장자명이 ctx , test 인경우 ,  이 파일이 parameter의 파일이 아닌경우를 확인합니다.
			this.rObjects.add(new RObject(file.getPath()));  // 현재 RObject ArrayList에 추가됩니다.
		}else {
			
		}
	}
	
	/**
	 * 이 파일이 사용자로부터 받은 파라미터의 파일인경우 무시합니다.
	 * @param file
	 * @return
	 */
	private boolean isParameterCtxFile(File file){
		return file.getAbsolutePath().equals(UserInputData.ParameterCtxPath);
	}
	
	/**
	 * 이 파일이 사용자로부터 받은 영문 파라미터의 파일인경우 무시합니다. 
	 * @param file
	 * @return
	 */
	private boolean isParameterCtxEnFile(File file){
		return file.getAbsolutePath().equals(UserInputData.ParameterCtxEnPath);
	}
	
	/**
	 * .ctx파일과 . test 파일인지 아닌지 확인하는 메소드입니다.
	 * @param file
	 * @return
	 */
	private boolean checkExtension(File file) {
		return (file.getName().endsWith(".ctx")) || (file.getName().endsWith(".test"));
	}
	
	/**
	 * get RObject
	 * @return
	 */
	public ArrayList<RObject> getObjects() {
		return rObjects;
	}
}
