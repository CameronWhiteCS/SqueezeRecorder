package squeeze.theorem.main;

public enum ActionType {

	MOVE, LEFT_CLICK, RIGHT_CLICK, MIDDLE_CLICK, LEFT_RELEASE, RIGHT_RELEASE, MIDDLE_RELEASE, KEY_PRESS, KEY_RELEASE;

	public static ActionType getActionType(String name) {
		for(ActionType a: ActionType.values()) {
			if(a.toString().equalsIgnoreCase(name)) return a;
		}
		return null;
	}
	
}
