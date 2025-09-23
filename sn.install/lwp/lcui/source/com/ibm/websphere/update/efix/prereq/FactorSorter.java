/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */
package com.ibm.websphere.update.efix.prereq;

/*
 * Factoring topological sorter, for efix prerequisite and
 * corequisite handling.
 *
 * History 1.3, 10/20/02
 *
 * 02-Oct-2002 Initial Version
 *
 * 18-Oct-2002 Made debugging output conditional.
 *
 * 20-Oct-2002 Fixed infinite loop in node merge operation.
 */

import com.ibm.websphere.product.*;
import com.ibm.websphere.product.history.*;
import com.ibm.websphere.product.history.xml.*;
import com.ibm.websphere.product.xml.component.*;
import com.ibm.websphere.product.xml.efix.*;
import com.ibm.websphere.product.xml.product.*;
import java.io.*;
import java.util.*;

/**
 *  
 */
public class FactorSorter
{
    // Program versioning ...

    public static final String pgmVersion = "1.3" ;
    // Program versioning ...

    public static final String pgmUpdate = "10/20/02" ;

    public static final String debugPropertyName = "com.ibm.websphere.update.efix.prereq.debug" ;
    public static final String debugTrueValue = "true" ;
    public static final String debugFalseValue = "false" ;

    public static boolean debug;

    /**
	 * @return  the debug
	 * @uml.property  name="debug"
	 */
    public static boolean isDebug()
    {
        return debug;
    }

    static {
        String debugPropValue = (String) System.getProperty(debugPropertyName);

        debug = ( (debugPropValue != null) && debugPropValue.equals(debugTrueValue) );
    }

    public static void debug(String arg)
    {
        if ( !debug )
            return;

        System.out.println(arg);
    }

    public static void debug(String arg1, String arg2)
    {
        if ( !debug )
            return;

        System.out.print(arg1);
        System.out.println(arg2);
    }

    // Invariants:
    //
    // aLeadingNode
    //   aLeadingNode.outEdges
    // aTrailingNode
    //   aTrailingNode.inEdges
    // anEdge
    //   anEdge.leadingNode
    //   anEdge.trailingNode
    //
    // anEdge.leadingNode.outEdges.get(anEdge.trailingNode) == anEdge
    // anEdge.trailingNode.inEdges.get(anEdge.leadingNode) == anEdge

    // An edge is directed <from> a leading node <to> a trailing node.

    // This class is not quite a proper factor edge ...
    //
    // If it were, it would keep links to the edges which it represents.

    /**
	 *  
	 */
    public class FactorEdge
    {
        public FactorNode leadingNode;
        public FactorNode trailingNode;
    
        public FactorEdge(FactorNode leadingNode, FactorNode  trailingNode)
        {
            this.leadingNode = leadingNode;
            this.trailingNode = trailingNode;
        }
    }

    // An node is representative of a collection of efix drivers.
    //
    // The edges are computed from prerequisite settings;
    // If efix1 has efix2 as a prerequiste, the 
    // edge is setup with efix1 leading and efix2 trailing.
    // This has consequences on the topological sort, below.

    /**
	 *  
	 */
    public class FactorNode
    {
        // The collection of drivers that this node represents.
        //
        // The key to the driver collection is the id of the
        // efix driver.
        //
        // [efixId] ==> [efixDriver]

        public HashMap drivers = new HashMap();
        protected String driverIds = "";

        // The collection of edges leading in to this node.
        //
        // For these edges, this node is the trailing node.
        //
        // The key to the in-edge collection is the node
        // on the far end of the edge.
        //
        // [FactorNode ==> FactorEdge]

        public HashMap inEdges = new HashMap();
    
        // The collection of edges leading out of this node.
        //
        // For these edges, this node is the leading node.
        //
        // The key to the out-edge collection is the node
        // on the far end of the edge.
        //
        // [FactorNode ==> FactorEdge]

        public HashMap outEdges = new HashMap();
    
        // Build a node on the argument driver.

        public FactorNode(efixDriver driver)
        {
            addDriver(driver);
        }

        protected void addDriver(efixDriver driver)
        {
            String efixId = driver.getId();

            drivers.put(efixId,  driver);

            if ( driverIds.length() == 0 )
                driverIds = efixId;
            else
                driverIds = driverIds + ", " + efixId;
        }

        /**
		 * @return  the driverIds
		 * @uml.property  name="driverIds"
		 */
        public String getDriverIds()
        {
            return driverIds;
        }

        protected boolean reaches(FactorNode trailingNode)
        {
            return ( outEdges.containsKey(trailingNode) );
        }

