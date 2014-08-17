package com.alfrescoblog.service;

import java.io.IOException;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.alfresco.repo.mail.AlfrescoJavaMailSender;
import org.alfresco.repo.nodelocator.NodeLocatorService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.apache.log4j.Logger;
import org.springframework.mail.MailException;

import com.alfrescoblog.util.Tracker;

/**
 * Class that overrides @AlfrescoJavaMailSender
 * 
 * @author Prvoslav
 *
 */
public class ABAlfrescoJavaMailSender extends AlfrescoJavaMailSender {

	private static final Logger logger = Logger
			.getLogger(ABAlfrescoJavaMailSender.class);

	private static final String CONTENT_TYPE_HTML = "text/html";

	String domainAddr;

	public void setDomainAddr(String domainAddr) {
		this.domainAddr = domainAddr;
	}

	private NodeService nodeService;

	private NodeLocatorService nodeLocatorService;

	private PermissionService permissionService;

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setNodeLocatorService(NodeLocatorService nodeLocatorService) {
		this.nodeLocatorService = nodeLocatorService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public void send(MimeMessage mimeMessage) throws MailException {
		modifyBody(mimeMessage);
		
		super.send(mimeMessage);
	}

	@Override
	public void send(MimeMessage[] mimeMessages) throws MailException {
		for (MimeMessage mm : mimeMessages) {
			modifyBody(mm);
		}
		super.send(mimeMessages);
	}

	public void modifyBody(MimeMessage mimeMessage) {
		try {
			
			Address[] addresses =  mimeMessage.getAllRecipients();
			Address address = null;
			if(addresses !=null)
			{
				if(addresses.length>0)
				{
					address=addresses[0];
				}
			}
			
			String subject = mimeMessage.getSubject();

			Object content = mimeMessage.getContent();
			if (content.getClass().isAssignableFrom(MimeMultipart.class)) {
				MimeMultipart mimeMultipart = (MimeMultipart) content;

				for (int i = 0; i < mimeMultipart.getCount(); i++) {

					BodyPart bodyPart = mimeMultipart.getBodyPart(i);

					if (bodyPart.getContentType().startsWith("text/plain")) {
						String cnt = Tracker.updateContent(
								(String) bodyPart.getContent(), subject,address,
								nodeLocatorService, nodeService, domainAddr,
								permissionService);

						bodyPart.setContent(cnt, CONTENT_TYPE_HTML);

					} else if (bodyPart.getContentType()
							.startsWith("text/html")) {
						String cnt = Tracker.updateContent(
								(String) bodyPart.getContent(), subject,address,
								nodeLocatorService, nodeService, domainAddr,
								permissionService);

						bodyPart.setContent(cnt, CONTENT_TYPE_HTML);
					}

				}
			} else {
				String cnt = Tracker.updateContent(
						(String) mimeMessage.getContent(), subject,address,
						nodeLocatorService, nodeService, domainAddr,
						permissionService);

				mimeMessage.setContent(cnt, CONTENT_TYPE_HTML);
			}

			mimeMessage.saveChanges();

		} catch (MessagingException e) {
			logger.error(e);

		} catch (IOException e) {

			logger.error(e);
		}

	}

}