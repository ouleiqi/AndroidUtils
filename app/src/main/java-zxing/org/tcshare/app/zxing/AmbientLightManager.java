/*
 * Copyright (C) 2012 ZXing authors
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
 */

package org.tcshare.app.zxing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Detects ambient light and switches on the front light when very dark, and off again when sufficiently light.
 *
 * @author Sean Owen
 * @author Nikolaus Huber
 */
final class AmbientLightManager {

    private final Context context;
    private Sensor lightSensor;
    private SensorEventListener sensorLitener;

    AmbientLightManager(Context context) {
        this.context = context;
    }

    void start(SensorEventListener listener) {
        this.sensorLitener = listener;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(sensorLitener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    void stop() {
        if (lightSensor != null) {
            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(sensorLitener);
            lightSensor = null;
        }
    }
}
