/**
 * CS576 Multimedia Systems Design
 * Image Based Searching
 * @author bijani@usc.edu
 * @author hmehra@usc.edu
 * 	 
 **/

import java.io.File;
import javax.swing.JFileChooser;


public class openFile {
	
	JFileChooser fc = new JFileChooser("/home/hmehra/Dropbox/Final Project/colorApproach/Images/");
	
	public String getFilePath() throws Exception {
		if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			System.out.println(file.getAbsolutePath());
			return file.getAbsolutePath();
		} else {
			return "False";
		}		 
	}	
}
