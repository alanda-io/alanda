pipeline {
    agent Linux

    tools {nodejs "node"}

    stages {
        stage('Formatting') {
            steps {
                sh 'cd frontend/alanda'
                sh 'npm ci'
                sh 'npm run format:check'
            }
        }
    }
}
