/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import fr.paris.lutece.plugins.libraryelastic.business.search.BoolQuery;
import fr.paris.lutece.plugins.libraryelastic.business.search.MatchLeaf;
import fr.paris.lutece.plugins.libraryelastic.business.search.SearchRequest;
import fr.paris.lutece.plugins.libraryelastic.business.suggest.CompletionSuggestRequest;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Elastic Test
 */
public class ElasticTest
{
    private static final String INDEX = "testlibraryindex";
    private static final String TYPE = "mydoc";
    private Elastic _elastic;

    /**
     * @throws InitializationError
     * @throws IOException
     */
    public ElasticTest( ) throws InitializationError, IOException
    {
        super( );
        AppPropertiesService.load( getClass( ).getResourceAsStream( "/elastic.properties" ) );
        this._elastic = new Elastic( AppPropertiesService.getProperty( "elastic.url" ) );
    }

    @Before
    public void createIndex( ) throws IOException, ElasticClientException
    {
        String strJsonMappings = IOUtils.toString( getClass( ).getResourceAsStream( "/mapping.json" ), StandardCharsets.UTF_8 );
        _elastic.createMappings( INDEX, strJsonMappings );
    }

    @After
    public void deleteIndex( ) throws ElasticClientException
    {
        _elastic.deleteIndex( INDEX );
    }

    /**
     * Test of create and delete a document, of class Elastic.
     * 
     * @throws ElasticClientException
     */
    @Test
    public void testCreateDeleteDocument( ) throws ElasticClientException
    {
        System.out.println( "create" );
        Object object = new MyDoc( "Hello Elastic !", "Bye" );
        String expResult = _elastic.create( INDEX, "my_id_001", object );
        System.out.println( expResult );
        System.out.println( "delete" );
        expResult = _elastic.deleteDocument( INDEX, "my_id_001" );
        System.out.println( expResult );
    }

    /**
     * Test of search and suggest method, of class Elastic.
     * 
     * @throws ElasticClientException
     * @throws InterruptedException
     */
    @Test
    public void testSearchAndSuggest( ) throws ElasticClientException, InterruptedException
    {
        Object object = new MyDoc( "Hello Elastic !", "Bye" );
        String expResult = _elastic.create( INDEX, object );
        System.out.println( expResult );

        // waiting object creation in ES
        Thread.sleep( 1000 );

        System.out.println( "search" );
        SearchRequest search = new SearchRequest( );
        BoolQuery query = new BoolQuery( );
        query.addShould( new MatchLeaf( "message", "Hello" ) );
        search.setSearchQuery( query );
        expResult = _elastic.search( INDEX, search );
        System.out.println( expResult );

        System.out.println( "suggest" );
        CompletionSuggestRequest suggest = new CompletionSuggestRequest( );
        suggest.setMatchType( "text" );
        suggest.setMatchValue( "Hello" );
        expResult = _elastic.suggest( INDEX, suggest );
        System.out.println( expResult );
    }

    class MyDoc
    {
        private String _strMessage;
        private String _strNotMessage;
        private Map<String, String> _mapSuggest;

        MyDoc( String strMessage, String strNotMessage )
        {
            _strMessage = strMessage;
            _strNotMessage = strNotMessage;
            _mapSuggest = new HashMap<String, String>( );
            _mapSuggest.put( "input", _strMessage );
            // output not managed for ES > 5.0
            // _mapSuggest.put( "output", "output-"+_strMessage );
        }

        /**
         * Returns the Message
         *
         * @return The Message
         */
        public String getMessage( )
        {
            return _strMessage;
        }

        /**
         * Sets the Message
         *
         * @param strMessage
         *            The Message
         */
        public void setMessage( String strMessage )
        {
            _strMessage = strMessage;
        }

        /**
         * Returns the NotMessage
         *
         * @return The NotMessage
         */
        public String getNotMessage( )
        {
            return _strNotMessage;
        }

        /**
         * Sets the NotMessage
         *
         * @param strNotMessage
         *            The NotMessage
         */
        public void setNotMessage( String strNotMessage )
        {
            _strNotMessage = strNotMessage;
        }

        /**
         * @return the mapSuggest
         */
        public Map<String, String> getSuggest( )
        {
            return _mapSuggest;
        }

        /**
         * @param mapSuggest
         *            the mapSuggest to set
         */
        public void setSuggest( Map<String, String> mapSuggest )
        {
            this._mapSuggest = mapSuggest;
        }
    }

}
