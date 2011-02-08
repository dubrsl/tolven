/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */

package org.tolven.appserverproperties.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.tolven.appserverproperties.api.AppServerProperties;
import org.tolven.appserverproperties.model.TolvenPropertyInfo;

/**
 *
 * @author  Joseph Isaac
 */
public class PropertiesPanel extends javax.swing.JPanel {

    public static final int UPTODATE = 0;
    public static final int UPDATEPENDING = 1;
    public static final int REMOVEPENDING = 2;

    public static final String UPDATE_COMMAND = "Update";
    public static final String REMOVE_COMMAND = "Remove";

    private JFileChooser chooser;
    private List<TolvenPropertyInfo> tolvenPropertyInfos;
    private AppServerProperties appServerProperties;
    private boolean startUpRefreshRequired = true;
    private JMenuItem addMenuItem = new JMenuItem("Add");
    private JMenuItem editMenuItem = new JMenuItem("Edit");
    private JMenuItem removeMenuItem = new JMenuItem("Remove");
    
    private boolean changesPending;

    public PropertiesPanel(AppServerProperties appServerProperties) {
        initComponents();
        propertiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propertiesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
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
                    JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
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
                    JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
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
                    JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
                }
            }
        });
        popup.add(removeMenuItem);
        propertiesTable.setComponentPopupMenu(popup);
        TableColumn column = propertiesTable.getColumnModel().getColumn(0);
        column.setCellRenderer(getTableCellRenderer());
        column = propertiesTable.getColumnModel().getColumn(1);
        column.setCellRenderer(getTableCellRenderer());
        column = propertiesTable.getColumnModel().getColumn(2);
        column.setCellRenderer(getTableCellRenderer());
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        editButton.setEnabled(false);
        importButton.setEnabled(false);
        exportButton.setEnabled(false);
        updateButton.setEnabled(false);
        changesPending = false;
        addMenuItem.setEnabled(false);
        editMenuItem.setEnabled(false);
        removeMenuItem.setEnabled(false);
        setupFileChooser();
        tolvenPropertyInfos = new ArrayList<TolvenPropertyInfo>();
        this.appServerProperties = appServerProperties;
    }

    private TableCellRenderer getTableCellRenderer() {
        return new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                TolvenPropertyInfo tolvenPropertyInfo = (TolvenPropertyInfo) value;
                JLabel label = new JLabel();
                label.setOpaque(true);
                if (isSelected) {
                    label.setForeground(table.getSelectionForeground());
                    label.setBackground(table.getSelectionBackground());
                } else {
                    label.setForeground(table.getForeground());
                    label.setBackground(table.getBackground());
                }
                if (column == 0) {
                    label.setText(tolvenPropertyInfo.getName());
                } else if (column == 1) {
                    label.setText(tolvenPropertyInfo.getValue());
                } else if (column == 2) {
                    label.setText(tolvenPropertyInfo.getDefaultStatusString());
                    label.setToolTipText(tolvenPropertyInfo.getDefaultStatusString());
                } else {
                    throw new RuntimeException("Unknown column found in " + PropertiesPanel.class);
                }
                return label;
            }
        };
    }

    private void setupPropertiesTable() {
        DefaultTableModel propertiesTableModel = (DefaultTableModel) propertiesTable.getModel();
        while (propertiesTable.getRowCount() > 0) {
            propertiesTableModel.removeRow(0);
        }
        for (TolvenPropertyInfo tolvenProperyInfo : tolvenPropertyInfos) {
            propertiesTableModel.addRow(new Object[] {
                    tolvenProperyInfo,
                    tolvenProperyInfo,
                    tolvenProperyInfo });
        }
    }

    private void updatePropertiesTable() {
        for (int i = 0; i < propertiesTable.getRowCount(); i++) {
            propertiesTable.setValueAt(tolvenPropertyInfos.get(i), i, 0);
            propertiesTable.setValueAt(tolvenPropertyInfos.get(i), i, 1);
            propertiesTable.setValueAt(tolvenPropertyInfos.get(i), i, 2);
        }
    }

    private AppServerProperties getAppServerProperties() {
        return appServerProperties;
    }

    private void setupFileChooser() {
        chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".properties.xml");
            }

            public String getDescription() {
                return "Properties XML File";
            }
        });
    }

    private void refresh() {
        try {
            if(changesPending) {
                int returnValue = JOptionPane.showConfirmDialog(getRootPane(), "You have changes pending. Reload anyway?", "Refresh", JOptionPane.YES_NO_OPTION);
                if (returnValue == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tolvenPropertyInfos = getAppServerProperties().getTolvenPropertyInfos(tolvenPropertyInfos);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        setupPropertiesTable();
        importButton.setEnabled(true);
        exportButton.setEnabled(true);
        startUpRefreshRequired = false;
        updateWidgets();
    }

    private void importProperties(File propertiesFile) throws Exception {
        tolvenPropertyInfos = getAppServerProperties().importProperties(propertiesFile.getPath());
        setupPropertiesTable();
        updateWidgets();
    }

    private void exportProperties(File propertiesFile) throws Exception {
        getAppServerProperties().exportProperties(propertiesFile.getPath());
    }

    private void add() {
        PropertyEditorDialog propertyEditorDialog = new PropertyEditorDialog(JOptionPane.getRootFrame(), "Property Editor", null, true, null, true);
        propertyEditorDialog.setVisible(true);
        if (propertyEditorDialog.getStatus() == PropertyEditorDialog.CANCEL_STATUS) {
            return;
        }
        String name = propertyEditorDialog.getPropertyName();
        if (name == null || name.length() == 0) {
            throw new RuntimeException("Property name must be supplied");
        }
        String value = propertyEditorDialog.getPropertyValue();
        for(TolvenPropertyInfo tolvenPropertyInfo : tolvenPropertyInfos) {
            if(name.equals(tolvenPropertyInfo.getName())) {
                int returnValue = JOptionPane.showConfirmDialog(getRootPane(), "This property already exists: " + name + "\nDo you wish to edit it anyway?", "Add Property", JOptionPane.YES_NO_OPTION);
                if (returnValue == JOptionPane.NO_OPTION) {
                    return;
                } else {
                    break;
                }
            }
        }
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tolvenPropertyInfos = getAppServerProperties().newProperty(name, value, tolvenPropertyInfos);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        setupPropertiesTable();
        updateButton.setEnabled(true);
        changesPending = true;
        updateWidgets();
    }
    
    private void edit() {
        int selectedRow = propertiesTable.getSelectedRow();
        TolvenPropertyInfo selectedTolvenPropertyInfo = (TolvenPropertyInfo) propertiesTable.getValueAt(selectedRow, 0);
        if (selectedTolvenPropertyInfo.getProperty() == null) {
            JOptionPane.showMessageDialog(getRootPane(), "The selected item must be refreshed from the database before it can be edited");
            updatePropertiesTable();
            return;
        }
        String key = selectedTolvenPropertyInfo.getName();
        String oldValue = selectedTolvenPropertyInfo.getValue();
        PropertyEditorDialog propertyEditorDialog = new PropertyEditorDialog(JOptionPane.getRootFrame(), "Property Editor", key, false, oldValue, true);
        propertyEditorDialog.setVisible(true);
        if (propertyEditorDialog.getStatus() == PropertyEditorDialog.CANCEL_STATUS) {
            return;
        }
        String newValue = propertyEditorDialog.getPropertyValue();
        if (!newValue.equals(oldValue)) {
            selectedTolvenPropertyInfo.setValue(newValue);
            selectedTolvenPropertyInfo.setStatus(TolvenPropertyInfo.UPDATEPENDING);
            updatePropertiesTable();
            updateButton.setEnabled(true);
            changesPending = true;
            updateButton.requestFocus();
        }
        updateWidgets();
    }

    private void remove() {
        int selectedRow = propertiesTable.getSelectedRow();
        TolvenPropertyInfo selectedTolvenPropertyInfo = (TolvenPropertyInfo) propertiesTable.getValueAt(selectedRow, 0);
        selectedTolvenPropertyInfo.setStatus(TolvenPropertyInfo.REMOVEPENDING);
        updatePropertiesTable();
        updateWidgets();
        updateButton.setEnabled(true);
        changesPending = true;
    }

    private void update() throws Exception {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tolvenPropertyInfos = getAppServerProperties().update(tolvenPropertyInfos);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        setupPropertiesTable();
        updateButton.setEnabled(false);
        changesPending = false;
        refresh();
    }

    public void updateWidgets() {
        addButton.setEnabled(!startUpRefreshRequired && tolvenPropertyInfos.size() > 0);
        addMenuItem.setEnabled(addButton.isEnabled());
        int selectedRow = propertiesTable.getSelectedRow();
        editMenuItem.setEnabled(!startUpRefreshRequired && tolvenPropertyInfos.size() > 0 && selectedRow != -1);
        importButton.setEnabled(!startUpRefreshRequired);
        exportButton.setEnabled(!startUpRefreshRequired);
        if(!startUpRefreshRequired && tolvenPropertyInfos.size() > 0 && selectedRow != -1) {
            TolvenPropertyInfo selectedTolvenPropertyInfo = (TolvenPropertyInfo) propertiesTable.getValueAt(selectedRow, 0);
            editButton.setEnabled(selectedTolvenPropertyInfo.getStatus() != TolvenPropertyInfo.REMOVEPENDING);
            editMenuItem.setEnabled(editMenuItem.isEnabled());
            removeButton.setEnabled(selectedTolvenPropertyInfo.getStatus() != TolvenPropertyInfo.REMOVEPENDING);
            removeMenuItem.setEnabled(removeButton.isEnabled());
        } else {
            editButton.setEnabled(false);
            editMenuItem.setEnabled(false);
            removeButton.setEnabled(false);
            removeMenuItem.setEnabled(false);
        }
        boolean allUpToDate = true;
        for(TolvenPropertyInfo tolvenPropertyInfo : tolvenPropertyInfos) {
            if(tolvenPropertyInfo.getStatus() != TolvenPropertyInfo.UPTODATE) {
                allUpToDate = false;
                break;
            }
        }
        updateButton.setEnabled(!startUpRefreshRequired && !allUpToDate);
        exportButton.setEnabled(!startUpRefreshRequired && allUpToDate);
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

        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        propertiesTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        updateButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();

        jLabel6.setText("Tolven Server Properties:");

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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 505, Short.MAX_VALUE)
                .add(refreshButton))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel6)
                .add(refreshButton))
        );

        propertiesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Value", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        propertiesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                propertiesTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(propertiesTable);

        updateButton.setText("Update All");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

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

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(addButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(editButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(importButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(exportButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 291, Short.MAX_VALUE)
                .add(updateButton))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(updateButton)
                .add(addButton)
                .add(editButton)
                .add(removeButton)
                .add(importButton)
                .add(exportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(22, 22, 22))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                importProperties(chooser.getSelectedFile());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
            }
        }
    }//GEN-LAST:event_importButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                exportProperties(chooser.getSelectedFile());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
            }
        }
    }//GEN-LAST:event_exportButtonActionPerformed

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

    private void propertiesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_propertiesTableMouseClicked
        try {
            if (evt.getClickCount() == 2 && propertiesTable.getSelectedRow() != -1 && tolvenPropertyInfos.size() > 0 ) {
                edit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
    }//GEN-LAST:event_propertiesTableMouseClicked

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            add();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
}//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        try {
            remove();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(getRootPane(), getNestedMessage(ex));
        }
}//GEN-LAST:event_removeButtonActionPerformed

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
    private javax.swing.JButton editButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton importButton;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable propertiesTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
    
}
