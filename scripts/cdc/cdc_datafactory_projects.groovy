folder('AZURE_COMPONENTS/datafactory-projects') {
    displayName('datafactory-projects')
    description('Carpeta para guardar los proyectos de datafactory del squad de onboarding para el proyecto de comunidad-digital-comercial')
}

folder('AZURE_COMPONENTS/datafactory-projects/PORV-PT-RG-FWTRANS') {
    displayName('PORV-PT-RG-FWTRANS')
    description('Carpeta con los Jobs creados para el grupo de recursos PORV-PT-RG-FWTRANS')
}

folder('AZURE_COMPONENTS/datafactory-projects/PORV-PT-RG-FWTRANS/firewall-transversal') {
    displayName('firewall-transversal')
    description('Carpeta con los Jobs creados para firewall-transversal')
}

//////////////////////////////////////////////////////////////////////////////////////////////
pipelineJob('AZURE_COMPONENTS/datafactory-projects/PORV-PT-RG-FWTRANS/firewall-transversal/datafactory-reset') {
    displayName('datafactory-reset')
    definition {
        cps {
          script ("build quietPeriod: 5, wait: true, job: 'JenkinsConfig/jenkinsDSLNew', parameters: [string(name: 'Rutas', value: 'Granular'), string(name: 'Proyecto', value: 'goya')]")
        }
    }
    logRotator {
        numToKeep(5)
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////
pipelineJob('AZURE_COMPONENTS/datafactory-projects/PORV-PT-RG-FWTRANS/firewall-transversal/datafactory-pipeline') {
    displayName('datafactory-pipeline' )
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
            scriptPath('jobs/comunidad-digital-comercial/datafactory-projects/cdc-datafactory-pipeline.groovy')
        }
    }
    logRotator {
        numToKeep(5)
    }

    parameters{
        choiceParam('componente', ["pipeline"], 'Componente DataFactory')
        choiceParam('ambienteOrigen', ["DEV", "QA"], 'ambienteOrigen')
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////
pipelineJob('AZURE_COMPONENTS/datafactory-projects/PORV-PT-RG-FWTRANS/firewall-transversal/datafactory-dataset') {
    displayName('datafactory-dataset' )
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
            scriptPath('jobs/comunidad-digital-comercial/datafactory-projects/cdc-datafactory-dataset.groovy')
        }
    }
    logRotator {
        numToKeep(5)
    }

    parameters{
        choiceParam('componente', ["dataset"], 'Componente DataFactory')
        choiceParam('ambienteOrigen', ["DEV", "QA"], 'ambienteOrigen')
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////
pipelineJob('AZURE_COMPONENTS/datafactory-projects/PORV-PT-RG-FWTRANS/firewall-transversal/datafactory-data-flow') {
    displayName('datafactory-data-flow' )
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
            scriptPath('jobs/comunidad-digital-comercial/datafactory-projects/cdc-datafactory-data-flow.groovy')
        }
    }
    logRotator {
        numToKeep(5)
    }

    parameters{
        choiceParam('componente', ["data-flow"], 'Componente DataFactory')
        choiceParam('ambienteOrigen', ["DEV", "QA"], 'ambienteOrigen')
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////
pipelineJob('AZURE_COMPONENTS/datafactory-projects/PORV-PT-RG-FWTRANS/firewall-transversal/datafactory-trigger') {
    displayName('datafactory-trigger' )
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
            scriptPath('jobs/comunidad-digital-comercial/datafactory-projects/cdc-datafactory-trigger.groovy')
        }
    }
    logRotator {
        numToKeep(5)
    }

    parameters{
        choiceParam('componente', ["trigger"], 'Componente DataFactory')
        choiceParam('ambienteOrigen', ["DEV", "QA"], 'ambienteOrigen')
    }
}
