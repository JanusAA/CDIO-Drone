/**
 * Author: Aleksander
 */


package droneTest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;


import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import QR.QRListener;
import controllers.DroneStateController;
import controllers.MainController;
import de.yadrone.apps.paperchase.PaperChase;
import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.LEDAnimation;
import imageDetection.Circle;
import imageDetection.CircleListener;
import droneTest.GUITest;

public class DroneCommander {


	private ArrayList<CircleListener> listeners = new ArrayList<CircleListener>();
	private ArrayList<String> tagVisitedList = new ArrayList<String>();
	
	private ARDrone drone = null;
	private CommandManager cmd = null;
	private droneGUI dronegui = null;
	private DroneStateController State = null;
	private MainController control = null;
	private int TOLERANCE = 20;
	
	private int speed = 30;  // The base velocity in %
	private int slowspeed = 3;  //  The velocity used for centralizing in %
	private int slowtime = 200;	//	The time centralizing commands are done in ms
	private double ErrorMargin = 10;	// the Margin of Error in which the center of a circle can be fount
	
	private int count = 0;	//	The counter for amount of centered circles 
	private int counterCircle = 0;
	private int altcount = 0;
	private int methodecount = 0;  // Count Used to limit the calls done in findCircleCenter
	private int countmax = 5;	//	The amount of centered circles we need before flying through a circle
	private boolean findCircle = false;	//	When true we look for circles
	
	private double midPoint_x = GUITest.IMAGE_WIDTH/2;	// Camera midpoint in x
	private double midPoint_y = GUITest.IMAGE_HEIGHT/2;	// camera midpoint in y
	private Result tag;
	private float tagOrientation;
	private double max_radius = 90;	// The size of the circle we want on the camera
	
