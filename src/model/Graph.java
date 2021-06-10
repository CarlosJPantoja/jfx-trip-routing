package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Graph {
	private ArrayList<ArrayList<Road>> matrix = new ArrayList<ArrayList<Road>>();
	private ArrayList<Road> bows = new ArrayList<Road>();
	private ArrayList<String> nodes = new ArrayList<>();

	public Graph() {

	}

	public Graph(String URL) throws IOException, NumberFormatException, IndexOutOfBoundsException {
		BufferedReader br = new BufferedReader(new FileReader(URL));
		String line = br.readLine();
		while(line != null && !line.equals("")) {
			String[] input = line.split(", ");
			add(input[0], input[1], input[2], Integer.parseInt(input[3]));
			bows.add(new Road(into(input[0]), into(input[1]), input[2], Integer.parseInt(input[3])));
			line = br.readLine();
		}
		br.close();
	}

	public void addRoute(String from, String to, String route, int miles) {
		add(from, to, route, miles);
		bows.add(new Road(into(from), into(to), route, miles));
	}

	private void add(String from, String to, String route, int miles) {
		int i = config(from);
		int j = config(to);
		replace(route, miles, i, j);
		replace(route, miles, j, i);	
	}

	private int config(String city) {
		int n = into(city);
		if(n==-1) {
			matrix.add(new ArrayList<Road>());
			nodes.add(city);
			resize();
			n = nodes.size()-1;
		}
		return n;
	}

	private int into(String city) {
		for(int i=0; i<nodes.size(); i++) {
			if(city.equals(nodes.get(i)))
				return i;
		}
		return -1;
	}

	private void resize() {
		for(int i=0; i<nodes.size(); i++) {
			for(int j=matrix.get(i).size(); j<nodes.size(); j++) {
				if(i==j)
					matrix.get(i).add(new Road(i, j, "", 0));
				else
					matrix.get(i).add(new Road(i, j, "", 1000000));
			}
		}
	}

	private void replace(String route, int miles, int i, int j) {
		if(matrix.get(i).get(j).getMiles()>miles) {
			matrix.get(i).set(j, new Road(i, j, route, miles));
		}
	}

	private int[][] listToArray(){
		int[][] array = new int[matrix.size()][matrix.size()];
		for(int i=0; i<matrix.size(); i++) {
			for(int j=0; j<matrix.size(); j++) {
				array[i][j] = matrix.get(i).get(j).getMiles();
			}
		}
		return array;
	}

	private String floyd(int from, int to){
		int[][] shortest = listToArray();
		String[][] roads = new String[shortest.length][shortest.length];
		int temp1, temp2;
		for(int k=0; k<nodes.size(); k++){
			for(int i=0; i<nodes.size(); i++){
				for (int j=0; j<nodes.size(); j++){
					temp1 = shortest[i][j];
					temp2 = shortest[i][k] + shortest[k][j];
					if(temp2<temp1) {
						shortest[i][j] = temp2;
						roads[i][j] = isEmpty(roads[i][j], i, j);
						roads[i][k] = isEmpty(roads[i][k], i, k);
						roads[k][j] = isEmpty(roads[k][j], k, j);
						roads[i][j] = roads[i][k].trim() + "  " + roads[k][j].trim();
					}
				}
			}
		}
		return roads[from][to];
	}

	private String isEmpty(String msg, int i, int j) {
		if(msg==null)
			return nodes.get(i)+"  "+nodes.get(j);
		else
			return msg;
	}

	private String dijkstra(int from, int to) {
		String[] roads = new String[matrix.size()];
		int[] miles = new int[matrix.size()];
		for(int i=0; i<matrix.size(); i++)
			miles[i] = 1000000;
		miles[from] = 0;
		PriorityQueue<Integer> queue = new PriorityQueue<>();
		queue.add(from);
		while (!queue.isEmpty()) {
			int i = queue.poll();
			for (int j = 0; j < matrix.size(); j++) {
				if (matrix.get(i).get(j).getMiles() != 0) {
					if (miles[j] > miles[i] + matrix.get(i).get(j).getMiles()) {
						roads[j] = isEmpty(roads[j], i, j);
						roads[i] = isEmpty(roads[i], i, i);
						roads[j] = roads[i] + "  " + nodes.get(matrix.get(i).get(j).getFrom()) + "  " + nodes.get(matrix.get(i).get(j).getTo());
						miles[j] = miles[i] + matrix.get(i).get(j).getMiles();
						queue.add(j);
					}
				}
			}
		}
		return roads[to];
	}

	public String shortestPath(String from, String to, String method) {
		String msg = "";
		int i = into(from);
		int j = into(to);
		int k = 0;
		String output = "";
		long time = System.currentTimeMillis();
		if(method.equals("Floyd-Warshall")) {
			output = floyd(i, j);
			output = isEmpty(output, i, j);
			msg += fixCenter("Performed with the Floyd-Warshall algorithm in "+(System.currentTimeMillis()-time)+" ms", 58)+"\n\n\n";
		} else {
			output = dijkstra(i, j);
			msg += fixCenter("Performed with the Dijkstra algorithm in "+(System.currentTimeMillis()-time)+" ms", 58)+"\n\n\n";
			k = 2;
		}
		String[] road = output.split("  ");
		int total = 0;
		msg += "From                 To                   Route      Miles\n"
				+ "-------------------- -------------------- ---------- -----\n";
		for( ; k+1<road.length; k+=2) {
			Road node = matrix.get(into(road[k])).get(into(road[k+1]));
			msg += fixRight(road[k], 20)+" "+fixRight(road[k+1], 20)+" "+fixRight(node.getRoute(), 10)+" "+fixLeft(node.getMiles()+"", 5)+"\n";
			total += node.getMiles();
		}
		msg += "                                                     -----\n"
				+ "                                          Total      "+fixLeft(total+"", 5);
		return msg;
	}

	public String kruskal() {
		String msg = "";
		long time = System.currentTimeMillis();
		msg += "From                 To                   Route      Miles\n"
				+ "-------------------- -------------------- ---------- -----\n";
		int[] fathers = new int[nodes.size()];
		for(int i=0; i<nodes.size(); i++)
			fathers[i] = i;
		int n = nodes.size();
		int m = bows.size();
		int mstWeight = 0, mstEdges = 0, mstNI = 0;
		Collections.sort(bows, new Comparator<Road>() {
			@Override 
			public int compare(Road p1, Road p2) {
				return p1.getMiles()-p2.getMiles();
			}
		});
		while(mstEdges < n-1 || mstNI < m){
			int from = bows.get(mstNI).getFrom();
			int to = bows.get(mstNI).getTo();
			String route = bows.get(mstNI).getRoute();
			int miles = bows.get(mstNI).getMiles();
			if(find(from, fathers) != find(to, fathers)) {
				unite(from, to, fathers);
				mstWeight += miles;
				msg += fixRight(nodes.get(from), 20)+" "+fixRight(nodes.get(to), 20)+" "+fixRight(route, 10)+" "+fixLeft(miles+"", 5)+"\n";
				mstEdges++;
			}
			mstNI++;
		}
		msg += "\n\n"+fixCenter("Weight of the MST is " + mstWeight, 58);
		msg = fixCenter("Performed with the Kruskal algorithm in "+(System.currentTimeMillis()-time)+" ms", 58)+"\n\n\n"+msg;
		return msg;
	}

	private int find(int x, int[] fathers){
		if(fathers[x] == x)
			return x;
		return find(fathers[x], fathers);
	}

	private void unite(int x, int y, int[] fathers){
		int fx = find(x, fathers);
		int fy = find(y, fathers);
		fathers[fx] = fy;
	}
	
	private int minKey(int key[], Boolean mstSet[]) {
        int min = Integer.MAX_VALUE, index = -1;
        for (int i=0; i<nodes.size(); i++)
            if (mstSet[i] == false && key[i] < min) {
                min = key[i];
                index = i;
            }
        return index;
    }
    public String prim() {
    	long time = System.currentTimeMillis();
    	int graph[][] = listToArray();
        int parent[] = new int[nodes.size()];
        int key[] = new int[nodes.size()];
        Boolean mstSet[] = new Boolean[nodes.size()];
        for(int i = 0; i < nodes.size(); i++) {
            key[i] = Integer.MAX_VALUE;
            mstSet[i] = false;
        }
        key[0] = 0;
        parent[0] = -1;
        for(int i=0; i<nodes.size()-1; i++) {
            int u = minKey(key, mstSet);
            mstSet[u] = true;
            for (int j=0; j<nodes.size(); j++)
                if (graph[u][j] != 0 && mstSet[j] == false && graph[u][j] < key[j]) {
                    parent[j] = u;
                    key[j] = graph[u][j];
                }
        }
        return printMST(parent, graph, time);
    }
    
    private String printMST(int parent[], int graph[][], long time) {
    	String msg = "From                 To                   Route      Miles\n"
				   + "-------------------- -------------------- ---------- -----\n";
        int total = 0;
        for (int i = 1; i < nodes.size(); i++) {
            msg += fixRight(nodes.get(parent[i]), 20)+" "+fixRight(nodes.get(i), 20)+" "
            	+fixRight(matrix.get(i).get(parent[i]).getRoute(), 10)+" "+fixLeft(graph[i][parent[i]]+"", 5)+"\n";
            total += graph[i][parent[i]];
        }
        msg += "\n\n"+fixCenter("Weight of the MST is " + total, 58);
        msg = fixCenter("Performed with the Prim algorithm in "+(System.currentTimeMillis()-time)+" ms", 58)+"\n\n\n"+msg;
        return msg;
    }
  	
    
	private String fixRight(String msg, int n) {
		for(int i=msg.length(); i<n; i++) {
			msg += " ";
		}
		return msg;
	}

	private String fixLeft(String msg, int n) {
		for(int i=msg.length(); i<n; i++) {
			msg = " "+msg;
		}
		return msg;
	}
	
	private String fixCenter(String msg, int n) {
		boolean aux = false;
		for(int i=msg.length(); i<n; i++) {
			if(aux)
				msg = " "+msg;
			else
				msg += " ";
			aux = !aux;
		}
		return msg;
	}

	public ArrayList<String> getNodes(){
		return nodes;
	}
}
