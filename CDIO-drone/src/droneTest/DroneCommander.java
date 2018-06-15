/**
 * Author: Simon Christiansen
 */


package droneTest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import imageDetection.Circle;
import imageDetection.CircleListener;
import imageDetection.CircleScanner;
import droneTest.GUITest;

public class DroneCommander implements CircleListener{


	private ArrayList<CircleListener> listeners = new ArrayList<CircleListener>();
	
	private ARDrone drone = null;
	private CommandManager cmd = null;
	private droneGUI dronegui = null;
	
	private int speed = 30;  // The base velocity in %
	private int slowspeed = 5;  //  The velocity used for centralizing in %
	private int slowtime = 50;	//	The time centralizing commands are done in ms
	private double ErrorMargin = 35;	// the Margin of Error in which the center of a circle can be fount
	
	private int count = 0;	//	The counter for amount of centered circles 
	private int altcount = 0;
	private int methodecount = 0;  // Count Used to limit the calls done in findCircleCenter
	private int countmax = 6;	//	The amount of centered circles we need before flying through a circle
	private boolean findCircle = true;	//	When true we look for circles
	
	private double midPoint_x = GUITest.IMAGE_WIDTH/2;	// Camera midpoint in x
	private double midPoint_y = GUITest.IMAGE_HEIGHT/2;	// camera midpoint in y

	private double max_radius = 140;	// The size of the circle we want on the camera
	
	private Circle[] circles;
	
		/**
		 * The DroneCommander Constructor
		 * @param drone
		 * @param speed
		 * @param gui
		 */
	public DroneCommander(ARDrone drone, int speed, droneGUI gui){
		this.drone = drone;
		this.speed = speed;	
		this.dronegui = gui;
		cmd = drone.getCommandManager();
	}
	
	/**
	 * Take off Method
	 * The first method called when you need to fly
	 */
	public void takeOff(){
		cmd.takeOff().doFor(5000);
		System.out.println("Takeoff");
	}
	
	/**
	 * Hover Method
	 * Makes the drone stand still in place
	 * with 1000 given as the amount of time in ms
	 */
	public void hover(){
		cmd.hover().doFor(1000);
	}
	
	/**
	 * Hover Method
	 * @param ms
	 */
	public void hover(int ms){
		cmd.hover().doFor(ms);
	}
	
	/**
	 * Landing Method
	 * When the drone is done flying
	 */
	public void Landing(){
		cmd.landing();
		System.out.println("Landing");
	}
	
	/**
	 * Turn Left Method
	 * Makes the drone spin left with speed given as the speed 
	 * and the amount of time spinning given as a parameter in ms
	 * @param ms
	 */
	public void TurnLeft(int ms){
		cmd.spinLeft(speed).doFor(ms);
	}
	
	/**
	 * Right Left Method
	 * Makes the drone spin right with speed given as the speed 
	 * and the amount of time spinning given as a parameter in ms
	 * @param ms
	 */
	public void TurnRight(int ms){
		cmd.spinRight(speed).doFor(ms);
	}
	
	/**
	 * Overloaded method, speed is the speed of the drone, 
	 * ms is the time in milliseconds the drone will turn for
	 * @param ms
	 * @param speed
	 */
	public void TurnLeft(int ms, int speed){
		cmd.spinLeft(speed).doFor(ms);
	}
	
	/**
	 * Overloaded method, speed is the speed of the drone, 
	 * ms is the time in milliseconds the drone will turn for
	 * @param ms
	 * @param speed
	 */
	public void TurnRight(int ms, int speed){
		cmd.spinRight(speed).doFor(ms);
	}
	
	/**
	 * Fly Right Method
	 * makes the drone fly right with the given velocity of the variable speed
	 */
	public void flyRight(){
		cmd.goRight(speed);
	}
	
	/**
	 * Fly Left Method
	 * makes the drone fly left with the given velocity of the variable speed
	 */
	public void flyLeft(){
		cmd.goLeft(speed);
	}
	
