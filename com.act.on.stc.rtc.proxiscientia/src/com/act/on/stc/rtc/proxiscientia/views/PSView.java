package com.act.on.stc.rtc.proxiscientia.views;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.act.on.stc.common.utilities.Pair;
import com.act.on.stc.datamanagement.XMLBuilder;
import com.act.on.stc.proxiscientia.visualization.ProxiScientia;
import com.act.on.stc.proxiscientia.visualization.datamanagement.XMLReader;
import com.act.on.stc.rtc.client.communication.RestClient;
import com.act.on.stc.rtc.common.rtc.RTCWrapper;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

public class PSView extends ViewPart implements IViewPart {
	
	private static final String HTTP_LOCALHOST_9998 = "http://localhost:9998/";
	private final Long REFRESH_RATE = 30000L; // refresh every 30 seconds
	private final Long DELAY_RATE = 0L;
	private final int DISPLAY_STYLE = SWT.H_SCROLL | SWT.V_SCROLL;
	private Display display;
	
	private final String USER_PROXIMITY_RES = "userVisualization";
	private final String TASK_PROXIMITY_RES = "taskVisualization";
	private final String mediaType = MediaType.APPLICATION_XML;
	
	private boolean dealingWithUsers;
	
	private ProxiScientia proxemics;
    private Composite parent;
    private ScrolledComposite  child;
	private StackLayout layout;
	private static Timer tmr;
	
	// No need for port forwarding-- client plugin takes care of it:
//	private static final int REMOTE_PORT = 9998;
//	private static final int LOCAL_PORT = 9998;
//	private static final String HOST = "ballroom.segal.uvic.ca";
//	private static final String PASSWORD = "1a2s3t4c";
//	private static final String USERNAME = "astc";
	
	 // class to refresh ego-network at a predefined time-rate
    private class RefreshScheduler extends TimerTask {
  
		public void run() {  
			if (dealingWithUsers) {
	            display.asyncExec(
                    new Runnable() {
                        public void run(){
                                 
                        	child.dispose();
                        	child = new ScrolledComposite(parent, DISPLAY_STYLE);
                        	child.setExpandHorizontal(true);
                            child.setExpandVertical(true);
                            child.setMinSize(parent.computeSize(300, 300));
                            layout.topControl = child;
                            
                            
                            try {
								visualizeUserNetwork();
							} catch (ClientHandlerException e) {
								e.printStackTrace();
							} catch (UniformInterfaceException e) {
								e.printStackTrace();
							} catch (URISyntaxException e) {
								e.printStackTrace();
							}
                            
                            parent.layout();
                            
                            child.redraw();
                            child.update();
                            
                            parent.redraw();
                            parent.update();
                        	                         
                        }
                    });
	        }
	        else {
	            display.asyncExec(
                    new Runnable() {
                    	public void run(){
                       
                    		child.dispose();
                            child = new ScrolledComposite(parent, DISPLAY_STYLE);
                            child.setExpandHorizontal(true);
                            child.setExpandVertical(true);
                            child.setMinSize(parent.computeSize(300, 300));
                            layout.topControl = child;
                            
                            
                            try {
								visualizeTaskNetwork();
							} catch (ClientHandlerException e) {
								e.printStackTrace();
							} catch (UniformInterfaceException e) {
								e.printStackTrace();
							} catch (URISyntaxException e) {
								e.printStackTrace();
							}
                            
                            parent.layout();
                           
                            child.redraw();
                            child.update();
                            
                            parent.redraw();
                            parent.update();
                                                        
                        }
                    });
	        }
        }
    }
    
	public PSView() {
		super();
		proxemics = new ProxiScientia(null);
		dealingWithUsers = true; // default ego-network: user-user proximities
	}
	
	public void init(IViewSite site) throws PartInitException {
        super.init(site);
	}
	
