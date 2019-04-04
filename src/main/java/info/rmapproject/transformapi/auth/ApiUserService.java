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

import info.rmapproject.transformapi.exception.RMapTransformApiException;

import java.net.URI;

import org.apache.cxf.configuration.security.AuthorizationPolicy;


/**
 * Manages interaction between rmap-auth and the API.  Used to validate API keys, and     
 * retrieve agent information to be associated with RMap objects.
 * @author khanson
 *
 */
public interface ApiUserService {

	
	/**
	 * Gets current authorization policy (contains authentication information)
	 * @return the AuthorizationPolicy
	 * @throws RMapTransformApiException
	 */
	public AuthorizationPolicy getCurrentAuthPolicy()
			throws RMapTransformApiException;

	/**
	 * Get current user Access Key
	 * @return current access key
	 * @throws RMapTransformApiException
	 */
	public String getAccessKey() throws RMapTransformApiException;

	/**
	 * Get current user Secret
	 * @return current user secret
	 * @throws RMapTransformApiException
	 */
	public String getSecret() throws RMapTransformApiException;

	/**
	 * Retrieves RMap:Agent URI associated with the current user. If a user not found, throws exception
	 * @return URI of current RMap System Agent
	 * @throws RMapTransformApiException
	 */
	public URI getCurrentSystemAgentUri() throws RMapTransformApiException;

	/**
	 * Retrieves RMap:Agent URI associated with the user/pass provided for use in the event
	 * if the User has an Agent ID that is not yet in the triplestore an Agent is created for them. 
	 * @param key the key
	 * @param secret the secret
	 * @return URI of RMap System Agent
	 * @throws RMapTransformApiException
	 */
	public URI getSystemAgentUri(String key, String secret) throws RMapTransformApiException;
		
	/**
	 * Validates the key/secret combination. If it is invalid an Exception is thrown.
	 * @param accessKey the key
	 * @param secret the secret
	 * @throws RMapTransformApiException
	 */
	public void validateKey(String accessKey, String secret) throws RMapTransformApiException;

}