// Licensed to Elasticsearch B.V. under one or more contributor
// license agreements. See the NOTICE file distributed with
// this work for additional information regarding copyright
// ownership. Elasticsearch B.V. licenses this file to you under
// the Apache License, Version 2.0 (the "License"); you may
// not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package shared

import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.sequential

class ParallelEmAll: Project({
    id("parallel_em_all")
    name = "Parallel 'em all"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    defaultTemplate = DefaultTemplate

    var bts = sequential {
        parallel {
            for (i in 1..10) {
                buildType(TestAgent("P10${i}"))
            }
        }
        parallel {
            for (i in 1..20) {
                buildType(TestAgent("P20${i}"))
            }
        }
        parallel {
            for (i in 1..30) {
                buildType(TestAgent("P30${i}"))
            }
        }
        parallel {
            for (i in 1..40) {
                buildType(TestAgent("P40${i}"))
            }
        }
        parallel {
            for (i in 1..50) {
                buildType(TestAgent("P50${i}"))
            }
        }
        buildType(TestAgent("P150"))
    }.buildTypes()

    bts.forEach{ buildType(it) }
})
