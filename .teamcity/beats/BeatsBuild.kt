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
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

class BeatsBuild(val beat: String, var os: String) : BuildType({
    id("beats_buils_${beat}_${os}".toId())
    name = "Beats - ${beat} - ${os}"

    features {
        feature {
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "+:**/build/TEST*.xml")
        }
    }

    vcs {
        root(BeatsVcs)
        checkoutMode = CheckoutMode.ON_AGENT
        checkoutDir = "src"
        cleanCheckout = true
    }

    steps {
        script {
            name = "Install gvm"
            scriptContent = """
                export HOME=${'$'}(pwd)
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
                export HOME=${'$'}(pwd)
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
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
                export HOME=${'$'}(pwd)
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                BIN=${'$'}{HOME}/bin
                GO_VERSION=$(cat .go-version)
                eval "$(gvm ${'$'}GO_VERSION)"
                go version
                cd ${beat}
                make -C auditbeat check
                make -C auditbeat update
                make check-no-changes
            """.trimIndent()
        }
        script {
            name = "Build"
            scriptContent = """
                export HOME=${'$'}(pwd)
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                BIN=${'$'}{HOME}/bin
                GO_VERSION=${'$'}(cat .go-version)
                eval "${'$'}(gvm ${'$'}GO_VERSION)"
                go version
                cd ${beat}
                mage build test
            """.trimIndent()
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-18")
    }

    triggers {
        vcs {
            groupCheckinsByCommitter = true
        }
    }
})