        protected boolean isReachedFrom(FactorNode leadingNode)
        {
            return ( inEdges.containsKey(leadingNode) );
        }

        // Link the receiver to the trailing node.
        // The receiver is the leading node.
        //
        // Invariants:
        //
        // anEdge.leadingNode.outEdges.get(anEdge.trailingNode) == anEdge
        // anEdge.trailingNode.inEdges.get(anEdge.leadingNode) == anEdge

        protected void linkTo(FactorNode trailingNode)
        {
            FactorEdge outEdge = new FactorEdge(this, trailingNode);

            this.outEdges.put(trailingNode, outEdge);
            trailingNode.inEdges.put(this, outEdge);

            if ( isDebug() ) {
                debug("Linking: " + this.getDriverIds(),
                      " --> " + trailingNode.getDriverIds());

                debug("Out Edges: ", Integer.toString(this.outEdges.size()));
                debug("In Edges", Integer.toString(trailingNode.inEdges.size()));
            }
        }
    }

    // A factor graph is a collection of factor nodes.
    //
    // Using the nodes collection as a set, so map each
    // node to itself.
    //
    // [FactorNode ==> FactorNode]

    protected HashMap nodes = new HashMap();

    protected void basicRemoveNode(FactorNode vanishingNode)
    {
        nodes.remove(vanishingNode);
    }

    protected void basicAddNode(FactorNode node)
    {
        nodes.put(node, node);
    }

    // Build the graph on the argument drivers.
    //
    // Initially, each node has exactly one driver.
    //
    // The nodes used by the edges are shared, so all
    // of the nodes are created before any of the edges
    // are created.
    //
    // While processing prereqs, a list is made of those
    // prereqs which are not satisfied by the collection
    // itself.  These will need to be tested against
    // the actual installed efixes to determine if they
    // are satisfied.

    protected Vector buildGraph(Vector drivers)
    {
        debug("Building graph ...");

        HashMap indexedNodes = new  HashMap();  // [efixId ==> FactorNode]

        int numDrivers =  drivers.size();

        for ( int driverNo = 0; driverNo < numDrivers; driverNo++ ) {
            efixDriver nextDriver = (efixDriver) drivers.elementAt(driverNo);

            FactorNode nextNode = new FactorNode(nextDriver);
            basicAddNode(nextNode);

            debug("  Creating node for efix: ", nextDriver.getId());

            indexedNodes.put(nextDriver.getId(), nextNode);
        }

        Vector externalPrereqs = new Vector();

        for ( int driverNo = 0; driverNo < numDrivers; driverNo++ ) {
            efixDriver nextDriver = (efixDriver) drivers.elementAt(driverNo);

            FactorNode nextDriverNode = (FactorNode) indexedNodes.get(nextDriver.getId());

            debug("  Processing prereqs of ", nextDriver.getId());

            int numPrereqs = nextDriver.getEFixPrereqCount();

            for ( int prereqNo = 0; prereqNo < numPrereqs; prereqNo++ ) {
                efixPrereq nextPrereq = nextDriver.getEFixPrereq(prereqNo);

                if ( !nextPrereq.getIsNegativeAsBoolean() ) {
                    String prereqId = nextPrereq.getEFixId();

                    FactorNode prereqNode = (FactorNode) indexedNodes.get(prereqId);

                    if ( prereqNode == null ) {
                        debug("    Noting external prerequisite: ", prereqId);
                        externalPrereqs.addElement(new String[] { nextDriver.getId(), prereqId });
                    } else {
                        debug("    Noting internal prerequisite: ", prereqId);
                        nextDriverNode.linkTo(prereqNode);
                    }
                } else {
                    debug("    Negative prerequisite; ignoring");
                }
            }
        }

        debug("Building graph ... done");

        return externalPrereqs;
    }
    
    // Have:
    //       --> (trailing) --> vanishingNode --> (leading) -->
    //       --> (trailing) --> absorbingNode --> (leading) -->
    //
    // Need to move all of the trailing edges of the vanishing node to
    // the absorbing node, but, don't transfer any edges that lead from the
    // the absorbing node, and don't transfer any edges from nodes that
    // already lead to the absorbing node.
    //
    // The edges which are moved must have their trailing node changed
    // from the vanishing node to the absorbing node.
    //
    // The edges which are not retaining must be removed from their leading
    // node.  (These edges need not be removed from the vanishing node,
    // since that node will be removed entirely.)
    //
    // In parallel, need to move all of the leading edges of the vanishing
    // node to the absorbing node, but, don't transfer any edges that lead
    // to the absorbing node, and don't transfer any edges from nodes that
    // already are reached from the absorbing node.
    //
    // The edges are moved must have their leading node changed from the
    // vanishing node to the absorbing node.
    //
    // The edges which are not retaining must be removed from their trailing
    // node.  (These edges need not be removed from the vanishing node, since
    // that node will be removed entirely.
    //
    // The vanishing node must be removed from the graph.
    
