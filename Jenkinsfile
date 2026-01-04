pipeline {
    agent any

    tools {
        // Ensure these names match your "Global Tool Configuration" in Jenkins
        maven 'Maven 3'
        jdk 'JDK 17'
    }

    environment {
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
    }

    stages {
        stage('Code Checkout') {
            steps {
                checkout scm
                echo 'Code checkout completed.'
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Building the application...'
                // CHANGED: 'sh' to 'bat' for Windows
                bat 'mvn clean compile'
            }
        }

        stage('Unit Test Execution') {
            steps {
                echo 'Running unit tests...'
                // CHANGED: 'sh' to 'bat'
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('WAR File Generation') {
            steps {
                echo 'Packaging WAR file...'
                // CHANGED: 'sh' to 'bat'
                bat 'mvn package -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
            archiveArtifacts artifacts: 'target/*.war', fingerprint: true
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}