/*
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
	private DroneAlttitudeListener nav;
	private droneGUI dronegui = null;
	
	private int speed = 30;
	private int slowspeed = 5;
	private int slowtime = 50;
	private double ErrorMargin = 35;
	
	private int count = 0;
	private int methodecount = 0;
	private int countmax = 6;
	private boolean findCircle = true;
	
	private double midPoint_x = GUITest.IMAGE_WIDTH/2;
	private double midPoint_y = GUITest.IMAGE_HEIGHT/2;

	private double max_radius = 140;
	
	private Circle[] circles;
	
		
	public DroneCommander(ARDrone drone, int speed, droneGUI gui){
		this.drone = drone;
		this.speed = speed;	
		this.dronegui = gui;
		cmd = drone.getCommandManager();
		nav = new DroneAlttitudeListener(drone);
		circles = dronegui.circles;
	}
	
	public void takeOff(){
		cmd.takeOff().doFor(5000);
		System.out.println("Takeoff");
	}
	
	public void hover(){
		cmd.hover().doFor(5000);
		cmd.hover().doFor(1000);
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
	public void flyRight(int speed){
		cmd.goRight(speed).doFor(slowtime);
	}
	
	public void flyLeft(int speed){
		cmd.goLeft(speed).doFor(slowtime);
	}
	
	public void flyForward(int speed){
		cmd.forward(speed).doFor(slowtime);
	}
	
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
	public void flyBackward(int speed){
		cmd.backward(speed).doFor(slowtime);
	}
	
	public void increaseAltitude(int speed){
		cmd.up(speed).doFor(1000);
		cmd.up(speed).doFor(slowtime);
	}
	
	public void increaseAltitude(int speed, int ms){
		cmd.up(speed).doFor(ms);
	}
	
	public void decreaseAltitude(int speed){
		cmd.down(speed).doFor(1000);
		cmd.down(speed).doFor(slowtime);
	}
	
	public void setFindCircle(boolean true_false){
		findCircle = true_false;
	}
	
	public boolean getFindCircle(){
		return findCircle;
	}
	
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
	
	public void findCircleCenter(Circle circle) {
		
		double circle_x = circle.x;
		double circle_y = circle.y;
		double circle_r = circle.r;
		
		if(methodecount == 0){
			if(circle.x <= midPoint_x + (ErrorMargin/2) == false && circle.x >= midPoint_x - (ErrorMargin/2) == false){
			if(circle_x > midPoint_x){
				flyRight(slowspeed);
				System.out.println("højre");
			}
			else if(circle_x < midPoint_x){
				flyLeft(slowspeed);
				System.out.println("venstre");
			}}
			methodecount = 1;
		}
		else if(methodecount ==1){
			if(circle.y <= midPoint_y + (ErrorMargin/2) == false && circle.y >= midPoint_y - (ErrorMargin/2) == false){
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
			if(circle.r <= max_radius + (ErrorMargin/2) == false && circle.r >= max_radius - (ErrorMargin/2) == false)
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
			Thread.currentThread().sleep(25);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		methodecount = 0;
		}
	}
	
	public void flythroughCircle(Circle[] circle){		
	if (circle != null){
//		System.out.println("fandt en cirkel");
//			for(Circle c : circle){
				findCircleCenter(circle[0]);
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
//			}	
		}
	}
	
	

	@Override
	public void circlesUpdated(Circle[] circle) {
		if(findCircle){
		flythroughCircle(circle);
		}
		else{
			System.out.println("fuck yeah!");
		}
	}
}