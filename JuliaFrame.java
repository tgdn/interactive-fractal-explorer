import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class JuliaFrame extends JFrame {

	private FractalExplorerPanel explorerPanel;
	private JuliaFavouriteManager favouriteManager;
	private Complex z;
	
	private JPanel mainPane;
	private JPanel controlPanel;
	private JButton saveButton;
	private JLabel selectFavLabel;
	
	private JComboBox<Integer> favCombo;
	
	public JuliaFrame() throws Exception {
		super("Julia Set Fractal");
		setType(Window.Type.UTILITY);
		setSize(new Dimension(600, 600));
		setPreferredSize(new Dimension(600, 600));
		setLocationRelativeTo(null);
		
		favouriteManager = new JuliaFavouriteManager(this);
		favouriteManager.setup();
		
		mainPane = (JPanel) getContentPane();
		mainPane.setLayout(new BorderLayout());
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		
		saveButton = new JButton("Add to favourites");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				favouriteManager.add(z);
			}
		});
		
		selectFavLabel = new JLabel("Or select a favourite below");
		
		explorerPanel = new FractalExplorerPanel(null, 2);
		mainPane.add(explorerPanel, BorderLayout.CENTER);
		mainPane.add(controlPanel, BorderLayout.SOUTH);
		
		favCombo = favouriteManager.getCombo();
		
		controlPanel.add(saveButton);
		controlPanel.add(selectFavLabel);
		controlPanel.add(favCombo);
		
		
		// wait until everything has been done
		favouriteManager.close();
	}
	
	public void updatePoint(Complex z) {
		if (z == null)
			return;
		
		this.z = z;
		if (explorerPanel == null)
			return;
		explorerPanel.setUserSelectedPoint(z);
		explorerPanel.init();
	}
	
	public void updateMaxIter(int maxIter) {
		explorerPanel.setMaxIter(maxIter);
	}
}
