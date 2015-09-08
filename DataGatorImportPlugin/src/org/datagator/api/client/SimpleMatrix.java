/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.api.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.io.Reader;
import static java.lang.Math.min;
import java.util.ArrayList;

/**
 *
 * @author liuyu
 */
@JsonDeserialize(using = MatrixDeserializer.class)
public class SimpleMatrix
    implements Matrix
{

    private static final JsonFactory json;

    static {
        json = new JsonFactory();
        json.setCodec(new ObjectMapper());
    }

    public static SimpleMatrix create(Reader reader)
        throws IOException
    {
        JsonParser parser = json.createParser(reader);
        return parser.readValueAs(SimpleMatrix.class);
    }

    private final String kind;
    private final int rowsCount;
    private final int columnsCount;
    private final Object[][] rows;
    private final int bodyRow;
    private final int bodyColumn;

    protected SimpleMatrix(int columnHeaders, int rowHeaders, Object[][] rows,
        int rowsCount, int columnsCount)
    {
        this.kind = "Matrix";
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
    public Matrix getColumnHeaders()
    {
        Object[][] slice = new Object[bodyRow][columnsCount];
        for (int r = 0; r < bodyRow; r++) {
            for (int c = 0; c < columnsCount; c++) {
                slice[r][c] = rows[r][c];
            }
        }
        return new SimpleMatrix(0, bodyColumn, slice, bodyRow, columnsCount);
    }

    @Override
    public Object[][] toArray()
    {
        return rows;
    }

}
