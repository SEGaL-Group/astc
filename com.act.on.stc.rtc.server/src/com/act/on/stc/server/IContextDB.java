package com.act.on.stc.server;

import java.util.Date;
import java.util.List;

import com.act.on.stc.server.artifacts.CodeContext;
import com.act.on.stc.server.artifacts.Task;
import com.act.on.stc.server.artifacts.User;

/*
 * 
 */
public interface IContextDB {
	/*
	 * works for full dump as well as delta updates
	 */
	public void storeContext(CodeContext ctx, User u, Task wi);
	
	public CodeContext retrieveContext(String contextID);
	public User getUserForContext (String contextID);
	public Task getWorkItemForContext (String contextID);
	
	/*
	 * date D can be null
	 */
	public List<CodeContext> retrieveContexts(User u, Date d);
	public List<CodeContext> retrieveContexts(Task wi, Date d);
	public List<CodeContext> retrieveContexts(String artifactID, Date d);
	
}
