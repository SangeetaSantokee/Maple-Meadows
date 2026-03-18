import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 A component that displays all the game entities
 */
public class GamePanel extends JPanel implements Runnable {

	private static final int NUM_ALIENS = 6;
	private static final double ZOOM = 1.20;

	private SoundManager soundManager;

	private Bat bat;
	private SolidObject[] solids;
	private Alien[] aliens;

	private boolean isRunning;
	private boolean isPaused;

	private Thread gameThread;

	private BufferedImage image;
	private BufferedImage backgroundImage;

	private int cameraX;
	private int cameraY;
	private int worldWidth;
	private int worldHeight;

	private final int panelWidth = 500;
	private final int panelHeight = 500;

	private int foxesCollected = 0;

	private boolean inRiver = false;
	private boolean foxVisible = false;
	private boolean gameOver = false;

	private ImageFX imageFX;
	private ImageFX imageFX2;
	private ImageFX imageFX3;

	private int fps = 0;
	private int frames = 0;
	private long fpsStartTime = 0;
	private String currentEffectName = "None";

	private boolean playedGameOverSound = false;
	private boolean playedRiverSound = false;
	private boolean playedFoxAlertSound = false;

	public GamePanel() {
		bat = null;
		aliens = null;
		isRunning = false;
		isPaused = false;
		soundManager = SoundManager.getInstance();

		backgroundImage = ImageManager.loadBufferedImage("images/Village.png");

		if (backgroundImage != null) {
			worldWidth = backgroundImage.getWidth();
			worldHeight = backgroundImage.getHeight();
		}
		else {
			worldWidth = panelWidth;
			worldHeight = panelHeight;
			System.out.println("Could not load images/Village.png");
		}

		System.out.println("World width = " + worldWidth);
		System.out.println("World height = " + worldHeight);

		cameraX = 0;
		cameraY = 0;

		fps = 0;
		frames = 0;
		fpsStartTime = System.currentTimeMillis();
		currentEffectName = "None";

		image = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
	}

	public void createGameEntities() {

		solids = new SolidObject[40];

		// top left houses
		solids[0] = new SolidObject(this, 118, 160, 120, 85);
		solids[1] = new SolidObject(this, 310, 170, 95, 65);
		solids[2] = new SolidObject(this, 420, 160, 135, 100);

		// top right houses
		solids[3]  = new SolidObject(this, 1078, 185, 95, 60);
		solids[4]  = new SolidObject(this, 1358, 185, 92, 60);

		// centre houses
		solids[5]  = new SolidObject(this, 662, 320, 82, 58);
		solids[6]  = new SolidObject(this, 1148, 448, 52, 62);
		solids[7]  = new SolidObject(this, 1410, 468, 58, 60);

		// left middle / lower-left
		solids[8]  = new SolidObject(this, 120, 585, 78, 72);
		solids[9]  = new SolidObject(this, 320, 735, 72, 72);

		// lower middle houses
		solids[10] = new SolidObject(this, 640, 575, 102, 62);
		solids[11] = new SolidObject(this, 495, 735, 72, 70);
		solids[12] = new SolidObject(this, 640, 740, 92, 62);

		// lower-right house
		solids[13] = new SolidObject(this, 1190, 635, 100, 65);

		// river pieces
		solids[14] = new SolidObject(this, 860, 0, 100, 380);
		solids[15] = new SolidObject(this, 860, 521, 100, 508);
		solids[16] = new SolidObject(this, 600, 930, 550, 90);
		solids[17] = new SolidObject(this, 960, 780, 80, 200);

		// yellow roof next to river
		solids[18] = new SolidObject(this, 995, 555, 48, 70);

		bat = new Bat(this, 50, 375, solids);
		bat.setCamera(cameraX, cameraY);

		aliens = new Alien[NUM_ALIENS];
		aliens[0] = new Alien(this, 0,    100, bat, worldWidth, worldHeight);
		aliens[1] = new Alien(this, 700,  250, bat, worldWidth, worldHeight);
		aliens[2] = new Alien(this, 1400, 500, bat, worldWidth, worldHeight);
		aliens[3] = new Alien(this, 800,  100, bat, worldWidth, worldHeight);
		aliens[4] = new Alien(this, 300,  250, bat, worldWidth, worldHeight);
		aliens[5] = new Alien(this, 1500, 500, bat, worldWidth, worldHeight);

		foxesCollected = 0;
		inRiver = false;
		foxVisible = false;
		gameOver = false;
		playedGameOverSound = false;
		playedRiverSound = false;
		playedFoxAlertSound = false;

		imageFX = new DisappearFX(this);
		imageFX2 = new GrayScaleFX(this);
		imageFX3 = new TintFX(this);
	}

	public void run() {
		try {
			isRunning = true;
			fpsStartTime = System.currentTimeMillis();
			frames = 0;

			while (isRunning) {
				if (!isPaused) {
					gameUpdate();
				}

				gameRender();
				frames++;

				long now = System.currentTimeMillis();
				if (now - fpsStartTime >= 1000) {
					fps = frames;
					frames = 0;
					fpsStartTime = now;
				}

				Thread.sleep(50);
			}
		}
		catch (InterruptedException e) {
		}
	}

