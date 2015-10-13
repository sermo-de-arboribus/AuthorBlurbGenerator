package abg;

import java.io.Closeable;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Utilities
{
	public static void safeClose(Closeable str)
	{
		try
		{
			str.close();
		}
	    catch (IOException e)
	    {
    		showErrorPane("Error: could not save file\n",e);
	    }
	}
	
	public static void showErrorPane(String errorMessage, Exception e)
	{
		JOptionPane.showMessageDialog(null, errorMessage + " " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
	}
}