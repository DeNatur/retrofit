pipeline {
    agent any
    stages {
        stage("build") {
            steps {
                sh 'echo "Starting Building Project"'

                echo "Prepering docker volumins"

                sh 'docker volume create vol-in'
                sh 'docker volume create vol-out'
                sh 'git clone https://github.com/DeNatur/retrofit /var/lib/docker/volumes/vol-in/_data'
                sh 'docker build . -f dependencies.DockerFile dependencies'
                sh 'docker build . -f build.DockerFile builder'

                sh 'docker run \
                        --mount source=vol-in,target=/vol-in \
                        --mount source=vol-out,target=/vol-out builder'
            }
        }

        stage("test") {
            steps {
                sh 'docker build . -f test.DockerFile tester'

                sh 'docker run \
                        --mount source=vol-in,target=/vol-in tester'
            }
        }
    }
}