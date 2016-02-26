package megamek.client.ui.swing.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

/** Abstract class for RasterOp instances which don't change the raster size */
public abstract class ConstantSizeRasterOp implements RasterOp {
    @Override
    public WritableRaster filter(Raster src, WritableRaster dest) {
        if (null == src) {
            throw new NullPointerException("src raster is null");
        }
        if (src == dest) {
            throw new IllegalArgumentException("src raster cannot be the same as the dest raster");
        }
        if (null == dest) {
            dest = createCompatibleDestRaster(src);
        }
        if (src.getHeight() != dest.getHeight() || src.getWidth() != dest.getWidth()) {
            throw new IllegalArgumentException("Width or height of rasters do not match");
        }
        if (src.getNumBands() != dest.getNumBands()) {
            throw new ImagingOpException("Different number of bands in src and dest rasters");
        }
        return dest;
    }
    
    @Override
    public Rectangle2D getBounds2D(Raster src) {
        return src.getBounds();
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster src) {
        return src.createCompatibleWritableRaster();
    }

    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
               return (Point2D)srcPt.clone();
        }
        dstPt.setLocation(srcPt);
        return dstPt;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }
}