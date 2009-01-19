/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.designer.core.model.schematic.HandleAdapterFactory;
import org.eclipse.birt.report.designer.internal.ui.editors.parts.DeferredGraphicalViewer;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.border.ReportDesignMarginBorder;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.editpolicies.ReportContainerEditPolicy;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.editpolicies.ReportFlowLayoutEditPolicy;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.figures.ReportRootFigure;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.tools.RootDragTracker;
import org.eclipse.birt.report.designer.internal.ui.layout.AbstractPageFlowLayout;
import org.eclipse.birt.report.designer.internal.ui.layout.ReportDesignLayout;
import org.eclipse.birt.report.designer.internal.ui.util.bidi.BidiUIUtils;
import org.eclipse.birt.report.designer.ui.IReportGraphicConstants;
import org.eclipse.birt.report.designer.ui.ReportPlugin;
import org.eclipse.birt.report.model.api.MasterPageHandle;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.elements.interfaces.IMasterPageModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.tools.DeselectAllTracker;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * Report design editPart This is the content edit part for report Designer. All
 * other report elements puts on to it
 * </p>
 */
public class ReportDesignEditPart extends AbstractReportEditPart
{

	protected boolean showMargin = true;
//	private final double wScale = 1.3;
//	private final double hScale = 1.0;

