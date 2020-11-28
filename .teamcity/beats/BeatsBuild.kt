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

class BeatsBuild(val beat: String, var os: String, ref: String) : BuildType({
    id("beats_build_${beat}_${os}_${ref}".toId())
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
                set -e
                export HOME=${'$'}(pwd)
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin
                BIN=${'$'}{HOME}/bin
                mkdir -p ${'$'}{BIN}
                curl -sL -o ${'$'}{BIN}/gvm https://github.com/andrewkroh/gvm/releases/download/v0.2.0/gvm-linux-amd64
                chmod +x ${'$'}{BIN}/gvm
                ls -la
            """.trimIndent()
        }
        script {
            name = "Install Go"
            scriptContent = """
                set -e
                export HOME=${'$'}(pwd)
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
                export HOME=${'$'}(pwd)
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin:${'$'}{HOME}/go/bin
                PYTHON_ENV=${'$'}{HOME}/python-env
                BIN=${'$'}{HOME}/bin
                GO_VERSION=$(cat .go-version)
                eval "${'$'}(gvm ${'$'}GO_VERSION)"
                go version
                make -C ${beat} check
                make -C ${beat} update
                make check-no-changes
            """.trimIndent()
        }
        script {
            name = "Build"
            scriptContent = """
                set -e
                export HOME=${'$'}(pwd)
                export PATH=${'$'}{PATH}:${'$'}{HOME}/bin:${'$'}{HOME}/.ci/scripts:${'$'}{HOME}/.local/bin:${'$'}{HOME}/go/bin
                PYTHON_ENV=${'$'}{HOME}/python-env
                BIN=${'$'}{HOME}/bin
                GO_VERSION=${'$'}(cat .go-version)
                eval "${'$'}(gvm ${'$'}GO_VERSION)"
                go version
                mage -C ${beat} build test
            """.trimIndent()
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-18")
    }
})
