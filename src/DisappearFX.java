import java.awt.*;
import java.awt.image.BufferedImage;

public class DisappearFX implements ImageFX {

	private GamePanel panel;
	private boolean active;
	private int alpha;

	public DisappearFX(GamePanel p) {
		panel = p;
		active = false;
		alpha = 255;
	}

	public void setActive(boolean active) {
		this.active = active;
		if (!active) {
			alpha = 255;
		}
	}

	public float getAlpha() {
		return alpha / 255.0f;
	}

	public void applyToImage(BufferedImage image) {
		if (!active) return;

		int w = image.getWidth();
		int h = image.getHeight();

		int[] pixels = new int[w * h];
		image.getRGB(0, 0, w, h, pixels, 0, w);

		for (int i = 0; i < pixels.length; i++) {
			int a     = (pixels[i] >> 24) & 255;
			int red   = (pixels[i] >> 16) & 255;
			int green = (pixels[i] >>  8) & 255;
			int blue  =  pixels[i]        & 255;

			if (a != 0) {
				pixels[i] = blue | (green << 8) | (red << 16) | (alpha << 24);
			}
		}

		image.setRGB(0, 0, w, h, pixels, 0, w);
	}

	@Override
	public void draw(Graphics2D g2) {
	}

	@Override
	public void update() {
		if (!active) {
			alpha = 255;
			return;
		}

		alpha -= 8;
		if (alpha < 20) {
			alpha = 20;    // Not fully invisible... Makes the player still be seen in water
		}
	}
}