import groovy.transform.Field @Field 

def proyectos = ["'calidad'", "'goya'", "'mobile'", "'canales-y-cognitive'", "'devops'", "'comunidad- digital-comercial'", "'servicenter'", "'portales-frontend'", "'portales-backend'", "'transversal'", "'transversal-seguridad'", "'backendForFrontend'", "' clientes'", "'frontend'", "'Informes'", "'cesantias'", "'embargos'", "'terraform-projects'"]

nodo('git'){ 
    contenedor('git'){
        propiedades([parámetros([
            [$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', filterLength: 1, filtrable: false, name: 'Rutas', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: ''], script: [sandbox: false, classpath: [], script: '["--- select ---:selected","Todas","Granular"]']]], 
            [$class: 'CascadeChoiceParameter', choiceType: 'PT_SINGLE_SELECT', filterLength: 1, filtrable: false, name: 'Proyecto', referencedParameters: 'Rutas', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: ''], script: [sandbox: false, classpath: [], script: """ 
                if(Rutas.equals("Granular")){
                    return ${proyectos} 
                } 
                """]]]
            ])]) 
            stage('CheckOut'){ checkout(
                [$class: 'GitSCM', ramas: [[name: '*/main']], extensiones: [], userRemoteConfigs: [[credencialesId: 'github-emu', url: 'https://github.com/PorvenirEMU/devops-jenkins-jobs.git']]]) 
            } 
            stage('Crear trabajos'){ 
                if (params.'Rutas' = = 'Todas') {
                    objetivos jobDsl: "scripts/**/**/*" 
                }else if (params.'Rutas' == 'Granular') { 
                    objetivos jobDsl: "scripts/${params.Proyecto}/* */*" 
                } 
            } 
        } 
    }