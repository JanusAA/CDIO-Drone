package controllers;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import droneTest.DroneCommander;

public class DroneStateController {


	public enum Command {
		TakeOff, Hover, SearchForQR, LostQR, ValidateQR, CentralizeQR, FlyThrough, UpdateGate, Land
	}

	public Command state;

	//Initializes drone commander and MainController
	private DroneCommander drone;
	private MainController control;
	
	public Result tag;

	public void commands (Command command) throws InterruptedException{

		switch (command) {

		//Gets the drone airborne
		case TakeOff:
//			takeOff();
			break; 

			//Hover state of the drone (stay airborne)
		case Hover: 
//			hover();
			break; 

			//Searches for QRCode, drone pans the area to the right in search of a QR
		case SearchForQR:
//			searchForQR();
			break;

			//If the QR is lost, the drone shall mode a bit around in order to locate the QR again
		case LostQR:
//			lostQR();
			break;

			// Used to validate the QR code, in order to differenciate between wallmarks and gates
		case ValidateQR:
//			validateQR();
			break;

			//Once the gate QR has been located the drone shall find the circle and centralice itself in fornt of it.
		case CentralizeQR:
//			centralizeQR();
			break;
		
		case FlyThrough:
//			flyThrough();
			break;

			//Once centralized the drone shall fly through the gate, update the gate number (QR) in order to find the next gate
		case UpdateGate:
//			updateGate();
			break;

			//Once the drone has completed the obstacle course it shall land, 
			//we might implement a loop which makes the drone complete the map several times before landing
		case Land:
//			land();
			break;


		}
	}

	public void setState(Command command){
		state = command;
	}
}
