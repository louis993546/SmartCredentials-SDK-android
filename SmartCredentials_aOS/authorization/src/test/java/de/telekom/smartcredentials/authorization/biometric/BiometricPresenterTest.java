/*
 * Copyright (c) 2019 Telekom Deutschland AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.telekom.smartcredentials.authorization.biometric;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.telekom.smartcredentials.authorization.AuthorizationManagerWrapper;
import de.telekom.smartcredentials.authorization.fingerprint.AuthHandler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BiometricPresenterTest {

    private BiometricPresenter mBiometricPresenter;

    @Before
    public void setUp() {
        AuthHandler authHandler = Mockito.mock(AuthHandler.class);
        BiometricPromptWrapper mBiometricPromptWrapper = Mockito.mock(BiometricPromptWrapper.class);
        mBiometricPresenter = new BiometricPresenter(authHandler, mBiometricPromptWrapper);
    }

    @Test
    public void getAuthorizationManagerWrapperReturnsAnInstanceOfBiometricPromptWrapper() {
        AuthorizationManagerWrapper wrapper = mBiometricPresenter.getAuthorizationManagerWrapper();

        assertNotNull(wrapper);
        assertTrue(wrapper instanceof BiometricPromptWrapper);
    }
}