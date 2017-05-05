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

import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 * The Class ElasticConnexion.
 */
public final class ElasticConnexion
{
    /** The _client. */
    private HttpAccess _clientHttp = new HttpAccess( );

    /**
     * Sent to elastic put.
     *
     * @param strURI
     *            the uri
     * @param strJSON
     *            the json
     * @return the string
     * @throws fr.paris.lutece.util.httpaccess.HttpAccessException
     */
    public String PUT( String strURI, String strJSON ) throws HttpAccessException
    {
        return _clientHttp.doPutJSON( strURI, strJSON, null, null );
    }

    /**
     * Sent to elastic post.
     *
     * @param strURI
     *            the uri
     * @param strJSON
     *            the json
     * @throws HttpAccessException
     *             http access exception
     * @return the string
     */
    public String POST( String strURI, String strJSON ) throws HttpAccessException
    {
        return _clientHttp.doPostJSON( strURI, strJSON, null, null );
    }

    /**
     * Sent to elastic delete.
     *
     * @param strURI
     *            the uri
     * @throws HttpAccessException
     *             http access exception
     * @return the string
     */
    public String DELETE( String strURI ) throws HttpAccessException
    {
        return _clientHttp.doDelete( strURI, null, null, null, null );
    }

}