    protected void merge(FactorNode vanishingNode, FactorNode absorbingNode)
    {
        debug("Merging nodes ...");
        debug("  Vanishing Node: ", vanishingNode.getDriverIds());
        debug("  Absorbing Node: ", absorbingNode.getDriverIds());

        if ( isDebug() )
            dumpGraph();
        
        FactorEdge nextInEdge;

        while ( (nextInEdge = anyInEdge(vanishingNode)) != null ) {
            FactorNode nextLeadingNode = nextInEdge.leadingNode;

            debug("  Transferring incoming edge ... ", nextLeadingNode.getDriverIds());

            boolean relink;

            if ( nextLeadingNode == absorbingNode ) {
                debug("    :: Removing virtual self link");
                relink = false;
            } else if ( absorbingNode.isReachedFrom(nextLeadingNode) ) {
                debug("    :: Removing virtual duplicate link");
                relink = false;
            } else {
                debug("    :: Transferring link");
                relink = true;
            }

            removeEdge(nextInEdge);

            if ( relink )
                nextLeadingNode.linkTo(absorbingNode);

            if ( isDebug() )
                dumpGraph();
        }

        FactorEdge nextOutEdge;

        while ( (nextOutEdge = anyOutEdge(vanishingNode)) != null ) {
            FactorNode nextTrailingNode = nextOutEdge.trailingNode;

            boolean relink;

            debug("  Transferring outgoing edge: ", nextTrailingNode.getDriverIds());

            if ( nextTrailingNode == absorbingNode ) {
                debug("    :: Removing virtual self link");
                relink = false;
            } else if ( absorbingNode.reaches(nextTrailingNode) ) {
                debug("    :: Removing virtual duplicate link");
                relink = false;
            } else {
                debug("    :: Transferring link");
                relink = true;
            }

            removeEdge(nextOutEdge);

            if ( relink )
                absorbingNode.linkTo(nextTrailingNode);

            if ( isDebug() )
                dumpGraph();
        }

        debug("Transferring drivers ...");

        Iterator drivers = vanishingNode.drivers.values().iterator();

        while ( drivers.hasNext() ) {
            efixDriver nextDriver = (efixDriver) drivers.next();

            debug("  Transferring driver: ", nextDriver.getId());

            absorbingNode.drivers.put(nextDriver.getId(), nextDriver);
        }

        // Clear out the vanishing node's driver all at once.
        vanishingNode.drivers = new HashMap();

        debug("  Removing vanishing node");

        basicRemoveNode(vanishingNode);

        debug("Merged node: ", absorbingNode.getDriverIds());
    }
    
    protected void removeEdge(FactorEdge edge)
    {
        FactorNode
            trailingNode = edge.trailingNode,
            leadingNode  = edge.leadingNode;

        if ( isDebug() ) {
            debug("  Removing edge: " + leadingNode.getDriverIds(),
                  " --> " + trailingNode.getDriverIds());

            debug("  Initial Out Edges: ", Integer.toString(leadingNode.outEdges.size()));
            debug("  Initial In Edges: ",  Integer.toString(trailingNode.inEdges.size()));
        }

        FactorEdge
            inEdge  = (FactorEdge) trailingNode.inEdges.remove(leadingNode),
            outEdge = (FactorEdge) leadingNode.outEdges.remove(trailingNode);

        if ( isDebug() ) {
            if ( inEdge == null ) {
                debug("  Could not remove in edge from trailing node.");
            } else {
                debug("  Removed in edge from trailing node: ",
                      inEdge.leadingNode.getDriverIds() + " --> " +
                      inEdge.trailingNode.getDriverIds());
            }

            if ( outEdge == null ) {
                debug("  Could not remove out edge from leading node.");
            } else {
                debug("  Removed out edge from leading node: ",
                      outEdge.leadingNode.getDriverIds() + " --> " +
                      outEdge.trailingNode.getDriverIds());
            }

            if ( !((inEdge == edge) && (outEdge == edge)) )
                debug("  Inconsistent edges!");

            debug("  Final Out Edges: ", Integer.toString(leadingNode.outEdges.size()));
            debug("  Final In Edges: ",  Integer.toString(trailingNode.inEdges.size()));
        }

        edge.leadingNode  = null;
        edge.trailingNode = null;
    }