	/**
	 * Fly forward Method
	 * makes the drone fly forward with the given velocity of the variable speed
	 */
	public void flyForward(){
		cmd.forward(speed);
	}
	
	/**
	 * Fly backward Method
	 * makes the drone fly backward with the given velocity of the variable speed
	 */
	public void flyBackward(){
		cmd.backward(speed);
	}
	
	/**
	 * Overloaded method, used to set speed of drone in %, and time it shall fly in ms
	 * @param speed
	 * @param ms
	 */
	public void flyRight(int speed, int ms){
		cmd.goRight(speed).doFor(ms);
	}
	
	/**
	 * Overloaded method, used to set speed of drone in %, and time it shall fly in ms
	 * @param speed
	 * @param ms
	 */
	public void flyLeft(int speed, int ms){
		cmd.goLeft(speed).doFor(ms);
	}
	
	/**
	 * Fly right method
	 * with time given as the slow time variable
	 * used for findCircleCenter()
	 * @param speed
	 */
	public void flyRight(int speed){
		cmd.goRight(speed).doFor(slowtime);
	}
	
	/**
	 * Fly left method
	 * with time given as the slow time variable
	 * used for findCircleCenter()
	 * @param speed
	 */
	public void flyLeft(int speed){
		cmd.goLeft(speed).doFor(slowtime);
	}
	
	/**
	 * Fly forward method
	 * with time given as the slow time variable
	 * used for findCircleCenter()
	 * @param speed
	 */
	public void flyForward(int speed){
		cmd.forward(speed).doFor(slowtime);
	}
	
	/**
	 * Overloaded method, used to set speed of drone in %, and time it shall fly in ms
	 * @param speed
	 * @param ms
	 */
	public void flyForward(int speed, int ms){
		cmd.forward(speed).doFor(ms);
	}
	
	/**
	 * Overloaded method, used to set speed of drone in %, and time it shall fly in ms
	 * @param speed
	 * @param ms
	 */
	public void flyBackward(int speed, int ms){
		cmd.backward(speed).doFor(ms);
	}
	
	/**
	 * Fly Backward method
	 * with time given as the slow time variable
	 * used for findCircleCenter()
	 * @param speed
	 */
	public void flyBackward(int speed){
		cmd.backward(speed).doFor(slowtime);
	}
	
	/**
	 * Increase Altitude method
	 * with time given as the slow time variable
	 * used for findCircleCenter()
	 * @param speed
	 */
	public void increaseAltitude(int speed){
		cmd.up(speed).doFor(slowtime);
	}
	
	/**
	 * Fly Backward method
	 * @param speed
	 * @param ms
	 */
	public void increaseAltitude(int speed, int ms){
		cmd.up(speed).doFor(ms);
	}
	
	/**
	 * decrease altitude method
	 * with time given as the slow time variable
	 * used for findCircleCenter()
	 * @param speed
	 */
	public void decreaseAltitude(int speed){
		cmd.down(speed).doFor(slowtime);
	}
	
	/**
	 * set findcircle method
	 * set the findcircle variable to either true of false
	 * set to true when wanting to find circle
	 * set to false when you have flown through a circle
	 * @param true_false
	 */
	public void setFindCircle(boolean true_false){
		findCircle = true_false;
	}
	
