package com.act.on.stc.server.proximity;

import java.util.List;

import com.act.on.stc.common.utilities.Pair;
import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.User;

public interface IEgoProximity {

	public List<Pair<CodeContext, Double>> computeProximity(CodeContext ctx);

	public List<ProximityDescriptor> computeProximity(Task wi);

	public List<Pair<User, Double>> computeProximity(User u);

}
