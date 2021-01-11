/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package shared

import apm.agents.python.ApmAgentPythonVcs
import jetbrains.buildServer.configs.kotlin.v2019_2.ParameterDisplay
import jetbrains.buildServer.configs.kotlin.v2019_2.Template
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests

object APMPythonTemplate: Template({
    name = "APM Python Template"

    params {
        param("teamcity.ui.settings.readOnly", "true")
        password("env.GITHUB_TOKEN", "credentialsJSON:fea3d2a6-ce76-4280-a1b8-ddcb04bee79d", display = ParameterDisplay.HIDDEN)
    }

    features {
        perfmon { }
        pullRequests {
            vcsRootExtId = "${ApmAgentPythonVcs.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:fea3d2a6-ce76-4280-a1b8-ddcb04bee79d"
                }
                filterTargetBranch = "+:refs/heads/master"
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
            }
        }
    }

    failureConditions {
        executionTimeoutMin = 240
    }
})