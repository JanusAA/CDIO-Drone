package droneTest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.video.ImageListener;
import imageDetection.Circle;
/**
 * Not used
 */

public class DroneVideoListener extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage image = null;
	private Circle[] circles;
	private int imgScale = 2;
	private Font tagFont = new Font("SansSerif", Font.BOLD, 14 * imgScale / 2);

	public DroneVideoListener(final IARDrone drone){
		super("CDIO-drone");

		setSize(640, 360);
		setVisible(true);
		

		drone.getVideoManager().addImageListener(new ImageListener() {
			public void imageUpdated(BufferedImage newImage)
			{
				image = newImage;
				SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						repaint();
					}
				});
			}
		});
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				drone.getCommandManager().setVideoChannel(VideoChannel.NEXT);
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) 
			{
				drone.stop();
				System.exit(0);
			}
		});
	}

	public synchronized void paint(Graphics g)
	{
		if (image != null){
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

		}
		
		// Draw circles
		if (circles != null)
			for (Circle c : circles) {
				g.setColor(Color.RED);
				g.drawRect((int) c.x * imgScale, (int) c.y * imgScale, 10, 10);
				g.setColor(Color.BLUE);
				g.drawOval((int) (c.x - c.r) * imgScale, (int) (c.y - c.r) * imgScale,
						(int) (2 * c.r) * imgScale, (int) (2 * c.r) * imgScale);
				g.drawString(c.toString(), (int) c.x * imgScale + 10, (int) c.y * imgScale + 10);
			}
		else {
		// draw "Waiting for video"
		g.setColor(Color.RED);
		g.setFont(tagFont);
		g.drawString("Waiting for VideoRecognition ...", 10, 20);
	}
	}
}

