package megamek.client.ui.swing.image;

import java.awt.Color;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ColorTintOp extends ConstantSizeRasterOp {
    private final float redTint;
    private final float greenTint;
    private final float blueTint;
    
    public ColorTintOp(Color tint) {
        float[] tints = tint.getRGBComponents(null);
        this.redTint = tints[0];
        this.greenTint = tints[1];
        this.blueTint = tints[2];
    }

    /**
     * @param tint ARGB tint
     */
    public ColorTintOp(int tint) {
        this(new Color(tint));
    }
    
    @Override
    public WritableRaster filter(Raster src, WritableRaster dest) {
        dest = super.filter(src, dest);
        float[] pixel = new float[4];
        for (int x = src.getWidth() - 1; x >= 0; -- x) {
            for (int y = src.getHeight() - 1; y >= 0; -- y) {
                src.getPixel(x, y, pixel);
                pixel[0] *= redTint;
                pixel[1] *= greenTint;
                pixel[2] *= blueTint;
                dest.setPixel(x, y, pixel);
            }
        }
        return dest;
    }
}