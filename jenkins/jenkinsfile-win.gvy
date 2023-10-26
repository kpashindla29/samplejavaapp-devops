pipeline {
    agent any
    tools { 
      maven 'MAVEN_HOME' 
      jdk 'JAVA_HOME' 
    }
    stages {
        stage('compile') {
	   steps {
                echo 'compiling..'
		git url: 'https://github.com/kpashindla29/samplejavaapp-devops'
		bat label: '', script: 'mvn compile'
           }
        }
        stage('codereview-pmd') {
	   steps {
                echo 'codereview..'
		bat label: '', script: 'mvn -P metrics pmd:pmd'
           }
	   post {
               success {
                   recordIssues enabledForFailure: true, tool: pmdParser(pattern: '**/target/pmd.xml')
               }
           }		
        }
        stage('unit-test') {
	   steps {
                echo 'codereview..'
		bat label: '', script: 'mvn test'
           }
	   post {
               success {
                   junit 'target/surefire-reports/*.xml'
               }
           }			
        }
        stage('metric-check') {
	   steps {
                echo 'unit test..'
		bat label: '', script: 'mvn verify'
           }
	   post {
               success {
	           jacoco(
	                execPattern: '**/**.exec',
	                classPattern: '**/classes',
	                sourcePattern: '**/src/main/java'
	            )
               }
           }		
        }
        stage('package') {
	   steps {
                echo 'Packaging....'
		bat label: '', script: 'mvn package'	
           }		
        }
    }
}
