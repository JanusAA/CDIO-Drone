/**
 * Author: Simon Christiansen
 */


package droneTest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import controllers.DroneStateController;
import controllers.DroneStateController.Command;
import controllers.MainController;
import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import imageDetection.Circle;
import imageDetection.CircleListener;
import imageDetection.CircleScanner;
import imageDetection.CircleScannerCMD;
import droneTest.GUITest;

public class DroneCommander implements CircleListener{


	private ArrayList<CircleListener> listeners = new ArrayList<CircleListener>();
	
	private ARDrone drone = null;
	private CommandManager cmd = null;
	private droneGUI dronegui = null;
	private DroneStateController State = null;
	private MainController control = null;
	
	private int speed = 30;  // The base velocity in %
	private int slowspeed = 10;  //  The velocity used for centralizing in %
	private int slowtime = 200;	//	The time centralizing commands are done in ms
	private double ErrorMargin = 35;	// the Margin of Error in which the center of a circle can be fount
	
	private int count = 0;	//	The counter for amount of centered circles 
	private int altcount = 0;
	private int methodecount = 0;  // Count Used to limit the calls done in findCircleCenter
	private int countmax = 6;	//	The amount of centered circles we need before flying through a circle
	private boolean findCircle = true;	//	When true we look for circles
	
	private double midPoint_x = GUITest.IMAGE_WIDTH/2;	// Camera midpoint in x
	private double midPoint_y = GUITest.IMAGE_HEIGHT/2;	// camera midpoint in y

	private double max_radius = 140;	// The size of the circle we want on the camera
	
	private Circle[] circles = CircleScanner.cir;
	
