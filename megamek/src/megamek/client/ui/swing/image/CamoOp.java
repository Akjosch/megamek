package megamek.client.ui.swing.image;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class CamoOp extends ConstantSizeRasterOp {
    private final Image camo;
    
    public CamoOp(Image camo) {
        this.camo = camo;
    }

    @Override
    public WritableRaster filter(Raster src, WritableRaster dest) {
        dest = super.filter(src, dest);
        int width = dest.getWidth();
        int height = dest.getHeight();
        int[] pCamo = new int[width * height];
        float[] pixel = new float[4];
        PixelGrabber pgCamo = new PixelGrabber(camo.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, width, height, pCamo, 0, width);
        try {
            pgCamo.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("CamoOp.filter(): Failed to grab pixels for camo image." + e.getMessage()); //$NON-NLS-1$
            return dest;
        }
        if ((pgCamo.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("CamoOp.filter(): Failed to grab pixels for mech image. ImageObserver aborted."); //$NON-NLS-1$
            return dest;
        }
        
        for (int x = 0; x < width; ++ x) {
            for (int y = 0; y < height; ++ y) {
                int camoPixel = pCamo[x + y * width];
                src.getPixel(x, y, pixel);
                pixel[0] *= ((camoPixel >> 16) & 0xff) / 255.0;
                pixel[1] *= ((camoPixel >> 8) & 0xff) / 255.0;
                pixel[2] *= (camoPixel & 0xff) / 255.0;
                dest.setPixel(x, y, pixel);
            }
        }
        
        return dest;
    }
}