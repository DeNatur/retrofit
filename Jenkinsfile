pipeline {
    agent any
    parameters {
        string(name: "ENVIRONMENT", defaultValue: "RELEASE")
        string(name: "VERSION", defaultValue: "1.0.0")
    }
    stages {

        // Necessery to provide new changes if previous build failed
        stage("initial cleanup"){
            when {
                expression {
                    !("SUCCESS".equals(currentBuild.previousBuild.result))
                }
            }
            steps{
                sh 'docker stop publisher || true'
                sh 'docker rm publisher || true'
                sh 'docker image rm deploy || true'
                sh 'docker image rm app-compiler || true'
                sh 'docker image rm test-compiler || true'
                sh 'docker image rm lint-compiler || true'
            }
        }
        stage("dependencies") {
            steps {
                sh 'echo "Prepare Dependencies..."'
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
                stage("compile tests and checks") {
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
        }

        stage("tests and checks") {
            parallel {
                stage("test") {
                    steps {
                        sh 'echo "Testing..."'
                        sh 'docker build -f test.DockerFile -t tester .'
                    }
                }
                stage("lint check") {
                    steps {
                        sh 'echo "Checking lint..."'
                        sh 'docker build -f lint.DockerFile -t lint-checker .'
                    }
                }
            }

        }

        stage("deploy") {
            steps {
                sh 'echo "Deploying ..."'
                sh 'docker build -f deploy.DockerFile -t deploy .'
            }
        }

        stage("publish") {
            when {
                expression { 
                    return params.ENVIRONMENT == 'RELEASE'
                }
            }

            steps {
                sh 'echo "Publishing ..."'
                sh 'docker run --name publisher -v $PWD:/retrofit/retrofit/temp app-compiler bash -c \"mv retrofit/build/libs/retrofit* /retrofit/retrofit/temp/\"'
                sh "mv retrofit*.jar retrofit-v${params.VERSION}.jar"
                archiveArtifacts artifacts: '*.jar'
            }
        }

        stage("clean") {
            steps{
                sh 'docker stop publisher || true'
                sh 'docker rm publisher || true'
                sh 'docker image rm app-compiler || true'
                sh 'docker image rm test-compiler || true'
                sh 'docker image rm lint-compiler || true'
            }
        }
    }
}