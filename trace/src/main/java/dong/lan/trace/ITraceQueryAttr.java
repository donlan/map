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


public interface ITraceQueryAttr {

    int MODE_WALKING = 1001;
    int MODE_DRIVING = 1002;
    int MODE_RIDING = 1003;

    /**
     *
     * @return 设备标识
     */
    String getEntry();

    /**
     *
     * @return 查询标记
     */
    int getTag();

    /**
     *
     * @return 查询的开始时间
     * */
    long getStartTime();

    /**
     *
     * @return 查询的结束时间
     */
    long getEndTime();


    /**
     *
     * @return 是否需要较偏
     */
    boolean needProcessed();

    /**
     *
     * @return 是否去噪
     */
    boolean needDenoise();

    /**
     *
     * @return 是否去希
     */
    boolean needVacuate();

    /**
     *
     * @return 是否需要绑路
     */
    boolean needMapMatch();

    /**
     *
     * @return 设置精度过滤值(定位精度大于1该值的过滤掉)
     */
    int getRadiusThreshold();


    int getTransportMode();



}
