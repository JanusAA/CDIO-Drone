package controllers;

public class DroneStateController {


	public enum Command {
		TakeOff, Hover, SearchForQR, FoundQR, LostQR, ValidateQR, Centralize, Flythrough, Land
	}

	public Command state;


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

	}
}