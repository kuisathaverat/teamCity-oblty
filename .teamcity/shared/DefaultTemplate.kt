/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package shared

import jetbrains.buildServer.configs.kotlin.v2019_2.ParameterDisplay
import jetbrains.buildServer.configs.kotlin.v2019_2.Template
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.perfmon

object DefaultTemplate: Template({
    name = "Default Template"

    params {
        param("teamcity.ui.settings.readOnly", "true")
//        password("env.GITHUB_TOKEN", "credentialsJSON:dfb790bf-8b34-4aa5-91b0-7b7c2979433a", display = ParameterDisplay.HIDDEN)
        password("env.GITHUB_TOKEN", "be2151db3748f5081127966c467e1bc9cc93ecf5", display = ParameterDisplay.HIDDEN)
    }

    features {
        perfmon { }
    }

    failureConditions {
        executionTimeoutMin = 240
    }
})