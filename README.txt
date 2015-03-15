

Complex.java: Contains the methods used for complex number manipulation as well as debug functions such as printPolar() and printCartesian();

FractalExplorer.java: main class of the program, only instantiates the GUI;

FractalExplorerPanel.java: a subclass of JPanel that basically draws a Mandelbrot fractal or a Julia fractal on it; uses a buffer image and threading to speed up the process; the actual fractal is drawn on an image which is then painted on the JPanel; this class contains most of the code and most of the calculations;

JuliaFavouriteManager.java: a class that opens or creates a favourites file, that reads its content and puts it in a ComboBox for the user to choose from; it can also save new favourites;

JuliaFrame.java: a subclass of JFrame that incorporates a FractalExplorerPanel that draws the Julia fractal; it also triggers the add to favourites button;

MainFrame.java: the GUI's main frame, it incorporates FractalExplorerPanel that draws the Mandelbrot fractal; It also has controls for the Complex plane's visible area and for the number of iterations used to draw the fractal, when the number of iterations changes, it is also updated on the julia frame; on this frame it is possible to zoom in by selecting the zoom area; something not possible on the julia frame, although that could be implemented in the future;
