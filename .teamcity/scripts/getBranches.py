# Licensed to Elasticsearch B.V. under one or more contributor
# license agreements. See the NOTICE file distributed with
# this work for additional information regarding copyright
# ownership. Elasticsearch B.V. licenses this file to you under
# the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http:#www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
import urllib.request
import json
import re
import configargparse

parser = configargparse.ArgParser(description='Command line tool to generate a Kotlin file with the lists of branches '
                                              'and PRs from a repo. ')

parser.add_argument('--repo',
                    dest='repo',
                    env_var='REPO',
                    required=True,
                    help='Repository name.')

parser.add_argument('--owner',
                    dest='owner',
                    env_var='OWNER',
                    required=True,
                    help='Organization/owner of the repository.')

parser.add_argument('--token',
                    dest='token',
                    env_var='GITHUB_TOKEN',
                    help='GitHub token to access to the API.')

parser.add_argument('--file',
                    dest='file',
                    env_var='FILE',
                    required=True,
                    help='Name of the file to save the Kotlin script.')

parser.add_argument('--branch-filter',
                    dest='branchFilter',
                    env_var='BRANCH_FILTER',
                    default='(master|main|6\.8|7\.[89]|7\.1\d|8\.\d+|6\.x|7\.x|8\.x)',
                    help='Organization/owner of the repository.')

args = parser.parse_args()

repo = args.repo
owner = args.owner
token = args.token
file = args.file
branchFilter = args.branchFilter
branchMatcher = re.compile(branchFilter)

ktScript = """
package {repo}

val {repo}Branches = listOf(
""".format(repo=repo)

headers = {}
if token:
    headers = {'Authorization': 'token ' + token}

i = 0
for page in range(1, 100):
    req = urllib.request.Request("https://api.github.com/repos/{}/{}/branches?page={}".format(owner, repo, page)
                                 , headers=headers)
    with urllib.request.urlopen(req) as response:
        obj = json.loads(response.read())
        if len(obj) == 0:
            break
        for item in obj:
            if branchMatcher.match(item['name']):
                if i != 0:
                    ktScript += ","
                ktScript += "\"{}\"".format(item['name'])
                i += 1
ktScript += """
)

val {repo}Pulls = listOf(
""".format(repo=repo)

i = 0
for page in range(1, 100):
    req = urllib.request.Request("https://api.github.com/repos/{}/{}/pulls?page={}".format(owner, repo, page)
                                 , headers=headers)
    with urllib.request.urlopen(req) as response:
        obj = json.loads(response.read())
        if len(obj) == 0:
            break
        for item in obj:
            if i != 0:
                ktScript += ","
            ktScript += "\"{}\"".format(item['number'])
            i += 1
ktScript += """
)
"""

print(ktScript)

f = open(file, "w")
f.write(ktScript)
f.close()