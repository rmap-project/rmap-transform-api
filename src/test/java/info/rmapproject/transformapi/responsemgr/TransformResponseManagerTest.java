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

import static org.junit.Assert.assertTrue;
import info.rmapproject.transformapi.exception.RMapTransformApiException;
import info.rmapproject.transformapi.responsemgr.TransformResponseManager;
import info.rmapproject.transformapi.utils.Constants;
import info.rmapproject.transformapi.utils.Utils;
import info.rmapproject.transformer.model.RecordType;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for TransformResponseManager
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath:/spring-rmaptransformapi-context.xml" })
public class TransformResponseManagerTest {
	
	/** The Transform Response Manager. */
	@Autowired
	TransformResponseManager transformResponseManager;
	
	/**
	 * Test OSF node transform - create and update.
	 *
	 * @throws RMapTransformApiException the RMap Transform API exception
	 * @throws HttpException the HTTP exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testOsfTransform() throws RMapTransformApiException, HttpException, IOException{
		String nodeId = "ndry9";
		
		//create the disco, check response
		Response response = transformResponseManager.createOrUpdateRMapDiSCO(RecordType.OSF_NODE, nodeId, null);
		assertTrue(response.getStatus()==201);
		String body = response.getEntity().toString();
		assertTrue(body.length()>0 && body.startsWith("ark"));
		String url = Utils.makeDiscoUrl(body);
		
		//make sure you can now retrieve the DiSCO from RMap
		GetMethod get= new GetMethod(url);
		get.addRequestHeader("Accept-type", Constants.TRANSFORM_MEDIA_TYPE);
		HttpClient httpClient = new HttpClient();
		int httpResp = httpClient.executeMethod(get);
		assertTrue(httpResp==200);
		String responseBody = get.getResponseBodyAsString();
		assertTrue(responseBody.contains(body) && responseBody.contains("DiSCO") && responseBody.contains(nodeId));

		//now update the DiSCO
		Response updateResponse = transformResponseManager.createOrUpdateRMapDiSCO(RecordType.OSF_NODE, nodeId, body);
		assertTrue(updateResponse.getStatus()==201);
		String updateBody = updateResponse.getEntity().toString();
		assertTrue(updateBody.length()>0 && updateBody.startsWith("ark"));
		String updatedDiscoUrl = Utils.makeDiscoUrl(updateBody);
		
		//make sure you can now retrieve the DiSCO from RMap
		GetMethod getUpdate= new GetMethod(updatedDiscoUrl);
		getUpdate.addRequestHeader("Accept-type", Constants.TRANSFORM_MEDIA_TYPE);
		HttpClient httpClientUpdate = new HttpClient();
		int httpRespUpdate = httpClientUpdate.executeMethod(getUpdate);
		assertTrue(httpRespUpdate==200);
		responseBody = getUpdate.getResponseBodyAsString();
		assertTrue(responseBody.contains(updateBody) && responseBody.contains("DiSCO") && responseBody.contains(nodeId));
		
		
	}
	
}
