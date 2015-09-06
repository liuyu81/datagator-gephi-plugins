/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.ext.gephi.importer;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.gephi.io.importer.spi.Importer;
import org.gephi.io.importer.spi.ImporterUI;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.api.Issue;
import org.openide.util.lookup.ServiceProvider;

import org.openide.util.NbBundle;

/**
 *
 * @author liuyu
 */
@ServiceProvider(service = ImporterUI.class)
public class MatrixJsonImporterWizard
    implements ImporterUI
{

    private MatrixJsonImporter importer;
    private MatrixJsonImporterWizardPanel panel;

    @Override
    public void setup(Importer importer)
    {
        this.importer = (MatrixJsonImporter) importer;
    }

    @Override
    public JPanel getPanel()
    {
        panel = new MatrixJsonImporterWizardPanel();
        return (JPanel) panel;
    }

    @Override
    public void unsetup(boolean update)
    {
        if (update) {
            // edge type: directed / undirected
            importer.setGraphType(panel.isDirectedGraph(), panel.isDynamicGraph());
        }
        importer = null;
        panel = null;
    }

    @Override
    public String getDisplayName()
    {
        return NbBundle.getMessage(MatrixJsonImporterWizard.class,
            "MatrixJsonImporterWizard.text.title");
    }

    @Override
    public boolean isUIForImporter(Importer importer)
    {
        return importer instanceof MatrixJsonImporter;
    }
}
