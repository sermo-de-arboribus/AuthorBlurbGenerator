import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class AuthorBlurbGenerator
{
	final Properties configuration = new Properties();
	
	public AuthorBlurbGenerator() throws IOException
	{
		InputStream in = null;
		try
		{
			in = new FileInputStream("config/config.properties");
			configuration.load(in);
		}
		finally
		{
			in.close();
		}
	}
	
	public static final void main(String[]args) throws IOException
	{
		AuthorBlurbGenerator abg = new AuthorBlurbGenerator();
		System.out.println(abg.fillWithText());
	}
	
	private String fillWithText()
	{
		StringBuffer sb = new StringBuffer();
		replaceVars(sb, configuration.getProperty("authorBlurbTemplate"));
		return sb.toString();
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
			String[] replacementOptions = configuration.getProperty(variable).split(",");
			if(replacementOptions == null)
			{
				throw new IllegalArgumentException("Error creating text from template. Variable " + variable + " missing!");
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
}
