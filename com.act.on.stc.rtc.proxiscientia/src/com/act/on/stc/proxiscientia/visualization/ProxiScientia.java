package com.act.on.stc.proxiscientia.visualization;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.act.on.stc.common.utilities.Pair;

public class ProxiScientia {
	private LinkedList<EntityProximity> links;
	private LinkedList<Place> place; 
	private double scale_glob;
	private double max_dist;
	
	// decimal rounding
	private final int DECIMALS = 2;
	
	private Composite parent;
	
	// default window dimensions
	private final int WIDTH = 300; 
	private final int HEIGHT = 300;
	
	private double stdev;
	private double average;
	
	public ProxiScientia(Composite parent){
		links = new LinkedList<EntityProximity>();
		place = new LinkedList<Place>();
		this.parent = parent; 
	}
	
	/**
	 * Overloaded constructor to deal with user/task tuples
	 * @param userTuples
	 * @author Arber Borici
	 * 2011-11-25
	 */
	public ProxiScientia(ScrolledComposite parent, List<Pair<Pair<String, String>, Double>> tuples, String task) {
		scale_glob = 1; // global scaling factor applied to all links. See also scale_local in generateVisual().
		max_dist = Double.MAX_VALUE;
		
		links = new LinkedList<EntityProximity>();
		place = new LinkedList<Place>();
		
		this.parent = parent;
		
		for (Pair<Pair<String, String>, Double> tuple : tuples){
			links.add(new EntityProximity(tuple.getFirstElem().getFirstElem(),
							tuple.getFirstElem().getSecondElem(),
							roundTwoDecimals(tuple.getSecondElem())));
			
			if (max_dist < tuple.getSecondElem()) 
				max_dist = tuple.getSecondElem();
		}
		
		max_dist = 1/max_dist;
	}
	
	/* Considers all objects in "links" and finds their x,y coordinates for 
	 * displaying on the shell. This data is stored in "place".
	 * 
	 * Method assumes second member of each link is the member of interest and 
	 * ignores the first member, except for the first link which considers both.
	 */
	private int layOutNodes(){
		
		if (links.size() == 0) return -1;
		
		int sizeof_links = links.size(); // avoid division by zero below
		
		double degs_per = 360/sizeof_links; //Degrees per object
		double degs_tot = 0;				//Degrees counted so far
		int sizeof_place = sizeof_links+1;

		for(int i=0; i<sizeof_place; i++){
			
			Place place_temp = new Place();
			if(i==0){ // turns the first name in the first object in "links" into the focus of the display				
				place_temp.name = links.getFirst().getFirstEntityName();
				place_temp.dist = 0;
				place_temp.prox = 0;
				place_temp.x_coord = 0;
				place_temp.y_coord = 0;
				
				place.add(place_temp);
			
			}
			else if (roundTwoDecimals(links.get(i-1).getProximityValue()) > 0.0){
				place_temp.name = links.get(i-1).getSecondEntityName();
				place_temp.dist = (1.0/links.get(i-1).getProximityValue())*10;
				place_temp.prox = links.get(i-1).getProximityValue();
				place_temp.x_coord = (Math.cos(Math.toRadians(degs_tot))*place_temp.dist*scale_glob);
				place_temp.y_coord = -1*(Math.sin(Math.toRadians(degs_tot))*place_temp.dist*scale_glob);
				
				place.add(place_temp);
				degs_tot += degs_per;
			}
		}
		
		return 0;
	}
	

	private Double roundTwoDecimals(double score) {
		String theFormat = "#";
		
		if (this.DECIMALS > 0) {
			theFormat += ".";
			for (int i = 0; i < DECIMALS; i++)
				theFormat += "#";
		}
		
		DecimalFormat df = new DecimalFormat(theFormat);
		return Double.valueOf(df.format(score));
	}
	
