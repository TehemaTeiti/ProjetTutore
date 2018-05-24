package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

public class ImgFilter {

	public BufferedImage removeRedBlue(BufferedImage im) {
		RGBImageFilter filter = new RGBImageFilter() {		

			public final int filterRGB(int x, int y, int rgb) {
				// filter green
				int green = (rgb & 0x00ff00) >> 8;
				if(green > 254) {
					rgb = (rgb & 0xff00ff00);
				} else {
					rgb = (rgb & 0xff000000) ;
				}
				return rgb ;
			}
				//filter red
				/*int red = (rgb & 0x00ff0000) >> 16;
				if(red > 50) {
					rgb = (rgb & 0xffff0000);
				} else {
					rgb = (rgb & 0xff000000) ;
				}
				return rgb ;
			}*/
		};

		return imageToBufferedImage(
				Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(im.getSource(), filter)));
	}
	
	public BufferedImage imageToBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}
}
