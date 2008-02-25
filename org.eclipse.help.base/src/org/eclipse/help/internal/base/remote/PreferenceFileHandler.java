/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.base.remote;

import java.util.ArrayList;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.help.internal.base.HelpBasePlugin;
import org.eclipse.help.internal.base.HelpBaseResources;
import org.eclipse.help.internal.base.IHelpBaseConstants;



public class PreferenceFileHandler {

	private String[] nameEntries, hostEntries, pathEntries, portEntries, isICEnabled = null,
			isICContributed = null;

	private String namePreference, hostPreference, pathPreference, portPreference, icEnabledPreference,
			icContributedPreference;

	private int numEntries = 0, numHostEntries=0;

	private static String PREFERENCE_ENTRY_DELIMITER = ","; //$NON-NLS-1$

	public PreferenceFileHandler() {

		/*
		 * Preference values are currently comma separated
		 */

		// TODO: Decide if comma is a good delimiter, or if we should use a different delimiter.
		Preferences prefs = HelpBasePlugin.getDefault().getPluginPreferences();

		namePreference = prefs.getString(IHelpBaseConstants.P_KEY_REMOTE_HELP_NAME);
		hostPreference = prefs.getString(IHelpBaseConstants.P_KEY_REMOTE_HELP_HOST);
		pathPreference = prefs.getString(IHelpBaseConstants.P_KEY_REMOTE_HELP_PATH);
		portPreference = prefs.getString(IHelpBaseConstants.P_KEY_REMOTE_HELP_PORT);
		icEnabledPreference = prefs.getString(IHelpBaseConstants.P_KEY_REMOTE_HELP_ICEnabled);
		icContributedPreference = prefs.getString(IHelpBaseConstants.P_KEY_REMOTE_HELP_ICContributed);

		//Get host array first, and initialize values
		if(hostPreference.length()==0)
		{
			this.hostEntries=new String[0];
			numHostEntries=0;
		}
		else
		{
			this.hostEntries = hostPreference.split(PREFERENCE_ENTRY_DELIMITER);
			numHostEntries=hostEntries.length;
		}
			
		// Get the preference values
		this.nameEntries = getValues(namePreference, HelpBaseResources.PreferenceNameDefault);
		this.pathEntries = getValues(pathPreference, "/"); //$NON-NLS-1$
		this.portEntries = getValues(portPreference, "80"); //$NON-NLS-1$
		this.isICEnabled = getValues(icEnabledPreference, "true"); //$NON-NLS-1$
		this.isICContributed = getValues(icContributedPreference, "false"); //$NON-NLS-1$

		// The size of any of the array elements should equal the number of remote infocenters
		if (this.nameEntries == null)
			this.numEntries = 0;
		else
			this.numEntries = this.nameEntries.length;

	}

	private String[] getValues(String preferenceEntry, String appendString) {

		if (numHostEntries==0) //preference equals ""
			return  new String[0];//NEW
		
		// Split the string and return an array of Strings
		String [] currEntries=preferenceEntry.split(PREFERENCE_ENTRY_DELIMITER);
		String [] updatedArray=null;
		
		if(currEntries.length!=numHostEntries) //Current Entry not equals to Hosts
		{
			int i;
			
			updatedArray=new String[numHostEntries];
						
			if(currEntries.length>numHostEntries) //More in this array then host.  Only take values for # of hosts
			{
				for(i=0;i<numHostEntries;i++)
				{
					updatedArray[i]=currEntries[i];
				}
							
			}
			else //Less values.  Append values based off or array types
			{
				int entryCount=0;
				
				for(i=0;i<currEntries.length;i++)
				{
					updatedArray[i]=currEntries[i];
					entryCount=entryCount+1;
				}
				
				for(i=entryCount;i<numHostEntries;i++)
				{
					updatedArray[i]=appendString;
				}
			}
			currEntries=updatedArray;
		}
			
		return currEntries;

	}

