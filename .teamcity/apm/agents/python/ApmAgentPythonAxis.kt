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
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.ParameterDisplay
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.toId

class ApmAgentPythonAxis(val os: String, val python: String) : BuildType({
    id("APM_agent_Python_${os}_${python}".toId())
    name = "Agent Python ${os} ${python}"

    params {
        password("env.TEST", "SuPeRSeCrEt", display = ParameterDisplay.HIDDEN)
    }

    features {
        feature {
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "+:**/python-agent-junit.xml,+:**/target/**/TEST-*.xml")
        }
    }

    steps {
        script {
            name = "Lint"
            scriptContent = """
                    echo ${'$'}{TEST}
                    echo '${os} - ${python}'
                    export HOME=$(pwd)
                    export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                    alias python=python3
                    alias pip=pip3
                    curl -s https://pre-commit.com/install-local.py | python -
                    pre-commit install --install-hooks
                    pre-commit run
                """.trimIndent()
        }
        frameworks.forEach { framework ->
            if(!excludes.contains("${python}:${framework}")){
                script {
                    name = "Test ${os} - ${python} - ${framework}"
                    scriptContent = """
                    echo '${os} - ${python} - ${framework}'
                    export HOME=$(pwd)
                    export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                    export BUILD_NUMBER=1
                    alias python=python3
                    alias pip=pip3
                    ./tests/scripts/docker/run_tests.sh  ${python} ${framework}
                """.trimIndent()
                }
            }
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-20")
    }

    features {
        pullRequests {
            vcsRootExtId = "${BeatsVcs.id}"
            provider = github {
                authType = token {
                    token = "%env.GITHUB_TOKEN%"
                }
                filterTargetBranch = "++:refs/heads/master"
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
})