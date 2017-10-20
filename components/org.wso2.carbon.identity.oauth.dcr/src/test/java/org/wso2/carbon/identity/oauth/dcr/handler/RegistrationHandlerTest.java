/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.identity.oauth.dcr.handler;

import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.omg.CORBA.Object;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.oauth.dcr.context.DCRMessageContext;
import org.wso2.carbon.identity.oauth.dcr.model.RegistrationRequest;
import org.wso2.carbon.identity.oauth.dcr.model.RegistrationRequestProfile;
import org.wso2.carbon.identity.oauth.dcr.model.RegistrationResponse;
import org.wso2.carbon.identity.oauth.dcr.model.RegistrationResponseProfile;
import org.wso2.carbon.identity.oauth.dcr.service.DCRManagementService;
import org.wso2.carbon.identity.testutil.powermock.PowerMockIdentityBaseTest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.testng.Assert.assertEquals;

@PrepareForTest({DCRManagementService.class, RegistrationHandler.class})
public class RegistrationHandlerTest extends PowerMockIdentityBaseTest {

    private RegistrationHandler registrationHandler;
    private RegistrationRequestProfile mockRegistrationRequestProfile;
    private String testTenantDomain = "testTenantDomain";

    @Mock
    RegistrationRequest mockRegisterRequest;

    @Mock
    DCRMessageContext mockDcrMessageContext;

    @Mock
    RegistrationResponseProfile mockRegistrationResponseProfile;

    @Mock
    DCRManagementService mockDCRManagementService;

    @BeforeMethod
    public void setUp() {
        registrationHandler = new RegistrationHandler();
    }

    @Test
    public void testHandle() throws Exception {
        mockRegistrationRequestProfile = new RegistrationRequestProfile();

        when(mockDcrMessageContext.getIdentityRequest()).thenReturn(mockRegisterRequest);
        when(mockRegisterRequest.getRegistrationRequestProfile()).thenReturn(mockRegistrationRequestProfile);
        when(mockRegisterRequest.getTenantDomain()).thenReturn(testTenantDomain);

        mockStatic(DCRManagementService.class);
        when (DCRManagementService.getInstance()).thenReturn(mockDCRManagementService);

        when(mockDCRManagementService.registerOAuthApplication(mockRegistrationRequestProfile)).
                thenReturn(mockRegistrationResponseProfile);

        RegistrationResponse.DCRRegisterResponseBuilder registerResponseBuilder =
                new RegistrationResponse.DCRRegisterResponseBuilder();
        whenNew(RegistrationResponse.DCRRegisterResponseBuilder.class).withNoArguments().thenReturn(registerResponseBuilder);

        assertEquals(registrationHandler.handle(mockDcrMessageContext), registerResponseBuilder,
                "Expected response builder is different from the actual");
        assertEquals(mockRegistrationRequestProfile.getTenantDomain(), testTenantDomain,
                "Expected tenant domain is not equal to the actual tenant domain");
    }
}
