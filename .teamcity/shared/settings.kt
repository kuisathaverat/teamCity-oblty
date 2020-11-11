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

import dependsOn
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.FailureAction
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.toId

val p10 = listOf(1..10).map {
    TestAgent("A${it}")
}

val p20 = listOf(1..20).map {
    TestAgent("B${it}")
}

val p30 = listOf(1..30).map {
    TestAgent("C${it}")
}

val p40 = listOf(1..40).map {
    TestAgent("D${it}")
}

val p50 = listOf(1..50).map {
    TestAgent("E${it}")
}

val syncA = TestAgent("SyncA")
val syncB = TestAgent("SyncB")
val syncC = TestAgent("SyncC")
val syncD = TestAgent("SyncD")
val syncE = TestAgent("SyncE")
val main = TestAgentMain()

class SharedProject: Project({
    id("shared_project")
    name = "Shared"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    defaultTemplate = DefaultTemplate

    buildType(main)

    syncB.dependsOn(syncA)
    syncC.dependsOn(syncB)
    syncD.dependsOn(syncC)
    syncE.dependsOn(syncD)

    subProject(P10())
    subProject(P20())
    subProject(P30())
    subProject(P40())
    subProject(P50())
    subProject(ParallelEmAll())
})

object syncF: BuildType({
    id("syncf".toId())
    name = "syncF"

    dependsOn(syncA){
        onDependencyFailure = FailureAction.ADD_PROBLEM
        onDependencyCancel = FailureAction.ADD_PROBLEM
    }
})
