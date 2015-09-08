/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.api.client;

/**
 *
 * @author liuyu
 */
public interface Matrix
{
    public int getRowsCount();
    public int getColumnsCount();
    public Matrix getColumnHeaders();
    public Object[][] toArray();
}
