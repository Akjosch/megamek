package megamek.client.ui.swing.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
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
    
    private SVGImage(SVGDiagram svg) {
    	this(svg, (int)svg.getWidth(), (int)svg.getHeight());
    }
    
    private SVGImage(SVGDiagram svg, int width, int height) {
    	super(width, height, BufferedImage.TYPE_INT_ARGB);
    	this.svg = svg;
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
    	return new SVGImage(svg, width, height);
    }
    
    
    /* (non-Javadoc)
	 * @see megamek.client.ui.swing.image.TintableImage#withTint(java.awt.Color)
	 */
	@Override
	public TintableImage withTint(Color tint) {
		SVGImage result = new SVGImage(svg, this.getWidth(), this.getHeight());
		result.rasterOperation = new ColorTintOp(tint);
		return result;
	}

	/* (non-Javadoc)
	 * @see megamek.client.ui.swing.image.TintableImage#withCamo(java.awt.Image)
	 */
	@Override
	public TintableImage withCamo(Image camo) {
		SVGImage result = new SVGImage(svg, this.getWidth(), this.getHeight());
		result.rasterOperation = new CamoOp(camo);
		return result;
	}

	private void render() {
        Graphics2D gfx = (Graphics2D)super.getGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        try {
        	svg.render(gfx);
            rendered = true;
        } catch (SVGException te) {
        	System.out.println("Could not transcode " + svg + " to raster image; you're going to get a blank BufferedImage of the correct size.");
        } finally {
            gfx.dispose();
        }
    	// Apply camo or tint if available
    	if (null != rasterOperation) {
    		// TODO: Apply
    	}
    }
	
	private static class ColorTintOp implements RasterOp {
		private final Color tint;
		
		public ColorTintOp(Color tint) {
			this.tint = tint;
		}

		@Override
		public WritableRaster filter(Raster src, WritableRaster dest) {
			// TODO Auto-generated method stub
			return null;
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
	
	private static class CamoOp implements RasterOp {
		private final Image camo;
		
		public CamoOp(Image camo) {
			this.camo = camo;
		}

		@Override
		public WritableRaster filter(Raster src, WritableRaster dest) {
			// TODO Auto-generated method stub
			return null;
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
}
