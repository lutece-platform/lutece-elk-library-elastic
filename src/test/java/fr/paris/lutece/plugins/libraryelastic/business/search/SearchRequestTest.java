/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.libraryelastic.business.search;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a DEV test which test nothing, just output json for control
 */
public class SearchRequestTest
{

    @Test
    public void testSearchWrapping( ) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper( );

        System.out.println( "wrapper" );
        BoolQuery query1 = new BoolQuery( );
        query1.addFilter( new TermLeaf( "fKey", "fValue" ) );
        query1.addMust( new TermLeaf( "mKey", "mValue" ) );
        query1.addMustNot( new MatchLeaf( "nKey", "nValue" ) );
        query1.addShould( new MatchLeaf( "sKey", "sValue" ) );
        SearchRequest request1 = new SearchRequest( );
        request1.setSearchQuery( query1 );

        BoolQuery query2 = new BoolQuery( );
        query2.addFilter( new TermLeaf( "fKey1", "fValue1" ) );
        query2.addFilter( new TermLeaf( "fKey2", "fValue2" ) );
        query2.addMust( new TermLeaf( "mKey1", "mValue1" ) );
        query2.addMust( new TermLeaf( "mKey2", "mValue2" ) );
        query2.addMustNot( new MatchLeaf( "nKey1", "nValue1" ) );
        query2.addMustNot( new MatchLeaf( "nKey2", "nValue2" ) );
        query2.addShould( new MatchLeaf( "sKey1", "sValue1" ) );
        query2.addShould( new MatchLeaf( "sKey2", "sValue2" ) );
        SearchRequest request2 = new SearchRequest( );
        request2.setSearchQuery( query2 );
        request2.setSize( 10 );

        System.out.println( "result whithout size" );
        System.out.println( mapper.writeValueAsString( request1.mapToNode( ) ) );

        System.out.println( "result whith size and multiple" );
        System.out.println( mapper.writeValueAsString( request2.mapToNode( ) ) );
    }
}
