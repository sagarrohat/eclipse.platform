package org.eclipse.team.internal.ui.target;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.team.core.target.Site;
import org.eclipse.team.core.target.TargetManager;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class SiteRootsElement implements IWorkbenchAdapter, IAdaptable {
	private Site[] sites = null;
	private boolean showFiles = true;

	public SiteRootsElement(Site[] sites, boolean showFiles) {
		this.sites = sites;
		this.showFiles = showFiles;
	}
	
	public SiteRootsElement() {
		this.sites = null;
	}
	
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public Object[] getChildren(Object o) {
		Site[] childSites;
		if(sites == null) {
			childSites = TargetManager.getSites();
		} else {
			childSites = sites;
		}
		SiteElement[] siteElements = new SiteElement[childSites.length];
		for (int i = 0; i < childSites.length; i++) {
			siteElements[i] = new SiteElement(childSites[i], showFiles);
		}
		return siteElements;
	}

	public String getLabel(Object o) {
		return null;
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) return this;
		return null;
	}

	public Object getParent(Object o) {
		return null;
	}
}
