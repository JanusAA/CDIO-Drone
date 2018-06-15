package controllers;

import java.util.ArrayList;

import com.google.zxing.Result;

import QR.QRCode;
import QR.QRListener;
import controllers.DroneStateController.Command;
import de.yadrone.base.ARDrone;


/*
 * MainController class for drone control. 
 * 
 */


public class MainController implements QRListener {
	
	public final static int IMAGE_WIDTH = 640;
	public final static int IMAGE_HEIGHT = 360;
	
	public final static int TOLERANCE = 40;
	
	//Defines the start gate:
	private int nextGate = 0;
	//Defines the maximum number of gates: 
	private final int maxGates = 5;
	
	private Result tag;
	private float tagOrientation;
	
	private ARDrone drone = null;
	private DroneStateController droneStateController;
	private QRCode scanner = null;
	//ArrayList to hold gatenumbers p.00, p.01 etc..
	private ArrayList<String> gates = new ArrayList<String>();
	public boolean stop;
	
	private DroneStateController dsc;
	
	public DroneStateController getDsc() {
		return dsc;
	}



	public MainController() {
		//For loop which fills gates with gatenumbers: 
		for(int i = 0; i < 7; i++){
			gates.add("p.0" + i);
		}
		
		
	}
	
	public void run() {
		this.stop = false;
		dsc = new DroneStateController();
		dsc.state = Command.TakeOff;
		while (!stop){
			try {
				if((tag != null) && (System.currentTimeMillis() - tag.getTimestamp() > 2000)){
					System.out.println("Resetting tag - ms our of bounds");
					tag = null;
				}
				dsc.commands(dsc.state);
				Thread.currentThread().sleep(200);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get method for tag
	 * @return tag, String read from QR
	 */
		public Result getTag() {
			return tag; 
		}
		
		public float getTagOrientation(){
			return tagOrientation;
		}
		/**
		 * Method to get gates, 
		 * @return ArrayList with gatenumbers (strings equivalent to QR codes on gates)
		 */
		public ArrayList<String> getGates(){
			return gates;
		}

		@Override
		public void onTag(Result result, float orientation) {
			if (result == null)
				return;
			tag = result; 
			tagOrientation = orientation;

		}

	public static void main(String[] args) {
		new MainController();
	}
	
	public void setGate(){
		nextGate++;
	}
	
	public int getGate(){
		return nextGate;
	}
	
	public int getMaxGate(){
		return maxGates;
	}

		
}