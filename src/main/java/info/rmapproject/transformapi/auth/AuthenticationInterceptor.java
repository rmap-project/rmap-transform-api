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

import info.rmapproject.transformapi.exception.ErrorCode;
import info.rmapproject.transformapi.exception.RMapTransformApiException;
import info.rmapproject.transformapi.exception.RMapTransformApiExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 * Intercepts interactions with the API to authenticate the user and 
 * verify they are authorized to access the API.
 *
 * @author khanson
 */
@Scope("request")
public class AuthenticationInterceptor extends AbstractPhaseInterceptor<Message> {

	/** The API User Service. */
	private ApiUserService apiUserService;

	/**
	 * Autowired from Spring configuration - sets apiUserService class.
	 *
	 * @param apiUserService the new api user service
	 * @throws RMapTransformApiException the r map transform api exception
	 */
    @Autowired
    public void setApiUserService(ApiUserService apiUserService) throws RMapTransformApiException {
    	if (apiUserService==null) {
			throw new RMapTransformApiException(ErrorCode.ER_FAILED_TO_INIT_API_USER_SERVICE);			
    	} else {
    		this.apiUserService = apiUserService;
		}
	}
    
    /**
     * Instantiates a new authentication interceptor.
     */
    public AuthenticationInterceptor() {
        super(Phase.RECEIVE);
    }


    /**
     * Gets basic authentication information from request and validates key.
     *
     * @param message the message
     */
    public void handleMessage(Message message) {

	    try {   
	    	//only authenticate if you are trying to write to the db... 
	    	HttpServletRequest req = (HttpServletRequest) message.get("HTTP.REQUEST");
	    	String method = req.getMethod();
	    	
	    	if (method!=HttpMethod.GET && method!=HttpMethod.OPTIONS && method!=HttpMethod.HEAD){
    	 
		    	AuthorizationPolicy policy = apiUserService.getCurrentAuthPolicy();
		    	String accessKey = policy.getUserName();
		    	String secret = policy.getPassword();
		    
				if (accessKey==null || accessKey.length()==0
						|| secret==null || secret.length()==0)	{
			    	throw new RMapTransformApiException(ErrorCode.ER_NO_USER_TOKEN_PROVIDED);
				}		
			
				apiUserService.validateKey(accessKey, secret);
	    	}
	    	
	    } catch (RMapTransformApiException ex){ 
	    	//generate a response to intercept default message
	    	RMapTransformApiExceptionHandler exceptionhandler = new RMapTransformApiExceptionHandler();
	    	Response response = exceptionhandler.toResponse(ex);
	    	message.getExchange().put(Response.class, response);   	
	    }
		
    }
		
}
