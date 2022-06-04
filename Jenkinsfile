pipeline {
    agent any
    stages {
        stage("build") {
            steps {
                sh 'echo "Prepare Dependencies container"'
                
                sh 'docker build -f dependencies.DockerFile -t dependencies .'

                sh 'echo "Prepare Build container"'

                sh 'docker build -f build.DockerFile -t builder .'
            }
        }

        stage("tests and checks") {

            stage("test") {
                steps {
                    sh 'docker build -f test.DockerFile -t tester .'

                    sh 'echo "Testing..."'

                    sh 'docker run tester'
                }
            }
            stage("lint check") {
                steps {
                    sh 'docker build -f lint.DockerFile -t lint-checker .'

                    sh 'echo "Checking lint..."'

                    sh 'docker run lint-checker'
                }
            }
        }
    }
}