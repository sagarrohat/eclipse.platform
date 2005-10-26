/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ui.synchronize.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ui.TeamUIPlugin;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.team.internal.ui.synchronize.SynchronizeView;
import org.eclipse.team.ui.synchronize.*;

public class ShowSynchronizeParticipantAction extends Action implements IPropertyChangeListener {
	
    private ISynchronizeParticipantReference fPage;
	private ISynchronizeView fView;
	
	public void run() {
		try {
			if (!fPage.equals(fView.getParticipant())) {
				fView.display(fPage.getParticipant());
			}
		} catch (TeamException e) {
			Utils.handle(e);
		}
	}
	
	/**
	 * Constructs an action to display the given synchronize participant in the
	 * synchronize view.
	 * 
	 * @param view the synchronize view in which the given page is contained
	 * @param participant the participant to show
	 */
	public ShowSynchronizeParticipantAction(ISynchronizeView view, ISynchronizeParticipantReference ref) {
		super(Utils.shortenText(SynchronizeView.MAX_NAME_LENGTH, ref.getDisplayName()), Action.AS_RADIO_BUTTON);
		fPage = ref;
		fView = view;
        setImageDescriptor( new ParticipantOverlay( ref));
        
        try {
          fPage.getParticipant().addPropertyChangeListener( this);
        } catch( TeamException e) {
          Utils.handle(e);
        }
	}
    
	public void propertyChange( PropertyChangeEvent event) {
      if( AbstractSynchronizeParticipant.P_PINNED.equals( event.getProperty())) {
        setImageDescriptor(new ParticipantOverlay( fPage));
      }
	}

    
	private static final class ParticipantOverlay extends CompositeImageDescriptor {

		private ImageData overlayData = TeamUIPlugin.getImageDescriptor("ovr/pinned_ovr.gif").getImageData(); //$NON-NLS-1$
		private ImageData imageData;
		private ISynchronizeParticipant participant;

		private ParticipantOverlay(ISynchronizeParticipantReference ref) {
			try {
				this.participant = ref.getParticipant();
				this.imageData = ref.getDescriptor().getImageDescriptor().getImageData();
			} catch (TeamException ex) {
				TeamUIPlugin.log(ex);
			}
		}

		protected void drawCompositeImage(int width, int height) {
			drawImage(this.imageData, 0, 0);
			if (this.participant.isPinned()) {
				drawImage(overlayData, this.imageData.width - overlayData.width, 0);
			}
		}

		protected Point getSize() {
			return new Point(this.imageData.width, this.imageData.height);
		}
	}
}

