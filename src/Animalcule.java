import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;


public class Animalcule extends Entity{

    Graphics2D g2d;

    float range;
    float speed;
    int[] sightPointsX;
    int[] sightPointsY;
    boolean inPursuit;
    Entity target;
    Color color;

    public int hungerInteraval = 60;
    public int hungerCounter = 0;




    public Animalcule(int x, int y, float heading, int size, Color color) {
        super(x, y, heading, size);
        speed = 100f;

        sightPointsX = new int[3];
        sightPointsY = new int[3];
        range = 150f;

        this.color = color;


    }

    @Override
    public BufferedImage draw(BufferedImage screen) {
        g2d = screen.createGraphics();
        g2d.setColor(color);
        g2d.fillOval(Math.round(pos.x - size/2), Math.round(pos.y - size/2), Math.round(size), Math.round(size));
        g2d.setColor(Color.BLUE);
        g2d.drawLine(Math.round(pos.x), Math.round(pos.y), (int) Math.round(pos.x + size/2 * Math.sin(heading)), (int) Math.round(pos.y + size/2 * Math.cos(heading)));

        if(Window.debug) {
            sightPointsX[0] = (int) (pos.x);
            sightPointsX[1] = (int) (pos.x + range * Math.sin(heading - Math.PI / 8));
            sightPointsX[2] = (int) (pos.x + range * Math.sin(heading + Math.PI / 8));

            sightPointsY[0] = (int) (pos.y);
            sightPointsY[1] = (int) (pos.y + range * Math.cos(heading - Math.PI / 8));
            sightPointsY[2] = (int) (pos.y + range * Math.cos(heading + Math.PI / 8));
        }

        g2d.setColor(new Color(255,0,255,60));
        g2d.fillPolygon(sightPointsX, sightPointsY, 3);

        g2d.rotate(heading);

        g2d.drawImage(screen, screen.getWidth(), screen.getHeight(), null);
        return screen;
    }

    @Override
    public void update(float dt) {
        speed = 3000 / size;

        if(hungerCounter >= hungerInteraval) {
            hungerCounter = 0;
            size -= 1;
        }
        if(size < 5) this.remove();

        if(size >= 50) {
            size = (float) (Math.sqrt(Math.PI*Math.pow(size, 2) / (2 * Math.PI)));
            Window.sim.addCell((int) pos.x, (int) pos.y, heading, (int) size - 1, color);
            System.out.println(Window.cellList.size());
        }

        if(checkForCellTarget()) {
            fightOrFlight();
            pos.move(speed, heading);
            return;
        }
        if(checkForFoodTarget()) {
            lookForFood();
            pos.move(speed, heading);
            return;
        }

        wander();

        pos.move(speed, heading);

        hungerCounter += 1 * dt;
    }

    public void wander() {
        heading = heading + (float) (Math.random() * Math.PI/4 - Math.PI/8);
    }


    void lookForFood() {

        heading = (float) Math.atan2(target.pos.x - pos.x, target.pos.y - pos.y);

        //(x - center_x)^2 + (y - center_y)^2 < radius^2.
        if(Math.pow(target.pos.x - pos.x, 2) + Math.pow(target.pos.y - pos.y, 2) < Math.pow(size/2, 2)) {
            target.remove();
            size += 1 / Math.log(size);
            target = null;
        }


    }

    void fightOrFlight() {

        if(size > target.size) {
            heading = (float) Math.atan2(target.pos.x - pos.x, target.pos.y - pos.y);
        }

        if(size > target.size && Math.pow(target.pos.x - pos.x, 2) + Math.pow(target.pos.y - pos.y, 2) < Math.pow(size/2, 2)) {

            size += (Math.sqrt(Math.PI*Math.pow(target.size, 2) / Math.PI));
            target.remove();
            target = null;
        }

    }

    public void remove() {
        Window.cellList.remove(this);
    }

    public boolean checkForCellTarget() {
        target = null;
        float distance;
        float shortestDistance = Float.MAX_VALUE;

        for(int i = 0; i < Window.cellList.size(); i++) {
            distance = (float) Point2D.distance(pos.x, pos.y, Window.cellList.get(i).pos.x, Window.cellList.get(i).pos.y);
            if(distance < shortestDistance && distance <= range && Window.cellList.get(i) != this) {
                target = Window.cellList.get(i);
            }
        }

        if(target == null || ((Animalcule) target).color.getRGB() == color.getRGB()) return false;
        else return true;
    }

    boolean checkForFoodTarget() {
        target = null;
        float distance;
        float shortestDistance = Float.MAX_VALUE;

        for(int i = 0; i < Window.foodList.size(); i++) {
            distance = (float) Point2D.distance(pos.x, pos.y, Window.foodList.get(i).pos.x, Window.foodList.get(i).pos.y);
            if(distance < shortestDistance && distance <= range) {
                shortestDistance = distance;
                target = Window.foodList.get(i);
            }
        }

        if(target == null || shortestDistance == Float.MAX_VALUE) return false;
        else return true;
    }


}
