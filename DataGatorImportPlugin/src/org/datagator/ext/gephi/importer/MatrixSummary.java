/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.ext.gephi.importer;

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
class MatrixSummary
    implements Matrix
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
        final int minColumn = min(bodyColumn, this.rows.columnHeaders.size());
        Object[][] headers = new Object[minColumn][columnsCount];
        for (int r = 0; r < minColumn; r++) {
            for (int c = 0; c < columnsCount; c++) {
                headers[r][c] = this.rows.columnHeaders.get(r).get(c);
            }
        }
        return headers;
    }

    private final int bodyRow;
    private final int bodyColumn;

    private final MatrixSummary.DataModel rows;

    @JsonDeserialize(using = MatrixSummary.Deserializer.class)
    private static class DataModel
    {

        private static final int MAX_ROW = 1;
        // private static final int MAX_COLUMN = 0;

        private final ArrayList<ArrayList<String>> columnHeaders;
        // private final ArrayList<ArrayList<String>> rowHeaders;

        public DataModel()
        {
            this.columnHeaders = new ArrayList<ArrayList<String>>();
            // this.rowHeaders = new ArrayList<ArrayList<String>>();
        }

        public DataModel(JsonParser jp)
            throws IOException, JsonProcessingException
        {
            this();
            assert (jp.getCurrentToken().equals(JsonToken.START_ARRAY));
            assert (jp.nextToken().equals(JsonToken.START_ARRAY));
            int rowIndex = 0;
            while (!jp.nextToken().equals(JsonToken.END_ARRAY)) {
                JsonToken token = jp.getCurrentToken();
                if (rowIndex < MAX_ROW) {
                    ArrayList<String> headerR = new ArrayList<String>();
                    while (!token.equals(JsonToken.END_ARRAY)) {
                        headerR.add(jp.getText());
                        token = jp.nextToken();
                    }
                    columnHeaders.add(headerR);
                } else {
                    while (!token.equals(JsonToken.END_ARRAY)) {
                        token = jp.nextToken();
                    }
                }
                rowIndex += 1;
            }
        }
    };

    private static class Deserializer
        extends JsonDeserializer<DataModel>
    {

        @Override
        public DataModel deserialize(JsonParser jp, DeserializationContext cntx)
            throws IOException, JsonProcessingException
        {
            return new MatrixSummary.DataModel(jp);
        }
    };

    @JsonCreator
    private MatrixSummary(
        @JsonProperty(value = "kind") String kind,
        @JsonProperty(value = "columnHeaders") int columnHeaders,
        @JsonProperty(value = "rowHeaders") int rowHeaders,
        @JsonProperty(value = "rows") MatrixSummary.DataModel rows,
        @JsonProperty(value = "rowsCount") int rowsCount,
        @JsonProperty(value = "columnsCount") int columnsCount)
    {
        this.kind = kind;
        this.bodyRow = columnHeaders;
        this.bodyColumn = rowHeaders;
        this.rows = rows;
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
    }

    public static MatrixSummary create(Reader reader)
        throws IOException
    {
        JsonParser parser = json.createParser(reader);
        return parser.readValueAs(MatrixSummary.class);
    }
}
