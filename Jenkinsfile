@Library("jenkins-library") _
pipeline {
    agent {
        kubernetes {
            yaml """ 
apiVersion: v1
kind: Pod
metadata:
  name: docker-pod
  namespace:
  labels:
    app: docker
spec:
  containers:
    - name: docker
      image: docker:latest
      command: ["tail", "-f", "/dev/null"]
      imagePullPolicy: Always
      volumeMounts:
        - name: docker
          mountPath: /var/run/docker.sock
    - name: kubectl
      image: jshimko/kube-tools-aws:latest
      command:
      - cat
      tty: true
  volumes:
  - name: docker
    hostPath:
      path: /var/run/docker.sock
"""
        }
    } 

    tools {
        maven 'maven'
        // 'org.jenkinsci.plugins.docker.commons.tools.DockerTool' 'docker'
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '1'))
        disableConcurrentBuilds()
    }

    environment {
        PORT           = 5000
        VERSION        = '1'
        REGISTRY       = 'archieaqua/bubble-b'
        DOCKERHUBCREDS = 'dockerhub-creds'
        DOCKERIMAGE    = ''
        COLOR          = 'test'
    }

    stages {
        // stage('Code Quality Analysis') {
        //     steps{
        //         withSonarQubeEnv(credentialsId: 'sonar-token', installationName: 'sonar') {
        //             sh 'mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=revature-bubble3-devops-team-1_BackEnd'
        //         }
        //     }
        // }
        stage('Clean & Package Directory') {
            steps {
                sh 'mvn clean'
            }
        }
        stage('Package Jar') {
            steps {
                sh 'mvn -DskipTests package'
            }
        }
        
        stage('Create Image') {
            steps {
                container('docker') {
                    script {

                    docker.build("${env.REGISTRY}:${env.VERSION}.${env.BUILD_ID}")
                    }
                }
            }
        }
        stage("Push Image to DockerHub") {
                steps {
                    container('docker'){
                    script {
                        docker.withRegistry('', DOCKERHUBCREDS){
                            docker.image(REGISTRY).push("$VERSION.$currentBuild.number")
                        }
                    }
                }
            }
        }
        stage('Set EKS Use'){
            steps{
                container('kubectl'){
                    script{
                        withAWS(credentials:'aws-creds', region:'us-east-1'){
                            sh 'aws eks update-kubeconfig --name team-aqua-mx2ESgug'
                        }
                    }
                }
            }
        }
        stage('Find Current Color'){
            steps{
                container('kubectl'){
                    script{
                        withAWS(credentials:'aws-creds', region:'us-east-1'){
                           env.COLOR = sh '$(echo kubectl get service backend -o jsonpath="{.spec.selector.color}")'
                        }
                        sh 'echo $COLOR'
                    }
                }
            }
        }
        stage('Deploy to Standby Pod') { 
            steps {
                container('kubectl'){
                    script {

                        withAWS(credentials:'aws-creds', region:'us-east-1'){

                            sh 'aws eks update-kubeconfig --name team-aqua-mx2ESgug'
                            sh 'echo $REGISTRY:$BUILD_ID'

                            if (sh(script: "kubectl get service backend -o jsonpath='{.spec.selector.color}'", returnStdout: true).trim() == 'blue') {
                                sh 'kubectl set image deployment.apps/backend-green bubble=$REGISTRY:$VERSION.$currentBuild.number'
                            } else {
                                sh 'kubectl set image deployment.apps/backend-blue bubble=$REGISTRY:$VERSION.$currentBuild.number'
                            }
                        }
                    } 
                }
            }
        }
        stage("Await Approval") {
            steps {
                script {
                    // Prompt, if yes build, if no abort
                    try {
                        timeout(time: 30, unit: 'MINUTES'){
                            approved = input message: 'Is standby pod healthy?', ok: 'Continue deployment',
                                parameters: [choice(name: 'approved', choices: 'Yes\nNo', description: 'Deploy this pod')]
                            if(approved != 'Yes'){
                                error('Build not approved')
                            }
                        }
                    } catch (error){
                        error('Build not approved in time')
                    }
                }
            }
        }
        stage('Reroute Traffic') { 
            steps {
                container('kubectl'){
                    script {

                        withAWS(credentials:'aws-creds', region:'us-east-1'){

                            sh 'aws eks update-kubeconfig --name team-aqua-mx2ESgug'

                            if (sh(script: "kubectl get service backend -o jsonpath='{.spec.selector.color}'", returnStdout: true).trim() == 'blue') {
                                
                                sh 'kubectl patch svc backend --type=json -p \'[{\\"op\\":\\"replace\\",\\"path\\":\\"/spec/selector/color\\",\\"value\\":\\"green\\"}]\''

                            } else {
                                sh 'kubectl patch svc backend --type=json -p \'[{\\"op\\":\\"replace\\",\\"path\\":\\"/spec/selector/color\\",\\"value\\":\\"blue\\"}]\'' 
                            }
                        }
                    } 
                }
            }
        }
    }
 }

 