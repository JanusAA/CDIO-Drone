package controllers;

import com.google.zxing.Result;

import droneTest.DroneCommander;

public class DroneStateController {


	public enum Command {
		TakeOff, Hover, SearchForQR, FoundQR, LostQR, ValidateQR, Centralize, Flythrough, Land
	}

	public Command state;

	private DroneCommander drone;
	private MainController control;

	public void commands (Command command) throws InterruptedException{


		switch (command) {

		case TakeOff:
			takeOff();
			break; 

		case Hover: 
			hover();
			break; 

		case SearchForQR:
			searchForQR();
			break;

		case FoundQR:
			foundQR();
			break;

		case LostQR:
			lostQR();
			break;

		case ValidateQR:
			validateQR();
			break;

		case Centralize:
			centralize();
			break;

		case Flythrough:
			flyThrough();
			break;

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
		drone.
		
	}
}