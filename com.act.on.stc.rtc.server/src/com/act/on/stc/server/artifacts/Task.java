package com.act.on.stc.server.artifacts;

public class Task {
	private int fTaskId;
	private String fUri;
	private String fUuid;

	public Task(String uuid, String uri, int taskId) {
		fUuid= uuid;
		fUri= uri;
		fTaskId= taskId;
	}

	public String getUUID() {
		return fUuid;
	}

	public int getId() {
		return fTaskId;
	}

	public String getUri() {
		return fUri;
	}

}
