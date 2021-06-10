package model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class GraphTest {

	Graph graph;

	public void setupScenary1() {
		graph = new Graph();
	}

	public void setupScenary2() throws IOException {
		graph = new Graph("data/data.csv");
	}

	@Test
	public void testGraph() throws IOException {
		setupScenary1();
		graph.addRoute("Cali", "Bogota", "10", 500);
		assertEquals(2, graph.getNodes().size());
	}

	@Test
	public void testFloyd() throws IOException {
		setupScenary2();
		String out = graph.shortestPath("Santa Barbara", "Las Vegas", "Floyd-Warshall");
		String sub = out.substring(61, out.length());
		String local = "";
		local += "From                 To                   Route      Miles\n";
		local += "-------------------- -------------------- ---------- -----\n";
		local += "Santa Barbara        Los Angeles          US-101        95\n";
		local += "Los Angeles          San Bernardino       I-10          65\n";
		local += "San Bernardino       Barstow              I-15          73\n";
		local += "Barstow              Baker                I-15          62\n";
		local += "Baker                Las Vegas            I-15          92\n";
		local += "                                                     -----\n";
		local += "                                          Total        387";
		assertEquals(local, sub);
	}

	@Test
	public void testDijkstra() throws IOException {
		setupScenary2();
		String out = graph.shortestPath("Santa Barbara", "Las Vegas", "Dijkstra");
		String sub = out.substring(61, out.length());
		String local = "";
		local += "From                 To                   Route      Miles\n";
		local += "-------------------- -------------------- ---------- -----\n";
		local += "Santa Barbara        Los Angeles          US-101        95\n";
		local += "Los Angeles          San Bernardino       I-10          65\n";
		local += "San Bernardino       Barstow              I-15          73\n";
		local += "Barstow              Baker                I-15          62\n";
		local += "Baker                Las Vegas            I-15          92\n";
		local += "                                                     -----\n";
		local += "                                          Total        387";
		assertEquals(local, sub);
	}
	
	@Test
	public void testKruskal() throws IOException {
		setupScenary2();
		String out = graph.kruskal();
		String sub = out.substring(61, out.length());
		String local = "";
		local += "From                 To                   Route      Miles\n";
		local += "-------------------- -------------------- ---------- -----\n";
		local += "Bakersfield          Wheeler Ridge        CA-99         24\n";
		local += "Barstow              Baker                I-15          62\n";
		local += "Bakersfield          Mojave               CA-58         65\n";
		local += "Los Angeles          San Bernardino       I-10          65\n";
		local += "Mojave               Barstow              CA-58         70\n";
		local += "San Bernardino       Barstow              I-15          73\n";
		local += "Baker                Las Vegas            I-15          92\n";
		local += "Santa Barbara        Los Angeles          US-101        95\n";
		local += "San Bernardino       San Diego            I-15         103\n";
		local += "San Luis Obispo      Santa Barbara        US-101       106\n";
		local += "\n\n";
		local += "                 Weight of the MST is 755                 ";
		assertEquals(local, sub);
	}
	
	@Test
	public void testPrim() throws IOException {
		setupScenary2();
		String out = graph.prim();
		String sub = out.substring(61, out.length());
		String local = "";
		local += "From                 To                   Route      Miles\n";
		local += "-------------------- -------------------- ---------- -----\n";
		local += "Mojave               Bakersfield          CA-58         65\n";
		local += "Barstow              Mojave               CA-58         70\n";
		local += "San Bernardino       Barstow              I-15          73\n";
		local += "Barstow              Baker                I-15          62\n";
		local += "Baker                Las Vegas            I-15          92\n";
		local += "San Luis Obispo      Santa Barbara        US-101       106\n";
		local += "Santa Barbara        Los Angeles          US-101        95\n";
		local += "Bakersfield          Wheeler Ridge        CA-99         24\n";
		local += "Los Angeles          San Bernardino       I-10          65\n";
		local += "San Bernardino       San Diego            I-15         103\n";
		local += "\n\n";
		local += "                 Weight of the MST is 755                 ";
		assertEquals(local, sub);
	}

}
