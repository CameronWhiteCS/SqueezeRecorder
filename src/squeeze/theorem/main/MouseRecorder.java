package squeeze.theorem.main;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

import squeeze.theorem.gui.RecorderGui;

public class MouseRecorder implements NativeMouseListener, NativeMouseMotionListener, NativeKeyListener  {

	public static final String VERSION = "0.1 Alpha";
	
	private Robot bot;
	private long startTime;
	private long pauseTime;
	private List<ActionEvent> actions;
	private boolean recording = false;
	File file = new File("/home/cameron/Desktop/stringing_bows.txt");
	private boolean paused = false;
	private boolean looping = true;
	
	private int lastX; //used to prevent impossible mouse movements that could be detected
	private int lastY; //also used for the pause feature
	
	int x;
	int y;
	
	/*Initialization methods*/
	public static void main(String[] args) {
		
		initializeNativeHook();
		
		if(shouldUseGui(args)) {
			new RecorderGui();
		} else {
			MouseRecorder recorder = new MouseRecorder(args);
			recorder.start();
		}

	}
	
	public static boolean shouldUseGui(String[] args) {
		boolean useGui = true;
		for(String pair: args) {
			String[] splits = pair.split("=");
			if(splits.length != 2) continue;
			String key = splits[0];
			String value = splits[1];
			if(key.equalsIgnoreCase("gui")) {
				useGui = Boolean.parseBoolean(value);
				break;
			}
		}
		return useGui;
	}
	
