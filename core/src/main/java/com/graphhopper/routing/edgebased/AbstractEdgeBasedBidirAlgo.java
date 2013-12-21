/*
 *  Licensed to Peter Karich under one or more contributor license 
 *  agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  Peter Karich licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except 
 *  in compliance with the License. You may obtain a copy of the 
 *  License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing.edgebased;

import com.graphhopper.routing.AbstractBidirAlgo;
import com.graphhopper.routing.RoutingAlgorithm;
import com.graphhopper.routing.util.DefaultTurnCostsCalc;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.TurnCostCalculation;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.storage.EdgeEntry;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;

/**
 * Shared methods for all edge-based algorithms. Edge-based algorithms need to consider the
 * direction of an edge. Therefore edge-based algorithms are not using the usual edge-id, but a
 * decorated edge-id. An edge in forward direction will get the usual edge id, an edge in backward
 * direction will get the edge id with its first bit on 1. The direction of an edge is determined by
 * comparing the ids of base and adjacent node.
 * 
 * @author Karl Huebner
 */
public abstract class AbstractEdgeBasedBidirAlgo extends AbstractBidirAlgo
{

    public static int HIGHEST_BIT_MASK = 0x7FFFFFFF;
    public static int HIGHEST_BIT_ONE = 0x80000000;
    
    protected boolean directed = true;
    protected TurnCostCalculation turnCostCalc;

    public AbstractEdgeBasedBidirAlgo( Graph g, FlagEncoder encoder, Weighting weighting )
    {
        super(g, encoder, weighting);
        turnCosts(new DefaultTurnCostsCalc(DefaultTurnCostsCalc.MODE_IGNORE_RESTRICTIONS));
    }

    protected int createIterKey(EdgeIteratorState iter, boolean backwards) {
        return createIterKey(iter.getEdge(), iter.getBaseNode(), iter.getAdjNode(), backwards);
    }
    
    protected int createIterKey(int edgeId, int startNode, int endNode, boolean backwards) {
        return edgeId | directionFlag(startNode, endNode, backwards);
    }

    private int directionFlag(int startNode, int endNode, boolean backwards) {
        if (directed && (!backwards && startNode > endNode || backwards
                && startNode < endNode)) {
            return HIGHEST_BIT_ONE;
        }
        return 0;
    }

    protected boolean accept( EdgeIterator iter, EdgeEntry currEdge )
    {
        if ( (iter.getEdge() & HIGHEST_BIT_ONE) == HIGHEST_BIT_ONE )
        {
            //since we need to distinguish between backward and forward direction we only can accept 2^31 edges 
            throw new IllegalStateException("graph has too many edges :(");
        }
        return (currEdge.edge == EdgeIterator.NO_EDGE || iter.getEdge() != currEdge.edge)
                && super.accept(iter);
    }

    @Override
    protected boolean accept( EdgeIterator iter )
    {
        throw new UnsupportedOperationException("use accept(EdgeIterator, EdgeEntry) instead");
    }

    public RoutingAlgorithm turnCosts(TurnCostCalculation calc) {
        this.turnCostCalc = calc;
        this.turnCostCalc.setGraph(graph);
        return this;
    }

}