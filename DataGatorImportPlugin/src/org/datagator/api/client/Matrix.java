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
public class Matrix
{

    private static final JsonFactory json;

    static {
        json = new JsonFactory();
        json.setCodec(new ObjectMapper());
    }

    public final String kind;
    public final int rowsCount;
    public final int columnsCount;

    public Object[][] getColumnHeaders()
    {
        Object[][] headers = new Object[bodyRow][columnsCount];
        for (int r = 0; r < bodyRow; r++) {
            for (int c = 0; c < columnsCount; c++) {
                headers[r][c] = rows[r][c];
            }
        }
        return headers;
    }

    protected final Object[][] rows;
    protected final int bodyRow;
    protected final int bodyColumn;

    public Matrix(int columnHeaders, int rowHeaders, Object[][] rows,
        int rowsCount, int columnsCount)
    {
        this.kind = "Matrix";
        this.bodyRow = columnHeaders;
        this.bodyColumn = rowHeaders;
        this.rows = rows;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
    }

    public static Matrix create(Reader reader)
        throws IOException
    {
        JsonParser parser = json.createParser(reader);
        return parser.readValueAs(Matrix.class);
    }
}
