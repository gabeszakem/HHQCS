/*
 * JPanel származtatása, háttérképpel
 */
package hhqcs.form;

import hhqcs.form.comco.Comco;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Gabesz
 */
public class BackgroundPanel extends JPanel {

    /* A hátérképet tároljuk az img-ben */
    Image img;

    /**
     * háttérkép
     */
    public BackgroundPanel() {
        // Háttérkép betöltése
        img = Toolkit.getDefaultToolkit().getImage(Comco.class.getResource("/hhqcs/images/bg_image.png"));
        setBorder(BorderFactory.createLineBorder(Color.orange, 1));
    }

    @Override
    public void paintComponent(Graphics g) {
        /* újrarajzolás kép átméretezésével */
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    }
}