package squeeze.theorem.main;

public class ActionEvent {

	private ActionType actionType;
	private long time;

	private int x;
	private int y;
	
	private String key;
	
	public ActionEvent(ActionType type, String key, long time) {
		try {
			if(type != ActionType.KEY_PRESS && type != ActionType.KEY_RELEASE) {
				throw new Exception("An action event must have an x and y coordinate unless it is of type KEY_PRESS or KEY_RELEASE.");
			}
		} catch(Exception exc) {
			exc.printStackTrace();
		}
		
		this.actionType = type;
		this.key = key;
		this.time = time;
	}
	
	public ActionEvent(ActionType actionType, int x, int y, long time) {
		this.actionType = actionType;
		this.x = x;
		this.y = y;
		this.time = time;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public long getTime() {
		return time;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public ActionType getType() {
		return this.actionType;
	}
	
	public String getKey() {
		return this.key;
	}
	
}
