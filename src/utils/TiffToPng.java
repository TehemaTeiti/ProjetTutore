package utils;
/* How to use this class : 
    If the input file is in the tif format use this class to generate a copy in png format readable by filters
    Don’t forget to adjust the name of the file

 Exemple in calling function :

        if(name.contains(".tif")) {
            TiffToPng convert = new TiffToPng(name);
            name = convert.changeName(name);
        }
 */

import java.io.File;
import java.awt.image.RenderedImage;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.imageio.ImageIO;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageCodec;
import java.io.IOException;

public class TiffToPng extends ImageFormatConverter {

	public TiffToPng(String name) {
		File f = new File(name);
		name = changeName(name);
		File out = new File(name);
		SeekableStream s;
		try {
			s = new FileSeekableStream(f);
			TIFFDecodeParam param = null ;
			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
			RenderedImage op = new NullOpImage(dec.decodeAsRenderedImage(0),
					null,
					OpImage.OP_IO_BOUND,
					null);
			ImageIO.write(op, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
