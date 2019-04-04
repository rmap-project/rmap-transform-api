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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.transformapi.exception.ErrorCode;
import info.rmapproject.transformapi.exception.RMapTransformApiException;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ApiUserService tests
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-*-context.xml" })
public class ApiUserServiceTest {
		
	/** The API User Service. */
	@Autowired
	private ApiUserService apiUserService;	
	
	/** name of a test user that has no agent assigned yet. */
	private static final String TEST_USER_NOAGENT = "usernoagent";

	/** password for a test user that has no agent assigned yet. */
	private static final String TEST_PASS_NOAGENT = "usernoagent";

	/** name of a test user that has an agent assigned. */
	private static final String TEST_USER_WITHAGENT = "userwithagent";

	/** password for a test user that has an agent assigned. */
	private static final String TEST_PASS_WITHAGENT = "userwithagent";

	/** name of a test user for testing the user synchronization. */
	private static final String TEST_USER_TESTSYNC = "usertestsync";

	/** password for a test user for testing the user synchronization. */
	private static final String TEST_PASS_TESTSYNC = "usertestsync";
	
	/**
	 * Set up for the tests
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * Tests retrieval of the System Agent URI to assign to an Event
	 * Should return an agent URI
	 */
	@Test
	public void getSystemAgentUriForEventTest() {
		try {
			URI sysAgent = apiUserService.getCurrentSystemAgentUri();
			assertTrue(sysAgent.toString().equals("rmap:rmaptestagent"));
		} catch (RMapTransformApiException e) {
			fail("sysAgent not retrieved");
		}
		
	}
	
	/**
	 * Gets the System Agent URI to assign to an Event where user has no Agent
	 * Should return exception saying the user has no Agent
	 **/
	@Test
	public void getSystemAgentUriForEventTestNoAgent() {
		try {
			@SuppressWarnings("unused")
			URI sysAgent = apiUserService.getSystemAgentUri(TEST_USER_NOAGENT,TEST_PASS_NOAGENT);
			fail("An exception should have been thrown");
		} catch (RMapTransformApiException e) {
			assertTrue(e.getErrorCode().equals(ErrorCode.ER_USER_HAS_NO_AGENT));
		}		
	}
	
	/**
	 * Tests retrieval of a System Agent URI where usser has an agent
	 * Should return an Agent URI.
	 */
	@Test
	public void getSystemAgentUriForEventTestWithAgent() {
		try {
			URI sysAgent = apiUserService.getSystemAgentUri(TEST_USER_WITHAGENT, TEST_PASS_WITHAGENT);
			assertTrue(sysAgent.toString().equals("rmap:userwithagent"));
		} catch (RMapTransformApiException e) {
			fail("sysAgent not retrieved");
		}		
	}

	/**
	 * Tests retrieval of an Agent that has been set up to synchronize with the database
	 * should retrieve an Agent URI
	 */
	@Test
	public void getSystemAgentUriForEventTestSyncAgent() {
		try {
			URI sysAgent = apiUserService.getSystemAgentUri(TEST_USER_TESTSYNC, TEST_PASS_TESTSYNC);
			assertTrue(sysAgent.toString().length()>0);
		} catch (RMapTransformApiException e) {
			fail("sysAgent not retrieved");
		}		
	}

	/**
	 * Test user validation
	 */
	@Test
	public void testValidateUser() {
		try {
			apiUserService.validateKey("jhu", "jhu");
		} catch (RMapTransformApiException e) {
			fail("validation failed");
		}		
	}
	
	

}
