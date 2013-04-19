package edu.berkeley.cs162;

public class KeyValue {

	private String key;
	private String value;

	public KeyValue() {
	}

	public KeyValue(String k, String v) {
		this.key = k;
		this.value = v;
	}
 	

	void setKey(String k) {
		this.key = k;
	}

	void setValue(String v) {
		this.value = v;
	}

	String getKey() {
		return this.key;
	}

	String getValue() {
		return this.value;
	}

}