	/** 
	 * Creates and initializes the viewer 
	 * */
	public void createPartControl(Composite parent) {
		
//		PortForwardingL portForward= new PortForwardingL(USERNAME,PASSWORD,HOST,LOCAL_PORT,REMOTE_PORT);
//		try {
//			portForward.forwardPort();
//		} catch (JSchException e2) {
//			e2.printStackTrace();
//		}
//		
		this.parent = parent;
		display = Display.getDefault();
		
		// Create menu
        createMenu();
        // Create Thank you button --> not possible in ViewPart, use menu
        //createThankYouButton();
        createContextMenu();
        
        this.parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        layout = new StackLayout();
        this.parent.setLayout(layout);
        this.child = new ScrolledComposite(this.parent, DISPLAY_STYLE);
        this.child.setExpandHorizontal(true);
        this.child.setExpandVertical(true);
        layout.topControl = this.child;
        
        // scheduled refreshing of visualization with data
        tmr = new Timer();
        try {
            tmr.schedule(new RefreshScheduler(), DELAY_RATE, REFRESH_RATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

	private void createMenu() {
		final IMenuManager mgr = getViewSite().getActionBars().getMenuManager();

		// to display user-centric ego-network
		mgr.add(new Action("User-User") {
		    public void run() { 
		    	this.setText("User-User");
		    	dealingWithUsers = true;
		    	
		    	child.dispose();
                child = new ScrolledComposite(parent, DISPLAY_STYLE);
                child.setExpandHorizontal(true);
                child.setExpandVertical(true);
                child.setMinSize(parent.computeSize(300, 300));
                layout.topControl = child;
                
		    	try {
					visualizeUserNetwork();
				} catch (ClientHandlerException e) {
					e.printStackTrace();
				} catch (UniformInterfaceException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} 
		    	
		    	parent.layout();
                
                child.redraw();
                child.update();
                
                parent.redraw();
                parent.update();
                
		    }  
		});
		
		// to display task-centric ego-network
		mgr.add(new Action("Task-Task") {
		    public void run() { 
		    	this.setText("Task-Task");
		    	dealingWithUsers = false;
		    	
		    	child.dispose();
                child = new ScrolledComposite(parent, DISPLAY_STYLE);
                child.setExpandHorizontal(true);
                child.setExpandVertical(true);
                child.setMinSize(parent.computeSize(300, 300));
                layout.topControl = child;
                
		    	try {
					visualizeTaskNetwork();
				} catch (ClientHandlerException e) {
					e.printStackTrace();
				} catch (UniformInterfaceException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} 
		    	
		    	parent.layout();
                
                child.redraw();
                child.update();
                
                parent.redraw();
                parent.update();
		    }  
		});
		
		// add separator
		mgr.add(new Separator());
		
		// to refresh ego-network manually
		mgr.add(new Action("Refresh") {
		    public void run() { 
		    	this.setText("Refresh");
		    	
		    	child.dispose();
                child = new ScrolledComposite(parent, DISPLAY_STYLE);
                child.setExpandHorizontal(true);
                child.setExpandVertical(true);
                child.setMinSize(parent.computeSize(300, 300));
                layout.topControl = child;
                
		    	if (dealingWithUsers)
					try {
						visualizeUserNetwork();
					} catch (ClientHandlerException e) {
						e.printStackTrace();
					} catch (UniformInterfaceException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				else
					try {
						visualizeTaskNetwork();
					} catch (ClientHandlerException e) {
						e.printStackTrace();
					} catch (UniformInterfaceException e) {
						e.printStackTrace();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
		    	
		    	parent.layout();
                
                child.redraw();
                child.update();
                
                parent.redraw();
                parent.update();
                
		    }  
		});
		
		// to clear ego-network manually
		mgr.add(new Action("Reset") {
		    public void run() { 
		    	this.setText("Reset");
		    	
		    	child.dispose();
                child = new ScrolledComposite(parent, DISPLAY_STYLE);
                child.setExpandHorizontal(true);
                child.setExpandVertical(true);
                child.setMinSize(parent.computeSize(300, 300));
                layout.topControl = child;
		    }  
		});
		
		// add separator
		mgr.add(new Separator());
		
		// to thank ProxiScientia for the alleged help
		mgr.add(new Action("Thank you!") {
		    public void run() { 
		    	this.setText("Thank you!");
		    	
		    	try {
					pushPSViewToServer();
				} catch (TeamRepositoryException e) {
					e.printStackTrace();
				}
		    }  
		});
			
     }

	// right-click menu for Thank You:
	public void createContextMenu(){
		// initialize the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Action("Thank you!") {
				    public void run() { 
				    	this.setText("Thank you!");
				    	
				    	try {
							pushPSViewToServer();
						} catch (TeamRepositoryException e) {
							e.printStackTrace();
						}
				    }  
				});
			}
			});

			// parent or child?
			if (child != null) {
				Menu menu = menuMgr.createContextMenu(child);
				child.setMenu(menu);
			}
			else {
				Menu menu = menuMgr.createContextMenu(parent);
				parent.setMenu(menu);
			}
				
		}
	
//	private void createThankYouButton(){
//		Button btnThankYou = new Button(parent, SWT.PUSH);
//		btnThankYou.setText("Thank You!");
//		btnThankYou.setLayoutData(new FillLayout(SWT.HORIZONTAL));
//
//		btnThankYou.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e){
//				pushPSViewToServer();
//			}
//		});
//		
//		btnThankYou.addKeyListener(new KeyListener() {
//			public void keyPressed(KeyEvent e){
//				//
//			}
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//				pushPSViewToServer();
//			}
//		});
//	}
	
