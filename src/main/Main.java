package main;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import models.ImageGraph;
import moea.MOEA;

public class Main {

	public static void main(String[] args) {
		
		
		BufferedImage picture = Main.readImageFrom("/testImage.jpg");
		
		// Undirected weighted graph for Prim's algorithm
		ImageGraph graph = new ImageGraph(picture);
		graph.fill_graph();
		
		int[] mst = graph.prims_algorithm();
		
		MOEA moea = new MOEA(mst, picture);
		moea.run_algorithm();
						
//		for (int j = 0; j < width; j++) {
//			picture.setRGB(j, height - 1, Color.RED.getRGB());
//		}
		
		Main.showImage(picture);

	}
	
	private static BufferedImage readImageFrom (String path) {
		
		BufferedImage picture = null;
		
		try {
			picture = ImageIO.read(Main.class.getResource(path));
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		return picture;
				
	}
	
	private static void showImage (BufferedImage img) {
		
		JLabel picLabel = new JLabel(new ImageIcon(img));
		
		JPanel jPanel = new JPanel();
		jPanel.add(picLabel);
		
		JFrame f = new JFrame();
		f.setSize(new Dimension(img.getWidth()+30, img.getHeight()+30));
		f.add(jPanel);
		f.setVisible(true);
		
	}
	
	/*
	 * Fill an array with integer values corresponding to the color of the pixel.
	 * The color is in the binary format, like 11...11010101, however it is given as an integer value.
	 */
//	private static void getImagePixels (BufferedImage img, int width, int height, int[] pixels) {
//		
//		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
//				
//	    try {
//	    	pg.grabPixels();
//	    } catch (InterruptedException e) {
//	        throw new IllegalStateException("Error: Interrupted Waiting for Pixels");
//	    }
//	    
//	    if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
//	        throw new IllegalStateException("Error: Image Fetch Aborted");
//	    }	    
//				
//	}

}