	// calculate and return sample standard deviation / average of proximities:
	private double calculateStDev(LinkedList<Place> place) {
		double stdev = 0.0;
		double sum = 0.0;
		double avg = 0.0;
		
		final int N = place.size() - 1; // less the ego-centered user
		
		if (N == 0) return 0.0;
		
		// calculate average; start from the second place -- the first is
		// the current user
		for(int i = 1; i < place.size(); i++) {
			sum += place.get(i).prox;
		}
		avg = sum / N;
		
		average = avg;
		
		sum = 0.0;
		for(int i = 1; i < place.size(); i++) {
			sum += Math.pow(place.get(i).prox - avg, 2.0);
		}
		
		// sample stdev
		stdev = roundTwoDecimals(Math.sqrt(sum/(N)));
		
		return stdev;
	}
	
	/* generateVisual() takes the info stored in the "place" 
	 * list and displays it in an SWT window.
	 * 
	 */
	public int generateEgoNetwork(){
		layOutNodes();
		
		// threshold check:
		// as a threshold, eliminate outliers that are at least
		// twice away from the standard deviation.
		// 1. calculate STDEV
		// 2. if STDEV/prox(i) > 2.0, eliminate, else draw line
		stdev = calculateStDev(place);
				
		return generateVisual(this.WIDTH, this.HEIGHT);
	}
	
	
	public int generateVisual(int x_size, int y_size){
		final int DIAMETER = 7;	// node diameter
		
		// ignore passed arguments: use parent dimensions dynamically
		x_size = parent.getSize().x;
		y_size = parent.getSize().y;
		
		parent.setBackground(new Color(null, new RGB(255,255,255)));
		
		/*if(user_focus)
			parent.setText("Current User: " + place.getFirst().name + ", working on: " +  this.task);
		else{}
		*/
		parent.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				Rectangle rect = parent.getClientArea();
								
				final int X_DISP = rect.width;	// the workable dimensions of the window
				final int Y_DISP = rect.height;
				if (place.size()==0) {
					event.gc.setForeground(new Color(null, new RGB(0,0,0)));
					event.gc.drawText("", X_DISP/2-20,	Y_DISP/2-10); // render empty white region
					return;
				}
				
				for(int i = 0; i < place.size(); i++){					
					double temp = roundTwoDecimals(place.get(i).prox);
					
					final double SCALE_LOCAL = scaleVis(X_DISP-2, Y_DISP-2);
					
					event.gc.setForeground(new Color(null, new RGB(0,0,0)));
					
					if (i==0){ // Draw the central node, having a larger diameter (diam + 3)
						event.gc.drawOval(	(int)(place.get(i).x_coord * SCALE_LOCAL + (X_DISP/2) - ((DIAMETER+3)/2)),	
											(int)(place.get(i).y_coord * SCALE_LOCAL + (Y_DISP/2) - ((DIAMETER+3)/2)), 
											DIAMETER+3, 
											DIAMETER+3);
						
						event.gc.setBackground(new Color(null, new RGB(0,0,0)));
						event.gc.fillOval(	(int)(place.get(i).x_coord * SCALE_LOCAL + (X_DISP/2) - ((DIAMETER+3)/2)),	
											(int)(place.get(i).y_coord * SCALE_LOCAL + (Y_DISP/2) - ((DIAMETER+3)/2)), 
											(DIAMETER+3), 
											(DIAMETER+3));
						event.gc.setBackground(new Color(null, new RGB(255,255,255)));
					}
					else if(temp != roundTwoDecimals(0.00) && !isOutlier(temp)) {
					
						event.gc.drawOval(	(int)(place.get(i).x_coord * SCALE_LOCAL + (X_DISP/2) - (DIAMETER/2)),	
											(int)(place.get(i).y_coord * SCALE_LOCAL + (Y_DISP/2) - (DIAMETER/2)), 
											DIAMETER, 
											DIAMETER);

						event.gc.drawLine(	X_DISP/2,	
											Y_DISP/2,	
											(int)(place.get(i).x_coord * SCALE_LOCAL + (X_DISP/2)),	
											(int)(place.get(i).y_coord * SCALE_LOCAL + (Y_DISP/2))); // draws the linking line
						
						// ensure the text remains within boundaries (reasonably)
						int textX, textY;
						FontMetrics fontMetric = event.gc.getFontMetrics();
						
						// check if x falls outside the rectangle width for edges displayed on the right of the central node
						textX = (int)(place.get(i).x_coord * SCALE_LOCAL + (X_DISP/2)) +  
								fontMetric.getAverageCharWidth() * place.get(i).name.length() >= rect.width 
								? (int)(place.get(i).x_coord * SCALE_LOCAL + (X_DISP/2)) - fontMetric.getAverageCharWidth() * place.get(i).name.length() 
								: (int)(place.get(i).x_coord * SCALE_LOCAL + (X_DISP/2)) + 3;
						
						// check if y falls outside the rectangle height for edges displayed below the central node
						textY = (int)(place.get(i).y_coord * SCALE_LOCAL + (Y_DISP/2) + DIAMETER+12) +  
								fontMetric.getHeight() * 2  >= rect.height 
								? (int)(place.get(i).y_coord * SCALE_LOCAL + (Y_DISP/2) + DIAMETER+12) - fontMetric.getHeight() * 2 - 5
								: (int)(place.get(i).y_coord * SCALE_LOCAL + (Y_DISP/2) + DIAMETER+12) + fontMetric.getHeight() * 0;
						
						// check if testX is smaller than 0 for edges displayed on the left of the central node
						textX = textX < 0 ? 3 : textX;
						
						// check if testY is smaller than 0 for edges displayed on top of the central node
						textY = textY < 0 ? 3 : textY;
						
						event.gc.drawText(	place.get(i).name,	
											textX,		
											textY); // draws the name
		
						event.gc.drawText(	Double.toString(roundTwoDecimals(1.0/temp)),	
											textX - 2,	
											textY - 15); // draws the prox score
					}
				}
			}

