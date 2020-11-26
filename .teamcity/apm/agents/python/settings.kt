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

import dependsOn
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import shared.DefaultTemplate

val operatingSystems = listOf("Mac OS X", "Windows", "Linux")
val pythonVersions = listOf(
        "python-2.7",
        "python-3.5",
        "python-3.6",
        "python-3.7",
        "python-3.8",
        "python-3.9-rc",
        "pypy-2",
        "pypy-3"
        )
val frameworks = listOf(
        "none",
        "django-1.8",
        "django-1.11",
        "django-2.0",
        "django-2.1",
        "django-2.2",
        "django-3.0",
        "flask-0.10",
        "flask-0.11",
        "flask-0.12",
        "flask-1.0",
        "flask-1.1",
        "celery-3-flask-1.0",
        "celery-3-django-1.11",
        "celery-3-django-2.0",
        "celery-4-flask-1.0",
        "celery-4-django-1.11",
        "celery-4-django-2.0",
        "opentracing-newest",
        "opentracing-2.0",
        "twisted-18",
        "twisted-17",
        "twisted-16",
        "twisted-15",
        "requests-newest",
        "boto3-1.0",
        "boto3-1.5",
        "boto3-1.6",
        "boto3-newest",
        "pymongo-2.9",
        "pymongo-3.0",
        "pymongo-3.1",
        "pymongo-3.2",
        "pymongo-3.3",
        "pymongo-3.4",
        "pymongo-3.5",
        "pymongo-newest",
        "redis-2.8",
        "redis-2.9",
        "redis-newest",
        "psycopg2-2.7",
        "psycopg2-newest",
        "pymssql-newest",
        "memcached-1.51",
        "memcached-newest",
        "pylibmc-1.4",
        "pylibmc-newest",
        "cassandra-3.4",
        "cassandra-newest",
        "psutil-newest",
        "psutil-5.0",
        "psutil-4.0",
        "elasticsearch-2",
        "elasticsearch-5",
        "elasticsearch-6",
        "elasticsearch-7",
        "gevent-newest",
        "zerorpc-0.4",
        "aiohttp-3.0",
        "aiohttp-4.0",
        "aiohttp-newest",
        "aiopg-newest",
        "asyncpg-newest",
        "tornado-newest",
        "starlette-0.13",
        "starlette-newest",
        "pymemcache-3.0",
        "pymemcache-newest",
        "graphene-2",
        "httpx-0.12",
        "httpx-0.13",
        "httpx-0.14",
        "httpx-newest"
)

val pythonMain = ApmAgentPythonMain()
class ApmAgentPythonProject: Project ({
    id("apm_agent_python_project")
    name = "APM Agent Python"

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    defaultTemplate = DefaultTemplate

    vcsRoot(ApmAgentPythonVcs)

    buildType(pythonMain)
    pythonVersions.forEach{ python ->
        operatingSystems.forEach{ os ->
            frameworks.forEach{ framework ->
                val bt = ApmAgentPythonAxis(os, python, framework)
                buildType(bt)
                pythonMain.dependsOn(bt)
            }
        }
    }
})

