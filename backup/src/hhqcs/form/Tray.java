/*
 * A program hozzásadása a tálcához.
 */
package hhqcs.form;

import hhqcs.form.comco.Comco;
import hhqcs.HHQCS;
import static hhqcs.HHQCS.LOGVIEWERISENABLED;
import static hhqcs.HHQCS.debug;
import static hhqcs.HHQCS.logViewer;
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
    @SuppressWarnings("Convert2Lambda")
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
            popup.addSeparator();
            if (HHQCS.LOGPANELISENABLED) {
                MenuItem logpanel = new MenuItem("event");
                logpanel.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (HHQCS.textAreaLog.isVisible()) {
                            HHQCS.textAreaLog.setVisible(false);
                        } else {
                            try {
                                HHQCS.textAreaLog.setVisible(true);
                            } catch (Exception ex) {
                                debug.printDebugMsg(null, this.getClass().getCanonicalName(),"(error)Hiba történt a log fájl megnyitásakor", ex);
                            }
                        }

                    }
                });
                popup.add(logpanel);
                popup.addSeparator();
            }
            if (LOGVIEWERISENABLED) {
                MenuItem logpanel = new MenuItem("logViewer");
                logpanel.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (logViewer.isVisible()) {
                            logViewer.setVisible(false);
                        } else {
                            try {
                                logViewer.setVisible(true);
                            } catch (Exception ex) {
                                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(warning)Hiba történt a logviewer megnyitásakor", ex);
                            }
                        }
                    }
                });
                popup.add(logpanel);
                popup.addSeparator();
            }

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
                popup.addSeparator();

            }

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

                    if (HHQCS.LOGPANELISENABLED) {
                        if (HHQCS.textAreaLog.isVisible()) {
                            HHQCS.textAreaLog.dispose();
                        } else {
                            HHQCS.textAreaLog.setState(JFrame.NORMAL);
                            HHQCS.textAreaLog.setVisible(true);
                        }
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
                debug.printDebugMsg(null, this.getClass().getCanonicalName(),"(error)Hiba történt a program tálcán futtatásakor", ex);
            }

        } else {
            System.out.println("System tray nem támogatott (" + Tray.class.getSimpleName() + ")");
            debug.printDebugMsg(null, this.getClass().getCanonicalName(),"(error)System tray nem támogatott");
            comco = new Comco();
            comco.setVisible(true);
        }
    }
}
