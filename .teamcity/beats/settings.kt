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

import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import shared.*

val operatingSystems = listOf(
        /*
        "centos-6", "centos-7", "centos-8",
        "debian-8", "debian-9", "debian-10",
        "ubuntu-14","ubuntu-16",
        */
        "ubuntu-20",
        /*
        "ubuntu-20",
         */
        "windows7-32bits","windows7","windows8","windows10","windows2008","windows2012","windows2016","windows2019",
        "masOS"
        )

val operatingSystemsPrs = listOf(
        "ubuntu-20",
        "windows2019"
)

val beat = listOf(
        "auditbeat",
        "filebeat",
        "heartbeat",
        "journalbeat",
        "metricbeat",
        "packetbeat",
        "winlogbeat",
        "x-pack/auditbeat",
        "x-pack/dockerlogbeat",
        "x-pack/elastic-agent",
        "x-pack/filebeat",
        "x-pack/functionbeat",
        "x-pack/metricbeat",
        "x-pack/packetbeat",
        "x-pack/winlogbeat"
        )
val beatOthers =  listOf(
        "deploy/kubernetes",
        "generator",
        "libbeat",
        "x-pack/libbeat",
        "dev-tools"
        )

class BeatsProject: Project({
    id("beats_project")
    name = "Beats"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    defaultTemplate = DefaultTemplate

    beatsBranches.forEach { branch ->
        subProject(BeatBranchProject("*/${branch}", operatingSystemsPrs))
    }
    vcsRoot(BeatsVcs)
})

