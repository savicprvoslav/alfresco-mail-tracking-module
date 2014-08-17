package com.alfrescoblog.util;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Address;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

import com.alfrescoblog.model.AlfrescoBlogModel;
import com.alfrescoblog.webscript.Track;

/**
 * Util Class for creating Email Tracker Instance
 * 
 * @author Prvoslav
 * 
 */
public class Tracker {

	private static final String EMAIL_FOLDER = "emailFolder";

	/**
	 * Update body of the email and add img tag in the end
	 * @param cnt
	 * @param subject
	 * @param address 
	 * @param nodeLocatorService
	 * @param nodeService
	 * @param domainAddr
	 * @param permissionService
	 * @return
	 */
	public static String updateContent(String cnt, String subject,
			Address address, NodeLocatorService nodeLocatorService, NodeService nodeService,
			String domainAddr, PermissionService permissionService) {

		NodeRef userHome = nodeLocatorService.getNode("userhome", null, null);

		/**
		 * If current user does not have userHome then do not track emails
		 */
		if (userHome != null) {

			NodeRef emailFolderRef = createEmailFoder(userHome, nodeService,
					permissionService);

			String emailTrackerName = Long.toString(new Date().getTime());
			QName emailTrackerQname = QName.createQName(emailTrackerName);

			ChildAssociationRef associationTrackerRef = nodeService.createNode(
					emailFolderRef, ContentModel.ASSOC_CONTAINS,
					emailTrackerQname, AlfrescoBlogModel.TYPE_TRACKED_EMAIL);

			NodeRef trackerRef = associationTrackerRef.getChildRef();

			Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
			properties.put(AlfrescoBlogModel.PROP_OPENED, false);
			properties.put(AlfrescoBlogModel.PROP_MAILDATE, new Date());
			properties.put(AlfrescoBlogModel.PROP_SENDER,
					AuthenticationUtil.getRunAsUser());
			properties.put(AlfrescoBlogModel.PROP_RECEIVER,
					address.toString());

			properties.put(AlfrescoBlogModel.PROP_SUBJECT, subject);
			properties.put(ContentModel.PROP_NAME, emailTrackerName);
			properties.put(ContentModel.PROP_TITLE, emailTrackerName);
			properties.put(ContentModel.PROP_DESCRIPTION, subject);
			nodeService.setProperties(trackerRef, properties);

			Long id = (Long) nodeService.getProperty(trackerRef,
					ContentModel.PROP_NODE_DBID);

			cnt += "<img src='" + domainAddr + "/" + Track.URL + "?id=" + id
					+ "'/> ";

		}

		return cnt;
	}

	/**
	 * Create email tracking folder
	 * 
	 * @param userHome
	 * @param nodeService
	 * @param permissionService
	 * @return
	 */
	private static NodeRef createEmailFoder(NodeRef userHome,
			NodeService nodeService, PermissionService permissionService) {
		NodeRef emailFolderRef = nodeService.getChildByName(userHome,
				ContentModel.ASSOC_CONTAINS, EMAIL_FOLDER);

		if (emailFolderRef == null) {
			QName emailFolderQname = QName.createQName(EMAIL_FOLDER);
			ChildAssociationRef associationRef = nodeService.createNode(
					userHome, ContentModel.ASSOC_CONTAINS, emailFolderQname,
					ContentModel.TYPE_FOLDER);
			emailFolderRef = associationRef.getChildRef();

			nodeService.setProperty(emailFolderRef, ContentModel.PROP_TITLE,
					EMAIL_FOLDER);
			nodeService.setProperty(emailFolderRef, ContentModel.PROP_NAME,
					EMAIL_FOLDER);

			permissionService
					.setInheritParentPermissions(emailFolderRef, false);
		}
		return emailFolderRef;
	}

}
