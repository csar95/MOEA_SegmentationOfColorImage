package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {

	public static void main(String[] args) {
		
		BufferedImage myPicture = null;
		String imagePath = "/testImage.jpg";
		try {
			myPicture = ImageIO.read(Main.class.getResource(imagePath));
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		Graphics2D g = (Graphics2D) myPicture.getGraphics();
		g.setStroke(new BasicStroke(3));
		g.setColor(Color.BLUE);
		g.drawRect(10, 10, myPicture.getWidth() - 20, myPicture.getHeight() - 20);
		
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		
		JPanel jPanel = new JPanel();
		jPanel.add(picLabel);
		
		JFrame f = new JFrame();
		f.setSize(new Dimension(myPicture.getWidth()+30, myPicture.getHeight()+30));
		f.add(jPanel);
		f.setVisible(true);
		
		System.out.println(myPicture.toString());

	}

}
