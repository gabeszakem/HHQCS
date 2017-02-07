/*
 * Panelek létrehozása
 */
package hhqcs.form.comco;

import static hhqcs.HHQCS.debug;
import hhqcs.HHQCSServer;
import java.awt.Color;

/**
 *
 * @author Gabesz
 */
public class JpanelForm extends javax.swing.JPanel {

    @SuppressWarnings("FieldMayBeFinal")
    private HHQCSServer server;

    /**
     * Creates new form JpanelForm
     *
     * @param server HHQCSServer
     */
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public JpanelForm(HHQCSServer server) {
        this.server = server;
        /*
         * A panelek ütemezet frissitése
         */

        Thread timer = new Thread("Machine Check Timer(" + this.server.setup.PLANTNAME + ")") {
            @Override
            @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
            public void run() {
                while (true) {
                    try {
                        synchronized (this) {
                            wait(100);
                            refreshPanel();
                        }
                    } catch (Exception ex) {
                        System.out.println("A panel frissitése közben hibatörtént(" + JpanelForm.class.getSimpleName() + ")" + ex.getMessage());
                        debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)A panel frissitése közben hibatörtént", ex);
                    }
                }
            }
        };
        timer.start();
        //HHQCS.threads.add(timer);
        initComponents();
    }

    private void refreshPanel() {
        try {

            long resetDataTime = (this.server.tcpData.tcp.CLIENTRESTARTTIME - System.currentTimeMillis() + this.server.tcpData.tcp.lastMessageTime) / 1000;
            long resetThicknessTime = (this.server.tcpThickness.tcp.CLIENTRESTARTTIME - System.currentTimeMillis() + this.server.tcpThickness.tcp.lastMessageTime) / 1000;
            long elapseDataTime = (System.currentTimeMillis() - this.server.tcpData.tcp.lastMessageTime) / 1000;
            long elapseThicknessTime = (System.currentTimeMillis() - this.server.tcpThickness.tcp.lastMessageTime) / 1000;

            if (this.server.setup.setupDataMessageEnable == true) {
                long resetSetupTime = (this.server.tcpThickness.tcp.CLIENTRESTARTTIME - System.currentTimeMillis() + this.server.tcpThickness.tcp.lastMessageTime) / 1000;
               // long elapseSetupTime = (System.currentTimeMillis() - this.server.tcpThickness.tcp.lastMessageTime) / 1000;

                if ((this.server.tcpSetup.tcp.CLIENTRESTARTTIME / 1000 - resetSetupTime) < 60) {
                    counterSAPQuery.setForeground(new java.awt.Color(10, 102, 0));
                    jButtonSetupReset.setEnabled(false);
                } else {
                    counterSAPQuery.setForeground(Color.RED);
                    jButtonSetupReset.setEnabled(true);
                }
            }else{
                jButtonSetupReset.setEnabled(false);
            }

            machineName.setText(this.server.setup.PLANTNAME);
            liveSignal.setText(Integer.toString(this.server.tcpLife.ls.count));
            dataLastMessageTime.setText(Long.toString(elapseDataTime));
            thicknessLastMessageTime.setText(Long.toString(elapseThicknessTime));
            sapLastSentCoilId.setText(this.server.sapR3SetupData.sapAlapanyagAzonosito);
            String tooltiptext = "<html><p width=\"300\">"
                    + "Tekercsszám: " + this.server.sapR3SetupData.sapAlapanyagAzonosito + "<br>"
                    + "Alapanyag vastagság: " + this.server.sapR3SetupData.alapanyagVastagsag + " [um]" + "<br>"
                    + "Szerződött vastagság: " + this.server.sapR3SetupData.szerzodottVastagsag + " [um]" + "<br>"
                    + "Negatív vastagság tűrés: " + this.server.sapR3SetupData.szerzodottVastagsagTuresMinimum + " [um]" + "<br>"
                    + "Pozitív vastagság tűrés: " + this.server.sapR3SetupData.szerzodottVastagsagTuresPlusz + " [um]" + "<br>"
                    + "Alapanyag szélesség: " + this.server.sapR3SetupData.alapanyagSzelesseg + " [mm]" + "<br>"
                    + "Szerződött szélesség: " + this.server.sapR3SetupData.szerzodottSzelesseg + " [mm]" + " <br>"
                    + "Alapanyag súly: " + this.server.sapR3SetupData.alapanyagSuly
                    + "</p></html>";
            sapLastSentCoilId.setToolTipText(tooltiptext);
            counterSAPQuery.setText(Integer.toString(this.server.count));

            if (this.server.tcpLife.hhqcsServer.centralografMessage.plantStatus == 0) {
                liveSignal.setForeground(Color.BLUE);
            } else if (this.server.tcpLife.hhqcsServer.centralografMessage.plantStatus == 1) {
                liveSignal.setForeground(new java.awt.Color(10, 102, 0));
            } else {
                liveSignal.setForeground(Color.RED);
            }

            if ((this.server.tcpData.tcp.CLIENTRESTARTTIME / 1000 - resetDataTime) < 60) {
                dataLastMessageTime.setForeground(new java.awt.Color(10, 102, 0));
                jButtonDataReset.setEnabled(false);
            } else {
                dataLastMessageTime.setForeground(Color.RED);
                jButtonDataReset.setEnabled(true);
            }

            if ((this.server.tcpThickness.tcp.CLIENTRESTARTTIME / 1000 - resetThicknessTime) < 60) {
                thicknessLastMessageTime.setForeground(new java.awt.Color(10, 102, 0));
                jButtonThicknessReset.setEnabled(false);
            } else {
                thicknessLastMessageTime.setForeground(Color.RED);
                jButtonThicknessReset.setEnabled(true);
            }

            if (this.server.sentCoilIdentification.equals(this.server.sapR3SetupData.sapAlapanyagAzonosito)) {
                sapLastSentCoilId.setForeground(new java.awt.Color(10, 102, 0));
            } else {
                sapLastSentCoilId.setForeground(Color.red);
            }

        } catch (Exception ex) {
            System.out.println("A panel frissitése közben hibatörtént(" + JpanelForm.class.getSimpleName() + ")" + ex.getMessage());
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)A panel frissitése közben hibatörtént", ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        machineName = new javax.swing.JLabel();
        liveSignal = new javax.swing.JLabel();
        jButtonLifeSignalReset = new javax.swing.JButton();
        dataLastMessageTime = new javax.swing.JLabel();
        jButtonDataReset = new javax.swing.JButton();
        thicknessLastMessageTime = new javax.swing.JLabel();
        jButtonThicknessReset = new javax.swing.JButton();
        sapLastSentCoilId = new javax.swing.JLabel();
        counterSAPQuery = new javax.swing.JLabel();
        jButtonSetupReset = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 228, 21));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 204, 204), 2, true));
        setFocusable(false);
        setMinimumSize(new java.awt.Dimension(950, 38));
        setName(""); // NOI18N
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(950, 38));
        setLayout(new java.awt.GridBagLayout());

        machineName.setBackground(new java.awt.Color(0, 0, 0, .3f));
        machineName.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        machineName.setForeground(new java.awt.Color(0, 0, 255));
        machineName.setText(this.server.setup.PLANTNAME);
        machineName.setFocusable(false);
        machineName.setMaximumSize(new java.awt.Dimension(160, 22));
        machineName.setMinimumSize(new java.awt.Dimension(160, 22));
        machineName.setPreferredSize(new java.awt.Dimension(160, 22));
        machineName.setRequestFocusEnabled(false);
        machineName.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(machineName, gridBagConstraints);

        liveSignal.setBackground(new java.awt.Color(0, 0, 0, .3f));
        liveSignal.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        liveSignal.setForeground(new java.awt.Color(0, 0, 255));
        liveSignal.setText("count");
        liveSignal.setFocusable(false);
        liveSignal.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        liveSignal.setMaximumSize(new java.awt.Dimension(60, 24));
        liveSignal.setMinimumSize(new java.awt.Dimension(60, 24));
        liveSignal.setPreferredSize(new java.awt.Dimension(60, 24));
        liveSignal.setRequestFocusEnabled(false);
        liveSignal.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(liveSignal, gridBagConstraints);

        jButtonLifeSignalReset.setText("reset");
        jButtonLifeSignalReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLifeSignalResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jButtonLifeSignalReset, gridBagConstraints);

        dataLastMessageTime.setBackground(new java.awt.Color(0, 0, 0, .3f));
        dataLastMessageTime.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        dataLastMessageTime.setForeground(new java.awt.Color(0, 0, 255));
        dataLastMessageTime.setText("timeout");
        dataLastMessageTime.setFocusable(false);
        dataLastMessageTime.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        dataLastMessageTime.setMaximumSize(new java.awt.Dimension(60, 24));
        dataLastMessageTime.setMinimumSize(new java.awt.Dimension(60, 24));
        dataLastMessageTime.setPreferredSize(new java.awt.Dimension(60, 24));
        dataLastMessageTime.setRequestFocusEnabled(false);
        dataLastMessageTime.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(dataLastMessageTime, gridBagConstraints);

        jButtonDataReset.setText("reset");
        jButtonDataReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDataResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jButtonDataReset, gridBagConstraints);

        thicknessLastMessageTime.setBackground(new java.awt.Color(0, 0, 0, .3f));
        thicknessLastMessageTime.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        thicknessLastMessageTime.setForeground(new java.awt.Color(0, 0, 255));
        thicknessLastMessageTime.setText("timeout");
        thicknessLastMessageTime.setFocusable(false);
        thicknessLastMessageTime.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        thicknessLastMessageTime.setMaximumSize(new java.awt.Dimension(60, 24));
        thicknessLastMessageTime.setMinimumSize(new java.awt.Dimension(60, 24));
        thicknessLastMessageTime.setPreferredSize(new java.awt.Dimension(60, 24));
        thicknessLastMessageTime.setRequestFocusEnabled(false);
        thicknessLastMessageTime.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(thicknessLastMessageTime, gridBagConstraints);

        jButtonThicknessReset.setText("reset");
        jButtonThicknessReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonThicknessResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        add(jButtonThicknessReset, gridBagConstraints);

        sapLastSentCoilId.setBackground(new java.awt.Color(0, 0, 0, .3f));
        sapLastSentCoilId.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        sapLastSentCoilId.setForeground(new java.awt.Color(0, 0, 255));
        sapLastSentCoilId.setText("timeout");
        sapLastSentCoilId.setFocusable(false);
        sapLastSentCoilId.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        sapLastSentCoilId.setMaximumSize(new java.awt.Dimension(150, 24));
        sapLastSentCoilId.setMinimumSize(new java.awt.Dimension(150, 24));
        sapLastSentCoilId.setName(""); // NOI18N
        sapLastSentCoilId.setPreferredSize(new java.awt.Dimension(150, 24));
        sapLastSentCoilId.setRequestFocusEnabled(false);
        sapLastSentCoilId.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(sapLastSentCoilId, gridBagConstraints);

        counterSAPQuery.setBackground(new java.awt.Color(0, 0, 0, .3f));
        counterSAPQuery.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        counterSAPQuery.setForeground(new java.awt.Color(0, 153, 0));
        counterSAPQuery.setText("timeout");
        counterSAPQuery.setFocusable(false);
        counterSAPQuery.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        counterSAPQuery.setMaximumSize(new java.awt.Dimension(60, 24));
        counterSAPQuery.setMinimumSize(new java.awt.Dimension(60, 24));
        counterSAPQuery.setPreferredSize(new java.awt.Dimension(60, 24));
        counterSAPQuery.setRequestFocusEnabled(false);
        counterSAPQuery.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(counterSAPQuery, gridBagConstraints);

        jButtonSetupReset.setText("reset");
        jButtonSetupReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetupResetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        add(jButtonSetupReset, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonLifeSignalResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLifeSignalResetActionPerformed
        this.server.tcpLife.tcp.restart = true;
    }//GEN-LAST:event_jButtonLifeSignalResetActionPerformed

    private void jButtonDataResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDataResetActionPerformed
        this.server.tcpData.tcp.restart = true;
    }//GEN-LAST:event_jButtonDataResetActionPerformed

    private void jButtonThicknessResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonThicknessResetActionPerformed
        this.server.tcpThickness.tcp.restart = true;
    }//GEN-LAST:event_jButtonThicknessResetActionPerformed

    private void jButtonSetupResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetupResetActionPerformed
        if (this.server.setup.setupDataMessageEnable == true) {
            this.server.tcpSetup.tcp.restart = true;
        }
    }//GEN-LAST:event_jButtonSetupResetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel counterSAPQuery;
    private javax.swing.JLabel dataLastMessageTime;
    private javax.swing.JButton jButtonDataReset;
    private javax.swing.JButton jButtonLifeSignalReset;
    private javax.swing.JButton jButtonSetupReset;
    private javax.swing.JButton jButtonThicknessReset;
    private javax.swing.JLabel liveSignal;
    private javax.swing.JLabel machineName;
    private javax.swing.JLabel sapLastSentCoilId;
    private javax.swing.JLabel thicknessLastMessageTime;
    // End of variables declaration//GEN-END:variables
}