	private static void initializeNativeHook() {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	/*Constructors*/
	public MouseRecorder() {
		this(new String[] {});
	}
	
	public MouseRecorder(String[] args) {
		parseArgs(args);
		startTime = System.currentTimeMillis();
		try {
			bot = new Robot();
		} catch (AWTException exc) {
			exc.printStackTrace();
		}
		actions = new ArrayList<ActionEvent>();

	}
	
	/*Misc. methods*/
	private void parseArgs(String[] args) {
		for(String s: args) {
			String[] splits = s.split("=");
			if(splits.length != 2) continue;
			String key = splits[0];
			String value = splits[1];
			if(key.equalsIgnoreCase("x")) {
				x = Integer.parseInt(value);
			} else if (key.equalsIgnoreCase("y")) {
				y = Integer.parseInt(value);
			} else if(key.equalsIgnoreCase("file")) {
				file = new File(value);
			} else if(key.equalsIgnoreCase("recording")) {
				recording = Boolean.parseBoolean(value);
			} else if(key.equalsIgnoreCase("looping")) {
				looping = Boolean.parseBoolean(value);
			}
		}
	}
	
	public void start() {
		System.out.println("Sleeping for 2500ms before program starts.");
		sleep(2500);
		System.out.println("SqueezeRecorder v 0.1 started.");
		System.out.println("Client x position: " + x);
		System.out.println("Client y position: " + y);
		System.out.println("Recording file: " + file);
		System.out.println("Recording mouse movements: " + recording);
		if(recording) {
			GlobalScreen.addNativeMouseListener(this);
			GlobalScreen.addNativeMouseMotionListener(this);
			GlobalScreen.addNativeKeyListener(this);	
		} else {
			do {
				playback();
				sleep(1000);
			} while (looping);
		}
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void playback() {
		startTime = System.currentTimeMillis();
		Scanner sc;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException exc) {
			exc.printStackTrace();
			return;
		}
		while(sc.hasNextLine()) {
			String[] args = sc.nextLine().split(" ");
			ActionType type;
			int x = 0;
			int y = 0;
			long actionTime = 0;
			String keyPressed = "";
			if(args.length == 4) { //Mouse events
			
				type = ActionType.getActionType(args[0]);
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
				actionTime = Long.parseLong(args[3]);
			} else if (args.length == 3){ //Key events
				type = ActionType.getActionType(args[0]);
				keyPressed = args[1];
				actionTime = Long.parseLong(args[2]);
			} else {
				continue;
			}
			
			long currentTime = System.currentTimeMillis() - startTime;
			long sleepTime = actionTime - currentTime;
			if(sleepTime > 0) {
				sleep(sleepTime);
			}
			
			while(paused) {
				sleep(1000);
			}
			
			switch(type) {
			case MOVE:
				bot.mouseMove(getAbsoluteX(x), getAbsoluteY(y));
				break;
			case LEFT_CLICK:
				bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				break;
			case MIDDLE_CLICK:
				bot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
				break;
			case RIGHT_CLICK:
				bot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				break;
			case LEFT_RELEASE:
				bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				break;
			case MIDDLE_RELEASE:
				bot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
				break;
			case RIGHT_RELEASE:
				bot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				break;
			case KEY_PRESS:
				if(keyPressed.equalsIgnoreCase("shift")) bot.keyPress(KeyEvent.VK_SHIFT);
				break;
			case KEY_RELEASE:
				if(keyPressed.equalsIgnoreCase("shift")) bot.keyRelease(KeyEvent.VK_SHIFT);
				break;
			}
		}
		sc.close();
	}
	
	public boolean saveRecording(List<ActionEvent> actions) {

			try {
				String output = "";
				for(ActionEvent a: actions) {
					if(a.getType() != ActionType.KEY_PRESS && a.getType() != ActionType.KEY_RELEASE) {
						output += String.format("%s %s %s %s\n", a.getActionType().toString(), a.getX(), a.getY(), a.getTime());
					} else {
						output += String.format("%s %s %s\n", a.getActionType().toString(), a.getKey(), a.getTime());
					}
				}
				
				if(!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				FileWriter writer = new FileWriter(file, false);
				writer.write(output);
				writer.flush();
				writer.close();
			} catch(IOException exc) {
				exc.printStackTrace();
				return false;
			} 
			
			return true;
	}

	/*Inherited methods*/
	@Override
	public void nativeMouseClicked(NativeMouseEvent evt) {
		
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent evt) {
		if(paused) return;
		ActionType action;
		if(evt.getButton() == 1) {
			action = ActionType.LEFT_CLICK;
		} else if(evt.getButton() == 2) {
			action = ActionType.MIDDLE_CLICK;
		} else {
			action = ActionType.RIGHT_CLICK;
		}
		actions.add(new ActionEvent(action, getRelativeX(evt.getX()), getRelativeY(evt.getY()), System.currentTimeMillis() - startTime));

	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent evt) {
		if(paused) return;
		ActionType action;
		if(evt.getButton() == 1) {
			action = ActionType.LEFT_RELEASE;
		} else if(evt.getButton() == 2) {
			action = ActionType.MIDDLE_RELEASE;
		} else {
			action = ActionType.RIGHT_RELEASE;
		}
		actions.add(new ActionEvent(action, getRelativeX(evt.getX()), getRelativeY(evt.getY()), System.currentTimeMillis() - startTime));
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent evt) {
		if(paused) return;
		actions.add(new ActionEvent(ActionType.MOVE, getRelativeX(evt.getX()), getRelativeY(evt.getY()), System.currentTimeMillis() - startTime));
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent evt) {
		if(paused) return;
		actions.add(new ActionEvent(ActionType.MOVE, getRelativeX(evt.getX()), getRelativeY(evt.getY()), System.currentTimeMillis() - startTime));
	}

	private void pause() {
		paused = !paused;
		if(paused) {
			pauseTime = System.currentTimeMillis();
			System.out.println("Paused");
			lastX = (int) MouseInfo.getPointerInfo().getLocation().getX();
			lastY = (int) MouseInfo.getPointerInfo().getLocation().getY();
		} else {
			long delta = System.currentTimeMillis() - pauseTime;
			startTime += delta;
			int currentX = (int) MouseInfo.getPointerInfo().getLocation().getX();
			int currentY = (int) MouseInfo.getPointerInfo().getLocation().getY();
			if(currentX != lastX || currentY != lastY) bot.mouseMove(lastX, lastY);
			System.out.println("Resumed");
		}
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent evt) {
		String text = NativeKeyEvent.getKeyText(evt.getKeyCode());
		if(text.equals("Escape") && recording) {
			boolean success = saveRecording(actions);
				System.out.println(success ? "Recording saved." : "Could not save recording. :(");
	
			System.exit(0);
			
		} else if (text.equals("Num Lock") && recording) {
			pause();
		} else if(text.equalsIgnoreCase("shift") && recording && !paused) {
			actions.add(new ActionEvent(ActionType.KEY_PRESS, "shift", System.currentTimeMillis() - startTime));
		}
		
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent evt) {
		String text = NativeKeyEvent.getKeyText(evt.getKeyCode());
		if(text.equalsIgnoreCase("shift") && recording && !paused) {
			actions.add(new ActionEvent(ActionType.KEY_RELEASE, "shift", System.currentTimeMillis() - startTime));
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent evt) {
		
	}
	
	/*Coordinate methods*/
	private int getRelativeX(int absoluteX) {
		return absoluteX - x;
	}
	
	private int getRelativeY(int absoluteY) {
		return absoluteY - y;
	}
	
	private int getAbsoluteX(int relativeX) {
		return relativeX + x;
	}
	
	private int getAbsoluteY(int relativeY) {
		return relativeY + y;
	}
	
	/*Builder methods*/
	public MouseRecorder setX(int x) {
		this.x = x;
		return this;
	}
	
	public MouseRecorder setY(int y) {
		this.y = y;
		return this;
	}
	
	public MouseRecorder setLooping(boolean looping) {
		this.looping = looping;
		return this;
	}
	
	public MouseRecorder setFile(File file) {
		this.file = file;
		return this;
	}
	
	public MouseRecorder setRecording(boolean recording) {
		this.recording = recording;
		return this;
	}

}
