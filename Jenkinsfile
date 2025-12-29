pipeline {
    agent any
    
    tools {
        jdk 'jdk21'
        maven 'maven3'
    }


    
    stages {  

        stage('Git Checkout') {
            steps {
                git branch: 'main', 
                    credentialsId: 'git-cred', 
                    url: 'https://github.com/bachir1915/Devops_bachir.git' 
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }

    post {
        always {
            node('') {
                cleanWs()
            }
        }       
    }
}