package me.adamoates.img2ascii.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


import me.adamoates.img2ascii.main.Image2Ascii.AsciiImage;

public class Main {

	public static void main(String[] args) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("/home/adamoates/Pictures/OneDrive-2021-04-25/IMG_20190913_220054__01.jpg"));
		} catch (IOException e) {
			System.err.println("Failed to read file.");
			e.printStackTrace();
			System.exit(1);
		}
		
		AsciiImage ascii = new Image2Ascii.AsciiImage(img, 1);
		ascii.getFrame().zoom(0.25);
		ascii.export("/home/adamoates/Documents/ascii.txt");
		ascii.exportHtml("/home/adamoates/Documents/ascii.html");
		ascii.exportImage("/home/adamoates/Documents/ascii.png");
	}

}

/* TODO:
	* Add the ability to output to JFrame.
	* Add the ability to choose custom font for aforementioned JFrame.
	* Add color support; instead of black-and-white. This should be supported in both command line output and JFrame
	* Possibly make an Image2Unicode and include UTF-8 characters instead of just ASCII.
*/