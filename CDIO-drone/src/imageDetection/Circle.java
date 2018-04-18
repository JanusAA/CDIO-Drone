package imageDetection;

import org.opencv.core.Point;

/**
 * Description of circle as a object. x,y values describes the center of the circle
 * r decribes the radius of the circle. 
 * @author janus
 *
 */

public class Circle {

	public double x, y, r;
	
	/**
	 * Circle constructor
	 * @param x coordinate for circle center
	 * @param y coordinate for circle center
	 * @param r Radius of circle
	 */
	public Circle(double x, double y, double r){
		this.x = x;
		this.y = y; 
		this.r = r;
	}
	
	public Circle(int x, int y, int r){
		this.x = (double) x;
		this.y = (double) y;
		this.r = (double) r;
	}
	
	/**
	 * get method which returns a openCV object (Point) which is the middle coordinates of the circle
	 * @return
	 */
	public Point getPoint(){
		return new Point(this.x, this.y);
		
	}
	/**
	 * Get method which returns radius value (double)
	 * @return
	 */
	public double getRadius(){
		return this.r;
	}
	
}
