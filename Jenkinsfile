pipeline {
    agent none
    stages {
        stage('Build') {
            agent { label 'docker' }
            steps {
                sh 'env'
                sh "docker build -t ${GIT_COMMIT} ."
            }
        }
        stage('Publish') {
            agent { label 'docker' }
            when {
                branch 'master'
            }
            steps {
                sh "docker tag ${GIT_COMMIT} dtr.rogfk.no/fint-beta/provider:latest"
                withDockerRegistry([credentialsId: 'dtr-rogfk-no', url: 'https://dtr.rogfk.no']) {
                    sh 'docker push dtr.rogfk.no/fint-beta/provider:latest'
                }
            }
        }
        stage('Tag image') {
            agent { label 'docker' }
            when { buildingTag() }
            steps {
                sh "docker tag ${GIT_COMMIT} dtr.rogfk.no/fint-beta/provider:${TAG_NAME}"
                withDockerRegistry([credentialsId: 'dtr-rogfk-no', url: 'https://dtr.rogfk.no']) {
                    sh "docker push dtr.rogfk.no/fint-beta/provider:${TAG_NAME}"
                }
            }
        }
    }
}
