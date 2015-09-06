/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.ext.gephi.importer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.TableColumn;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import org.openide.util.NbBundle;
import org.openide.awt.Mnemonics;

/**
 *
 * @author liuyu
 */
public class MatrixJsonImporterWizardPanel
    extends javax.swing.JPanel
{

    private static class RoleEditorWidget
        extends JComboBox
    {

        // column role options
        public static final String SOURCE_NODE = NbBundle.getMessage(
            MatrixJsonImporterWizard.class,
            "MatrixJsonImporterWizard.role.node_src");
        public static final String TARGET_NODE = NbBundle.getMessage(
            MatrixJsonImporterWizard.class,
            "MatrixJsonImporterWizard.role.node_tgt");
        public static final String NODE = NbBundle.getMessage(
            MatrixJsonImporterWizard.class, "MatrixJsonImporterWizard.role.node");
        public static final String EDGE = NbBundle.getMessage(
            MatrixJsonImporterWizard.class, "MatrixJsonImporterWizard.role.edge");
        public static final String TIME = NbBundle.getMessage(
            MatrixJsonImporterWizard.class, "MatrixJsonImporterWizard.role.time");

        public RoleEditorWidget(boolean isDirectedGraph, boolean isDynamicGraph)
        {
            super();
            DefaultComboBoxModel model = (DefaultComboBoxModel) getModel();
            model.removeAllElements();
            if (isDirectedGraph) {
                model.addElement(SOURCE_NODE);
                model.addElement(TARGET_NODE);
            } else {
                model.addElement(NODE);
            }
            model.addElement(EDGE);
            if (isDynamicGraph) {
                model.addElement(TIME);
            }
        }
    };

    private static class RoleEditor
        extends DefaultCellEditor
    {

        private final boolean isDirected;
        private final boolean isDynamic;

        public RoleEditor(boolean isDirected, boolean isDynamic)
        {
            super(new RoleEditorWidget(isDirected, isDynamic));
            this.isDirected = isDirected;
            this.isDynamic = isDynamic;
        }

        @Override
        public Component getTableCellEditorComponent(final JTable table,
            final Object value, boolean isSelected, final int row,
            final int column)
        {
            assert (column == 1);

            final RoleEditorWidget widget
                = (RoleEditorWidget) super.getTableCellEditorComponent(
                    table, value, isSelected, row, column);

            for (ActionListener item : widget.getActionListeners()) {
                widget.removeActionListener(item);
            }

            // resolve conflicting roles
            widget.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String oldRole = (String) value;
                    String newRole = (String) widget.getSelectedItem();
                    if (newRole != null) {
                        if (!newRole.equals(oldRole)) {
                            onRoleChange(table, row, newRole);
                        }
                    }
                }
            });
            return widget;
        }

        private void onRoleChange(JTable table, int row, String newRole)
        {
            assert (newRole != null);
            // for undirecte graph, NODE role can appear twice (recur once),
            // other roles are exclusive, they cannot recur;
            final int maxRecurrence;
            if (!isDirected && newRole.equals(RoleEditorWidget.NODE)) {
                maxRecurrence = 1;
            } else {
                maxRecurrence = 0;
            }
            // unset reappearing roles 
            TableModel model = table.getModel();
            assert (model.getColumnCount() == 2);
            int observed = 0;
            for (int r = 0, c = 1; r < model.getRowCount(); r++) {
                if (r == row) {
                    continue;
                }
                if (newRole.equals(model.getValueAt(r, c))) {
                    observed += 1;
                    if (observed > maxRecurrence) {
                        model.setValueAt(null, r, c);
                    }
                }
            }
        }

    }

    /**
     * Creates new form MatrixJsonImporterWizardPanel
     */
    public MatrixJsonImporterWizardPanel()
    {
        initComponents();
        updateCellEditor();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(500, 250));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Directed", "Undirected" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                onEdgeTypeChange(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dynamic", "Static" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                onGraphTypeChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MatrixJsonImporterWizardPanel.class, "MatrixJsonImporterWizard.text.graph_type")); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null},
                {null, null}
            },
            new String []
            {
                "Matrix Column", "Role"
            }
        )
        {
            Class[] types = new Class []
            {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean []
            {
                false, true
            };

            public Class getColumnClass(int columnIndex)
            {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void updateCellEditor()
    {
        jTable1.removeEditor();
        // clear previously-specified roles
        TableModel model = jTable1.getModel();
        assert (model.getColumnCount() == 2);
        for (int r = 0, c = 1; r < model.getRowCount(); r++) {
            model.setValueAt(null, r, c);
        }
        // update cell editor
        TableColumn column = jTable1.getColumnModel().getColumn(1);
        column.setCellEditor(new RoleEditor(isDirectedGraph(), isDynamicGraph()));
    }

    private void onGraphTypeChange(java.awt.event.ActionEvent evt)//GEN-FIRST:event_onGraphTypeChange
    {//GEN-HEADEREND:event_onGraphTypeChange
        updateCellEditor();
    }//GEN-LAST:event_onGraphTypeChange

    private void onEdgeTypeChange(java.awt.event.ActionEvent evt)//GEN-FIRST:event_onEdgeTypeChange
    {//GEN-HEADEREND:event_onEdgeTypeChange
        updateCellEditor();
    }//GEN-LAST:event_onEdgeTypeChange

    public boolean isDirectedGraph()
    {
        return this.jComboBox1.getSelectedIndex() == 0;
    }

    public boolean isDynamicGraph()
    {
        return this.jComboBox2.getSelectedIndex() == 0;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