	private Circle[] circles;
	
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
		moveToAltitude(1000);
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
	public boolean CircleInCenter(){
		Circle[] circle = circles;	
		
		double circle_x = Math.abs(circle[0].x);
		double circle_y = Math.abs(circle[0].y);
		double circle_r = Math.abs(circle[0].r);
		
		boolean inCenter = false;
				
		if(circle_x <= midPoint_x + (ErrorMargin/2) && circle_x >= midPoint_x - (ErrorMargin/2)){
			if(circle_y <= midPoint_y + (ErrorMargin/2) && circle_y >= midPoint_y - (ErrorMargin/2)){
				drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_ORANGE, 10, 5);
					Landing();
				
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
	public void findCircleCenter() throws InterruptedException{
		
		
		Circle[] circle;
		synchronized(circles)
		{
			circle = circles;	
		}
		
			double circle_x = Math.abs(circle[0].x);
			double circle_y = Math.abs(circle[0].y);
			double circle_r = Math.abs(circle[0].r);
			System.out.println(circle_r);
			double abs_dif_x = Math.abs(circle_x - midPoint_x);
			double abs_dif_y = Math.abs(circle_y - midPoint_y);
			double abs_dif_r = Math.abs(circle_r - max_radius);
			
			
			if(abs_dif_x > abs_dif_y){
				System.out.println("x");
			if (circle_x < (midPoint_x + (ErrorMargin/2)))
				{
				System.out.println("Go left");
				drone.getCommandManager().goLeft(slowspeed);
				Thread.currentThread().sleep(200);
				hover();
				}
			else if (circle_x > (midPoint_x + (ErrorMargin/2)))
				{
				System.out.println("PaperChaseAutoController: Go right");
				drone.getCommandManager().goRight(slowspeed);
				Thread.currentThread().sleep(200);
				hover();
				}
			}
			else if(abs_dif_y > abs_dif_x){
			if (circle_y < (midPoint_y + (ErrorMargin/4)))
			{
				System.out.println("PaperChaseAutoController: Go left");
				drone.getCommandManager().up(slowspeed);
				Thread.currentThread().sleep(200);
				hover();
			}
			else if (circle_y > (midPoint_y + (ErrorMargin/4)))
			{
				System.out.println("PaperChaseAutoController: Go left");
				drone.getCommandManager().down(slowspeed);
				Thread.currentThread().sleep(200);
				hover();
			}
			}
//			else if (circle_r < max_radius){
//				System.out.println("PaperChaseAutoController: Go forward");
//				drone.getCommandManager().forward(slowspeed);
//				Thread.currentThread().sleep(200);
//				hover();
//				Thread.currentThread().sleep(500);
//			}
//			else if (circle_r > max_radius){
//				System.out.println("PaperChaseAutoController: Go backwards");
//				drone.getCommandManager().backward(slowspeed);
//				Thread.currentThread().sleep(200);
//				hover();
//				Thread.currentThread().sleep(500);
//			}
			
			if(circle_x <= midPoint_x + (ErrorMargin/2) && circle_x >= midPoint_x - (ErrorMargin/2)){
				if(circle_y <= midPoint_y + (ErrorMargin) && circle_y >= midPoint_y - (ErrorMargin)){
					drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_ORANGE, 10, 5);
						counterCircle++;
						System.out.println(counterCircle);
						System.out.println(counterCircle);
						System.out.println(counterCircle);
						System.out.println(counterCircle);
						System.out.println(counterCircle);
						System.out.println(counterCircle);
						System.out.println(counterCircle);
						System.out.println(counterCircle);

						if(counterCircle >= countmax){
							Landing();
						}
				}
			}
			}
	

		
	
	/**
	 * Fly Through circle method
	 * This method calls findCircleCenter() until the drone has found the circle center countmax times in a row.
	 * then proceeds to fly straight through the circle.
	 * @param circle
	 */
//	public void flythroughCircle(Circle[] circle){		
//	if (circle != null){
////				findCircleCenter();
//					if(CircleInCenter(circle[0])){
//						count++;
//					}
//					else{
//						count = 0;
//					}
//				if(count >= countmax){
//					setFindCircle(false);
//					flyForward(35,2500);
//					hover();
//					Landing();
//					System.out.println("Found Circle Center");
//					return;
//				}
//				else{
//				}		
//		}
//	}
	
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
	

	@Override
	public void circlesUpdated(Circle[] circle) {
		if (circle == null) // ToDo: do not call if no tag is present
			return;
		this.circles = circle;
		//		this.circles = circle;
//		if(circle[0].r > 30){
//		if(true){
//		findCircleCenter(circle[0]);
//		}
//		}
	}
	
	public void runTheQRFinder()
	{
		while(true) // control loop
		{
			try
			{
				if ((tag != null) && (System.currentTimeMillis() - tag.getTimestamp() > 500)){ // reset if too old (and not updated)
					tag = null;
					System.out.println("tag to old");
				}
				if(tag == null){
					strayAround();
				}
				
				if (!isTagCentered() && tag != null) // tag visible, but not centered
				{
					System.out.println("no tag in center");
					centerTag();
				}
				else if (isTagCentered() && tag != null){
					if (isTagCentered()){
						count++;
						}
						else{
						count = 0;
						}
						if(count > countmax){
						System.out.println("Er i midten");
						}
				}
				else
				{
					System.out.println("PaperChaseAutoController: I do not know what to do ...");
				}
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
		}
	}

//	public void onTag(Result result, float orientation)
//	{
//		if (result == null) // ToDo: do not call if no tag is present
//			return;
//		
//		System.out.println("PaperChaseAutoController: Tag found");
//
//		tag = result;
//		tagOrientation = orientation;
//	}
	
	
	private boolean isTagCentered()
	{
		if (tag == null)
			return false;
		
		// a tag is centered if it is
		// 1. if "Point 1" (on the tag the upper left point) is near the center of the camera  
		// 2. orientation is between 350 and 10 degrees
		
		int imgCenterX = (int) midPoint_x;
		int imgCenterY = (int) midPoint_y;
		
		ResultPoint[] points = tag.getResultPoints();
		boolean isCentered = ((points[1].getX() > (imgCenterX - TOLERANCE)) &&
			(points[1].getX() < (imgCenterX + TOLERANCE)) &&
			(points[1].getY() > (imgCenterY - TOLERANCE)) &&
			(points[1].getY() < (imgCenterY + TOLERANCE)));

		boolean isOriented = ((tagOrientation < 10) || (tagOrientation > 350));
			
		System.out.println("PaperChaseAutoController: Tag centered ? " + isCentered + " Tag oriented ? " + isOriented);
		
		return isCentered && isOriented;
	}
	
	private boolean hasTagBeenVisited()
	{
		synchronized(tag)
		{
			for (int i=0; i < tagVisitedList.size(); i++)
			{
				if (tag.getText().equals(tagVisitedList.get(i)))
					return true;
			}
		}
		
		return false;
	}
	
	public void strayAround() throws InterruptedException
	{
		int direction = new Random().nextInt() % 4;
		switch(direction)
		{
		case 0 : drone.getCommandManager().goLeft(slowspeed); System.out.println("PaperChaseAutoController: Stray Around: LEFT"); break;
		case 1 : drone.getCommandManager().goRight(slowspeed); System.out.println("PaperChaseAutoController: Stray Around: RIGHT");break;
			case 2 : drone.getCommandManager().goLeft(slowspeed); System.out.println("PaperChaseAutoController: Stray Around: LEFT"); break;
			case 3 : drone.getCommandManager().goRight(slowspeed); System.out.println("PaperChaseAutoController: Stray Around: RIGHT");break;
		}
		
		Thread.currentThread().sleep(500);
	}
	
	public void centerTag() throws InterruptedException
	{
		String tagText;
		ResultPoint[] points;
		
		synchronized(tag)
		{
			points = tag.getResultPoints();	
			tagText = tag.getText();
		}
		
		int imgCenterX = (int) midPoint_x;
		int imgCenterY = (int) midPoint_y;
		
		float x = points[1].getX();
		float y = points[1].getY();
		
		if ((tagOrientation > 10) && (tagOrientation < 180))
		{
			System.out.println("PaperChaseAutoController: Spin left");
			drone.getCommandManager().spinLeft(slowspeed * 2);
			Thread.currentThread().sleep(200);
		}
		else if ((tagOrientation < 350) && (tagOrientation > 180))
		{
			System.out.println("PaperChaseAutoController: Spin right");
			drone.getCommandManager().spinRight(slowspeed * 2);
			Thread.currentThread().sleep(200);
		}
		else if (x < (imgCenterX - TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go left");
			drone.getCommandManager().goLeft(slowspeed);
			Thread.currentThread().sleep(200);
		}
		else if (x > (imgCenterX + TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go right");
			drone.getCommandManager().goRight(slowspeed);
			Thread.currentThread().sleep(200);
		}
		else if (y < (imgCenterY - TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go up");
			drone.getCommandManager().up(slowspeed);
			Thread.currentThread().sleep(200);
		}
		else if (y > (imgCenterY + PaperChase.TOLERANCE))
		{
			System.out.println("PaperChaseAutoController: Go down");
			drone.getCommandManager().down(slowspeed);
			Thread.currentThread().sleep(200);
		}
		else
		{
			System.out.println("PaperChaseAutoController: Tag centered");
			drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 10, 5);
			
			tagVisitedList.add(tagText);
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	
			takeOff();
			hover();
//			cmd.moveToAltitude(1500);
			increaseAltitude(50);
			hover();
			while(true){
				try {
					findCircleCenter();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			
			
				
			}

//				while(true){
//			try {
//				findCircleCenter();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
	}
	
