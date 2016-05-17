/**
 * This class makes sure that only numbers are input to this JTextArea.
 * Last Edited: 05/01/2016
 * @author Melkis Espinal, Ian Jacobs, Ally Colisto, and Janine Jay
 */
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class CustomTextArea extends JTextArea{

	//constructor
	public CustomTextArea(){
		super();
	}
	//constructor
	public CustomTextArea(int cols){
		super();
	}

	@Override
	protected Document createDefaultModel(){
		return new UpperCaseDocument();
	}

	//Makes sure document returns only ints
	private static class UpperCaseDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException{

			if (str == null) {
				return;
			}

			char[] chars = str.toCharArray();
			boolean ok = true;

			for (int i=0;i<chars.length;i++){
				try{
					Integer.parseInt( String.valueOf(chars[i]));
				}catch (NumberFormatException exc){
					ok = false;
					break;
				}
			}
			if (ok) super.insertString( offs, new String( chars ), a );
		}
	}
}