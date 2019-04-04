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
package info.rmapproject.transformapi.exception;

import info.rmapproject.transformapi.utils.ConfigUtils;
import info.rmapproject.transformapi.utils.Constants;

import javax.ws.rs.core.Response.Status;

/**
 * Custom error codes for RMap Transform API
 * @author khanson
 */
public enum ErrorCode {		
	//400**** Bad Request
	ER_USER_HAS_NO_AGENT (Status.BAD_REQUEST, 4005101),
	ER_CANNOT_FIND_TRANSFORM_TYPE (Status.BAD_REQUEST,4005102),
	ER_NO_TRANSFORM_ID_PROVIDED (Status.BAD_REQUEST, 4005103),
	
	//401**** Unauthorized
	ER_USER_NOT_AUTHENTICATED (Status.UNAUTHORIZED, 4015101),
	ER_NO_USER_TOKEN_PROVIDED (Status.UNAUTHORIZED, 4015102),
	ER_INVALID_USER_TOKEN_PROVIDED (Status.UNAUTHORIZED, 4015103),
	ER_NO_SYSTEMAGENT_PROVIDED (Status.UNAUTHORIZED, 4015104), 
	ER_INVALID_SYSTEMAGENT_PROVIDED (Status.UNAUTHORIZED, 4015105), 
	ER_SYSTEMAGENT_DELETED (Status.UNAUTHORIZED, 4015106), 
	ER_COULD_NOT_RETRIEVE_AUTHPOLICY (Status.UNAUTHORIZED,4015107),
	ER_USER_NOT_AUTHORIZED_FOR_THIS_TRANSFORM (Status.UNAUTHORIZED,4015108),

	//500**** Internal Server Errors
	//5001*** Internal Server Errors that probably originate in API code
	ER_FAILED_TO_INIT_API_RESP_MGR (Status.INTERNAL_SERVER_ERROR, 5005101),
	ER_RETRIEVING_API_HEAD(Status.INTERNAL_SERVER_ERROR,5005102),
	ER_RETRIEVING_API_OPTIONS(Status.INTERNAL_SERVER_ERROR,5005103),
	ER_RMAP_API_PROPERTIES_FILENOTFOUND (Status.INTERNAL_SERVER_ERROR, 5005104),
	ER_CANNOT_ENCODE_URL (Status.INTERNAL_SERVER_ERROR, 5005105),
	ER_CANNOT_DECODE_URL (Status.INTERNAL_SERVER_ERROR, 5005106),
	ER_FAILED_TO_INIT_API_USER_SERVICE (Status.INTERNAL_SERVER_ERROR, 5005107),
	ER_TRANSFORM_CONFIG_NOT_FOUND (Status.INTERNAL_SERVER_ERROR, 5005108),
	ER_COULD_NOT_RETRIEVE_RECORD_TO_TRANSFORM (Status.INTERNAL_SERVER_ERROR, 5005109),
	
	//5003*** Internal Server Errors originating in Auth RMap Service
	ER_USER_AGENT_COULD_NOT_BE_RETRIEVED (Status.INTERNAL_SERVER_ERROR,5005201),
	ER_INVALID_AGENTID_FOR_USER(Status.INTERNAL_SERVER_ERROR,5005202),
	ER_INVALID_KEYURI_FOR_USER(Status.INTERNAL_SERVER_ERROR, 5005203),
	
	//5009*** Generic Internal Server Errors
	ER_CORE_GENERIC_RMAP_EXCEPTION (Status.INTERNAL_SERVER_ERROR,5009201),
	ER_UNKNOWN_SYSTEM_ERROR (Status.INTERNAL_SERVER_ERROR,5009202); 

	/** Error code number. */
	private final int number;
	
	/**  HTTP Response status. */
	private final Status status;
	
	/**
	 * Message corresponding to error code.
	 */
	private String message;


	/**
	 * Initiate error code object.
	 *
	 * @param status the HTTP response status
	 * @param number the error number
	 */
	private ErrorCode (Status status, int number) {
		this.number = number;
		this.status = status;
	}

	/**
	 * Get error code number.
	 *
	 * @return the number
	 */
	public int getNumber()  {
		return number;
	}
	
	/**
	 * Get HTTP response status for error code.
	 *
	 * @return the response status
	 */
	public Status getStatus()  {
		return status;
	}
	
	/**
	 * Retrieves the message that corresponds to the error code.
	 * String messages are configured in the file named in the Constants.ERROR_MSGS_PROPS_FILE property.
	 * @return String
	 */
	public String getMessage() {
        if (message == null) {
        	String key = this.getStatus().getStatusCode() + "_" + this.toString();
    		try {	
    	        message = (String) ConfigUtils.getPropertyValue(Constants.ERROR_MSGS_PROPS_FILE, key);
    		} 
    		catch(Exception e){
    			message = getDefaultText(this);
    			if (message == null){
    				message = "";
    			}
    		}   
        }
        return message;
	}

	/**
	 * If all else fails, a simple default error is returned in English.
	 *
	 * @param errorCode the error code
	 * @return error message string
	 */
	private static String getDefaultText(ErrorCode errorCode){
		String defaultText = "";
		switch (errorCode.getStatus()) {
		case GONE:  defaultText = "The requested item has been deleted.";
        	break;
		case NOT_FOUND:  defaultText = "The requested item cannot be found.";
    		break;
		case BAD_REQUEST:  defaultText = "The request was not formatted correctly. Please check the request and try again.";
			break;
		case INTERNAL_SERVER_ERROR:  defaultText = "A system error occurred.";
    		break;
        default: defaultText = "An error occurred.";
        	break;	
		}
		return defaultText;
	}
	
}

