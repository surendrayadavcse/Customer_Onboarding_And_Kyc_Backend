pipeline {
    agent any

    environment {
        IMAGE_NAME = 'surendracse/customerkyc'
        DOCKER_CREDENTIALS_ID = '8f97f107-bbc2-401c-a703-e9b1b3153ae7'
    }

    stages {
        stage('Clone') {
            steps {
                git url: 'https://github.com/surendrayadavcse/KYC_PROJECT_FRONTEND.git', branch: 'Finalcopyjenkin'
            }
        }

        stage('Build') {
            steps {
                bat '.\\mvnw.cmd clean package -DskipTests=false'
            }
        }

        stage('Test') {
            steps {
                bat '.\\mvnw.cmd test'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    dockerImage = docker.build("${IMAGE_NAME}:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Docker Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS_ID}") {
                        dockerImage.push()
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build and push successful!"
        }
        failure {
            echo "❌ Build failed!"
        }
    }
}
