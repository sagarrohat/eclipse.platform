package org.eclipse.team.internal.ui.target;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.sync.IRemoteResource;
import org.eclipse.team.core.target.IRemoteTargetResource;
import org.eclipse.team.internal.ui.TeamUIPlugin;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class RemoteResourceElement implements IWorkbenchAdapter, IAdaptable {
	IRemoteTargetResource remote;
	boolean showFiles = true;
	
	public RemoteResourceElement(IRemoteTargetResource remote) {
		this.remote = remote;
	}
	
	public RemoteResourceElement(IRemoteTargetResource remote, boolean showFiles) {
		this.remote = remote;
		this.showFiles = showFiles;
	}

	public IRemoteTargetResource getRemoteResource() {
		return remote;
	}
	
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) return this;
		return null;
	}

	public Object[] getChildren(Object o) {
		final Object[][] result = new Object[1][];
		try {
			TeamUIPlugin.runWithProgress(null, true /*cancelable*/, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						IRemoteResource[] children = remote.members(monitor);
						List remoteElements = new ArrayList();
						int n = 0;
						for (int i = 0; i < children.length; i++) {
							IRemoteTargetResource child = (IRemoteTargetResource)children[i];
							if(child.isContainer() || showFiles) {
								remoteElements.add(new RemoteResourceElement(child, showFiles));
							}
						}
						result[0] = (RemoteResourceElement[])remoteElements.toArray(new RemoteResourceElement[remoteElements.size()]);
					} catch (TeamException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InterruptedException e) {
			return new Object[0];
		} catch (InvocationTargetException e) {
			TeamUIPlugin.handle(e.getTargetException());
			return new Object[0];
		}
		return result[0];
	}
	
	public ImageDescriptor getImageDescriptor(Object object) {
		if(remote.isContainer()) {
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		} else {
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
		}
	}
	
	public String getLabel(Object o) {
		// would be nice to display more than just the name (e.g. timestamp, size...)
		return remote.getName();
	}
	
	public Object getParent(Object o) {
		return null;
	}
}