	public void gameUpdate() {

		if (bat != null) {
			bat.updateOnlyAnimation();
		}

		if (aliens != null) {
			for (int i = 0; i < aliens.length; i++) {
				if (aliens[i] != null) {
					aliens[i].move();
				}
			}
		}

		// player in river
		inRiver = false;
		if (bat != null && solids != null) {
			Rectangle2D.Double player = bat.getBoundingRectangle();

			for (int i = 14; i <= 17; i++) {
				if (solids[i] != null &&
						player.intersects(solids[i].getBoundingRectangle())) {
					inRiver = true;
					break;
				}
			}
		}
		if (inRiver && !playedRiverSound) {
			soundManager.playClip("river", false);
			playedRiverSound = true;
		}
		if (!inRiver) {
			playedRiverSound = false;
			soundManager.stopClip("river");
		}

		// fox visible
		foxVisible = false;
		if (aliens != null) {
			for (int i = 0; i < aliens.length; i++) {
				if (aliens[i] != null && aliens[i].isAlive()) {
					double fx = aliens[i].getX();
					double fy = aliens[i].getY();

					if (fx + aliens[i].getWidth() >= cameraX &&
							fx <= cameraX + getVisibleWorldWidth() &&
							fy + aliens[i].getHeight() >= cameraY &&
							fy <= cameraY + getVisibleWorldHeight()) {
						foxVisible = true;
						break;
					}
				}
			}
		}

		if (foxVisible && !playedFoxAlertSound && !gameOver) {
			soundManager.playClip("fox", false);
			soundManager.stopClip("background");
			playedFoxAlertSound = true;
		}
		if (!foxVisible && playedFoxAlertSound) {
			soundManager.stopClip("fox");
			playedFoxAlertSound = false;

			if (!gameOver && !isPaused) {
				soundManager.playClip("background", true);
			}
		}

		gameOver = (foxesCollected >= NUM_ALIENS);

		if (gameOver && !playedGameOverSound) {
			soundManager.stopClip("fox");
			soundManager.stopClip("background");
			soundManager.playClip("gameover", false);
			playedGameOverSound = true;
		}

		((DisappearFX) imageFX).setActive(inRiver);
		((GrayScaleFX) imageFX2).setActive(gameOver);
		((TintFX) imageFX3).setActive(foxVisible && !gameOver);

		imageFX.update();
		imageFX2.update();
		imageFX3.update();

		if (gameOver) {
			currentEffectName = "Game Over: GrayScale";
		}
		else if (foxVisible && inRiver) {
			currentEffectName = "Oh no! You're in water and there are foxes: Disappear & Tint";
		}
		else if (inRiver) {
			currentEffectName = "Oh no! You're in water: Disappear";
		}
		else if (foxVisible) {
			currentEffectName = "Oh Look! A fox!: Orange Tint";
		}
		else {
			currentEffectName = "Lucky You!: None";
		}
	}

	public void updateBat(int direction) {

		if (bat == null || isPaused || gameOver) return;

		int step = 10;

		int oldX = bat.getX();
		int oldY = bat.getY();
		int oldCameraX = cameraX;
		int oldCameraY = cameraY;

		int newX = bat.getX();
		int newY = bat.getY();

		bat.setDirection(direction);

		if (direction == 1) {   // left
			if (bat.getX() > 0) {
				newX = bat.getX() - step;
				if (newX < 0) newX = 0;
				bat.setX(newX);
			}
			else if (cameraX > 0) {
				cameraX -= step;
				if (cameraX < 0) cameraX = 0;
			}
		}
		else if (direction == 2) {   // right
			if (bat.getX() < panelWidth - bat.getWidth()) {
				newX = bat.getX() + step;
				if (newX > panelWidth - bat.getWidth()) {
					newX = panelWidth - bat.getWidth();
				}
				bat.setX(newX);
			}
			else if (cameraX < worldWidth - getVisibleWorldWidth()) {
				cameraX += step;
				if (cameraX > worldWidth - getVisibleWorldWidth()) {
					cameraX = worldWidth - getVisibleWorldWidth();
				}
			}
		}
		else if (direction == 3) {   // up
			if (bat.getY() > 0) {
				newY = bat.getY() - step;
				if (newY < 0) newY = 0;
				bat.setY(newY);
			}
			else if (cameraY > 0) {
				cameraY -= step;
				if (cameraY < 0) cameraY = 0;
			}
		}
		else if (direction == 4) {   // down
			if (bat.getY() < panelHeight - bat.getHeight()) {
				newY = bat.getY() + step;
				if (newY > panelHeight - bat.getHeight()) {
					newY = panelHeight - bat.getHeight();
				}
				bat.setY(newY);
			}
			else if (cameraY < worldHeight - getVisibleWorldHeight()) {
				cameraY += step;
				if (cameraY > worldHeight - getVisibleWorldHeight()) {
					cameraY = worldHeight - getVisibleWorldHeight();
				}
			}
		}

		bat.setCamera(cameraX, cameraY);

		if (collidesWithBlockingSolid()) {
			bat.setX(oldX);
			bat.setY(oldY);
			cameraX = oldCameraX;
			cameraY = oldCameraY;
			bat.setCamera(cameraX, cameraY);
		}
	}

