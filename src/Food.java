import java.awt.*;
import java.awt.image.BufferedImage;

public class Food extends Entity{

    Graphics2D g2d;

    public Food(float x, float y, float heading, float size) {
        super(x, y, heading, size);
    }

    @Override
    public BufferedImage draw(BufferedImage screen) {
        g2d = screen.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.fillOval(Math.round(pos.x - size/2), Math.round(pos.y - size/2), Math.round(size), Math.round(size));
        g2d.drawImage(screen, screen.getWidth(), screen.getHeight(), null);
        return screen;
    }

    @Override
    public void update(float dt) {

    }

    public void remove() {
        Window.foodList.remove(this);
    }
}
