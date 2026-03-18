import java.awt.*;
import java.awt.image.BufferedImage;

public class GrayScaleFX implements ImageFX {

	private GamePanel panel;
	private boolean active;

	public GrayScaleFX(GamePanel p) {
		panel = p;
		active = false;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private int toGray(int pixel) {
		int alpha = (pixel >> 24) & 255;
		int red   = (pixel >> 16) & 255;
		int green = (pixel >>  8) & 255;
		int blue  =  pixel        & 255;

		int gray = (int)(0.2126 * red + 0.7152 * green + 0.0722 * blue);
		gray = Math.min(gray, 255);

		return (alpha << 24) | (gray << 16) | (gray << 8) | gray;
	}

	public void applyToImage(BufferedImage image) {
		if (!active) return;

		int w = image.getWidth();
		int h = image.getHeight();

		int[] pixels = new int[w * h];
		image.getRGB(0, 0, w, h, pixels, 0, w);

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = toGray(pixels[i]);
		}

		image.setRGB(0, 0, w, h, pixels, 0, w);
	}

	@Override
	public void draw(Graphics2D g2) {
	}

	@Override
	public void update() {
	}
}