import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class Main extends JPanel implements ActionListener,
	PropertyChangeListener, KeyListener {
	
	/**
	 * Fields
	 */
	
	
	//click toggle button
	JButton clickToggle = new JButton("Disable");
	boolean clicking;
	int clickX = 10;
	int clickY = 10;
	
	//clicks per second
	private static double clicksps = 1.0;
	private JLabel clickspsLabel;
	private static String clickspsString =
		"Clicks per Second: ";
	private JFormattedTextField clickspsField;
	
	//click configuration
	private JLabel clickConfigLabel;
	private static String clickConfigString =
		"Click Location (x,y): ";
	private JFormattedTextField clickConfigFieldX;
	private JFormattedTextField clickConfigFieldY;
	
	//private thread to control clicking
	private ClickBot clickThread;
	
	
	
	
	/**
	 * Constructor
	 */
    public Main() {
    	super(new GridLayout(0, 1));
    	
    	JTabbedPane tabbedPane = new JTabbedPane();
    	
    	//tabbedPane.setTabPlacement(JTabbedPane.TOP);
    	
    	//ImageIcon icon = 
    	//	new ImageIcon(Main.class.getResource("images/mouse.gif"));
        tabbedPane.setPreferredSize(new Dimension(400,200));
    	
        initializeClickToggle();
        initializeFieldsAndLabels();
        
        //add property change listeners to text fields
    	clickspsField.addPropertyChangeListener("value", this);
    	clickConfigFieldX.addPropertyChangeListener("value", this);
    	clickConfigFieldY.addPropertyChangeListener("value", this);
        
    	//set up layout
        JPanel autoClickerTabA = createPanel(null,clickspsLabel,
        		clickspsField, clickToggle, null);
        JPanel autoClickerTabB = createPanel(null, clickConfigLabel,
        		clickConfigFieldX, clickConfigFieldY, null);
        JLabel instructions = new JLabel("Press 'm' to configure location");
        JPanel autoClickerTab = createPanel(autoClickerTabA, null,
        		autoClickerTabB, null, instructions);
        
        tabbedPane.addTab("AutoClicker", /*icon*/ null, autoClickerTab,
        		"Autoclicker");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
    	tabbedPane.addKeyListener(this);
        
        //Add the tabbed pane to this panel.
        add(tabbedPane);
    }
    
	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {	
		//use invokeLater to set up the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	//set up JFrame
            	JFrame frame = new JFrame("My Utility");
            	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            	frame.getContentPane().add(new Main());
            	frame.pack();
            	frame.setVisible(true);
            }
        });
	}

	
	
	
	/**
	 * Event Listeners and Related methods
	 */
	
	/**
	 * blank methods to avoid complaints after implementing KeyListener
	 */
	public void keyReleased(KeyEvent arg0) {

    }
	public void keyPressed(KeyEvent arg0) {
		
	}

	/**
	 * waits for a key press and evaluates
	 * 	if e, then this toggles the auto-clicking
	 *  if m, then configures the clickThread's position
	 */
	public void keyTyped(KeyEvent arg0) {
		if(arg0.getKeyChar() == 'e') {
			toggleClicking();
    	}
		else if(arg0.getKeyChar() == 'm') {
			clickX = MouseInfo.getPointerInfo().getLocation().x;
			clickY = MouseInfo.getPointerInfo().getLocation().y;
			clickConfigFieldX.setValue(clickX);
	    	clickConfigFieldY.setValue(clickY);
	    	try {
	    		clickThread.updateClickLocation(clickX, clickY);
	    	} catch (NullPointerException e) {}
    	}
	}

    /**
     * enables or disables auto clicking for the clickToggle button
     */
    public void actionPerformed(ActionEvent e) {
        if ("click_enable".equals(e.getActionCommand())) {
        	toggleClicking();
        }
    }
    
    /**
     * changes whether the clickThread is clicking or not
     */
    private void toggleClicking() {
    	if(clicking) {
    		clickToggle.setText("Enable");
    		clickThread.interrupt();
    	} else {
    		clickToggle.setText("Disable");
    		clickThread = new ClickBot(clicksps, clickX, clickY);
    		clickThread.start();
    	}
    	clicking = !clicking;
    }

    /**
     * changes the clicks per second when the text field is changed
     */
    public void propertyChange(PropertyChangeEvent e) {
        Object source = e.getSource();
        if (source == clickspsField) {
            clicksps = ((Number)clickspsField.getValue()).intValue();
        }
        else if(source == clickConfigFieldX) {
        	clickX = ((Number)clickConfigFieldX.getValue()).intValue();
        }
        else if(source == clickConfigFieldY) {
        	clickY = ((Number)clickConfigFieldY.getValue()).intValue();
        }
    }

    
    
    
    /**
     * Panel Creation and Constructor related methods
     */
    
    /**
	 * Creates a JButton with variable parameters
	 * uses a predefined alignment for text and icon
	 * @param text
	 * @param icon
	 * @param action
	 * @param enabled
	 * @return
	 */
	private JButton createButton(String text, ImageIcon icon,
			String action, boolean enabled) {
		JButton result = new JButton();
		if(text != null)
			result.setText("Enable");
		if(icon != null)
			result.setIcon(icon);
		if(action != null)
			result.setActionCommand(action);
		
		result.setVerticalTextPosition(AbstractButton.BOTTOM);
		result.setHorizontalTextPosition(AbstractButton.CENTER);
		result.setEnabled(enabled);
		
		return result;
	}
	
	/**
	 * creates a JFormattedTextField intended for input
	 * @param label
	 * @param value
	 * @param columns
	 * @return
	 */
	private JFormattedTextField createInputField(JLabel label,
			double value, int columns) {
		JFormattedTextField result = new JFormattedTextField();
		if(label != null)
			label.setLabelFor(result);
		result.setValue(value);
		result.setColumns(10);
		
		return result;
	}
	
	/**
	 * creates a JPanel from components with a predefined layout
	 * @param north
	 * @param west
	 * @param center
	 * @param east
	 * @param south
	 * @return
	 */
	private JPanel createPanel(JComponent north, JComponent west,
			JComponent center, JComponent east, JComponent south) {
		JPanel result = new JPanel();
		if(north != null)
			result.add(north, BorderLayout.NORTH);
		if(west != null)
			result.add(west, BorderLayout.WEST);
		if(center != null)
			result.add(center, BorderLayout.CENTER);
		if(east != null)
			result.add(east, BorderLayout.EAST);
		if(south != null)
			result.add(south, BorderLayout.SOUTH);
		
		return result;
	}
	
	/**
	 * initializes the click toggle button
	 * @param icon
	 */
	private void initializeClickToggle() {
		clickToggle = createButton("Enable", null,
        		"click_enable", true);
    	clickToggle.setMnemonic(KeyEvent.VK_E);
    	clickToggle.addActionListener(this);
    	clickToggle.addKeyListener(this);
    	clicking = false;
	}
	
	/**
	 * initializes the fields and labels for the autoclicker tab
	 */
	private void initializeFieldsAndLabels() {
		clickspsLabel = new JLabel(clickspsString);
    	clickspsField = createInputField(clickspsLabel,
    			new Double(clicksps), 10);
    	
    	clickConfigLabel = new JLabel(clickConfigString);
    	clickConfigFieldX = createInputField(null, clickX, 10);
    	clickConfigFieldY = createInputField(null, clickY, 10);
	}
}
