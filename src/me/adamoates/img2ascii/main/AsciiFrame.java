package me.adamoates.img2ascii.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import me.adamoates.img2ascii.main.Image2Ascii.Flag;
import me.adamoates.img2ascii.main.Image2Ascii.RGB;

public class AsciiFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final int PACKED_SIZE = 10;
	private ArrayList<ArrayList<Character>> lines;
	private ArrayList<ArrayList<RGB>> colors;
	private int currentLine = 1;
	private int currentCharacter = 1;
	private int fontSize = 10;
	private Graphics2D g;
	private double zoom = 1.0;
	private int width;
	private int height;
	
	/**
	 * Instantiates an AsciiFrame from the characters and RGB values
	 * of an AsciiImage object.
	 * @param lines
	 * @param colors
	 */
	public AsciiFrame(ArrayList<ArrayList<Character>> lines, ArrayList<ArrayList<RGB>> colors) {
		this(lines, colors, "Image2Ascii");
	}
	
	/**
	 * Instantiates an AsciiFrame from the characters and RGB values
	 * of an AsciiImage object and sets the title so specified String.
	 * @param lines
	 * @param colors
	 * @param title
	 */
	public AsciiFrame(ArrayList<ArrayList<Character>> lines, ArrayList<ArrayList<RGB>> colors, String title) {
		super();
		super.setTitle(title);
		
		ImageIcon logo = new ImageIcon("logo.png");
		super.setIconImage(logo.getImage());
		
		super.setResizable(true);
		
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.lines = lines;
		this.colors = colors;
		this.fontSize = 16;	// this should make
		
		this.toggleVisibility();
		
		this.g = (Graphics2D) super.getGraphics();
		this.width = (int) (this.lines.get(0).size() * 1.0 * (this.fontSize / 2) * this.zoom);
		this.height = (int) (this.lines.size() * 1.0 * this.fontSize * this.zoom);
	}
	
	/**
	 * Clears the Graphics2D of this AsciiFrame
	 */
	private void cleanSlate() {
		this.cleanSlate(this.width, this.height);
	}
	
	/**
	 * Clears the Graphics2D of the AsciiFrame up to specified width and height.
	 * @param w
	 * @param h
	 */
	private void cleanSlate(int w, int h) {
		this.g.setColor(new Color(220, 220, 210));
		this.g.fillRect(0, 0, w, h);
	}
	
	/**
	 * Draws the ASCII contents to this AsciiFrame's buffer
	 */
	public void drawAscii() {
		this.cleanSlate();
		
		for (int i = 0; i < this.lines.size(); i++) {
			int y = this.fontSize * this.currentLine++;
			if (Image2Ascii.flags.contains(Image2Ascii.Flag.PACK)) y -= this.currentLine * PACKED_SIZE;
			this.currentCharacter = 1;
			for (int j = 0; j < lines.get(i).size(); j++) {
				int x = (this.fontSize / 2) * this.currentCharacter++;
				if (Image2Ascii.flags.contains(Image2Ascii.Flag.PACK)) x -= this.currentCharacter * PACKED_SIZE / 2;
				char ch = lines.get(i).get(j);
				RGB rgb = colors.get(i).get(j);
				this.drawCharacter(ch, rgb, x, y);
			}
		}
		
		this.currentLine = 1;
		this.currentCharacter = 1;
	}
	
	/**
	 * Draws a single character to this AsciiImage's Graphics2D object
	 * @param ch Character to draw 
	 * @param rgb Color of character
	 * @param x X location of character
	 * @param y Y location of character
	 */
	public void drawCharacter(char ch, RGB rgb, int x, int y) {
		Font f = new Font("monospace", Font.PLAIN, this.fontSize);
		Color c = new Color(rgb.r, rgb.g, rgb.b);
		this.g.setFont(f);
		if (!(Image2Ascii.flags.contains(Flag.BLACK_AND_WHITE))) g.setColor(c);
		this.g.drawString(Character.toString(ch), x, y);
	}
	
	/**
	 * Zooms to specified value; default is 1.0.
	 * @param x
	 */
	public void zoom(double x) {
		this.zoom = x;
		this.width = (int) (this.lines.get(0).size() * 1.0 * (this.fontSize / 2) * this.zoom);
		this.height = (int) (this.lines.size() * 1.0 * this.fontSize * this.zoom);
		if (Image2Ascii.flags.contains(Image2Ascii.Flag.PACK)) this.height = (int) (this.lines.size() * 1.0 * (this.fontSize-PACKED_SIZE) * this.zoom);
		if (Image2Ascii.flags.contains(Image2Ascii.Flag.PACK)) this.width = (int) (this.lines.get(0).size() * 1.0 * ((this.fontSize / 2) - PACKED_SIZE/2) * this.zoom);
		super.setSize(this.width, this.height);
		this.center();
		
		AffineTransform at = new AffineTransform();
		at.scale(this.zoom, this.zoom);
		this.g.setTransform(at);
		this.drawAscii();
	}
	
	/**
	 * Saves the contents of this AsciiImage's Graphics2D to specified file.
	 * @param path The location on disk to write to
	 * @return If file successfully wrote
	 */
	public boolean export(String path) {
		int w = (int) (this.width / this.zoom);
		int h = (int) (this.height / this.zoom);
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
 		Graphics2D tmp = this.g;

		try {
			Graphics2D graphic = image.createGraphics();
			this.g = graphic;
			this.cleanSlate(w, h);
			this.drawAscii();
	        File output = new File(path);
	        ImageIO.write(image, "png", output);
	    } catch(IOException e) {
	    	System.err.println("Err: Failed to export image.");
	    	e.printStackTrace();
	        return false;
	    } finally {
	    	this.g = tmp;
	    }
		return true;
	}
	
	/**
	 * Centers AsciiFrame on screen
	 */
	private void center() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = super.getSize().width,
			h = super.getSize().height;
		int x = (dim.width - w) / 2,
			y = (dim.height - h) / 2;
		super.setLocation(x, y);
	}
	
	/**
	 * Toggles the visibility of AsciiImage
	 */
	public void toggleVisibility() {
		if (super.isVisible()) super.setVisible(false);
		else super.setVisible(true);
	}

}
