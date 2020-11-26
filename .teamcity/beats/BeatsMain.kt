package beats

import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.toId
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

class BeatsMain : BuildType({
    id("beats_main".toId())
    name = "Beats - Main"
    description = "Beats main job"

    steps {
        script {
            name = "shell"
            scriptContent = """dd if=/dev/urandom bs=1k count=100 |base64"""
        }
    }

    requirements {
        contains("teamcity.agent.name", "apm-ci-ubuntu-18")
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
        }
    }
})