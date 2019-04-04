/*******************************************************************************
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This software was produced as part of the RMap Project (http://rmap-project.info),
 * The RMap Project was funded by the Alfred P. Sloan Foundation and is a 
 * collaboration between Data Conservancy, Portico, and IEEE.
 *******************************************************************************/
package info.rmapproject.transformapi.auth;

import info.rmapproject.auth.exception.RMapAuthException;
import info.rmapproject.transformapi.exception.ErrorCode;
import info.rmapproject.transformapi.exception.RMapTransformApiException;

import java.net.URI;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.springframework.context.annotation.Scope;

/**
 * Implementation of API User Service
 */
@Scope("request")
public class ApiUserServiceSkipAuthImpl implements ApiUserService {
	
	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getCurrentAuthPolicy()
	 */
	@Override
	public AuthorizationPolicy getCurrentAuthPolicy() throws RMapTransformApiException {
		AuthorizationPolicy authorizationPolicy = null;
		Message message = JAXRSUtils.getCurrentMessage();
		authorizationPolicy = (AuthorizationPolicy)message.get(AuthorizationPolicy.class);
	    if (authorizationPolicy == null) {
	        throw new RMapTransformApiException(ErrorCode.ER_COULD_NOT_RETRIEVE_AUTHPOLICY);
	        }
	    return authorizationPolicy;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getAccessKey()
	 */
	@Override
	public String getAccessKey() throws RMapTransformApiException {
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
	    return policy.getUserName();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSecret()
	 */
	@Override
	public String getSecret() throws RMapTransformApiException {
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
	    return policy.getPassword();
	}
		
	
    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSystemAgentUriForEvent()
	 */
	public URI getCurrentSystemAgentUri() throws RMapTransformApiException {
		return null;
	}
	
    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSystemAgentUri(String, String)
	 */
	@Override
	public URI getSystemAgentUri(String key, String secret) throws RMapTransformApiException {
		return null;
	}
		
    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#validateKey(String, String)
	 */
	@Override
	public void validateKey(String accessKey, String secret)
			throws RMapTransformApiException {
		try {
			//ok
		}
		catch (RMapAuthException e) {
			throw RMapTransformApiException.wrap(e, ErrorCode.ER_INVALID_USER_TOKEN_PROVIDED);
		}	
	}


}
