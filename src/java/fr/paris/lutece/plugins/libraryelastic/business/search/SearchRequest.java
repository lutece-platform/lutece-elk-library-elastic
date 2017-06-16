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
package fr.paris.lutece.plugins.libraryelastic.business.search;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JSON object for representing a search request
 */
public class SearchRequest
{
    private AbstractSearchQuery _searchQuery = null;
    private Integer _nSize = null;

    /**
     * @return the searchQuery
     */
    public AbstractSearchQuery getSearchQuery( )
    {
        return _searchQuery;
    }

    /**
     * @param searchQuery
     *            the searchQuery to set
     */
    public void setSearchQuery( AbstractSearchQuery searchQuery )
    {
        this._searchQuery = searchQuery;
    }

    /**
     * @return the nSize
     */
    public Integer getSize( )
    {
        return _nSize;
    }

    /**
     * @param nSize
     *            the nSize to set
     */
    public void setSize( Integer nSize )
    {
        this._nSize = nSize;
    }

    /**
     * @return a JSON ObjectNode which can be use with ObjectMapper
     */
    public ObjectNode mapToNode( )
    {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = new ObjectNode( factory );
        ObjectNode query = _searchQuery.mapToNode( factory );
        root.set( "query", query );
        if ( _nSize != null )
        {
            root.put( "size", _nSize );
        }

        return root;
    }
}
