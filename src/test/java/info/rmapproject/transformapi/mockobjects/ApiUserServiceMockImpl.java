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
package info.rmapproject.transformapi.mockobjects;

import info.rmapproject.auth.exception.RMapAuthException;
import info.rmapproject.auth.model.User;
import info.rmapproject.auth.service.RMapAuthService;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.transformapi.auth.ApiUserService;
import info.rmapproject.transformapi.exception.ErrorCode;
import info.rmapproject.transformapi.exception.RMapTransformApiException;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
/**
 * Mock Api User Service for testing
 */
@ContextConfiguration({ "classpath*:/spring-*-context.xml" })
public class ApiUserServiceMockImpl implements ApiUserService {

	/** The authorization policy. */
	private AuthorizationPolicy policy;
	
	/** The test user name. */
	private static final String TEST_USER = "portico";
	
	/** The test user password. */
	private static final String TEST_PASS = "portico";
	
	/** The RMap Auth Service. */
	@Autowired
	private RMapAuthService rmapAuthService;
	
		
	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getCurrentAuthPolicy()
	 */
	@Override
	public AuthorizationPolicy getCurrentAuthPolicy() throws RMapTransformApiException {
		this.policy = new AuthorizationPolicy();
		this.policy.setUserName(TEST_USER);
		this.policy.setPassword(TEST_PASS);
	    return this.policy;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getAccessKey()
	 */
	@Override
	public String getAccessKey() throws RMapTransformApiException {
		//NOTE: if need both key and secret, better to retrieve AuthPolicy to prevent multiple calls to retrieve the Policy.
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
	    return policy.getUserName();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSecret()
	 */
	@Override
	public String getSecret() throws RMapTransformApiException {
		//NOTE: if need both key and secret, better to retrieve AuthPolicy to prevent multiple calls to retrieve the Policy.
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
	    return policy.getPassword();
	}
		


    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSystemAgentUriForEvent()
	 */
	public URI getCurrentSystemAgentUri() throws RMapTransformApiException {
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
		String key = policy.getUserName();
		String secret = policy.getPassword();
		return getSystemAgentUri(key, secret);
	}
	
    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSystemAgentUri(String, String)
	 */
	@Override
	public URI getSystemAgentUri(String key, String secret) throws RMapTransformApiException {
		URI sysAgentUri = null;
		
		try {
			User user = rmapAuthService.getUserByKeySecret(key, secret);
			String agentUri = user.getRmapAgentUri();
			if (agentUri==null){
				//no agent id
				throw new RMapTransformApiException(ErrorCode.ER_USER_HAS_NO_AGENT);				
			}
			sysAgentUri = new URI(agentUri);

		} catch (URISyntaxException ex) {
			throw new RMapTransformApiException(ex, ErrorCode.ER_USER_AGENT_COULD_NOT_BE_RETRIEVED);
		} catch (RMapException ex) {
			throw new RMapTransformApiException(ex, ErrorCode.ER_USER_AGENT_COULD_NOT_BE_RETRIEVED);
		} catch (RMapAuthException ex) {
			throw RMapTransformApiException.wrap(ex, ErrorCode.ER_USER_AGENT_COULD_NOT_BE_RETRIEVED);
		}  
		
		return sysAgentUri;
	}
		
	/* (non-Javadoc)
	 * @see info.rmapproject.transformapi.auth.ApiUserService#validateKey(java.lang.String, java.lang.String)
	 */
	@Override
	public void validateKey(String accessKey, String secret)
			throws RMapTransformApiException {
		try {
			rmapAuthService.validateApiKey(accessKey, secret);
		}
		catch (RMapAuthException e) {
			throw RMapTransformApiException.wrap(e, ErrorCode.ER_INVALID_USER_TOKEN_PROVIDED);
		}	
	}

}
