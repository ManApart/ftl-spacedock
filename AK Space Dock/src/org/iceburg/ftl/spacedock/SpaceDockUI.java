package org.iceburg.ftl.spacedock;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import net.blerf.ftl.parser.DataManager;
import net.blerf.ftl.ui.IconCycleButton;
import net.blerf.ftl.xml.ShipBlueprint;

import org.iceburg.ftl.spacedock.ShipSaveParser.ShipSave;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Credit to by Vhati and ComaToes for their FTLEditor and allowing me to use their source
//FTL Editor found here: http://www.ftlgame.com/forum/viewtopic.php?f=7&t=10959&start=70
//In order to get pics of each ship, I borrowed a lot of their code in order to interact with their datamanager

//TODO outline:
//Better understand buffers/caches and implement them
//clean up code/ organize it better
//Set background, pack image and make sure works as jar
//test as jar
//upload

public class SpaceDockUI {

	private static final Logger log = LogManager.getLogger(SpaceDockUI.class);
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
		File propFile = new File("ftl-spacedock.cfg");
		File datsPath = null;
		boolean writeConfig = false;
		Properties config = new Properties();
		//TODO private HashMap<String,BufferedImage> imageCache = new HashMap<String,BufferedImage>;

		// Read the config file.
		InputStream in = null;
		try {
			if ( propFile.exists() ) {
				log.trace( "Loading properties from config file." );
				in = new FileInputStream(propFile);
				config.load( in );
			} else {
				writeConfig = true; // Create a new cfg, but only if necessary.
			}
		} catch (IOException e) {
			log.error( "Error loading config.", e );
			showErrorDialog( "Error loading config from " + propFile.getPath() );
		} finally {
			if ( in != null ) { try { in.close(); } catch (IOException e) {} }
		}
		
		//FTL Resources Path.
		String datsPathString = config.getProperty("ftlDatsPath");

		if ( datsPathString != null ) {
			log.info( "Using FTL dats path from config: " + datsPathString );
			datsPath = new File(datsPathString);
			if ( isDatsPathValid(datsPath) == false ) {
				log.error( "The config's ftlDatsPath does not exist, or it lacks data.dat." );
				datsPath = null;
			}
		} else {
			log.trace( "No FTL dats path previously set." );
		}
		

		// Find/prompt for the path to set in the config.
		if ( datsPath == null ) {
			datsPath = findFtlPath();
			if ( datsPath != null ) {
				int response = JOptionPane.showConfirmDialog(null, "FTL resources were found in:\n"+ datsPath.getPath() +"\nIs this correct?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if ( response == JOptionPane.NO_OPTION ) datsPath = null;
			}

			if ( datsPath == null ) {
				log.debug("FTL dats path was not located automatically. Prompting user for location.");
				datsPath = promptForFtlPath();
			}

			if ( datsPath != null ) {
				config.setProperty( "ftlDatsPath", datsPath.getAbsolutePath() );
				writeConfig = true;
				log.info( "FTL dats located at: " + datsPath.getAbsolutePath() );
			}
		}

		if ( datsPath == null ) {
			showErrorDialog( "FTL data was not found.\nFTL Profile Editor will now exit." );
			log.debug( "No FTL dats path found, exiting." );
			System.exit(1);
		}
		OutputStream out = null;
		if ( writeConfig ) {
			try {
				out = new FileOutputStream(propFile);
				config.store( out, "FTL Profile Editor - Config File" );

			} catch (IOException e) {
				log.error( "Error saving config to " + propFile.getPath(), e );
				showErrorDialog( "Error saving config to " + propFile.getPath() );

			} finally {
				if ( out != null ) { try { out.close(); } catch (IOException e) {} }
			}
		}

		try {
			DataManager.init( datsPath ); // Parse the dats.
		}
		catch (Exception e) {
		//	log.error( "Error parsing FTL data files.", e );
			showErrorDialog( "Error parsing FTL data files." );
			System.exit(1);
		}
		
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
		try {
			DataManager.init(datsPath);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
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
		frmSpaceDock.setLayout(new GridLayout(0, 1, 0, 0));
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 2, 0, 0));
				
