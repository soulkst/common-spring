pipeline {
    agent any

    tools {
        maven "Default"
    }

    environment {
        PRIVATE_REPO_CRED = credentials('LDAP-Account')
    }

    stages {
        stage('Test') {
            steps {
                sh './gradlew clean test'
                junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Publish') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'LDAP-Account', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                    sh './gradlew publish'
                }
            }
        }
    }
}