	// send visualization data when the user clicks/presses thank-you button
	private void pushPSViewToServer() throws TeamRepositoryException {	
		// validations...:
		if (RTCWrapper.getUser() == null || RTCWrapper.getCurrentWorkItem() == null)
			return;
		
		String visualizationInfo = "";
		
		// construct current timestamp:
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		
		// construct string:
		visualizationInfo = ts.toString() + "\t" +  
				RTCWrapper.getUser().getItemId().toString() + "\t" +
				RTCWrapper.getCurrentWorkItemUUID().toString();
		
		// push to a server text file:
		try {
			postFeedbackToServer(visualizationInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Method executes servlet with param visualizationInfo. At the time
	 * of writing (2012-01-17), the script is located at 
	 * 	http://home.segal.uvic.ca/~borici/astc/psthanks.php
	 */
	private void postFeedbackToServer(String visualizationInfo) throws IOException {
		String postResource = "http://home.segal.uvic.ca/~borix/astc/psthanks.php";
		
		URL url = new URL(postResource);
		try {
			RestClient.POST(url.toURI(), MediaType.APPLICATION_FORM_URLENCODED, "param=" + visualizationInfo);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void visualizeUserNetwork() throws ClientHandlerException, 
			UniformInterfaceException, URISyntaxException {
		// Parse the xml in the response and feed it to the ProxiScientia class
		String entity = "", content = "";

		XMLBuilder cnt;
		try {
			cnt = new XMLBuilder();
			IContributor usr = RTCWrapper.getUser();
			
			cnt.addUser(usr);
			IWorkItem wi = RTCWrapper.getCurrentWorkItem();
			cnt.addTask(wi, RTCWrapper.getCurrentWorkItemUUID(), 
					RTCWrapper.getCurrentWorkItemURI());
			
			content = cnt.build();
			
			if (!(usr == null || wi == null)) { 
				entity = getResponse(content, this.USER_PROXIMITY_RES);
			
				XMLReader xmlReader = new XMLReader(entity);
				List<Pair<Pair<String, String>, Double>> userTuples = 
						xmlReader.getUserProxTuples();

				
				if (!userTuples.isEmpty()) {
					proxemics = new ProxiScientia(child, userTuples, 
							RTCWrapper.getCurrentWorkItemURI().toString());
					proxemics.generateEgoNetwork();
				}
			}
			else {
				proxemics = new ProxiScientia(parent);
				proxemics.generateEgoNetwork();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TeamRepositoryException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}
	 
	private void visualizeTaskNetwork() throws ClientHandlerException, 
			UniformInterfaceException, URISyntaxException {
		// Parse the xml in the response and feed it to the ProxiScientia class
		String entity = "", content = "";

		XMLBuilder cnt;
		try {
			cnt = new XMLBuilder();
			IContributor usr = RTCWrapper.getUser();
			
			cnt.addUser(usr);
			IWorkItem wi = RTCWrapper.getCurrentWorkItem();
			cnt.addTask(wi, RTCWrapper.getCurrentWorkItemUUID(), 
					RTCWrapper.getCurrentWorkItemURI());
			
			content = cnt.build();
			
			if (!(usr == null || wi == null)) { 
				entity = getResponse(content, this.TASK_PROXIMITY_RES);
			
				XMLReader xmlReader = new XMLReader(entity);
				List<Pair<Pair<String, String>, Double>> taskTuples = 
						xmlReader.getTaskProxTuples();

				
				if (!taskTuples.isEmpty()) {
					proxemics = new ProxiScientia(child, taskTuples, 
							RTCWrapper.getCurrentWorkItemURI().toString());
					proxemics.generateEgoNetwork();
				}
			}
			else {
				proxemics = new ProxiScientia(parent);
				proxemics.generateEgoNetwork();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TeamRepositoryException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}
	
	private String getResponse(String content, String egoNodeResource) throws 
		ClientHandlerException, UniformInterfaceException, URISyntaxException {

		String xmlResponse = "";
		
		try {
			
			xmlResponse = RestClient.GET(new URI(HTTP_LOCALHOST_9998 + egoNodeResource), 
					 this.mediaType, content).getEntity(String.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return xmlResponse;
		
	}
	
	public void setFocus() {
		//viewer.getControl().setFocus();
	}

}
