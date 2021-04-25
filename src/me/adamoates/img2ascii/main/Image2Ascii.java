package me.adamoates.img2ascii.main;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

import javax.imageio.ImageIO;

/**
 * @author Adam Oates adam.oates@criptext.com
 */
public class Image2Ascii {
	
	public static EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
	
	/**
	 * Flags to pass to the AsciiImage object pre-processing
	 * @author Adam Oates adam.oates@criptext.com
	 */
	public static enum Flag {
		INVERT,
		BLACK_AND_WHITE,
		GRAYSCALE,
		PACK,
		SINGLE_CHAR;
	}

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
		 * Instantiates an RGB object from specified values. Takes range from 0-255.
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
		 * Instantiates an RGB object from the specified values. Takes range from 0.0-1.0.
		 * @param r 
		 * @param g
		 * @param b
		 */
		public RGB(double r, double g, double b) {
			this((int)r*255, (int)g*255, (int)b*255);
		}
		
		/**
		 * Instantiates an RGB object from integer.
		 * @param clr The integer to convert
		 * @see #fromInt(int)
		 */
		public RGB(int clr) {
			this.fromInt(clr);
		}
		
		/**
		 * Converts integer value to red, green, and blue values.
		 * @param clr The integer to convert
		 */
		private void fromInt(int clr) {
			this.r = (clr & 0x00ff0000) >> 16;
			this.g = (clr & 0x0000ff00) >> 8;
			this.b = (clr & 0x000000ff);
		}
		
		/**
		 * Inverts the color.
		 */
		public void invert() {
			int clr = this.toInt() ^ 0x00ffffff;
			this.fromInt(clr);
		}
		
		/**
		 * Converts the color to grayscale.
		 */
		public void grayscale() {
			double R_linear = SrgbToLinear(this.r/255.0);
			double G_linear = SrgbToLinear(this.g/255.0);
			double B_linear = SrgbToLinear(this.b/255.0);
			double gray_linear = 0.2126 * R_linear + 0.7152 * G_linear + 0.0722 * B_linear;
			int gray_color = (int) Math.round(linearToSrgb(gray_linear) * 255);
			this.r = gray_color;
			this.g = gray_color;
			this.b = gray_color;
		}
		
		/**
		 * Converts the red, green, and blue values to an integer.
		 * @return The converted integer
		 */
		public int toInt() {
			int r = (this.r << 16) & 0x00ff0000,
				g = (this.g << 8) & 0x0000ff00,
				b = this.b & 0x000000ff;
			return 0xff000000 | r | g | b;
		}
		
		/**
		 * Tests if two RGB objects are equal.
		 * @return
		 */
		public boolean equals(Object obj) {
			if (!(obj instanceof RGB)) return false;
			
			RGB rgb = (RGB) obj;
			if (rgb.r == this.r && rgb.g == this.g && rgb.b == this.b) return true;
			else return false;
		}
		
		/**
		 * Creates a copy of RGB
		 * @return
		 */
		public RGB clone() {
			return new RGB(this.r, this.g, this.b);
		}
		
		/**
		 * placeholder
		 * @param x
		 * @return
		 */
		private static double SrgbToLinear(double x) {
		    if (x < 0.04045) return x/12.92;
		    return Math.pow((x+0.055)/1.055, 2.4);
		}
		
