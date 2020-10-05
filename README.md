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
  ```
* Push the changes to the repo
* Update the project configuration on TeamCity UI 
    * Open the project in TeamCity UI
    * Go to **Versioned Settings** to the tab **Change log**
    * Check for changes by click on **Check for changes** button


