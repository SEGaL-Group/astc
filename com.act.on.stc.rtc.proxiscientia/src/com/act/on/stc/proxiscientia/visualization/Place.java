package com.act.on.stc.proxiscientia.visualization;

/* The Place class is designed to store each object for placement on the shell.
 * 
 */
public class Place {
	public String name;
	public double dist;
	public double prox;
	public double x_coord;
	public double y_coord;
	
	public Place(){
		name = "";
	}

	public Place(String one, double len, double x, double y, boolean draw){
		name = one;
		dist = len;
		x_coord = x;
		y_coord = y;
	}
	
}
