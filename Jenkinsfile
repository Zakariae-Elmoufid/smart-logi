pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'smartlogi:latest'
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
                        reportName: 'JaCoCo Coverage'
                    ])
                }
            }
        }

       stage('SonarQube Analysis') {
           steps {
               sh '''
                   export $(grep -v '^#' .env | xargs)
                    echo "Token loaded: $TOKEN"
                   ./mvnw clean verify sonar:sonar \
                     -Dsonar.host.url=http://localhost:9000 \
                     -Dsonar.login=$TOKEN
               '''
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
