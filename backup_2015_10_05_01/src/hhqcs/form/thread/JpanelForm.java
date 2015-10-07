/*
 * Panelek létrehozása
 */
package hhqcs.form.thread;

import static hhqcs.HHQCS.debug;
import java.lang.Thread.State;

/**
 *
 * @author Gabesz
 */
public class JpanelForm extends javax.swing.JPanel {

    @SuppressWarnings("FieldMayBeFinal")
    private Thread thread;

    /**
     * Creates new form JpanelForm
     *
     * @param thread
     */
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public JpanelForm(Thread thread) {
        this.thread = thread;
        /*
         * A panelek ütemezet frissitése
         */
        Thread timer = new Thread("Thread Check Timer(" + this.thread.getName() + ")") {
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
            jLabel1.setText(this.thread.getName());
            State state=this.thread.getState();
            
                jTextField1.setText(state.toString());
                jTextField1.setForeground(new java.awt.Color(10, 102, 0));
          
           

        } catch (Exception ex) {
            System.out.println("A panel frissitése közben hibatörtént(" + JpanelForm.class.getSimpleName() + ")" + ex.getMessage());
            debug.printDebugMsg(null, this.getClass().getCanonicalName(),"(warning)A panel frissitése közben hibatörtént", ex);
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

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setBackground(new java.awt.Color(255, 228, 21));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        setFocusable(false);
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(600, 38));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setBackground(new java.awt.Color(0, 0, 0, .3f));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText(this.thread.getName());
        jLabel1.setFocusable(false);
        jLabel1.setRequestFocusEnabled(false);
        jLabel1.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jLabel1, gridBagConstraints);

        jTextField1.setBackground(new java.awt.Color(0, 0, 0, .3f));
        jTextField1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(0, 0, 255));
        jTextField1.setText("fault");
        jTextField1.setFocusable(false);
        jTextField1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jTextField1.setMaximumSize(new java.awt.Dimension(100, 24));
        jTextField1.setMinimumSize(new java.awt.Dimension(100, 24));
        jTextField1.setName(""); // NOI18N
        jTextField1.setPreferredSize(new java.awt.Dimension(120, 24));
        jTextField1.setRequestFocusEnabled(false);
        jTextField1.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(jTextField1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jTextField1;
    // End of variables declaration//GEN-END:variables
}
