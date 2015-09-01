package megamek.client.ui.swing.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

public final class SVGImage extends BufferedImage implements SelfScalingImage, TintableImage {
	private final static SVGUniverse universe = new SVGUniverse();
	
    public static SVGImage fromFile(File file) {
    	SVGDiagram svg = null;
    	try(InputStream fileStream = new FileInputStream(file)) {
			URI svgFile = universe.loadSVG(fileStream, file.toString());
			svg = universe.getDiagram(svgFile);
		} catch (IOException e) {
			return null;
		}
    	if (null == svg) {
    		return null;
    	}
		svg.setIgnoringClipHeuristic(true);
        return new SVGImage(svg);
    }
    
    private boolean rendered = false;
    private final SVGDiagram svg;
    private RasterOp rasterOperation = null;
    private Point2D pivot = null;
    private Double rotation = null;
    
    private SVGImage(SVGDiagram svg) {
    	this(svg, (int)svg.getWidth(), (int)svg.getHeight());
    }
    
    private SVGImage(SVGDiagram svg, int width, int height) {
    	super(width, height, BufferedImage.TYPE_INT_ARGB);
    	this.svg = svg;
    }
    
    /** Clone constructor */
    private SVGImage(SVGImage other) {
    	this(other.svg, other.getWidth(), other.getHeight());
    	this.rasterOperation = other.rasterOperation;
    	this.pivot = other.pivot;
    	this.rotation = other.rotation;
    }
    
    /** Clone constructor with explicit width/height */
    private SVGImage(SVGImage other, int width, int height) {
    	this(other.svg, width, height);
    	this.rasterOperation = other.rasterOperation;
    	this.pivot = other.pivot;
    	this.rotation = other.rotation;
    }
    
    @Override
    public void coerceData(boolean isAlphaPremultiplied) {
        if (!rendered) {
        	render();
        }
        super.coerceData(isAlphaPremultiplied);
    }
    @Override
    public WritableRaster copyData(WritableRaster outRaster) {
        if (!rendered) {
        	render();
        }
        return super.copyData(outRaster);
    }
    
    @Override
    public WritableRaster getAlphaRaster() {
        if (!rendered) {
        	render();
        }
        return super.getAlphaRaster();
    }
    
    @Override
    public Raster getData() {
        if (!rendered) {
        	render();
        }
        return super.getData();
    }

    @Override
    public Graphics getGraphics() {
        if (!rendered) {
        	render();
        }
        return super.getGraphics();
    }
    
    @Override
    public ImageProducer getSource() {
        if (!rendered) {
        	render();
        }
    	return super.getSource();
    }
    
    @Override
    public Image getScaledInstance(int width, int height, int hints) {
    	SVGImage result = new SVGImage(this, width, height);
    	result.render();
    	return result;
    }
    
    /** Pass (Double)null for an unrotated instance */
    public Image getRotatedInstance(Double rot) {
		SVGImage result = new SVGImage(this);
		// Pivot in the middle of the place (TODO: Specific pivot)
		result.pivot = new Point2D.Double(getWidth() / 2.0, getHeight() / 2.0);
		result.rotation = rot;
    	result.render();
		return result;
    }
    
    /* (non-Javadoc)
	 * @see megamek.client.ui.swing.image.TintableImage#withTint(java.awt.Color)
	 */
	@Override
	public Image withTint(Color tint) {
		SVGImage result = new SVGImage(this);
		result.rasterOperation = new ColorTintOp(tint);
		result.render();
		return result;
	}

	/* (non-Javadoc)
	 * @see megamek.client.ui.swing.image.TintableImage#withCamo(java.awt.Image)
	 */
	@Override
	public Image withCamo(Image camo) {
		SVGImage result = new SVGImage(this);
		result.rasterOperation = new CamoOp(camo);
		result.render();
		return result;
	}

	private void render() {
        Graphics2D gfx = (Graphics2D)super.getGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        // Scaling
        gfx.scale(getWidth() / svg.getWidth(), getHeight() / svg.getHeight());
        if (null != rotation) {
        	gfx.translate(pivot.getX(), pivot.getY());
        	gfx.rotate(rotation);
        	gfx.translate(-pivot.getX(), -pivot.getY());
        }
        try {
        	svg.render(gfx);
            rendered = true;
        } catch (SVGException te) {
        	System.out.println("Could not transcode " + svg + " to raster image; you're going to get a blank BufferedImage of the correct size.");
        } finally {
            gfx.dispose();
        }
    	// Apply camo or tint (or other operations/filters) if available
    	if (null != rasterOperation) {
    		WritableRaster raster = rasterOperation.filter(getRaster(), null);
    		setData(raster);
    	}
    }
	
	/** Abstract class for RasterOp instances which don't change the raster size */
	private static abstract class ConstantSizeRasterOp implements RasterOp {
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
	
	private static class ColorTintOp extends ConstantSizeRasterOp {
		private final float redTint;
		private final float greenTint;
		private final float blueTint;
		
		public ColorTintOp(Color tint) {
			float[] tints = tint.getRGBComponents(null);
			this.redTint = tints[0];
			this.greenTint = tints[1];
			this.blueTint = tints[2];
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
	
	private static class CamoOp extends ConstantSizeRasterOp {
		private final Image camo;
		
		public CamoOp(Image camo) {
			this.camo = camo;
		}

		@Override
		public WritableRaster filter(Raster src, WritableRaster dest) {
			dest = super.filter(src, dest);
			// TODO Auto-generated method stub
			return dest;
		}
	}
}
