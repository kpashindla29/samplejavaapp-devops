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
					git url: 'https://github.com/kpashindla29/spring-boot-restapi-for-ec2.git'
					sh 'mvn compile'
				 }
		}
        stage('codereview-pmd') {
		   steps {
					echo 'codereview..'
					sh 'mvn -P metrics pmd:pmd'
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
					sh 'mvn test'
			   }
		   post {
				   success {
					   junit 'target/surefire-reports/*.xml'
				   }
			   }			
			}
        stage('code-coverage-stage') {
		   steps {
					echo 'unit test..'
					sh 'mvn verify'
				}
		   post {
				   success {
						
						jacoco(
							execPattern: '**/target/**.exec',
							classPattern: '**/target/classes',
							sourcePattern: '**/src',
							inclusionPattern: 'com/ab/**',
							changeBuildStatus: true
						)
				   }
			   }		
        }
        stage('Package') {
		   steps {
					echo 'Packaging..'
					sh 'mvn package'	
			   }		
        }
       
    }
}
