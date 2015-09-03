package megamek.client.ui.swing.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Image consisting of multiple base images, layered on top of each other. Rendering is delayed until
 * the final width and height are known.
 */
public class CompositeImage extends BufferedImage implements TransformableImage, FilterableImage {
	private final List<Image> baseImages;
	
    private boolean rendered = false;
    private RasterOp rasterOperation = null;
    private Point2D pivot = null;
    private double rotation = 0.0;
    private double scaleX = 1.0;
    private double scaleY = 1.0;

    public CompositeImage(int width, int height) {
    	this((List<Image>)null, width, height);
    }
    
    public CompositeImage(Image base, int width, int height) {
    	this(Arrays.<Image>asList(base), width, height);
    }
    
	public CompositeImage(List<Image> baseImages, int width, int height) {
		super(width, width, TYPE_INT_ARGB);
		if (null == baseImages) {
			this.baseImages = new ArrayList<Image>(0);
		} else {
			this.baseImages = new ArrayList<Image>(baseImages);
		}
		pivot = new Point2D.Double(width / 2.0, width / 2.0);
	}
	
    /** Clone constructor with explicit width/height */
	private CompositeImage(CompositeImage other, int width, int height) {
		super(width, width, TYPE_INT_ARGB);
		this.baseImages = other.baseImages;
		this.rasterOperation = other.rasterOperation;
		this.pivot = other.pivot;
		this.rotation = other.rotation;
	}
	
    /** Clone constructor */
	private CompositeImage(CompositeImage other) {
		this(other, other.getWidth(), other.getHeight());
	}
	
	public void addImage(Image img) {
		if (null != img) {
			baseImages.add(img);
		}
	}
	
	public void addImages(Image ... img) {
		Collections.<Image>addAll(baseImages, img);
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
    	CompositeImage result = new CompositeImage(this, width, height);
    	result.scaleX = scaleX * width / getWidth();
    	result.scaleY = scaleY * height / getHeight();
    	result.render();
    	return result;
    }
    
    @Override
    public Image getRotatedInstance(double rot) {
		CompositeImage result = new CompositeImage(this);
		result.rotation = rot;
    	result.render();
		return result;
    }

	@Override
	public Image withFilter(RasterOp filter) {
		CompositeImage result = new CompositeImage(this);
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
        //gfx.scale(scaleX, scaleY);
        //if (0.0 != rotation) {
        //	gfx.translate(pivot.getX(), pivot.getY());
        //	gfx.rotate(rotation);
        //	gfx.translate(-pivot.getX(), -pivot.getY());
        //}
        for( Image img : baseImages ) {
        	Image scaledImg = img.getScaledInstance((int)(scaleX * img.getWidth(null)), (int)(scaleY * img.getHeight(null)), SCALE_SMOOTH);
        	gfx.drawImage(scaledImg, null, null);
        }
        gfx.dispose();

    	// Apply camo or tint (or other operations/filters) if available
    	if (null != rasterOperation) {
    		WritableRaster raster = rasterOperation.filter(getRaster(), null);
    		setData(raster);
    	}
    }
}
