/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;

/**
 * An operation that can be configured to run in the foreground using
 * the {@link org.eclipse.ui.progress.IProgressService} or the background
 * as a {@link org.eclipse.core.runtime.Job}. The executione context is determined
 * by what is returned by the {@link #canRunAsJob()} hint which may be overriden by subclasses. 
 * Subsclass must override the <code>run(IProgressMonitor)</code> method to perform 
 * the behavior of the operation in the desired execution context.
 * <p>
 * If this operation is run as a job, it is registered with the job as a 
 * {@link org.eclipse.core.runtime.jobs.IJobChangeListener} and is scheduled with
 * the part of this operation if it is not <code>null</code>. 
 * Subsclasses can override the methods of this
 * interface to receive job change notificaton.
 * 
 * @see org.eclipse.ui.progress.IProgressService
 * @see org.eclipse.core.runtime.Job
 * @see org.eclipse.core.runtime.ISchedulingRule
 * @see org.eclipse.core.runtime.jobs.IJobChangeListener
 */
public abstract class TeamOperation extends JobChangeAdapter implements IRunnableWithProgress {
	
	private IWorkbenchPart part;
	
	/**
	 * Create an team operation associated with the given part.
	 * @param part the part the operation is associated with or <code>null</code>
	 */
	protected TeamOperation(IWorkbenchPart part) {
		this.part = part;
	}
	
	/**
	 * Run the operation in a context that is determined by the <code>canRunAsJob()</code>
	 * hint. If this operation can run as a job then it will be run in a background thread.
	 * Otherwise it will run in the foreground and block the caller.
	 */
	public final void run() throws InvocationTargetException, InterruptedException {
		getRunnableContext().run(this);
	}
	
	/**
	 * Returns the scheduling rule that is to be obtained before this
	 * operation is executed by it's context or <code>null</code> if
	 * no scheduling rule is to be obtained. If the operation is run 
	 * as a job, the schdulin rule is used as the schduling rule of the
	 * job. Otherwise, it is obtained before execution of the operation
	 * occurs.
	 * <p>
	 * By default, no scheduling
	 * rule is obtained. Sublcasses can override to in order ot obtain a
	 * scheduling rule or can obtain schduling rules withing their operation
	 * if finer grained schduling is desired.
	 * @return the schduling rule to be obtained by this operation
	 * or <code>null</code>
	 */
	protected ISchedulingRule getSchedulingRule() {
		return null;
	}
	
	/**
	 * Return whether the auto-build should be postponed until after
	 * the operation is complete. The default is to postpone the auto-build.
	 * subclas can override.
	 * @return whether to postpone the auto-build while the operation is executing
	 */
	protected boolean isPostponeAutobuild() {
		return true;
	}
	
	/**
	 * If this operation can safely be run in the background, then subclasses can
	 * override this method and return <code>true</code>. This will make their
	 * action run in a {@link  org.eclipse.core.runtime.Job}. 
	 * Subsclass that override this method should 
	 * also override the <code>getJobName()</code> method.
	 * 
	 * @return <code>true</code> if this action can be run in the background and
	 * <code>false</code> otherwise.
	 */
	protected boolean canRunAsJob() {
		return false;
	}
	
	/**
	 * Return the job name to be used if the action can run as a job. (i.e.
	 * if <code>canRunAsJob()</code> returns <code>true</code>).
	 * 
	 * @return the string to be used as the job name
	 */
	protected String getJobName() {
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Return a shell that can be used by the operation to display dialogs, etc.
	 * @return a shell
	 */
	protected Shell getShell() {
		return Utils.getShell(getSite());
	}
	
	/*
	 * Uses the {@link #canRunAsJob()} hint to return a {@link ITeamRunnableContext}
	 * that is used to execute the <code>run(SyncInfoSet, IProgressMonitor)</code>
	 * method of this action. 
	 * 
	 * @param syncSet the sync info set containing the selected elements for which this
	 * action is enabled.
	 * @return the runnable context in which to run this action.
	 */
	private ITeamRunnableContext getRunnableContext() {
		if (canRunAsJob()) {
			JobRunnableContext context = new JobRunnableContext(getJobName(), this, getSite());
			context.setPostponeBuild(isPostponeAutobuild());
			context.setSchedulingRule(getSchedulingRule());
			return context;
		} else {
			ProgressDialogRunnableContext context = new ProgressDialogRunnableContext(getShell());
			context.setPostponeBuild(isPostponeAutobuild());
			context.setSchedulingRule(getSchedulingRule());
			return context;
		}
	}
	
	private IWorkbenchSite getSite() {
		IWorkbenchSite site = null;
		if(part != null) {
			site = part.getSite();
		}
		return site;
	}
}
