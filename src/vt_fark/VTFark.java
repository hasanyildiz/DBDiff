package vt_fark;

import Connection.ColumnCompare;
import Connection.ConstraintCompare;
import Connection.IndexCompare;
import Connection.SourceConnection;
import Connection.TableCompare;
import Connection.TargetConnection;
import Connection.ViewCompare;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class VTFark extends javax.swing.JFrame {

    private Thread th;

    /**
     * Creates new form VTFark
     */
    public VTFark() {
        initComponents();
        java.awt.event.FocusAdapter focusAdapter = new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                javax.swing.JTextField tf = (javax.swing.JTextField) evt.getComponent();
                tf.selectAll();
            }
        };
        tfSourceID.addFocusListener(focusAdapter);
        tfSourcePort.addFocusListener(focusAdapter);
        tfSourceServiceName.addFocusListener(focusAdapter);
        tfTargetIP.addFocusListener(focusAdapter);
        tfTargetPort.addFocusListener(focusAdapter);
        tfTargetServiceName.addFocusListener(focusAdapter);
        jTextArea1.setEditable(menuDuzenlemeEtkin.isSelected());
        getFrameIcon();
        super.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        super.setLocationRelativeTo(null);
    }
    
    private void getFrameIcon() {
        Image i;
        try {
            i = ImageIO.read(getClass().getResource("icon.png"));
            setIconImage(i);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
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

        tfSourceID = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tfSourcePort = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfSourceServiceName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfTargetIP = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfTargetPort = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tfTargetServiceName = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        lbStatus = new javax.swing.JLabel();
        btnBaslat = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        menuFarkliKaydet = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        menuDuzenlemeEtkin = new javax.swing.JCheckBoxMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Veri Tabanı Karşılaştırması");
        setMaximumSize(new java.awt.Dimension(4000, 4000));
        setMinimumSize(new java.awt.Dimension(200, 200));
        setPreferredSize(new java.awt.Dimension(1024, 768));
        setSize(new java.awt.Dimension(10, 10));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        tfSourceID.setText("localhost");

        jLabel1.setLabelFor(tfSourceID);
        jLabel1.setText("Kaynak VT IP");

        tfSourcePort.setText("1521");

        jLabel2.setLabelFor(tfSourceID);
        jLabel2.setText("Kaynak VT Port");

        tfSourceServiceName.setText("Veritabanı");

        jLabel3.setLabelFor(tfSourceID);
        jLabel3.setText("Kaynak Servis İsmi:");

        tfTargetIP.setText("HEDEF IP");

        jLabel4.setLabelFor(tfSourceID);
        jLabel4.setText("Hedef VT IP:");

        tfTargetPort.setText("1521");

        jLabel5.setLabelFor(tfSourceID);
        jLabel5.setText("Hedef VT Port:");

        tfTargetServiceName.setText("HEDEF DB");

        jLabel11.setLabelFor(tfSourceID);
        jLabel11.setText("Hedef Servis İsmi:");

        jTextArea1.setColumns(100);
        jTextArea1.setAutoscrolls(false);
        jTextArea1.setDoubleBuffered(true);
        jTextArea1.setName("textArea"); // NOI18N
        jTextArea1.setVerifyInputWhenFocusTarget(false);
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextArea1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        lbStatus.setLabelFor(tfSourceID);
        lbStatus.setText("      ");

        btnBaslat.setText("Karşılaştırmayı Başlat");
        btnBaslat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaslatActionPerformed(evt);
            }
        });

        jMenu2.setMnemonic('D');
        jMenu2.setText("Dosya");

        menuFarkliKaydet.setMnemonic('F');
        menuFarkliKaydet.setText("Farklı Kaydet");
        menuFarkliKaydet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFarkliKaydetActionPerformed(evt);
            }
        });
        jMenu2.add(menuFarkliKaydet);

        jMenuBar1.add(jMenu2);

        jMenu1.setMnemonic('\u00fc');
        jMenu1.setText("Düzenle");

        menuDuzenlemeEtkin.setMnemonic('D');
        menuDuzenlemeEtkin.setText("Düzenlemeyi Etkinleştir");
        menuDuzenlemeEtkin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                menuDuzenlemeEtkinStateChanged(evt);
            }
        });
        jMenu1.add(menuDuzenlemeEtkin);

        jMenuItem1.setMnemonic('A');
        jMenuItem1.setText("Ara");
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfSourceServiceName, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfSourcePort, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfSourceID, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(33, 33, 33)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfTargetServiceName, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfTargetPort, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfTargetIP, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(86, 86, 86)
                        .addComponent(btnBaslat)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfTargetIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfTargetPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfTargetServiceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(btnBaslat)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfSourceID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfSourcePort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfSourceServiceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaslatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaslatActionPerformed
        if (th != null) {
            if (th.isAlive()) {
                JOptionPane.showMessageDialog(this, "İşlem zaten şuan gerçekleşiyor!", "İşlem vedam ediyor", JOptionPane.OK_OPTION);
                return;
            } else if (th.isInterrupted()) {
                JOptionPane.showMessageDialog(this, "İşlem zaten şuan gerçekleşiyor!", "İşlem vedam ediyor", JOptionPane.OK_OPTION);
                return;
            }
            menuDuzenlemeEtkin.setEnabled(false);
            menuFarkliKaydet.setEnabled(false);
            jTextArea1.setEditable(false);
            createThread();
        } else {
            menuDuzenlemeEtkin.setEnabled(false);
            menuFarkliKaydet.setEnabled(false);
            jTextArea1.setEditable(false);
            createThread();
        }
    }//GEN-LAST:event_btnBaslatActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (th != null) {
            if (th.isAlive()) {
                JOptionPane.showMessageDialog(this, "işlem gerçekleşirken kapatılamaz");
                return;
            }
        }
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void menuDuzenlemeEtkinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_menuDuzenlemeEtkinStateChanged
        jTextArea1.setEditable(menuDuzenlemeEtkin.isSelected());
    }//GEN-LAST:event_menuDuzenlemeEtkinStateChanged
