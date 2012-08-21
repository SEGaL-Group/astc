package com.act.on.stc.server;

import java.util.List;

import com.act.on.stc.common.utilities.Pair;
import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.User;

public interface IContextDataMgr { 
	public void storeContextData(CodeContext ctx, User u, Task wi);
	public List<Pair<Task,Double>> getProximityData(Task wi);
	public List<Pair<User,Double>> getProximityData(User u);
	public List<Pair<CodeContext,Double>> getProximityData(CodeContext ctx);
}
