/*
 *
 *   Copyright (C) 2017 author : 梁桂栋
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me : stonelavender@hotmail.com
 *
 */

package dong.lan.mapeye.common;


import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import dong.lan.trace.ITraceAttr;
import dong.lan.trace.TraceClient;

public class TraceManager {
    private static TraceManager manager;


    private TraceManager() {
        traceClientMap = new HashMap<>();
    }

    public static TraceManager get() {
        if (manager == null)
            manager = new TraceManager();
        return manager;
    }

    private Map<String, TraceClient> traceClientMap;



    public void start(Context appContext, final String entry) {
        TraceClient client = traceClientMap.get(entry);
        if (client == null) {
            client = new TraceClient();
            client.init(appContext, new ITraceAttr() {
                @Override
                public String getEntry() {
                    return entry;
                }

                @Override
                public boolean needObjectStorage() {
                    return false;
                }

                @Override
                public int getGatherInterval() {
                    return 2;
                }

                @Override
                public int getPackInterval() {
                    return 10;
                }
            });
        }
        client.startGather();
    }

    public void stop(String entry) {
        TraceClient client = traceClientMap.get(entry);
        if (client != null)
            client.stopGather();
    }

    public void stopAll(String entry) {
        TraceClient client = traceClientMap.get(entry);
        if (client != null) {
            client.stopAll();
            traceClientMap.remove(entry);
        }
    }



}
