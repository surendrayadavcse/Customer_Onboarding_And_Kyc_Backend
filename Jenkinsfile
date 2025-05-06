pipeline {
    agent any

    environment {
        IMAGE_NAME = 'surendracse/customerkyc'
     
    }

    stages {
        stage('Clone') {
            steps {
                git url: 'https://github.com/surendrayadavcse/KYC_PROJECT_FRONTEND.git', branch: 'Finalcopyjenkin'
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests=false'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
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
