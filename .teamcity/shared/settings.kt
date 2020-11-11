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

class SharedProject: Project({
    id("shared_project")
    name = "Shared"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    defaultTemplate = DefaultTemplate

    /*
    var bts = sequential {
        buildType(TestAgent("SyncA"))
        buildType(TestAgent("SyncB"))
        buildType(TestAgent("SyncC"))
        buildType(TestAgent("SyncD"))
        buildType(TestAgent("SyncE"))
        buildType(TestAgentMain())
    }.buildTypes()
    */

    sequential {
        buildType(TestAgent("SyncA"))
        buildType(TestAgentMain())
    }

    sequential {
        buildType(TestAgent("SyncB"))
        buildType(TestAgentMain())
    }

    sequential {
        buildType(TestAgent("SyncC"))
        buildType(TestAgentMain())
    }

    sequential {
        buildType(TestAgent("SyncD"))
        buildType(TestAgentMain())
    }

    sequential {
        buildType(TestAgent("SyncE"))
        buildType(TestAgentMain())
    }

    buildType(TestAgentMain())

    subProject(P10())
    subProject(P20())
    subProject(P30())
    subProject(P40())
    subProject(P50())
})
