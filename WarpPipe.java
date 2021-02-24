import java.awt.Rectangle;


/**
 * Represents a pair of warp pipes. One pipe is at the bottom of the screen and
 * one pipe is at the top of the screen.
 */
public class WarpPipe {

    /**
     * Each Warp Pipe concists of tho parts: one pipe that grows out of the top of
     * the screen (downwards) and one pipe that grows out of the bottom.
     */
    private Rectangle rectangle1;
    private Rectangle rectangle2;

    /**
     * 
     * @param width  Width of one warp pipe.
     * @param height Height of one warp pipe.
     */
    public WarpPipe(int width, int height) {
        int x1 = width;
        int y1 = 0;
        int x2 = width;
        int y2 = height / 2;

        rectangle1 = new Rectangle(x1, y1, 60, 300);
        rectangle2 = new Rectangle(x2, y2, 60, 400);

    }

    /**
     * Check if a Rectangle intersects with this Warp Pipe.
     * 
     * @param r Rectangle.
     * @return True if warp pipe intersects with rectangle r.
     */
    boolean intersects(Rectangle r) {
        return rectangle1.intersects(r) || rectangle2.intersects(r);
    }

    /**
     * Displace Warp Pipes.
     * 
     * @param dx Displacement on x-axis.
     * @param dy Displacement on y-axis.
     */
    void translate(int dx, int dy) {
        rectangle1.translate(dx, dy);
        rectangle2.translate(dx, dy);
    }

    /**
     * 
     * @return True if Warp Pipes have moved beyond left side of screen.
     */
    boolean noLongerOnScreen() {
        if (rectangle1.x + rectangle1.width < 0)
            return true;
        else
            return false;
    }

    boolean halfwayAcrossScreen(int width) {
        if (rectangle1.x + rectangle1.width < width / 2)
            return true;
        else
            return false;
    }

    public Rectangle getRectangle1() {
        return rectangle1;
    }

    public Rectangle getRectangle2() {
        return rectangle2;
    }

}
