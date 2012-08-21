package com.act.on.stc.server.artifacts;

public interface IContext {
	public static final int CODE_CONTEXT_TYPE = 0;
	static final int TASK_CONTEXT_TYPE = 1;

	public int getType();
}
