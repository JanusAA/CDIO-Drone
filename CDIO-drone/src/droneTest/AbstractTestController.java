package droneTest;

import com.google.zxing.Result;

import QR.QRListener;
import imageDetection.Circle;
import imageDetection.CircleListener;

public abstract class AbstractTestController extends Thread implements CircleListener{

	@Override
	public void circlesUpdated(Circle[] circle) {
		
	}

	public abstract void run();
	
//	@Override
//	public void onTag(Result result, float orientation)
//	{
//
//	}
	
}
