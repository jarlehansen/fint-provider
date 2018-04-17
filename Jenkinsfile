pipeline {
    agent none
    stages {
        stage('Build') {
            agent { label 'docker' }
            steps {
                sh 'env'
                sh "docker build -t ${BUILD_ID} ."
            }
        }
        stage('Publish') {
            agent { label 'docker' }
            when {
                branch 'master'
            }
            steps {
                sh "docker tag ${BUILD_ID} dtr.rogfk.no/fint-beta/provider:latest"
                withDockerRegistry([credentialsId: 'dtr-rogfk-no', url: 'https://dtr.rogfk.no']) {
                    sh 'docker push dtr.rogfk.no/fint-beta/provider:latest'
                }
            }
        }
    }
}
