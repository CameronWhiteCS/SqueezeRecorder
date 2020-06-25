package squeeze.theorem.gui;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import squeeze.theorem.main.MouseRecorder;

public class RecorderGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 8054579792675113171L;
	
	private Label xLabel;
	private TextField x;
	
	private TextField y;
	private Label yLabel;
	
	private TextField file;
	private Label fileLabel;	
	
	private Checkbox recording;
	private Checkbox looping;
	
	private Button start;
	
	public RecorderGui() {
		this.setTitle("SqueezeRecorder v. " + MouseRecorder.VERSION);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(true);
		this.setSize(350, 250);
		GridLayout layout = new GridLayout(6, 2);
		layout.setHgap(0);
		layout.setVgap(10);
		
		this.setLayout(layout);
		
		xLabel = new Label("X: ");
		xLabel.setSize(25, 25);
		this.add(xLabel);
		
		x = new TextField();
		x.setSize(100, 25);
		this.add(x);
		
		yLabel = new Label("Y: ");
		yLabel.setSize(25, 25);
		this.add(yLabel);
		
		y = new TextField();
		y.setSize(100, 25);
		this.add(y);
		
		fileLabel = new Label("File: ");
		fileLabel.setSize(25, 25);
		this.add(fileLabel);
		
		file = new TextField();
		this.add(file);
		
		recording = new Checkbox("Recording");
		this.add(recording);
		
		looping = new Checkbox("Looping");
		looping.setSize(100, 25);
		this.add(looping);
		
		start = new Button("Start");
		start.addActionListener(this);
		this.add(start);

		
		
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		
		try {
			
			MouseRecorder recorder = new MouseRecorder()
				.setFile(new File(file.getText()))
				.setLooping(looping.getState())
				.setRecording(recording.getState())
				.setX(Integer.parseInt(x.getText()))
				.setY(Integer.parseInt(x.getText()));
			
				recorder.start();
				
				
				
		} catch(Exception exc) {
			exc.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error occurred with the provided settings.\nPlease check the console for more details and make\nsure that the provided settings are correct.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		this.dispose();
		
	}

}
