package com.act.on.stc.server.artifacts;

import java.sql.Timestamp;

public class CodeContext implements IContext {
	private String fFileName;
	private String fClassName;
	private String fMethodName;
	private boolean fEdited;
	private Timestamp fTime;
	private String fHandle;

	public CodeContext(String fileName, String className, String methodName, String handle, boolean edited, Timestamp time) {
		fFileName= fileName;
		fClassName= className;
		fMethodName= methodName;
		fEdited= edited;
		fTime= time;
		fHandle= handle;
	}

	@Override
	public int getType() {
		return IContext.CODE_CONTEXT_TYPE;
	}

	public boolean wasEdited() {
		return fEdited;
	}

	public String getMethodName() {
		return fMethodName;
	}

	public String getHandle() {
		return fHandle;
	}
	
	public String getClassName() {
		return fClassName;
	}

	public String getFileName() {
		return fFileName;
	}

	public Timestamp getTimeStamp() {
		return fTime;
	}
}
