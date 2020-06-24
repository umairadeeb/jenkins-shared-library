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
                            sh("helm install -f nodejs/values-${params.environment}.yaml nodejs helm/nodejs")
                            echo "Application successfully deployed on ${params.environment} environment."
                        } catch (caughtError) {
                            buildErrorMessage = caughtError
                            throw buildErrorMessage
                        }
                    }
                }
            }
        }
    }
}
