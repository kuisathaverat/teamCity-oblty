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
import jetbrains.buildServer.configs.kotlin.v2019_2.*

val p10 = (1..10).map {
    TestAgent("A${it}")
}

val p20 = (1..20).map {
    TestAgent("B${it}")
}

val p30 = (1..30).map {
    TestAgent("C${it}")
}

val p40 = (1..40).map {
    TestAgent("D${it}")
}

val p50 = (1..50).map {
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

    p10.forEach { syncA.dependsOn(it) }
    p20.forEach { syncB.dependsOn(it) }
    p30.forEach { syncC.dependsOn(it) }
    p40.forEach { syncD.dependsOn(it) }
    p50.forEach { syncE.dependsOn(it) }

    syncB.dependsOn(syncA)
    syncC.dependsOn(syncB)
    syncD.dependsOn(syncC)
    syncE.dependsOn(syncD)

    subProject(P10())
    subProject(P20())
    subProject(P30())
    subProject(P40())
    subProject(P50())
})

object syncF: BuildType({
    id("syncf".toId())
    name = "syncF"

    dependsOn(syncA){
        onDependencyFailure = FailureAction.ADD_PROBLEM
        onDependencyCancel = FailureAction.ADD_PROBLEM
        reuseBuilds = ReuseBuilds.NO
    }
})
