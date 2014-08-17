package com.alfrescoblog.webscript;

import java.io.IOException;
import java.util.Date;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.alfrescoblog.model.AlfrescoBlogModel;


/**
 * Webscript that outputs gif 1x1px and marks this email as read
 * 
 * @author Prvoslav
 *
 */
public class Track extends AbstractWebScript {

	public static final String URL = "track";

	private static final byte[] GIF = { 71, 73, 70, 56, 57, 97, 1, 0, 1, 0,
			-16, 0, 0, 0, 0, 0, 0, 0, 0, 33, -7, 4, 1, 0, 0, 0, 0, 44, 0, 0, 0,
			0, 1, 0, 1, 0, 0, 2, 2, 68, 1, 0, 59 };

	private org.alfresco.service.ServiceRegistry serviceRegistry;

	public void setServiceRegistry(
			org.alfresco.service.ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void execute(WebScriptRequest req, final WebScriptResponse res)
			throws IOException {

		final String id = req.getParameter("id");

		AuthenticationUtil.runAs(new RunAsWork<Void>() {

			@Override
			public Void doWork() throws Exception {
				SearchParameters sp = new SearchParameters();
				sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
				sp.setLanguage(SearchService.LANGUAGE_LUCENE);

				String queryLucene = "@sys\\:node-dbid:" + id;
				sp.setQuery(queryLucene);
				org.alfresco.service.cmr.search.ResultSet results = null;
				try {
					results = serviceRegistry.getSearchService().query(sp);
					for (ResultSetRow row : results) {
						serviceRegistry.getNodeService().setProperty(
								row.getNodeRef(),
								AlfrescoBlogModel.PROP_OPENED, true);

						serviceRegistry.getNodeService().setProperty(
								row.getNodeRef(),
								AlfrescoBlogModel.PROP_OPENED_DATE, new Date());
					}
				} finally {
					if (results != null) {
						results.close();
					}
				}

				res.setContentType("image/gif");
				res.setHeader("Content-Type", "image/gif");
				res.setHeader("Cache-Control",
						"no-cache,no-store,must-revalidate");
				res.setHeader("Pragma", "no-cache");

				res.getOutputStream().write(GIF);
				return null;
			}

		}, AuthenticationUtil.getSystemUserName());

	}

}
