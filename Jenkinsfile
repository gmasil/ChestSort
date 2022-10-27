pipeline {
  agent {
    docker {
      image 'registry.gmasil.de/docker/maven-build-container'
      args '-v /maven:/maven -e JAVA_TOOL_OPTIONS=\'-Duser.home=/maven\' -u root:root'
    }
  }
  environment {
    MAVEN_PROJECT_GROUPID_ARTIFACTID = "${sh(script:'mvn -q -Dexec.executable=echo -Dexec.args=\'${project.groupId}:${project.artifactId}\' --non-recursive exec:exec 2>/dev/null | tr -d \'\n\r\'', returnStdout: true)}"
    MAVEN_PROJECT_NAME = "${sh(script:'mvn -q -Dexec.executable=echo -Dexec.args=\'${project.name}\' --non-recursive exec:exec 2>/dev/null | tr -d \'\n\r\'', returnStdout: true)}"
  }
  stages {
    stage('compile') {
      steps {
        sh 'mvn clean package --fail-at-end'
      }
    }
    stage('analyze') {
      environment {
        SONAR_TOKEN = credentials('SONAR_TOKEN')
      }
      steps {
        script {
          def safeBranch = env.GIT_BRANCH.replaceAll("[^a-zA-Z0-9_:{\\.-]+", "-")
          sh("mvn jacoco:report sonar:sonar --no-transfer-progress -Dsonar.host.url=https://sonar.gmasil.de -Dsonar.login=\$SONAR_TOKEN -Dsonar.projectKey=${env.MAVEN_PROJECT_GROUPID_ARTIFACTID}:${safeBranch} \"-Dsonar.projectName=${env.MAVEN_PROJECT_NAME} (${env.GIT_BRANCH})\"")
        }
      }
    }
  }
  post {
    always {
      junit '**/surefire-reports/**/*.xml'
      archiveArtifacts artifacts: 'target/ChestSort.jar', fingerprint: true
      cleanWs()
      dir("${env.WORKSPACE}@tmp") {
        deleteDir()
      }
    }
  }
}
