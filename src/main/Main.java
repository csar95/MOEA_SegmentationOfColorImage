package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import models.ImageGraph;
import models.Solution;
import moea.MOEA;

public class Main {

	public static void main(String[] args) {
		
		BufferedImage picture = Main.readImageFrom("/testImage.jpg");
		int height = picture.getHeight();
		int width = picture.getWidth();
		
		// Undirected weighted graph for Prim's algorithm
		ImageGraph graph = new ImageGraph(picture);
		graph.fill_graph();
		
		int[] mst = graph.prims_algorithm();
		
		MOEA moea = new MOEA(mst, picture);
		HashMap<Integer, Solution> bestSolutions = moea.run_algorithm();
		
		System.out.println("Found best solution!!");
		
		// Draw segments
		Solution bestSolution;
		ArrayList<Integer> membersInArchive = new ArrayList<Integer>(bestSolutions.keySet());
		for (int key : membersInArchive) {
			
			bestSolution = bestSolutions.get(key);			
			int[] bestGraph = bestSolution.getSolution();
			for (int pixelPos = 0; pixelPos < bestGraph.length; pixelPos++) {
				
				int j = pixelPos%width;
				int i = pixelPos/width;
				
				// Check if pixel neighbors belong to a different cluster
				if (i > 0 && !bestSolution.from_same_cluster(pixelPos, pixelPos - width)) { // Upper pixel
					picture.setRGB(j, i, Color.RED.getRGB());
				}
				else if (j < (width - 1) && !bestSolution.from_same_cluster(pixelPos, pixelPos + 1)) { // Right pixel
					picture.setRGB(j, i, Color.RED.getRGB());
				}
				else if (i < (height - 1) && !bestSolution.from_same_cluster(pixelPos, pixelPos + width)) { // Lower pixel
					picture.setRGB(j, i, Color.RED.getRGB());
				}
				else if (j > 0 && !bestSolution.from_same_cluster(pixelPos, pixelPos - 1)) { // Left pixel
					picture.setRGB(j, i, Color.RED.getRGB());
				}			
				
			}
			
			Main.saveImage(picture, key);			
		}

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
	
//	private static void showImage (BufferedImage img) {
//		
//		File outputfile = new File("result.jpg");
//		try {
//			ImageIO.write(img, "jpg", outputfile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		JLabel picLabel = new JLabel(new ImageIcon(img));
//		
//		JPanel jPanel = new JPanel();
//		jPanel.add(picLabel);
//		
//		JFrame f = new JFrame();
//		f.setSize(new Dimension(img.getWidth()+30, img.getHeight()+30));
//		f.add(jPanel);
//		f.setVisible(true);
//		
//	}
	
	private static void saveImage (BufferedImage img, int key) {
		
		File outputfile = new File("result" + key + ".jpg");
		try {
			ImageIO.write(img, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
