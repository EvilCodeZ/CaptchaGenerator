package de.evilcodez.captcha;

import java.awt.image.BufferedImage;

public class Captcha {
	
	private final CaptchaType type;
	private final String code;
	private final String result;
	private final BufferedImage image;
	
	public Captcha(CaptchaType type, String code, String result, BufferedImage image) {
		this.type = type;
		this.code = code;
		this.result = result;
		this.image = image;
	}
	
	public CaptchaType getType() {
		return type;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getResult() {
		return result;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	@Override
	public String toString() {
		return "Captcha [type=" + type + ", code=" + code + ", result=" + result + "]";
	}
}
