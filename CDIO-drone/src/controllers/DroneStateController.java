package controllers;

import com.google.zxing.Result;

import droneTest.DroneCommander;

public class DroneStateController {


	public enum Command {
		TakeOff, Hover, SearchForQR, LostQR, ValidateQR, CentralizeAndFlyThrough, UpdateGate, Land
	}

	public Command state;

	private DroneCommander drone;
	private MainController control;
	
	//Defines the start gate:
	private int nextGate = 0;
	//Defines the maximum number of gates: 
	private final int maxGates = 5;

	public void commands (Command command) throws InterruptedException{


		switch (command) {

		//Gets the drone airborne
		case TakeOff:
			takeOff();
			break; 

			//Hover state of the drone (stay airborne)
		case Hover: 
			hover();
			break; 

			//Searches for QRCode, drone pans the area to the right in search of a QR
		case SearchForQR:
			searchForQR();
			break;

			//If the QR is lost, the drone shall mode a bit around in order to locate the QR again
		case LostQR:
			lostQR();
			break;

			// Used to validate the QR code, in order to differenciate between wallmarks and gates
		case ValidateQR:
			validateQR();
			break;

			//Once the gate QR has been located the drone shall find the circle and centralice itself in fornt of it.
		case CentralizeAndFlyThrough:
			centralizeAndFlyThrough();
			break;

			//Once centralized the drone shall fly through the gate, update the gate number (QR) in order to find the next gate
		case UpdateGate:
			updateGate();
			break;

			//Once the drone has completed the obstacle course it shall land, 
			//we might implement a loop which makes the drone complete the map several times before landing
		case Land:
			land();
			break;


		}
	}
	public void takeOff(){
		//Take the drone off and sets it to over
		System.out.println("State: Preparing for takeoff");
		drone.takeOff();
		drone.hover();
		state = Command.Hover;

	}

	public void hover(){
		//Makes the drone hover (used to wait for further instructions)
		System.out.println("State: Hover");
		drone.hover(7000);
		state = Command.SearchForQR;
	}

	public void searchForQR(){
		//Search for QR method:
		System.out.println("State: Searching for QR..");
		Result tag = control.getTag();
		if (tag != null){
			System.out.println("QR located");
			state = Command.ValidateQR;
		}
		drone.moveToAltitude(1000);
		System.out.println("Spinning");
		drone.TurnRight(30, 40);
		//TODO: Might need to sleep the controller, and wait for the drone to spin. 

	}
	public void lostQR(){
		System.out.println("State: Lost QR.. ");
		drone.moveToAltitude(1000);
		Result tag = control.getTag();
		if (tag != null){
			state = Command.ValidateQR;

		} else {
			int moves = 0;
			int tries = 0;

			while (tries < 6){
				switch (moves){

				case 0: 
					drone.flyBackward(10, 150);
					moves = 1;
					tries++;
					break;

				case 1:
					drone.flyRight(10, 100);
					moves = 2;
					tries++;
					break;

				case 2:
					drone.flyLeft(10, 200);
					moves = 0;
					tries++;
					break;
				}
			}
			state = Command.SearchForQR;
		}

	}

	public void validateQR() {
		System.out.println("State: Validate QR: Validating QR.. ");
		Result tag = control.getTag();
		if (tag == null){
			System.out.println("Tag lost");
			state = Command.LostQR;
		}
		if (tag != null){
			if(control.getGates().get(nextGate).equals(tag.getText())){
				System.out.println("Valid port with number: " + tag.getText());
				state = Command.CentralizeAndFlyThrough;
			} else {
				System.out.println("Invalid port number: " +tag.getText());
				state = Command.SearchForQR;
			}
		}
	}
	public void centralizeAndFlyThrough() {
		System.out.println("State: Centralizing on gate, and flying through.. ");
		//TODO: Method needs to be merged from other git branch.
//		drone.setFindCircle(true);
		state = Command.UpdateGate;
	}
	
	public void updateGate(){
		System.out.println("Updating next gate..");
		//Updating the gate number which is searched for: 
		nextGate++;
		
		System.out.println("Next gate is: p.0" + nextGate);
		
		if(nextGate == maxGates){
			System.out.println("Course complete.. searching for landing spot");
			drone.hover();
			state = Command.Land;
		}
	}
	
	public void land(){
		System.out.println("State: landing.. searching for landing spot");
		//TODO: create method which finds the landing spot and lands. 
		drone.Landing();
	}
}
