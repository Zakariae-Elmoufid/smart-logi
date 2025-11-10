pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'SmartLogi:latest'
    }
    stages {
        stage('Checkout') {
            steps {
                git  branch: 'dev',  url: 'https://github.com/Zakariae-Elmoufid/smart-logi.git'
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
                    publishHTML([reportDir: 'target/site/jacoco', reportFiles: 'index.html', reportName: 'JaCoCo Report'])
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                sh './mvnw sonar:sonar -Dsonar.projectKey=my-springboot-app -Dsonar.host.url=http://localhost:9000 -Dsonar.login=YOUR_SONAR_TOKEN'
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
