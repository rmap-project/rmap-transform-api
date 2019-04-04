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
package info.rmapproject.transformapi.responsemgr;



import info.rmapproject.transformapi.auth.ApiUserService;
import info.rmapproject.transformapi.exception.ErrorCode;
import info.rmapproject.transformapi.exception.RMapTransformApiException;
import info.rmapproject.transformapi.utils.ConfigUtils;
import info.rmapproject.transformapi.utils.Constants;
import info.rmapproject.transformapi.utils.Utils;
import info.rmapproject.transformer.DiscoBuilder;
import info.rmapproject.transformer.DiscoBuilderFactory;
import info.rmapproject.transformer.TransformUtils;
import info.rmapproject.transformer.model.RecordType;
import info.rmapproject.transformer.osf.OsfClientService;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.openrdf.model.vocabulary.DC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 * Creates HTTP responses for RMap DiSCO REST API requests.
 *
 * @author khanson
 */
@Scope("prototype")
public class TransformResponseManager {

	/** The log. */
	private static final Logger log = LoggerFactory.getLogger(TransformResponseManager.class);
			
	/** The documentation link. */
	private static final String DOC_LINK="http://github.com/rmap-project/rmap-transform-api";
	
	/** The API User Service. */
	private final ApiUserService apiUserService;
	
