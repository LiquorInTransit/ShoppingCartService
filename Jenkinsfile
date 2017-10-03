node {
   stage('Preparation') {
   		git 'https://github.com/LiquorInTransit/ShoppingCartService.git'
   }
   stage('Build') {
        sh "mvn package"
   }
   stage('Results') {
    //  junit '**/target/surefire-reports/TEST-*.xml'
    //  archive 'target/*.jar'
   }
   stage('Deploy') {      
      withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'awscredentials', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
         ansiblePlaybook credentialsId: 'ssh-credentials', installation: 'ansible-installation', playbook: 'deploy.yaml', sudoUser: null
      }      
   }
}