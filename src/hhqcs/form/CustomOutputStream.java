package hhqcs.form;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JLabel;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import hhqcs.HHQCS;
/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 *
 * @author www.codejava.net
 *
 */
public class CustomOutputStream extends OutputStream {

    @SuppressWarnings("FieldMayBeFinal")
    private JTextArea textArea;
    @SuppressWarnings("FieldMayBeFinal")
    private JLabel label;
    private int pointer = 0;
    private final int MAXLINE = 1500;
    private boolean correction = false;

    public CustomOutputStream(JTextArea textArea, JLabel label) {
        this.textArea = textArea;
        this.label=label;
    }

    @Override
    public void write(int b) throws IOException {

        if (b != -61 && b != -59) {
            if (!correction) {
                if (b < -64 && b > -320) {
                    b = 320 + b;
                }
            }else{
                if(b==-79){
                    b=251;
                }else if(b==-111){
                    b=245;
                }
                correction=false;
            }
            textArea.insert(String.valueOf((char) b), pointer);
            if (b != 10) {
                pointer++;
            } else {
                pointer = 0;
            }
        } else {
            if (b == -61) {
                correction = false;
            } else {
                correction = true;
            }
        }
        if (pointer
                == 0) {
            Element paragraph = textArea.getDocument().getDefaultRootElement();
            int contentCount = paragraph.getElementCount();
            if (MAXLINE < contentCount) {
                Element e = paragraph.getElement(MAXLINE);
                int rangeStart = e.getStartOffset();
                String line = "";
                int rangeEnd = textArea.getDocument().getLength();
                try {

                    textArea.getDocument().remove(rangeStart, rangeEnd - rangeStart);

                } catch (BadLocationException ex) {
                    HHQCS.debug.printDebugMsg(null, CustomOutputStream.class.getName(), "(error) text törlése nem sikerült...", ex);
                }
            }
            label.setText(Integer.toString(textArea.getDocument().getDefaultRootElement().getElementCount()-1));
        }
    }
}
