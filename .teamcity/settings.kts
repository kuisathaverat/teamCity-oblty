import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

version = "2019.2"

project {
    vcsRoot(Observability_BuildConfVcs)
    buildType(Build)
}

object Build : BuildType({
    name = "Build"
    artifactRules = "target/*jar"

    vcs {
        root(BuildConfVcs)
        checkoutMode = CheckoutMode.ON_AGENT
        checkoutDir = "src"
        cleanCheckout = true
    }
    steps {
        script {
            scriptContent = "ls -la"
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

object Observability_BuildConfVcs : GitVcsRoot({
    name = "ObltVcs"
    url = "https://github.com/kuisathaverat/teamCity-oblty.git"
})

object BuildConfVcs : GitVcsRoot({
    name = "PetclinicVcs"
    url = "https://github.com/elastic/apm-agent-python.git"
})
