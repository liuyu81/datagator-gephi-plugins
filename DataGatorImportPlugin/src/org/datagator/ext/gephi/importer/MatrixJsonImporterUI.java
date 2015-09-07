/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.ext.gephi.importer;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.gephi.desktop.mrufiles.api.MostRecentFiles;
import org.gephi.io.importer.spi.Importer;
import org.gephi.io.importer.spi.ImporterUI;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.api.Issue;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.openide.util.NbBundle;

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

    private final LongTaskExecutor executor = new LongTaskExecutor(true,
        "ImporterUI", 10);

    private void prefetchMatrix()
    {
        assert ((importer != null) && (panel != null));

        // during setup(), importer.reader is still `null`, the only viable
        // means to fetch the selected file is through the MRF service.
        MostRecentFiles mrf = Lookup.getDefault().lookup(
            MostRecentFiles.class);
        final String filePath = mrf.getMRUFileList().get(0);

        // pre-fetch matrix columns for role-annotation, for large files (i.e.
        // > 1 mil records), this may take more than 15 seconds, so we need
        // to launch it within a thread to avoid jamming the UI.
        executor.execute(null, new Runnable()
        {
            @Override
            public void run()
            {
                Cursor oldCursor = panel.getCursor();
                panel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                try {
                    FileReader reader = new FileReader(filePath);
                    MatrixSummary matrix = MatrixSummary.create(reader);
                    panel.updateTableModel(matrix.getColumnHeaders());
                } catch (IOException ex) {
                    // failing matrix pre-fetch is an unrecoverable failure.
                    importer.getReport().logIssue(new Issue(
                        NbBundle.getMessage(
                            MatrixJsonImporterUI.class,
                            "MatrixJsonImporterWizard.message.failed_prefetch"),
                        Issue.Level.CRITICAL));
                } finally {
                    panel.setCursor(oldCursor);
                }
            }
        });
    }

    @Override
    public void setup(final Importer importer)
    {
        this.importer = (MatrixJsonImporter) importer;
        prefetchMatrix();
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
        }
        importer = null;
        panel = null;
    }

    @Override
    public String getDisplayName()
    {
        return NbBundle.getMessage(MatrixJsonImporterUI.class,
            "MatrixJsonImporterWizard.text.title");
    }

    @Override
    public boolean isUIForImporter(Importer importer)
    {
        return importer instanceof MatrixJsonImporter;
    }
}
