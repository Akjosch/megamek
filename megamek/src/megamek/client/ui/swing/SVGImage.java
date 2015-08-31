package megamek.client.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

public final class SVGImage extends BufferedImage {
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
    
    private SVGImage(SVGDiagram svg) {
    	this(svg, (int)svg.getWidth(), (int)svg.getHeight());
    }
    
    private SVGImage(SVGDiagram svg, int width, int height) {
    	super(width * 16, height * 16, BufferedImage.TYPE_INT_ARGB);
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
    public WritableRaster getRaster() {
        return super.getRaster();
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
    
    public Image forSize(int width, int height) {
    	return new SVGImage(svg, width, height);
    }
    
    private void render() {
        Graphics2D gfx = (Graphics2D)super.getGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        try {
        	svg.render(gfx);
            rendered = true;
        } catch (SVGException te) {
        	System.out.println("Could not transcode " + svg + " to raster image; you're going to get a blank BufferedImage of the correct size.");
        } finally {
            gfx.dispose();
        }
    }
}
