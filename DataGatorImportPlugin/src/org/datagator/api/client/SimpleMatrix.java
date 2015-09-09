/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.api.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * immutable Matrix object implementation
 *
 * @author LIU Yu <liuyu@opencps.net>
 * @date 2015/09/07
 */
@JsonDeserialize(using = MatrixDeserializer.class)
public class SimpleMatrix
    extends Entity
    implements Matrix
{

    public static SimpleMatrix create(Reader reader)
        throws IOException
    {
        return (SimpleMatrix) Entity.create(reader);
    }

    private final int rowsCount;
    private final int columnsCount;
    private final MatrixRowBuffer rows;
    private final int bodyRow;
    private final int bodyColumn;

    protected SimpleMatrix(int columnHeaders, int rowHeaders,
        MatrixRowBuffer rows, int rowsCount, int columnsCount)
    {
        super("datagator#Matrix");
        this.bodyRow = columnHeaders;
        this.bodyColumn = rowHeaders;
        this.rows = rows;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
    }

    @Override
    public int getRowsCount()
    {
        return rowsCount;
    }

    @Override
    public int getColumnsCount()
    {
        return columnsCount;
    }

    @Override
    public Matrix columnHeaders()
    {
        MatrixRowBuffer slice = new SimpleRowBuffer();
        Iterator<Object[]> it = rows();
        for (int r = 0; r < bodyRow; r++) {
            slice.put(it.next());
        }
        return new SimpleMatrix(0, bodyColumn, slice, bodyRow, columnsCount);
    }

    @Override
    public Iterator<Object[]> rows()
    {
        return rows.iterator();
    }
}
