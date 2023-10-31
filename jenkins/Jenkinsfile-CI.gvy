pipeline {
    agent any
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
		      withCredentials([string(credentialsId: 'DOCKER_HUB_PWD', variable: 'DOCKER_HUB_PWD')]) {
                                echo $DOCKER_HUB_PWD
                                sh "docker login -u kpashindla -p $DOCKER_HUB_PWD"
		      }
		      sh 'docker push kpashindla/mysampleapp:$BUILD_NUMBER'
		    }
      }
           
    }
}
