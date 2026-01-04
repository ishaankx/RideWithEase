pipeline {
    agent any

    tools {
        // These must match the names in your Jenkins "Global Tool Configuration"
        maven 'Maven 3'
        jdk 'JDK 17'
    }

    environment {
        // Define common maven flags to keep logs clean
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
    }

    stages {
        stage('Code Checkout') {
            steps {
                // Pulls code from the SCM defined in the Jenkins Job
                checkout scm
                echo 'Code checkout completed.'
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Building the application...'
                // Compiles the code without running tests yet
                sh 'mvn clean compile'
            }
        }

        stage('Unit Test Execution') {
            steps {
                echo 'Running unit tests...'
                // Explicitly runs the test phase
                sh 'mvn test'
            }
            post {
                always {
                    // Generates a test result trend graph in Jenkins
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('WAR File Generation') {
            steps {
                echo 'Packaging WAR file...'
                // Packages the WAR, skipping tests since they passed in the previous stage
                sh 'mvn package -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
            // Archives the generated WAR file so you can download it from Jenkins
            archiveArtifacts artifacts: 'target/*.war', fingerprint: true
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}