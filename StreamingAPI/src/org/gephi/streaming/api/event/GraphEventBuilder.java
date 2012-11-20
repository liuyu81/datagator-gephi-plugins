/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Andre Panisson <panisson@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.streaming.api.event;

import java.util.Map;

import org.gephi.streaming.api.event.EdgeAddedEvent;
import org.gephi.streaming.api.event.ElementEvent;
import org.gephi.streaming.api.event.ElementType;
import org.gephi.streaming.api.event.EventType;
import org.gephi.streaming.api.event.GraphEvent;

/**
 * An event builder
 * 
 * @author Andre' Panisson
 * @see GraphEventContainer
 *
 */
public class GraphEventBuilder {
    
    private Object source;
    
    /**
     * @param container
     */
    public GraphEventBuilder(Object source) {
        this.source = source;
    }

    public GraphEvent graphEvent(ElementType elementType, EventType eventType, String elementId, Map<String, Object> attributes) {
        GraphEvent event = null;
        switch (elementType) {
            case NODE:
                event = new ElementEvent(source, eventType, ElementType.NODE, elementId, attributes);
                break;
            case EDGE:
                if (eventType == EventType.ADD) {
                        String fromNodeId = (String)attributes.remove("source");
                        String toNodeId = (String)attributes.remove("target");
                        Boolean directed = (Boolean)attributes.remove("directed");
                        event = new EdgeAddedEvent(source, elementId, fromNodeId, toNodeId, directed, attributes);
                } else {
                    event = new ElementEvent(source, eventType, ElementType.EDGE, elementId, attributes);
                }
                break;
            case GRAPH:
                event = new ElementEvent(source, eventType, ElementType.GRAPH, elementId, attributes);
                break;
        }
        return event;
    }

    public GraphEvent edgeAddedEvent(String edgeId, String fromNodeId, String toNodeId,
            boolean directed, Map<String, Object> attributes) {
        return new EdgeAddedEvent(source, edgeId, fromNodeId, toNodeId, directed, attributes);
    }
    
}