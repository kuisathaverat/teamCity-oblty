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

package apm.agents.python

import beats.BeatsVcs
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

class ApmAgentPythonMain : BuildType({
    name = "APM Agent Python"
    description = "Run all Build configurations."

    params {
        password("env.TEST", "SuPeRSeCrEt", display = ParameterDisplay.HIDDEN)
    }

    steps {
        script {
            name = "Build Packages"
            scriptContent = """
                    echo ${'$'}{TEST}
                    export HOME=$(pwd)
                    export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                    alias python=python3
                    alias pip=pip3
                    pip3 install --user cibuildwheel
                    mkdir wheelhouse
                    CIBW_SKIP="pp*" cibuildwheel --platform linux --output-dir wheelhouse; ls -l wheelhouse
                """.trimIndent()
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-20")
    }

    vcs {
        root(ApmAgentPythonVcs)
        checkoutMode = CheckoutMode.ON_AGENT
        checkoutDir = "src"
        cleanCheckout = true
    }

    triggers {
        vcs {
            groupCheckinsByCommitter = true
            branchFilter = "+:*"
        }
    }
})