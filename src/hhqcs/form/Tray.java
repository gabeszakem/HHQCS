/*
 * A program hozzásadása a tálcához.
 */
package hhqcs.form;

import hhqcs.form.comco.Comco;
import hhqcs.HHQCS;
import hhqcs.form.thread.ThreadFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

/**
 *
 * @author Gabesz
 */
public class Tray {

    private static final boolean THREADISENABLE = false;

    /**
     *
     * @throws Exception
     */
    public Tray() throws Exception {
        final TrayIcon trayIcon;
        SystemTray tray;
        final Image image;
        final Comco comco;
        final ThreadFrame threadFrame;
        ActionListener actionListener;

        /*
         * Ellenőrzés, tray icon támogatott?
         */
        if (SystemTray.isSupported()) {

            comco = new Comco();
            comco.setVisible(false);
            threadFrame = new ThreadFrame();
            tray = SystemTray.getSystemTray();
            image = Toolkit.getDefaultToolkit().getImage(Tray.class.getResource("/hhqcs/images/testimonials.png"));

            MouseListener mouseListener = new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            };

            ActionListener showListener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };

            PopupMenu popup = new PopupMenu();

            MenuItem logItem = new MenuItem("comco");
            logItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (comco.isVisible()) {
                        comco.setVisible(false);
                    } else {
                        comco.setVisible(true);
                    }

                }
            });
            popup.add(logItem);
            if (THREADISENABLE) {
                MenuItem threadItem = new MenuItem("threads");
                threadItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (threadFrame.isVisible()) {
                            threadFrame.setVisible(false);
                        } else {
                            threadFrame.setVisible(true);
                        }

                    }
                });

                popup.add(threadItem);

            }
            popup.addSeparator();

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(-1);
                }
            });
            popup.add(exitItem);
            final PopupMenu myPopup = popup;
            trayIcon = new TrayIcon(image, "", myPopup);
            actionListener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    /*
                     * trayIcon.displayMessage("Action Event", "",
                     * TrayIcon.MessageType.INFO);
                     */

                    if (comco.isVisible()) {
                        comco.dispose();
                    } else {
                        comco.setState(JFrame.NORMAL);
                        comco.setVisible(true);
                    }
                }
            };
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("hhqcs");
            trayIcon.addActionListener(actionListener);
            trayIcon.addMouseListener(mouseListener);
            try {
                tray.add(trayIcon);
            } catch (AWTException ex) {
                System.out.println("Hiba történt a program tálcán futtatásakor (" + Tray.class.getSimpleName() + ")" + ex.getMessage());
                HHQCS.debug.printDebugMsg(null, Tray.class.getName(),
                        "Hiba történt a program tálcán futtatásakor", ex);
            }

        } else {
            System.out.println("System tray nem támogatott (" + Tray.class.getSimpleName() + ")");
            HHQCS.debug.printDebugMsg(null, Tray.class.getName(),
                    "System tray nem támogatott");
            comco = new Comco();
            comco.setVisible(true);
        }
    }
}
