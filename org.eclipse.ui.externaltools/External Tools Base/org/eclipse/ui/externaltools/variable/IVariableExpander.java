package org.eclipse.ui.externaltools.variable;

/*******************************************************************************
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 * IBM - Initial API and implementation
 ******************************************************************************/

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public interface IVariableExpander {
	/**
	 * Returns the path location to a file or directory
	 * for the given variable tag and value. The path does
	 * not need to exist.
	 * 
	 * @param varTag the variable tag name
	 * @param varValue the value for the variable
	 * @param context the context the variable should use to expand itself
	 * @return the <code>IPath</code> to a file/directory
	 * 		or <code>null</code> if not possible
	 */
	public IPath getPath(String varTag, String varValue, ExpandVariableContext context);
	/**
	 * Returns the <code>IResource</code> list
	 * for the given variable tag and value.
	 * 
	 * @param varTag the variable tag name
	 * @param varValue the value for the variable
	 * @param context the context the variable should use to expand itself
	 * @return the list of <code>IResource</code> or <code>null</code> if not
	 * 		possible (note, elements of the list can be <code>null</code>)
	 */
	public IResource[] getResources(String varTag, String varValue, ExpandVariableContext context);
	/**
	 * Returns the expanded text for the given variable
	 * tag and value.
	 * 
	 * @param varTag the variable tag name
	 * @param varValue the value for the variable
	 * @param context the context the variable should use to expand itself
	 * @return the text of the expanded variable
	 * 		or <code>null</code> if not possible
	 */
	public String getText(String varTag, String varValue, ExpandVariableContext context);
}
