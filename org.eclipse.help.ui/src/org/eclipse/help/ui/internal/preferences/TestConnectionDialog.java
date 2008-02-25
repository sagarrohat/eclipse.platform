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
package org.eclipse.help.ui.internal.preferences;

import org.eclipse.help.ui.internal.IHelpUIConstants;
import org.eclipse.help.ui.internal.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class TestConnectionDialog extends Dialog implements IShellProvider {

	private Group group;
	private Label testConnectionLabel;
	private Label urlLabel;
	private Label connectStatusLabel;
	
	Point shellSize;
	Point shellLocation;
	private String infoCenterName = "", infoCenterHost = "", //$NON-NLS-1$ //$NON-NLS-2$
			infoCenterPath = "", infoCenterPort = ""; //$NON-NLS-1$ //$NON-NLS-2$

	boolean successfullConnection = false;
	Color connectionColor;

	protected TestConnectionDialog(Shell parentShell) {

		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				IHelpUIConstants.PREF_PAGE_HELP_CONTENT);

		Composite composite = (Composite) super.createDialogArea(parent);
		//add controls to composite as necessary

		createGroup(parent);

		//Create Button Barthis.createButton(parent, OK, "OK", true);
		this.createButtonBar(parent);
		
		return composite;

	}

	public void initializeBounds() {
		shellSize = getInitialSize();
		shellLocation = getInitialLocation(shellSize);
		this.getShell().setBounds(shellLocation.x, shellLocation.y,
				shellSize.x + 150, shellSize.y - 40);
		this.getShell().setText(
				Messages.TestConnectionDialog_4);
	}

	/*
	 * Create the "Location" group.
	 */
	private void createGroup(Composite parent) {
		group = new Group(parent, SWT.NONE);
		group.setText(Messages.TestConnectionDialog_5);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));

		createTestLabelSection(group);
		createURLSection(group);
		createStatusSection(group);
		
	}

	/*
	 * Create the "Connection" label.
	 */
	private void createTestLabelSection(Composite parent) {
		testConnectionLabel = new Label(parent, SWT.VERTICAL);
	
		testConnectionLabel.setText(Messages.TestConnectionDialog_6 +infoCenterName +"\n"); //$NON-NLS-1$
	}

	/*
	 * Create the "URL" label.
	 */
	private void createURLSection(Composite parent) {
		urlLabel = new Label(parent, SWT.VERTICAL);
					
		String urlString="\nURL: http://" + infoCenterHost; //$NON-NLS-1$
		if (infoCenterPort
				.equals("80")) { //$NON-NLS-1$
			urlString = urlString + infoCenterPath;

		} else {
			urlString = urlString + ":" + infoCenterPort + infoCenterPath; //$NON-NLS-1$

		}
		urlLabel.setText(urlString+"\n"); //$NON-NLS-1$
	}

	/*
	 * Create the "Status" label and text field.
	 */
	private void createStatusSection(Composite parent) {
		String connectStatus="\n"; //$NON-NLS-1$
		
		connectStatusLabel = new Label(parent, SWT.NONE);
	
		if (successfullConnection == true) {
			connectStatus=connectStatus+Messages.TestConnectionDialog_12;
			
		} else if (successfullConnection == false) {
			connectStatus=connectStatus+ Messages.TestConnectionDialog_13;

		}
		
		connectStatusLabel.setText(connectStatus);
	}

	//Override cancel button so it acts as OK.  Then set OK button not visible, because we only need one button
	public void cancelPressed()
	{
		this.setReturnCode(OK);

		if (connectionColor != null) {
			connectionColor.dispose();
		}
		this.close();
	}
	public void setValues(String icName, String icHost, String icPort,
			String icPath) {
		infoCenterName = icName;
		infoCenterHost = icHost;
		infoCenterPath = icPath;
		infoCenterPort = icPort;
	}

	public void setConnectionStatus(boolean testStatus) {
		successfullConnection = testStatus;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create OK button only by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
}
