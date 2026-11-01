pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'gi4gi/finsecure-pay'
        AWS_REGION = 'ap-south-1'
        EKS_CLUSTER = 'finsecure-pay-eks'
        K8S_DEPLOYMENT = 'finsecure-pay'
        CONTAINER_NAME = 'finsecure-pay'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo 'Source code checked out successfully.'
            }
        }

        stage('Build') {
            steps {
                bat 'mvnw.cmd clean'
            }
        }

        stage('Test') {
            steps {
                bat 'mvnw.cmd test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvnw.cmd package -DskipTests'
            }
        }

        stage('Dependency Security Check') {
            steps {
                bat 'mvnw.cmd org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7'
            }
        }

        stage('Container Build') {
            steps {
                bat 'docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% .'
                bat 'docker tag %DOCKER_IMAGE%:%BUILD_NUMBER% %DOCKER_IMAGE%:latest'
            }
        }

        stage('Trivy Security Scan') {
            steps {
                echo 'Scanning Docker image for HIGH and CRITICAL vulnerabilities...'

                bat 'trivy image --scanners vuln --severity HIGH,CRITICAL --exit-code 1 %DOCKER_IMAGE%:%BUILD_NUMBER%'

                echo 'Trivy security scan passed. No HIGH or CRITICAL vulnerabilities detected.'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    bat 'echo %DOCKER_PASSWORD%| docker login --username %DOCKER_USERNAME% --password-stdin'

                    bat 'docker push %DOCKER_IMAGE%:%BUILD_NUMBER%'
                    bat 'docker push %DOCKER_IMAGE%:latest'

                    bat 'docker logout'
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'aws-credentials',
                        usernameVariable: 'AWS_ACCESS_KEY_ID',
                        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                    )
                ]) {
                    bat '''
                    aws eks update-kubeconfig --region %AWS_REGION% --name %EKS_CLUSTER%

                    kubectl set image deployment/%K8S_DEPLOYMENT% %CONTAINER_NAME%=%DOCKER_IMAGE%:%BUILD_NUMBER%

                    kubectl rollout status deployment/%K8S_DEPLOYMENT% --timeout=180s
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'FinSecure Pay CI/CD pipeline completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Check the stage logs before proceeding.'
        }

        always {
            echo 'Pipeline execution finished.'
        }
    }
}
