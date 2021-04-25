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
			img = ImageIO.read(new File("/home/adamoates/Pictures/signal-2021-03-24-125935.jpg"));
		} catch (IOException e) {
			System.err.println("Failed to read file.");
			e.printStackTrace();
			System.exit(1);
		}
		
		AsciiImage ascii = new Image2Ascii.AsciiImage(img);
		ascii.export("/home/adamoates/Documents/ascii.txt");
		ascii.exportHtml("/home/adamoates/Documents/ascii.html");
		ascii.getFrame().zoom(0.3);
	}

}

/* TODO:
	* Add the ability to output to JFrame.
	* Add the ability to choose custom font for aforementioned JFrame.
	* Add color support; instead of black-and-white. This should be supported in both command line output and JFrame
	* Possibly make an Image2Unicode and include UTF-8 characters instead of just ASCII.
*/