package com.java.project;

import java.io.File;
import java.util.ArrayList;

import com.java.rcptt.RObject;

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
public class __Project {
	private String path = null;
	private File rootFile = null;
	
	private ArrayList<RObject> objects = new ArrayList<RObject>(); // 모든 RObject 를 저장하기 위하여
	
	public __Project(String path) {
		this.path = path;
		this.rootFile = new File(path);

		this.read();
	}
	
	
	private void read() {
		this.readLoop(this.rootFile); // 루프 루트 폴더 입력
	}
	
	/**
	 * <pre>
	 * 재귀를 통하여 모든 RCPTT object를 확인
	 * 확장자명이 ctx 혹은 test인경우 ROject 추가
	 * 
	 * <b>readLoop</b>
	 * private void readLoop(File file) 
	 * </pre>
	 * @param file
	 * @see RObject
	 */
	private void readLoop(File file) {
		File[] files = file.listFiles(); // 파일리스트 
		
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) { // 폴더인경우 재귀
					this.readLoop(files[i]);
				} else {
					if (checkExtension(files[i])) { // 확장자명이 ctx , test 인경우 
						//if (!files[i].getPath().equals(UserInfo.ParameterCtxPath)) { // 이 파일이 parameter의 파일이 아닌경우
						//	this.objects.add(new RObject(files[i].getPath()));  // 추가
						//}
					}
				}

			}
		}
	}
	
	/**
	 * <pre>
	 * 확장자명이 ctx 혹은 test인지 확인
	 * 
	 * <b>checkExtension</b>
	 * 	private boolean checkExtension(File file) 
	 * </pre>
	 * @param file
	 * @return
	 */
	private boolean checkExtension(File file) {
		boolean ret = (file.getName().endsWith(".ctx")) || (file.getName().endsWith(".test"));
		
		return ret;
		
//		String extension_name = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
//		if (extension_name.equals("ctx") || extension_name.equals("test")){
//			return true;
//		}
//		return false;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public File getRoot_file() {
		return rootFile;
	}

	public void setRoot_file(File root_file) {
		this.rootFile = root_file;
	}

	public ArrayList<RObject> getObjects() {
		return objects;
	}

	public void setObjects(ArrayList<RObject> objects) {
		this.objects = objects;
	}
	
	
}