	/**
	 * get find circle method
	 * when you want to get the findcircle variable
	 * @return
	 */
	public boolean getFindCircle(){
		return findCircle;
	}
	
	
	public void moveToAltitude(int height){
		DroneAlttitudeListener nav = new DroneAlttitudeListener();
		nav.addAltListener(drone);
		while(height + ErrorMargin > nav.getAltitude() || height - ErrorMargin < nav.getAltitude())
		{
			if (height - ErrorMargin > nav.getAltitude()) {
				cmd.up(slowspeed).doFor(slowtime);
			} 
			else if (height + ErrorMargin < nav.getAltitude()){
				cmd.down(slowspeed).doFor(slowtime);
			} 
			else 
				altcount++;
				if(altcount >= countmax){
					return;
				}
			{
			}
			nav.removeAltListener(drone);
			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Circle in center
	 * A boolean method used to see if the drone have found the circles center
	 * within the margin of error
	 * @param circle
	 * @return
	 */
	public boolean CircleInCenter(Circle circle){
		boolean inCenter = false;
				
		if(circle.x <= midPoint_x + ErrorMargin && circle.x >= midPoint_x - ErrorMargin){
			if(circle.y <= midPoint_y + ErrorMargin && circle.y >= midPoint_y - ErrorMargin){
				if(circle.r <= max_radius + (ErrorMargin/2) && circle.r >= max_radius - (ErrorMargin/2)){
					inCenter = true;
				}
			}
		}
		return inCenter;
	}
	
	/**
	 * Find Circle Center Method
	 * This method find the center of a circle
	 * to limit the amount of commands the methodecounter is used
	 * the thread sleeps for and given time in ms every 4th call of this method
	 * also to limit amount of commands called
	 * @param circle
	 */
	public void findCircleCenter() {
		
		while(count <= countmax){
			
		
		double circle_x = circles[0].x;
		double circle_y = circles[0].y;
		double circle_r = circles[0].r;
		
		if(methodecount == 0){
			if(circle_x <= midPoint_x + (ErrorMargin/2) == false && circle_x >= midPoint_x - (ErrorMargin/2) == false){
			if(circle_x > midPoint_x){
				flyRight(slowspeed);
				System.out.println("h�jre");
			}
			else if(circle_x < midPoint_x){
				flyLeft(slowspeed);
				System.out.println("venstre");
			}}
			methodecount = 1;
		}
		else if(methodecount ==1){
			if(circle_y <= midPoint_y + (ErrorMargin/2) == false && circle_y >= midPoint_y - (ErrorMargin/2) == false){
			if(circle_y > midPoint_y){
				decreaseAltitude(slowspeed);
				System.out.println("ned");
			}
			else if(circle_y < midPoint_y){
				increaseAltitude(slowspeed);
				System.out.println("up");
			}}
			methodecount = 2;
		}
		else if(methodecount == 2){
			if(circle_r <= max_radius + (ErrorMargin/2) == false && circle_r >= max_radius - (ErrorMargin/2) == false)
			if(circle_r > max_radius){
				flyBackward(slowspeed);
				System.out.println("bagud");
			}
			else if(circle_r < max_radius){
				flyForward(slowspeed);
				System.out.println("frem");
			}
			methodecount = 3;
		}		
		else if(methodecount == 3){
			
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		methodecount = 0;
		
		if(CircleInCenter(circles[0])){
			count++;
		}
		else{
			count = 0;
		}
		
		if(count >= countmax){
			return;
		}
		}}
	}
	
	/**
	 * Fly Through circle method
	 * This method calls findCircleCenter() until the drone has found the circle center countmax times in a row.
	 * then proceeds to fly straight through the circle.
	 * @param circle
	 */
	public void flythroughCircle(Circle[] circle){		
	if (circle != null){
				findCircleCenter();
					if(CircleInCenter(circle[0])){
						count++;
					}
					else{
						count = 0;
					}
				if(count >= countmax){
					setFindCircle(false);
					flyForward(35,2500);
					hover();
					Landing();
					System.out.println("Found Circle Center");
					return;
				}
				else{
				}		
		}
	}
	
	
	/**
	 * CirclesUpdated method comes from the implementation of circlelistener
	 * this method is called every time a circle is found in CircleScanner
	 * if the boolean findcircle is true
	 * this method calls flythroughCircle
	 */
	
	@Override
	public void circlesUpdated(Circle[] circles) {
		this.circles = circles;
	}
}