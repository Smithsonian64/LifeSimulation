public class Vector2 {

    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void translate(float x, float y) {
        this.x += x * Window.deltaTime;
        this.y += y * Window.deltaTime;
        System.out.println(this.x);
    }

    public void move(float distance, float angle) {
        double changeX = distance * Window.deltaTime * Math.sin(angle);
        double changeY = distance * Window.deltaTime * Math.cos(angle);
        if (this.x + changeX <= 1920 && this.x + changeX >= 0) this.x += changeX;
        if (this.y + changeY <= 1080 && this.y + changeY >= 0) this.y += changeY;
    }
}
