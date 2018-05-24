pipeline {
    agent { label 'docker' }
    stages {
        stage('Build') {
            steps {
                sh "docker build -t ${GIT_COMMIT} ."
            }
        }
        stage('Publish Latest') {
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
        stage('Publish Tag') {
            when { buildingTag() }
            steps {
                sh "docker tag ${GIT_COMMIT} dtr.rogfk.no/fint-beta/provider:${TAG_NAME}"
                withDockerRegistry([credentialsId: 'dtr-rogfk-no', url: 'https://dtr.rogfk.no']) {
                    sh "docker push dtr.rogfk.no/fint-beta/provider:${TAG_NAME}"
                }
            }
        }
        stage('Publish PR') {
            when { changeRequest() }
            steps {
                sh "docker tag ${GIT_COMMIT} dtr.rogfk.no/fint-beta/provider:${BRANCH_NAME}"
                withDockerRegistry([credentialsId: 'dtr-rogfk-no', url: 'https://dtr.rogfk.no']) {
                    sh "docker push 'dtr.rogfk.no/fint-beta/provider:${BRANCH_NAME}'"
                }
            }
        }
    }
}
