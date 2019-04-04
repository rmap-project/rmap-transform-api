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
package info.rmapproject.transformapi.service;

import info.rmapproject.transformapi.exception.ErrorCode;
import info.rmapproject.transformapi.exception.RMapTransformApiException;
import info.rmapproject.transformapi.responsemgr.TransformResponseManager;
import info.rmapproject.transformer.model.RecordType;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 * REST API service for Transforms.
 *
 * @author khanson
 */
@Scope("request")
@Path("/")
public class TransformApiService {

	/** The Transform Response Manager. */
	private TransformResponseManager transformResponseManager;	

    /**
     * Sets the Transform Response Manager.
     *
     * @param transformResponseManager the new Transform Response Manager
     * @throws RMapTransformApiException the RMap Transform API exception
     */
    @Autowired
    public void setTransformResponseManager(TransformResponseManager transformResponseManager) throws RMapTransformApiException {
    	if (transformResponseManager==null) {
			throw new RMapTransformApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);			
    	}
   		this.transformResponseManager = transformResponseManager;
	}
	
/*
 * ------------------------------
 * 
 * 	 GET INFO ABOUT API SERVICE
 *  
 *-------------------------------
 */	

	/**
	 * GET /transforms
	 * Returns link to transform API documentation, and lists HTTP options.
	 *
	 * @return HTTP Response
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
    @GET
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() throws RMapTransformApiException {
    	Response response = transformResponseManager.getTransformServiceOptions();
	    return response;
    }
    
    
	/**
	 * HEAD /transforms
	 * Returns link to transform API documentation, and lists HTTP options.
	 *
	 * @return HTTP Response
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
    @HEAD
    public Response getApiDetails() throws RMapTransformApiException {
    	Response response = transformResponseManager.getTransformServiceHead();
	    return response;
    }
    
	/**
	 * OPTIONS /transforms
	 * Returns link to transform API documentation, and lists HTTP options.
	 *
	 * @return HTTP Response
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
    @OPTIONS
    public Response apiGetApiDetailedOptions() throws RMapTransformApiException {
    	Response response = transformResponseManager.getTransformServiceHead();
	    return response;
    }
    
    
/*
 * -----------------------------------
 * 
 *  	 INITIATE DATA TRANSFORM
 *  
 *------------------------------------
 */ 
    

    /**
	 * POST /transforms/{type}?id={api_lookup_id}&discoid={orig_disco_id}
	 * e.g. /transforms/osf_users?id=ksdjs&discoid=ark:/27279/kfsjkajdf
	 * Uses id and type provided to initiate a transform process that can generate
	 * a new DiSCO.  Can also update existing DiSCOs by passing in the original DiSCO ID
	 *
	 * @param transformType the transform type
	 * @param apiLookupId the source API lookup id
	 * @param origDiscoId - optional original DiSCO URI to do update
	 * @return the HTTP Response
	 * @throws RMapTransformApiException the RMap Transform API exception
	 */
    @POST
    @Path("/{type}")
    public Response apiInitiateDiscoTransform(@PathParam("type") String transformType,
			    								@QueryParam("id") String apiLookupId, 
			    								@QueryParam("discoid") String origDiscoId) throws RMapTransformApiException {
    	RecordType type = RecordType.forValue(transformType);
   		return transformResponseManager.createOrUpdateRMapDiSCO(type, apiLookupId, origDiscoId);
    }	



    
}