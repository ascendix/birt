/*******************************************************************************
 * Copyright (c) 2004, 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.data.oda.xml.impl;

import java.util.Map;

import org.eclipse.birt.report.data.oda.xml.Constants;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.enablement.oda.xml.i18n.Messages;

/**
 * This class is used to build an XML data source connection.
 * 
 * @deprecated Please use DTP xml driver
 */
public class Connection
		extends
			org.eclipse.datatools.enablement.oda.xml.impl.Connection
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException
	{
		if ( !( context instanceof Map ) )
			throw new OdaException( Messages.getString( "Connection.InvalidAppContext" ) );
		Map appContext = (Map) context;
		if ( appContext.get( Constants.APPCONTEXT_INPUTSTREAM ) != null )
		{
			appContext.put( org.eclipse.datatools.enablement.oda.xml.Constants.APPCONTEXT_INPUTSTREAM,
					appContext.get( Constants.APPCONTEXT_INPUTSTREAM ) );
		}

		if ( appContext.get( Constants.APPCONTEXT_CLOSEINPUTSTREAM ) != null )
		{
			appContext.put( org.eclipse.datatools.enablement.oda.xml.Constants.APPCONTEXT_CLOSEINPUTSTREAM,
					appContext.get( Constants.APPCONTEXT_CLOSEINPUTSTREAM ) );
		}
		super.setAppContext( (Map) context );
	}
}
