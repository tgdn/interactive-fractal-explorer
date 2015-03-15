import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;


public class MainFrame extends JFrame implements PropertyChangeListener {
	
	// initial values for the fields
	protected Double minR = -2.0;
	protected Double maxR = 2.0;
	protected Double minI = -1.6;
	protected Double maxI = 1.6;
	
	// default number of iterations
	protected Integer iterations = 100;
	
	// labels
	private JLabel rMinLabel;
	private JLabel rMaxLabel;
	private JLabel iMinLabel;
	private JLabel iMaxLabel;
	private JLabel iterLabel;
	
	// fields
	private JFormattedTextField rMinField;
	private JFormattedTextField rMaxField;
	private JFormattedTextField iMinField;
	private JFormattedTextField iMaxField;
	
	private JFormattedTextField iterValueField;
	private JButton equalizeAxisButton;

	// format for control display fields
	private DecimalFormat controlFormat;
	
	private FractalExplorerPanel explorerPanel;
	
	public MainFrame() throws HeadlessException {
		
		super("Fractal Explorer");
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(800, 850); // set start size
		this.setMinimumSize(new Dimension(456, 552));
		//this.setResizable(false); // just so we dont need to do anything
		
		// setup ui elements
		this.setup();
		
		// setup explorer
		explorerPanel.init();
		
	}
	
	public void setup() {
		
		// setup main pane
		JPanel mainpanel = new JPanel(new BorderLayout());
		this.setContentPane(mainpanel);
		
		// panel containing the fractal
		explorerPanel = new FractalExplorerPanel(this, FractalExplorerPanel.TYPE_MANDELBROT);
		mainpanel.add(explorerPanel, BorderLayout.CENTER);
		
		// panel containing the main controls for the display
		JPanel buttonspanel = new JPanel(new BorderLayout());
		buttonspanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainpanel.add(buttonspanel, BorderLayout.SOUTH);
		
		// set up labels
		rMinLabel = new JLabel("Minimum");
		rMaxLabel = new JLabel("Maximum");
		iMinLabel = new JLabel("Minimum");
		iMaxLabel = new JLabel("Maximum");
		iterLabel = new JLabel("N° of iterations");
		
		// set up text fields
		rMinField = new JFormattedTextField(controlFormat);
		rMinField.setValue(minR);
		rMinField.setColumns(10);
		rMinField.addPropertyChangeListener("value", this);

		rMaxField = new JFormattedTextField(controlFormat);
		rMaxField.setValue(maxR);
		rMaxField.setColumns(10);
		rMaxField.addPropertyChangeListener("value", this);
		
		iMinField = new JFormattedTextField(controlFormat);
		iMinField.setValue(minI);
		iMinField.setColumns(10);
		iMinField.addPropertyChangeListener("value", this);
		
		iMaxField = new JFormattedTextField(controlFormat);
		iMaxField.setValue(maxI);
		iMaxField.setColumns(10);
		iMaxField.addPropertyChangeListener("value", this);
		
		// tell accessibility tools about label/field pairs
		rMinLabel.setLabelFor(rMinField);
		rMaxLabel.setLabelFor(rMaxField);
		iMinLabel.setLabelFor(iMinField);
		iMaxLabel.setLabelFor(iMaxField);
		
		// layout labels in a panel
		// labels for real
		JPanel rLabelPane = new JPanel(new GridLayout(0,1));
		rLabelPane.add(rMinLabel);
		rLabelPane.add(rMaxLabel);
		// labels for imaginary
		JPanel iLabelPane = new JPanel(new GridLayout(0,1));
		iLabelPane.add(iMinLabel);
		iLabelPane.add(iMaxLabel);
		
		// layout fields in a panel
		// fields for real
		JPanel rFieldPane = new JPanel(new GridLayout(0,1));
		rFieldPane.add(rMinField);
		rFieldPane.add(rMaxField);
		// fields for imaginary
		JPanel iFieldPane = new JPanel(new GridLayout(0,1));
		iFieldPane.add(iMinField);
		iFieldPane.add(iMaxField);
		
		// real and imaginary control panels
		JPanel realControlPane = new JPanel(new BorderLayout());
		TitledBorder titleReal = BorderFactory.createTitledBorder("Real");
		realControlPane.setBorder(titleReal);
		
		JPanel imaginaryControlPane = new JPanel(new BorderLayout());
		TitledBorder titleImaginary = BorderFactory.createTitledBorder("Imaginary");
		imaginaryControlPane.setBorder(titleImaginary);
		
		JPanel controlDisplayPane = new JPanel(new GridLayout(0,2));
		//JPanel controlRenderPane = new JPanel();
		JPanel controlIterPane = new JPanel();
		
		equalizeAxisButton = new JButton("Equalize Axis");
		equalizeAxisButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				explorerPanel.equalizeAxis();
			}
		});
		
		// iterations formatter
		iterValueField = new JFormattedTextField(controlFormat);
		iterValueField.setValue(iterations);
		iterValueField.setColumns(10);
		iterValueField.addPropertyChangeListener("value", this);
		
		controlIterPane.add(iterLabel);
		controlIterPane.add(iterValueField);
		controlIterPane.add(equalizeAxisButton);
		
		// add control panes to buttons pane
		controlDisplayPane.add(realControlPane);
		controlDisplayPane.add(imaginaryControlPane);
		
		buttonspanel.add(controlDisplayPane, BorderLayout.CENTER);
		buttonspanel.add(controlIterPane, BorderLayout.SOUTH);
		
		// add label/field panes to control panes
		realControlPane.add(rLabelPane, BorderLayout.CENTER);
		realControlPane.add(rFieldPane, BorderLayout.LINE_END);
		imaginaryControlPane.add(iLabelPane, BorderLayout.CENTER);
		imaginaryControlPane.add(iFieldPane, BorderLayout.LINE_END);
		
		this.setLocationRelativeTo(null); // center window
		setVisible(true);
	}

	/** Called when a field's "value" property changes. */
    public void propertyChange(PropertyChangeEvent e) {
        Object source = e.getSource();
        if (source == rMinField) {
            minR = ((Number)rMinField.getValue()).doubleValue();
        } else if (source == rMaxField) {
            maxR = ((Number)rMaxField.getValue()).doubleValue();
        } else if (source == iMinField) {
            minI = ((Number)iMinField.getValue()).doubleValue();
        } else if (source == iMaxField) {
            maxI = ((Number)iMaxField.getValue()).doubleValue();
        } else if (source == iterValueField) {
        	iterations = ((Number)iterValueField.getValue()).intValue();
        }
 
        // update fields and trigger repaint on the panel
        explorerPanel.updateValues(minR, maxR, minI, maxI, iterations);
 
    }
    
 // update fields
 	public void updateValues(Double minR, Double maxR, Double minI,
 			Double maxI, Integer iterations) {

 		this.minR = minR;
 		this.maxR = maxR;
 		this.minI = minI;
 		this.maxI = maxI;
 		this.iterations = iterations;
 		
 		matchFieldValues();
 	}
 	
 	public void matchFieldValues() {
 		rMinField.setValue(minR);
 		rMaxField.setValue(maxR);
 		iMinField.setValue(minI);
 		iMaxField.setValue(maxI);
 		
 		// iterations slider and field containing value
 		iterValueField.setValue(iterations);
 	}

	public Integer getIterations() { return iterations; }
	
	//public MandelbrotPanel getExplorer() {
	public FractalExplorerPanel getExplorer() {
		return explorerPanel;
	}
}
