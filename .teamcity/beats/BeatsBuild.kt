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

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

class BeatsBuild(val beat: String, var os: String, ref: String) : BuildType({
    id("beats_build_${beat}_${os}_${ref}".toId())
    name = "Beats - ${beat} - ${os}"

    features {
        feature {
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "+:**/build/TEST*.xml")
        }
        pullRequests {
            vcsRootExtId = "${BeatsVcs.id}"
            provider = github {
                authType = token {
                    token = "%env.GITHUB_TOKEN%"
                }
                filterTargetBranch = "+:${ref}"
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }

    vcs {
        root(BeatsVcs)
        checkoutMode = CheckoutMode.ON_AGENT
        checkoutDir = "src/github.com/elastic/beats"
        cleanCheckout = true
    }

    params {
        param("env.TC_WS", "%teamcity.agent.work.dir%")

        // For now these are just to ensure compatibility with existing Jenkins-based configuration
        param("env.JENKINS_URL", "%teamcity.serverUrl%")
        param("env.BUILD_URL", "%teamcity.serverUrl%/build/%teamcity.build.id%")
        param("env.JOB_NAME", "%system.teamcity.buildType.id%")
        //param("env.GIT_BRANCH", "%vcsroot.branch%")
    }

    steps {
        script {
            name = "Install gvm"
            scriptContent = """
                set -e
                export HOME=${'$'}{TC_WS}
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                BIN=${'$'}{HOME}/bin
                mkdir -p ${'$'}{BIN}
                curl -sL -o ${'$'}{BIN}/gvm https://github.com/andrewkroh/gvm/releases/download/v0.2.0/gvm-linux-amd64
                chmod +x ${'$'}{BIN}/gvm
            """.trimIndent()
        }
        script {
            name = "Install Go"
            scriptContent = """
                set -e
                export HOME=${'$'}{TC_WS}
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin:${'$'}{HOME}/go/bin
                BIN=${'$'}{HOME}/bin
                GO_VERSION=${'$'}(cat .go-version)
                eval "${'$'}(gvm ${'$'}GO_VERSION)"
                go version
                make mage
            """.trimIndent()
        }
        script {
            name = "Lint"
            scriptContent = """
                set -e
                export HOME=${'$'}{TC_WS}
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin:${'$'}{HOME}/go/bin
                export PYTHON_ENV=${'$'}{HOME}/python-env
                BIN=${'$'}{HOME}/bin
                GO_VERSION=$(cat .go-version)
                eval "${'$'}(gvm ${'$'}GO_VERSION)"
                go version
                python3 --version
                make -C ${beat} check
                make -C ${beat} update
                make check-no-changes
            """.trimIndent()
        }
        script {
            name = "Build"
            scriptContent = """
                set -e
                export HOME=${'$'}{TC_WS}
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin:${'$'}{HOME}/go/bin
                export PYTHON_ENV=${'$'}{HOME}/python-env
                export CGO_ENABLED=0
                export GOPATH=${'$'}{HOME}
                BIN=${'$'}{HOME}/bin
                GO_VERSION=${'$'}(cat .go-version)
                eval "${'$'}(gvm ${'$'}GO_VERSION)"
                go version
                export | grep GO
                echo "HOME=${'$'}HOME"
                echo "PATH=${'$'}PATH"
                cd ${beat}
                mage build test
            """.trimIndent()
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-20")
    }
})
