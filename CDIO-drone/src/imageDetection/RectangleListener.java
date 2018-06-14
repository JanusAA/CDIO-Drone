package imageDetection;

import java.util.ArrayList;

import org.opencv.core.Rect;

public interface RectangleListener {

		public void rectanglesUpdated(ArrayList<Rect> rectangles);
	
}
