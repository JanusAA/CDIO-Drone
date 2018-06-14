package droneTest;

import java.awt.Color;
import java.util.ArrayList;

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import imageDetection.Circle;
import imageDetection.CircleListener;
import droneTest.GUITest;

public class DroneCommander implements CircleListener{


	private ArrayList<CircleListener> listeners = new ArrayList<CircleListener>();
	
	private ARDrone drone = null;
	private CommandManager cmd = null;
	private DroneAlttitudeListener nav;
	private int speed = 30;
	private int slowspeed = 2;
	
	private double midPoint_x = GUITest.IMAGE_WIDTH/2;
	private double midPoint_y = GUITest.IMAGE_HEIGHT/2;
	private double max_radius = 125;
	
	private Circle[] circles;
	
		
	public DroneCommander(ARDrone drone, int speed){
		this.drone = drone;
		this.speed = speed;	
		cmd = drone.getCommandManager();
		nav = new DroneAlttitudeListener(drone);
	}
	
	public void takeOff(){
		cmd.takeOff().doFor(5000);
		System.out.println("Takeoff");
	}
	
	public void hover(){
		cmd.hover().doFor(5000);
	}
	
	public void hover(int ms){
		cmd.hover().doFor(ms);
	}
	
	public void Landing(){
		cmd.landing();
		System.out.println("Landing");
	}
	
	public void TurnLeft(int ms){
		cmd.spinLeft(speed).doFor(ms);
	}
	
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
	
	public void flyRight(){
		cmd.goRight(speed);
	}
	
	public void flyLeft(){
		cmd.goLeft(speed);
	}
	
	public void flyForward(){
		cmd.forward(speed);
	}
	
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
	
	public void increaseAltitude(int speed){
		cmd.up(speed).doFor(1000);
	}
	
	public void decreaseAltitude(int speed){
		cmd.down(speed).doFor(1000);
	}
	
	/**
	 * Fly the drone to designated height in mm, keeps adjusting until the designated height is within 5cm.
	 * @param height
	 */
	
	public void moveToAltitude(int height){
		while(true){
			if (height - 50 > nav.getAltitude()) {
				System.out.println("Current altitude: " + nav.getAltitude());
				System.out.println("Going up");
				cmd.up(speed).doFor(20);
				//May need to add hover at the end of methods
			} else if (height + 50 < nav.getAltitude()){
				System.out.println("Going down");
				System.out.println("Current altitude: " + nav.getAltitude());
				cmd.down(speed).doFor(20);
			} else {
				System.out.println("Current altitude: " + nav.getAltitude());
			}
		}
	}
	
	
	
	public void findCircleCenter(Circle circle){
		
		double circle_x = circle.getX();
		double circle_y = circle.getY();
		double circle_r = circle.getRadius();
		
		if(circle_x > midPoint_x){
			flyRight(slowspeed);
		}
		else if(circle_x < midPoint_x){
			flyLeft(slowspeed);
		}
		if(circle_y > midPoint_y){
			decreaseAltitude(slowspeed);		
		}
		else if(circle_y < midPoint_y){
			increaseAltitude(slowspeed);
		}
		if(circle_r > max_radius){
			flyBackward(slowspeed);
		}
		else if(circle_r < max_radius){
			flyForward(slowspeed);
		}
		
	}
	
	public void flythroughCircle(){
		int count = 0;
		
	if (droneGUI.circles != null)
		System.out.println("");
//			for (Circle c : circles) {
//				findCircleCenter(c);
//				count++;	
//			}
//		if(count >= 5){
//			System.out.println("count is: " + count);
//			Landing();
//		}
//		else{
//			System.out.println("count failed");
//			Landing();
//		}
		
	}

	@Override
	public void circlesUpdated(Circle[] circle) {
		this.circles = circle;
		
	}
	

	
}