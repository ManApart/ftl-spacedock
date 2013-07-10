package org.iceburg.ftl.spacedock;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.ui.IconCycleButton;
import net.blerf.ftl.xml.ShipBlueprint;

import org.iceburg.ftl.spacedock.ShipSaveParser.ShipSave;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SpaceDockUITestGUI {

	private JFrame frmSpaceDock;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SpaceDockUITestGUI window = new SpaceDockUITestGUI();
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
	public SpaceDockUITestGUI() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//initializ - get ships/file to display
		ShipSave[] myShips = ShipSaveParser.getShipsList();
		
		
		frmSpaceDock = new JFrame();
		frmSpaceDock.setTitle("Space Dock");
		frmSpaceDock.setBounds(100, 100, 450, 300);
		frmSpaceDock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSpaceDock.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		
		
		int i = 4;
		//for (int i = 0; i < myShips.length; i++) {			
		//create panel
		String panelName = ("shipPanel" + i);
		JPanel loopPanel = new JPanel();
		frmSpaceDock.getContentPane().add(loopPanel);
		
		JButton btnButtonbutton = new JButton("buttonbutton");
		btnButtonbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Button pressed!");
			}
		});
		loopPanel.add(btnButtonbutton);
		JLabel lblShipName = new JLabel(myShips[i].getPlayerShipName());
		frmSpaceDock.getContentPane().add(lblShipName);
		JLabel lblExplored = new JLabel(myShips[i].getTotalBeaconsExplored() + " beacons explored.");
		frmSpaceDock.getContentPane().add(lblExplored);
		//TODO - replace blueprint ID with appropriate picture
		//JLabel lblShipID = new JLabel(myShips[i].getPlayerShipBlueprintId());
//		ShipBlueprint ship = DataManager.get().getShips().get(myShips[i].getPlayerShipBlueprintId());
//		ImageIcon shipPic = new ImageIcon("img/ship/" + ship.getGraphicsBaseName() + "_base.png");
//		JLabel lblShipID = new JLabel("", shipPic, JLabel.CENTER);
//		frmSpaceDock.getContentPane().add(lblShipID);
		System.out.println(myShips[i].getshipFilePath());
		File currentFile = 
				new File(myShips[i].getshipFilePath().getParentFile() + "\\continue.sav");
		System.out.println(currentFile);
		JButton btnBoard = null;
		if (myShips[i].getshipFilePath().equals(currentFile)) {
		btnBoard = new JButton("Dock");
		}
		else {
			btnBoard = new JButton("Board");
		}
		frmSpaceDock.getContentPane().add(btnBoard);
		
		
				
//		for ( ShipBlueprint ship : DataManager.get().getPlayerShips() ) {
//			IconCycleButton shipBox = frmSpaceDock.createCycleButton( "img/ship/" + ship.getGraphicsBaseName() + "_base.png", false );
//			shipBox.setText( ship.getShipClass() );
			//shipBoxes.add(shipBox);
			//shipPanel.add(shipBox);
		//}
		
	}

}


//
//if (myShips[i].getshipFilePath().equals(currentFile)) {
//	btnDock = new JButton("Dock");
//	frmSpaceDock.getContentPane().add(btnDock);
//	btnDock.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//			boardButtonPress(true, btnDock);
//			System.out.println("Dock pressed");
//		}
//	});
//}
//else {
//	btnDock = new JButton("Board");
//	frmSpaceDock.getContentPane().add(btnDock);
//	btnDock.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//			boardButtonPress(false, btnDock);
//			System.out.println("Board pressed");
//		}
//	});
//}