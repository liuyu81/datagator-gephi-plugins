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
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author liuyu
 */
@ServiceProvider(service = ImporterUI.class)
public class MatrixJsonImporterWizard implements ImporterUI {

    private JPanel panel;
    private JCheckBox option;
    private MatrixJsonImporter importer;

    @Override
    public void setup(Importer importer) {
        this.importer = (MatrixJsonImporter) importer;
    }

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new MatrixJsonImporterWizardPanel();
        }
        return panel;
    }

    @Override
    public void unsetup(boolean update) {
        if (update) {
            // importer.setOption(option.isSelected());
        }
        panel = null;
        importer = null;
        option = null;
    }

    @Override
    public String getDisplayName() {
        return "Importer for DataGator Matrix JSON files";
    }

    @Override
    public boolean isUIForImporter(Importer importer) {
        return importer instanceof MatrixJsonImporter;
    }
}
