import java.awt.image.BufferedImage;

public abstract class Entity {

    public Vector2 pos;
    float size;
    float heading;

    public Entity(float x, float y, float heading, float size) {
        pos = new Vector2(x, y);
        this.size = size;
        this.heading = heading;
    }

    public abstract BufferedImage draw(BufferedImage screen);

    public abstract void update(float dt);

    public abstract void remove();



}
