/***********************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.chart.ui.swt.series;

import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.log.ILogger;
import org.eclipse.birt.chart.log.Logger;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.type.StockSeries;
import org.eclipse.birt.chart.model.util.ChartElementUtil;
import org.eclipse.birt.chart.ui.extension.i18n.Messages;
import org.eclipse.birt.chart.ui.plugin.ChartUIExtensionPlugin;
import org.eclipse.birt.chart.ui.swt.composites.LineAttributesComposite;
import org.eclipse.birt.chart.ui.swt.wizard.ChartWizardContext;
import org.eclipse.birt.chart.ui.util.ChartHelpContextIds;
import org.eclipse.birt.chart.ui.util.ChartUIExtensionUtil;
import org.eclipse.birt.chart.ui.util.ChartUIUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Actuate Corporation
 * 
 */
public class StockSeriesAttributeComposite extends Composite implements
		Listener,
		SelectionListener
{

	// FillChooserComposite fccCandle = null;

	private LineAttributesComposite liacStock = null;

	private Spinner iscStick = null;

	protected StockSeries series = null;

	protected transient ChartWizardContext context;

	private Button btnAuto;

	private static ILogger logger = Logger.getLogger( "org.eclipse.birt.chart.ui.extension/swt.series" ); //$NON-NLS-1$

	/**
	 * @param parent
	 * @param style
	 */
	public StockSeriesAttributeComposite( Composite parent, int style,
			ChartWizardContext context, Series series )
	{
		super( parent, style );
		if ( !( series instanceof StockSeries ) )
		{
			try
			{
				throw new ChartException( ChartUIExtensionPlugin.ID,
						ChartException.VALIDATION,
						"StockSeriesAttributeComposite.Exception.IllegalArgument", new Object[]{series.getClass( ).getName( )}, Messages.getResourceBundle( ) ); //$NON-NLS-1$
			}
			catch ( ChartException e )
			{
				logger.log( e );
				e.printStackTrace( );
			}
		}
		this.series = (StockSeries) series;
		this.context = context;
		init( );
		placeComponents( );
		ChartUIUtil.bindHelp( parent, ChartHelpContextIds.SUBTASK_YSERIES_STOCK );
	}

	private void init( )
	{
		this.setSize( getParent( ).getClientArea( ).width,
				getParent( ).getClientArea( ).height );
	}

	protected void placeComponents( )
	{
		// Main content composite
		this.setLayout( new GridLayout( ) );

		Group grpLine = new Group( this, SWT.NONE );
		{
			grpLine.setText(  Messages.getString( "StockSeriesAttributeComposite.Lbl.Line" ) ); //$NON-NLS-1$
			GridLayout glLine = new GridLayout( );
			glLine.numColumns = series.isShowAsBarStick( ) ? 4 : 1;
			grpLine.setLayout( glLine );
			grpLine.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		}

		initUIComponents( grpLine );
	}

	protected void initUIComponents( Group grpLine )
	{
		// Line Attributes composite
		liacStock = new LineAttributesComposite( grpLine,
				SWT.NONE,
				context,
				series.getLineAttributes( ),
				true,
				true,
				false,
				true,
				true );
		GridData gdLIACStock = new GridData( GridData.FILL_HORIZONTAL );
		gdLIACStock.verticalSpan = 3;
		liacStock.setLayoutData( gdLIACStock );
		liacStock.addListener( this );

		if ( series.isShowAsBarStick( ) )
		{
			new Label( grpLine, SWT.NONE ).setText( Messages.getString( "StockSeriesAttributeComposite.Lbl.StickLength" ) ); //$NON-NLS-1$

			iscStick = new Spinner( grpLine, SWT.BORDER );
			iscStick.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
			iscStick.setMinimum( 0 );
			iscStick.setMaximum( Integer.MAX_VALUE );
			iscStick.setSelection( series.getStickLength( ) );
			iscStick.addSelectionListener( this );
			
			btnAuto = new Button( grpLine, SWT.CHECK );
			btnAuto.setText( ChartUIExtensionUtil.getAutoMessage( ) );
			btnAuto.addSelectionListener( this );
			GridData gd = new GridData();
			btnAuto.setLayoutData( gd );
			btnAuto.setSelection( !series.isSetStickLength( ) );
			iscStick.setEnabled( !btnAuto.getSelection( ) );
		}
	}

	public Point getPreferredSize( )
	{
		return new Point( 400, 200 );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected( SelectionEvent e )
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected( SelectionEvent e )
	{
		if ( e.widget.equals( iscStick ) )
		{
			series.setStickLength( iscStick.getSelection( ) );
		}
		else if ( e.widget == btnAuto )
		{
			if ( btnAuto.getSelection( ) )
			{
				iscStick.setEnabled( false );
			}
			else
			{
				iscStick.setEnabled( true );
			}
			ChartElementUtil.setEObjectAttribute( series,
					"stickLength", //$NON-NLS-1$
					Integer.valueOf( iscStick.getSelection( ) ),
					btnAuto.getSelection( ) );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent( Event event )
	{
		// if ( event.widget.equals( fccCandle ) )
		// {
		// series.setFill( (Fill) event.data );
		// }
		boolean isUnset = ( event.detail == ChartUIExtensionUtil.PROPERTY_UNSET );
		if ( event.widget.equals( liacStock ) )
		{
			if ( event.type == LineAttributesComposite.VISIBILITY_CHANGED_EVENT )
			{
				ChartElementUtil.setEObjectAttribute( series.getLineAttributes( ),
						"visible",//$NON-NLS-1$
						( (Boolean) event.data ).booleanValue( ),
						isUnset );
			}
			else if ( event.type == LineAttributesComposite.STYLE_CHANGED_EVENT )
			{
				ChartElementUtil.setEObjectAttribute( series.getLineAttributes( ),
						"style",//$NON-NLS-1$
						event.data,
						isUnset );
			}
			else if ( event.type == LineAttributesComposite.WIDTH_CHANGED_EVENT )
			{
				ChartElementUtil.setEObjectAttribute( series.getLineAttributes( ),
						"thickness",//$NON-NLS-1$
						( (Integer) event.data ).intValue( ),
						isUnset );
			}
			else if ( event.type == LineAttributesComposite.COLOR_CHANGED_EVENT )
			{
				series.getLineAttributes( )
						.setColor( (ColorDefinition) event.data );
			}
		}
	}

}