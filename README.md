# Buenas prácticas DevOps
Una buena práctica es una experiencia positiva, probada y replicada en contextos diversos y que, por consiguiente, pueder ser recomendada como modelo. Merece ser compartida para que el mayor número de personas pueda adaptarla y adoptarla. En este documento irán algunas de los estándares manejados por el área de DevOps principalmente enfocado al uso de tokens, configuración de pipelines y nombramiento de herramientas propias del área

# Estándar manejo de Tokens para WebHook
En la configuración para el WebHook debemos configurar un token disiente para que se ejecute el job correspondiente en Jenkins, el estándar es el siguiente dependiendo si se trabaja por squads o no:
# proyecto-tecnologia-squad || proyecto-tecnologia
# cdc-ms-java-onb || goya-ms-java