    // The node which is removed is a terminal node, hence is has no
    // leading edges.

    protected void removeTerminalNode(FactorNode terminalNode)
    {
        debug("Removing terminal node: ", terminalNode.getDriverIds());

        Iterator leadingNodes = terminalNode.inEdges.keySet().iterator();

        while ( leadingNodes.hasNext() ) {
            FactorNode nextLeadingNode = (FactorNode) leadingNodes.next();
            nextLeadingNode.outEdges.remove(terminalNode);
        }

        terminalNode.inEdges = new HashMap();

        basicRemoveNode(terminalNode);
    }

    // Start with a singleton factor graph;
    // Start with an empty sort list;
    //
    // While there are more nodes in the graph:
    //     Pick any node and add it to the stack;
    //
    //     While there is a node on the stack;
    //          If the node has any out edges:
    //              Pick any out edge;
    //              Get the trailing node of the out edge;
    //              If the trailing node is on the stack:
    //                  Merge the loop on the stack;
    //              If the trailing node is not on the stack:
    //                  Push the trailing node;
    //         If the node has no out edges:
    //              Push the last node onto the sort list;
    //              Remove the last node from the graph;
    //              Pop the last node;
    
    protected FactorNode anyNode()
    {
        Iterator nodeIterator = nodes.values().iterator();

        if ( nodeIterator.hasNext() )
            return (FactorNode) nodeIterator.next();
        else
            return null;
    }

    protected FactorEdge anyOutEdge(FactorNode leadingNode)
    {
        Iterator outEdges = leadingNode.outEdges.values().iterator();
        
        if ( outEdges.hasNext() )
            return (FactorEdge) outEdges.next();
        else
            return null;
    }

    protected FactorEdge anyInEdge(FactorNode trailingNode)
    {
        Iterator inEdges = trailingNode.inEdges.values().iterator();
        
        if ( inEdges.hasNext() )
            return (FactorEdge) inEdges.next();
        else
            return null;
    }

    // This merge operation is turned for the optimization that is
    // present in 'sort()', below.

    protected FactorNode merge(Vector nodes, int initialOffset, FactorNode tailNode)
    {
        debug("Merging cycle: ");

        int finalOffset = nodes.size();

        if ( isDebug() )
            debug("  Tail Node [ " + finalOffset + " ]: ", tailNode.getDriverIds());

        int trav = finalOffset;

        while ( trav > initialOffset ) {
            trav--;

            FactorNode priorTail = (FactorNode) nodes.elementAt(trav);

            if ( isDebug() )
                debug("  Tail Node [ " + trav + " ]: ", priorTail.getDriverIds());
        }

        while ( finalOffset > initialOffset ) {
            finalOffset--;

            FactorNode priorTail = (FactorNode) nodes.elementAt(finalOffset);
            nodes.removeElementAt(finalOffset);

            merge(tailNode, priorTail);

            tailNode = priorTail;
        }

        return tailNode;
    }

    // Optimized to avoid pushing the current tail node.
    //
    // This means that the traversalCount is one more than
    // the length of the traversal stack.

    public Vector sort()
    {
        debug("Performing sort ...");

        Vector sortedList = new Vector();
        Vector traversalStack = new  Vector();

        FactorNode tailNode;

        while ( (tailNode = anyNode()) != null ) {
            int traversalCount = 1;

            debug("Processing any node: ", tailNode.getDriverIds());

            while ( traversalCount > 0 ) {
                FactorEdge nextOutEdge;

                while ( (nextOutEdge = anyOutEdge(tailNode)) != null ) {
                    FactorNode nextTailNode = nextOutEdge.trailingNode;

                    debug("Next tail node: ", nextTailNode.getDriverIds());

                    if ( nextTailNode == tailNode ) {
                        debug("  :: Link to self");

                        merge(nextTailNode, tailNode);

                    } else {
                        int priorLocation = traversalStack.indexOf(nextTailNode);

                        if ( priorLocation != -1 ) {
                            debug("  :: Link to prior tail node");

                            tailNode = merge(traversalStack, priorLocation, tailNode);
                            traversalCount = priorLocation + 1;

                        } else  {
                            debug("  :: No prior link; pushing");

                            traversalStack.addElement(tailNode);
                            tailNode = nextTailNode;
                            traversalCount++;
                        }
                    }
                }

                debug("Emitting tail node: ", tailNode.getDriverIds());

                removeTerminalNode(tailNode);

                sortedList.addElement(tailNode);

                traversalCount--;

                if ( traversalCount > 0 ) {
                    tailNode = (FactorNode) traversalStack.elementAt(traversalCount - 1);
                    traversalStack.removeElementAt(traversalCount - 1);

                    debug("Popped tail node: ", tailNode.getDriverIds());

                } else {
                    tailNode = null;

                    debug("No current tail node.");
                }
            }
        }

        return sortedList;
    }

