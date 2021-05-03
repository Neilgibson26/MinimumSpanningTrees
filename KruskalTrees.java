// Kruskal's Minimum Spanning Tree Algorithm
// Union-find implemented using disjoint set trees without compression

import java.io.*;
import java.util.Scanner;    
 
class Edge {
    public int u, v, wgt;

    public Edge() {
        u = 0;
        v = 0;
        wgt = 0;
    }

    public Edge( int x, int y, int w) {
        ;// missing lines
        this.u = x;
        this.v = y;
        this.wgt = w;
    }
    
    public void show() {
        System.out.print("Edge " + toChar(u) + "--" + wgt + "--" + toChar(v) + "\n") ;
    }
    
    // convert vertex into char for pretty printing
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }
}


class Heap
{
	private int[] h; // h will work like a hpos array and heap for me to access the edge array.
    int N; //   Max size of the array
    int pos = 1; // To keep track of the position I am at in the array  when I remove an element.
    Edge[] edge; // Passed in array of all edges


    public Heap(int _N, Edge[] _edge) {
        int i;
        N = _N;
        h = new int[N+1];
        edge = _edge;
       
        // initially just fill heap array with 
        // indices of edge[] array.
        for (i=1; i < N+1; i++)
        { 
            h[i] = i;
        }  
        // Then sort the array based on the weights of each edge
        quick_sort(h, 1, N);


    }

    //  Sort the list based on weights of each edge
    public void quick_sort(int[] h, int low, int high)
    {
        if(low>high) return;
        int mid = low+(high-low)/2; //  Get mid value
        int pivot = edge[h[mid]].wgt; //    Set pivot

        int i = low;
        int j = high;

        while(i<=j)
        {
            while(edge[h[i]].wgt<pivot)
            {
                i++;
            }
            while(edge[h[j]].wgt>pivot)
            {
                j--;
            }
            if(i<=j)
            {
                int temp = h[i];
                h[i] = h[j];
                h[j] = temp;
                i++;
                j--;
            }
        }
        if(low<j)
        {
            quick_sort(h, low, j);
        }
        if(high>i)
        {
            quick_sort(h, i, high);
        }
    }

    //  This remove will be called until the MST is full
    public Edge remove() {

        return edge[h[pos++]]; //   Return edge at hpos array
    }

    //  just for error checking and checking my sorted list
    public void displayHeap()
    {
        for(int i = 1; i<=N; i++)
        {
            System.out.println("Element " + h[i] + " weight = " + edge[h[i]].wgt);
        }
    }
}

/****************************************************
*
*       UnionFind partition to support union-find operations
*       Implemented simply using Discrete Set Trees
*
*****************************************************/

class UnionFindSets
{
    private int[] treeParent; //    Gets root node of each tree
    private int N;
    
    public UnionFindSets( int V)
    {
        N = V;
        treeParent = new int[V+1];
        
        //  Set root node for each element to be itself
        for(int i = 0; i<N+1; i++)
        {
            treeParent[i] = i;
        }
    }

    public int findSet( int vertex)
    {   
        //  Find root node of element passed in recursively
        if(treeParent[vertex] == vertex)
        {
            return vertex;
        }
        else
        {
            return findSet(treeParent[vertex]);
        }
    }
    
    //  Join 2 trees when using the edge between them
    public void union( int set1, int set2)
    {
        int parent = findSet(set1); //  Get the root node of set 1
        treeParent[parent] = findSet(set2); //  set parents new root node to be set 2's root node
    }

    //  Function to show each tree
    public void showTrees()
    {
        int i;
        for(i=1; i<=N; ++i)
            System.out.print(toChar(i) + "->" + toChar(treeParent[i]) + "  " );
        System.out.print("\n");
    }
    
    //  Function to show all of the sets
    public void showSets()
    {
        int u, root;
        int[] shown = new int[N+1];
        for (u=1; u<=N; ++u)
        {   
            root = findSet(u);
            if(shown[root] != 1) {
                showSet(root);
                shown[root] = 1;
            }            
        }   
        System.out.print("\n");
    }

    //  Function to show particular set
    private void showSet(int root)
    {
        int v;
        System.out.print("Set{");
        for(v=1; v<=N; ++v)
            if(findSet(v) == root)
                System.out.print(toChar(v) + " ");
        System.out.print("}  ");
    
    }
    
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }
}

class Graph 
{ 
    private int V, E;
    private Edge[] edge;
    private Edge[] mst;        

    public Graph(String graphFile) throws IOException
    {
        int u, v;
        int w, e;

        FileReader fr = new FileReader(graphFile);
		BufferedReader reader = new BufferedReader(fr);
	           
        String splits = " +";  // multiple whitespace as delimiter
		String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);
        
        // create edge array
        edge = new Edge[E+1];   
        
       // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            w = Integer.parseInt(parts[2]);
            
            System.out.println("Edge " + toChar(u) + "--(" + w + ")--" + toChar(v));                         
             
            // create Edge object and intialise it into edge array
            edge[e] = new Edge(u, v, w);

        }
        System.out.println(); //    new line for readability and neatness
    }


/**********************************************************
*
*       Kruskal's minimum spanning tree algorithm
*
**********************************************************/
    public Edge[] MST_Kruskal() 
    {
        int mstSize = 0; // Store sizeof MST
        Edge e; //  Temp edge
        int uSet, vSet;
        UnionFindSets partition;
        
        // create edge array to store MST
        // Initially it has no edges.
        mst = new Edge[V-1];

        // priority queue for indices of array of edges
        Heap h = new Heap(E, edge);

        // create partition of singleton sets for the vertices
        partition = new UnionFindSets(V);
        System.out.println("The sets starting out are as follows: ");
        partition.showSets();
        System.out.println();


        while(mstSize<V-1)
        {
            e = h.remove(); //  Retrieve next shortest edge
            uSet = partition.findSet(e.u); //   Get the root node of first vertex
            vSet = partition.findSet(e.v); //   Get root node of second vertex

            if(vSet != uSet) // Check if the roots are the same, if they are then it will create a cycle so move on
            {  
                partition.union(e.v, e.u); //   Join 2 disjointed sets
                mst[mstSize] = e; //    Add edge to MST
                mstSize += 1; //    Increase MST size

                //  Let the user know what is happening and for error checking
                System.out.println("Adding Edge [" + toChar(e.u) + " +(" + e.wgt + ") + " + toChar(e.v) + "]");
                partition.showSets();
                System.out.println();
            }
        }
            
        return mst;
    }


    // convert vertex into char for pretty printing
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }

    //  Print the MST
    public void showMST()
    {
        System.out.print("\nMinimum spanning tree build from following edges:\n");
        for(int e = 0; e < V-1; e++) {
            mst[e].show(); 
        }
        System.out.println();
       
    }

} // end of Graph class
    
// test code
class KruskalTrees {
    public static void main(String[] args) throws IOException
    {
        Scanner sc = new Scanner(System.in);
        String fname;
        System.out.print("\nInput name of file with graph definition: ");
        fname = sc.nextLine();

        Graph g = new Graph(fname);

        g.MST_Kruskal();

        g.showMST();
        
    }
}    


