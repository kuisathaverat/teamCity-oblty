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

import apm.ApmProject
import beats.BeatsProject
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import shared.DefaultTemplate
import shared.SharedProject
import shared.TestManualConfig

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.1"

project {
    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    template(DefaultTemplate)

    buildType(TestManualConfig())

    subProject(ApmProject())
    subProject(BeatsProject())
    subProject(SharedProject())

    features {
        feature {
            id = "Observability"
            type = "CloudProfile"
            param("agentPushPreset", "")
            param("profileId", "Observability")
            param("profileServerUrl", "")
            param("name", "Observability")
            param("total-work-time", "")
            param("credentialsType", "key")
            param("description", "")
            param("next-hour", "")
            param("cloud-code", "google")
            param("terminate-after-build", "true")
            param("terminate-idle-time", "30")
            param("enabled", "true")
            param("secure:accessKey", "credentialsJSON:33f809b9-679b-4641-9874-dd3bc8f2098b")
        }

        feature {
            id = "observability-ci-ubuntu-1804-lts"
            type = "CloudImage"
            param("network", "teamcity")
            param("subnet", "teamcity")
            param("growingId", "true")
            param("agent_pool_id", "-2")
            param("preemptible", "false")
            param("sourceProject", "elastic-images-prod")
            param("sourceImageFamily", "elastic-beats-ci-ubuntu-2004-lts")
            param("source-id", "elastic-apm-ci-ubuntu-1804-lts")
            param("zone", "us-central1-a")
            param("profileId", "Observability")
            param("diskType", "pd-ssd")
            param("machineCustom", "false")
            param("maxInstances", "400")
            param("imageType", "ImageFamily")
            param("diskSizeGb", "150")
            param("machineType", "n2-standard-4")
        }
    }

    cleanup {
        all(builds=10)
        history(builds=10)
        artifacts(builds=10)
    }
}
