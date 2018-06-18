package controllers;

import java.util.Random;
import java.util.Timer;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import de.yadrone.apps.paperchase.PaperChase;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.LEDAnimation;
import droneTest.DroneAlttitudeListener;
import droneTest.DroneCommander;
import droneTest.GUITest;
import imageDetection.Circle;

public class DroneStateController {
	private int speed = 30;  // The base velocity in %
	private int slowspeed = 3;  //  The velocity used for centralizing in %
	private int slowtime = 200;	//	The time centralizing commands are done in ms
	private double ErrorMargin = 10;	// the Margin of Error in which the center of a circle can be fount
	private double midPoint_x = GUITest.IMAGE_WIDTH/2;	// Camera midpoint in x
	private double midPoint_y = GUITest.IMAGE_HEIGHT/2;	// camera midpoint in y
	private float tagOrientation;
	private double max_radius = 90;	// The size of the circle we want on the camera
	private Circle[] circles;
	
	private int count = 0;	//	The counter for amount of centered circles 
	private int counterCircle = 0;
	private int altcount = 0;
	private int methodecount = 0;  // Count Used to limit the calls done in findCircleCenter
	private int countmax = 3;	//	The amount of centered circles we need before flying through a circle
	private boolean findCircle = false;	//	When true we look for circles
	public enum Mode {
		Normal, Continous
	}
	
	private Mode currentMode;

	public enum Command {
		TakeOff, Hover, CentralizeCircle, SearchForCircle, ValidateQR, StrayAround, FlyThrough, UpdateGate, Land
	}

	public Command state;

	private IARDrone drone;
	private CommandManager cmd;
	private MainController control;
	int strayCircleMode = 0;
	
	private Timer timer;
	
	private int nextGate = 0;
	private final int maxGates = 5;
	
	
	public Result tag;
	
	public DroneStateController(MainController mainCon, IARDrone drone, CommandManager cmd){
		this.control = mainCon;
		this.cmd = cmd;
		this.drone = drone;
		this.currentMode = Mode.Normal;
		this.timer = new Timer();
	}

	public void commands (Command command) throws InterruptedException{
		if (System.currentTimeMillis() - this.control.lastImageTimer > 10) {
			System.out.println("Image lag, delaying commands...");
			drone.hover();
			MainController.sleep(150);
		}
		switch (command) {

		//Gets the drone airborne
		case TakeOff:
			takeOffDrone();
			break; 

			//Hover state of the drone (stay airborne)
		case Hover: 
			hoverDrone();
			break; 

	// Used to validate the QR code, in order to differenciate between wallmarks and gates
		case ValidateQR:
			qRValidate();
			break;

		case SearchForCircle:
			searchForCircle();
			break;
			
		case CentralizeCircle:
			findCircleCenter();
			break;
			
			
		case FlyThrough:
			cmd.forward(25).doFor(1000);
			break;
			
		case StrayAround:
			strayAround();
			break;

			//Once the drone has completed the obstacle course it shall land, 
			//we might implement a loop which makes the drone complete the map several times before landing
		case Land:
			Landing();
			break;


		}
	}
	public void takeOffDrone() throws InterruptedException {
		// Takeoff
		System.out.println("State: ReadyForTakeOff");
		cmd.flatTrim();
		cmd.takeOff().doFor(5000);
		state = Command.Hover;
	}

	public void hoverDrone() throws InterruptedException {
		// Hover method
		System.out.println("State: Hover");
		cmd.hover().doFor(1000);
		control.sleep(100);
		// Check conditions and transit to next state
		state = Command.SearchForCircle;
	}

	int strayMode = 0;


	public void qRValidate() throws InterruptedException{
		this.state = state.FlyThrough;
	}
	
	Circle lastCircle;
	int lastCircleCount = 0;
	int CenteredCount = 0;
	int CenteredMax = 5;

	public void searchForCircle() throws InterruptedException {
		// Increase altitude and search for the circle
		System.out.print("State: SearchForCircle - ");
		if (control.getCircles().length >= 1) {
			for (Circle c : control.getCircles()) {
					System.out.println("Circle found!");
					this.state = Command.CentralizeCircle;
					lastCircle = c;
					lastCircleCount = 0;
				
			}
			
		} else {
			if (lastCircle != null) {
				if (++lastCircleCount > 10) {
					System.out.println("lastCircle for the 10th time!!!");
					Thread.currentThread().sleep(200);
					lastCircleCount = 0;
					state = Command.StrayAround;
					return;
				}
			} else {
				CenteredCount = 0;
				System.out.println("No lastCircle?!");
				this.state = Command.StrayAround;
				control.sleep(200);
				return;
			}

			Thread.currentThread().sleep(200);

			this.state = Command.CentralizeCircle;
		}
	}
	
//	public void qRValidate() throws InterruptedException {
//		System.out.print("State: QRValidate: ");
//		Result tag = control.getTag();
//		if (tag == null) {
//			if (firstTag) {
//				System.out.println("Tag Lost");
//				firstTag = false;
//				this.state = Command.LostQR;
//				return;
//			}
//			this.state = Command.SearchForQR;
//			return;
//
//		}
//		firstTag = true;
//		// The scanned QR is the next port we need
//		if (control.getGates().get(nextGate).equals(tag.getText())) {
//			System.out.println("Validated port: " + tag.getText());
//			this.state = Command.CentralizeQR;
//		} else {
//			System.out.println("Not validated port: " + tag.getText());
//			this.state = Command.SearchForQR;
//		}
//		Thread.currentThread().sleep(10);
//	}
	
public void findCircleCenter() throws InterruptedException{
	if(count < countmax){
		if(control.CircleIsCentered()){
			count++;
			System.out.println(count);
		}
		else count = 0;
	}
	if(count > countmax){
		System.out.println(count);
		System.out.println("SEE YAAAAA");
		state = Command.FlyThrough;
	}
	else
		System.out.println("JEG ER IKKE I MIDTEN");
		state = Command.CentralizeCircle;
	}


public boolean CircleInCenter(Circle c){
boolean centered = false;
	if(c.x <= midPoint_x + (ErrorMargin/2) && c.x >= midPoint_x - (ErrorMargin/2)){
		if(c.y <= midPoint_y + (ErrorMargin/2) && c.y >= midPoint_y - (ErrorMargin/2)){
			System.out.println("Centreret!!");
			centered = true;
		}}
	return centered;
}




/**
 * Take off Method
 * The first method called when you need to fly
 */
public void takeOff(){
	cmd.takeOff();
	increaseAltitude(90);
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
	this.state = Command.SearchForCircle;
}
/**
 * A method to limit values.
 * @param i The value
 * @param min
 * @param max
 * @return The limited value.
 */

}

