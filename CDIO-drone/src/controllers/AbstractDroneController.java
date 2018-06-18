package controllers;

import com.google.zxing.Result;

import QR.QRListener;
import de.yadrone.base.IARDrone;

public abstract class AbstractDroneController extends Thread implements QRListener{
	protected boolean doStop = false;

	protected IARDrone drone;
	
	public AbstractDroneController(IARDrone drone)
	{
		this.drone = drone;
	}

	public abstract void run();
	
	public void onTag(Result result, float orientation)
	{

	}
	
	public void stopControl()
	{
		doStop = true;
	}
}
