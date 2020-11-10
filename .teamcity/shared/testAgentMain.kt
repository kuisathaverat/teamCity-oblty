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

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

class TestAgentMain : BuildType({
    id("test_agent_main".toId())
    name = "Test Agent - Main"
    description = "Test Agent with 17MB of log"

    steps {
        script {
            name = "shell"
            scriptContent = """dd if=/dev/urandom bs=1k count=17000 |base64"""
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-18")
    }

    dependencies {
        snapshot(SyncA){}
        snapshot(SyncB){}
        snapshot(SyncC){}
        snapshot(SyncD){}
        snapshot(SyncE){}
        artifacts(SyncA) {
            artifactRules = "A.txt"
        }
        artifacts(SyncB) {
            artifactRules = "B.txt"
        }
        artifacts(SyncC) {
            artifactRules = "C.txt"
        }
        artifacts(SyncD) {
            artifactRules = "D.txt"
        }
        artifacts(SyncE) {
            artifactRules = "E.txt"
        }
    }
})

open class SyncTasks(idName: String) : BuildType({
    id("sync_${idName}".toId())
    name = "Sync tasks ${idName}"
    description = "BuildType to synchronize tasks"
    artifactRules = "${idName}.txt"

    steps {
        script {
            name = "shell"
            scriptContent = """touch ${idName}.txt"""
        }
    }
})


//object SyncA: SyncTasks("A")
object SyncA : BuildType({
    id("sync_A".toId())
    name = "Sync tasks A"
    description = "BuildType to synchronize tasks"
    artifactRules = "A.txt"

    steps {
        script {
            name = "shell"
            scriptContent = """touch A.txt"""
        }
    }
})

object SyncB: SyncTasks("B")
object SyncC: SyncTasks("C")
object SyncD: SyncTasks("D")
object SyncE: SyncTasks("E")
