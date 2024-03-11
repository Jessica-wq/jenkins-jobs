@Library('jenkins-shared-library@main')_
import java.util.ArrayList
import groovy.transform.Field

@Field def objectListDEV = []
@Field def objectListQA = []

pipeline {
  agent { label 'azcli'}

  stages {
    stage('Inicio') {
      steps {
        script{
		  createComponentList()
      properties([parameters([
        [$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', filterLength: 1, filterable: false, 
            name: 'componente', randomName: 'choice-parameter-9465507585539128', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: ''], script: [classpath: [], sandbox: false, 
            script: '["data-flow:selected"]']]], 
        [$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', filterLength: 1, filterable: false, 
            name: 'ambienteOrigen', randomName: 'choice-parameter-9465507587491625', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: ''], 
            script: [classpath: [], sandbox: false, script: '["DEV:selected", "QA"]']]], 
        [$class: 'CascadeChoiceParameter', choiceType: 'PT_SINGLE_SELECT', filterLength: 1, filterable: false, 
            name: 'nombreComponente', randomName: 'choice-parameter-9465507588986081', 
            referencedParameters: 'componente, ambienteOrigen', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: ''], script: [classpath: [], sandbox: true, script: """
            switch(ambienteOrigen){
              case "DEV":
                return ${objectListDEV}
              break;
              case "QA":
                return ${objectListQA}
              break;
            } """ ]]],
        [$class: 'CascadeChoiceParameter', choiceType: 'PT_SINGLE_SELECT', filterLength: 1, filterable: false, 
            name: 'ambienteDestino', randomName: 'choice-parameter-9465507585539124', 
            referencedParameters: 'ambienteOrigen', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: ''], script: [classpath: [], sandbox: false, script: """
            if( ambienteOrigen.equals('DEV')){ return ['QA']  }else { return ['PRD'] } """]]]
        
        ])])
        }
      }
    }

    stage('Despliegue'){
      steps{
        script{
          jenkinsUtils.deployProductionDataFactory(
            ambienteDestino: params.ambienteDestino
          )

          container('azcli'){
              dataFactoryUtils.migrateDataFactoryComponent (
                  project: "datafactory-cdc",
                  componente: params.componente,
                  nombreComponente: params.nombreComponente,
                  ambienteOrigen: params.ambienteOrigen,
                  ambienteDestino: params.ambienteDestino
              )
              println "El componente ${params.nombreComponente} fue migrado del ambiente de ${params.ambienteOrigen} a ${params.ambienteDestino}."
          }
        }
      }
    }
  }
  post{
    always{
      script{
	    jenkinsUtils.dataInfluxdb(
          state: 'real',
          target: 'influxdb'
        )
        jenkinsUtils.notification (
          email: "arodriguezp@porvenir.com.co,mpelaezb@porvenir.com.co,por14350@porvenir.com.co,por15001@porvenir.com.co,por12233@porvenir.com.co,sossa@porvenir.com.co,POR16399@porvenir.com.co,jacastroc@porvenir.com.co,wmorenot@porvenir.com.co,POR14803@porvenir.com.co",
          logName: "report.txt"
        )
      }
    }
  }
}

def createComponentList(){
  container ('azcli'){
    script{
      dataFactoryUtils.dataFatoryLogin(
                  project: "datafactory-cdc",
                  environment: "DEV",
                  component: "data-flow"
              )

      objectListDEV = dataFactoryUtils.getDataFatoryComponents(
                  project: "datafactory-cdc",
                  environment: "DEV",
                  component: "data-flow"
              )

      objectListQA  = dataFactoryUtils.getDataFatoryComponents(
                  project: "datafactory-cdc",
                  environment: "QA",
                  component: "data-flow"
              )
    }
  }
}