	/**
	 * This methods writes the remote infocenters in the table model to the preferences.ini.
	 * 
	 * @param List
	 *            of RemoteIC Objects
	 * 
	 */
	public static void commitRemoteICs(RemoteIC[] remoteICs) {

		RemoteIC remote_ic = null;
		String name = "", host = "", path = "", port = "", enabledString = "", contributedString = ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		boolean enabled, contributed;

		int numICs = remoteICs.length;

		if (numICs > 0) {

			remote_ic = remoteICs[0];
			name = remote_ic.getName();
			host = remote_ic.getHost();
			path = remote_ic.getPath();
			port = remote_ic.getPort();
			enabled = remote_ic.isEnabled();
			enabledString = enabled + ""; //$NON-NLS-1$
			contributed = remote_ic.isContributed();
			contributedString = contributed + ""; //$NON-NLS-1$

			for (int i = 1; i < numICs; i++) {
				remote_ic = remoteICs[i];
				name = name + PREFERENCE_ENTRY_DELIMITER + remote_ic.getName();
				host = host + PREFERENCE_ENTRY_DELIMITER + remote_ic.getHost();
				path = path + PREFERENCE_ENTRY_DELIMITER + remote_ic.getPath();
				port = port + PREFERENCE_ENTRY_DELIMITER + remote_ic.getPort();
				enabledString = enabledString + PREFERENCE_ENTRY_DELIMITER + remote_ic.isEnabled();
				contributedString = contributedString + PREFERENCE_ENTRY_DELIMITER
						+ remote_ic.isContributed();
			}

		}

		// Save new strings to preferences
		Preferences prefs = HelpBasePlugin.getDefault().getPluginPreferences();

		prefs.setValue(IHelpBaseConstants.P_KEY_REMOTE_HELP_NAME, name);
		prefs.setValue(IHelpBaseConstants.P_KEY_REMOTE_HELP_HOST, host);
		prefs.setValue(IHelpBaseConstants.P_KEY_REMOTE_HELP_PATH, path);
		prefs.setValue(IHelpBaseConstants.P_KEY_REMOTE_HELP_PORT, port);
		prefs.setValue(IHelpBaseConstants.P_KEY_REMOTE_HELP_ICEnabled, enabledString);
		prefs.setValue(IHelpBaseConstants.P_KEY_REMOTE_HELP_ICContributed, contributedString);

		HelpBasePlugin.getDefault().savePluginPreferences();

	}

	/**
	 * 
	 * This method returns an ArrayList containing all RemoteIC entries in the preferences
	 * 
	 */
	public ArrayList getRemoteICList() {
		ArrayList remoteICList = new ArrayList();

		// Load the preferences in org.eclipse.help.base/preferences.ini
		RemoteIC initRemoteIC;
		int totalICs = this.getTotalRemoteInfocenters();
		String host, name, path, port, enabledDisabled, isContributed;
		boolean currEnabled, currContributed;

		for (int i = 0; i < totalICs; i++) {

			host = (this.getHostEntries())[i];
			name = (this.getNameEntries())[i];
			path = (this.getPathEntries())[i];
			port = (this.getPortEntries())[i];
			enabledDisabled = (this.getEnabledEntries())[i];
			if (enabledDisabled.equalsIgnoreCase("true")) //$NON-NLS-1$
			{
				currEnabled = true;
			} else {
				currEnabled = false;
			}

			isContributed = (this.getContributedEntries())[i];
			if (isContributed.equalsIgnoreCase("true")) //$NON-NLS-1$
			{
				currContributed = true;


			} else {
				currContributed = false;
			}


			// Add preferences to the model but only when contributed
			initRemoteIC = new RemoteIC(currEnabled, name, host, path, port, currContributed);
			remoteICList.add(initRemoteIC);

		}

		return remoteICList;

	}

	/**
	 * 
	 * This method returns an ArrayList of the ICs contributed to the preferences by external
	 * plugins
	 */

	public ArrayList getContributedICs() {
		ArrayList contributedICList = new ArrayList();

		RemoteIC initRemoteIC;
		int totalICs = this.getTotalRemoteInfocenters();
		String host, name, path, port, enabledDisabled, isContributed;
		boolean currEnabled, currContributed;

		for (int i = 0; i < totalICs; i++) {

			host = (this.getHostEntries())[i];
			name = (this.getNameEntries())[i];
			path = (this.getPathEntries())[i];
			port = (this.getPortEntries())[i];
			enabledDisabled = (this.getEnabledEntries())[i];
			if (enabledDisabled.equalsIgnoreCase("true")) //$NON-NLS-1$
			{
				currEnabled = true;
			} else {
				currEnabled = false;
			}

			isContributed = (this.getContributedEntries())[i];
			if (isContributed.equalsIgnoreCase("true")) //$NON-NLS-1$
			{
				currContributed = true;

				// Add preferences to the model but only when contributed

				initRemoteIC = new RemoteIC(currEnabled, name, host, path, port, currContributed);
				contributedICList.add(initRemoteIC);
			}

		}

		return contributedICList;
	}

	public String[] getHostEntries() {
		return hostEntries;
	}

	public String[] getNameEntries() {
		return nameEntries;
	}

	public String[] getPathEntries() {
		return pathEntries;
	}

	public String[] getPortEntries() {
		return portEntries;
	}

	public String[] getEnabledEntries() {
		return isICEnabled;
	}

	public String[] getContributedEntries() {
		return isICContributed;
	}

	public int getTotalRemoteInfocenters() {
		return numEntries;
	}

	public String[] isEnabled() {
		return isICEnabled;
	}

	public String getDelimeter() {
		return PREFERENCE_ENTRY_DELIMITER;
	}

}
