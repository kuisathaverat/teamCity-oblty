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

import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.toId
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

class BeatsMain : BuildType({
    id("beats_main".toId())
    name = "Beats - Main"
    description = "Beats main job"

    steps {
        script {
            name = "shell"
            scriptContent = """dd if=/dev/urandom bs=1k count=100 |base64"""
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-18")
    }

    vcs {
        root(BeatsVcs)
        checkoutMode = CheckoutMode.ON_AGENT
        checkoutDir = "src"
        cleanCheckout = true
    }

    triggers {
        vcs {
            groupCheckinsByCommitter = true
        }
    }
})