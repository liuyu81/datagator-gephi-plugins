/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.ext.gephi.importer;

import java.io.LineNumberReader;
import java.io.Reader;

import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.ImportUtils;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.FileImporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

/**
 * @author LIU Yu <liuyu@opencps.net>
 * @date 2015/09/02
 */
public class MatrixJsonImporter
    implements FileImporter, LongTask
{

    private static final JsonFactory json = new JsonFactory();

    private Reader reader;
    private ContainerLoader container;
    private Report report;
    private ProgressTicket progressTicket;
    private boolean cancel = false;

    public void setGraphType(boolean isDirected, boolean isDynamic)
    {
        ;
    }
    
    @Override
    public void setReader(Reader reader)
    {
        this.reader = reader;
    }

    @Override
    public boolean execute(ContainerLoader loader)
    {
        this.container = loader;
        this.report = new Report();

        try {
            //Import
            JsonParser parser = json.createParser(this.reader);
            // TODO
            // importData(lineReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return !cancel;
    }

    @Override
    public ContainerLoader getContainer()
    {
        return container;
    }

    @Override
    public Report getReport()
    {
        return report;
    }

    @Override
    public boolean cancel()
    {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket)
    {
        this.progressTicket = progressTicket;
    }

    private void importData(LineNumberReader reader)
        throws Exception
    {
        // read type code initial line
        String line = reader.readLine();
        String typecode = line;
        report.log("Typecode is " + typecode);

        // read comment lines if any
        boolean comment = true;
        while (comment) {
            line = reader.readLine();
            comment = line.startsWith("%");
        }

        String[] str = line.split("( )+");
        int nRows = (Integer.valueOf(str[0].trim())).intValue();
        int nColumns = (Integer.valueOf(str[1].trim())).intValue();
        int nNonZeros = (Integer.valueOf(str[2].trim())).intValue();
        report.log("Number of rows: " + nRows);
        report.log("Number of cols: " + nColumns);
        report.log("Number of non zeros: " + nNonZeros);

        while ((line = reader.readLine()) != null) {
            //Read coordinates and value
            str = line.split("( )+");
            int node1Index = (Integer.valueOf(str[0].trim())).intValue();
            int node2Index = (Integer.valueOf(str[1].trim())).intValue();
            float weight = 1f;
            if (str.length > 2) {
                weight = (Double.valueOf(str[2].trim())).floatValue();
            }

            //Get or create node
            NodeDraft node1 = null;
            if (container.nodeExists(String.valueOf(node1Index))) {
                node1 = container.getNode(String.valueOf(node1Index));
            } else {
                node1 = container.factory().newNodeDraft();
                node1.setId(String.valueOf(node1Index));

                //Don't forget to add the node
                container.addNode(node1);
            }
            NodeDraft node2 = null;
            if (container.nodeExists(String.valueOf(node2Index))) {
                node2 = container.getNode(String.valueOf(node2Index));
            } else {
                node2 = container.factory().newNodeDraft();
                node2.setId(String.valueOf(node2Index));

                //Don't forget to add the node
                container.addNode(node2);
            }

            //Create edge
            EdgeDraft edgeDraft = container.factory().newEdgeDraft();
            edgeDraft.setSource(node1);
            edgeDraft.setTarget(node2);
            edgeDraft.setWeight(weight);
            container.addEdge(edgeDraft);
        }
    }
}
