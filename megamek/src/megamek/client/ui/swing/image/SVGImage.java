package megamek.client.ui.swing.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
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

import com.kitfox.svg.Path;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGElementException;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.animation.AnimationElement;

public final class SVGImage extends BufferedImage implements TransformableImage, FilterableImage {
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
    	SVGElement pivotElement = svg.getElement("pivot");
    	if (pivotElement instanceof Path) {
    		PathIterator pivotPath = ((Path)pivotElement).getShape().getPathIterator(null);
    		if (!pivotPath.isDone()) {
    			double[] coords = new double[6];
    			pivotPath.currentSegment(coords);
    			pivot = new Point2D.Double(coords[0], coords[1]);
    		}
    		// Hide the pivot path element
    		try {
				pivotElement.setAttribute("display", AnimationElement.AT_CSS, "none");
			} catch (SVGElementException e) {
				// Ignore
			}
    	}
    	if (null == pivot) {
    		// Create a pivot in the middle of the SVG image
    		pivot = new Point2D.Double(svg.getWidth() / 2.0, svg.getHeight() / 2.0);
    	}
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
    
    @Override
    public Image getRotatedInstance(Double rot) {
		SVGImage result = new SVGImage(this);
		// Pivot in the middle of the place (TODO: Specific pivot)
		result.pivot = null != pivot ? pivot : new Point2D.Double(getWidth() / 2.0, getHeight() / 2.0);
		result.rotation = rot;
    	result.render();
		return result;
    }
    
	@Override
	public Image withFilter(RasterOp filter) {
		SVGImage result = new SVGImage(this);
		result.rasterOperation = filter;
		result.render();
		return result;
	}

	@Override
	public Image setRasterOperation(RasterOp filter) {
		rasterOperation = filter;
		rendered = false;
		render();
		return this;
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
}
