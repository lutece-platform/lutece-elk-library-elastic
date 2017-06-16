/*
 * Copyright (c) 2002-2017, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.portal.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Mock AppPropertiesService for testing purpose
 */
public final class AppPropertiesService
{
    private final static Properties _props = new Properties( );

    /**
     * Private constructor
     */
    private AppPropertiesService( )
    {
    }

    /**
     * Returns the value of a variable defined in the .properties file of the application as a String
     *
     * @param strProperty
     *            The variable name
     * @return The variable value read in the properties file
     */
    public static String getProperty( String strProperty )
    {
        return _props.getProperty( strProperty );
    }

    /**
     * Returns the value of a variable defined in the .properties file of the application as a String
     *
     * @param strProperty
     *            The variable name
     * @param strDefault
     *            The default value which is returned if no value is found for the variable in the .properties file.
     * @return The variable value read in the properties file
     */
    public static String getProperty( String strProperty, String strDefault )
    {
        return _props.getProperty( strProperty, strDefault );
    }

    /**
     * Returns the value of a variable defined in the .properties file of the application as an int
     *
     * @param strProperty
     *            The variable name
     * @param nDefault
     *            The default value which is returned if no value is found for the variable in the le downloadFile .properties. .properties file.
     * @return The variable value read in the properties file
     */
    public static int getPropertyInt( String strProperty, int nDefault )
    {
        return Integer.parseInt( _props.getProperty( strProperty, Integer.toString( nDefault ) ) );
    }

    /**
     * Returns the value of a variable defined in the .properties file of the application as an long
     *
     * @param strProperty
     *            The variable name
     * @param lDefault
     *            The default value which is returned if no value is found for the variable in the le downloadFile .properties. .properties file.
     * @return The variable value read in the properties file
     */
    public static long getPropertyLong( String strProperty, long lDefault )
    {
        return Long.parseLong( _props.getProperty( strProperty, Long.toString( lDefault ) ) );
    }

    /**
     * Returns the value of a variable defined in the .properties file of the application as a boolean
     *
     * @param strProperty
     *            The variable name
     * @param bDefault
     *            The default value which is returned if no value is found for the variable in the le downloadFile .properties. .properties file.
     * @return The variable value read in the properties file
     */
    public static boolean getPropertyBoolean( String strProperty, boolean bDefault )
    {
        return Boolean.getBoolean( _props.getProperty( strProperty, Boolean.toString( bDefault ) ) );
    }

    public static void load( InputStream stream ) throws IOException
    {
        _props.load( stream );
    }

}
