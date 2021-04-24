package me.adamoates.img2ascii.main;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Adam Oates adam.oates@criptext.com
 */
public class Image2Ascii {
	

	/**
	 * @author Adam Oates adam.oates@criptext.com
	 */
	public static class RGB {
		public int r = 0;
		public int g = 0;
		public int b = 0;
		
		/**
		 * Instantiates an RGB object as 0, 0, 0 (black).
		 */
		public RGB() {}
		
		/**
		 * Instantiates an RGB object from specified values.
		 * @param r The red value
		 * @param g The green value
		 * @param b The blue value
		 */
		public RGB(int r, int g, int b) {
			if (r > 255) r = 255;
			if (g > 255) g = 255;
			if (b > 255) b = 255;
			
			if (r < 0) r = 0;
			if (g < 0) g = 0;
			if (b < 0) b = 0;
			
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		/**
		 * Instantiates an RGB object from integer.
		 * @param clr The integer to convert
		 */
		public RGB(int clr) {
			this.r = (clr & 0x00ff0000) >> 16;
			this.g = (clr & 0x0000ff00) >> 8;
			this.b = (clr & 0x000000ff);
		}
	}


	/**
	 * @author Adam Oates adam.oates@criptext.com
	 */
	public static class AsciiImage {
		// same as int[][]
		private ArrayList<ArrayList<Character>> ascii_index = new ArrayList<>();
		
		/**
		 * Instantiates an AsciiImage at normal scale and tries to adjust for
		 * vertical stretching.
		 * @param bimg Image to convert to ASCII
		 */
		public AsciiImage(BufferedImage bimg) {
			this(bimg, 1.0, 1.0);
		}
		
		/**
		 * Instantiates an AsciiImage at specified scale and tries to adjust
		 * for vertical stretching.
		 * @param bimg Image to convert to ASCII
		 * @param scalex How much to scale the X dimension
		 * @param scaley How much to scale the Y dimension
		 */
		public AsciiImage(BufferedImage bimg, double scalex, double scaley) {
			this(bimg, scalex, scaley, true);
		}
		
		/**
		 * Instantiates an AsciiImage at specified scale and whether or not to
		 * adjust for vertical stretching.
		 * @param bimg Image to convert to ASCII
		 * @param scalex How much to scale the X dimension
		 * @param scaley How much to scale the Y dimension
		 * @param adjust_for_stretching Adjust for vertical stretching?
		 */
		public AsciiImage(BufferedImage bimg, double scalex, double scaley, boolean adjust_for_stretching) {
			BufferedImage scaled_img = resizeImage(bimg, scalex, scaley);

			// Because a line is taller than a character is wide, the printed
			// ASCII image is stretched vertically. On average a line is slightly
			// smaller than double the width of a character, so 0.49 seems to
			// be a good size
			if (adjust_for_stretching) scaled_img = resizeImage(scaled_img, 1.0, 0.49);
			
			for (int y = 0; y < scaled_img.getHeight(); y++) {
				this.ascii_index.add(new ArrayList<>(scaled_img.getWidth()));
				for (int x = 0; x < scaled_img.getWidth(); x++) {
					RGB rgb = new RGB(scaled_img.getRGB(x, y));
					int idx = Image2Ascii.rgbToAsciiTable(rgb);
					Character ch = asciiTable[idx];
					this.ascii_index.get(y).add(ch);
				}
			}
		}
		
		/**
		 * Gets the character from the specified position.
		 * @param x The X location to pull from
		 * @param y The Y location to pull from
		 * @return The character found at x and y
		 */
		public char get(int x, int y) {
			return this.ascii_index.get(y).get(x);
		}
		
		/**
		 * Converts the AsciiImage to a String.
		 * @return AsciiImage values
		 */
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int y = 0; y < this.ascii_index.size(); y++) {
				for (int x = 0; x < this.ascii_index.get(y).size(); x++) {
					sb.append(this.get(x, y));
				}
				sb.append('\n');
			}
			return sb.toString();
		}
		
		/**
		 * Exports the AsciiImage to specified file path and overwrites file if
		 * it already exists.
		 * @param path The location on disk to write AsciiImage to
		 * @return Whether or not file successfully wrote to disk
		 */
		public boolean export(String path) {
			return this.export(path, true);
		}
		
		/**
		 * Exports the AsciiImage to specified file path
		 * @param path The location on disk to write AsciiImage to
		 * @param overwrite Whether or not to overwrite if a file already exists at this path
		 * @return Whether or not file successfully wrote to disk
		 */
		public boolean export(String path, boolean overwrite) {
			FileWriter file = null;
			try {
				file = new FileWriter(path);
				file.write(this.toString());
				file.close();
			} catch (IOException e) {
				System.out.println("An error occured.");
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		/**
		 * Resizes a BufferedImage to specified percentage of originalImage.
		 * @param originalImage The BufferedImage to scale
		 * @param scalex The percentage to scale the X dimension
		 * @param scaley The percentage to scale the Y dimension
		 * @return The scaled BufferedImage
		 */
		private static BufferedImage resizeImage(BufferedImage originalImage, double scalex, double scaley) {
			int targetWidth = (int) (originalImage.getWidth() * scalex);
			int targetHeight = (int) (originalImage.getHeight() * scaley);
			return resizeImage(originalImage, targetWidth, targetHeight);
		}
		
		/**
		 * Resizes a BufferedImage to specified width and height of originalImage.
		 * @param originalImage The BufferedImage to scale
		 * @param targetWidth The width to scale up/down to
		 * @param targetHeight The height to scale up/down to
		 * @return The scaled BufferedImage
		 */
		private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
		    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
		    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
		    return outputImage;
		}
	}
	
	// TODO:
	// these are just random ASCII characters, but eventually
	// I want them to be in order of which one covers the most
	// screen space. i.e. rgb(0,0,0) would be something like #
	// while rgb(255,255,255) would be `
	public static char[] asciiTable = {
			// 94 characters because these are most of the printable ASCII characters
			'#', '@', '!', '$', '%', '&', '\'',
			'(', ')', '*', '+', ',', '-', '.',
			'/', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', ':', ';', '<',
			'=', '>', '?', '"', '`', 'A', 'B',
			'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z', '[', '\\', '~', ']',
			'^', '_', 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z',
			'{', '|', '}'
	};
	
	/**
	 * Converts an RGB value to a index in asciiTable
	 * @param rgb The RGB object to convert
	 * @return An index in asciiTable
	 */
	private static int rgbToAsciiTable(RGB rgb) {
		return (int) Math.round((rgb.r + rgb.g + rgb.b) / (255.0 * 3) * (asciiTable.length-1));
	}
	
}