			/**
			 * Detect outliers from a univariate statistical perspective:
			 * 
			 * For any confidence coefficient alpha, 0 < alpha < 1, 
			 * the alpha-outlier region of the N(mu, sigma^2) distribution is:
			 * 		outliers(alpha, mu, sigma) = {prox : |prox - mu| > z_(1-alpha/2) * sigma},
			 * where z_(1-alpha/2) is the (1-alpha/2) quartile of N(0, 1).
			 * We assume alpha = 5%. So, z_(0.975) = 1.9.
			 * @param prox is the proximity value
			 * @return
			 */
			private boolean isOutlier(double prox) {
				if (Math.abs(prox - average) >  1.9 * stdev)
					return true;				
				
				return false;
			}


			// scales visualization as window dimensions change:
			private double scaleVis(int x_disp, int y_disp) {
				double scaleLocal = 1.0; // no scaling
				
				double max_x = Double.MIN_VALUE;
				double max_y = Double.MIN_VALUE;
				
				for(int i=0; i<place.size(); i++){
					if (isOutlier(place.get(i).prox)) continue;

					if (max_x < Math.abs(place.get(i).x_coord))
						max_x = Double.isInfinite(Math.abs(place.get(i).x_coord))? max_x : Math.abs(place.get(i).x_coord);
					if (max_y < Math.abs(place.get(i).y_coord))
						max_y = Double.isInfinite(Math.abs(place.get(i).y_coord))? max_y : Math.abs(place.get(i).y_coord);
				}

				double minDim = Math.min(x_disp, y_disp)/2.0;
				double maxProx = Math.max(max_x, max_y);
				
				// zoom out (default)
				scaleLocal = minDim > maxProx ? minDim/maxProx : maxProx/minDim;
				
				return scaleLocal;
			}
		});
		
		Rectangle clientArea = parent.getClientArea();
		parent.setBounds(clientArea.x , clientArea.y , x_size, y_size);
		
		return 0;	
	}

}
