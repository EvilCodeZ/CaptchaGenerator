package de.evilcodez.captcha;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CaptchaGenerator {
	
	private static final String[] FONTS = new String[] {
			"Arial", "Arial Black", "Segoe UI",
			"Impact", "Consolas", "Georgia",
			"Corbel", "Corbel New", "Segoe Script",
			"Tahoma", "Verdana", "Times New Roman",
			"Comic Sans MS", "Impact", "Courier New",
			"Geneva"
	};
	
	public static Captcha generate(CaptchaType type) {
		if(type == CaptchaType.CALCULATION) {
			return generateCalculationCaptcha();
		}
		return generateCodeCaptcha();
	}
	
	private static Captcha generateCalculationCaptcha() {
		final SecureRandom rnd = new SecureRandom();
		String term;
		String result;
		int i1;
		int i2;
		switch(3) {
		default:
		case 0:
			i1 = (-9 + rnd.nextInt(19));
			i2 = (-9 + rnd.nextInt(19));
			term = i1 + " + " + i2;
			result = String.valueOf(i1 + i2);
			break;
		case 1:
			i1 = (-9 + rnd.nextInt(19));
			i2 = (-9 + rnd.nextInt(19));
			term = i1 + " - " + i2;
			result = String.valueOf(i1 - i2);
			break;
		case 2:
			i1 = rnd.nextInt(10);
			i2 = -9 + rnd.nextInt(19);
			term = i1 + " * " + i2;
			result = String.valueOf(i1 * i2);
			break;
		case 3:
			i1 = (1 + rnd.nextInt(10)) * 2;
			final List<Integer> list = new ArrayList<>();
			for(int i = 1; i <= i1; ++i) {
				if(i1 % i == 0) {
					list.add(i);
				}
			}
			if(list.isEmpty()) {
				list.add(1);
			}
			i2 = list.get(rnd.nextInt(list.size()));
			term = i1 + " : " + i2;
			result = String.valueOf(i1 / i2);
			break;
		}
		return new Captcha(CaptchaType.CALCULATION, term, result, generateImage(term, false));
	}
	
	private static Captcha generateCodeCaptcha() {
		final String code = generateCode();
		return new Captcha(CaptchaType.CODE, code, code, generateImage(code, true));
	}
	
	private static BufferedImage generateImage(String text, boolean allowStrikethrough) {
		final ThreadLocalRandom rnd = ThreadLocalRandom.current();
		
		// Create font
		Font font = randomFont().deriveFont(0, 28 + rnd.nextInt(3));
		Map<TextAttribute, Object> attributes = new HashMap<>();
		attributes.put(TextAttribute.TRACKING, -0.05 + rnd.nextDouble() * 0.35);
		attributes.put(TextAttribute.STRIKETHROUGH, rnd.nextBoolean() && allowStrikethrough);
		font = font.deriveFont(attributes);
		
		// Random rotation angle
		final double angle = -45.0 + rnd.nextDouble() * 90.0F;
		
		// Create image
		final BufferedImage image = new BufferedImage(180, 180, BufferedImage.TYPE_INT_ARGB);
		
		// Render background
		final Graphics2D g = image.createGraphics();
		Color backgroundColor = randomColor();
		
		if(rnd.nextBoolean()) {
			g.setColor(backgroundColor);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
		}else {
			for(int y = 0; y < image.getHeight(); ++y) {
				for(int x = 0; x < image.getWidth(); ++x) {
					g.setColor(backgroundColor = randomColor(backgroundColor, 20));
					g.fillRect(x, y, 1, 1);
				}
			}
		}
		
		// Render text
		Color fontColor = randomColor();
		while(getColorDistance(backgroundColor, fontColor) < 90) {
			fontColor = randomColor();
		}
		
		g.setFont(font);
		g.translate(image.getWidth() / 2, image.getHeight() / 2);
		g.rotate(Math.toRadians(angle));
		g.translate(-(image.getWidth() / 2), -(image.getHeight() / 2));
		
		int shadowSize = 3 + rnd.nextInt(3);
		final int stringX = image.getWidth() / 2 - g.getFontMetrics().stringWidth(text) / 2 + 5;
		final int stringY = image.getHeight() / 2;
		for(int i = 0; i <= shadowSize; ++i) {
			final int off = shadowSize;
			g.setColor(i == shadowSize ? fontColor : randomColor());
			g.drawString(text, stringX + off - i, stringY + off - i);
		}
		
		g.dispose();
		
		// Blur effect
		blurImage(image, 1 + rnd.nextInt(4));
		
		return image;
	}
	
	private static String generateCode() {
		final Random rnd = new Random();
		final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		final StringBuilder code = new StringBuilder();
		for(int i = 0; i < 6; ++i) {
			code.append(chars[rnd.nextInt(chars.length)]);
		}
		return code.toString();
	}
	
	private static Color randomColor() {
		final ThreadLocalRandom rnd = ThreadLocalRandom.current();
		return new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
	}
	
	private static Color randomColor(Color prev, int i) {
		final ThreadLocalRandom rnd = ThreadLocalRandom.current();
		int r;
		do {
			r = prev.getRed() + (-i + rnd.nextInt(i * 2));
		}while(r > 255 || r < 0);
		int g;
		do {
			g = prev.getGreen() + (-i + rnd.nextInt(i * 2));
		}while(g > 255 || g < 0);
		int b;
		do {
			b = prev.getBlue() + (-i + rnd.nextInt(i * 2));
		}while(b > 255 || b < 0);
		return new Color(r, g, b);
	}
	
	private static void blurImage(BufferedImage img, int amount) {
		for(int i = 0; i < amount; ++i) {
			for(int y = 0; y < img.getHeight(); ++y) {
				for(int x = 0; x < img.getWidth(); ++x) {
					final Color c1 = x - 1 < 0 || x + 1 >= img.getWidth() ? new Color(img.getRGB(x, y)) : new Color(img.getRGB(x - 1, y));
					final Color c2 = y - 1 < 0 || y - 1 >= img.getWidth() ? new Color(img.getRGB(x, y)) : new Color(img.getRGB(x, y - 1));
					final Color c3 = y + 1 < 0 || y + 1 >= img.getWidth() ? new Color(img.getRGB(x, y)) : new Color(img.getRGB(x, y + 1));
					final Color c4 = x + 1 < 0 || x + 1 >= img.getWidth() ? new Color(img.getRGB(x, y)) : new Color(img.getRGB(x + 1, y));
					
					final int ar = c1.getRed() + c2.getRed() + c3.getRed() + c4.getRed();
					final int ag = c1.getGreen() + c2.getGreen() + c3.getGreen() + c4.getGreen();
					final int ab = c1.getBlue() + c2.getBlue() + c3.getBlue() + c4.getBlue();
					
					img.setRGB(x, y, new Color(ar / 4, ag / 4, ab / 4).getRGB());
				}
			}
		}
	}
	
	private static double getColorDistance(Color color1, Color color2) {
		final double dr = color2.getRed() - color1.getRed();
		final double dg = color2.getGreen() - color1.getGreen();
		final double db = color2.getBlue() - color1.getBlue();
		return Math.sqrt(dr * dr + dg * dg + db * db);
	}
	
	private static Font randomFont() {
		final Random rnd = new Random();
		return new Font(FONTS[rnd.nextInt(FONTS.length)], 0, 10);
	}
}
