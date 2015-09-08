/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.api.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author liuyu
 */
class MatrixDeserializer
    extends JsonDeserializer<SimpleMatrix>
{

    @Override
    public SimpleMatrix deserialize(JsonParser jp, DeserializationContext cntx)
        throws IOException, JsonProcessingException
    {
        int rowIndex;

        String kind = null;
        int rowsCount = -1;
        int columnsCount = -1;
        int bodyRow = -1;
        int bodyColumn = -1;

        ArrayList<ArrayList<Object>> columnHeaders
            = new ArrayList<ArrayList<Object>>();

        JsonToken token = jp.getCurrentToken();
        if (!token.equals(JsonToken.START_OBJECT)) {
            throw new RuntimeException(
                String.format("Unexpected token %s", token));
        }

        while (jp.nextToken().equals(JsonToken.FIELD_NAME)) {
            String name = jp.getText();
            token = jp.nextToken();
            if (name.equals("kind")) {
                if (!token.equals(JsonToken.VALUE_STRING)) {
                    throw new RuntimeException(
                        String.format("Unexpected token %s", token));
                }
                kind = jp.getText();
                if (!kind.equals("datagator#Matrix")) {
                    throw new RuntimeException(
                        String.format("Unexpected Entity kind %s", kind));
                }
            } else if (name.equals("columnHeaders")) {
                if (!token.equals(JsonToken.VALUE_NUMBER_INT)) {
                    throw new RuntimeException(
                        String.format("Unexpected token %s", token));
                }
                bodyRow = jp.getIntValue();
            } else if (name.equals("rowHeaders")) {
                if (!token.equals(JsonToken.VALUE_NUMBER_INT)) {
                    throw new RuntimeException(
                        String.format("Unexpected token %s", token));
                }
                bodyColumn = jp.getIntValue();
            } else if (name.equals("rows")) {
                if (bodyRow < 0 || bodyColumn < 0) {
                    throw new RuntimeException(
                        "Unexpected property order 'columnHeaders' and 'rowHeaders' should precede 'rows'.");
                }
                rowIndex = 0;
                if (!token.equals(JsonToken.START_ARRAY)) {
                    throw new RuntimeException(
                        String.format("Unexpected token %s", token));
                }
                token = jp.nextToken();
                while (token.equals(JsonToken.START_ARRAY)) {
                    int columnIndex = 0;
                    if (rowIndex < bodyRow) {
                        ArrayList<Object> rowBuffer = new ArrayList<Object>();
                        token = jp.nextToken();
                        while (!token.equals(JsonToken.END_ARRAY)) {
                            rowBuffer.add(jp.getText());
                            columnIndex += 1;
                            token = jp.nextToken();
                        }
                        columnHeaders.add(rowBuffer);
                    } else {
                        token = jp.nextToken();
                        while (!token.equals(JsonToken.END_ARRAY)) {
                            columnIndex += 1;
                            token = jp.nextToken();
                        }
                    }
                    rowIndex += 1;
                    token = jp.nextToken();
                }
            } else if (name.equals("rowsCount")) {
                if (!token.equals(JsonToken.VALUE_NUMBER_INT)) {
                    throw new RuntimeException(
                        String.format("Unexpected token %s", token));
                }
                rowsCount = jp.getIntValue();
            } else if (name.equals("columnsCount")) {
                if (!token.equals(JsonToken.VALUE_NUMBER_INT)) {
                    throw new RuntimeException(
                        String.format("Unexpected token %s", token));
                }
                columnsCount = jp.getIntValue();
            } else {
                throw new RuntimeException(
                    String.format("Unexpected property '%s'", name));
            }
        }

        if (!(0 <= bodyRow && bodyRow <= rowsCount)) {
            throw new RuntimeException("Invalid Matrix shape");
        }

        if (!(0 <= bodyColumn && bodyColumn <= columnsCount)) {
            throw new RuntimeException("Invalid Matrix shape");
        }

        // special case: size of empty matrix is 1 x 0
        if ((columnsCount == 0) && (rowsCount != 1)) {
            throw new RuntimeException("Invalid Matrix shape");
        }

        Object[][] rows = new Object[bodyRow][columnsCount];
        for (int r = 0; r < bodyRow; r++) {
            for (int c = 0; c < columnsCount; c++) {
                rows[r][c] = columnHeaders.get(r).get(c);
            }
        }

        return new SimpleMatrix(bodyRow, bodyColumn, rows, rowsCount, columnsCount);
    }

};
