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

package de.telekom.smartcredentials.authorization.fingerprint.presenters;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import de.telekom.smartcredentials.authorization.biometric.BiometricPresenter;
import de.telekom.smartcredentials.authorization.biometric.BiometricPromptWrapper;
import de.telekom.smartcredentials.authorization.fingerprint.AuthHandler;

public class AuthPresenterFactory {

    private static AuthPresenterFactory instance;

    public static AuthPresenterFactory getInstance() {
        if (instance == null) {
            instance = new AuthPresenterFactory();
        }
        return instance;
    }

    @RequiresApi(api = 28)
    public BiometricPresenter getBiometricPresenter(AuthHandler authHandler, BiometricPromptWrapper biometricPromptWrapper) {
        return new BiometricPresenter(authHandler, biometricPromptWrapper);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerprintPresenter getFingerprintPresenter(Context context, AuthHandler authHandler) {
        return new FingerprintPresenter(context, authHandler);
    }
}
