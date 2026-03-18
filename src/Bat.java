import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

public class Bat {

   private JPanel panel;
   private int x;
   private int y;
   private int width;
   private int height;

   private int dx;
   private int dy;

   private SolidObject[] solids;
   private boolean facingLeft = false;

   private BufferedImage[][] idleFrames;
   private BufferedImage[][] walkFrames;
   private BufferedImage[][] shootFrames;

   private int currentRow;
   private int currentFrame;

   private int frameCounter;
   private int frameDelay;
   private int idleFrameDelay = 6;
   private int walkFrameDelay = 5;
   private int shootFrameDelay = 2;

   private boolean moving;
   private boolean shooting;

   private int cameraX;
   private int cameraY;

   private int lastDirection = 2;   // 1:left, 2:right, 3:up, 4:down

   public Bat(JPanel p, int xPos, int yPos, SolidObject[] solids) {
      panel = p;
      x = xPos;
      y = yPos;
      this.solids = solids;

      dx = 10;
      dy = 10;

      width = 64;
      height = 64;

      currentRow = 0;
      currentFrame = 0;
      frameCounter = 0;
      frameDelay = 6;

      moving = false;
      shooting = false;

      cameraX = 0;
      cameraY = 0;

      loadFarmerFrames();
   }

   private void loadFarmerFrames() {
      BufferedImage idleSheet = ImageManager.loadBufferedImage("images/Idle.png");
      BufferedImage walkSheet = ImageManager.loadBufferedImage("images/Walk.png");

      BufferedImage shootSheet = ImageManager.loadBufferedImage("images/shoot.png");
      if (shootSheet != null) {
         shootSheet = makeBlackTransparent(shootSheet);
      }

      if (idleSheet == null || walkSheet == null || shootSheet == null) {
         System.out.println("Farmer sprite sheets could not be loaded.");
         return;
      }

      int idleRows = 3;
      int idleCols = 4;

      int walkRows = 3;
      int walkCols = 6;

      int shootRows = 3;
      int shootCols = 4;

      int idleW = idleSheet.getWidth() / idleCols;
      int idleH = idleSheet.getHeight() / idleRows;

      int walkW = walkSheet.getWidth() / walkCols;
      int walkH = walkSheet.getHeight() / walkRows;

      int shootW = shootSheet.getWidth() / shootCols;
      int shootH = shootSheet.getHeight() / shootRows;

      idleFrames = new BufferedImage[idleRows][idleCols];
      walkFrames = new BufferedImage[walkRows][walkCols];
      shootFrames = new BufferedImage[shootRows][shootCols];

      for (int r = 0; r < idleRows; r++) {
         for (int c = 0; c < idleCols; c++) {
            idleFrames[r][c] = idleSheet.getSubimage(c * idleW, r * idleH, idleW, idleH);
         }
      }

      for (int r = 0; r < walkRows; r++) {
         for (int c = 0; c < walkCols; c++) {
            walkFrames[r][c] = walkSheet.getSubimage(c * walkW, r * walkH, walkW, walkH);
         }
      }

      for (int r = 0; r < shootRows; r++) {
         for (int c = 0; c < shootCols; c++) {
            shootFrames[r][c] = shootSheet.getSubimage(c * shootW, r * shootH, shootW, shootH);
         }
      }
   }

   private void updateAnimation() {
      frameCounter++;

      int delay;

      if (shooting) {
         delay = shootFrameDelay;
      }
      else if (moving) {
         delay = walkFrameDelay;
      }
      else {
         delay = idleFrameDelay;
      }

      if (frameCounter >= delay) {
         frameCounter = 0;
         currentFrame++;

         if (shooting) {
            if (currentFrame >= 4) {
               shooting = false;
               moving = false;
               currentFrame = 0;
               frameCounter = 0;
            }
         }
         else if (moving) {
            if (currentFrame >= 6) {
               currentFrame = 0;
            }
         }
         else {
            if (currentFrame >= 4) {
               currentFrame = 0;
            }
         }
      }
   }

