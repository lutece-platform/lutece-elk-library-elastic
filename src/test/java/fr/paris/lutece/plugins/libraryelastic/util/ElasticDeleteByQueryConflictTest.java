/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntFunction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.sun.net.httpserver.HttpServer;

import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * _delete_by_query while documents of the query are written by a concurrent indexing: Elasticsearch answers 409
 * (version_conflict) unless conflicts=proceed is asked, and then reports the documents it could not delete in
 * version_conflicts. The documents in conflict must not be left behind.
 */
@TestInstance( Lifecycle.PER_CLASS )
public class ElasticDeleteByQueryConflictTest
{
    private static final String INDEX = "appointmentslotdatasource";
    private static final String QUERY = "{\"query\":{\"term\":{\"idForm\":1455}}}";
    private static final String CONFLICT_409 = "{\"took\":3,\"timed_out\":false,\"total\":5,\"deleted\":3,\"version_conflicts\":2,"
            + "\"failures\":[{\"index\":\"appointmentslotdatasource\",\"id\":\"1455_1\",\"cause\":{\"type\":\"version_conflict_engine_exception\"},\"status\":409}]}";

    private HttpServer _server;
    private final List<String> _listRequestUris = new CopyOnWriteArrayList<>( );

    @BeforeAll
    public void loadProperties( ) throws IOException
    {
        AppPropertiesService.load( getClass( ).getResourceAsStream( "/elastic.properties" ) );
    }

    @AfterEach
    public void stopServer( )
    {
        if ( _server != null )
        {
            _server.stop( 0 );
        }
        _listRequestUris.clear( );
    }

    /**
     * Stub of Elasticsearch: without conflicts=proceed the call answers 409, as a real cluster does during a concurrent
     * indexing; with it, the n-th call answers the body given by the function.
     */
    private Elastic startServer( IntFunction<String> proceedAnswer ) throws IOException
    {
        _server = HttpServer.create( new InetSocketAddress( "127.0.0.1", 0 ), 0 );
        _server.createContext( "/", exchange -> {
            String strUri = exchange.getRequestURI( ).toString( );
            _listRequestUris.add( strUri );
            exchange.getRequestBody( ).readAllBytes( );
            boolean bProceed = strUri.contains( "conflicts=proceed" );
            String strBody = bProceed ? proceedAnswer.apply( _listRequestUris.size( ) ) : CONFLICT_409;
            byte [ ] body = strBody.getBytes( StandardCharsets.UTF_8 );
            exchange.getResponseHeaders( ).add( "Content-Type", "application/json" );
            exchange.sendResponseHeaders( bProceed ? 200 : 409, body.length );
            try ( OutputStream out = exchange.getResponseBody( ) )
            {
                out.write( body );
            }
        } );
        _server.start( );
        return new Elastic( "http://127.0.0.1:" + _server.getAddress( ).getPort( ) + "/" );
    }

    /**
     * The documents in conflict are deleted by a second pass, the call succeeds.
     */
    @Test
    public void testDocumentsInConflictAreDeletedAgain( ) throws Exception
    {
        Elastic elastic = startServer( n -> n == 1 ? "{\"deleted\":3,\"version_conflicts\":2,\"failures\":[]}"
                : "{\"deleted\":2,\"version_conflicts\":0,\"failures\":[]}" );

        elastic.deleteByQuery( INDEX, QUERY );

        assertEquals( 2, _listRequestUris.size( ), "a second _delete_by_query for the documents in conflict" );
        assertTrue( _listRequestUris.get( 0 ).contains( "_delete_by_query" ) && _listRequestUris.get( 0 ).contains( "conflicts=proceed" ),
                _listRequestUris.get( 0 ) );
    }

    /**
     * Documents still in conflict after the passes: the failure is reported, not hidden.
     */
    @Test
    public void testDocumentsStillInConflictAreReported( ) throws Exception
    {
        Elastic elastic = startServer( n -> "{\"deleted\":0,\"version_conflicts\":1,\"failures\":[]}" );

        ElasticClientException ex = assertThrows( ElasticClientException.class, ( ) -> elastic.deleteByQuery( INDEX, QUERY ) );

        assertTrue( ex.getMessage( ).contains( "version_conflicts" ), ex.getMessage( ) );
    }
}
