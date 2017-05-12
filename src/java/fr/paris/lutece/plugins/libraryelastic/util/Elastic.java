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

package fr.paris.lutece.plugins.libraryelastic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.httpaccess.HttpAccessStatus;
import fr.paris.lutece.util.httpaccess.InvalidResponseStatus;
import org.apache.commons.httpclient.HttpStatus;

/**
 * Elastic
 */
public class Elastic
{
    private static ObjectMapper _mapper = new ObjectMapper( );
    private ElasticConnexion _connexion;
    private String _strServerUrl;

    /**
     * Constructor
     */
    public Elastic( )
    {
        _strServerUrl = AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_SERVER_URL, Constants.DEFAULT_SERVER_URL );
        _connexion = new ElasticConnexion( );
    }

    /**
     * Constructor
     * 
     * @param strServerUrl
     *            The Elastic server URL
     */
    public Elastic( String strServerUrl )
    {
        _strServerUrl = strServerUrl;
        _connexion = new ElasticConnexion( );
    }

    /**
     * Create a document oof given type into a given index
     * 
     * @param strIndex
     *            The index
     * @param strType
     *            The document type
     * @param object
     *            The document
     * @return The JSON response from Elastic
     * @throws ElasticClientException
     *             If a problem occurs connecting Elastic
     */
    public String create( String strIndex, String strType, Object object ) throws ElasticClientException
    {
        String strResponse = "";
        try
        {
            String strJSON = _mapper.writeValueAsString( object );
            String strURI = getURI( strIndex, strType );
            strResponse = _connexion.POST( strURI, strJSON );
        }
        catch( JsonProcessingException | HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error creating object : " + ex.getMessage( ), ex );
        }
        return strResponse;
    }

    /**
     * Delete a given index
     * 
     * @param strIndex
     *            The index
     * @return The JSON response from Elastic
     * @throws ElasticClientException
     *             If a problem occurs connecting Elastic
     */
    public String deleteIndex( String strIndex ) throws ElasticClientException
    {
        String strResponse = "";
        try
        {
            String strURI = getURI( strIndex );
            strResponse = _connexion.DELETE( strURI );
        }
        catch( HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error deleting index : " + ex.getMessage( ), ex );
        }
        return strResponse;
    }
    
    /**
     * Check if a given index exists
     * @param strIndex The index
     * @return if th index exists
     * @throws ElasticClientException If a problem occurs connecting Elastic 
     */
    public boolean isExists( String strIndex ) throws ElasticClientException
    {
        try
        {
            String strURI = getURI( strIndex );
            _connexion.GET( strURI );
        }
        catch(  InvalidResponseStatus ex )
        {
            if( ex.getResponseStatus() == HttpStatus.SC_NOT_FOUND )
            {
                return false;
            }
            throw new ElasticClientException( "ElasticLibrary : Error getting index : " + ex.getMessage( ), ex );
        }
        catch( HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error getting index : " + ex.getMessage( ), ex );
        }
        return true;
    }

    /**
     * 
     * @param strIndex
     * @param strJsonMappings
     * @return
     * @throws ElasticClientException
     */
    public String createMappings( String strIndex, String strJsonMappings ) throws ElasticClientException
    {
        String strResponse = "";
        try
        {
            String strURI = getURI( strIndex );
            strResponse = _connexion.PUT( strURI, strJsonMappings );
        }
        catch( HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error deleting index : " + ex.getMessage( ), ex );
        }
        return strResponse;

    }

    /**
     * Build the URI of a given index
     * 
     * @param strIndex
     * @return The URI
     */
    private String getURI( String strIndex )
    {
        return getURI( strIndex, null );
    }

    /**
     * Build the URI of a given index
     * 
     * @param strIndex
     *            The index name
     * @param strType
     *            The document type
     * @return The URI
     */
    private String getURI( String strIndex, String strType )
    {
        String strURI = _strServerUrl;
        strURI = ( strURI.endsWith( "/" ) ) ? strURI : strURI + "/";
        strURI = strURI + strIndex + "/";
        if ( strType != null )
        {
            strURI = strURI + strType + "/";
        }
        return strURI;
    }
}
