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
package fr.paris.lutece.plugins.libraryelastic.business.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JSON object for representing a Bool Query in search request
 */
public class BoolQuery extends AbstractSearchQuery
{
    List<AbstractSearchLeaf> _listMust;
    List<AbstractSearchLeaf> _listFilter;
    List<AbstractSearchLeaf> _listShould;
    List<AbstractSearchLeaf> _listMustNot;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNodeName( )
    {
        return "bool";
    }

    /**
     * @return the listMust
     */
    public List<AbstractSearchLeaf> getMust( )
    {
        return _listMust;
    }

    public void addMust( AbstractSearchLeaf leaf )
    {
        if ( _listMust == null )
        {
            _listMust = new ArrayList<AbstractSearchLeaf>( );
        }
        _listMust.add( leaf );
    }

    /**
     * @return the listFilter
     */
    public List<AbstractSearchLeaf> getFilter( )
    {
        return _listFilter;
    }

    /**
     * @param leaf
     *            to add to listFilter
     */
    public void addFilter( AbstractSearchLeaf leaf )
    {
        if ( _listFilter == null )
        {
            _listFilter = new ArrayList<AbstractSearchLeaf>( );
        }
        _listFilter.add( leaf );
    }

    /**
     * @return the listShould
     */
    public List<AbstractSearchLeaf> getShould( )
    {
        return _listShould;
    }

    /**
     * @param leaf
     *            to add to listShould
     */
    public void addShould( AbstractSearchLeaf leaf )
    {
        if ( _listShould == null )
        {
            _listShould = new ArrayList<AbstractSearchLeaf>( );
        }
        _listShould.add( leaf );
    }

    /**
     * @return the listMustNot
     */
    public List<AbstractSearchLeaf> getMustNot( )
    {
        return _listMustNot;
    }

    /**
     * @param leaf
     *            to add to listMustNot
     */
    public void addMustNot( AbstractSearchLeaf leaf )
    {
        if ( _listMustNot == null )
        {
            _listMustNot = new ArrayList<AbstractSearchLeaf>( );
        }
        _listMustNot.add( leaf );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectNode getNodeContent( JsonNodeFactory factory )
    {
        ObjectNode content = new ObjectNode( factory );

        if ( CollectionUtils.isNotEmpty( _listMust ) )
        {
            JsonNode must;
            if ( _listMust.size( ) == 1 )
            {
                must = _listMust.get( 0 ).mapToNode( factory );
            }
            else
            {
                must = new ArrayNode( factory );
                for ( AbstractSearchLeaf searchLeaf : _listMust )
                {
                    ( (ArrayNode) must ).add( searchLeaf.mapToNode( factory ) );
                }
            }
            content.set( "must", must );
        }

        if ( CollectionUtils.isNotEmpty( _listFilter ) )
        {
            JsonNode filter;
            if ( _listFilter.size( ) == 1 )
            {
                filter = _listFilter.get( 0 ).mapToNode( factory );
            }
            else
            {
                filter = new ArrayNode( factory );
                for ( AbstractSearchLeaf searchLeaf : _listFilter )
                {
                    ( (ArrayNode) filter ).add( searchLeaf.mapToNode( factory ) );
                }
            }
            content.set( "filter", filter );
        }

        if ( CollectionUtils.isNotEmpty( _listShould ) )
        {
            JsonNode should;
            if ( _listShould.size( ) == 1 )
            {
                should = _listShould.get( 0 ).mapToNode( factory );
            }
            else
            {
                should = new ArrayNode( factory );
                for ( AbstractSearchLeaf searchLeaf : _listShould )
                {
                    ( (ArrayNode) should ).add( searchLeaf.mapToNode( factory ) );
                }
            }
            content.set( "should", should );
        }

        if ( CollectionUtils.isNotEmpty( _listMustNot ) )
        {
            JsonNode mustNot;
            if ( _listMustNot.size( ) == 1 )
            {
                mustNot = _listMustNot.get( 0 ).mapToNode( factory );
            }
            else
            {
                mustNot = new ArrayNode( factory );
                for ( AbstractSearchLeaf searchLeaf : _listMustNot )
                {
                    ( (ArrayNode) mustNot ).add( searchLeaf.mapToNode( factory ) );
                }
            }
            content.set( "must_not", mustNot );
        }

        return content;
    }
}
