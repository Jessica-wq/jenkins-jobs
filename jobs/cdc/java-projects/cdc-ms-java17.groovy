@Library('jenkins-shared-library@main')_
pipeline {
    agent { label 'microservicios-java17'}
     environment {
      TICKET_ID=""
    }

    stages {
        stage('Inicio') {
            steps {
                script{
                    env.branch = gitUtils.infoPayload('branch')
                    env.repo = gitUtils.infoPayload('urlRepo')
                    env.registryName = 'azuepvgoydvpsptacr.azurecr.io'
                    env.environment = gitUtils.infoPayload('environment')
                    env.nameProject = gitUtils.infoPayload('nameProject')
                    env.org = 'PorvenirEMU'
                    env.userGit= gitUtils.infoPayload('user')
                    env.title= gitUtils.infoPayload('title')
                    env.issue= gitUtils.infoPayload('issue')
                    env.commit= gitUtils.imageVersionGit()                    
                    gitUtils.infoPayload('action')=='opened' ? jenkinsUtils.deployProduction(branch: env.environment) : error('pull request: '+gitUtils.infoPayload('action'))
                                     
                    gitCheckout(
                        branch: env.branch,
                        org: env.org,
                        repo: env.repo
                    )                         
                    env.IMAGE_VERSION = gitUtils.riseVersionNumber()
                    env.versionLibreriaElastic = sh(script: 'grep -A 2 \'<artifactId>tra-log-lib-elastic-v17-java</artifactId>\' pom.xml | grep \'<version>\' | awk -F\'[<>]\' \'/version/{print $3}\'', returnStdout: true).trim()
                  
                }
            }
        }
        stage('Integración Continua'){
          when{
            expression{ gitUtils.validatePayload('CI') == true }
          }
          steps{
            script{
              container('maven'){   
                config = readYaml(file: 'Jenkinsfile.yaml')
                downloadLibs(versionLibreriaElastic)            
                build.buildMvn()
              }

              if(env.environment != "main" || env.branch !="release"){
                  container('sonar'){
                        sonar.sonarqubeMvnSonarScanner(
                            idSonarToken: 'sonarToken',
                            nameProject: env.nameProject,
                            exclusion: '**/domain/dto/**/*,src/main/java/com/porvenir/config/**/*,src/main/java/com/porvenir/interceptor/util/**,**/integration/**/*,**/persistence/**/*'
                        )
                  }
                  
                  container('fortify'){ 
                    scanFortify (
                      routeScan: "src/**/*.java",
                      nameProject: env.nameProject
                    )
                  }
              }else{
                  jenkinsUtils.printm("Ya existe una incidencia en Jira con la evidencia del escaneo de código estático en el siguiente tablero: https://jsp-sdlc-pr-prv-01.atlassian.net/jira/software/c/projects/LBFD/boards/235 Para acceder al reporte realizar la búsqueda así: ${env.nameProject}_${env.branch}", "INFO")
              }

              config = readYaml(file: 'Jenkinsfile.yaml')
              kubeUtils.buildPushDocker(
                registryName: env.registryName,
                imageName: "${config.artifactName}-${env.environment}",
                imageVersion: env.IMAGE_VERSION,
                dockerfilePath: '.'
              )

              securityUtils.scanImageAzure(
                registryName: env.registryName,
                image: "${env.registryName}/${config.artifactName}-${env.environment}:${env.IMAGE_VERSION}",
                severity: 'HIGH,CRITICAL',
                stop: false
              )

              container('git'){
                gitUtils.commitTag(
                  branch: env.environment,
                  tagVersion: env.IMAGE_VERSION,
                  repo: env.repo
                )
              }
             
            }
          }
        }
        stage('Despliegue Continuo'){
          when{
            expression{ gitUtils.validatePayload('CD') == true }
          }
          steps{
            script{
              container('azcli'){

                if(!gitUtils.validatePayload('CI')){
                  if(gitUtils.infoPayload('title').contains("version=")){
                      env.IMAGE_VERSION = gitUtils.infoPayload('version')
                  }else{
                      error("El título del Pull Request NO contiene version a desplegar")
                  }
                }
                config = readYaml(file: 'Jenkinsfile.yaml')
                kubeUtils.deployPod(
                  project: config.project,
                  path: config.pathKubernetes,
                  environment: env.environment,
                  imageVersion: env.IMAGE_VERSION,
                  imageName: "${config.artifactName}-${env.environment}"
                )
              }
             
            }
          }
        }
    }
    post{
        always{
          script{
            def email = ""
             if(fileExists('vulnerability.txt') && fileExists('code.pdf') && gitUtils.validatePayload('CI') == true){
              sh "cat vulnerability.txt"

              def tempUser = env.userGit.split('_')
              email = "${tempUser[0]}@porvenir.com.co"

              jiraUtils.deleteIssueExists(project: env.nameProject)
              TICKET_ID = jiraUtils.createJiraSoftwareFortify(
                nameProject: env.nameProject,
                repo: env.repo,
                branch: env.branch,
                email: email
                
              )
              println "ticket :"+ TICKET_ID
              jiraUtils.addFileJiraSoftware(TICKET_ID)

              if(env.environment == "release" && env.title.contains('issue=') ){
                jiraUtils.issuelinkJiraSoftware(
                  issue: env.issue,
                  ticked_id: TICKET_ID,      
                  repo: env.repo,
                  branch: env.branch,
                  commit: env.commit,
                  result: currentBuild.result.toString()
                )
                
              }

            }
            def repoName = env.repo.tokenize('/').last()
            repoName = repoName.tokenize('.').first()
            jiraUtils.createINCJiraLBSD(
              projectName: repoName,
              branch : env.branch,
              responsable : email
            )
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
              logName: "report.txt,code.pdf"
            )
          }            
        }
    }
}
def downloadLibs(versionLibreriaElastic){
  withCredentials([usernamePassword(credentialsId: 'artifactory-cloud', passwordVariable: 'pass', usernameVariable: 'user')]) {
    sh """
      curl -k -u ${user}:${pass} -O  "https://devops.porvenir.com/artifactory/transversal/com/porvenir/log/tra-log-lib-elastic-v17-java/${versionLibreriaElastic}/tra-log-lib-elastic-v17-java-${versionLibreriaElastic}.jar"
                                      
    """
    if (fileExists("tra-log-lib-elastic-v17-java-${versionLibreriaElastic}.jar")){
        sh """
            mkdir -p repo/com/porvenir/log/tra-log-lib-elastic-v17-java/${versionLibreriaElastic}/
            mv tra-log-lib-elastic-v17-java-${versionLibreriaElastic}.jar repo/com/porvenir/log/tra-log-lib-elastic-v17-java/${versionLibreriaElastic}/
        """
    }
  } 
}