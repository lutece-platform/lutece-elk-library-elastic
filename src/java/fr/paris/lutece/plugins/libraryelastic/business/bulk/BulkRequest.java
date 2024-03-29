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
package fr.paris.lutece.plugins.libraryelastic.business.bulk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Bulk Request class to do bulk operations on Elastic Search with only one request
 */
public class BulkRequest
{
    private Map<AbstractSubRequest, Object> _mapSubAction = new HashMap<AbstractSubRequest, Object>( );

    /**
     * Set the SubActions map
     * 
     * @param mapSubAction
     *            the map of subactions of bulking request
     */
    public void setMapSubAction( Map<AbstractSubRequest, Object> mapSubAction )
    {
        _mapSubAction = mapSubAction;
    }

    /**
     * Add an entry to the subAction map
     * 
     * @param action
     *            The action
     * @param object
     *            The object on which the action will be done
     */
    public void addAction( AbstractSubRequest action, Object object )
    {
        _mapSubAction.put( action, object );
    }

    /**
     * Get body of bulking request
     * 
     * @param mapper
     *            the Object Mapper
     * @return the body of a bulk request, based on multiple index/create/delete actions
     * @throws JsonProcessingException
     */
    public String getBulkBody( ObjectMapper mapper ) throws JsonProcessingException
    {
        StringBuilder builderBulkBody = new StringBuilder( );

        for ( Map.Entry<AbstractSubRequest, Object> entry : _mapSubAction.entrySet( ) )
        {
            builderBulkBody.append( entry.getKey( ).getNodeAction( JsonNodeFactory.instance ) );
            builderBulkBody.append( "\n" );

            Object object = entry.getValue( );

            if ( object instanceof String )
            {
                builderBulkBody.append( (String) object );
            }
            else
            {
                builderBulkBody.append( mapper.writeValueAsString( object ) );
            }

            builderBulkBody.append( "\n" );
        }

        return builderBulkBody.toString( );
    }

}
