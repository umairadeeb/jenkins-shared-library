def call(String pipelineConfigPath) {

    pipeline {
        agent {
            label 'parent'
        }
        parameters {
            choice(
                    name: 'environment',
                    choices: "dev\nprod",
                    description: 'Select environment to deploy this application on.' )
        }

        stages {
            stage('Read Properties from YAML') {
                steps {
                    script {
                        config = readYaml file: "${pipelineConfigPath}"
                    }
                }
            }
            stage('Deploy') {
                when {
                    expression { config.pipeline.stages.deploy.status == "enabled"}
                }
                steps {
                    script {

                        echo "Environment: ${params.environment}"

                        try {
                            sh("helm install -f helm/nodejs/values-${params.environment}.yaml nodejs helm/nodejs || helm upgrade -f helm/nodejs/values-${params.environment}.yaml nodejs helm/nodejs")
                            echo "Application successfully deployed on ${params.environment} environment."
                            echo "---"
                            echo "---"
                            echo "You can access the application after couple of seconds on this url."
                            echo "Application URL: http://localhost/"
                        } catch (caughtError) {
                            buildErrorMessage = caughtError
                            throw buildErrorMessage
                        }
                    }
                }
            }
            stage('Smoke Test') {
                steps {
                    timeout(10) {
                        waitUntil {
                            script {
                                def r = sh script: "curl -s http://localhost/ | grep ${params.environment}  &> /dev/null", returnStatus: true
                                return (r == 0);
                            }
                        }
                    }
                }
            }
        }
    }
}
