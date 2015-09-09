/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datagator.api.client;

import java.io.IOException;

/**
 * Row buffer model of Matrix object
 *
 * @author LIU Yu <liuyu@opencps.net>
 * @date 2015/09/09
 */
public interface MatrixRowBuffer extends Iterable<Object[]>
{
    public void put(Object[] row);
    public void clear();
    public int size();
}