private String getFileExtension(File file) {
    if (file == null) {
        return "";
    }
    String name = file.getName();
    int i = name.lastIndexOf('.');
    String ext = i > 0 ? name.substring(i + 1) : "";
    return ext;
}
    private void menuFarkliKaydetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFarkliKaydetActionPerformed
        System.out.println("action performed");
        JFileChooser fileChooser = null;
        List<String> extensions = new ArrayList<>();
        extensions.add("sql");
        extensions.add("txt");
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SQL & txt dosyası", extensions.get(0),extensions.get(1));
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            System.out.println("file path:"+fileChooser.getSelectedFile().getParent());
            if(extensions.contains(getFileExtension(fileChooser.getSelectedFile()))){
                try(Writer fos = new FileWriter(fileChooser.getSelectedFile())){
                    if(!fileChooser.getSelectedFile().exists()){
                        fileChooser.getSelectedFile().createNewFile();
                    }
                    jTextArea1.write(fos);
                    /*byte[] contentBytes = jTextArea1.getText().getBytes(Charset.forName("UTF-8"));
                    fos.write(contentBytes);
                    fos.flush();
                    fos.close();*/
                    JOptionPane.showMessageDialog(this, "Dosya kaydedildi");
                } catch (IOException ex) {
                    Logger.getLogger(VTFark.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("You chose to open this file: " +
            fileChooser.getSelectedFile().getName());
            }else{
                JOptionPane.showMessageDialog(this, "Geçersiz format");
            }
            
        }
    }//GEN-LAST:event_menuFarkliKaydetActionPerformed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        
    }//GEN-LAST:event_formKeyReleased

    private void jTextArea1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea1KeyPressed
        if ((evt.getKeyCode() == KeyEvent.VK_S) && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            
                    menuFarkliKaydetActionPerformed(null);
        }
    }//GEN-LAST:event_jTextArea1KeyPressed
    
    private void createThread() {
        final VTFark vTFarkForm = this;
        jTextArea1.requestFocus();
        th = new Thread(() -> {
            jTextArea1.setText("");
            try {
                SourceConnection.DBNAME = tfSourceServiceName.getText();
                SourceConnection.IP = tfSourceID.getText();
                SourceConnection.PORT = tfSourcePort.getText();
                PromptData form = new PromptData();
                form.lblTitle.setText("Kaynak VT Giriş Bilgileri");
                int resultSource = JOptionPane.showConfirmDialog(this, form.getComponents(), "My custom dialog", JOptionPane.OK_CANCEL_OPTION);
                if(resultSource != JOptionPane.OK_OPTION){
                    return;
                }
              
                SourceConnection.setUSERNAME(form.username);
                SourceConnection.setPASSWORD(form.password);
                
                TargetConnection.DBNAME = tfTargetServiceName.getText();
                TargetConnection.IP = tfTargetIP.getText();
                TargetConnection.PORT = tfTargetPort.getText();
                form = new PromptData();
                form.lblTitle.setText("Hedef VT Giriş Bilgileri");
                int resultTarget = JOptionPane.showConfirmDialog(this, form.getComponents(), "My custom dialog", JOptionPane.OK_CANCEL_OPTION);
                if(resultTarget != JOptionPane.OK_OPTION){
                    return;
                }
                TargetConnection.setUSERNAME(form.username);
                TargetConnection.setPASSWORD(form.password);
            
                try {
                    if (!SourceConnection.testConnection()) {
                        JOptionPane.showMessageDialog(vTFarkForm, "Kaynak veritabanına bağlantı sağlanamadı", "Bağlantı sağlanamadı", JOptionPane.OK_OPTION);
                        return;
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(vTFarkForm, ex.getLocalizedMessage(), "Bağlantı sağlanamadı", JOptionPane.OK_OPTION);
                    return;
                }
                lbStatus.setText("Kaynak veritabanı bağlantısı başarılı!");
                try {
                    if (!TargetConnection.testConnection()) {
                        JOptionPane.showMessageDialog(vTFarkForm, "Hedef veritabanına bağlantı sağlanamadı", "Bağlantı sağlanamadı", JOptionPane.OK_OPTION);
                        return;
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(vTFarkForm, ex.getLocalizedMessage(), "Bağlantı sağlanamadı", JOptionPane.OK_OPTION);
                    return;
                }

                lbStatus.setText("Hedef veritabanı bağlantısı başarılı!");
                TableCompare tc = new TableCompare();
                lbStatus.setText("Tablolar karşılaştırılıyor...");
                for (String st : tc.compareTables()) {
                    jTextArea1.append(st + System.lineSeparator());
                }

                for (String st : tc.getExistingTables()) {
                    boolean found = false;
                    //jTextArea1.append("/**************start of " + st + "*****************/" + System.lineSeparator());
                    lbStatus.setText(st + " tablosunun sütunları karşılaştırılıyor...");
                    ColumnCompare colComp = new ColumnCompare(st);
                    List<String> columnSqlList = colComp.compareColumns();
                    if (columnSqlList.size() > 0) {
                        for (String colSql : columnSqlList) {
                            jTextArea1.append(colSql + System.lineSeparator());
                            if (!found) {
                                found = true;
                            }
                        }
                    }
                    //jTextArea1.append("/**************end of " + st + "*****************/" + System.lineSeparator());
                    if (found) {
                        jTextArea1.append(System.lineSeparator());
                    }

                }

                lbStatus.setText("İndexler karşılattırılıyor...");
                IndexCompare inCompare = new IndexCompare();
                jTextArea1.append("/**************Target eksik indexler******************/" + System.lineSeparator());
                for (String str : inCompare.getReverseMissingIndexes()) {
                    lbStatus.setText(str + " index karşılaştırılıyor...");
                    jTextArea1.append(inCompare.compareIndexReverse(str) + System.lineSeparator());
                }
                jTextArea1.append("/**************Source eksik indexler******************/" + System.lineSeparator());
                for (String str : inCompare.getMissingIndexes()) {
                    lbStatus.setText(str + " index karşılaştırılıyor...");
                    System.out.println("missing index:" + str);
                    jTextArea1.append(inCompare.compareMissingIndexes(str) + System.lineSeparator());
                }
                List<String> resultList;
                jTextArea1.append("/**************Source index karşılaştırması******************/" + System.lineSeparator());
                for (String str : inCompare.getExistingIndexes()) {
                    lbStatus.setText(str + " index karşılaştırılıyor...");
                    resultList = inCompare.compareExistingIndex(str);
                    for (String res : resultList) {
                        jTextArea1.append(res + System.lineSeparator());
                    }
                }

                lbStatus.setText("Constraintler karşılaştırılıyor...");
                ConstraintCompare constCompare = new ConstraintCompare();
                for (String st : constCompare.compareConstraints()) {
                    jTextArea1.append(st + System.lineSeparator());
                }

                ViewCompare viewCompare = new ViewCompare();
                for (String st : viewCompare.compareViews()) {
                    jTextArea1.append(System.lineSeparator());
                    jTextArea1.append(st + System.lineSeparator());
                    jTextArea1.append(System.lineSeparator());
                }

                jTextArea1.append("EXEC DBMS_UTILITY.compile_schema(schema => '" + TargetConnection.getSchemaName() + "');");
                lbStatus.setText("İşlem tamamlandı!");
            } catch (SQLException | ClassNotFoundException | IOException  ex) {
                JOptionPane.showMessageDialog(vTFarkForm, ex.getLocalizedMessage(), "Bir hata oluştu.", JOptionPane.OK_OPTION);
            } finally {
                try {
                    SourceConnection.getConnection().close();
                } catch (SQLException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(vTFarkForm, ex.getLocalizedMessage(), "Bir hata oluştu.", JOptionPane.OK_OPTION);
                }
                try {
                    TargetConnection.getConnection().close();
                } catch (SQLException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(vTFarkForm, ex.getLocalizedMessage(), "Bir hata oluştu.", JOptionPane.OK_OPTION);
                }
                menuDuzenlemeEtkin.setEnabled(true);
            menuFarkliKaydet.setEnabled(true);
            jTextArea1.setEditable(menuDuzenlemeEtkin.isSelected());
            }
        });
        th.start();
    }

    /**
     * @param args the command line arguments
     */
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new VTFark().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaslat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JCheckBoxMenuItem menuDuzenlemeEtkin;
    private javax.swing.JMenuItem menuFarkliKaydet;
    private javax.swing.JTextField tfSourceID;
    private javax.swing.JTextField tfSourcePort;
    private javax.swing.JTextField tfSourceServiceName;
    private javax.swing.JTextField tfTargetIP;
    private javax.swing.JTextField tfTargetPort;
    private javax.swing.JTextField tfTargetServiceName;
    // End of variables declaration//GEN-END:variables
}
