pipeline {
    agent any
    stages {
        stage("build") {
            steps {
                sh 'echo "Prepare Dependencies container"'

                sh 'docker build . -f dependencies.DockerFile dependencies'

                sh 'echo "Prepare Build container"'

                sh 'docker build . -f build.DockerFile builder'

                sh 'echo "Building..."'

                sh 'docker run builder'
            }
        }

        stage("test") {
            steps {
                sh 'docker build . -f test.DockerFile tester'

                sh 'echo "Testing..."'

                sh 'docker run tester'
            }
        }
    }
}