	private boolean collidesWithBlockingSolid() {
		if (bat == null || solids == null) return false;

		Rectangle2D.Double player = bat.getBoundingRectangle();

		for (int i = 0; i < solids.length; i++) {
			if (solids[i] == null) continue;

			if (i >= 14 && i <= 17) continue;

			if (player.intersects(solids[i].getBoundingRectangle())) {
				return true;
			}
		}
		return false;
	}

	public void gameRender() {

		Graphics2D imageContext = (Graphics2D) image.getGraphics();
		imageContext.clearRect(0, 0, panelWidth, panelHeight);

		if (backgroundImage != null) {
			int srcX1 = cameraX;
			int srcY1 = cameraY;
			int srcX2 = cameraX + getVisibleWorldWidth();
			int srcY2 = cameraY + getVisibleWorldHeight();

			if (srcX1 < 0) srcX1 = 0;
			if (srcY1 < 0) srcY1 = 0;
			if (srcX2 > worldWidth) srcX2 = worldWidth;
			if (srcY2 > worldHeight) srcY2 = worldHeight;

			imageContext.drawImage(
					backgroundImage,
					0, 0, panelWidth, panelHeight,
					srcX1, srcY1, srcX2, srcY2,
					null
			);
		}

		// foxes
		if (aliens != null) {
			for (int i = 0; i < aliens.length; i++) {
				if (aliens[i] != null) {
					aliens[i].draw(imageContext, cameraX, cameraY);
				}
			}
		}

		// player
		if (bat != null) {
			bat.setCamera(cameraX, cameraY);

			Graphics2D playerContext = (Graphics2D) imageContext.create();

			float alpha = 1.0f;
			if (imageFX instanceof DisappearFX) {
				alpha = ((DisappearFX) imageFX).getAlpha();
			}

			playerContext.setComposite(
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
			);
			bat.draw(playerContext);
			playerContext.dispose();
		}

		if (imageFX3 instanceof TintFX) {
			((TintFX) imageFX3).applyToImage(image);
		}

		if (imageFX2 instanceof GrayScaleFX) {
			((GrayScaleFX) imageFX2).applyToImage(image);
		}

		if (gameOver) {
			imageFX2.draw(imageContext);
			imageContext.setColor(Color.WHITE);
			imageContext.setFont(new Font("Arial", Font.BOLD, 36));
			imageContext.drawString("GAME OVER", 130, 240);

			imageContext.setFont(new Font("Arial", Font.PLAIN, 18));
			imageContext.drawString("All foxes collected!", 145, 275);
		}

		Graphics2D g2 = (Graphics2D) getGraphics();
		if (g2 != null) {
			g2.drawImage(image, 0, 0, panelWidth, panelHeight, null);
			g2.dispose();
		}

		imageContext.dispose();
	}

	public void startGame() {
		if (isRunning) return;

		isPaused = false;
		createGameEntities();
		if(!foxVisible && !isPaused) {
			soundManager.playClip("background", true);
		}
		gameThread = new Thread(this);
		gameThread.start();
	}

	private int worldToScreenX(double worldX) {
		return (int)Math.round((worldX - cameraX) * ZOOM);
	}

	private int worldToScreenY(double worldY) {
		return (int)Math.round((worldY - cameraY) * ZOOM);
	}

	private int worldToScreenSize(double value) {
		return (int)Math.round(value * ZOOM);
	}

	private int getVisibleWorldWidth() {
		return (int)Math.round(panelWidth / ZOOM);
	}

	private int getVisibleWorldHeight() {
		return (int)Math.round(panelHeight / ZOOM);
	}

	public void shoot() {
		if (bat == null || aliens == null || isPaused || gameOver) return;

		bat.startShooting();

		for (int i = 0; i < aliens.length; i++) {
			if (aliens[i] != null && aliens[i].isAlive() && !aliens[i].isDying()) {
				if (bat.getShootRange().intersects(aliens[i].getBoundingRectangle())) {
					aliens[i].startDeath();
					foxesCollected++;
					break;
				}
			}
		}
	}

	public void pauseGame() {
		if (isRunning) {
			isPaused = !isPaused;
		}
	}

	public void stopBat() {
		if (bat != null) {
			bat.standStill();
		}
	}

	public void endGame() {
		isRunning = false;
		soundManager.stopClip("background");
		soundManager.stopClip("river");
	}

	public boolean isOnBat(int x, int y) {
		return bat != null && bat.isOnBat(x, y);
	}

	public int getFPS() {
		return fps;
	}

	public String getCurrentEffectName() {
		return currentEffectName;
	}

	public String getPlayerPosition() {
		if (bat == null) return "(0,0)";

		int worldX = bat.getX() + cameraX;
		int worldY = bat.getY() + cameraY;

		return "(" + worldX + ", " + worldY + ")";
	}

	public int getFoxesCollected() {
		return foxesCollected;
	}

}