	/**
	 * constructor
	 * 
	 * @param obj
	 *            the object
	 */
	public ReportDesignEditPart( Object obj )
	{
		super( obj );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure( )
	{
		ReportRootFigure figure = new ReportRootFigure( );

		figure.setOpaque( true );
		figure.setShowMargin( showMargin );

		ReportDesignLayout layout = new ReportDesignLayout( this );

		SimpleMasterPageHandle masterPageHandle = getSimpleMasterPageHandle( );

		Dimension size = getMasterPageSize( masterPageHandle );

		Rectangle bounds = new Rectangle( 0, 0, size.width - 1, size.height - 1 );

		layout.setInitSize( bounds );

		figure.setLayoutManager( layout );

		figure.setBorder( new ReportDesignMarginBorder( getMasterPageInsets( masterPageHandle ) ) );

		figure.setBounds( bounds.getCopy( ) );

		return figure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies( )
	{
		installEditPolicy( EditPolicy.LAYOUT_ROLE,
				new ReportFlowLayoutEditPolicy( ) );
		installEditPolicy( EditPolicy.CONTAINER_ROLE,
				new ReportContainerEditPolicy( ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.designer.ui.editor.edit.ReportElementEditPart#getModelChildren()
	 */
	protected List getModelChildren( )
	{
		return HandleAdapterFactory.getInstance( )
				.getReportDesignHandleAdapter( getModel( ) )
				.getChildren( );
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
	 */

	public DragTracker getDragTracker( Request req )
	{
		if ( req instanceof SelectionRequest
				&& ( (SelectionRequest) req ).getLastButtonPressed( ) == 3 )
			return new DeselectAllTracker( this );
		return new RootDragTracker( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.designer.internal.ui.editors.schematic.editparts.AbstractReportEditPart#refreshFigure()
	 */
	public void refreshFigure( )
	{
		
		SimpleMasterPageHandle masterPageHandle = getSimpleMasterPageHandle();

		Dimension size = getMasterPageSize( masterPageHandle );

		Rectangle bounds = new Rectangle( 0, 0, size.width - 1, size.height - 1 );

		ReportRootFigure figure = (ReportRootFigure) getFigure( );
		figure.setShowMargin( showMargin );
		
		((ReportDesignLayout)(figure.getLayoutManager())).setAuto(DesignChoiceConstants.REPORT_LAYOUT_PREFERENCE_AUTO_LAYOUT.
				equals(( (ReportDesignHandle) getModel( ) ).getLayoutPreference()));
		/*
		 * Bidi-specific properties are affected only if Bidi Support is enabled
		 */
		
		( (ReportDesignLayout) ( figure.getLayoutManager( ) ) )
				.setAuto( DesignChoiceConstants.BIDI_DIRECTION_LTR
						.equals( ( (ReportDesignHandle) getModel( ) )
								.getBidiOrientation( ) ) );
		
		if ( !showMargin )
		{
			Insets mg = getMasterPageInsets( masterPageHandle );

			bounds.width -= mg.getWidth( );
			bounds.height -= mg.getHeight( );
		}
		Insets initInsets = getMasterPageInsets( masterPageHandle ) ;
		( (AbstractPageFlowLayout) getFigure( ).getLayoutManager( ) ).setInitSize( bounds );
		( (AbstractPageFlowLayout) getFigure( ).getLayoutManager( ) ).setInitInsets(initInsets);
		// getFigure( ).setBounds( bounds );

		int color = getBackgroundColor( masterPageHandle );
		getFigure( ).setBackgroundColor(getBackGroundColor(color)  );


		refreshBackground( masterPageHandle );
	}

	private SimpleMasterPageHandle getSimpleMasterPageHandle()
	{
		SlotHandle slotHandle = ( (ModuleHandle) getModel( ) ).getMasterPages( );
		Iterator iter = slotHandle.iterator( );
		SimpleMasterPageHandle masterPageHandle = (SimpleMasterPageHandle) iter.next( );
		return masterPageHandle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate( )
	{
		super.activate( );

		//getFigure( ).setFocusTraversable( false );

		getViewer( ).addPropertyChangeListener( new PropertyChangeListener( ) {

			public void propertyChange( PropertyChangeEvent evt )
			{
				if ( DeferredGraphicalViewer.PROPERTY_MARGIN_VISIBILITY.equals( evt.getPropertyName( ) ) )
				{
					showMargin = ( (Boolean) evt.getNewValue( ) ).booleanValue( );

					refresh( );
					markDirty( true );
				}
			}
		} );
		if (getModel() instanceof ReportDesignHandle)
		{
			getViewer().setProperty(IReportGraphicConstants.REPORT_LAYOUT_PROPERTY, 
					((ReportDesignHandle)getModel()).getLayoutPreference());
			
			/* 
			 * if BiDi support is enabled, set valid value for BiDi orientation property
			 */
			
			getViewer( ).setProperty( IReportGraphicConstants.REPORT_BIDIORIENTATION_PROPERTY,
					( (ReportDesignHandle) getModel()).getBidiOrientation( ) );
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#isinterest(java.lang.Object)
	 */
	public boolean isinterest( Object model )
	{
		return super.isinterest( model ) || model instanceof MasterPageHandle;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#propertyChange(java.util.Map)
	 */
	protected void propertyChange(Map info) 
	{
		boolean invalidate = false;
		/*
		 * Bidi-specific behavior is addressed only if Bidi support is enabled
		 */
		
		if ( info.get( ReportDesignHandle.BIDI_ORIENTATION_PROP ) instanceof ReportDesignHandle )
		{
			String newOrientation = ( (ReportDesignHandle) info.get(
					ReportDesignHandle.BIDI_ORIENTATION_PROP ) ).getBidiOrientation( );
					
			boolean mirrored = DesignChoiceConstants
					.BIDI_DIRECTION_RTL.equals( newOrientation );

			this.getViewer( ).flush( );
				
			// Apply new orientation to the view.
			Composite parent = getViewer( ).getControl( ).getParent( );
			BidiUIUtils.INSTANCE.applyOrientation( parent, mirrored );

			parent.layout( true );

			getViewer( ).setProperty( IReportGraphicConstants
					.REPORT_BIDIORIENTATION_PROPERTY, newOrientation );

			invalidate = true;
		}
		
		super.propertyChange(info);
		if (info.get(ReportDesignHandle.LAYOUT_PREFERENCE_PROP) != null)
		{
			if (info.get(ReportDesignHandle.LAYOUT_PREFERENCE_PROP) instanceof ReportDesignHandle)
			{
				getViewer().setProperty(IReportGraphicConstants.REPORT_LAYOUT_PROPERTY, 
						((ReportDesignHandle)info.get(ReportDesignHandle.LAYOUT_PREFERENCE_PROP)).getLayoutPreference());

				invalidate = true;
			}
		}
		if ( invalidate )
		{
			getFigure( ).invalidateTree( );
			//getFigure( ).getUpdateManager( ).addInvalidFigure( getFigure( ) );
			getFigure( ).revalidate( );
		}
		if (getModel() instanceof ReportDesignHandle)
		{
			
			if (info.get(IMasterPageModel.TOP_MARGIN_PROP) != null
					||info.get(IMasterPageModel.BOTTOM_MARGIN_PROP) != null
					||info.get(IMasterPageModel.LEFT_MARGIN_PROP) != null
					||info.get(IMasterPageModel.RIGHT_MARGIN_PROP) != null
					
					||info.get(IMasterPageModel.TYPE_PROP) != null
					||info.get(IMasterPageModel.WIDTH_PROP) != null
					||info.get(IMasterPageModel.HEIGHT_PROP) != null
					||info.get(IMasterPageModel.ORIENTATION_PROP) != null
						)
			{				
				SlotHandle slotHandle = ( (ModuleHandle) getModel( ) ).getMasterPages( );
				Iterator iter = slotHandle.iterator( );
				SimpleMasterPageHandle masterPageHandle = (SimpleMasterPageHandle) iter.next( );
	
				Dimension size = getMasterPageSize( masterPageHandle );
	
				Rectangle bounds = new Rectangle( 0, 0, size.width - 1, size.height - 1 );
	
				((AbstractPageFlowLayout)getFigure( ).getLayoutManager( )).setInitSize( bounds );
	
	
				figure.setBorder( new ReportDesignMarginBorder( getMasterPageInsets( masterPageHandle ) ) );
	
				figure.setBounds( bounds.getCopy( ) );
			}
		}
	}
	
}