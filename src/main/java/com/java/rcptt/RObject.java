package com.java.rcptt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.java.util.ErrorMessage;
import com.java.util.ProgramData;
import com.java.util.UserInputData;

/*
 * 
--- RCPTT testcase ---
Format-Version: 1.0
Context-Type: org.eclipse.rcptt.ctx.parameters
Element-Name: p
Element-Type: context
Element-Version: 2.0
Id: __XnUYPseEeerEKrtJAzT4w
Runtime-Version: 2.2.0.201706152316
Save-Time: 1/17/18 1:18 PM

------=_.parameters.context-2e023de5-3294-36a9-ac1d-6701f05a40ee
Content-Type: text/properties
Entry-Name: .parameters.context

#Wed Jan 17 13:18:21 KST 2018

------=_.parameters.context-2e023de5-3294-36a9-ac1d-6701f05a40ee--

위는 ctx 파일의 형태 입니다.
 처음에 기본 정보가 나오며 그다음 아래에 ecl script 나 properties 파일이나 description 이 나옵니다.
 그래서 위에 기본 정보는 ROjbectForm에서 저장을 ArrayList로 한줄한줄 가지고있습니다.
 아래의 정보의 경우 parameter 뿐만아니라 따른 파일도 올 수 있기 때문에 ArrayList로 관리합니다.
 
 */

/**
 *
 * <pre>
 * RCPTT에서 사용되는 파일들을 저장하는 클래스 입니다.
 *
 * 파일의 경로를 입력받으면 ecl script, testcase , parameter파일 3가지를
 * 자동으로 나누어 보관하고 있는다.
 *
 * 만약 RCPTT API 가 확인이 된다면 사용하지 않을 수 있는 클래스 입니다.
 *
 * @author suresoft
 * @see ROjectContent
 * @see ROjectForm
 */
public class RObject {

	/**
	* 기본 RCPTT파일의 형식을 저장할 ArrayList 입니다.
	*/
	private ArrayList<RObjectForm> form = new ArrayList<RObjectForm>();

	/**
	* 파일안에 있는 content 정보를 담을 ArrayList 입니다.
	* testcase나 ctx 파일의 경우 description 혹은 다른 content 도 포함되어있어
	* ArrayList로 나누어 보관하고 있습니다.
	*/
	private ArrayList<RObjectContent> content = new ArrayList<RObjectContent>();

	/**
	* 사용자로 부터 받은 경로를 저장하고 있을 String 형의 변수입니다.
	*/
	private String path = null;

	/**
	* 사용자로 부터 받은 경로로 부터 파일을 저장하고 있을 File 형의 변수입니다.
	*/
	private File file = null;

	/**
	* enum 형의 변수로 TESTCASE , PARAMETER , ECL , DESCRIPT 의 정보로 나뉩니다.
	* RObject의 타입을 저장하고 있습니다.
	*/
	private RcpttResourceType type;

	/**
	* 경로를 입력받아 path 와 file 을 입력받으며
	* 바로 read() 메소드를 진행합니
	*/
	public RObject(String path) {
		this.path = path;
		this.file = new File(path);
		this.read();
	}

	/**
	* 입력받은 경로로부터 파일을 읽어 form와 content 형식을
	* 나누어 저장합니다.
	*/
	public void read() {
		this.readForm();
		this.readContent();
	}

	/**
	* 먼저 RCPTT 파일의 정보를 읽습니다 ( form )
	* 하드코딩으로 RCPTT API 를 사용하지 않았습니다.
	* API 가 확인되면 변경예정입니다.
	*/
	private void readForm() {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		
		try {
			fis = new FileInputStream(path);
			isr = new InputStreamReader(fis, "UTF-8");
			reader = new BufferedReader(isr);

			String currentLine = "";
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.equals("--- RCPTT testcase ---")) {	} // RCPTT 파일의 첫 시작 인트로 do nothing 
				else if (currentLine.trim().length() == 0) { //비어있을경우
					break;
				} else {
					String[] splitLine = currentLine.split(":",2);
					this.form.add(new RObjectForm(splitLine[0],splitLine[1].trim()));
				}
			}
			this.setFormType();

