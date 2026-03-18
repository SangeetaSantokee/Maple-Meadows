import java.awt.*;
import java.awt.image.BufferedImage;

public class TintFX implements ImageFX {

	private GamePanel panel;
	private boolean active;

	public TintFX(GamePanel p) {
		panel = p;
		active = false;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private int applyOrangeTint(int pixel) {
		int alpha = (pixel >> 24) & 255;
		int red   = (pixel >> 16) & 255;
		int green = (pixel >>  8) & 255;
		int blue  =  pixel        & 255;

		red   = Math.min(255, red   + 100);
		green = Math.min(255, green + 40);

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	public void applyToImage(BufferedImage image) {
		if (!active) return;

		int w = image.getWidth();
		int h = image.getHeight();

		int[] pixels = new int[w * h];
		image.getRGB(0, 0, w, h, pixels, 0, w);

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = applyOrangeTint(pixels[i]);
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