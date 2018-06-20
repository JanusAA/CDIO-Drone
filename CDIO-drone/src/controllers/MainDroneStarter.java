package controllers;

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.opencv.core.Core;

import QR.QRCode;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.navdata.AttitudeListener;
import imageDetection.CircleScanner;

/**
 * Class used for late testing just before adding statecontroller
 * @author simon
 *
 */

public class MainDroneStarter {
	public final static int IMAGE_WIDTH = 1280 /2;	// The size of the videofeed, used for centralizing and drawing
	public final static int IMAGE_HEIGHT = 720 /2;	// The size of the videofeed, used for centralizing and drawing

	public final static int TOLERANCE = 35;	// The Tolerance, static value used in the entire project

	private IARDrone drone = null;	// The Drone
	private MainController droneControl;	// The main controller
	private QRCode scanner = null;	//The QR Scanner
	
	private boolean autoControlEnabled = false;
	
	/**
	 * The main method in this class
	 * used for starting the drone up
	 */
	public MainDroneStarter(){
		drone = new ARDrone();
		droneControl = new MainController(drone);
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().setVideoCodecFps(30);
		
		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {
			public void attitudeUpdated(float pitch, float roll, float yaw) {
			}

			@Override
			public void attitudeUpdated(float pitch, float roll) {
			}

			@Override
			public void windCompensation(float pitch, float roll) {
			}
		});
		
		GUI gui = new GUI(drone, this);

		scanner = new QRCode();
		scanner.addListener(gui);
		
		CircleScanner circlescan = new CircleScanner();
		
		circlescan.addListener(droneControl);
		circlescan.addListener(gui);

		drone.getVideoManager().addImageListener(droneControl);
		drone.getVideoManager().addImageListener(gui);
		drone.getVideoManager().addImageListener(circlescan);
		drone.getVideoManager().addImageListener(scanner);
		
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
		droneControl.run();
		
		
	}
	
	/**
	 * Method for returning the Main controller
	 * @return
	 */
	public MainController getDroneControl(){
		return droneControl;
	}
	
	/**
	 * Method used for enabling the autocontroller
	 * @param enable
	 */
	public void enableAutoControl(boolean enable){
		if(enable){
			scanner.addListener(droneControl);
			new Thread(droneControl).start();
		}
		else {
			droneControl.stopControl();
			scanner.removeListener(droneControl);
		}
		this.autoControlEnabled = enable;
		}

/**
 * The main
 * @param args
 * @throws FileNotFoundException
 */
		public static void main(String[] args) throws FileNotFoundException {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Load OpenCV
			new MainDroneStarter();
		}

		/**
		 * returns a boolean discribing if autocontrol is on
		 * @return
		 */
		public boolean getAutoControlEnabled() {
			return autoControlEnabled;
		}

		/**
		 * returns the Altitude
		 * @return
		 */
		public int getAltitude() {
			return this.droneControl.getAltitude();
		}
	}
	


