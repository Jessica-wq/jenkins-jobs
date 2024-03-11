@Library('jenkins-shared-library@main')_
pipeline {
    agent { label 'api-management'}

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

              container('api-management') {
                apiManagementUtils.deployApiManagement(
                  project: config.project,
                  branch: env.environment
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
              state: 'real',
              target: 'influxdb'
            )
          }else{
            println "No se registra en influxdb el resultado ya que no es un pull request opened"
          }
          jenkinsUtils.notification (
            email: "arodriguezp@porvenir.com.co,mpelaezb@porvenir.com.co,por14350@porvenir.com.co,por15001@porvenir.com.co,por12233@porvenir.com.co,sossa@porvenir.com.co,POR16399@porvenir.com.co,jacastroc@porvenir.com.co,wmorenot@porvenir.com.co,acifuentess@porvenir.com.co,dcarvajals@porvenir.com.co,agutierrezl@porvenir.com.co,POR09288@porvenir.com.co,yvargasr@porvenir.com.co,gvillamizar@porvenir.com.co,dvillamilv@porvenir.com.co,agutierrezl@porvenir.com.co,dsegoviap@porvenir.com.co,hperezc@porvenir.com.co,ypvargas@porvenir.com.co,POR14593@porvenir.com.co,POR14387@porvenir.com.co,POR12725@porvenir.com.co,POR10836@porvenir.com.co,POR14803@porvenir.com.co",
            logName: "report.txt"
          )
        }            
     }
  }
}
