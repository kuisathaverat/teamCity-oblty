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
import shared.TestAgent

class BeatProject(var beat: String): Project({
    id("beats_project_${beat}".toId())
    name = "${beat}"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    operatingSystems.forEach{ os ->
        val bt = BeatsBuild("${beat}", "${os}")
        buildType(bt)
        beatsMain.dependsOn(bt)
    }
})