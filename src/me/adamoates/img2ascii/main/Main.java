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
			img = ImageIO.read(new File("/home/adamoates/Pictures/Screenshot from 2021-03-27 18-23-21.png"));
		} catch (IOException e) {
			System.err.println("Failed to read file.");
			e.printStackTrace();
			System.exit(1);
		}
		
		AsciiImage ascii = new Image2Ascii.AsciiImage(img, 0.3, 0.3);
		System.out.println(ascii.export("/home/adamoates/Documents/ascii.txt"));
	}

}

/* TODO:
	* Add the ability to output to JFrame.
	* Add the ability to choose custom font for aforementioned JFrame.
	* Add color support; instead of black-and-white. This should be supported in both command line output and JFrame
	* Possibly make an Image2Unicode and include UTF-8 characters instead of just ASCII.
*/