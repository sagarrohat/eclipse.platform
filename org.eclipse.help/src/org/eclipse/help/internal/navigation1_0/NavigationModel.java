package org.eclipse.help.internal.navigation1_0;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.URL;
import java.util.*;
import org.eclipse.help.internal.*;
import org.eclipse.help.internal.contributions1_0.*;
import org.eclipse.help.internal.contributors.xml1_0.ContributionParser;
import org.eclipse.help.internal.util.*;
import org.xml.sax.*;
/**
 * Domain notifier for the help model.
 * It holds the model (views and topic elements).
 */
public class NavigationModel {
	// the map of topic url to all possible topics
	private Map urlToTopicMap = new HashMap();
	private Contribution infoset;
	private String infosetID;
	/**
	 * NavigationModel constructor.
	 */
	public NavigationModel(String infoset) {
		super();
		this.infosetID = infoset;
		load();
		registerURLs();
	}
	/**
	 * NavigationModel constructor.
	 */
	public NavigationModel(Contribution infoset) {
		super();
		this.infoset = infoset;
		registerURLs();
	}
	public Set getAllURLs() {
		return urlToTopicMap.keySet();
	}
	public Contribution getRootElement() {
		return infoset;
	}
	public Topic[] getTopicsWithURL(String url) {
		// Correct URL format first to what is recorded in the map
		int pos = url.indexOf("?");
		if (pos > 0)
			url = url.substring(0, pos);
		else {
			pos = url.indexOf("#");
			if (pos > 0)
				url = url.substring(0, pos);
		}
		// strip off the http://host:port/path
		URL helpServerURL = HelpSystem.getLocalHelpServerURL();
		if (helpServerURL != null && url.startsWith(helpServerURL.getProtocol())) {
			url = url.substring(helpServerURL.toExternalForm().length());
		} else if (url.indexOf('/') != 0)
			url = "/" + url;
		// get topics for corrected URL from the map
		return (Topic[]) urlToTopicMap.get(url);
	}
	/**
	 * Loads the xml model and creates the ui elements
	 */
	private void load() {
		String xmlFile =
			HelpPlugin
				.getDefault()
				.getStateLocation()
				.addTrailingSeparator()
				.append("nl")
				.addTrailingSeparator()
				.append(Locale.getDefault().toString())
				.append(infosetID)
				.append(HelpNavigationManager.NAV_XML_FILENAME)
				.toOSString();
		ContributionParser parser = null;
		try {
			parser = new ContributionParser();
			if (Logger.DEBUG)
				Logger.logDebugMessage("NavigationModel", "Loading _nav= " + xmlFile);
			InputStream input = new FileInputStream(xmlFile);
			InputSource source = new InputSource(input);
			// set id info for parser exceptions.
			// use toString method to capture protocol...etc
			source.setSystemId(xmlFile);
			parser.parse(source);
			infoset = parser.getContribution();
		} catch (SAXException se) {
			// create message string from exception
			//String message = parser.getMessage("E002", se);
			// Log the error. No need to populate RuntimeHelpStatus
			// because the parsing already did this.
			Logger.logError("", se);
			// now pass it to the RuntimeHelpStatus object explicitly because we
			// still need to display errors even if Logging is turned off.
			//RuntimeHelpStatus.getInstance().addParseError(message, se.getSystemId());
		} catch (Exception e) {
			// we need to populate the RuntimeHelpStatus object because this is not
			// a parse exception.
			String msg = Resources.getString("E009", xmlFile);
			Logger.logError(msg, e);
			// now pass it to the RuntimeHelpStatus object explicitly because we
			// still need to display errors even if Logging is turned off.
			RuntimeHelpStatus.getInstance().addParseError(msg, xmlFile);
		}
	}
	/**
	 * Registers an element with the model
	 */
	private void registerURL(Topic topic) {
		// if it is a topic, map it to its url;
		String url = topic.getHref();
		if (url == null || "".equals(url))
			return;
		// see if mapped already
		Object t = urlToTopicMap.get(url);
		Topic[] topics;
		if (t == null) {
			topics = new Topic[1];
			topics[0] = topic;
		} else {
			topics = new Topic[((Topic[]) t).length + 1];
			for (int i = 0; i < ((Topic[]) t).length; i++) {
				topics[i] = ((Topic[]) t)[i];
			}
			topics[((Topic[]) t).length] = topic;
		}
		urlToTopicMap.put(url, topics);
	}
	/**
	 * Register all the topics by url
	 */
	private void registerURLs() {
		if (infoset == null)
			return;
		// Get the views
		for (Iterator views = infoset.getChildren(); views.hasNext();) {
			Contribution view = (Contribution) views.next();
			Stack stack = new Stack();
			stack.push(view.getChildren());
			while (!stack.isEmpty()) {
				Iterator topics = (Iterator) stack.pop();
				while (topics.hasNext()) {
					Topic topic = (Topic) topics.next();
					registerURL(topic);
					Iterator subtopics = topic.getChildren();
					if (subtopics.hasNext())
						stack.push(subtopics);
				}
			}
		}
	}
}