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

import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2019_2.toId
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

class ApmAgentPythonAxis(val os: String, val python: String) : BuildType({
    id("APM_agent_Python_${os}_${python}".toId())
    name = "Agent Python ${os} ${python}"

    vcs {
        root(ApmAgentPythonVcs)
        checkoutMode = CheckoutMode.ON_AGENT
        checkoutDir = "src"
        cleanCheckout = true
    }

    steps {
        frameworks.forEach { framework ->
            script {
                name = "Lint"
                scriptContent = """
                    echo '${os} - ${python} - ${framework}'
                    export HOME=$(pwd)
                    export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                    curl -s https://pre-commit.com/install-local.py | python -
                    pre-commit install --install-hooks
                    pre-commit run
                """.trimIndent()
            }
            script {
                name = "Test"
                scriptContent = """
                    echo '${os} - ${python} - ${framework}'
                    export HOME=$(pwd)
                    export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                    export BUILD_NUMBER=1
                    ./tests/scripts/docker/run_tests.sh  ${python} ${framework}
                """.trimIndent()
            }
        }
    }

    triggers {
        vcs {
            groupCheckinsByCommitter = true
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-18")
    }
})