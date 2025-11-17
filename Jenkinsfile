pipeline {
    agent any

tools {
    maven 'maven-3.8.9'
    jdk 'jdk-17'
}

    environment {
        DOCKER_IMAGE = 'smartlogi:latest'
    }

    stages {

        stage('Checkout') {
            steps {
            checkout csm
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
                withSonarQubeEnv('SonarQube_Local') {
                    sh '''
                        ./mvnw sonar:sonar \
                        -Dsonar.projectKey=SmartLogi \
                        -Dsonar.projectName=SmartLogi \
                        -Dsonar.java.coveragePlugin=jacoco \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                        -Dsonar.junit.reportPaths=target/surefire-reports \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.sources=src/main/java \
                        -Dsonar.tests=src/test/java \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.java.test.binaries=target/test-classes
                    '''
                    withSonarQubeEnv('SonarQube') {
                        sh "echo $SONAR_HOST_URL"
                        sh "echo $SONAR_AUTH_TOKEN"
                    }

                }
            }
        }

       stage('Package') {
                   steps {
                       sh 'mvn package -DskipTests'
                       archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                   }
               }
           }

           post {
               always {
                   echo "Build ${currentBuild.result} - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
               }
               success {
                   echo 'Pipeline exécuté avec succès!'
               }
               failure {
                   echo 'Pipeline a échoué!'
               }
           }
}