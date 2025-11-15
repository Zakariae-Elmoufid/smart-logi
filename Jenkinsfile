pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'smartlogi:latest'
        SONAR_HOST = 'http://localhost:9000'
        SONAR_TOKEN = credentials('SonarQube_Local')
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'devlop', url: 'https://github.com/Zakariae-Elmoufid/smart-logi.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean verify'
            }
        }

        stage('Code Coverage (JaCoCo)') {
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
                sh "./mvnw sonar:sonar \
                    -Dsonar.projectKey=SmartLogi \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.login=qu_9b126b24469c75d8ff742d001684c1116587fcdc"
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Deploy') {
            steps {
                sh 'chmod +x deploy.sh'
                sh './deploy.sh'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
