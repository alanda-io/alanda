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
        stage('Run enforcer') {
           when {
    	        anyOf {
     	            branch 'master';
        	    }
	        }
            steps {
                sh 'mvn -f ./backend/pom.xml enforcer:enforce -Drules=requireReleaseDeps'
            }
        }
        stage('Upload to Registry') {
            when {
    	        anyOf {
     	            branch 'master';
     	            branch 'develop';
     	            branch 'jenkins';
        	    }
	        }
            steps {
                sh 'mvn -f ./backend/pom.xml clean deploy -Dmaven.test.skip=true'
            }
        }
    }
 }
