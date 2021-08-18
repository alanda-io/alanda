pipeline {
    agent any
    tools {
	maven 'Maven 3.6.3'
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -f ./backend/pom.xml clean install -Dmaven.test.skip=true'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -f ./backend/pom.xml test'
            }
        }
        stage('Upload to Registry') {
            when {
    	        anyOf {
     	            branch 'master';
     	            branch 'develop';
        	    }
	        }
            steps {
                sh 'mvn -f ./backend/pom.xml clean deploy -Dmaven.test.skip=true'
            }
        }
    }
 }