		ImageIO.setUseCache(false);  // Small images don't need extra buffering.
		
		for (int i = 0; i < myShips.length; i++) {			
		//create panel/ basic data
		JPanel loopPanel = new JPanel();
		loopPanel.setLayout(new BoxLayout(loopPanel, BoxLayout.Y_AXIS));
		//loopPanel.setBackground(Color.gray);
		JLabel lblShipName = new JLabel(myShips[i].getPlayerShipName());
		loopPanel.add(lblShipName);
		JLabel lblExplored = new JLabel(myShips[i].getTotalBeaconsExplored() + " beacons explored.");
		loopPanel.add(lblExplored);
		
		//add the ship's miniship picture
		
		ShipBlueprint ship = DataManager.get().getShips()
				.get(myShips[i].getPlayerShipBlueprintId());
		BufferedImage baseImage;
		//baseImage = getResourceImage("img/customizeUI/miniship_"+ ship.getGraphicsBaseName()+ ".png");
		//TODO - ^will crash if no miniship, for instance on enemy ships. Therefore custom ships must have miniship to work with this program. 
		//So let's use base image until I can test if miniship exists.
		baseImage = getResourceImage("img/ship/"+ ship.getGraphicsBaseName() +"_base.png");
		JLabel lblShipID = new JLabel("", new ImageIcon(baseImage), JLabel.CENTER);
		//lblShipID.setPreferredSize(new Dimension(200, 140));
		loopPanel.add(lblShipID);
		
		//add the board / dock button
		if (myShips[i].getshipFilePath().equals(currentFile)) {
			buttonList[i] =  new JButton("Dock");		
		}
		else {
			buttonList[i] =  new JButton("Board");
		}
		//add to a button array so we can use the index to match the button to the ship		
		loopPanel.add(buttonList[i]);
		buttonList[i].addActionListener(new BoardListener());
		
		
		
		loopPanel.add(Box.createRigidArea(new Dimension(25, 10)));
		//frmSpaceDock.add(loopPanel);
		mainPanel.add(loopPanel);
		}
		JScrollPane scrollPanel = new JScrollPane(mainPanel);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frmSpaceDock.add(scrollPanel);
		//frmSpaceDock.pack();
	}
	
	class BoardListener implements ActionListener {
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
	          //if they have boarded a ship, dock it before boarding new one; 
	    	   if  (currentShip != null) {
	    		   //TODO Find which ship has the file, dock it, and then update it's button
	    		  // System.out.println("Already manning a ship!");
	    		   parser.dockShip(currentShip);
	    		   int b = Arrays.asList(myShips).indexOf(currentShip);
	    		   buttonList[b].setText("Board");
	    		   currentShip = null;
	    	   }  
	    	   parser.boardShip(myShips[i]);
	    	   currentShip = myShips[i];
		       
			}
		}
	}
	
	//some functions ripped straight from FTLProfileEditor because they were private
	private static void showErrorDialog( String message ) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private static boolean isDatsPathValid(File path) {
		return (path.exists() && path.isDirectory() && new File(path,"data.dat").exists());
	}
	
	private static File promptForFtlPath() {
		File ftlPath = null;

		String message = "FTL Profile Editor uses images and data from FTL,\n";
		message += "but the path to FTL's resources could not be guessed.\n\n";
		message += "You will now be prompted to locate FTL manually.\n";
		message += "Select '(FTL dir)/resources/data.dat'.\n";
		message += "Or 'FTL.app', if you're on OSX.";
		JOptionPane.showMessageDialog(null,  message, "FTL Not Found", JOptionPane.INFORMATION_MESSAGE);

		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle( "Find data.dat or FTL.app" );
		fc.addChoosableFileFilter( new FileFilter() {
			@Override
			public String getDescription() {
				return "FTL Data File - (FTL dir)/resources/data.dat";
			}
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().equals("data.dat") || f.getName().equals("FTL.app");
			}
		});
		fc.setMultiSelectionEnabled(false);

		if ( fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ) {
			File f = fc.getSelectedFile();
			if ( f.getName().equals("data.dat") )
				ftlPath = f.getParentFile();
			else if ( f.getName().endsWith(".app") && f.isDirectory() ) {
				File contentsPath = new File(f, "Contents");
				if( contentsPath.exists() && contentsPath.isDirectory() && new File(contentsPath, "Resources").exists() )
					ftlPath = new File(contentsPath, "Resources");
			}
			log.trace( "User selected: " + ftlPath.getAbsolutePath() );
		} else {
			log.trace( "User cancelled FTL dats path selection." );
		}

		if ( ftlPath != null && isDatsPathValid(ftlPath) ) {
			return ftlPath;
		}

		return null;
	}
	private static File findFtlPath() {
		String steamPath = "Steam/steamapps/common/FTL Faster Than Light/resources";
		String gogPath = "GOG.com/Faster Than Light/resources";

		String xdgDataHome = System.getenv("XDG_DATA_HOME");
		if (xdgDataHome == null)
			xdgDataHome = System.getProperty("user.home") +"/.local/share";

		File[] paths = new File[] {
			// Windows - Steam
			new File( new File(""+System.getenv("ProgramFiles(x86)")), steamPath ),
			new File( new File(""+System.getenv("ProgramFiles")), steamPath ),
			// Windows - GOG
			new File( new File(""+System.getenv("ProgramFiles(x86)")), gogPath ),
			new File( new File(""+System.getenv("ProgramFiles")), gogPath ),
			// Linux - Steam
			new File( xdgDataHome +"/Steam/SteamApps/common/FTL Faster Than Light/data/resources" ),
			// OSX - Steam
			new File( System.getProperty("user.home") +"/Library/Application Support/Steam/SteamApps/common/FTL Faster Than Light/FTL.app/Contents/Resources" ),
			// OSX
			new File( "/Applications/FTL.app/Contents/Resources" )
		};

		File ftlPath = null;

		for ( File path: paths ) {
			if ( isDatsPathValid(path) ) {
				ftlPath = path;
				break;
			}
		}

		return ftlPath;
	}
	
	private BufferedImage getResourceImage(String innerPath) {
		  // If caching, you can get(innerPath) from a HashMap and return the pre-loaded pic.
		  InputStream in = null;
		  BufferedImage result = null;
		  BufferedImage scaledBI = null;
		  try {
		    in = DataManager.get().getResourceInputStream(innerPath);
		    result = ImageIO.read(in);
		    if (result.getWidth() > 200) {
		    	int scaledWidth = 0;
		    	int scaledHeight = 0;
		    	if (result.getWidth() > result.getHeight()){
		    		scaledWidth = 191;
		    		scaledHeight = (result.getHeight()/(result.getWidth()/191));
		    	}
		    	else {
		    		scaledHeight = 121;
		    		scaledWidth = (result.getWidth()/(result.getHeight()/121));
		    	}
		    	scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TRANSLUCENT);
		    	Graphics2D g = scaledBI.createGraphics();
		    	g.drawImage(result, 0, 0, scaledWidth, scaledHeight, null); 
		    	g.dispose();
		    }
		    else {
		    	return result;
		    }
		    
		    return scaledBI;  // If caching, put result in the map before returning.
		  }
		  catch (IOException e) {
		    log.error( "Failed to load resource image ("+ innerPath +")", e );
		  }
		  finally {
		    try {if (in != null) in.close();}
		    catch (IOException f) {}
		  }
		  return result;
		}

}
