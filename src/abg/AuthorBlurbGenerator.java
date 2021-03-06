/*
 * Main class of AuthorBlurbGenerator
 * (c) 2015 by Kai Weber
 * published under the GNU GENERAL PUBLIC LICENSE
 * (see separate license document)
 */
package abg;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import abg.Utilities;

public class AuthorBlurbGenerator extends JFrame
{
	private static final long serialVersionUID = 1L;
	final Properties configuration = new Properties();
	private JTextArea textarea;
	
	public AuthorBlurbGenerator()
	{
		super("Author Blurb");
		
		// read configuration
		InputStream in = null;
		try
		{
			File jarPath=new File(AuthorBlurbGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	        String propertiesPath=jarPath.getParentFile().getAbsolutePath();
	        in = new FileInputStream(propertiesPath.replaceAll("%20", " ") + "/config/config.properties");
			configuration.load(in);
		}
		catch(IOException e)
		{
			Utilities.showErrorPane("Error when trying to open configuration file!", e);
		}
		finally
		{
			Utilities.safeClose(in);
		}
		
		// prepare text area
		textarea = new JTextArea();
		textarea.setLineWrap(true);
		textarea.setWrapStyleWord(true);
		Font font = new Font(Font.SERIF, Font.ROMAN_BASELINE, 18);
		textarea.setFont(font);
		this.add(textarea, BorderLayout.CENTER);
		
		// set window behaviour
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(800, 200);
		
		// add repeat button
		JButton repeat = new JButton("Noch einmal");
		repeat.addActionListener(new RepeatButtonListener(this));
		this.add(repeat, BorderLayout.SOUTH);
	}
	
	public static void main(String[] args)
	{
		AuthorBlurbGenerator abg = new AuthorBlurbGenerator();
		abg.fillWithText();
		abg.setVisible(true);
	}
	
	public void fillWithText()
	{
		StringBuffer sb = new StringBuffer();
		replaceVars(sb, configuration.getProperty("authorBlurbTemplate"));
		textarea.setText(sb.toString());
	}
	
	private void replaceVars(StringBuffer resultBuffer, String unprocessedText)
	{
		Random random = new Random();
		if(unprocessedText.contains("{$"))
		{
			int varStart = unprocessedText.indexOf("{$");
			int varEnd = unprocessedText.indexOf("}");
			
			resultBuffer.append(unprocessedText.substring(0, varStart));
			String variable = unprocessedText.substring(varStart + 2, varEnd);
			
			// take the variable value from the configuration file
			String variableContent = configuration.getProperty(variable);
			if(variableContent == null)
			{
				Utilities.showErrorPane("No variable values found for template variable {$" + variable + "}", new IllegalArgumentException());
			}
			String[] replacementOptions = variableContent.split(",");
			if(replacementOptions == null)
			{
				Utilities.showErrorPane("Error parsing template variable " + variable, new IllegalArgumentException());
			}
			else
			{
				String replacement = replacementOptions[random.nextInt(replacementOptions.length)];
				resultBuffer.append(replacement);	
			}
			replaceVars(resultBuffer, unprocessedText.substring(varEnd + 1));
		}
		else
		{
			resultBuffer.append(unprocessedText);
		}
	}
	
	class RepeatButtonListener implements ActionListener
	{
		private AuthorBlurbGenerator abg;
		
		RepeatButtonListener(AuthorBlurbGenerator abg)
		{
			this.abg = abg;
		}
		
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			abg.fillWithText();
			abg.repaint();
		}
	}
}