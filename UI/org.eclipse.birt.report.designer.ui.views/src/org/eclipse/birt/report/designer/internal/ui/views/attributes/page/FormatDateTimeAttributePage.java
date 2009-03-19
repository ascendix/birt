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

package org.eclipse.birt.report.designer.internal.ui.views.attributes.page;

import org.eclipse.birt.report.designer.internal.ui.dialogs.FormatChangeEvent;
import org.eclipse.birt.report.designer.internal.ui.dialogs.IFormatChangeListener;
import org.eclipse.birt.report.designer.internal.ui.dialogs.IFormatPage;
import org.eclipse.birt.report.designer.internal.ui.util.ExceptionHandler;
import org.eclipse.birt.report.designer.internal.ui.views.attributes.provider.FormatDataTimeDescriptorProvider;
import org.eclipse.birt.report.designer.internal.ui.views.attributes.section.FormatDateTimeSection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Format date time attribute page for formatting date and times.
 */

public class FormatDateTimeAttributePage extends AttributePage
{
	private FormatDateTimeSection formatSection;
	private FormatDataTimeDescriptorProvider provider;

	public void buildUI( Composite parent  )
	{
		super.buildUI( parent );
		container.setLayout( new GridLayout( 1, false ) );
		provider = new FormatDataTimeDescriptorProvider();
		formatSection = new FormatDateTimeSection(container,IFormatPage.PAGE_ALIGN_VIRTICAL,true);
		formatSection.setProvider( provider );
		addSection( PageSectionId.FORMATDATETIME_FORMAT, formatSection );
		
		createSections( );
		layoutSections( );
		formatSection.addFormatChangeListener( new IFormatChangeListener( ) {

			public void formatChange( FormatChangeEvent event )
			{
				if ( formatSection.getFormatControl( ).isDirty( ) && formatSection.getFormatControl( ).isFormatModified( ) )
				{
					try
					{
						provider.save( new String[]{
								event.getCategory( ),
								event.getPattern( ),
								event.getLocale( )
						} );
					}
					catch ( Exception e )
					{
						ExceptionHandler.handle( e );
					}					
					if ( event.getCategory( ) != null
							|| event.getPattern( ) != null )
					{
						refresh();
					}
				}
			}
		} );
	}
}