		/**
		 * placeholder
		 * @param y
		 * @return
		 */
		private static double linearToSrgb(double y) {
			if (y <= 0.0031308) return 12.92 * y;
			return 1.055 * Math.pow(y, 1/2.4) - 0.055;
		}
	}


	/**
	 * @author Adam Oates adam.oates@criptext.com
	 */
	public static class AsciiImage {
		// same as int[][]
		private ArrayList<ArrayList<Character>> ascii_index = new ArrayList<>();
		private ArrayList<ArrayList<RGB>> ascii_index_color = new ArrayList<>();
		private AsciiFrame aframe;
		
		/**
		 * Instantiates an AsciiImage at normal scale and tries to adjust for
		 * vertical stretching.
		 * @param path Path to image to convert to ASCII
		 * @throws IOException
		 */
		public AsciiImage(String path) throws IOException {
			this(ImageIO.read(new File("/home/adamoates/Pictures/123248749_1010450512756962_4390809450020714039_n.jpg")), 1.0);
		}
		
		/**
		 * Instantiates an AsciiImage at normal scale and tries to adjust for
		 * vertical stretching.
		 * @param bimg Image to convert to ASCII
		 */
		public AsciiImage(BufferedImage bimg) {
			this(bimg, 1.0, 1.0);
		}
		
		/**
		 * Instantiates an AsciiImage at the specified scale and tries to adjust
		 * for vertical stretching.
		 * @param path Path to image to convert to ASCII
		 * @param scale How much to scale the X and Y dimensions
		 * @throws IOException
		 */
		public AsciiImage(String path, double scale) throws IOException {
			this(ImageIO.read(new File("/home/adamoates/Pictures/123248749_1010450512756962_4390809450020714039_n.jpg")), scale);
		}
		
		/**
		 * Instantiates an AsciiImage at specified scaled and tries to adjust
		 * for vertical stretching.
		 * @param bimg Image to convert to ASCII
		 * @param scale How much to scale the X and Y dimensions
		 */
		public AsciiImage(BufferedImage bimg, double scale) {
			this(bimg, scale, scale);
		}
		
		/**
		 * Instantiates an AsciiImage at specified scale and tries to adjust
		 * for vertical stretching.
		 * @param path Path to image to convert to ASCII
		 * @param scalex How much to scale the X dimension
		 * @param scaley How much to scale the Y dimension
		 * @throws IOException
		 */
		public AsciiImage(String path, double scalex, double scaley) throws IOException {
			this(ImageIO.read(new File("/home/adamoates/Pictures/123248749_1010450512756962_4390809450020714039_n.jpg")), scalex, scaley, true);
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
		 * @param path Path to image to convert to ASCII
		 * @param scalex How much to scale the X dimension
		 * @param scaley How much to scale the Y dimension
		 * @param adjust_for_stretching Adjust for vertical stretching?
		 * @throws IOException
		 */
		public AsciiImage(String path, double scalex, double scaley, boolean adjust_for_stretching) throws IOException {
			this(ImageIO.read(new File("/home/adamoates/Pictures/123248749_1010450512756962_4390809450020714039_n.jpg")), scalex, scaley, adjust_for_stretching);
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
				this.ascii_index_color.add(new ArrayList<>(scaled_img.getWidth()));
				for (int x = 0; x < scaled_img.getWidth(); x++) {
					RGB rgb = new RGB(scaled_img.getRGB(x, y));
					
					if (flags.contains(Flag.INVERT)) rgb.invert();
					if (flags.contains(Flag.GRAYSCALE)) rgb.grayscale();
					
					int idx = Image2Ascii.rgbToAsciiTable(rgb);
					char ch = asciiTable[idx];
					if (flags.contains(Flag.SINGLE_CHAR)) ch = '@';
					this.ascii_index.get(y).add(ch);
					this.ascii_index_color.get(y).add(rgb);
				}
			}
			

			this.aframe = new AsciiFrame(this.ascii_index, this.ascii_index_color);
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
		 * Gets the RGB object from the specified position
		 * @param x The X location to pull from
		 * @param y The Y location to pull from
		 * @return The RGB object found at <code>x</code> and <code>y</code>
		 */
		public RGB getRgb(int x, int y) {
			return this.ascii_index_color.get(y).get(x);
		}
		
		/**
		 * Converts the AsciiImage to a String.
		 * @return AsciiImage character contents
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
		 * Converts the AsciiImage to HTML.
		 * @return AsciiImage character contents as HTML with color values.
		 */
		public String toHtml() {
			StringBuilder sb = new StringBuilder();
			sb.append("<style>span { font-size: 6px; font-family: monospace; }</style>");
			RGB last = null;
			for (int y = 0; y < this.ascii_index.size(); y++) {
				for (int x = 0; x < this.ascii_index.get(y).size(); x++) {
					RGB rgb = this.getRgb(x, y);
					char ch = this.get(x, y);
					if (rgb.equals(last)) {	// add repeating color characters to the same DOM element
						sb.append(ch);
					} else {
						if (!(last==null)) sb.append("</span>");
						String htmlElem = "<span style=\"color: rgb(" + rgb.r + ", " + rgb.g + ", " + rgb.b + ");\">" + ch;
						sb.append(htmlElem);
					}
					last = rgb;
				}
				sb.append("<br>");
			}
			return sb.toString();
		}
		
		/**
		 * Gets the AsciiFrame
		 * @return
		 * @see AsciiFrame#AsciiFrame(ArrayList, ArrayList)
		 */
		public AsciiFrame getFrame() {
			return this.aframe;
		}
		
		/**
		 * Exports the AsciiImage to specified file path
		 * @param path The location on disk to write to
		 * @param overwrite Whether or not to overwrite if a file already exists at this path
		 * @return Whether or not file successfully wrote to disk
		 */
		public boolean export(String path) {
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
		 * Exports the contents of AsciiFrame as PNG image to specified path.
		 * @param path The location on disk to write to
		 * @return If successfully saved
		 * @see AsciiFrame#export(String)
		 */
		public boolean exportImage(String path) {
		    return this.getFrame().export(path);
		}
		
		/**
		 * Exports the contents of AsciiFrame as HTML to specified path
		 * @param path The location on disk to write to
		 * @return If successfully saved
		 * @see #toHtml()
		 */
		public boolean exportHtml(String path) {
			FileWriter file = null;
			try {
				file = new FileWriter(path);
				file.write(this.toHtml());
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
	
	private static char[] asciiTable = {
			// 94 characters because these are most of the printable ASCII characters
			'@', '$', '%', 'G', '&', '8', 'W', '6', '9', 'm', 'Q', 'g', 'b', 'O',
			'3', 'p', '0', '#', 'X', 'd', 'h', 'S', 'k', 'U', 'B', 'A', 'e', 'R',
			'w', 'q', 'u', 'a', 'N', 'n', 'V', 'P', 'y', 'x', '5', 'C', '{', '2',
			'}', 's', '4', 'j', '?', 'v', 'o', 'M', 'z', 'E', '|', 'i', 'Y', 'c',
			'Z', 'K', ']', '[', '*', 'l', '>', 'I', '1', '<', '/', '\\', 't', '+',
			'F', 'D', '7', ')', 'T', '"', '(', 'r', 'H', 'f', 'L', 'J', '^', '~',
			'!', ';', '=', ',', '\'', '`', ':', '-', '.', '_'
	};
	
	/**
	 * Converts an RGB value to a index in asciiTable
	 * @param rgb The RGB object to convert
	 * @return An index in asciiTable
	 */
	private static int rgbToAsciiTable(RGB rgb) {
		rgb = rgb.clone();
		rgb.grayscale();
		return (int) Math.round(rgb.r / 255.0 * (asciiTable.length-1));
	}
	
}
