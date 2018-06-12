package controllers;

import java.util.ArrayList;

import com.google.zxing.Result;

import QR.QRListener;


/*
 * MainController class for drone control. 
 * 
 */


public class MainController implements QRListener {
	public Result tag;

	//ArrayList to hold gatenumbers p0.1, p0.2 etc..
	private ArrayList<String> gates = new ArrayList<String>();


	public MainController() {
		//For loop which fills gates with gatenumbers: 
		for(int i = 0; i < 7; i++){
			gates.add("p.0" + i);
		}

	}

	/**
	 * Get method for tag
	 * @return tag, String read from QR
	 */
		Result getTag() {
			return tag; 
		}
		/**
		 * Method to get gates, 
		 * @return ArrayList with gatenumbers (strings equivalent to QR codes on gates)
		 */
		ArrayList<String> getGates(){
			return gates;
		}

		@Override
		public void onTag(Result result, float orientation) {
			if (result == null)
				return;
			tag = result; 

		}

	
}