def call(String pipelineConfigPath) {
    pipeline {
        agent {
            label 'parent'
        }
        stages {
            stage('Read Properties from YAML') {
                steps {
                    script {
                        config = readYaml file: "${pipelineConfigPath}"
                    }
                }
            }
            stage('Build') {
                when {
                    expression { "${config.pipeline.stages.build.status}" == "enabled" }
                }
                steps {
                    Build("${config.pipeline.stages.build.cmd_name}", "${config.pipeline.stages.build.cmd_args}")
                }
            }
        }
    }
}
