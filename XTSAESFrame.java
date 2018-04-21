import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.OpenType;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class XTSAESFrame extends JFrame implements ActionListener{

	private static final int INPUT_MAKS = 20;
	private static final int WIDTH_CANVAS = 600;
	private static final int HEIGHT_CANVAS = 600;
	private JPanel mainPanel;
	private JPanel bottomPanel;
	private JLabel sourceJL, keyJL, outputJL;
	private JTextField sourceJTF, keyJTF, outputJTF;
	private JButton decryptButton, encryptButton, keyBrowseButton, sourceBrowseButton, outputBrowseButton;
	private JFileChooser selectedPath;
	private String sourcePath, keyPath, outputPath;
	private Controller controller;
		
	public XTSAESFrame(){
		setDefaultCloseOperation( EXIT_ON_CLOSE );
        setLayout(null);
        setSize(650, 600);
        setBounds(0,0,650,600);
        setLocationRelativeTo(null);
        setResizable(false);;
		createTextField();
		createLabel();
		createButton();
		createPanel();
		add(mainPanel);
		add(bottomPanel);
		setVisible(true);
	}

	private void createTextField() {
		sourceJTF = new JTextField(INPUT_MAKS);
        sourceJTF.setLocation(145,70);
        sourceJTF.setSize(250,40);
		keyJTF = new JTextField(INPUT_MAKS);
        keyJTF.setLocation(145,130);
        keyJTF.setSize(250,40);
		outputJTF = new JTextField(INPUT_MAKS);
        outputJTF.setLocation(145,190);
        outputJTF.setSize(250,40);
	}
	
	private void createLabel(){
        sourceJL = new JLabel("Source:");
        sourceJL.setHorizontalAlignment(SwingConstants.LEFT);
        sourceJL.setSize(120,20);
        sourceJL.setFont(new Font("Times New Roman",0,20));
        sourceJL.setHorizontalTextPosition(SwingConstants.CENTER);
        sourceJL.setVerticalTextPosition(SwingConstants.CENTER);
        sourceJL.setLocation(35,75);
        
        keyJL = new JLabel("Key:");
        keyJL.setHorizontalAlignment(SwingConstants.LEFT);
        keyJL.setSize(120,20);
        keyJL.setFont(new Font("Times New Roman",0,20));
        keyJL.setHorizontalTextPosition(SwingConstants.CENTER);
        keyJL.setVerticalTextPosition(SwingConstants.CENTER);
        keyJL.setLocation(35,135);
        
        outputJL = new JLabel("Target:");
        outputJL.setHorizontalAlignment(SwingConstants.LEFT);
        outputJL.setSize(120,20);
        outputJL.setFont(new Font("Times New Roman",0,20));
        outputJL.setHorizontalTextPosition(SwingConstants.CENTER);
        outputJL.setVerticalTextPosition(SwingConstants.CENTER);
        outputJL.setLocation(35,195);
        
	}
	
	private void createButton(){
		encryptButton = new JButton("Enkripsi");
		encryptButton.setSize(120, 45);
		encryptButton.setLocation(25,25);
		encryptButton.addActionListener(this);
		
		decryptButton = new JButton("Dekripsi");
		decryptButton.setSize(120, 45);
		decryptButton.setLocation(235,25);
		decryptButton.addActionListener(this);
		
		sourceBrowseButton = new JButton("Browse");
		sourceBrowseButton.setSize(100, 35);
		sourceBrowseButton.setLocation(425,75);
		sourceBrowseButton.addActionListener(this);
		
		keyBrowseButton = new JButton("Browse");
		keyBrowseButton.setSize(100, 35);
		keyBrowseButton.setLocation(425,135);
		keyBrowseButton.addActionListener(this);
		
		outputBrowseButton = new JButton("Browse");
		outputBrowseButton.setSize(100, 35);
		outputBrowseButton.setLocation(425,195);
		outputBrowseButton.addActionListener(this);
	}

	private void createPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        mainPanel.setLocation(30,100);
        mainPanel.setSize(590,300);
        mainPanel.add(sourceJL);
		mainPanel.add(sourceJTF);
		mainPanel.add(keyJL);
		mainPanel.add(keyJTF);
		mainPanel.add(outputJL);
		mainPanel.add(outputJTF);
		mainPanel.add(sourceBrowseButton);
		mainPanel.add(keyBrowseButton);
		mainPanel.add(outputBrowseButton);
		
		bottomPanel = new JPanel();
		bottomPanel.setLayout(null);
		bottomPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		bottomPanel.setLocation(125,450);
		bottomPanel.setSize(385,100);
		bottomPanel.add(encryptButton);
		bottomPanel.add(decryptButton);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sourceBrowseButton){
			sourcePath = openFileChooser();
			sourceJTF.setText(sourcePath);
		}else if(e.getSource() == keyBrowseButton){
			keyPath = openFileChooser();
			keyJTF.setText(keyPath);
		}else if(e.getSource() == outputBrowseButton){
			outputPath = openFileChooser();
			outputJTF.setText(outputPath);
		}else if(e.getSource() == encryptButton){
			
//			System.out.println(sourcePath +" "+ keyPath +" "+ outputPath);
			
			// do encrypt
			controller = new Controller(sourcePath, keyPath, outputPath);
			try {
				controller.encrypt();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(e.getSource() == decryptButton){
			
//			System.out.println(sourcePath +" "+ keyPath +" "+ outputPath);
			
			// do decrypt
			controller = new Controller(sourcePath, keyPath, outputPath);
			try {
				controller.decrypt();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public String openFileChooser(){
		selectedPath = new JFileChooser();
		int result = selectedPath.showOpenDialog(null);
        if( result == JFileChooser.APPROVE_OPTION) {
            return selectedPath.getSelectedFile().getPath();
        } else if ( result ==  JFileChooser.CANCEL_OPTION) {
            return null;
        }else{
        	return null;
        }
	}
	
	
	
}
