package me.adamoates.img2ascii.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import me.adamoates.img2ascii.main.Image2Ascii.Flag;
import me.adamoates.img2ascii.main.Image2Ascii.RGB;

public class AsciiFrame extends JFrame {
	
	private ArrayList<ArrayList<Character>> lines;
	private ArrayList<ArrayList<RGB>> colors;
	private int currentLine = 1;
	private int currentCharacter = 1;
	private int fontSize = 10;
	private Graphics2D g;
	private double zoom = 1.0;
	
	public AsciiFrame(ArrayList<ArrayList<Character>> lines, ArrayList<ArrayList<RGB>> colors) {
		this(lines, colors, "Image2Ascii");
	}
	
	public AsciiFrame(ArrayList<ArrayList<Character>> lines, ArrayList<ArrayList<RGB>> colors, String title) {
		super();
		super.setTitle(title);
		
		ImageIcon logo = new ImageIcon("logo.png");
		super.setIconImage(logo.getImage());
		
		super.setResizable(false);
		
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.lines = lines;
		this.colors = colors;
		this.fontSize = 16;	// this should make
		
		this.toggleVisibility();
		
		this.g = (Graphics2D) super.getGraphics();
	}
	
	public void drawAscii() {
		this.g.clearRect(0, 0, super.getWidth()*2, super.getHeight()*2);
		
		for (int i = 0; i < this.lines.size(); i++) {
			int y = this.fontSize * this.currentLine++;
			//if (Image2Ascii.flags.contains(Image2Ascii.Flag.PACK)) y -= this.currentLine * 10;
			this.currentCharacter = 1;
			for (int j = 0; j < lines.get(i).size(); j++) {
				int x = (this.fontSize / 2) * this.currentCharacter++;
				//if (Image2Ascii.flags.contains(Image2Ascii.Flag.PACK)) x -= this.currentCharacter * 10 / 2;
				char ch = lines.get(i).get(j);
				RGB rgb = colors.get(i).get(j);
				this.drawCharacter(ch, rgb, x, y);
			}
		}
		
		this.currentLine = 1;
		this.currentCharacter = 1;
	}
	
	public void zoom(double x) {
		this.zoom = x;
		
		int w = (int) (this.lines.get(0).size() * 1.0 * (this.fontSize / 2) * this.zoom);
		int h = (int) (this.lines.size() * 1.0 * this.fontSize * this.zoom);
		super.setSize(w, h);
		this.center();
		
		AffineTransform at = new AffineTransform();
		at.scale(this.zoom, this.zoom);
		this.g.setTransform(at);
		this.drawAscii();
	}
	
	public void drawCharacter(char ch, RGB rgb, int x, int y) {
		Font f = new Font("monospace", Font.PLAIN, this.fontSize);
		Color c = new Color(rgb.r, rgb.g, rgb.b);
		this.g.setFont(f);
		if (!(Image2Ascii.flags.contains(Flag.BLACK_AND_WHITE))) g.setColor(c);
		this.g.drawString(Character.toString(ch), x, y);
	}
	
	private void center() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = super.getSize().width,
			h = super.getSize().height;
		int x = (dim.width - w) / 2,
			y = (dim.height - h) / 2;
		super.setLocation(x, y);
	}
	
	public void toggleVisibility() {
		if (super.isVisible()) super.setVisible(false);
		else super.setVisible(true);
	}

}
