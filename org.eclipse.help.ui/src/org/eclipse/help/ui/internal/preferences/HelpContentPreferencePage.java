/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.ui.internal.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.help.internal.base.HelpBasePlugin;
import org.eclipse.help.internal.base.IHelpBaseConstants;
import org.eclipse.help.internal.base.remote.PreferenceFileHandler;
import org.eclipse.help.internal.base.remote.RemoteHelp;
import org.eclipse.help.internal.base.remote.RemoteIC;
import org.eclipse.help.ui.internal.IHelpUIConstants;
import org.eclipse.help.ui.internal.Messages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Preference page to set remote infocenters
 */
public class HelpContentPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private InfocenterDisplay remoteICPage;
	Preferences prefs;

	private Button checkbox;

	/**
	 * Creates the preference page
	 */
	public HelpContentPreferencePage() {

		prefs = HelpBasePlugin.getDefault().getPluginPreferences();

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				IHelpUIConstants.PREF_PAGE_HELP_CONTENT);

		initializeDialogUnits(parent);

		createCheckbox(parent);

		TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayout(new TabFolderLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		folder.setFont(parent.getFont());

		remoteICPage = new InfocenterDisplay(this);

		remoteICPage.createTabItem(folder);
		
		initializeTableEnablement(checkbox.getSelection());

		return folder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		super.performDefaults();

		
		RestoreDefaultsDialog dialog = new RestoreDefaultsDialog((this.getControl().getShell()));
		  
		 if (dialog.open() == Window.OK) { // Restore Defaults functionality here
		
				HelpContentBlock currentBlock=remoteICPage.getHelpContentBlock();
				currentBlock.getRemoteICviewer().getRemoteICList().removeAllRemoteICs(currentBlock.getRemoteICList());
				currentBlock.restoreDefaultButtons();

		 }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {

		HelpContentBlock currentBlock;
		RemoteIC[] currentRemoteICArray;
		
		prefs = HelpBasePlugin.getDefault().getPluginPreferences();
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.preference.PreferencePage#performOk()
		 */
		prefs.setValue(IHelpBaseConstants.P_KEY_REMOTE_HELP_ON, checkbox.getSelection());
	
		currentBlock=remoteICPage.getHelpContentBlock();
		currentRemoteICArray=currentBlock.getRemoteICList();
     	PreferenceFileHandler.commitRemoteICs(currentRemoteICArray);
    	HelpBasePlugin.getDefault().savePluginPreferences();
		
    	RemoteHelp.notifyPreferenceChange();
		return super.performOk();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#setButtonLayoutData(org.eclipse.swt.widgets.Button)
	 */
	protected GridData setButtonLayoutData(Button button) {
		return super.setButtonLayoutData(button);
	}

	private void createCheckbox(Composite parent) {
		checkbox = new Button(parent, SWT.CHECK);
		checkbox.setText(Messages.HelpContentPreferencePage_remote);
		checkbox.addListener(SWT.Selection, changeListener);

		boolean isOn = prefs.getBoolean(IHelpBaseConstants.P_KEY_REMOTE_HELP_ON);
		checkbox.setSelection(isOn);
	
	}
	
	/*
	 * Initialize the table enablement with the current checkbox selection 
	 */
	
	private void initializeTableEnablement(boolean isRemoteHelpEnabled)
	{
		
		HelpContentBlock currentBlock=remoteICPage.getHelpContentBlock();
		
		if(isRemoteHelpEnabled)
			currentBlock.restoreDefaultButtons();
		else
			currentBlock.disableAllButtons();
			
		// Disable/Enable table
		currentBlock.getRemoteICviewer().getTable().setEnabled(isRemoteHelpEnabled);
	}

	/*
	 * Listens for any change in the UI and checks for valid input and correct
	 * enablement.
	 */
	private Listener changeListener = new Listener() {
		public void handleEvent(Event event) {

			HelpContentBlock currentBlock=remoteICPage.getHelpContentBlock();			
			
			boolean isRemoteHelpEnabled=checkbox.getSelection();
			//  Disable/Enable buttons
			if(isRemoteHelpEnabled)
				currentBlock.restoreDefaultButtons();
			else
				currentBlock.disableAllButtons();
				
			// Disable/Enable table
			currentBlock.getRemoteICviewer().getTable().setEnabled(isRemoteHelpEnabled);
			
			
			
		}
	};

}
