package com.java.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import com.java.util.ErrorMessage;
import com.java.util.Logger;
import com.java.util.ProgramData;
import com.java.util.UserInputData;


/**
 * 생성자로 부터 경로를 받아 그 경로를 시작으로 
 * 모든 .properties파일을 읽어 ProgramData의 parameter에 추가합니다.
 * @author suresoft
 *
 */
public class ProductProperties {
	
	/**
	 * 생성될때 루트 경로를 저장하고 있을 String 형 변수입니다.
	 */
	private String rootPath;
	
	/**
	 *  몇개의 properties파일이 있는지 확인하기 위함 입니다.
	 */
	private int propertiesFileCount;
	
	
	/**
	 * 파라미터로 경로를 받아 rootPath에 저장합니다
	 * @param inputPath
	 */
	public ProductProperties(String inputPath) {
		this.rootPath = inputPath;
	}
	
	/**
	 * 모든 properties파일을 읽는 함수이며
	 * 생성자에서 부터 받은 경로로 부터 모든 properties파일을 확인합니다.
	 */
	public void read() {
		try (Stream<Path> paths = Files.walk(Paths.get(rootPath))) {
		    paths
		        .filter(Files::isRegularFile)
		    	.forEach(this::isPropertiesOrJar);
		} catch (IOException exception) {
			ErrorMessage.getInstance().printErrorMessage("ProductProperties.read.properties.error");
		}
		Logger.write(" CT경로에서 찾은 .properties의 갯수 : "+propertiesFileCount); // 몇개의 properties 파일을 읽었는지 출력합니다.
	}
	
	/**
	 * Jar파일과 그냥 properties 파일을 나눈 이유는
	 * Jar파일만의 다른 라이브러리를 사용하며 추가되는 내용이 있었기 때문에 나누었습니다.
	 * @param path
	 */
	private void isPropertiesOrJar(Path path){
		File file = path.toFile();
		if((isPropertiesFile(file.getAbsolutePath())) && (isContainsLanguage(file.getAbsolutePath()))) { // .properties 파일이 맞는지 language관련 문자열이 들어가있는지 확인합니다.
			readFileAndClassifyParameter(file); // 두개가 맞다면 parameter 에 추가합니다.
		}
		else if(isJarFile(file)) { // .jar 파일인경우 를 확인합니다.
			readJarFileClassifyParameter(file); // jar파일의 properties파일들을 확인하여 parameter 에 추가합니다.
		}
	}
	
	/**
	 * .jar 파일인경우 jar파일 라이브러리를 불러온 뒤 
	 * JarEntry를 이용하여 파일이 properties파일인경우
	 * parameter를 추가합니다.
	 */ 
	private void readJarFileClassifyParameter(File file) {
		// jar파일 안을 확인하여 그것이 properties 파일인경우 parameter 로 추가합니다.
		JarFile jarFile  = null;
		try {
			propertiesFileCount++; 
			jarFile = new JarFile(file.getAbsolutePath());  // jar 파일 경로를 읽습니다.
			Enumeration<JarEntry> entries = jarFile.entries(); // jar파일안에 있는 entry 들을 확인합니다.

			
			JarEntry entry = null;
			StringBuilder pathStringBuilder = null;
			InputStream is = null;
			Properties properties = null;
			
			while(entries.hasMoreElements()) {
				entry = entries.nextElement(); 
				
				if( (isPropertiesFile(entry.getName())) && (isContainsLanguage(entry.getName()) )) { //만약 entry name 이 properties 와 language 에 맞을경우
					try{
						is = jarFile.getInputStream(entry);
						
						properties = new Properties();
						properties.load(is);
						
						pathStringBuilder = new StringBuilder();
						pathStringBuilder.append(jarFile.getName());
						pathStringBuilder.append("-");
						pathStringBuilder.append(entry.getName());
						
						//key set을 가지고와 key들을 하나하나 Parameter에 옮깁니다.
						properties.keySet();
						for(Object obj : properties.keySet()){
							String key = (String)obj;
							ProgramData.getInstance().addParameter(key , properties.getProperty(key) , pathStringBuilder.toString());
						}
					}catch(IOException exception){
						ErrorMessage.getInstance().printErrorMessage("ProductProperties.readJar.properties.error");
					}finally{
						try {
							if(is!=null)
								is.close();
						} catch (IOException exception2) {
							ErrorMessage.getInstance().printErrorMessage("ProductProperties.close.inputstream.error");
						}
					}
				}
			}
		}catch(IOException exception) {
			ErrorMessage.getInstance().printErrorMessage("ProductProperties.read.Jar.error");
		}finally{
			try {
				if(jarFile!=null){
					jarFile.close();
				}
			} catch (IOException exception2) {
				ErrorMessage.getInstance().printErrorMessage("ProductProperties.closeJar.inputstream.error");
			}
			
		}
	}
	

	/**
	 * <pre>
	 * properties파일을 읽어와서
	 * Properties 라이브러리를 이용하여 
	 * 파라미터를 추가합니다.
	 * @param file
	 */
	private void readFileAndClassifyParameter(File file) { 
		InputStream is = null;
		Properties properties = null;
		try{
			is = new FileInputStream(file);
			properties = new Properties();
			properties.load(is);

			properties.keySet();
			for(Object obj :properties.keySet()){
				String key = (String)obj;
				ProgramData.getInstance().addParameter(key, properties.getProperty(key),file.getAbsolutePath());
			}
			
		}catch(IOException exception){
			ErrorMessage.getInstance().printErrorMessage("ProductProperties.read.properties.error");
		}finally{
			try {
				if(is!=null)
					is.close();
			} catch (IOException exception2) {
				ErrorMessage.getInstance().printErrorMessage("ProductProperties.close.inputstream.error");
			}
		}
		
		propertiesFileCount++;
		
	}	
	
	/**
	 * 확장자명 확인하기 위하여 
	 * .properties 인경우 true
	 * 아닌경우 false 를 반환합니다.
	 * @param path
	 * @return
	 */
	private boolean isPropertiesFile(String path) {		
		return path.endsWith(".properties");
	}
	
	/**
	 * 입력된 파라미터인 String 경로에서 
	 * UserInfo.language가 들어가있는지 확인 하는 메소드입니다. 
	 * @param path
	 * @return
	 */
	private boolean isContainsLanguage(String path) {
		return (UserInputData.Language.length()==0) || (path.contains(UserInputData.Language));
	}
	
	/**
	 * 파일 이름의 확장자명이 jar파일인 경우를 확인하는 메소드 입니다. 
	 * @param file
	 * @return
	 */
	private boolean isJarFile(File file) {
		return file.getName().endsWith("jar");
	}
	
}
