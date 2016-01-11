/*
 * Form a kommunikációs üzenetekhez
 * 
 */
package hhqcs.form.comco;

import form.LogViewer;
import hhqcs.HHQCSServer;
import hhqcs.form.BackgroundPanel;
import hhqcs.setup.SetupDataManager;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Gabesz
 */
public class Comco extends JFrame {
    
    
    @SuppressWarnings("Convert2Lambda")
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LogViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LogViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LogViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LogViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               // new LogViewer().setVisible(true);
            }
        });
    }
    

    /**
     * A keret szélessége
     */
    private int frameWidth;
    /**
     * A keret magassága
     */
    private int frameHeight;
    /**
     * A keret felirata
     */
    private final String TITLE = "Comco";
    /**
     * A frame keretének a mérete
     */
    private final int FRAMEDECORSIZE = 0;
    /**
     * A frame szélessége
     */
    private final int FRAMEWIDTH = 660;
    /**
     * A panel magassága
     */
    private final int PANELHEIGHT = 45;
    /*
     * Egér helyzete
     */
    Point point = new Point();

    /**
     * Konstruktor
     */
    public Comco() {
        /*
         * inicializálás
         */
        initComponents();
        /*
         * egér figyelése
         */
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (!e.isMetaDown()) {
                    point.x = e.getX();
                    point.y = e.getY();
                }
            }
        });
        /*
         * egér mozgásának figyelése
         */
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!e.isMetaDown()) {
                    Point p = getLocation();
                    setLocation(p.x + e.getX() - point.x,
                            p.y + e.getY() - point.y);
                }
            }
        });
    }

    private void setPosition() {
        /*
         * Az ablak beállítása középre
         */
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        this.setLocation((screenWidth - frameWidth) / 2, ((screenHeight - frameHeight) / 2));
    }

    private void initComponents() {
        /*
         * Icon beállítása
         */
        setIconImage(Toolkit.getDefaultToolkit().getImage(Comco.class.getResource("/hhqcs/images/testimonials.png")));
        /*
         * Fő panel beállítása
         */
        BackgroundPanel mainPanel = new BackgroundPanel();
        /*
         * Frame beállítása
         */
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        /*
         * Az ablak nem átméretezhető
         */
        setResizable(false);

        setTitle(TITLE);
        this.frameWidth = FRAMEWIDTH;
        this.frameHeight = FRAMEDECORSIZE + (SetupDataManager.hhqcsServers.size() * PANELHEIGHT);
        setSize(frameWidth, frameHeight);
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        /*
         * Frame pozicionálása az asztal közepére
         */
        setPosition();
        /*
         * keret elhagyása
         */
        setUndecorated(true);

        /*
         * Serverek paneleinek a beállítása
         */
        for (HHQCSServer server : SetupDataManager.hhqcsServers) {
            JPanel jPanel = new JpanelForm(server);
            mainPanel.add(jPanel);
        }

        add(mainPanel);
        pack();
    }

    /**
     *
     */
    @Override
    public void dispose() {
        super.dispose();
    }
}
