package model;

public class Road {
	private int from;
	private int to;
	private String route;
	private int miles;
	
	public Road(int f, int t, String r, int m) {
		from = f;
		to = t;
		route = r;
		miles = m;
	}
	
	public int getFrom() {
		return from;
	}
	
	public int getTo() {
		return to;
	}
	
	public String getRoute() {
		return route;
	}
	
	public int getMiles() {
		return miles;
	}
}
