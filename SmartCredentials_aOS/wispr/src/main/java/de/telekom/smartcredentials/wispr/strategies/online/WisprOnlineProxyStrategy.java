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

package de.telekom.smartcredentials.wispr.strategies.online;

import java.io.IOException;

import de.telekom.smartcredentials.wispr.callbacks.WisprRedirectCallback;
import de.telekom.smartcredentials.wispr.exceptions.WisprException;
import de.telekom.smartcredentials.wispr.replies.WisprProxyReply;
import de.telekom.smartcredentials.wispr.replies.WisprRedirectReply;
import de.telekom.smartcredentials.wispr.parsers.WisprProxyParser;
import de.telekom.smartcredentials.wispr.parsers.WisprRedirectParser;
import de.telekom.smartcredentials.wispr.services.WisprService;

public class WisprOnlineProxyStrategy extends WisprOnlineStrategy {

    public WisprOnlineProxyStrategy(WisprRedirectCallback redirectCallback) {
        super(redirectCallback);
    }

    @Override
    public WisprRedirectReply checkOnline(WisprService wisprService, String url) throws IOException, WisprException {
        String proxyBody = wisprService.checkOnline(url).execute().body();
        WisprProxyReply proxyReply = new WisprProxyParser(mRedirectCallback).parse(proxyBody);
        String redirectBody = wisprService.followRedirect(proxyReply.getNextUrl())
                .execute().body();
        return new WisprRedirectParser(mRedirectCallback).parse(redirectBody);
    }
}
