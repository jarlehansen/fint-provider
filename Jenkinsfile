pipeline {
    agent none
    stages {
        stage('Build') {
            agent { label 'docker' }
            steps {
                script {
                    props=readProperties file: 'gradle.properties'
                    VERSION="${props.version}"
                }
                sh "docker build -t 'dtr.rogfk.no/fint-beta/provider:${VERSION}' ."
            }
        }
        stage('Publish') {
            agent { label 'docker' }
            when {
                branch 'master'
            }
            steps {
                withDockerRegistry([credentialsId: 'dtr-rogfk-no', url: 'https://dtr.rogfk.no']) {
                    sh "docker push 'dtr.rogfk.no/fint-beta/provider:${VERSION}'"
                }
            }
        }
    }
}
