folder('comunidad-digital-comercial/java-projects') {
    displayName('java-projects')
    description('Carpeta para guardar los proyectos de java para la Comunidad digital comercial.')
}

pipelineJob('comunidad-digital-comercial/java-projects/cdc-ms-java-telnet') {
    displayName('cdc-ms-java-telnet')
    definition {
        cpsScm {
            scm {
                git {
                    branch('main')
                    remote{
                        credentials('github-emu')
                        url('https://github.com/PorvenirEMU/devops-jenkins-jobs.git')
                    }
                }
            }
            scriptPath('jobs/comunidad-digital-comercial/java-projects/cdc-ms-java-telnet.groovy')
        }
    }
    logRotator {
        numToKeep(20)
    }
    triggers {   
        genericTrigger {  
            genericRequestVariables {
                genericRequestVariable {
                    key("payload")
                    regexpFilter("\$.payload")
                }
            }
            token('cdc-ms-java-telnet')
            regexpFilterText("")
            regexpFilterExpression("")
        }
    }
    parameters {
        wHideParameterDefinition {
            name('payload')
            defaultValue('')
            description('payload')
        }
    }
}

pipelineJob('comunidad-digital-comercial/java-projects/cdc-ms-java-17') {
    displayName('cdc-ms-java-17')
    definition {
        cpsScm {
            scm {
                git {
                    branch('main')
                    remote{
                        credentials('github-emu')
                        url('https://github.com/PorvenirEMU/devops-jenkins-jobs.git')
                    }
                }
            }
            scriptPath('jobs/comunidad-digital-comercial/java-projects/cdc-comercial-ms-java17-lib.groovy')
        }
    }
    logRotator {
        numToKeep(20)
    }
    triggers {   
        genericTrigger {  
            genericRequestVariables {
                genericRequestVariable {
                    key("payload")
                    regexpFilter("\$.payload")
                }
            }
            token('cdc-ms-java-17')
            regexpFilterText("")
            regexpFilterExpression("")
        }
    }
    parameters {
        wHideParameterDefinition {
            name('payload')
            defaultValue('')
            description('payload')
        }
    }
}

pipelineJob('comunidad-digital-comercial/java-projects/cdc-ms-java-19') {
    displayName('cdc-ms-java-19')
    definition {
        cpsScm {
            scm {
                git {
                    branch('main')
                    remote{
                        credentials('github-emu')
                        url('https://github.com/PorvenirEMU/devops-jenkins-jobs.git')
                    }
                }
            }
            scriptPath('jobs/comunidad-digital-comercial/java-projects/cdc-comercial-ms-java19.groovy')
        }
    }
    logRotator {
        numToKeep(20)
    }
    triggers {   
        genericTrigger {  
            genericRequestVariables {
                genericRequestVariable {
                    key("payload")
                    regexpFilter("\$.payload")
                }
            }
            token('cdc-ms-java-19')
            regexpFilterText("")
            regexpFilterExpression("")
        }
    }
    parameters {
        wHideParameterDefinition {
            name('payload')
            defaultValue('')
            description('payload')
        }
    }
}
