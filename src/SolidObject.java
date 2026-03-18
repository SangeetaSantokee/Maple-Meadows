import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class SolidObject {

	private JPanel panel;
	private int x;
	private int y;
	private int width;
	private int height;

	public SolidObject(JPanel p, int xPos, int yPos, int w, int h) {
		panel = p;
		x = xPos;
		y = yPos;
		width = w;
		height = h;
	}

	public void draw(Graphics2D g2, int cameraX, int cameraY) {
		int drawX = x - cameraX;
		int drawY = y - cameraY;

		Rectangle2D.Double solid = new Rectangle2D.Double(drawX, drawY, width, height);
		g2.setColor(new Color(255, 0, 0, 80));   // temp
		g2.fill(solid);
	}

	public Rectangle2D.Double getBoundingRectangle() {
		return new Rectangle2D.Double(x, y, width, height);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}