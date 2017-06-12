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


public interface ITraceAttr {


    /**
     *
     * @return 围栏标识名
     */
    String getEntry();

    /**
     *
     * @return 是否需要对象存储服务，默认为：false，关闭对象存储服务。
     * 注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能
     * 该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
     */
    boolean needObjectStorage();

    /**
     *
     * @return 定位周期(单位:秒)
     */
    int getGatherInterval();

    /**
     *
     * @return  打包回传周期(单位:秒)
     */
    int getPackInterval();
}