	/**
	 * Constructor autowires the ApiUserService.
	 *
	 * @param apiUserService the api user service
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	@Autowired
	public TransformResponseManager(ApiUserService apiUserService) throws RMapTransformApiException {
		if (apiUserService ==null){
			throw new RMapTransformApiException(ErrorCode.ER_FAILED_TO_INIT_API_USER_SERVICE);
		}
		this.apiUserService = apiUserService;
	}
    
	/**
	 * Displays RMap Transform Service Options.
	 *
	 * @return HTTP Response
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public Response getTransformServiceOptions() throws RMapTransformApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<" + DOC_LINK + ">;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"RMap Transform API\"}")
					.header("Allow", "HEAD,OPTIONS,POST")
					.header("Link",linkRel)	
					.build();
			
			reqSuccessful = true;

		}
		catch (Exception ex){
			throw RMapTransformApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		finally{
			if (!reqSuccessful && response!=null) response.close();
		}
		return response;  
	}


	/**
	 * Displays Transform Service Options Header.
	 *
	 * @return HTTP Response
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public Response getTransformServiceHead() throws RMapTransformApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<" + DOC_LINK + ">;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,POST")
					.header("Link",linkRel)	
					.build();
			
			reqSuccessful = true;
		}
		catch (Exception ex){
			throw RMapTransformApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_HEAD);
		}
		finally{
			if (!reqSuccessful && response!=null) response.close();
		}
		return response; 
	}

	/**
	 * Performs transform of data to DiSCO and creates new RMap:DiSCO.
	 *
	 * @param transformType the transform type
	 * @param apiLookupId the source API lookup id
	 * @param origDiscoUri the original DiSCO URI
	 * @return HTTP Response
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
	public Response createOrUpdateRMapDiSCO(RecordType transformType, String apiLookupId, String origDiscoUri) throws RMapTransformApiException {
		boolean reqSuccessful = false;
		PostMethod post = null;
		Response response = null;
		try	{ 		
	    	if (transformType==null){
	    		throw new RMapTransformApiException(ErrorCode.ER_CANNOT_FIND_TRANSFORM_TYPE);
	    	}
	    	if (apiLookupId==null || apiLookupId.length()==0){
	    		throw new RMapTransformApiException(ErrorCode.ER_NO_TRANSFORM_ID_PROVIDED);
	    	}	

			//check that this agent has permission to create an OSF DiSCO
			//URI systemAgent = apiUserService.getCurrentSystemAgentUri();

			List<String> authorizedUsers =
					ConfigUtils.getPropertyValueList(Constants.TRANSFORMACCTS_PROPFILE, 
							(Constants.TRANSFORMACCTS_PROPPREFIX + transformType.value()), ",");
			if (authorizedUsers == null || authorizedUsers.size()==0){
				throw new RMapTransformApiException(ErrorCode.ER_TRANSFORM_CONFIG_NOT_FOUND);
			} 
			//if (!authorizedUsers.contains(systemAgent.toString())){
			//	throw new RMapTransformApiException(ErrorCode.ER_USER_NOT_AUTHORIZED_FOR_THIS_TRANSFORM);				
			//}
			
			//authorized, now do transform
			OsfClientService osf = new OsfClientService();
			Object record = null;	
			switch (transformType) {
			case OSF_NODE:
				record = osf.getNode(apiLookupId);	
				break;
			case OSF_REGISTRATION:
				record = osf.getRegistration(apiLookupId);
				break;
			case OSF_USER:
				record = osf.getUser(apiLookupId);
				break;
			default:
				break;
			}
			if (record==null){
				throw new RMapTransformApiException(ErrorCode.ER_COULD_NOT_RETRIEVE_RECORD_TO_TRANSFORM);	
			}
			//pick a disco builder
			DiscoBuilder discoModel = DiscoBuilderFactory.createDiscoBuilder(transformType);
			
			//pass record into discobuilder
			discoModel.setRecord(record); 

			String postUrl = null;
			if (origDiscoUri!=null && origDiscoUri.trim().length()>0){
				//is an update
				postUrl = Utils.makeDiscoUrl(origDiscoUri);
			} else {
				postUrl = Utils.getDiscoBaseUrl();
			}
			URI postUri = new URI(postUrl);

			String discoRdf = TransformUtils.generateTurtleRdf(discoModel.getModel()).toString();
			
			post = new PostMethod(postUrl);
			
			StringRequestEntity requestEntity = new StringRequestEntity(discoRdf,Constants.TRANSFORM_MEDIA_TYPE, "UTF-8");
		    post.setRequestEntity(requestEntity);		
			post.addRequestHeader("Content-type", Constants.TRANSFORM_MEDIA_TYPE);
			
			HttpClient httpClient = new HttpClient();
			Credentials defaultcreds = new UsernamePasswordCredentials(apiUserService.getAccessKey(), apiUserService.getSecret());
			httpClient.getParams().setAuthenticationPreemptive(true);
			httpClient.getState().setCredentials(
							new AuthScope(postUri.getHost(), postUri.getPort(), AuthScope.ANY_REALM
							), defaultcreds);

			
			int httpResp = httpClient.executeMethod(post);
			ResponseBuilder responseBldr = Response.status(httpResp).type(Constants.RESPONSE_MEDIA_TYPE);	
			Header[] headers = post.getResponseHeaders();
			for (Header header : headers) {
				responseBldr.header(header.getName(), header.getValue());
			}
			String responseBody = post.getResponseBodyAsString();
			responseBldr.entity(responseBody);
    		response = responseBldr.build(); 
						
    		log.info("DiSCO transformed and sent to RMap. URI: " + responseBody);
    		
			reqSuccessful = true;
				
		}
		catch(RMapTransformApiException ex)	{
			throw RMapTransformApiException.wrap(ex);
		}
		catch(Exception ex)	{
			throw RMapTransformApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
		finally{            
			if (post!=null) post.releaseConnection();
			if (!reqSuccessful && response!=null) response.close();
		}
		return response;	
	}

//
//	/**
//	 * Updates RMap:DiSCO.  Does this by inactivating the previous version of the DiSCO and 
//	 * creating a new version using valid client-provided RDF.
//	 * @param origDiscoUri
//	 * @param discoRdf
//	 * @param contentType
//	 * @return HTTP Response
//	 * @throws RMapApiException
//	 * @throws URISyntaxException 
//	 * @throws RMapDefectiveArgumentException 
//	 * @throws RMapException 
//	 */
//	public Response updateDisco(RMapDiSCO disco, RMapRequestAgent reqAgent, String origDiscoUri) 
//			throws RMapApiException, RMapException, RMapDefectiveArgumentException, URISyntaxException {
//	
//		RMapEvent discoEvent = rmapService.updateDiSCO(new URI(origDiscoUri), disco, reqAgent);
//		
//		if (discoEvent == null) {
//			throw new RMapApiException(ErrorCode.ER_CORE_UPDATEDISCO_NOT_COMPLETED);
//		} 
//		
//		URI uDiscoURI = disco.getId().getIri();  
//		if (uDiscoURI==null){
//			throw new RMapApiException(ErrorCode.ER_CORE_GET_DISCOID_RETURNED_NULL);
//		}
//		String sDiscoURI = uDiscoURI.toString();  
//		if (sDiscoURI.length() == 0){
//			throw new RMapApiException(ErrorCode.ER_CORE_DISCOURI_STRING_EMPTY);
//		} 
//		
//		URI uEventURI = discoEvent.getId().getIri();  
//		if (uEventURI==null){
//			throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
//		} 
//		String sEventURI = uEventURI.toString();  
//		if (sEventURI.length() == 0){
//			throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
//		} 
//
//		String newEventURL = Utils.makeEventUrl(sEventURI); 
//		String prevDiscoUrl = Utils.makeDiscoUrl(origDiscoUri); 
//		String newDiscoUrl = Utils.makeDiscoUrl(sDiscoURI); 
//
//		String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";
//		linkRel = linkRel.concat(",<" + prevDiscoUrl + ">" + ";rel=\"predecessor-version\"");
//		
//		Response response = Response.status(Response.Status.CREATED)
//					.entity(sDiscoURI)
//					.location(new URI(newDiscoUrl)) 
//					.header("Link",linkRel)    //switch this to link()
//					.build();   
//	
//		return response;
//	}
//	
//	private Response createDisco(RMapDiSCO disco, RMapRequestAgent reqAgent)
//				throws RMapApiException, URISyntaxException, RMapException, RMapDefectiveArgumentException{
//		//persist disco
//		RMapEventCreation discoEvent = (RMapEventCreation)rmapService.createDiSCO(disco, reqAgent); 
//		if (discoEvent == null) {
//			throw new RMapApiException(ErrorCode.ER_CORE_CREATEDISCO_NOT_COMPLETED);
//		} 
//		
//		URI uDiscoURI = disco.getId().getIri();  
//		if (uDiscoURI==null){
//			throw new RMapApiException(ErrorCode.ER_CORE_GET_DISCOID_RETURNED_NULL);
//		} 
//		
//		String sDiscoURI = uDiscoURI.toString();  
//		if (sDiscoURI.length() == 0){
//			throw new RMapApiException(ErrorCode.ER_CORE_DISCOURI_STRING_EMPTY);
//		} 
//		
//		log.info("New DiSCO created with URI " + sDiscoURI);
//		
//		URI uEventURI = discoEvent.getId().getIri();  
//		if (uEventURI==null){
//			throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
//		} 
//		String sEventURI = uEventURI.toString();  
//		if (sEventURI.length() == 0){
//			throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
//		} 
//		
//		String newEventURL = Utils.makeEventUrl(sEventURI); 
//		String newDiscoUrl = Utils.makeDiscoUrl(sDiscoURI); 
//		
//		String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";
//		
//		Response response = Response.status(Response.Status.CREATED)
//					.entity(sDiscoURI)
//					.location(new URI(newDiscoUrl)) //switch this to location()
//					.header("Link",linkRel)    //switch this to link()
//					.build(); 
//			
//		return response;
//	}
	
	
}
