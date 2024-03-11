folder('comunidad-digital-comercial/react-mf-projects') {
    displayName('react-mf-projects')
    description('Carpeta para guardar los proyectos de react para la Comunidad digital comercial.')
}

folder('ts') {
    displayName('ts')
    description('Carpeta para guardar los proyectos de react para la Comunidad digital comercial.')
}

pipelineJob('comunidad-digital-comercial/react-mf-projects/cdc-mf-react') {
    displayName('cdc-mf-react')
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
            scriptPath('jobs/comunidad-digital-comercial/react-mf-projects/cdc-mf-react.groovy')
        }
    }
    logRotator {
        numToKeep(5)
    }
    triggers {   
        genericTrigger {  
            genericRequestVariables {
                genericRequestVariable {
                    key("payload")
                    regexpFilter("\$.payload")
                }
            }
            token('cdc-mf-react')
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
pipelineJob('ts/cdc-test') {
    displayName('cdc-test')
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
            scriptPath('jobs/comunidad-digital-comercial/react-mf-projects/cdc-mf-react.groovy')
        }
    }
    logRotator {
        numToKeep(5)
    }
    triggers {   
        genericTrigger {  
            genericRequestVariables {
                genericRequestVariable {
                    key("payload")
                    regexpFilter("\$.payload")
                }
            }
            token('cdc-test')
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
