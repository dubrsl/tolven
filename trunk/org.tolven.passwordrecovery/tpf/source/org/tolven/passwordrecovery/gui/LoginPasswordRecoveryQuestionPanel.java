/*
 *
 * Created on August 19, 2007, 3:00 AM
 */

package org.tolven.passwordrecovery.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.tolven.passwordrecovery.api.SecurityQuestionsImpl;
import org.tolven.passwordrecovery.gui.PropertyEditorDialog;
import org.tolven.passwordrecovery.model.LoadSecurityQuestions;
import org.tolven.passwordrecovery.model.PasswordRecoveryQuestionInfo;

/**
 *
 * @author  Joseph Isaac
 */
public class LoginPasswordRecoveryQuestionPanel extends javax.swing.JPanel {

    private JFileChooser chooser;
    private List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos;
    private SecurityQuestionsImpl securityQuestionsImpl;
    private boolean startUpRefreshRequired = true;
    private JMenuItem addMenuItem = new JMenuItem("Add");
    private JMenuItem editMenuItem = new JMenuItem("Edit");
    private JMenuItem removeMenuItem = new JMenuItem("Remove");

    public LoginPasswordRecoveryQuestionPanel(SecurityQuestionsImpl securityQuestionsImpl) {
        initComponents();
        securityQuestionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        securityQuestionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateWidgets();
                }
            }

        });
        JPopupMenu popup = new JPopupMenu();
        addMenuItem = new JMenuItem("Add");
        addMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    add();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(getRootPane(), ex.getMessage());
                }
            }
        });
        popup.add(addMenuItem);
        editMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    edit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(getRootPane(), ex.getMessage());
                }
            }
        });
        popup.add(editMenuItem);
        removeMenuItem = new JMenuItem("Remove");
        removeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    remove();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(getRootPane(), ex.getMessage());
                }
            }
        });
        popup.add(removeMenuItem);
        securityQuestionsTable.setComponentPopupMenu(popup);
        TableColumn column = securityQuestionsTable.getColumnModel().getColumn(0);
        column.setCellRenderer(getTableCellRenderer());
        column = securityQuestionsTable.getColumnModel().getColumn(1);
        column.setCellRenderer(getTableCellRenderer());
        editButton.setEnabled(false);
        updateButton.setEnabled(false);
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        importButton.setEnabled(false);
        exportButton.setEnabled(false);
        addMenuItem.setEnabled(false);
        editMenuItem.setEnabled(false);
        removeMenuItem.setEnabled(false);
        setupFileChooser();
        passwordRecoveryQuestionInfos = new ArrayList<PasswordRecoveryQuestionInfo>();
        this.securityQuestionsImpl = securityQuestionsImpl;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(chooser);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            throw new RuntimeException("Could not set the Swing Look And Feel", ex);
        }
    }

    private TableCellRenderer getTableCellRenderer() {
        return new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                if (isSelected) {
                    label.setForeground(table.getSelectionForeground());
                    label.setBackground(table.getSelectionBackground());
                } else {
                    label.setForeground(table.getForeground());
                    label.setBackground(table.getBackground());
                }
                PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo = (PasswordRecoveryQuestionInfo) value;
                if (column == 0) {
                    label.setText(passwordRecoveryQuestionInfo.getQuestion());
                } else if (column == 1) {
                    label.setText(passwordRecoveryQuestionInfo.getDefaultStatusString());
                    label.setToolTipText(passwordRecoveryQuestionInfo.getDefaultStatusString());
                } else {
                    throw new RuntimeException("Unknown column found in " + LoginPasswordRecoveryQuestionPanel.class);
                }
                return label;
            }
        };
    }

    public void propertyValueEditingStopped(ChangeEvent e) {
        int selectedRow = securityQuestionsTable.getSelectedRow();
        PasswordRecoveryQuestionInfo selectedPasswordRecoveryQuestionInfo = passwordRecoveryQuestionInfos.get(selectedRow);
        if (selectedPasswordRecoveryQuestionInfo.getSecurityQuestion() == null) {
            JOptionPane.showMessageDialog(getRootPane(), "The selected item must be refreshed from the database before it can be edited");
            updateSecurityQuestionsTable();
            return;
        }
        DefaultCellEditor defaultCellEditor = (DefaultCellEditor) e.getSource();
        selectedPasswordRecoveryQuestionInfo.setValue((String) defaultCellEditor.getCellEditorValue());
    }

    private SecurityQuestionsImpl getSecurityQuestionsImpl() {
        return securityQuestionsImpl;
    }

    private void setupFileChooser() {
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".xml");
            }

            public String getDescription() {
                return "Security Questions XML File";
            }
        });
    }

    private void setupSecurityQuestionsTable() {
        DefaultTableModel propertiesTableModel = (DefaultTableModel) securityQuestionsTable.getModel();
        while (securityQuestionsTable.getRowCount() > 0) {
            propertiesTableModel.removeRow(0);
        }
        DefaultTableModel securityQuestionsTableModel = (DefaultTableModel) securityQuestionsTable.getModel();
        for (PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo : passwordRecoveryQuestionInfos) {
            securityQuestionsTableModel.addRow(new Object[] {
                    passwordRecoveryQuestionInfo,
                    passwordRecoveryQuestionInfo,
                    new Boolean(false) });
        }
    }

    private void updateSecurityQuestionsTable() {
        for (int i = 0; i < securityQuestionsTable.getRowCount(); i++) {
            securityQuestionsTable.setValueAt(passwordRecoveryQuestionInfos.get(i), i, 0);
            securityQuestionsTable.setValueAt(passwordRecoveryQuestionInfos.get(i), i, 1);
        }
    }

    private void refresh() {
        try {
            if (updateButton.isEnabled()) {
                int returnValue = JOptionPane.showConfirmDialog(getRootPane(), "You have changes pending. Reload anyway?", "Refresh", JOptionPane.YES_NO_OPTION);
                if (returnValue == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            passwordRecoveryQuestionInfos = getSecurityQuestionsImpl().getPasswordRecoveryQuestionInfos();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        setupSecurityQuestionsTable();
        startUpRefreshRequired = false;
        updateWidgets();
    }

    private void importSecurityQuestions() {
        if (updateButton.isEnabled()) {
            int returnValue = JOptionPane.showConfirmDialog(getRootPane(), "You have changes pending, which will be lost.\nProceed anyway?", "Import", JOptionPane.YES_NO_OPTION);
            if (returnValue == JOptionPane.NO_OPTION) {
                return;
            }
        }
        int returnVal = chooser.showOpenDialog(JOptionPane.getRootFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            passwordRecoveryQuestionInfos = getSecurityQuestionsImpl().importPasswordRecoveryQuestionInfos(chooser.getSelectedFile());
            setupSecurityQuestionsTable();
            updateButton.setEnabled(true);
            updateButton.requestFocus();
            updateWidgets();
        }
    }

    private void exportSecurityQuestions(File securityQuestionsFile) {
        getSecurityQuestionsImpl().exportPasswordRecoveryQuestionInfos(securityQuestionsFile);
    }

    public void add() {
        PropertyEditorDialog propertyEditorDialog = new PropertyEditorDialog(JOptionPane.getRootFrame(), "Add Security Question", "Add Security Question", false, null, true);
        propertyEditorDialog.setVisible(true);
        if (propertyEditorDialog.getStatus() == PropertyEditorDialog.CANCEL_STATUS) {
            return;
        }
        PasswordRecoveryQuestionInfo newPasswordRecoveryQuestionInfo = getSecurityQuestionsImpl().addPasswordRecoveryQuestionInfo(propertyEditorDialog.getPropertyValue());
        if (newPasswordRecoveryQuestionInfo != null) {
            passwordRecoveryQuestionInfos.add(newPasswordRecoveryQuestionInfo);
            setupSecurityQuestionsTable();
        }
        updateButton.setEnabled(true);
        updateButton.requestFocus();
        updateWidgets();
    }

    private void edit() {
        int selectedRow = securityQuestionsTable.getSelectedRow();
        PasswordRecoveryQuestionInfo selectedPasswordRecoveryQuestionInfo = passwordRecoveryQuestionInfos.get(selectedRow);
        if (selectedPasswordRecoveryQuestionInfo.getSecurityQuestion() == null) {
            JOptionPane.showMessageDialog(getRootPane(), "The selected item must be refreshed from the database before it can be edited");
            updateSecurityQuestionsTable();
            return;
        }
        PropertyEditorDialog propertyEditorDialog = new PropertyEditorDialog(JOptionPane.getRootFrame(), "Edit Security Question", "Edit Security Question", false, selectedPasswordRecoveryQuestionInfo.getQuestion(), true);
        propertyEditorDialog.setVisible(true);
        if (propertyEditorDialog.getStatus() == PropertyEditorDialog.CANCEL_STATUS) {
            return;
        }
        selectedPasswordRecoveryQuestionInfo.setValue(propertyEditorDialog.getPropertyValue());
        selectedPasswordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.UPDATEPENDING);
        updateSecurityQuestionsTable();
        updateButton.setEnabled(true);
        updateButton.requestFocus();
        updateWidgets();
    }

    private void remove() {
        int selectedRow = securityQuestionsTable.getSelectedRow();
        PasswordRecoveryQuestionInfo selectedPasswordRecoveryQuestionInfo = (PasswordRecoveryQuestionInfo) securityQuestionsTable.getValueAt(selectedRow, 0);
        selectedPasswordRecoveryQuestionInfo.setStatus(PasswordRecoveryQuestionInfo.REMOVEPENDING);
        updateSecurityQuestionsTable();
        updateWidgets();
        updateButton.setEnabled(true);
    }

    private void update() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            getSecurityQuestionsImpl().update(passwordRecoveryQuestionInfos);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        updateButton.setEnabled(false);
        refresh();
        updateWidgets();
    }

    public void updateWidgets() {
        addButton.setEnabled(!startUpRefreshRequired && passwordRecoveryQuestionInfos.size() > 0);
        addMenuItem.setEnabled(!startUpRefreshRequired && passwordRecoveryQuestionInfos.size() > 0);
        int selectedRow = securityQuestionsTable.getSelectedRow();
        editMenuItem.setEnabled(!startUpRefreshRequired && passwordRecoveryQuestionInfos.size() > 0 && selectedRow != -1);
        if (!startUpRefreshRequired && passwordRecoveryQuestionInfos.size() > 0 && selectedRow != -1) {
            PasswordRecoveryQuestionInfo selectedTolvenPropertyInfo = (PasswordRecoveryQuestionInfo) securityQuestionsTable.getValueAt(selectedRow, 0);
            editButton.setEnabled(selectedTolvenPropertyInfo.getStatus() != PasswordRecoveryQuestionInfo.REMOVEPENDING);
            editMenuItem.setEnabled(selectedTolvenPropertyInfo.getStatus() != PasswordRecoveryQuestionInfo.REMOVEPENDING);
            removeButton.setEnabled(selectedTolvenPropertyInfo.getStatus() != PasswordRecoveryQuestionInfo.REMOVEPENDING);
            removeMenuItem.setEnabled(selectedTolvenPropertyInfo.getStatus() != PasswordRecoveryQuestionInfo.REMOVEPENDING);
        } else {
            editButton.setEnabled(false);
            editMenuItem.setEnabled(false);
            removeButton.setEnabled(false);
            removeMenuItem.setEnabled(false);
        }
        importButton.setEnabled(true);
        exportButton.setEnabled(true && passwordRecoveryQuestionInfos.size() > 0);
    }

    private String getNestedMessage(Exception ex) {
        StringBuffer buff = new StringBuffer();
        Throwable throwable = ex;
        do {
            buff.append(throwable.getMessage() + "\n");
            throwable = throwable.getCause();
        } while (throwable != null);
        return buff.toString();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        connectionStringTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        securityQuestionsTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Connection String:");

        connectionStringTextField.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(connectionStringTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel7)
                .add(connectionStringTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel6.setText("Password Recovery Questions:");

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 540, Short.MAX_VALUE)
                .add(refreshButton))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel6)
                .add(refreshButton))
        );

        securityQuestionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Password Recovery Question", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        securityQuestionsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                securityQuestionsTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(securityQuestionsTable);

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        updateButton.setText("Update All Questions");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(addButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(exportButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 287, Short.MAX_VALUE)
                .add(updateButton))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {addButton, importButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(addButton)
                .add(editButton)
                .add(removeButton)
                .add(importButton)
                .add(exportButton)
                .add(updateButton))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(16, 16, 16))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            add();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        try {
            importSecurityQuestions();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
    }//GEN-LAST:event_importButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                exportSecurityQuestions(chooser.getSelectedFile());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
            }
        }
    }//GEN-LAST:event_exportButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        try {
            remove();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
}//GEN-LAST:event_removeButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        try {
            update();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        try {
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void securityQuestionsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_securityQuestionsTableMouseClicked
        try {
            if (evt.getClickCount() == 2 && securityQuestionsTable.getSelectedRow() != -1 && !startUpRefreshRequired && passwordRecoveryQuestionInfos.size() > 0) {
                edit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
    }//GEN-LAST:event_securityQuestionsTableMouseClicked

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        try {
            edit();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
    }//GEN-LAST:event_editButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField connectionStringTextField;
    private javax.swing.JButton editButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton importButton;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JTable securityQuestionsTable;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

}
