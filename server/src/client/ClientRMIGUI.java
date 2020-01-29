package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;


public class ClientRMIGUI extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;	
	private JPanel textPanel, inputPanel;
	private JTextField textField;
	private String name, message;
	private Font TimesRoman = new Font("TimesRoman", Font.PLAIN, 14);
	private Border blankBorder = BorderFactory.createEmptyBorder(10,10,20,10);//top,r,b,l
	private ChatClient chatClient;
    private JList<String> list;
    private DefaultListModel<String> listModel;
    
    protected JTextArea textArea, userArea;
    protected JFrame frame;
    protected JButton privateMsgButton, sendButton;
    protected JPanel clientPanel, userPanel;
    private JPanel panel;
    private JLabel lblName;
    private JTextField textField_1;
    private JLabel lblIp;
    private JTextField textField_2;
    private JButton startButton;
    String hostname;
    String[] curses = {"Jerk", "Dyke", "Fag", "Shit", "Damn", "WTF", "STFU", "Crazy", "Prick", "Dumb", "Stupid", "Fool", "Goddamn"};
    String[] filters = {"J**k", "D**e", "F*g", "S**t", "D**n", "W*F", "S**U", "C***y", "P***k", "D**b", "S****d", "F**l", "Godd**n"};
    private JButton endButton;

	
    //Main method
	public static void main(String args[]){
		try{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
				if("Nimbus".equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch(Exception e){
			}
		new ClientRMIGUI();
		}
	
	
	/**
	 * GUI Constructor
	 */
	
	
	public ClientRMIGUI(){
			
		frame = new JFrame("Client Chat Console");	
	
		//Close method and message server to leave
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        
		    	if(chatClient != null){
			    	try {
			        	sendMessage(name + "exited");
			        	chatClient.serverIF.leaveChat(name);
					} catch (RemoteException e) {
						e.printStackTrace();
					}		        	
		        }
		        System.exit(0);  
		    }   
		});
		
		
		//remove buttons and frames
		Container c = getContentPane();
		JPanel outerPanel = new JPanel(new BorderLayout());
		
		outerPanel.add(getInputPanel(), BorderLayout.CENTER);
		outerPanel.add(getTextPanel(), BorderLayout.NORTH);
		
		c.setLayout(new BorderLayout());
		c.add(outerPanel, BorderLayout.CENTER);
		c.add(getUsersPanel(), BorderLayout.WEST);
		
		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		lblName = new JLabel("Name");
		panel.add(lblName);
		
		textField_1 = new JTextField();
		panel.add(textField_1);
		textField_1.setColumns(10);
		
		lblIp = new JLabel("IP");
		panel.add(lblIp);
		
		textField_2 = new JTextField();
		panel.add(textField_2);
		textField_2.setColumns(10);
		
		startButton = new JButton("Connect ");
		startButton.addActionListener(this);
		panel.add(startButton);
		
		endButton = new JButton("Disconnect");
		panel.add(endButton);
		endButton.addActionListener(this);
		endButton.setEnabled(false);
		
		

		frame.add(c);
		frame.pack();
		frame.setAlwaysOnTop(true);
		frame.setLocation(150, 150);
		textField.requestFocus();
	
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
	/**
	 * Method to set up the JPanel to display the chat text
	 * @return
	 */
	public JPanel getTextPanel(){
		String welcome = "Welcome enter your name and enter server IP and press Connect to begin\n";
		textArea = new JTextArea(welcome, 14, 34);
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setFont(TimesRoman);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textPanel = new JPanel();
		textPanel.add(scrollPane);
	
		textPanel.setFont(new Font("TimesRoman", Font.PLAIN, 14));
		return textPanel;
	}
	
	
	//Build input field
	public JPanel getInputPanel(){
		inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
		inputPanel.setBorder(blankBorder);	
		textField = new JTextField();
		textField.setFont(TimesRoman);
		inputPanel.add(textField);
		return inputPanel;
	}

	
	
	//Panel to display list of users online
	public JPanel getUsersPanel(){
		
		userPanel = new JPanel(new BorderLayout());
		String  userStr = " Current Users      ";
		
		JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
		userPanel.add(userLabel, BorderLayout.NORTH);	
		userLabel.setFont(new Font("TimesRoman", Font.PLAIN, 16));

		String[] noClientsYet = {"No other users"};
		setClientPanel(noClientsYet);

		clientPanel.setFont(TimesRoman);
		userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);		
		userPanel.setBorder(blankBorder);

		return userPanel;		
	}

	
	//Panel to add users online to panel
    public void setClientPanel(String[] currClients) {  	
    	clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();
        
        for(String s : currClients){
        	listModel.addElement(s);
        }
        if(currClients.length > 1){
        	privateMsgButton.setEnabled(true);
        }
        
        //Create the list and add scroll 
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(TimesRoman);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }
	
    
	//Generate buttons and add listener
	public JPanel makeButtonPanel() {		
		sendButton = new JButton("Send ");
		sendButton.addActionListener(this);
		sendButton.setEnabled(false);

        privateMsgButton = new JButton("Send PM");
        privateMsgButton.addActionListener(this);
        privateMsgButton.setEnabled(false);
		
		JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
		buttonPanel.add(privateMsgButton);
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(sendButton);

		
		return buttonPanel;
	}
	
	
	// Adding actions for buttons to perform
	public static int stringContainsItemFromList(String inputStr, String[] items)
	  {
	      for(int i =0; i < items.length; i++)
	      {
	          if(inputStr.toLowerCase().contains(items[i].toLowerCase()))
	          {
	        	  System.out.println(i);
	              return i;
	          }
	      }
	      return 100;
	  }
	
	
	@Override
	public void actionPerformed(ActionEvent e){

		try {
			//Check if start button is clicked, if it is and everything is right connect them to the service
			if(e.getSource() == startButton){
				name = textField_1.getText();
				hostname = textField_2.getText();
				if(name.length() != 0){
					if(hostname.length() != 0){
						textField_1.setText("");
						textField_2.setText("");
						int value = getConnected(name);
						
						//Check if there is no connection problem
						if(!chatClient.connectionProblem){
							frame.setTitle(name + "'s console ");
							textArea.append("Name : " + name + " is connecting now\n");	
							startButton.setEnabled(false);
							endButton.setEnabled(true);
							sendButton.setEnabled(true);
							}
						
						// Check if there is no user online with the same name 
						if(value == 1){
							JOptionPane.showMessageDialog(frame, "The user is already online. Try a different username");
							startButton.setEnabled(true);
							endButton.setEnabled(false);
							sendButton.setEnabled(false);
						}
					}
					else{
						JOptionPane.showMessageDialog(frame, "Enter IP to connect");
					}
				}
				else{
					JOptionPane.showMessageDialog(frame, "Enter your name to connect");
				}
			}
			
			
			//Check if end button is clicked, if yes and everything is right disconnect them from the service
			if(e.getSource() == endButton){
				frame.setTitle("Client's Chat console");
				endButton.setEnabled(false);
				sendButton.setEnabled(false);
				startButton.setEnabled(true);
				if(chatClient != null){
			    	try {
			        	sendMessage(name + " Exited");
			        	chatClient.serverIF.leaveChat(name);
						textArea.setText("Welcome enter your name and enter server IP and press Connect to begin\n");
					} catch (RemoteException eex) {
						eex.printStackTrace();
					}		        	
		        }
				System.exit(0);
			}
			

			// Check if send button is clicked
			// If it is, then get text from text field and send message to all
			if(e.getSource() == sendButton){
				message = textField.getText();
				textField.setText("");
				textField_1.setText("");
				textField_2.setText("");
				int value = stringContainsItemFromList(message, curses);
			      if( value != 100){
			    	  String curse_used = curses[value];
			    	  String filter_curse = filters[value];
			          message = message.replaceAll("(?i)" + curse_used,filter_curse);  
			      }
				sendMessage(message);
				System.out.println("Sending message : " + message);
			}
			
			
			// Check if send button is clicked
			// If it is, then get text from text field and send message to selected users
			if(e.getSource() == privateMsgButton){
				int[] privateList = list.getSelectedIndices();
				
				for(int i=0; i<privateList.length; i++){
					System.out.println("selected index :" + privateList[i]);
				}
				message = textField.getText();
				textField.setText("");
				sendPrivate(privateList);
			}
			
		}
		catch (RemoteException remoteExc) {			
			remoteExc.printStackTrace();	
		}
		
	}

	// --------------------------------------------------------------------
	
	// Make connection to chat server
	private int getConnected(String userName) throws RemoteException{
		
		//remove whitespace and non word characters
		String uName_cleaned = userName.replaceAll("\\s+","_");
		uName_cleaned = userName.replaceAll("\\W+","_");
		int value = 0;
		try {		
//			System.out.println("The uname is :" + uName_cleaned);
			chatClient = new ChatClient(this, uName_cleaned);
			value = chatClient.startClient(hostname);
//			c.checkUname("sachin");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
	// Method to send message to all users
	// This is called when Send is clicked
	private void sendMessage(String chatMessage) throws RemoteException {
		chatClient.serverIF.updateChat(name, chatMessage);
	}

	
	// Method to send message to selected users
	//This is called when Send PM is clicked
	private void sendPrivate(int[] privateList) throws RemoteException {
		String privateMessage = "[PM from " + name + "] :" + message + "\n";
		chatClient.serverIF.sendPM(privateList, privateMessage);
	}
	




}










