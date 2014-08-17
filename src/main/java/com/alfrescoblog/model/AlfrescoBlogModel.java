package com.alfrescoblog.model;

import org.alfresco.service.namespace.QName;

/**
 * 
 * 
 * @author Prvoslav
 *
 */
public class AlfrescoBlogModel {

	public static final String APP_MODEL_1_0_URI = "http://www.alfrescoblog.com/model/data/1.0";
	public static final String APP_SHORT = "ab";



	public static final QName TYPE_TRACKED_EMAIL = QName.createQName(
			APP_MODEL_1_0_URI, "emailTracked");

	public static final QName PROP_SUBJECT = QName.createQName(
			APP_MODEL_1_0_URI, "subject");
	public static final QName PROP_MAILDATE = QName.createQName(
			APP_MODEL_1_0_URI, "mailDate");

	public static final QName PROP_SENDER = QName.createQName(
			APP_MODEL_1_0_URI, "sender");

	public static final QName PROP_RECEIVER = QName.createQName(
			APP_MODEL_1_0_URI, "receiver");

	public static final QName PROP_OPENED = QName.createQName(
			APP_MODEL_1_0_URI, "opened");

	public static final QName PROP_OPENED_DATE = QName.createQName(
			APP_MODEL_1_0_URI, "openedDate");
	
}