    public Vector expandFactors(Vector factorNodes)
    {
        debug("Expanding factor nodes ...");

        Vector finalList = new Vector();

        int numFactors = factorNodes.size();

        for ( int factorNo = 0; factorNo < numFactors; factorNo++ ) {
            FactorNode nextNode = (FactorNode) factorNodes.elementAt(factorNo);

            debug("  Sorting factor: ", nextNode.getDriverIds());

            Iterator factorMembers = nextNode.drivers.values().iterator();

            Vector factorDrivers = new Vector();

            while ( factorMembers.hasNext() ) {
                efixDriver nextDriver = (efixDriver) factorMembers.next();

                factorDrivers.addElement(nextDriver);
            }

            Object[] drivers = factorDrivers.toArray();

            Comparator driverComparator = new Comparator() {
                protected HashMap indexMap = new HashMap();

                public int compare(Object firstDriver, Object secondDriver) {
                    return getDriverIndex(firstDriver).compareTo(getDriverIndex(secondDriver));
                }

                public boolean equals(Object firstDriver, Object secondDriver) {
                    return getDriverIndex(firstDriver).equals(getDriverIndex(secondDriver));
                }

                protected String getDriverIndex(Object driver) {
                    String index = (String) indexMap.get(driver);

                    if ( index == null ) {
                        index = basicGetDriverIndex(driver);
                        indexMap.put(driver, index);
                    }

                    return index;
                }

                protected String basicGetDriverIndex(Object driver) {
                    efixDriver useDriver = (efixDriver) driver;

                    int prereqCount = useDriver.getEFixPrereqCount();
                    if ( prereqCount == 0 )
                        return "";

                    efixPrereq selectedPrereq = null;

                    for ( int prereqNo = 0;
                          (selectedPrereq == null ) && (prereqNo < prereqCount);
                          prereqNo++ ) {

                        efixPrereq nextPrereq = useDriver.getEFixPrereq(prereqNo);

                        if ( !nextPrereq.getIsNegativeAsBoolean() )
                            selectedPrereq = nextPrereq;
                    }

                    if ( selectedPrereq == null )
                        return null;

                    String installIndex = selectedPrereq.getInstallIndex();

                    return ( (installIndex == null) ? "" : installIndex );
                }
            };

            Arrays.sort(drivers, driverComparator);

            for ( int driverNo = 0; driverNo < drivers.length; driverNo++ ) {
                if ( isDebug() )
                    debug("Adding driver: ", ((efixDriver) (drivers[driverNo])).getId());

                finalList.addElement(drivers[driverNo]);
            }
        }

        debug("Expanding factor nodes ... done");

        int numDrivers = finalList.size();

        for ( int driverNo = 0; driverNo < numDrivers; driverNo++ ) {
            efixDriver nextDriver = (efixDriver) finalList.elementAt(driverNo);

            if ( isDebug() )
                debug("  Driver [ " + driverNo + " ]: ", nextDriver.getId());
        }

        return finalList;
    }

    protected void dumpGraph()
    {
        debug("Graph Contents:");

        Iterator nodeIterator = nodes.values().iterator();

        while ( nodeIterator.hasNext() ) {
            FactorNode nextNode = (FactorNode) nodeIterator.next();

            debug("  Next Node: ", nextNode.getDriverIds());

            debug("    In Edges:");

            Iterator inEdges = nextNode.inEdges.values().iterator();
        
            while ( inEdges.hasNext() ) {
                FactorEdge nextInEdge = (FactorEdge) inEdges.next();

                debug("      " + nextInEdge.leadingNode.getDriverIds(),
                      " --> "  + nextInEdge.trailingNode.getDriverIds());
            }

            debug("    Out Edges:");

            Iterator outEdges = nextNode.outEdges.values().iterator();
        
            while ( outEdges.hasNext() ) {
                FactorEdge nextOutEdge = (FactorEdge) outEdges.next();

                debug("      " + nextOutEdge.leadingNode.getDriverIds(),
                      " --> "  + nextOutEdge.trailingNode.getDriverIds());
            }
        }
    }
}
