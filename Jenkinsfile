pipeline {
    agent any
    stages {
        stage("dependencies") {
            steps {
                sh 'echo "Prepare Dependencies..."'
                sh 'docker volume create --name vol-out'
                sh 'docker inspect vol-out'
                sh 'docker build -f dependencies.DockerFile -t dependencies .'
            }
        }
        stage("build") {
            stages {
                stage("compile app") {
                    steps {
                        sh 'echo "Compiling app..."'
                        sh 'docker build -f compileapp.DockerFile -t app-compiler .'
                    }
                }
                parallel {
                    stage("compile unit tests") {
                        steps {
                            sh 'echo "Compiling unit tests..."'
                            sh 'docker build -f compileunittests.DockerFile -t test-compiler .'
                        }
                    }
                    stage("compile lint checker") {
                        steps {
                            sh 'echo "Compiling lint check..."'
                            sh 'docker build -f compilelintcheck.DockerFile -t lint-compiler .'
                        }
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

        stage("publish") {
            steps {
                sh 'echo "Publishing ..."'
                sh 'docker build -f publish.DockerFile -t publisher .'
                sh 'docker run publisher --mount type=volume,src="vol-in", dst=/here/pip'
            }
        }

    }
}