   public void draw(Graphics2D g2) {
      if (idleFrames == null || walkFrames == null || shootFrames == null) return;

      BufferedImage currentImage;

      if (shooting) {
         int shootRow;

         if (currentRow == 0) {          // down
            shootRow = 0;
         }
         else if (currentRow == 1) {     // up
            shootRow = 1;
         }
         else {                          // left/right
            shootRow = 2;
         }

         currentImage = shootFrames[shootRow][currentFrame];
      }
      else if (moving) {
         currentImage = walkFrames[currentRow][currentFrame];
      }
      else {
         currentImage = idleFrames[currentRow][currentFrame];
      }

      int drawX = x;
      int drawY = y;

      if (shooting) {
         drawX = x - 8;
         drawY = y - 4;
      }

      if ((currentRow == 2) && facingLeft) {
         g2.drawImage(currentImage, drawX + width, drawY, -width, height, null);
      }
      else {
         g2.drawImage(currentImage, drawX, drawY, width, height, null);
      }
   }

   public void setDirection(int direction) {
      if (shooting) return;

      moving = true;
      lastDirection = direction;

      if (direction == 1) {
         currentRow = 2;
         facingLeft = true;
      }
      else if (direction == 2) {
         currentRow = 2;
         facingLeft = false;
      }
      else if (direction == 3) {
         currentRow = 1;
      }
      else if (direction == 4) {
         currentRow = 0;
      }

      updateAnimation();
   }

   public void standStill() {
      if (!shooting) {
         moving = false;
         currentFrame = 0;   // go to first idle frame cleanly
         frameCounter = 0;
      }
   }

   public void startShooting() {
      if (shooting) return;

      shooting = true;
      moving = false;
      currentFrame = 0;
      frameCounter = 0;
   }

   public boolean isShooting() {
      return shooting;
   }

   public Rectangle2D.Double getShootRange() {
      int range = 110;

      int shotHeight = 50;
      int shotWidth = 40;

      int shootX = x - 8;
      int shootY = y - 4;

      if (lastDirection == 1) {   // left
         return new Rectangle2D.Double(
                 shootX + cameraX - range + 10,
                 shootY + cameraY + 8,
                 range,
                 shotHeight
         );
      }
      else if (lastDirection == 2) {   // right
         return new Rectangle2D.Double(
                 shootX + cameraX + width - 10,
                 shootY + cameraY + 8,
                 range,
                 shotHeight
         );
      }
      else if (lastDirection == 3) {   // up
         return new Rectangle2D.Double(
                 shootX + cameraX + 12,
                 shootY + cameraY - range + 10,
                 shotWidth,
                 range
         );
      }
      else {   // down
         return new Rectangle2D.Double(
                 shootX + cameraX + 12,
                 shootY + cameraY + height - 10,
                 shotWidth,
                 range
         );
      }
   }

   private BufferedImage makeBlackTransparent(BufferedImage image) {
      BufferedImage newImage = new BufferedImage(
              image.getWidth(),
              image.getHeight(),
              BufferedImage.TYPE_INT_ARGB
      );

      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int argb = image.getRGB(x, y);

            int a = (argb >> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            if (r < 25 && g < 25 && b < 25) {
               newImage.setRGB(x, y, 0x00000000);
            } else {
               newImage.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
         }
      }

      return newImage;
   }

   public void updateOnlyAnimation() {
      if (shooting || moving) {
         updateAnimation();
      }
   }

   public void setCamera(int cameraX, int cameraY) {
      this.cameraX = cameraX;
      this.cameraY = cameraY;
   }

   public boolean isOnBat(int px, int py) {
      Rectangle2D.Double bat = getBoundingRectangle();
      return bat.contains(px, py);
   }

   public boolean collidesWithSolid() {
      if (solids == null) return false;

      Rectangle2D.Double myRect = getBoundingRectangle();

      for (int i = 0; i < solids.length; i++) {
         if (solids[i] != null && myRect.intersects(solids[i].getBoundingRectangle())) {
            return true;
         }
      }
      return false;
   }

   public Rectangle2D.Double getBoundingRectangle() {
      double zoom = 1.20;
      double boxScreenW = 22;
      double boxScreenH = 14;

      double boxWorldW = boxScreenW / zoom;
      double boxWorldH = boxScreenH / zoom;

      double worldSpriteX = cameraX + (x / zoom);
      double worldSpriteY = cameraY + (y / zoom);

      double worldX = worldSpriteX + ((width / zoom) - boxWorldW) / 2.0;
      double worldY = worldSpriteY + (height / zoom) - boxWorldH - (4 / zoom);

      return new Rectangle2D.Double(worldX, worldY, boxWorldW, boxWorldH);
   }

   public int getX() { return x; }
   public int getY() { return y; }
   public int getWidth() { return width; }
   public int getHeight() { return height; }
   public void setX(int x) { this.x = x; }
   public void setY(int y) { this.y = y; }
}