package com.act.on.stc.server.artifacts;

public class User {
	private String fUuid;
	private String fUserName;

	public User(String userName, String uuid) {
		fUserName= userName;
		fUuid= uuid;
	}

	public String getUserName() {
		return fUserName;
	}

	public String getUUID() {
		return fUuid;
	}

}
