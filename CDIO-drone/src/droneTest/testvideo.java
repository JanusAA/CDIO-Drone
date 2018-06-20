package droneTest;

/**
 * early test class
 * used for videofeed
 */

import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;

public class testvideo {
	
	
	BufferedImage image = null;
	
	public BufferedImage vid(IARDrone drone){
	
	drone.getVideoManager().addImageListener(new ImageListener() {
		public void imageUpdated(BufferedImage newImage)
		{
			image = newImage;
		}
		
	});
	return image;
}
}

