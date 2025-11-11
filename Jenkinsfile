pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'SmartLogi:latest'
    }
    stages {
        stage('Checkout') {
            steps {
                git  branch: 'devlop',  url: 'https://github.com/Zakariae-Elmoufid/smart-logi.git'
            }
        }
        stage('Build & Test') {
            steps {
                sh './mvnw clean install'
            }
        }
        stage('Code Coverage') {
            steps {
                sh './mvnw jacoco:report'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    publishHTML(target: [
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'target/site/jacoco',
                                reportFiles: 'index.html',
                                reportName: 'JaCoCo Report'
                    ])
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                 sh './mvnw clean verify sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -Dsonar.login=squ_9b126b24469c75d8ff742d001684c1116587fcdc'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }
        stage('Deploy') {
            steps {
                sh './deploy.sh'
            }
        }
    }
}
