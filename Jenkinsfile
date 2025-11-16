pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "smartlogi:latest"
        SONAR_HOST_URL = "http://sonarqube:9000"
        SONAR_TOKEN = credentials('SONAR_TOKEN') // يجب إضافته في Jenkins
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'devlop', url: 'https://github.com/Zakariae-Elmoufid/smart-logi.git'
            }
        }

        stage('Build & Test') {
            steps {
                dir('/opt/projects/SmartLogi') {
                    // Build the project with Maven
                    sh 'mvn clean package'
                }
            }
        }

        stage('Code Coverage (JaCoCo)') {
            steps {
                dir('/opt/projects/SmartLogi') {
                    // Generate JaCoCo report
                    sh 'mvn jacoco:report'
                }
            }
            post {
                always {
                    dir('/opt/projects/SmartLogi') {
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
        }

        stage('SonarQube Analysis') {
            steps {
                dir('/opt/projects/SmartLogi') {
                    sh """
                        mvn clean verify sonar:sonar \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('/opt/projects/SmartLogi') {
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Deploy') {
            steps {
                dir('/opt/projects/SmartLogi') {
                    sh 'chmod +x deploy.sh'
                    sh './deploy.sh'
                }
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
