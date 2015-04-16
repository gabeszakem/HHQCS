package hhqcs.form;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import java.awt.Toolkit;

public class TextAreaLogProgram extends JFrame {

    /**
     * The text area which is used for displaying logging information.
     */
    private JTextArea textArea;

    @SuppressWarnings("FieldMayBeFinal")
    private JButton buttonClear = new JButton("Clear");

    private JLabel label = new JLabel("teszt");

    private PrintStream standardOut;

    public TextAreaLogProgram() {
        super("Esemény Napló");
        setIconImage(Toolkit.getDefaultToolkit().getImage(TextAreaLogProgram.class.getResource("/hhqcs/images/testimonials.png")));

        textArea = new JTextArea(50, 10);
        textArea.setEditable(false);

        label.setSize(50, 10);
        label.setEnabled(false);

        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea, label));

        // keeps reference of standard output stream
        standardOut = System.out;

        // re-assigns standard output stream and error output stream
        System.setOut(printStream);
        System.setErr(printStream);

        // creates the GUI
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 1;
        add(buttonClear, constraints);

        constraints.gridx = 2;
        add(label, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(new JScrollPane(textArea), constraints);

        // adds event handler for button Clear
        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //clears the text area

                try {
                    textArea.getDocument().remove(0,
                            textArea.getDocument().getLength());
                    standardOut.println("Text area cleared");
                    label.setText(Integer.toString(textArea.getDocument().getDefaultRootElement().getElementCount() - 1));
                } catch (BadLocationException ex) {
                    ex.printStackTrace(System.err);
                }

            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(480, 320);
        setExtendedState(TextAreaLogProgram.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);    // centers on screen
    }

    @Override

    public void dispose() {
        super.dispose();
    }

}
