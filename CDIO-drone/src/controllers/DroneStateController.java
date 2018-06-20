	/**
	 * Drone State Controller
	 * A class to control what the drone does at every point
	 * It consist of different commands or states
	 * when given a state a switch calls a method depending on the state
	 * At the end of every state a new state is called 
	 * @author Simon & Aleksander
	 *
	 */

package controllers;

import java.util.Random;
import java.util.Timer;

import com.google.zxing.Result;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import droneTest.DroneAlttitudeListener;
import droneTest.GUITest;
import imageDetection.Circle;

public class DroneStateController {
	private int speed = 30;  // The base velocity in %
	private int slowspeed = 3;  //  The velocity used for centralizing in %
	private int slowtime = 200;	//	The time centralizing commands are done in ms
	private double ErrorMargin = 10;	// the Margin of Error in which the center of a circle can be found
	private double midPoint_x = GUITest.IMAGE_WIDTH/2;	// Camera midpoint in x
	private double midPoint_y = GUITest.IMAGE_HEIGHT/2;	// camera midpoint in y
	private double max_radius = 90;	// The size of the circle we want on the camera

	private int count = 0;	//	The counter for amount of centered circles 
	private int altcount = 0;	// Counter for times in a row actualAlttitude == wantedAlttitude
	private int methodecount = 0;  // Count Used to limit the calls done in findCircleCenter
	private int countmax = 2;	//	The amount of centered circles we need before flying through a circle
	private boolean findCircle = false;	//	When true we look for circles
	

	public enum Command {
		TakeOff, Hover, MoveToFirstCircle, LostCircleGoBack, CentralizeCircle, SearchForCircle, ValidateQR, StrayAround, FlyThrough, UpdateGate, Land	}

	public Command state;

	private IARDrone drone;
	private CommandManager cmd;
	private MainController control;
	int strayCircleMode = 0;

	public Result tag;
	
	
/**
 * Constructor for the Drone State controller
 * @param mainCon
 * @param drone
 * @param cmd
 */
	public DroneStateController(MainController mainCon, IARDrone drone, CommandManager cmd){
		this.control = mainCon;
		this.cmd = cmd;
		this.drone = drone;
	}

	/**
	 * The Command method
	 * The main method of this class
	 * controls everything the drone does
	 * @param command
	 * @throws InterruptedException
	 */
	public void commands (Command command) throws InterruptedException{
		if (System.currentTimeMillis() - this.control.lastImageTimer > 15) {
			System.out.println("Image lag, delaying commands...");
			drone.hover();
			MainController.sleep(200);
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
		
		case MoveToFirstCircle:
			strayToFirstCircle();
			break;
			
		case LostCircleGoBack:
			lostCircleGoBack();
			break;

			// Used to validate the QR code, in order to differenciate between wallmarks and gates
		case ValidateQR:
			validateQR();
			break;

		case SearchForCircle:
			searchForCircle();
			break;

		case CentralizeCircle:
			findCircleCenter();
			break;


		case FlyThrough:
//			cmd.forward(25).doFor(1000);
			System.out.println("Vi skal fremad! - Hurtigt!");
			drone.getCommandManager().forward(8).doFor(4000);
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
	
	/**
	 * Takeoff method 
	 * the first method called when starting the drone
	 * Exception due to control.sleep
	 * @throws InterruptedException
	 */
	public void takeOffDrone() throws InterruptedException {
		// Takeoff
		System.out.println("State: ReadyForTakeOff");
		cmd.flatTrim();
		cmd.takeOff().doFor(3000);
		
		control.sleep(200);
		state = Command.Hover;
	}

	/**
	 * Hover method 
	 * used when the drone needs to be still mid air
	 * mostly used in between other states
	 * @throws InterruptedException
	 */
	public void hoverDrone() throws InterruptedException {
		// Hover method
		System.out.println("State: Hover");
		cmd.hover().doFor(1000);
		control.sleep(100);
		// Check conditions and transit to next state
		state = Command.MoveToFirstCircle;
	}

	int strayMode = 0;

	/**
	 * Method for finding circels
	 * strays around random until it finds a circle
	 * @throws InterruptedException
	 */
	public void strayToFirstCircle() throws InterruptedException {
		System.out.println("State: MoveToFirstCircle");
		int direction = new Random().nextInt() % 4;
		switch(direction)
		{
		case 0 : drone.getCommandManager().goLeft(slowspeed); System.out.println("LEFT"); break;
		case 1 : drone.getCommandManager().goRight(slowspeed); System.out.println("RIGHT");break;
		case 2 : drone.getCommandManager().up(slowspeed); System.out.println("UP"); break;
		case 3 : drone.getCommandManager().down(slowspeed); System.out.println("DOWN");break;
		}
		
		Thread.currentThread().sleep(500);
		if(control.getCircles().length >= 1){
			this.state = Command.SearchForCircle;	
		}
		else
			this.state = Command.MoveToFirstCircle;
		
	}

	/**
	 * Method used when the drone can not find a circle
	 * before it found the center
	 * @throws InterruptedException
	 */
	public void lostCircleGoBack() throws InterruptedException {
		System.out.println("State: LostCircleGoBack");
		int direction = new Random().nextInt() % 4;
		switch(direction)
		{
		case 0 : drone.getCommandManager().backward(slowspeed).doFor(1500); System.out.println("BACK"); break;
		case 1 : drone.getCommandManager().backward(slowspeed).doFor(1500); System.out.println("BACK");break;
			case 2 : drone.getCommandManager().goLeft(slowspeed).doFor(1500); System.out.println("LEFT"); break;
			case 3 : drone.getCommandManager().goLeft(slowspeed).doFor(1500); System.out.println("LEFT");break;
		}
		
		Thread.currentThread().sleep(200);
		if(control.getCircles().length >= 1){
			this.state = Command.SearchForCircle;	
		}
		else
			this.state = Command.LostCircleGoBack;
		
	}

	
	public void validateQR() throws InterruptedException{
		this.state = state.FlyThrough;
	}

	Circle lastCircle;
	int lastCircleCount = 0;
	int CenteredCount = 0;
	int CenteredMax = 5;

	
	/**
	 * method used for searching for circels
	 * if the drone cant see a circle 10 frames in a row it will change state to strayaround
	 * if the drone finds a circle it will change state to centralizecircle
	 * @throws InterruptedException
	 */
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

	
	/**
	 * method used for centralizing the drone right outside the center of the circle
	 * @throws InterruptedException
	 */
	public void findCircleCenter() throws InterruptedException{
		if(control.getCircles().length < 1){
			state = Command.LostCircleGoBack;
		}
		if(count < countmax){
			if(control.CircleIsCentered()){
				count++;
				System.out.println(count);
			}
//			else count = 0;
		}
		if(count >= countmax){
			System.out.println(count);
			System.out.println("SEE YAAAAA");
			state = Command.FlyThrough;
		}
		else{
			System.out.println("JEG ER IKKE I MIDTEN");
		state = Command.CentralizeCircle;
		}
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
	
	/**
	 * method used to search for circles
	 * moves the drone randomly around
	 * @throws InterruptedException
	 */
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


}

