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
package info.rmapproject.transformapi.utils;

/**
 * Constants for Transform API service
 */
public final class Constants  {

	/** RDF media type to do transform through. */
  	public static final String TRANSFORM_MEDIA_TYPE = "text/turtle";
	  
  	/** Non-rdf media type to create response in. */
  	public static final String RESPONSE_MEDIA_TYPE = "text/plain";
	
	/** File path for API props. */
	public static final String RMAP_API_PROPS_FILE = "rmapapi";
	  
  	/** API path property key. */
  	public static final String API_PATH_KEY = "rmapapi.path";
	  
	/** File path for Transform Accounts properties. */
	public static final String TRANSFORMACCTS_PROPFILE = "transform-accounts";
	  
  	/** Property file for transform accounts settings. */
  	public static final String TRANSFORMACCTS_PROPPREFIX = "transform-accounts.";
	  
	/** File path for error message text. */
	public static final String ERROR_MSGS_PROPS_FILE = "transform_error_msgs";
	  
	 /**
  	 * Instantiates a new constants.
  	 */
  	private Constants(){
		    //this prevents even the native class from calling this ctor as well :
		    throw new AssertionError();
		  }
}
