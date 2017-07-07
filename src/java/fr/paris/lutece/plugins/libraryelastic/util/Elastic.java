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
import fr.paris.lutece.plugins.libraryelastic.business.bulk.BulkRequest;

import fr.paris.lutece.plugins.libraryelastic.business.search.SearchRequest;
import fr.paris.lutece.plugins.libraryelastic.business.suggest.AbstractSuggestRequest;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.httpaccess.InvalidResponseStatus;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;

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
     * Create a document of given type into a given index
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
        return create( strIndex, strType, StringUtils.EMPTY, object );
    }

    /**
     * Create a document of given type into a given index at the given id
     * 
     * @param strIndex
     *            The index
     * @param strType
     *            The document type
     * @param strId
     *            The document id
     * @param object
     *            The document
     * @return The JSON response from Elastic
     * @throws ElasticClientException
     *             If a problem occurs connecting Elastic
     */
    public String create( String strIndex, String strType, String strId, Object object ) throws ElasticClientException
    {
        String strResponse = StringUtils.EMPTY;
        try
        {
            String strJSON = _mapper.writeValueAsString( object );
            String strURI = getURI( strIndex, strType ) + strId;
            strResponse = _connexion.POST( strURI, strJSON );
        }
        catch( JsonProcessingException | HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error creating object : " + ex.getMessage( ), ex );
        }
        return strResponse;
    }
    
    /**
     * perform a bulk indexing of documents : this is used for indexing thousand doc with one HTTP call
     * @param strIndex the elk index name
     * @param strType the type of document
     * @param bulkRequest the bulkRequest
     * @return the reponse of Elk server
     * @throws ElasticClientException 
     */
    public String createByBulk( String strIndex, String strType, BulkRequest bulkRequest ) throws ElasticClientException
    {
        String strResponse = StringUtils.EMPTY;
        try
        {
            String strURI = getURI( strIndex, strType ) + Constants.PATH_QUERY_BULK;
            String strBulkBody = bulkRequest.getBulkBody( _mapper );
            strResponse = _connexion.POST( strURI, strBulkBody );
        }
        catch( JsonProcessingException | HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error processing bulking request : " + ex.getMessage( ), ex );
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
        String strResponse = StringUtils.EMPTY;
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
     * Delete a document based on its id in the index
     * 
     * @param strIndex
     *            The index
     * @param strType
     *            The document type
     * @param strId
     *            The id
     * @return The JSON response from Elastic
     * @throws ElasticClientException
     *             If a problem occurs connecting Elastic
     */
    public String deleteDocument( String strIndex, String strType, String strId ) throws ElasticClientException
    {
        String strResponse = StringUtils.EMPTY;
        try
        {
            String strURI = getURI( strIndex, strType ) + strId;
            strResponse = _connexion.DELETE( strURI );
        }
        catch( HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error deleting document : " + ex.getMessage( ), ex );
        }
        return strResponse;
    }

    /**
     * Check if a given index exists
     * 
     * @param strIndex
     *            The index
     * @return if th index exists
     * @throws ElasticClientException
     *             If a problem occurs connecting Elastic
     */
    public boolean isExists( String strIndex ) throws ElasticClientException
    {
        try
        {
            String strURI = getURI( strIndex );
            _connexion.GET( strURI );
        }
        catch( InvalidResponseStatus ex )
        {
            if ( ex.getResponseStatus( ) == HttpStatus.SC_NOT_FOUND )
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
     * Search a document of given type into a given index
     * 
     * @param strIndex
     *            The index
     * @param search
     *            search request
     * @return The JSON response from Elastic
     * @throws ElasticClientException
     *             If a problem occurs connecting Elastic
     */
    public String search( String strIndex, SearchRequest search ) throws ElasticClientException
    {
        String strResponse = StringUtils.EMPTY;
        try
        {
            String strJSON = _mapper.writeValueAsString( search.mapToNode( ) );
            String strURI = getURI( strIndex ) + Constants.PATH_QUERY_SEARCH;
            strResponse = _connexion.POST( strURI, strJSON );
        }
        catch( JsonProcessingException | HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error searching object : " + ex.getMessage( ), ex );
        }
        return strResponse;
    }

    /**
     * suggest a list of document of given type into a given index The suggest is done with a _search request with size set to 0 to avoid fetch in 'hits' so be
     * careful with the JSON result
     * 
     * @param strIndex
     *            The index
     * @param suggest
     *            suggest request
     * @return The JSON response from Elastic
     * @throws ElasticClientException
     *             If a problem occurs connecting Elastic
     */
    public String suggest( String strIndex, AbstractSuggestRequest suggest ) throws ElasticClientException
    {
        String strResponse = StringUtils.EMPTY;
        try
        {
            SearchRequest search = new SearchRequest( );
            search.setSize( 0 );
            search.setSearchQuery( suggest );
            String strJSON = _mapper.writeValueAsString( search.mapToNode( ) );
            String strURI = getURI( strIndex ) + Constants.PATH_QUERY_SEARCH;
            strResponse = _connexion.POST( strURI, strJSON );
        }
        catch( JsonProcessingException | HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error suggesting object : " + ex.getMessage( ), ex );
        }
        return strResponse;
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
        String strResponse = StringUtils.EMPTY;
        try
        {
            String strURI = getURI( strIndex );
            strResponse = _connexion.PUT( strURI, strJsonMappings );
        }
        catch( HttpAccessException ex )
        {
            throw new ElasticClientException( "ElasticLibrary : Error creating mappings : " + ex.getMessage( ), ex );
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
        strURI = ( strURI.endsWith( Constants.URL_PATH_SEPARATOR ) ) ? strURI : strURI + Constants.URL_PATH_SEPARATOR;
        if ( StringUtils.isNotEmpty( strIndex ) )
        {
            strURI = ( ( strIndex.endsWith( Constants.URL_PATH_SEPARATOR ) ) ? strURI + strIndex : strURI + strIndex + Constants.URL_PATH_SEPARATOR );
        }
        if ( StringUtils.isNotEmpty( strType ) )
        {
            strURI = ( ( strType.endsWith( Constants.URL_PATH_SEPARATOR ) ) ? strURI + strType : strURI + strType + Constants.URL_PATH_SEPARATOR );
        }
        return strURI;
    }
}
