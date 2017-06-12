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

package dong.lan.trace;

import android.content.Context;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.TransportMode;

/**
 *
 */

public class TraceClient {

    private static final long SERVICE_Id = 120020;
    private Trace mTrace;
    private LBSTraceClient mTraceClient;


    // 初始化轨迹服务监听器
    private OnTraceListener mTraceListener = new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {
            System.out.println("onBindServiceCallback:"+s);
        }

        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {
            System.out.println("onStartTraceCallback:"+message);
        }

        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {
            System.out.println("onStopTraceCallback:"+message);
        }

        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            System.out.println("onStartGatherCallback:"+message);
        }

        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
            System.out.println("onStopGatherCallback:"+message);
        }

        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {
            System.out.println("onPushCallback:"+message.getMessage());
        }
    };


    public void init(Context appContext, ITraceAttr traceAttr) {
        String entityName = traceAttr.getEntry();
        mTrace = new Trace(SERVICE_Id, entityName, traceAttr.needObjectStorage());
        mTraceClient = new LBSTraceClient(appContext);
        mTraceClient.setLocationMode(LocationMode.High_Accuracy);
        mTraceClient.setProtocolType(ProtocolType.HTTPS);
        mTraceClient.setInterval(traceAttr.getGatherInterval(), traceAttr.getPackInterval());
        mTraceClient.startTrace(mTrace, mTraceListener);
    }

    public void stopAll() {
        mTraceClient.stopTrace(mTrace, mTraceListener);
    }

    public void stopGather() {
        mTraceClient.startGather(mTraceListener);
    }

    public void startGather() {
        mTraceClient.startGather(mTraceListener);
    }


    public void queryTrace(Context appContext , final ITraceQueryAttr queryAttr, final OnTrackListener trackResponse) {
        String entityName = queryAttr.getEntry();
        mTrace = new Trace(SERVICE_Id, entityName, false);
        mTraceClient = new LBSTraceClient(appContext);
        mTraceClient.setInterval(2, 12);
        mTraceClient.startTrace(mTrace, new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {
                HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(
                        queryAttr.getTag(), SERVICE_Id, queryAttr.getEntry());

                historyTrackRequest.setStartTime(queryAttr.getStartTime());
                historyTrackRequest.setEndTime(queryAttr.getEndTime());
                historyTrackRequest.setPageSize(100);
                historyTrackRequest.setPageIndex(1);
                historyTrackRequest.setSortType(SortType.desc);
                if (queryAttr.needProcessed()) {
                    ProcessOption option = new ProcessOption();
                    option.setNeedDenoise(queryAttr.needDenoise());
                    option.setNeedMapMatch(queryAttr.needMapMatch());
                    option.setNeedVacuate(queryAttr.needVacuate());
                    option.setRadiusThreshold(queryAttr.getRadiusThreshold());
                    option.setTransportMode(getTransportMode(queryAttr));
                    historyTrackRequest.setProcessed(true);
                    historyTrackRequest.setProcessOption(option);
                    historyTrackRequest.setSupplementMode(getSupplementMode(queryAttr));
                }
                mTraceClient.queryHistoryTrack(historyTrackRequest, trackResponse);
            }

            @Override
            public void onStartTraceCallback(int i, String s) {

            }

            @Override
            public void onStopTraceCallback(int i, String s) {

            }

            @Override
            public void onStartGatherCallback(int i, String s) {

            }

            @Override
            public void onStopGatherCallback(int i, String s) {

            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {

            }
        });

    }

    private SupplementMode getSupplementMode(ITraceQueryAttr attr) {
        switch (attr.getTransportMode()) {
            case ITraceQueryAttr.MODE_DRIVING:
                return SupplementMode.driving;
            case ITraceQueryAttr.MODE_RIDING:
                return SupplementMode.riding;
            case ITraceQueryAttr.MODE_WALKING:
                return SupplementMode.walking;
            default:
                return null;
        }
    }


    private TransportMode getTransportMode(ITraceQueryAttr attr) {
        switch (attr.getTransportMode()) {
            case ITraceQueryAttr.MODE_DRIVING:
                return TransportMode.driving;
            case ITraceQueryAttr.MODE_RIDING:
                return TransportMode.riding;
            case ITraceQueryAttr.MODE_WALKING:
                return TransportMode.walking;
            default:
                return null;
        }
    }

}
