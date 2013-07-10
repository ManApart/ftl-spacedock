package org.iceburg.ftl.spacedock;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToggleButton;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.xml.ShipBlueprint;

import org.iceburg.ftl.spacedock.ShipSaveParser.ShipSave;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JLabel;

public class SpaceDockUI {


	private JFrame frmSpaceDock;
	public ShipSave[] myShips;
	public JButton[] buttonList;
	public ShipSave currentShip;
	public File currentFile;
	
	//currentShip getter and setter
	public void setCurrentShip( ShipSave ss1) {
		currentShip = ss1;
	}
	public ShipSave getCurrentShip() { return currentShip; }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SpaceDockUI window = new SpaceDockUI();
					window.frmSpaceDock.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SpaceDockUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//initializ - get ships/file to display
		myShips = ShipSaveParser.getShipsList();
		buttonList = new JButton[myShips.length];
		File currentFile = 
   				new File(myShips[0].getshipFilePath().getParentFile() + "\\continue.sav");
		currentShip = ShipSaveParser.findCurrentShip(myShips, currentFile);
		frmSpaceDock = new JFrame();
		frmSpaceDock.setTitle("Space Dock");
		frmSpaceDock.setBounds(100, 100, 450, 300);
		frmSpaceDock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSpaceDock.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		
		
		for (int i = 0; i < myShips.length; i++) {			
		//create panel/ basic data
		String panelName = ("shipPanel" + i);
		JPanel loopPanel = new JPanel();
		frmSpaceDock.getContentPane().add(loopPanel);
		JLabel lblShipName = new JLabel(myShips[i].getPlayerShipName());
		frmSpaceDock.getContentPane().add(lblShipName);
		JLabel lblExplored = new JLabel(myShips[i].getTotalBeaconsExplored() + " beacons explored.");
		frmSpaceDock.getContentPane().add(lblExplored);
		
		//add the ship's miniship picture
//		ShipBlueprint ship = DataManager.get().getShips().get(myShips[i].getPlayerShipBlueprintId());
//		ImageIcon shipPic = new ImageIcon("img/ship/" + ship.getGraphicsBaseName() + "_base.png");
//		JLabel lblShipID = new JLabel("", shipPic, JLabel.CENTER);
//		frmSpaceDock.getContentPane().add(lblShipID);
		
		//add the board / dock button
		if (myShips[i].getshipFilePath().equals(currentFile)) {
			buttonList[i] =  new JButton("Dock");		
		}
		else {
			buttonList[i] =  new JButton("Board");
		}
		//add to a button array so we can use the index to match the button to the ship		
		frmSpaceDock.getContentPane().add(buttonList[i]);
		buttonList[i].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {				
				//connect the button to the proper ship (there must be a better way to do this!)
				//Well, this is much better than the old for loop, anyway
				JButton sourceButton = (JButton) ae.getSource();
				int i = Arrays.asList(buttonList).indexOf(sourceButton);
				ShipSaveParser parser = new ShipSaveParser();
				if (sourceButton.getText().equals("Dock")) {
		    	   sourceButton.setText("Board");	    	   
		    	   parser.dockShip(myShips[i]);
		    	   currentShip = myShips[i];
		    	   
				} else if (sourceButton.getText().equals("Board")) {
		    	   sourceButton.setText("Dock");
		          //TODO if they have boarded a ship, dock it before boarding new one;
		    	   if  (currentShip != null) {
		    		   //Find which ship has the file, dock it, and then update it's button
		    		   parser.dockShip(currentShip);
		    		   currentShip = null;
		    	   }  
		    	   parser.boardShip(myShips[i]);
		    	   currentShip = myShips[i];
			       
				}
			}
		});
		
		
		}
		
		
		
	}
	

}
