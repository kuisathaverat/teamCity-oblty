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
package beats

import dependsOn
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.toId

class BeatBranchProject(var branch: String, operatingSystemsPrs: List<String>): Project({
    id("beats_${branch}".toId())
    name = "${branch}"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    var beatsMain = BeatsMain(branch)
    buildType(beatsMain)

    beat.forEach{ beat ->
        subProject(BeatProject(beat, beatsMain, operatingSystems))
    }

    beatOthers.forEach{
        val bt = BeatsBuild(it, "ubuntu-20", beatsMain.ref)
        buildType(bt)
        beatsMain.dependsOn(bt)
    }
})