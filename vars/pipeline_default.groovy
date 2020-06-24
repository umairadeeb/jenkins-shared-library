// ==========================================================
// This is the main method that is called from a Jenkinsfile
// ==========================================================
//

def call() {

    // read properties file of default pipeline.

    node() {
        checkout scm
        config = readYaml file: 'pipeline/properties_default.yaml'
    }

    // match jenkins' current job name with the provided job for every job type

    config.pipeline_job.each { pipe_name, pipe_values ->

        if (JOB_NAME.toLowerCase().contains(pipe_values.job_name)) {
            println "Found a pipeline that matches job name."
            println "JOB NAME: ${pipe_values.job_name}"
            println "PIPELINE SCRIPT: ${pipe_name}.groovy"
            "$pipe_name"(pipe_values.config_path)
        }

    }

} // end of method