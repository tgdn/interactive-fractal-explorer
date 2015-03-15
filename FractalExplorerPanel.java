import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.lang.Thread;


public class FractalExplorerPanel extends JPanel implements MouseListener, MouseMotionListener,
															Runnable {
	public static final int TYPE_MANDELBROT = 1;
	public static final int TYPE_JULIA = 2;
	
	public int fractal; /* Type of fractal */
	
	private MainFrame mainFrame;
	
	private Thread thread;
	
	/* intial values for mandelbrot */
	private double Xmin = -2.0;
	private double Xmax = 2.0;
	private double Ymin = -1.6;
	private double Ymax = 1.6;
	private int maxIter = 190;
	
	private JuliaFrame juliaFrame;
	
	private Image image;
	private Image bufferImage;
	
	private Graphics graphics;
	private Graphics bufferGraphics;
	
	private int width, height;
	
	private Double mouseX, mouseY; // in complex form
	private int draggingX, draggingY; // in pixel form
	private double percent = 0.0;
	
	private Complex start, end;
	private boolean isSelection, isDrawn, isZoomCancelled = false;
	
	private Complex userSelectedPoint = new Complex(0.0, 0.0);
	
	public FractalExplorerPanel(MainFrame mf, int type) {
		mainFrame = mf;
		thread = null;
		
		// default to mandelbrot
		switch (type) {
		case 2:
			fractal = type;
			break;
		default:
			fractal = TYPE_MANDELBROT;
			break;
		}
		
		if (fractal == TYPE_JULIA)
			return;

		addMouseListener(this);
		addMouseMotionListener(this);		
	}
	
	public void init() {
		if (fractal == TYPE_JULIA) {
			draw();
			return;
		}
		redraw();
	}
	
	@Override
	public void run() {
		while (thread != null) {
			while (draw() || Thread.interrupted()) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {}
				}
			}
		}
	}
	
	private void redraw() {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		} else {
			thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY); // set it to min so it is not obtrusive
			thread.start();
		}
	}
	
	private boolean draw() {
		if (fractal == TYPE_MANDELBROT) {
			if (isDrawn || thread.isInterrupted())
				return false;
		}
		
		initImaging();
		drawLowDef();
		drawHighDef();
		
		isDrawn = true;
		return true;
	}
	
	private void initImaging() {
		Dimension size = getSize();

		if (image == null || size.width != width || size.height != height) {
			width = size.width;
			height = size.height;

			image = createImage(width, height);
			graphics = image.getGraphics();

			bufferImage = createImage(width, height);
			bufferGraphics = bufferImage.getGraphics();

		}
	}
	
	private void drawLowDef() {
		// quickly drawing buffer (only draw some of the pixels)
		for (int x = 0; x < width + 4; x += 8) {
			for (int y = 0; y < height + 4; y += 8) {

				Color color = getColor( pixel2Complex(x, y) );

				graphics.setColor(color);
				graphics.fillRect(x - 4, y - 4, 8, 8);
			}
			repaint();
		}
	}
	
	private void drawHighDef() {
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				Color color = getColor( pixel2Complex(x, y));
				
				graphics.setColor(color);
				graphics.fillRect(x,y, 1,1);
				
				percent = Math.ceil(100.0 * (double)y / (double)height);
			}
			repaint();
		}
	}
	
	// update fields
		public void updateValues(Double minR, Double maxR, Double minI,
								 Double maxI, Integer iterations) {
			
			
			if (fractal == TYPE_JULIA)
				return;
			
			if (juliaFrame != null)
				juliaFrame.updateMaxIter(iterations);
			
			// interrupt current thread and change values
			while (thread.isInterrupted() || percent != 100) {
				synchronized (this) {
					thread.interrupt();
				}
			}
			
			Xmin = minR;
			Xmax = maxR;
			Ymin = minI;
			Ymax = maxI;
			maxIter = iterations;

			isDrawn = false;
			
			while (percent != 100) {
				try {
					wait();
				} catch (InterruptedException e) {}
			}
			// call this not to have a squeezed image
			equalizeAxis();
		}
		
		/* make the ratio of the axis equal to the ratio of the size of the window */
		public void equalizeAxis() {
			
			if (fractal == TYPE_JULIA)
				return;
			
			Dimension size = getSize();
			
			// normalize values
			Ymin = Math.min(Ymin, Ymax);
			Ymax = Math.max(Ymin, Ymax);
			Xmin = Math.min(Xmin, Xmax);
			Xmax = Math.max(Xmin, Xmax);
			
			double ratio = size.width / size.height;
			
			double complexWidth = Xmax - Xmin;
			double complexHeight = Ymax - Ymin;
			
			double complexRatio = complexWidth / complexHeight;
			double eqComplexHeight = complexWidth / ratio;
			double eqComplexWidth = complexHeight * ratio;
			
			// always add and not remove area
			// let user view all selection
			if (ratio > complexRatio) {
				double widthToAdd = (eqComplexWidth - complexWidth) / 2;
				Xmin -= widthToAdd;
				Xmax += widthToAdd;
			}
			else {
				double heightToAdd = (eqComplexHeight - complexHeight) / 2;
				Ymin -= heightToAdd;
				Ymax += heightToAdd;
			}
			
			mainFrame.updateValues(Xmin, Xmax, Ymin, Ymax, maxIter);
			
			redraw();
		}
		
		/* where a and b are both extremities
		 * (upperMin, lowerMax)
		 */
		public void zoom(Complex a, Complex b) {
			
			// dont zoom until finished drawing
			if (thread.isInterrupted() || percent != 100)
				return;
			
			double minX = Math.min(a.getRe(), b.getRe());
			double maxX = Math.max(a.getRe(), b.getRe());
			double minY = Math.min(a.getIm(), b.getIm());
			double maxY = Math.max(a.getIm(), b.getIm());
			
			isDrawn = false;
			
			// update instance variables and call paint
			updateValues(minX, maxX, minY, maxY, maxIter);
			// update fields 
			mainFrame.updateValues(Xmin, Xmax, Ymin, Ymax, maxIter);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			Dimension size = getSize();
			
			if (image == null)
				return;
			
			// for writing on screen purposes
			Graphics2D g2 = (Graphics2D)g;
		    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    					RenderingHints.VALUE_ANTIALIAS_ON);
		    Font font = new Font("Consolas", Font.PLAIN, 12);
		    g2.setFont(font);
			
			bufferGraphics.drawImage(image, 0, 0, null);
			
			g.drawImage(bufferImage, 0, 0, null);
			
			// draw user selected point
			if (userSelectedPoint != null) {
				g2.setColor(Color.white);
				g2.drawString("USER SELECTED POINT", 2, 15);
				g2.drawString("Re:" + userSelectedPoint.getRe() + "  Im:" + userSelectedPoint.getIm(), 2, 32);
			}
			
			// exit if this is julia frame
			if (fractal == TYPE_JULIA)
				return;
			
			/* draw zoom box */
			if (isSelection && !isZoomCancelled) {
				int minX = Math.min(draggingX, (int)complex2Pixel(start).getX());
				int maxX = Math.max(draggingX, (int)complex2Pixel(start).getX());
				int minY = Math.min(draggingY, (int)complex2Pixel(start).getY());
				int maxY = Math.max(draggingY, (int)complex2Pixel(start).getY());
				
				// do draw
				g.setColor(Color.yellow);
				g.drawRect(minX, minY, maxX-minX, maxY-minY);
			}
			
			// draw complex coordinates
			if (mouseX != null && mouseY != null) {
				g2.setColor(Color.white);
				if (isSelection) {
					Complex p = pixel2Complex(draggingX, draggingY);
					g2.drawString("x1:" + mouseX + "  y1:" + mouseY, 2, size.height - 22);
					g2.drawString("x2:" + p.getRe() + "  y2:" + p.getIm(), 2, size.height - 5);
				} else
					g2.drawString("Re:" + mouseX + "  Im:" + mouseY, 2, size.height - 5);
			}
			
			/* draw percent */
			g2.setColor(Color.white);
			g2.drawString(percent + "%", size.width - 100, 15);
		}
		
		private Color getColor(Complex c) {
			int iter = (fractal == TYPE_MANDELBROT) ? mandelbrot(new Complex(0.0, 0.0), c) : julia(c);
			return new Color(iter | (iter << 21)); // get RGB value depending on iteration count
		}
		
		/* compute Mandelbrot escape point for given point */
		private int mandelbrot(Complex z, Complex c) {
			int count = 0;
			
			while (z.getModulus() < 2.0 && count < maxIter) {
				z = z.square().add(c);
				count++;
			}
			
			return count;
		}
		
		private int julia(Complex z) {
			
			int count = 0;
			Complex c = userSelectedPoint;
			
			while (z.getModulus() < 2.0 && count < maxIter) {
				z = z.square().add(c);
				count++;
			}
			
			return count;
		}
		
		public Point2D.Double complex2Pixel(Complex c) {
			
			//Dimension size = getSize();
			// use current saved size so that
			// if user resizes window while
			// rendering, the image is not split
			Dimension size = new Dimension(width, height);
			
			double x = c.getRe();
			double y = c.getIm();
			
			double xp = size.width / (Xmax - Xmin) * (x - Xmin);
			double yp = -(size.height) / (Ymax - Ymin) * (y - Ymax);
			
			return new Point2D.Double(xp, yp);
		}
		
		public Complex pixel2Complex(double x, double y) {
			
			//Dimension size = getSize();
			Dimension size = new Dimension(width, height);
			
			double re = (x / size.width) * (Xmax - Xmin) + Xmin;
			double im = (y / -(size.height)) * (Ymax - Ymin) + Ymax;
			
			return new Complex(re, im);
		}
		
		
		public void setUserSelectedPoint(Complex userSelectedPoint) {
			this.userSelectedPoint = userSelectedPoint;
		}
		
		public void openJuliaSet() {
			if (juliaFrame == null) {
				try {
					juliaFrame = new JuliaFrame();
				} catch (Exception err) {
					return;
				}
			}

			/* Set location of Julia Frame under the pointer so the
			 * user selected point wont change when moving the pointer
			 * over the Mandebrot fractal
			 */
			
			// get mouse position relative to screen
			Point mousePos = MouseInfo.getPointerInfo().getLocation();
			
			// compute the center of the Julia frame
			double x = mousePos.getX() - (juliaFrame.getWidth() / 2);
			double y = mousePos.getY() - (juliaFrame.getHeight() / 2);
			
			juliaFrame.setVisible(true);
			juliaFrame.setLocation((int)x, (int)y);
			juliaFrame.updatePoint(userSelectedPoint);
		}
		
		public void setMaxIter(int iter) {
			maxIter = iter;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// zoom
			draggingX = e.getX();
			draggingY = e.getY();
			isSelection = true;
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
			Complex point = pixel2Complex(x, y);
			
			mouseX = point.getRe();
			mouseY = point.getIm();
			
			repaint();
			
			// update Julia set when mouse moves
			if (juliaFrame != null && fractal == 1 && juliaFrame.isVisible()) {
				userSelectedPoint = pixel2Complex(e.getX(), e.getY());
				juliaFrame.updatePoint(userSelectedPoint);
			}
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
			// only do something when displaying mandelbrot
			if (fractal == TYPE_JULIA)
				return;
			
			userSelectedPoint = pixel2Complex(e.getX(), e.getY());
			openJuliaSet();
			
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// store the starting point of the drag
			// so we can compute zoom area to zoom when 
			// mouse released
			start = new Complex(mouseX, mouseY);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
			// end point of drag 
			end = pixel2Complex(x, y);
			
			// zoom if the mouse has only been released after a drag
			if (isSelection && !start.equals(end) && !isZoomCancelled)
				zoom(start, end);
			
			// update values 
			isSelection = false;
			isZoomCancelled = false;
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		public int getMaxIter() {
			return maxIter;
		}

		public Image getImage() {
			return image;
		}

		public Image getBufferImage() {
			return bufferImage;
		}

		public Graphics getIGraphics() {
			return graphics;
		}

		public Graphics getBufferGraphics() {
			return bufferGraphics;
		}

}
