/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Actuate Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.birt.chart.ui.swt.wizard.format.series;

import java.util.List;

import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.DialChart;
import org.eclipse.birt.chart.ui.extension.i18n.Messages;
import org.eclipse.birt.chart.ui.swt.composites.ExternalizedTextEditorComposite;
import org.eclipse.birt.chart.ui.swt.composites.LocalizedNumberEditorComposite;
import org.eclipse.birt.chart.ui.swt.fieldassist.TextNumberEditorAssistField;
import org.eclipse.birt.chart.ui.swt.wizard.format.SubtaskSheetImpl;
import org.eclipse.birt.chart.ui.util.ChartHelpContextIds;
import org.eclipse.birt.chart.ui.util.ChartUIExtensionUtil;
import org.eclipse.birt.chart.ui.util.ChartUIUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Actuate Corporation
 * 
 */
public class SeriesXSheetImpl extends SubtaskSheetImpl
		implements
			Listener,
			ModifyListener,
			SelectionListener
{

	private transient Label lblMinSlice;
	private transient Label lblBottomPercent;
	private transient Label lblLabel;
	private transient Combo cmbMinSlice;
	private transient LocalizedNumberEditorComposite txtMinSlice;
	private Button btnTxtMinSliceAuto;
	private transient ExternalizedTextEditorComposite txtLabel = null;

	private final static String TOOLTIP_MINIMUM_SLICE = Messages.getString( "PieBottomAreaComponent.Label.AnySliceWithASize" ); //$NON-NLS-1$

	private final static String[] MINMUM_SLICE_ITEMS = new String[]{
			Messages.getString( "PieBottomAreaComponent.Label.Percentage" ), Messages.getString( "PieBottomAreaComponent.Label.Value" ) //$NON-NLS-1$ //$NON-NLS-2$
	};

	public void createControl( Composite parent )
	{
		ChartUIUtil.bindHelp( parent, ChartHelpContextIds.SUBTASK_XSERIES );
		cmpContent = new Composite( parent, SWT.NONE );
		{
			GridLayout glContent = new GridLayout( 2, false );
			cmpContent.setLayout( glContent );
			GridData gd = new GridData( GridData.FILL_BOTH );
			cmpContent.setLayoutData( gd );
		}

		Composite cmpBasic = new Composite( cmpContent, SWT.NONE );
		{
			cmpBasic.setLayout( new GridLayout( 2, false ) );
			GridData gd = new GridData( GridData.FILL_HORIZONTAL );
			cmpBasic.setLayoutData( gd );
		}

		if ( getChart( ) instanceof ChartWithoutAxes
				&& !( getChart( ) instanceof DialChart ) )
		{
			createPieAxisArea( cmpBasic );
		}
		updateUIState( );
	}

	private void createPieAxisArea( Composite parent )
	{
		lblMinSlice = new Label( parent, SWT.NONE );
		{
			lblMinSlice.setText( Messages.getString( "PieBottomAreaComponent.Label.MinimumSlice" ) ); //$NON-NLS-1$
			lblMinSlice.setToolTipText( TOOLTIP_MINIMUM_SLICE );
		}

		Composite cmpMinSlice = new Composite( parent, SWT.NONE );
		{
			GridLayout gridLayout = new GridLayout( 4, false );
			gridLayout.marginWidth = 0;
			gridLayout.marginHeight = 0;
			gridLayout.horizontalSpacing = 8;
			cmpMinSlice.setLayout( gridLayout );
			cmpMinSlice.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		}

		cmbMinSlice = new Combo( cmpMinSlice, SWT.DROP_DOWN | SWT.READ_ONLY );
		{
			cmbMinSlice.setToolTipText( TOOLTIP_MINIMUM_SLICE );
			cmbMinSlice.setItems( ChartUIExtensionUtil.getItemsWithAuto( MINMUM_SLICE_ITEMS ) );
			cmbMinSlice.select( ( (ChartWithoutAxes) getChart( ) ).isSetMinSlicePercent( ) ? ( ( (ChartWithoutAxes) getChart( ) ).isMinSlicePercent( ) ? 1
					: 2 )
					: 0 );
			cmbMinSlice.addSelectionListener( this );
		}

		txtMinSlice = new LocalizedNumberEditorComposite( cmpMinSlice,
				SWT.BORDER );
		new TextNumberEditorAssistField( txtMinSlice.getTextControl( ), null );
		{
			GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
			txtMinSlice.setLayoutData( gridData );
			txtMinSlice.setToolTipText( TOOLTIP_MINIMUM_SLICE );
			txtMinSlice.setValue( ( (ChartWithoutAxes) getChart( ) ).getMinSlice( ) );
			txtMinSlice.addModifyListener( this );
		}

		lblBottomPercent = new Label( cmpMinSlice, SWT.NONE );
		lblBottomPercent.setText( "%" ); //$NON-NLS-1$
		
		btnTxtMinSliceAuto = new Button(cmpMinSlice, SWT.CHECK );
		btnTxtMinSliceAuto.setText( ChartUIExtensionUtil.getAutoMessage( ) );
		btnTxtMinSliceAuto.setSelection( !( (ChartWithoutAxes) getChart( ) ).isSetMinSlice( ) );
		txtMinSlice.setEnabled( !btnTxtMinSliceAuto.getSelection( ) );
		lblBottomPercent.setVisible( ( (ChartWithoutAxes) getChart( ) ).isSetMinSlicePercent( )
				&& ( (ChartWithoutAxes) getChart( ) ).isMinSlicePercent( ) && !btnTxtMinSliceAuto.getSelection( ) );
		btnTxtMinSliceAuto.addSelectionListener( this );
		
		lblLabel = new Label( parent, SWT.NONE );
		{
			lblLabel.setText( Messages.getString( "PieBottomAreaComponent.Label.MinSliceLabel" ) ); //$NON-NLS-1$
			lblLabel.setToolTipText( TOOLTIP_MINIMUM_SLICE );
		}

		List<String> keys = null;
		if ( getContext( ).getUIServiceProvider( ) != null )
		{
			keys = getContext( ).getUIServiceProvider( ).getRegisteredKeys( );
		}
		txtLabel = new ExternalizedTextEditorComposite( parent,
				SWT.BORDER,
				-1,
				-1,
				keys,
				getContext( ).getUIServiceProvider( ),
				( (ChartWithoutAxes) getChart( ) ).getMinSliceLabel( ) != null
						? ( (ChartWithoutAxes) getChart( ) ).getMinSliceLabel( )
						: "" ); //$NON-NLS-1$
		{
			GridData gdTXTTitle = new GridData( GridData.FILL_HORIZONTAL );
			txtLabel.setLayoutData( gdTXTTitle );
			txtLabel.setEnabled( ( (ChartWithoutAxes) getChart( ) ).isSetMinSlice( )
					&& ( (ChartWithoutAxes) getChart( ) ).getMinSlice( ) != 0 );
			txtLabel.addListener( this );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText( ModifyEvent e )
	{
		if ( e.getSource( ) == txtMinSlice )
		{
			( (ChartWithoutAxes) getChart( ) ).setMinSlice( txtMinSlice.getValue( ) );
			txtLabel.setEnabled( ( (ChartWithoutAxes) getChart( ) ).isSetMinSlice( )
					&& ( (ChartWithoutAxes) getChart( ) ).getMinSlice( ) != 0 );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent( Event event )
	{
		if ( getChart( ) instanceof ChartWithoutAxes )
		{
			if ( event.widget.equals( txtLabel ) )
			{
				( (ChartWithoutAxes) getChart( ) ).setMinSliceLabel( txtLabel.getText( ) );
			}
		}
	}

	public void widgetSelected( SelectionEvent e )
	{
		if ( getChart( ) instanceof ChartWithoutAxes )
		{
			if ( e.widget == cmbMinSlice )
			{
				if ( cmbMinSlice.getSelectionIndex( ) == 0 )
				{
					( (ChartWithoutAxes) getChart( ) ).unsetMinSlicePercent( );
				}
				else
				{
					( (ChartWithoutAxes) getChart( ) ).setMinSlicePercent( cmbMinSlice.getSelectionIndex( ) == 1 );
				}
				updateUIState( );
			}
			else if ( e.widget == btnTxtMinSliceAuto )
			{
				if ( btnTxtMinSliceAuto.getSelection( ) )
				{
					 ( (ChartWithoutAxes) getChart( ) ).unsetMinSlice( );
				}
				else
				{
					( (ChartWithoutAxes) getChart( ) ).setMinSlice( txtMinSlice.getValue( ) );
				}
				updateUIState( );
			}
		}
	}

	private void updateUIState( )
	{
		if ( cmbMinSlice.getSelectionIndex( ) ==  0 )
		{
			txtMinSlice.setEnabled( false );
			lblBottomPercent.setVisible( false );
			txtLabel.setEnabled( false );
			btnTxtMinSliceAuto.setEnabled( false );
		}
		else
		{
			btnTxtMinSliceAuto.setEnabled( true );
			if ( btnTxtMinSliceAuto.getSelection( ) )
			{
				txtMinSlice.setEnabled( false );
				lblBottomPercent.setVisible( false );
			}
			else
			{
				txtMinSlice.setEnabled( true );
				lblBottomPercent.setVisible( ( (ChartWithoutAxes) getChart( ) ).isSetMinSlice( ) && ( (ChartWithoutAxes) getChart( ) ).isMinSlicePercent( ) );
			}
			txtLabel.setEnabled( ( (ChartWithoutAxes) getChart( ) ).isSetMinSlice( )
					&& ( (ChartWithoutAxes) getChart( ) ).getMinSlice( ) != 0 );
		}
		
	}
	
	public void widgetDefaultSelected( SelectionEvent e )
	{
		// TODO Auto-generated method stub
	}

}