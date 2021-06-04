package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import model.Road;

public class MainGUI {

	private ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<String>> roads = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> miles = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> duocities = new ArrayList<ArrayList<String>>();
	private ArrayList<String> cities = new ArrayList<>();
	private ArrayList<Road> bows = new ArrayList<Road>();

	public static void main(String[] args) throws IOException {	
		MainGUI m = new MainGUI();
		m.menu();
	}

	private void menu() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String msg = "";
		String line = reader.readLine();
		while(!line.equals("") && line != null) {
			String[] input = line.split(",");
			add(input[0], input[1], input[2], Integer.parseInt(input[3]));
			bows.add(new Road(into(input[0]), into(input[1]), input[2], Integer.parseInt(input[3])));
			line = reader.readLine();
		}
		System.out.println("\n\n Kruskal");
		kruskal();
		System.out.println("\n\n Dijkistra");
		for(int i = 0; i<matrix.size(); i++)
			System.out.println(Arrays.toString(dijkistra(i)));
		System.out.println("\n\n Prim");
		array(AlgPrim());
		floyd();
		System.out.println("\n\n Floyd");
		print();
		line = reader.readLine();
		while(!line.equals("") && line != null) {
			String[] input = line.split(",");
			int i = into(input[0]);
			int j = into(input[1]);
			msg += "\n\n\nFrom                 To                   Route      Miles\n-------------------- -------------------- ---------- -----\n";
			String[] rds = roads.get(i).get(j).split(" ");
			String[] mls = miles.get(i).get(j).split(" ");
			String[] cts = duocities.get(i).get(j).split("=");
			for(int k=0; k<rds.length; k++) {
				msg += ajust(cts[k], 41)+" "+ajust(rds[k], 10)+" "+ajustL(mls[k], 5)+"\n";
			}
			msg += "     				                     -----\n                                          Total      "+ajustL(matrix.get(i).get(j)+"", 5);
			line = reader.readLine();
		}
		System.out.println(msg);
		reader.close();
	}

	public void print() {
		String msg = "";
		for(int i=0; i<matrix.size(); i++) {
			for(int j=0; j<matrix.size(); j++) {
				msg += matrix.get(i).get(j) + " ";
			}
			msg += "\n";
		}
		System.out.println(msg);
	}

	private String ajust(String msg, int n) {
		for(int i=msg.length(); i<n; i++) {
			msg += " ";
		}
		return msg;
	}

	private String ajustL(String msg, int n) {
		for(int i=msg.length(); i<n; i++) {
			msg = " "+msg;
		}
		return msg;
	}

	private void floyd(){
		int temp1, temp2;
		for(int k=0; k<cities.size(); k++){
			for(int i=0; i<cities.size(); i++){
				for (int j=0; j<cities.size(); j++){
					temp1 = matrix.get(i).get(j);
					temp2 = matrix.get(i).get(k) + matrix.get(k).get(j);
					if(temp2<temp1) {
						matrix.get(i).set(j, temp2);
						roads.get(i).set(j, (roads.get(i).get(k).trim()+" "+roads.get(k).get(j).trim()).trim());
						miles.get(i).set(j, (miles.get(i).get(k).trim()+" "+miles.get(k).get(j).trim()).trim());
						duocities.get(i).set(j, (duocities.get(i).get(k).trim()+"="+duocities.get(k).get(j).trim()).trim());
					}
				}
			}
		}
	}

	public int[] dijkistra(int k) {
		int[] mls = new int[matrix.size()];
		for(int i=0; i<matrix.size(); i++)
			mls[i] = 1000000;
		mls[k] = 0;
		PriorityQueue<Integer> pila = new PriorityQueue<>();
		pila.add(k);
		while (!pila.isEmpty()) {
			int i = pila.poll();
			for (int j = 0; j < matrix.size(); j++) {
				if (matrix.get(i).get(j) != 0) {
					if (mls[j] > mls[i] + matrix.get(i).get(j)) {
						mls[j] = mls[i] + matrix.get(i).get(j);
						pila.add(j);
					}
				}
			}
		}
		return mls;
	}

	private void kruskal() {
		int fathers[] = new int[matrix.size()];
		for(int i=0;i<matrix.size();i++){
			fathers[i]=i;
		}
		int n = matrix.size();
		int m = bows.size();
		int mst_weight = 0, mst_edges = 0;
		int	mst_ni = 0;
		Collections.sort(bows, new Comparator<Road>() {
			@Override 
			public int compare(Road p1, Road p2) {
				return p1.getMiles() - p2.getMiles();
			}
		});
		while( ( mst_edges < n-1) || (mst_ni < m) ){
			int a = bows.get(mst_ni).getFrom();
			int b = bows.get(mst_ni).getTo();
			int w = bows.get(mst_ni).getMiles();
			if( find(a, fathers) != find(b, fathers) ) {
				unite(a,b, fathers);
				mst_weight += w;
				System.out.println(cities.get(a) + " " + cities.get(b) + " " + w);
				mst_edges++;
			}
			mst_ni++;
		}
		System.out.println( "\nWeight of the MST is " + mst_weight);
	}

	int find(int x, int[] fathers){
		if(fathers[x] == x){
			return x;
		}
		return find(fathers[x], fathers);
	}

	void unite(int x, int y, int[] fathers){
		int fx = find(x, fathers);
		int fy = find(y, fathers);
		fathers[fx] = fy;
	}

	private void add(String from, String to, String route, int miles) {
		int i = config(from);
		int j = config(to);
		replace(route, miles, i, j);
		replace(route, miles, j, i);	
	}

	private void replace(String route, int mls, int i, int j) {
		if(matrix.get(i).get(j)>mls) {
			matrix.get(i).set(j, mls);
			roads.get(i).set(j, route);
			miles.get(i).set(j, mls+"");
			duocities.get(i).set(j, ajust(cities.get(i), 20)+" "+cities.get(j));
		}
	}

	private int config(String city) {
		int n = into(city);
		if(n==-1) {
			matrix.add(new ArrayList<Integer>());
			roads.add(new ArrayList<String>());
			miles.add(new ArrayList<String>());
			duocities.add(new ArrayList<String>());
			cities.add(city);
			resize();
			n = cities.size()-1;
		}
		return n;
	}

	private int into(String city) {
		for(int i=0; i<cities.size(); i++) {
			if(city.equals(cities.get(i)))
				return i;
		}
		return -1;
	}

	private void resize() {
		for(int i=0; i<cities.size(); i++) {
			for(int j=matrix.get(i).size(); j<cities.size(); j++) {
				if(i==j)
					matrix.get(i).add(0);
				else
					matrix.get(i).add(1000000);
				roads.get(i).add("");
				miles.get(i).add("");
				duocities.get(i).add("");
			}
		}
	}
	
	public int[][] AlgPrim() {
		boolean[] marcados = new boolean[bows.size()];
		Road vertice = bows.get(0);
		return AlgPrim(marcados, vertice, new int[matrix.size()][matrix.size()]);
	}
	private int[][] AlgPrim(boolean[] marcados, Road vertice, int[][] Final) {
		marcados[bows.indexOf(vertice)] = true;
		int aux = -1;
		if (!TodosMarcados(marcados)) {
			for (int i = 0; i < marcados.length; i++) {
				if (marcados[i]) {
					for (int j = 0; j < matrix.size(); j++) {
						if (matrix.get(i).get(j) != 0) {
							if (!marcados[j]) {
								if (aux == -1) {
									aux = matrix.get(i).get(j);
								} else {
									aux = Math.min(aux, matrix.get(i).get(j));
								}
							}
						}
					}
				}
			}
			for (int i = 0; i < marcados.length; i++) {
				if (marcados[i]) {
					for (int j = 0; j < matrix.size(); j++) {
						if (matrix.get(i).get(j) == aux) {
							if (!marcados[j]) {
								Final[i][j] = aux;
								Final[j][i] = aux;
								return AlgPrim(marcados, bows.get(j), Final);
							}
						}
					}
				}
			}
		}
		return Final;
	}
	public boolean TodosMarcados(boolean[] vertice) { 
		for (boolean b : vertice) {
			if (!b) {
				return b;
			}
		}
		return true;
	}
	private void array(int[][] matrix) {
		String msg = "";
		for(int i=0; i<matrix.length; i++) {
			for(int j=0; j<matrix.length; j++) {
				msg += matrix[i][j] + " ";
			}
			msg += "\n";
		}
		System.out.println(msg);
	}
}
