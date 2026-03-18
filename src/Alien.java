import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import java.util.Random;

public class Alien {

   private JPanel panel;

   private int x;
   private int y;

   private int width;
   private int height;

   private int dx;

   private boolean facingLeft;

   private Random random;

   private Bat bat;
   private SoundManager soundManager;

   private BufferedImage[][] runFrames;
   private int currentFrame;
   private int frameCounter;
   private int frameDelay;

   private int worldWidth;
   private int worldHeight;

   private BufferedImage[][] deathFrames;
   private boolean alive = true;
   private boolean dying = false;
   private boolean removable = false;
   private int deathFrame = 0;
   private int deathCounter = 0;
   private int deathDelay = 5;

   public Alien(JPanel p, int xPos, int yPos, Bat bat, int worldWidth, int worldHeight) {
      panel = p;

      this.bat = bat;
      this.worldWidth = worldWidth;
      this.worldHeight = worldHeight;

      soundManager = SoundManager.getInstance();
      random = new Random();

      width = 64;
      height = 64;

      x = xPos;
      y = yPos;

      dx = 4;
      facingLeft = false;

      currentFrame = 0;
      frameCounter = 0;
      frameDelay = 5;

      loadFoxFrames();
      loadDeathFrames();
      chooseDirection();
   }

   private void loadFoxFrames() {
      BufferedImage runSheet =
              ImageManager.loadBufferedImage("images/Fox_Run_with_shadow.png");

      if (runSheet == null) {
         System.out.println("Could not load images/Fox_Run_with_shadow.png");
         return;
      }

      int rows = 4;
      int cols = 6;

      int frameW = runSheet.getWidth() / cols;
      int frameH = runSheet.getHeight() / rows;

      runFrames = new BufferedImage[rows][cols];

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < cols; c++) {
            runFrames[r][c] = runSheet.getSubimage(c * frameW, r * frameH, frameW, frameH);
         }
      }
   }

   private void chooseDirection() {
      if (random.nextBoolean()) {
         dx = 4;
         facingLeft = false;
         x = -width;      // enter left
      } else {
         dx = -4;
         facingLeft = true;
         x = worldWidth;  // enter right
      }

      y = random.nextInt(worldHeight - height);
   }

   private void updateAnimation() {
      frameCounter++;

      if (frameCounter >= frameDelay) {
         frameCounter = 0;
         currentFrame++;

         if (currentFrame >= 6) {
            currentFrame = 0;
         }
      }
   }

   public void draw(Graphics2D g2, int cameraX, int cameraY) {
      if ((!alive && !dying) || runFrames == null) return;

      int drawX = x - cameraX;
      int drawY = y - cameraY;

      BufferedImage currentImage;

      if (dying && deathFrames != null) {
         if (facingLeft) {
            currentImage = deathFrames[3][deathFrame];
         } else {
            currentImage = deathFrames[2][deathFrame];
         }
      }
      else {
         if (facingLeft) {
            currentImage = runFrames[3][currentFrame];
         } else {
            currentImage = runFrames[2][currentFrame];
         }
      }

      g2.drawImage(currentImage, drawX, drawY, width, height, null);
   }

   public void move() {
      if (!panel.isVisible()) return;

      if (dying) {
         deathCounter++;
         if (deathCounter >= deathDelay) {
            deathCounter = 0;
            deathFrame++;
            if (deathFrame >= 6) {
               dying = false;
               alive = false;
               removable = true;
            }
         }
         return;
      }

      if (!alive) return;

      x += dx;

      if (dx > 0 && x > worldWidth) {
         x = -width;
         y = random.nextInt(worldHeight - height);
      }
      else if (dx < 0 && x < -width) {
         x = worldWidth;
         y = random.nextInt(worldHeight - height);
      }

      updateAnimation();
   }

   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double(x, y, width, height);
   }

   public boolean collidesWithBat() {
      Rectangle2D.Double myRect = getBoundingRectangle();
      Rectangle2D.Double batRect = bat.getBoundingRectangle();
      return myRect.intersects(batRect);
   }

   private void loadDeathFrames() {
      BufferedImage deathSheet =
              ImageManager.loadBufferedImage("images/Fox_Death_with_shadow.png");

      if (deathSheet == null) {
         System.out.println("Could not load images/Fox_Death_with_shadow.png");
         return;
      }

      int rows = 4;
      int cols = 6;

      int frameW = deathSheet.getWidth() / cols;
      int frameH = deathSheet.getHeight() / rows;

      deathFrames = new BufferedImage[rows][cols];

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < cols; c++) {
            deathFrames[r][c] = deathSheet.getSubimage(c * frameW, r * frameH, frameW, frameH);
         }
      }
   }

   public void startDeath() {
      if (!alive || dying) return;

      dying = true;
      deathFrame = 0;
      deathCounter = 0;
      soundManager.playClip("shot", false);
   }

   public boolean isAlive() {
      return alive;
   }

   public boolean isDying() {
      return dying;
   }

   public boolean isRemovable() {
      return removable;
   }

   public int getX() { return x; }
   public int getY() { return y; }
   public int getWidth() { return width; }
   public int getHeight() { return height; }
}