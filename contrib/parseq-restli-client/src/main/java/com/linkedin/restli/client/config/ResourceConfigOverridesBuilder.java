/*
 * Copyright 2016 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.restli.client.config;

import java.util.Optional;

public class ResourceConfigOverridesBuilder {

  private ConfigValue<Long> _timeoutMs;
  private ConfigValue<Boolean>  _batchingEnabled;
  private ConfigValue<Integer> _maxBatchSize;

  public ResourceConfigOverrides build() {
    return new ResourceConfigOverridesImpl(Optional.ofNullable(_timeoutMs), Optional.ofNullable(_batchingEnabled),
        Optional.ofNullable(_maxBatchSize));
  }

  public ResourceConfigOverridesBuilder setTimeoutMs(long timeoutMs, String source) {
    _timeoutMs = new ConfigValue<>(timeoutMs, source);
    return this;
  }

  public ResourceConfigOverridesBuilder setTimeoutMs(long timeoutMs) {
    _timeoutMs = new ConfigValue<>(timeoutMs, null);
    return this;
  }

  public ResourceConfigOverridesBuilder setBatchingEnabled(boolean batchingEnabled, String source) {
    _batchingEnabled = new ConfigValue<>(batchingEnabled, source);
    return this;
  }

  public ResourceConfigOverridesBuilder setBatchingEnabled(boolean batchingEnabled) {
    _batchingEnabled = new ConfigValue<>(batchingEnabled, null);
    return this;
  }

  public ResourceConfigOverridesBuilder setMaxBatchSize(int maxBatchSize, String source) {
    _maxBatchSize =  new ConfigValue<>(maxBatchSize, source);
    return this;
  }

  public ResourceConfigOverridesBuilder setMaxBatchSize(int maxBatchSize) {
    _maxBatchSize =  new ConfigValue<>(maxBatchSize, null);
    return this;
  }
}
