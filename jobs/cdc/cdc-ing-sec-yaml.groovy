@Library('jenkins-shared-library@main')_
pipeline {
    agent { label 'azcli'}

    stages {
        stage('Inicio') {
            steps {
                script{
                    env.branch = gitUtils.infoPayload('branch')
                    env.repo = gitUtils.infoPayload('urlRepo')
                    env.environment = gitUtils.infoPayload('environment')
                    env.org = 'PorvenirEMU'
                    
                    gitUtils.infoPayload('action')=='opened' ? jenkinsUtils.deployProduction(branch: env.environment) : error('pull request: '+gitUtils.infoPayload('action'))
                    
                    gitCheckout(
                        branch: env.branch,
                        org: env.org,
                        repo: env.repo
                    )                                          
                }
            }
        }
        stage('Despliegue Continuo'){
          when{
            expression{ gitUtils.validatePayload('CD') == true }
          }
          steps{
            script{             
              config = readYaml(file: 'Jenkinsfile.yaml')

              container('azcli') {
                kubeUtils.deploySecretProvider(
                  project: config.project,
                  branch: env.environment,
                  fileSecretProviderClass: config.pathSecretKubernetes                  
                )

                kubeUtils.deployIngress(
                  project: config.project,
                  branch: env.environment,
                  fileIngressRoutes: config.pathIngressKubernetes                  
                )

                def environmentToPathMap = [
                  'develop': [configMapPath: config.pathConfigMappt, logstashPath: config.pathLogstashpt],
                  'release': [configMapPath: config.pathConfigMapqa, logstashPath: config.pathLogstashqa],
                  'main': [configMapPath: config.pathConfigMapprd, logstashPath: config.pathLogstashprd]
                ]

                def targetPaths = environmentToPathMap[env.environment]
                if (targetPaths == null) {
                    error("Rama no soportada: ${env.environment}")
                }               
                
                kubeUtils.deployConfigMap(
                  project: config.project,
                  branch: env.environment,
                  configMapPath: targetPaths.configMapPath                  
                )              
                
                kubeUtils.deployLogstash(
                  project: config.project,
                  branch: env.environment,
                  logstashPath: targetPaths.logstashPath                  
                )
              }             
            }
          }
        }
    }
    post{
        always{
          script{
            if(gitUtils.validatePayload('CI') == true && fileExists('report.txt')){
              sh "cat report.txt"
            }
            if (gitUtils.infoPayload('action') == 'opened'){
              jenkinsUtils.dataInfluxdb(
                state: 'test',
                target: 'influxdb'
              )
            }else{
              println "No se registra en influxdb el resultado ya que no es un pull request opened"
            }
            jenkinsUtils.notification (
              email: "arodriguezp@porvenir.com.co,mpelaezb@porvenir.com.co,por14350@porvenir.com.co,por15001@porvenir.com.co,por12233@porvenir.com.co,sossa@porvenir.com.co,POR16399@porvenir.com.co,jacastroc@porvenir.com.co,wmorenot@porvenir.com.co,acifuentess@porvenir.com.co,POR14803@porvenir.com.co",
              logName: "report.txt"
            )
          }            
        }
    }
}
