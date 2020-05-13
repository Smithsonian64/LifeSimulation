import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Window extends JFrame{

    public static boolean debug = false;
    public static float deltaTime = 1;
    public static BufferedImage blank;
    public static BufferedImage screen;
    public static ArrayList<Entity> graphicsQueue;
    public static int cellBeginIndex = 0;
    public static int cellEndIndex = 0;
    public static int foodBeginIndex = 0;
    public static int foodEndIndex = 0;
    public static ArrayList<Entity> cellList;
    public static ArrayList<Entity> foodList;

    public static Timer foodSpawn;
    public static TimerTask foodSpawnTask;

    public static Window sim;

    public Window(int width, int height) {
        sim = this;

        foodSpawn = new Timer();
        foodSpawn.scheduleAtFixedRate(foodSpawnTask = new FoodSpawnTask(), 500, 500);


        blank = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        cellList = new ArrayList<>();
        foodList = new ArrayList<>();

        this.setSize(screen.getWidth(), screen.getHeight());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setVisible(true);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                    return;
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

    }

    void doIterations() throws InterruptedException {

        Runnable reRaster = () -> {
            screen = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
        };

        Thread reRasterT = new Thread(reRaster);

        Runnable run = () -> {
            double currentTime;
            double lastTime = System.nanoTime();
            double totalTime;
            float frameTime = 0;
            Graphics2D g2d;
            while(true) {


                currentTime = System.nanoTime();
                totalTime = (currentTime - lastTime) / 1000000000;
                lastTime = currentTime;
                frameTime += totalTime;


                //System.out.println(deltaTime);
                if(frameTime < 1f/60f) continue;
                deltaTime = frameTime;
                frameTime = 0;



                //screen.setData(blank.getRaster());
                reRasterT.run();
                //reRasterT.join();
                //screen = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);

                g2d = screen.createGraphics();
                g2d.drawImage(blank, screen.getWidth(), screen.getHeight(), null);
                for (int i = 0; i < foodList.size(); i++) {
                    foodList.get(i).update(1);
                }
                for (int i = 0; i < cellList.size(); i++) {
                    cellList.get(i).update(1);
                }
                for (int i = 0; i < foodList.size(); i++) {
                    foodList.get(i).draw(screen);
                }
                for (int i = 0; i < cellList.size(); i++) {
                    cellList.get(i).draw(screen);
                }
                /*for (int i = 0; i < graphicsQueue.size(); i++) {
                    graphicsQueue.get(i).draw(screen);
                }*/
                repaint();
            }
        };



        Thread runT = new Thread(run);
        runT.join();
        runT.start();

    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(screen, 0, 0, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater( () -> {
            Window sim = new Window((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()));

            for(int i = 0; i < 20; i++) {
                sim.addCell((int) (Math.random() * 1920), (int) (Math.random() * 1080),  (float) (Math.random() * Math.PI * 2), (int) (Math.random() * 37) + 3,
                        new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255), 255));

            }

            for(int i = 0; i <  100; i++) {
                sim.addFood((int) (Math.random() * 1920), (int) (Math.random() * 1080), (float) Math.PI, 10);
                //System.out.println(graphicsQueue.toString());
            }

            try {
                sim.doIterations();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void addCell(int x, int y, float heading, int size, Color color) {
        cellList.add(cellEndIndex, new Animalcule(x, y, heading, size, color));
    }

    public void addFood(int x, int y, float heading, int size) {
        foodList.add(foodEndIndex, new Food(x, y, heading, size));

    }
}

class FoodSpawnTask extends TimerTask {

    public FoodSpawnTask() {

    }

    @Override
    public void run() {
        Window.sim.addFood((int) (Math.random() * 1920), (int) (Math.random() * 1080), (float) Math.PI, 10);
    }
}