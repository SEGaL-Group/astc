package com.act.on.stc.rtc.common.rtc;

import java.net.URI;

import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.ItemNotFoundException;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.workitem.client.IAuditableClient;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import com.ibm.team.workitem.rcp.core.ClientModel;

public class RTCWrapper {

	public static IContributor getUser() {
		if (TeamPlatform.isStarted() && TeamPlatform.getTeamRepositoryService().getTeamRepositories().length > 0)
			return TeamPlatform.getTeamRepositoryService().getTeamRepositories()[0].loggedInContributor();
		return null;
	}

	public static IWorkItem getCurrentWorkItem() throws TeamRepositoryException {
		if (TeamPlatform.isStarted() && TeamPlatform.getTeamRepositoryService().getTeamRepositories().length > 0) {
			IWorkItemHandle wih= ClientModel.getWorkItemActivationManager().getActiveWorkItem();
			
			if (wih == null) return null;
			
			for (ITeamRepository repo : TeamPlatform.getTeamRepositoryService().getTeamRepositories()) {
				IAuditableClient ac= (IAuditableClient) repo.getClientLibrary(IAuditableClient.class);
				IWorkItem wi= (IWorkItem) ac.resolveAuditable(wih, IWorkItem.SMALL_PROFILE, null);
				if (wi != null) {
					return wi;
				}
			}
		}
		return null;
	}

	public static UUID getCurrentWorkItemUUID() {
		IWorkItemHandle wih= ClientModel.getWorkItemActivationManager().getActiveWorkItem();
		if (wih == null) return null;
		return wih.getItemId();
	}

	public static URI getCurrentWorkItemURI() throws TeamRepositoryException {
		if (TeamPlatform.isStarted() && TeamPlatform.getTeamRepositoryService().getTeamRepositories().length > 0) {
			IWorkItemHandle wih= ClientModel.getWorkItemActivationManager().getActiveWorkItem();

			if (wih == null) return null;
			
			for (ITeamRepository repo : TeamPlatform.getTeamRepositoryService().getTeamRepositories()) {
				IAuditableClient ac= (IAuditableClient) repo.getClientLibrary(IAuditableClient.class);
				IWorkItem wi= (IWorkItem) ac.resolveAuditable(wih, IWorkItem.SMALL_PROFILE, null);
				if (wi != null) {
					return URI.create(repo.getRepositoryURI() + "resource/itemName/com.ibm.team.workitem.WorkItem/" + wi.getId());
				}
			}
		}
		return null;
	}

	public static boolean hasCurrentWorkItem() {
		if (TeamPlatform.isStarted() && TeamPlatform.getTeamRepositoryService().getTeamRepositories().length > 0) {
			return ClientModel.getWorkItemActivationManager().getActiveWorkItem() != null;
		}
		return false;
	}


	public static IWorkItem getWorkItem(UUID itemId) throws TeamRepositoryException {
		if (TeamPlatform.isStarted() && TeamPlatform.getTeamRepositoryService().getTeamRepositories().length > 0) {
			for (ITeamRepository repo : TeamPlatform.getTeamRepositoryService().getTeamRepositories()) {
				IWorkItemHandle wih= (IWorkItemHandle) IWorkItem.ITEM_TYPE.createItemHandle(itemId, null);
				
				if (wih == null) return null;
				
				IAuditableClient ac= (IAuditableClient) repo.getClientLibrary(IAuditableClient.class);
				IWorkItem wi= (IWorkItem) ac.resolveAuditable(wih, IWorkItem.SMALL_PROFILE, null);
				if (wi != null) {
					return wi;
				}
			}
		}
		return null;
	}

	public static URI getWorkItemURI(UUID itemId) throws TeamRepositoryException {
		if (TeamPlatform.isStarted() && TeamPlatform.getTeamRepositoryService().getTeamRepositories().length > 0) {
			for (ITeamRepository repo : TeamPlatform.getTeamRepositoryService().getTeamRepositories()) {
				IWorkItemHandle wih= (IWorkItemHandle) IWorkItem.ITEM_TYPE.createItemHandle(itemId, null);
				IAuditableClient ac= (IAuditableClient) repo.getClientLibrary(IAuditableClient.class);
				try {
					IWorkItem wi= (IWorkItem) ac.resolveAuditable(wih, IWorkItem.SMALL_PROFILE, null);
					if (wi != null) {
						return URI.create(repo.getRepositoryURI() + "resource/itemName/com.ibm.team.workitem.WorkItem/" + wi.getId());
					}
				} catch (ItemNotFoundException e) {

				}
			}
		}
		return null;
	}

	public static boolean hasWorkItem(UUID workItemUUID) throws TeamRepositoryException {
		return getWorkItemURI(workItemUUID) != null;
	}
}
