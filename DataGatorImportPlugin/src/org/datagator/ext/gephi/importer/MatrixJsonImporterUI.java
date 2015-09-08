/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.ext.gephi.importer;

import java.awt.Cursor;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import org.datagator.api.client.Matrix;
import org.datagator.api.client.SimpleMatrix;
import org.gephi.desktop.mrufiles.api.MostRecentFiles;
import org.gephi.io.importer.spi.Importer;
import org.gephi.io.importer.spi.ImporterUI;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author liuyu
 */
@ServiceProvider(service = ImporterUI.class)
public class MatrixJsonImporterUI
    implements ImporterUI
{

    private MatrixJsonImporter importer;
    private MatrixJsonImporterUIPanel panel;
    private LongTaskExecutor executor;

    private void previewMatrixAsync()
    {
        assert ((importer != null) && (panel != null));

        // during `ImporterUI.setup()`, `importer.reader` is still `null`, the
        // only viable means to access the file is through the MRF list.
        MostRecentFiles mrf = Lookup.getDefault().lookup(MostRecentFiles.class);
        String filePath = mrf.getMRUFileList().get(0);
        try {
            importer.setReader(new FileReader(filePath));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // pre-fetch matrix columns for role annotation, for large files (i.e.
        // > 1 mil records), this may take more than 10 seconds, so we need
        // to run in an asynchronous task to avoid jamming the UI.
        Runnable asyncTask = new Runnable()
        {
            @Override
            public void run()
            {
                Cursor oldCursor = panel.getCursor();
                panel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                Matrix preview = importer.getMatrixHeaders();
                panel.updateTableModel(preview.getRowsCount(),
                    preview.getColumnsCount(), preview.toArray());
                panel.setCursor(oldCursor);
            }
        };
        executor.execute(null, asyncTask);
    }

    @Override
    public void setup(final Importer importer)
    {
        this.executor = new LongTaskExecutor(true,
            "MatrixJsonImporterUI", 10);
        this.importer = (MatrixJsonImporter) importer;
        previewMatrixAsync();
    }

    @Override
    public JPanel getPanel()
    {
        panel = new MatrixJsonImporterUIPanel();
        return (JPanel) panel;
    }

    @Override
    public void unsetup(boolean update)
    {
        if (update) {
            // edge type: directed / undirected
            importer.setGraphType(panel.isDirectedGraph(),
                panel.isDynamicGraph());
            // 
            TableModel model = panel.getTableModel();
            for (int r = 0, c = 1; r < model.getRowCount(); r++) {
                Object roleName = model.getValueAt(r, c);
                final MatrixJsonImporter.RoleType roleValue;
                if (roleName == null) {
                    continue;
                } if (roleName.equals(MatrixJsonImporterUIPanel.SOURCE_NODE)) {
                    roleValue = MatrixJsonImporter.RoleType.SOURCE_NODE;
                } else if (roleName.equals(MatrixJsonImporterUIPanel.TARGET_NODE)) {
                    roleValue = MatrixJsonImporter.RoleType.SOURCE_NODE;
                } else if (roleName.equals(MatrixJsonImporterUIPanel.NODE)) {
                    roleValue = MatrixJsonImporter.RoleType.NODE;
                } else if (roleName.equals((MatrixJsonImporterUIPanel.EDGE))) {
                    roleValue = MatrixJsonImporter.RoleType.EDGE_LABEL;
                } else if (roleName.equals((MatrixJsonImporterUIPanel.TIME))) {
                    roleValue = MatrixJsonImporter.RoleType.TIME;
                } else {
                    throw new RuntimeException(NbBundle.getMessage(
                        MatrixJsonImporter.class,
                        "MatrixJsonImporter.msg.bad_role"));
                }
            }
        }
        executor.cancel();
        executor = null;
        importer = null;
        panel = null;
    }

    @Override
    public String getDisplayName()
    {
        return NbBundle.getMessage(MatrixJsonImporterUI.class,
            "MatrixJsonImporterUI.text.title");
    }

    @Override
    public boolean isUIForImporter(Importer importer)
    {
        return importer instanceof MatrixJsonImporter;
    }
}
