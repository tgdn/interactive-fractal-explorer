import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import javax.swing.JComboBox;


public class JuliaFavouriteManager {

	private JuliaFrame juliaFrame;
	
	private String 			filename = "julia_favourites.txt";
	private BufferedReader 	bf;
	
	private Vector<Complex> favourites;
	private Vector<Integer> favouritesNames;
	
	private JComboBox<Integer> favCombo;
	
	public JuliaFavouriteManager(JuliaFrame f) throws Exception {
		
		juliaFrame = f;
		favourites = new Vector<Complex>();
		favouritesNames = new Vector<Integer>();
		
		
		// check that file exists or create one
		File file = new File(filename);
		file.createNewFile();
		
		try {
			bf = new BufferedReader( new FileReader(file) );
		} catch(FileNotFoundException e) {}
		
		
	}
	
	public void close() throws IOException {
		bf.close();
	}
	
	public void add(Complex z) {
		
		// file should exist
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
		    out.println(z.getRe() + ":" + z.getIm());
		}catch (IOException e) {
			return;
		}
		
		Integer name;
		
		favourites.add(z);
		
		if (favouritesNames.isEmpty())
			name = 1;
		else
			name = favouritesNames.lastElement().intValue() + 1;
		
		// adding the element here also adds it to combo box
	    favouritesNames.add(name);
	}
	
	public void setup() throws Exception {
		String line = null;
		int i = 1;
		
		// read until end of file
		while ( (line = bf.readLine()) != null ) {
			Double x, y;
			
			// each line should be such as:
			// x:y
			
			try {
				x = Double.parseDouble(line.split(":")[0]);
				y = Double.parseDouble(line.split(":")[1]);
			} catch (IndexOutOfBoundsException e) {
				throw new Exception("Favourites file is of incorrect format");
			}
			
			// add favourite to array list
			try {
			favourites.add(new Complex(x,y));
			favouritesNames.add(i);
			} catch (Exception err) {
				// if the line is of incorrect format fail silently and continue
				i -= 1;
			}
			i++;
		}

		favCombo = new JComboBox<Integer>(favouritesNames);
		favCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<Integer> cb = (JComboBox<Integer>)e.getSource();
				Integer elt = (Integer)cb.getSelectedItem();
				Complex z = null;
				
				try {
					z = favourites.get(elt.intValue() - 1);
				}catch (IndexOutOfBoundsException err) {
					System.err.println("Error trying to match favourite in selection box");
					return;
				}
				// finally update display
				juliaFrame.updatePoint(z);
			}
		});
	}
	
	public JComboBox<Integer> getCombo() {
		return favCombo;
	}
	
}
