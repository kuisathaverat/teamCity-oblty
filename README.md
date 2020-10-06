# teamCity-oblty
Test Project for TemaCity

* Create the Project in TeamCity UI
    * Configure VCS Roots
      * Add a new repo configuration
    * Configure **Versioned Settings**
      * Synchronization enabled
      * use settings from VCS
      * Push changes to the repo with **Commit current project settings…** button
* Clone the repo locally
* Edit the `.teamcity/settings.kts`
  * add a new BuiltType
  ```
  import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
  
  ...
  
  project {
      // repository to save project changes (this repo)
      // this is needed to mantaing the project in sync
      vcsRoot(BuildConfVcs)
  
      // Build configuration 
      buildType(MyTest)
  }
  
  ...
  
  object MyTest : BuildType({
      name = "Build"
      steps {
          script {
              scriptContent = "echo 'Hello world'"
          }
      }
  }
  
  ...
  
  object Observability_VCS : GitVcsRoot({
      name = "ObltProject"
      url = "https://github.com/kuisathaverat/teamCity-oblty.git"
  })
  ```
* Push the changes to the repo
* Update the project configuration on TeamCity UI 
    * Open the project in TeamCity UI
    * Go to **Versioned Settings** to the tab **Change log**
    * Check for changes by click on **Check for changes** button
    * Check configuration tab on the botton, **Current status** to see the update results.


A TeamCity project is like a folder in Jenkins and it contains subprojects(Jenkins folders)
and build configurations (Jobs). 

# Sequential Build configurations

```
project {
    vcsRoot(BuildConfVcs)

    // You have to define the build configurations
    buildType(Basic)
    buildType(BasicFromGit)

    // You set the way to execute them
    sequential {
        buildType(Basic)
        buildType(BasicFromGit)
    }
}
```

# Parallel Build configurations

```
project {
    vcsRoot(BuildConfVcs)

    // You have to define the build configurations
    buildType(Basic)
    buildType(BasicFromGit)

    // You set the way to execute them
    sequential {
        // parallel should be inside a sequential block
        parallel {
            buildType(Basic)
            buildType(BasicFromGit)
        }
    }
}
```