			reader.close();
		} catch (IOException exception) {
			ErrorMessage.getInstance().printErrorMessage("ROject.readform.error");
		}finally{
			try {
				if(fis!=null){
					fis.close();
				}
				if(isr!=null){
					isr.close();
				}
				if(reader!=null){
					reader.close();
				}

			} catch (IOException exception2) {
				ErrorMessage.getInstance().printErrorMessage("ROject.close.inputstream.error");
			}
		}

	}

	/**
	 * form 의 다음문장을 읽는 기능
	 * 한 라인씩 계속하여 확인함.
	 * ecl script의 경우 ecl script 에 맞게 저장
	 * parameter 의경우 parameter 에 맞게 저장
	 */
	private void readContent() {

		try {
			FileInputStream fis = new FileInputStream(path);
			InputStreamReader isr
				= new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);

			String currentLine = "";

			String startLine = "";
			String contentType = "";
			String entryName = "";
			StringBuilder contentText = null;
			String newLine = "\n";

			//위에 포멧시작을 건너 뛰기 위하여
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.equals("--- RCPTT testcase ---")) {	} // do nothing 
				else if (currentLine.trim().length() == 0) {
					break;
				}
			}

			boolean isContentLine = false; //0은 비활성화 1은 이제 시작
			boolean isParameter = false;
			while ((currentLine = reader.readLine()) != null) {
				if(currentLine.startsWith("------=")){//content의 시작은 ------= 로 시작하여 이 부분을 만났을 경우 아래의 조건을 확인합니다.
					if(!isContentLine) {
						// 시작
						contentText = new StringBuilder();
						startLine = currentLine;

						currentLine = reader.readLine();
						if(currentLine!=null){
							contentType = currentLine; // ------= 문장의 다음문장은 무조건 content-type
						}

						currentLine = reader.readLine();
						if(currentLine!=null){
							entryName = currentLine; // 다음 문장은 entry-name
						}

						if(contentType.equals("Content-Type: text/properties")){ // 만약 이것이 parameter 형식의 ctx 파일인경우
							isParameter = true;
						}
						isContentLine = true;
					}else if (isContentLine) { // 이제 종료
						if(isParameter) { // String start_line , String content_type , String entry_name , String text , String end_line , boolean isParameter
							this.content.add(new RObjectContent(startLine,contentType,entryName,"",startLine+"--", true,this.path));
							isParameter = false;
						}else { // String start_line , String content_type , String entry_name , String text , String end_line , boolean isParameter
							this.content.add(new RObjectContent(startLine,contentType,entryName,contentText.toString(),startLine+"--" , false));
						}
						isContentLine = false;
					}
				}else {
					if(isParameter) {
						if(currentLine.contains("=")) {// = 가없는경우 그 라인은 버림
							String[] splitLine = currentLine.split("=",2);
							if(this.path.equals(UserInputData.ParameterCtxPath)){
								ProgramData.getInstance().addParameter(splitLine[0],splitLine[1],true);
							}else if (this.path.equals(UserInputData.ParameterCtxEnPath)){
								ProgramData.getInstance().addParameter(splitLine[0],splitLine[1],false);
							}
						}
					}
					else {
						contentText.append(currentLine);
						contentText.append(newLine);
					}
				}
			}
			reader.close();

		}catch(IOException exception) {
			ErrorMessage.getInstance().printErrorMessage("ROject.readcontent.error");
		}
	}

	/**
	* ECL 스크립트의 파일이 맞는지 확인하여
	* ECL 스크립트 파일이 맞은경우 ecl text를
	* String 형식으로 반환합니다.
	*/
	public String getEclScript() {
		for(RObjectContent tempRObjectContent : this.content){
			if (tempRObjectContent.getType() == RcpttResourceType.ECL) {
				
				return tempRObjectContent.getText();
			}
		}
		return null;
	}

	/**
	* ECL 스크립트의 파일이 맞는지 확인하여
	* ECL 스크립트 파일이 맞은 경우 ecl text를
	* RObject에 저장합니다.
	*/
	public void setECLCode(String eclCode) {
		for(RObjectContent tempRObjectContent : this.content){
			if (tempRObjectContent.getType() == RcpttResourceType.ECL) {
				tempRObjectContent.setText(eclCode);
			}
		}
	}

	/**
	* Robject를 읽은 뒤 form 에서 context-type 정보를 불러와
	* 해당하는 타입을 결정합니다.
	* 하드코딩
	*/
	private void setFormType() { // 이것이 무슨 파일인지 분류 (not yet = -1 / testcase = 0 / parameter = 1 / ecl = 2 )
		RObjectForm tempRObjevtForm = this.selectRObjectForm("Context-Type");
		
		if (tempRObjevtForm == null) {
			this.type = RcpttResourceType.TESTCASE; // testcase
		} else if (tempRObjevtForm.getValue().equals("org.eclipse.rcptt.ctx.parameters")) {
			this.type = RcpttResourceType.PARAMETER;
			if(this.path.equals(UserInputData.ParameterCtxPath)){
				ProgramData.getInstance().setParameterCtxId(selectRObjectForm("Id").getValue());
			}
			else if(this.path.equals(UserInputData.ParameterCtxEnPath)){
				ProgramData.getInstance().setParameterEnCtxId(selectRObjectForm("Id").getValue());
			}
		} else if (tempRObjevtForm.getValue().equals("org.eclipse.rcptt.ctx.ecl")) {
			this.type = RcpttResourceType.ECL;
		}
	}

	/**
	 * testcase 파일인경우 parameter의 고유 아이디를 추가해주어야
	 * RCPTT 에서 링크가되어 정상 작동 됩니다.
	 * @param id
	 */
	public void addContexts(String id) {
		if (this.type == RcpttResourceType.TESTCASE) {
			RObjectForm tempRObjectForm = this.selectRObjectForm("Contexts");
			if (tempRObjectForm == null) {
				this.form.add(1, new RObjectForm("Contexts", id));
			} else {
				if (!tempRObjectForm.getValue().contains(id)){
					tempRObjectForm.setValue("" + id + "," + tempRObjectForm.getValue());
				}
			}
		}
		
	}
	
	/**
	 * 사용자가 en 혹은 ko를 입력하였을때 
	 * testcase에서는 그에 맞게 변형되어야 함으로 
	 * 그전에 있던 parameter ctx 파일의 id 값을 제거 후 
	 * 위에 있는 메소드인 add 를 추가합니다.
	 * @param id
	 */
	public void deleteContexts(String id){
		if (this.type == RcpttResourceType.TESTCASE) {
			RObjectForm tempRObjectForm = this.selectRObjectForm("Contexts");
			if (tempRObjectForm == null) {	} //do nothing 
			else {
				tempRObjectForm.setValue(tempRObjectForm.getValue().replace(id+",", ""));
				tempRObjectForm.setValue(tempRObjectForm.getValue().replace(","+id, ""));
				tempRObjectForm.setValue(tempRObjectForm.getValue().replace(id, ""));
			}
		}
	}
	
	/**
	 * 현재 RObejctform 과 content 를 이용하여
	 * RCPTT 파일의 포멧대로 파일을 덮어써 저장합니다.
	 */
	public void save() {
		StringBuilder saveStringBuilder = new StringBuilder();
		String newLine = "\n";
		
		saveStringBuilder.append("--- RCPTT testcase ---"+newLine); // intro

		for(RObjectForm tempRObjectForm : this.form){ // form들을 가지고와서 saveStringBuilder에 추가합니다.
			saveStringBuilder.append(tempRObjectForm.getKey());
			saveStringBuilder.append(": ");
			saveStringBuilder.append(tempRObjectForm.getValue());
			saveStringBuilder.append(newLine);
		}
		saveStringBuilder.append(newLine);
		for(RObjectContent tempContent : this.content){ // content들을 가지고와서 saveStringBuilder에 추가합니다.
			saveStringBuilder.append(tempContent.output());			
		}
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(this.file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(saveStringBuilder.toString());
			osw.close();

		}catch(IOException exception) {
			ErrorMessage.getInstance().printErrorMessage("RObject.save.error");
		}finally{
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (IOException exception2) {
				ErrorMessage.getInstance().printErrorMessage("RObject.close.inputstream.error");
			}
		}
	}
	
	/**
	 * Test 용 입니다.
	 * save되는 내용을 그대로 String 으로 리턴합니다.
	 * @return
	 */
	public String getOutputFormat(){
		StringBuilder saveStringBuilder = new StringBuilder();
		String newLine = "\n";
		
		saveStringBuilder.append("--- RCPTT testcase ---"+newLine); // intro

		for(RObjectForm tempRObjectForm : this.form){ // form들을 가지고와서 saveStringBuilder에 추가합니다.
			saveStringBuilder.append(tempRObjectForm.getKey());
			saveStringBuilder.append(": ");
			saveStringBuilder.append(tempRObjectForm.getValue());
			saveStringBuilder.append(newLine);
		}
		saveStringBuilder.append(newLine);
		for(RObjectContent tempContent : this.content){ // content들을 가지고와서 saveStringBuilder에 추가합니다.
			saveStringBuilder.append(tempContent.output());			
		}
		
		return saveStringBuilder.toString();
	}
	
	/**
	 * form 에서 입력한 파라미터와 동일한 key를 가진경우
	 * RObjectForm 을 반환합니다.
	 * @param key
	 * @return
	 */
	public RObjectForm selectRObjectForm(String key) {
		for(RObjectForm tempRObjectForm : this.form){
			if( tempRObjectForm.getKey().equals(key)){
				return tempRObjectForm;
			}
		}
		return null;
	}

	/**
	 * get set 
	 */
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
