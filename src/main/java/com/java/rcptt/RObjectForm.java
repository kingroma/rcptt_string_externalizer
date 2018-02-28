package com.java.rcptt;

/**
 * form 에있는정보를 key와 value 형식으로 나누어 저장
 * ROject에서 ArrayList로 가지고 있는다.
 *
 * @author suresoft
 * @see RObject
 */
public class RObjectForm {
	
	/**
	* RCPTT 파일의 정보는
	* key: value 형식으로 되어있습니다.
	* RObject에서는 ArrayList RObjectForm 형식으로
	* 가지고있기때문에 key와 value 만 현재 클래스에서 저장하고 있습니다.
	*/
	private String key = "";
	private String value = "";

	/**
	* 생성과 동시에 key value 저장합니다.
	*/
	public RObjectForm(String key, String value) {
		this.key = key;
		this.value = value.trim();
	}

	/**
	 * get set
	 */
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
