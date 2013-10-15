package org.societies.api.internal.privacytrust.privacy.model.dataobfuscation;

public class ObfuscationExample {
	private String content;
	private int obfuscationLevelStep;
	
	public ObfuscationExample(int obfuscationLevel, String content) {
		super();
		this.content = content;
		this.obfuscationLevelStep = obfuscationLevel;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getObfuscationLevelStep() {
		return obfuscationLevelStep;
	}
	public void setObfuscationLevelStep(int obfuscationLevel) {
		this.obfuscationLevelStep = obfuscationLevel;
	}

	@Override
	public String toString() {
		return "ObfuscationExample ["
				+ (content != null ? "content=" + content + ", " : "")
				+ "obfuscationLevelStep=" + obfuscationLevelStep + "]";
	}
	
	
}
