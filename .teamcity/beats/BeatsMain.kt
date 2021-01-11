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
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.toId
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

class BeatsMain(var ref: String) : BuildType({
    id("beats_main_${ref}".toId())
    name = "Beats - Main - ${ref}"
    description = "Beats main job - ${ref}"
    var ref = ref

    steps {
        script {
            name = "shell"
            scriptContent = """dd if=/dev/urandom bs=1k count=100 |base64"""
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-20")
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
            branchFilter = "+:${ref}"
        }
    }

    features {
        pullRequests {
            vcsRootExtId = "${BeatsVcs.id}"
            provider = github {
                authType = token {
                    token = "secure:credentialsJSON:c1fd7c33-38c1-441d-b6b5-0777c34140eb"
                }
                filterTargetBranch = "+:${ref}"
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }
})