package com.act.on.stc.server.proximity;


import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.User;

public interface IPairProximity  {
	ProximityDescriptor computeProximity(User u1, User u2);
	ProximityDescriptor computeProximity(Task wi1, Task wi2);
	ProximityDescriptor computeProximity(CodeContext ctx1, CodeContext ctx2);
}
