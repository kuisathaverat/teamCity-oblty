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

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

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

val operatingSystems = listOf("Mac OS X", "Windows", "Linux")
val pythonVersions = listOf("2.7", "3.5", "3.7")

project {
    vcsRoot(BuildConfVcs)
    buildType(ApmAgentPythonMain)
    operatingSystems.forEach() {os ->
        pythonVersions.forEach(){ version ->
            buildType(ApmAgentPythonAxis(os, version))
        }
    }

    sequential {
        buildType(ApmAgentPythonMain)
        parallel {
            operatingSystems.forEach() {os ->
                pythonVersions.forEach(){ version ->
                    buildType(ApmAgentPythonAxis(os, version))
                }
            }
        }
    }
    subProject {
        id("APM")
        name = "APM"
    }
    subProject {
        id("Beats")
        name = "Beats"
    }
}

object ApmAgentPythonMain : BuildType({
    name = "APM Agent Python"
    description = "Run all Build configurations"

    steps {
        script {
            name = "shell"
            scriptContent = """echo "NOOP""""
        }
    }

    requirements {
        contains("teamcity.agent.name", "ubuntu-16")
    }
})

class ApmAgentPythonAxis(val os: String, val version: String) : BuildType ({
    id("APM_agent_Python_${os}_${version}".toId())
    name = "Agent Python ${os} ${version}"

    vcs {
        root(BuildConfVcs)
        checkoutMode = CheckoutMode.ON_AGENT
        checkoutDir = "src"
        cleanCheckout = true
    }

    steps {
        script {
            scriptContent = """echo '${os} - ${version}'"""
        }
    }

    triggers {
        vcs {
            groupCheckinsByCommitter = true
        }
    }

    requirements {
        contains("teamcity.agent.name", "ubuntu-16")
    }
})

object BuildConfVcs : GitVcsRoot({
    name = "APMAgentPython"
    url = "https://github.com/elastic/apm-agent-python.git"
})
