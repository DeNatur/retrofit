pipeline {
    agent any
    stages {
        stage("dependencies") {
            steps {
                sh 'echo "Prepare Depencies..."'
                sh 'docker build -f dependencies.DockerFile -t dependencies .'
            }
        }
        stage("build") {
            parallel {
                stage("compile app") {
                    steps {
                        sh 'echo "Compiling app..."'
                        sh 'docker build -f compileapp.DockerFile -t app-compiler .'
                    }
                }
                stage("compile unit tests") {
                    steps {
                        sh 'echo "Compiling unit tests..."'
                        sh 'docker build -f compileunittests.DockerFile -t test-compiler .'
                    }
                }
                stage("compile lint checker") {
                    steps {
                        sh 'echo "Compiling unit tests..."'
                        sh 'docker build -f compilelintcheck.DockerFile -t lint-compiler .'
                    }
                }
            }
        }

        stage("tests and checks") {
            parallel {
                stage("test") {
                    steps {
                        sh 'echo "Testing..."'
                        sh 'docker build -f test.DockerFile -t tester .'
                        sh 'docker run tester'
                    }
                }
                stage("lint check") {
                    steps {
                        sh 'echo "Checking lint..."'
                        sh 'docker build -f lint.DockerFile -t lint-checker .'
                        sh 'docker run lint-checker'
                    }
                }
            }

        }

        stage("deploy") {
            steps {
                sh 'echo "Deploying ..."'
                sh 'docker build -f deploy.DockerFile -t lint-checker .'
                sh 'docker run lint-checker'
            }
        }
    }
}