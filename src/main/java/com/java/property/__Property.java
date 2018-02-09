package com.java.property;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.java.util.Logger;


/**
 * <pre>
 * 모든 properties를 읽는 기능,
 * 루트로부터 하위 파일까지 전체 확인 
 * 
 * <b>Property</b>
 * 	public Property()
 * 	생성과 동시해 실행
 * </pre>
 * @author suresoft
 *
 */


public class __Property {
	private String root_path = ""; // 루트 경로
	private File root_file = null; // 루트 폴더
	private String language = ""; 
	private int prameterCount = 0; // 몇개의 파라미터가 확인됬는지 확인하기 위해
	private int propertiesCount = 0; // 몇개의 properties파일이 있는지 확인하기위해
	
	private boolean propertyReturnValue = true; 
	
	
	public __Property() {
		//root_path = UserInfo.CodeScrollFilePath;
		//language = UserInfo.language;
		root_file = new File(root_path);
		//read();
	}
	
	
	public boolean read() {
		
		
		readLoop(root_file);
		
		Logger.write("CT경로에서 찾은 .properties의 갯수 : "+propertiesCount);
		Logger.write(".properties에서 모은 parameter의 총 갯수 : "+prameterCount);
		
		return propertyReturnValue;
	}
	
	
	/**
	 * <pre>
	 * 재귀로 모든 파일 확인하는 함수
	 * 만약 폴더일경우 재귀  .propeties 파일인 경우 parameter 를 확인
	 * 만약 jar파일인경우 jar파일안에 있는 파일들을 확인 후 parameter로 변형
	 * 
	 * <b>readLoop</b>
	 * 	private void readLoop(File file)
	 * </pre>
	 * 
	 * @param file
	 */
	private void readLoop(File file) {
		
		File[] files = file.listFiles(); // 파일리스트
		if(files!=null){
			for(int i = 0 ; i < files.length ; i ++) {
				if(files[i].isDirectory()){ // 그것이 폴더 ? 
					readLoop(files[i]); // 재귀
				}
				else {
					if(isPropertiesFile(files[i].getAbsolutePath())&&
							containsLanguage(files[i].getAbsolutePath())) {// .properties 파일이 맞나 language가 들어가있나
						readFileAndClassifyParameter(files[i]); // 두개가 맞다면 parameter 에 추가.
					}
					else if(isJarFile(files[i])) { // .jar 파일인경우 
						readJarFileClassifyParameter(files[i]); // jar파일 확인하여 parameter 에 추가
					}
				}
			}
		}
	}
	
	
	/**
	 * <pre>
	 * 
	 * jar 파일안에 있는 파일들을 확인하여 
	 * 그파일이 properties파일인 경우 
	 * 파일 text를 읽어와서 parameter 로 변형
	 * 
	 * <b>readJarFileClassifyParameter</b>
	 * 	private void readJarFileClassifyParameter(File file)
	 * </pre>
	 * @param file
	 */ 
	 
	private void readJarFileClassifyParameter(File file) {
		// jar파일 안을 확인하여 그것이 properties 파일인경우 parameter 로 추가
		try {
			JarFile jarFile = new JarFile(file.getAbsolutePath());  // jar 파일 경로를 읽는다
			Enumeration<JarEntry> entries = jarFile.entries(); // jar파일안에 있는 entry 들을 확인
			
			JarEntry entry = null; 
			
			InputStreamReader isr = null;
			BufferedReader reader  = null;
			while(entries.hasMoreElements()) {
				entry = entries.nextElement(); 
				
				if(isPropertiesFile(entry.getName())&&containsLanguage(entry.getName())) { //만약 entry name 이 properties 와 language 에 맞을경우    

					propertiesCount++; 
					isr  = new InputStreamReader(jarFile.getInputStream(entry));
					reader = new BufferedReader(isr);
					
					String line = "";

					while((line = reader.readLine())!=null) { 
						if(line.length()==0) {
							// 아무것도 없는 라인이면 do nothing
						}else if(line.charAt(0)=='#'){
							// 라인 처음이 #으로 주석 처리 
						}else {
							if(line.contains("=")) { // = 가없는경우 그라인은 버림
//								String[] splitLine = line.split("=",2);
								
								StringBuilder pathSb = new StringBuilder();
								pathSb.append(jarFile.getName());
								pathSb.append("-");
								pathSb.append(entry.getName());
								
								//ProgramData.addParameter(new Parameter(splitLine[0],splitLine[1],
										// 앞에 jarFile 이름과 entry 이름을 나누기위하여 -
										//pathSb.toString()	));
								
								prameterCount++;
							}
						}
					}
					if(reader!=null){
						reader.close();
					}
					if(isr!=null){
						isr.close();
					}
				}
					
				
			}
			if(jarFile!=null){
				jarFile.close();
			}
		}catch(IOException e) {
			Logger.write("readJarFileClassifyParameter error");
			propertyReturnValue = false;
		}
	}
	

	/**
	 * <pre>
	 * 파일 text를 읽어와서 parameter 로 변형
	 * 
	 * <b>readFileAndClassifyParameter</b>
	 * 	private void readFileAndClassifyParameter(File file)
	 * </pre>
	 * @param file
	 */
	private void readFileAndClassifyParameter(File file) {
		
		// 파일 자체를 읽은 뒤 parameter 로 나누어 주는 작업 
		try {
			propertiesCount++; // 
			String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))); // 파일 text 읽어오기
			BufferedReader reader = new BufferedReader(new StringReader(text)); 
			
			String line = null;
			
			while((line = reader.readLine())!=null) {
				if(line.length()==0) {
					// 아무것도 없는 라인이면 do nothing
				}else if(line.charAt(0)=='#'){
					// 라인 처음이 #으로 주석 처리 
				}else {
					int i = 0; // = << 이것의 위치를 확인하기위하여 
					if(line.contains("=")) {// = 가없는경우 그라인은 버림 
						for(i = 0 ; i < line.length() ; i ++) {
							if(line.charAt(i)=='=') {// = detect !
								break;
							}
						}
//						key = line.substring(0, i); // = 가 생기면 왼쪽 라인은 key 
//						value = line.substring(i+1, line.length()); // = 오른쪽은 value 
						
						//파라미터 추가
						//ProgramData.addParameter(new Parameter(key,value,file.getAbsolutePath()));
						prameterCount++;
					}
				}
			}
		}catch(IOException e) {
			Logger.write("readFileAndClassifyParameter");
			propertyReturnValue = false;
		}
	}	

	private boolean isPropertiesFile(String path) {		
		boolean ret = path.endsWith("properties");
		
		return ret;
	}
	
	private boolean containsLanguage(String path) {
		boolean ret = (language.length()==0) || (path.contains(language));
		
		return ret;
	}
	

	/**
	 * jar파일인지 확인
	 * @param file
	 * @return
	 */
	private boolean isJarFile(File file) {
		boolean ret = file.getName().endsWith("jar");
		
		return ret;
	}

	
	
	
	
	
	
	
	
	
	
}