		/**
		 * The DroneCommander Constructor
		 * @param drone
		 * @param speed
		 * @param gui
		 */
	public DroneCommander(ARDrone drone, int speed, DroneStateController state){
		this.drone = drone;
		this.speed = speed;
		this.State = state;
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
		
		while(count < countmax){
			System.out.println("inde");
			Circle[] circle = null;
			CircleScannerCMD.updateCurrentCircle();
//			while(CircleScannerCMD.getCircleSaved() == false){
//				System.out.println("kommer ikke ud :(");
//			}
			timeOut();
			circle = CircleScannerCMD.getCircle();
			if(circle != null){
			System.out.println("x: " + circle[0].x + " y: " + circle[0].y);
		
			double circle_x = Math.abs(circle[0].x);
			double circle_y = Math.abs(circle[0].y);
			double circle_r = Math.abs(circle[0].r);
			
			double abs_dif_x = Math.abs(circle_x - midPoint_x);
			double abs_dif_y = Math.abs(circle_y - midPoint_y);
			double abs_dif_r = Math.abs(circle_r - max_radius);
			
			if(abs_dif_x > abs_dif_y){
				if(circle_x <= midPoint_x + (ErrorMargin/2) || circle_x >= midPoint_x - (ErrorMargin/2)){
					if(circle_x < midPoint_x){
						flyRight(slowspeed);
						System.out.println("h�jre");
						hover();
					}
					else if(circle_x > midPoint_x){
						flyLeft(slowspeed);
						System.out.println("venstre");
						hover();
					}}
			}
			if(abs_dif_y > abs_dif_x){
				if(circle_y <= midPoint_y + (ErrorMargin/2) || circle_y >= midPoint_y - (ErrorMargin/2)){
					if(circle_y > midPoint_y){
						decreaseAltitude(slowspeed);
						System.out.println("ned");
						hover();
					}
					else if(circle_y < midPoint_y){
						increaseAltitude(slowspeed);
						System.out.println("up");
						hover();
					}}
			}
//			if(abs_dif_r > abs_dif_x && abs_dif_r > abs_dif_y){
//				if(circle_r <= max_radius + (ErrorMargin/2) || circle_r >= max_radius - (ErrorMargin/2)){
//					if(circle_r > max_radius){
//						flyBackward(slowspeed);
//						System.out.println("bagud");
//						hover();
//					}
//					else if(circle_r < max_radius){
//						flyForward(slowspeed);
//						System.out.println("frem");
//						hover();
//					}}
//			}
		
			if(CircleInCenter(circles[0])){
				count++;
				System.out.println("FUCK YEAH " + count + " gang!");
			}
			else{
				count = 0;
			}
		
			if(count >= countmax){
				System.out.println("FUCK YEAH");
//				findCircle = false;
				return;
			}}
			CircleScannerCMD.setNullLastCircle();
		}
		}
		
		
		
	
	/**
	 * Fly Through circle method
	 * This method calls findCircleCenter() until the drone has found the circle center countmax times in a row.
	 * then proceeds to fly straight through the circle.
	 * @param circle
	 */
	public void flythroughCircle(Circle[] circle){		
	if (circle != null){
//				findCircleCenter();
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
	
	public void timeOut(){
		int dateToBeat = (int) new Date().getTime() + 250;
		int date = (int) new Date().getTime();
		while(dateToBeat > date){
			date = (int) new Date().getTime();
		}
	}
	/**
	 * CirclesUpdated method comes from the implementation of circlelistener
	 * this method is called every time a circle is found in CircleScanner
	 * if the boolean findcircle is true
	 * this method calls flythroughCircle
	 */
	
//	@Override
//	public void circlesUpdated(Circle[] circles) {
//		
//		this.circles = circles;
//	}
	
	public void searchForQR() throws InterruptedException{
		//Search for QR method:
		moveToAltitude(1000);
		Result tag = control.getTag();
		if (tag != null){
			State.setState(Command.ValidateQR);
		}
		moveToAltitude(1000);
		TurnRight(30, 40);
		
		Thread.currentThread().sleep(20);
		//TODO: Might need to sleep the controller, and wait for the drone to spin. 

	}
	
	public void centralizeQR() throws InterruptedException {
		//Relying on code from API paperchase:
		Result tag = control.getTag();
		String tagText;
		ResultPoint[] points;
		
		synchronized(tag){
			points = tag.getResultPoints();
			tagText = tag.getText();
		}

		
		float x = points[1].getX();
		float y = points[1].getY();
		
		if ((control.getTagOrientation() > 10) && (control.getTagOrientation() < 180)){
			System.out.println("Spin left");
			TurnLeft(10, 500);
		}
		else if ((control.getTagOrientation() < 350) &&(control.getTagOrientation() > 180)){
			System.out.println("Spin right");
			TurnRight(10, 500);
		}
		else if (x < (midPoint_x - control.TOLERANCE)){
			System.out.println("Go left");
			flyLeft(5, 500);
		}
		else if (x > (midPoint_x + control.TOLERANCE)){
			System.out.println("Go Right");
			flyRight(5, 500);
		}
		else if (y < (midPoint_y - control.TOLERANCE)){
			System.out.println("Go forward");
			flyForward(5, 500);
		}
		else if (y > (midPoint_y + control.TOLERANCE)){
			System.out.println("Go forward");
			flyBackward(5, 500);
		}
		else{
			System.out.println("QR tag centered");
			
			State.setState(Command.FlyThrough);
			Thread.currentThread().sleep(600);
		}
	}
	
	public void validateQR() throws InterruptedException{
		System.out.println("State: Validate QR: Validating QR.. ");
		Result tag = control.getTag();
		if (tag == null){
			System.out.println("Tag lost");
			State.setState(Command.LostQR);
		}
		if (tag != null){
			if(control.getGates().get(control.getGate()).equals(tag.getText())){
				System.out.println("Valid port with number: " + tag.getText());
				State.setState(Command.CentralizeQR);
			}
			// Checks if the gate is a QR which starts with p/P:
			else if ("p" != (tag.getText().substring(0, 1).toLowerCase())){
				System.out.println("WallMarks");
				//TODO: implement action the drone shall take if a wallmark is read. 
			
		}else {
				System.out.println("Invalid port number: " +tag.getText());
				State.setState(Command.SearchForQR);
			}
		}
		Thread.currentThread().sleep(20);
	}
	
	public void updateGate(){
		System.out.println("Updating next gate..");
		//Updating the gate number which is searched for: 
		control.setGate();;
		
		System.out.println("Next gate is: p.0" + control.getGate());
		
		if(control.getGate() == control.getMaxGate()){
			System.out.println("Course complete.. searching for landing spot");
			drone.hover();
			State.setState(Command.Land);
		}
	}
	
	public void lostQR() throws InterruptedException{
		System.out.println("State: Lost QR.. ");
		moveToAltitude(1000);
		Result tag = control.getTag();
		if (tag != null){
			State.setState(Command.ValidateQR);

		} else {
			int moves = 0;
			int tries = 0;

			while (tries < 6){
				switch (moves){

				case 0: 
					flyBackward(10, 150);
					moves = 1;
					tries++;
					break;

				case 1:
					flyRight(10, 100);
					moves = 2;
					tries++;
					break;

				case 2:
					flyLeft(10, 200);
					moves = 0;
					tries++;
					break;
				}
				Thread.currentThread().sleep(100);
			}
			State.setState(Command.SearchForQR);
		}
	}

	@Override
	public void circlesUpdated(Circle[] circle) {
//		this.circles = circle;
//		if(circle[0].r > 30){
//		if(true){
//		findCircleCenter(circle[0]);
//		}
//		}
	}
}