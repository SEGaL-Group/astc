package com.act.on.stc.server.artifacts;

import java.sql.Timestamp;

public class TaskContext implements IContext {

	private Task fTask;
	private Timestamp fTime;
	private String fField;
	private boolean fEdited;

	public TaskContext(Task task, Timestamp time, String field, boolean edited) {
		fTask= task;
		fTime= time;
		fField= field;
		fEdited= edited;
	}

	@Override
	public int getType() {
		return IContext.TASK_CONTEXT_TYPE;
	}

	public Task getTask() {
		return fTask;
	}

	public Timestamp getTimeStamp() {
		return fTime;
	}

	public String getField() {
		return fField;
	}

	public boolean wasEdited() {
		return fEdited;
	}

}
