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

import info.rmapproject.transformapi.exception.ErrorCode;
import info.rmapproject.transformapi.exception.RMapTransformApiException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.MissingResourceException;


/**
 * Utils for RMap Transform API
 *
 * @author khanson
 */
public class Utils {
	
	/** The api path. */
	private static String apiPath;
	
    /**
     * True if properties are initialized
     */
    private static boolean isInitialized = false;
    
	/**
	 * Initializes the Transform API properties
	 *
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	protected static void init() throws RMapTransformApiException{
		try {
			apiPath = ConfigUtils.getPropertyValue(Constants.RMAP_API_PROPS_FILE, Constants.API_PATH_KEY);
			isInitialized=true;
		}
		catch(MissingResourceException me){
			throw new RMapTransformApiException(ErrorCode.ER_RMAP_API_PROPERTIES_FILENOTFOUND);
			}
		catch (Exception e){RMapTransformApiException.wrap(e, ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);}
	}
		
	/**
	 * Get Base URL from properties file.
	 *
	 * @return the API path
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String getApiPath() throws RMapTransformApiException {
		if (!isInitialized){
			init();
		}
		return apiPath;
	}
	
	/**
	 * Get stmts API base URL.
	 *
	 * @return the stmt base url
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String getStmtBaseUrl() throws RMapTransformApiException {
		String stmtBaseUrl = getApiPath() + "/stmts/";
		return stmtBaseUrl;
	}
	
	/**
	 * Get DiSCO API base URL.
	 *
	 * @return the disco base url
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String getDiscoBaseUrl() throws RMapTransformApiException {
		String discoBaseUrl = getApiPath() + "/discos/";
		return discoBaseUrl;
	}

	
	/**
	 * Get Event API base URL.
	 *
	 * @return the event base url
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String getEventBaseUrl() throws RMapTransformApiException {
		String eventBaseUrl = getApiPath() + "/events/";
		return eventBaseUrl;
	}

	/**
	 * Get Agent API base URL.
	 *
	 * @return the agent base url
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String getAgentBaseUrl() throws RMapTransformApiException {
		String agentBaseUrl = getApiPath() + "/agents/";
		return agentBaseUrl;
	}	

	
	/**
	 * Get Resource API base URL.
	 *
	 * @return the Resource base URL
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String getResourceBaseUrl() throws RMapTransformApiException {
		String resourceBaseUrl = getApiPath() + "/resources/";
		return resourceBaseUrl;
	}

	/**
	 * Appends DiSCO URI to DiSCO API URL.
	 *
	 * @param uri the DiSCO URI
	 * @return the URL for the DiSCO
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String makeDiscoUrl(String uri) throws RMapTransformApiException {
		String discoUrl = appendEncodedUriToURL(getDiscoBaseUrl(),uri);
		return discoUrl;
	}
	
	/**
	 * Appends Event URI to Event API URL.
	 *
	 * @param uri the Event URI
	 * @return the URL for the Event
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String makeEventUrl(String uri) throws RMapTransformApiException {
		String eventUrl = appendEncodedUriToURL(getEventBaseUrl(),uri);
		return eventUrl;
	}
	
	/**
	 * Appends Agent URI to Agent API URL.
	 *
	 * @param uri the Agent URI
	 * @return the URL for the Agent
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String makeAgentUrl(String uri) throws RMapTransformApiException {
		String agentUrl = appendEncodedUriToURL(getAgentBaseUrl(),uri);
		return agentUrl;
	}

	/**
	 * Appends Resource URI to Resource API URL.
	 *
	 * @param uri the Resource URI
	 * @return the URL for the Resource
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public static String makeResourceUrl(String uri) throws RMapTransformApiException {
		String resourceUrl = appendEncodedUriToURL(getResourceBaseUrl(),uri);
		return resourceUrl;
	}

	/**
	 * Constructs the URL path that has the triple encoded as path params in the format
	 * http://urlexample.org/s/p/o
	 *
	 * @param s the subject
	 * @param p the predicate
	 * @param o the object
	 * @return the URL containing the triple parameters
	 * @throws RMapApiException the RMap API exception
	 */
	public static String makeStmtUrl(String s, String p, String o) throws RMapTransformApiException {
		String stmtUrl = appendEncodedUriToURL(getStmtBaseUrl(),s) + "/";
		stmtUrl = appendEncodedUriToURL(stmtUrl,p) + "/";
		stmtUrl = appendEncodedUriToURL(stmtUrl,o);
		return stmtUrl;
	}
	
	
	
	/**
	 * Appends encoded URI to an API URL.
	 *
	 * @param baseURL the base URL
	 * @param objUri the object URI
	 * @return a URL as string
	 * @throws RMapApiException the RMap API exception
	 */
	public static String appendEncodedUriToURL(String baseURL, String objUri) throws RMapTransformApiException {
		String url = null;
		try {
			//may already been encoded, so let's decode first to make sure we aren't double encoding
			objUri = URLDecoder.decode(objUri,"UTF-8");
			//now encode!
			url = baseURL + URLEncoder.encode(objUri,"UTF-8");
		}
		catch (Exception e)	{
			throw new RMapTransformApiException(ErrorCode.ER_CANNOT_ENCODE_URL);
		}
		return url;
	}

	
}
