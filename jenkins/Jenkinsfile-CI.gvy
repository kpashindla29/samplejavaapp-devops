pipeline {
    agent any
    environment {
      DOCKER_CREDS = credentials('docker_pwd') 
    }
    stages {
        stage('compile') {
	   steps {
                echo 'compiling..'
		git url: 'https://github.com/kpashindla29/samplejavaapp-devops'
		sh script: '/opt/maven/bin/mvn compile'
           }
        }
        stage('codereview-pmd') {
	   steps {
                echo 'codereview..'
		sh script: '/opt/maven/bin/mvn -P metrics pmd:pmd'
           }
	   post {
               success {
		   recordIssues enabledForFailure: true, tool: pmdParser(pattern: '**/target/pmd.xml')
               }
           }		
        }
        stage('unit-test') {
	   steps {
                echo 'unittest..'
	        sh script: '/opt/maven/bin/mvn test'
                 }
	   post {
               success {
                   junit 'target/surefire-reports/*.xml'
               }
           }			
        }
        stage('codecoverage') {
	   steps {
                echo 'unittest..'
	        sh script: '/opt/maven/bin/mvn verify'
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
                echo 'package......'
		sh script: '/opt/maven/bin/mvn package'	
           }		
        }

     stage('push docker image') {
	      steps {
                      	sh script: 'cd  $WORKSPACE'
			sh script: 'docker build --file Dockerfile --tag docker.io/kpashindla/mysampleapp:$BUILD_NUMBER .'
                        sh script: 'docker login -u kpashindla -p $DOCKER_HUB_PWD'
		        sh script: 'docker push docker.io/kpashindla/mysampleapp:$BUILD_NUMBER'
		    }
      }
           
    }
}
