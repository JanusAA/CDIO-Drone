package droneTest;

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;

public class DroneCommander {

	private ARDrone drone = null;
	private CommandManager cmd = null;
	
	private int speed = 0;
		
	public DroneCommander(ARDrone drone, int speed){
		this.drone = drone;
		this.speed = speed;	
		cmd = drone.getCommandManager();
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
	
	public void flyRight(int speed){
		cmd.goRight(speed);
	}
	
	public void flyLeft(int speed){
		cmd.goLeft(speed);
	}
	
	public void flyForward(int speed){
		cmd.forward(speed);
	}
	
	public void flyBackward(int speed){
		cmd.backward(speed);
	}
	
	
}