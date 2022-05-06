pipeline {
    agent any

    tools {
        maven 'maven'
    }

    options {
        buildDiscarder(logRotator(daysToKeepStr: '7', numToKeepStr: '1'))
        disableConcurrentBuilds()
    }

    environment {
        PORT = 5000
        IMAGE_TAG = "latest"
        registry       = 'archieaqua/bubble-b'
        CONTAINER_NAME = "bubble-b"
        CRED = "dockerhub"
        dockerImage    = ''
    }

    stages {
        stage('Code Quality Analysis'){
            steps{
                withSonarQubeEnv(credentialsId: 'sonar-token', installationName: 'sonar'){
                    sh 'mvn Backend/ verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=revature-bubble3-devops-team-1'
                }
            }
        }
        stage('Clean & Package Directory') {
            steps {
                sh 'mvn -f Backend/pom.xml clean package -Dmaven.test.skip'
//                 discordSend description: ":soap: *Cleaned ${env.JOB_NAME}*", result: currentBuild.currentResult,
//                 webhookURL: env.WEBHO_BE
            }
        }
//         stage('Run Tests') {
//             steps {
//                 sh 'mvn test'
// //                 discordSend description: ":memo: *Tested ${env.JOB_NAME}*", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
//                 script {testfail = false}
//             }
//         }
//         stage('Package Jar') {
//             steps {
//                 sh 'mvn -DskipTests package'
// //                 discordSend description: ":package: *Packaged ${env.JOB_NAME}*", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
//             }
//         }
        // stage('SonarCloud') {
        //     environment {
        //         SCANNER_HOME = tool 'sonar'
        //         ORGANIZATION = "revature-bubble"
        //         PROJECT_NAME = "Revature-Bubble_BackEnd"
        //     }
        //     steps {
        //         withSonarQubeEnv('CloudScan') {
        //             sh '''$SCANNER_HOME/bin/sonar-scanner -Dsonar.organization=$ORGANIZATION \
        //                 -Dsonar.java.binaries=target/classes/com/revature/ \
        //                 -Dsonar.projectKey=$PROJECT_NAME \
        //                 -Dsonar.sources=.'''
        //         }
        //     }
        // }
        // stage("Quality Gate") {
        //     steps {
        //        script{
        //             timeout(time: 40, unit: 'MINUTES') {
        //                 approved = input mesasage: 'Deploy to production?', ok: 'Continue',
        //                     parameters: [choice(name: 'approved', choices: 'Yes\nNo', description: 'Deploy this build to production')]
        //                 if(approved != 'Yes'){
        //                     error('Build not approved')
        //                     }
        //                 }
        //             } catch (error){
        //                 error('Build not approved in time')
        //             }
        //         }
        //     }
//         stage('Remove Previous Artifacts') {
//             steps {
//                 sh 'docker stop ${CONTAINER_NAME} || true'
// //                 discordSend description: ":axe: *Removed Previous Docker Artifacts*", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
//             }
//         }
//         stage('Create Image') {
//             steps {
//                 sh 'docker build -t ${IMAGE_TAG} -f Dockerfile .'
// //                 discordSend description: ":screwdriver: *Built New Docker Image*", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
//             }
//         }
//         stage('Run Container') {
//             steps {
//                 sh 'docker run -d --env DB_URL --env DB_USER --env DB_PASS --rm -p ${PORT}:${PORT} --name ${CONTAINER_NAME} ${IMAGE_TAG} '
// //                 discordSend description: ":whale: *Running Docker Container*", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
//             }
//         }
//         stage('Push to DockerHub') {
//             steps {
//                 script {
//                     docker.withRegistry('', CRED) {
//                           docker.image(IMAGE_TAG).push()
//                     }
//                 }
// //                 discordSend description: ":face_in_clouds: *Pushed Latest to DockerHub*", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
//             }
//         }
           stage("Build Docker Image") {
               steps{
                   script{
                       dockerImage = docker.build "$registry"
                   }
               }
           }
           stage("Push Image to DockerHub") {
               steps {
                   script {
                       docker.withRegitry('', dockerHubCreds){
                           dockerImage.push("$currentBuild.number")
                           dockerImage.push("latest")
                       }
                   }
               }
           }
    //        stage("create kubeconfig file"){

    //        }
    //        stage("deploy blue container"){

    //            when {branch 'blue'}

    //        }
    //        stage("Redirect service to blue container"){

    //            when { branch "blue"
    //            }
    //            steps{

    //            }    
    //        }
    //         stage("deploy green container"){

    //            when {branch 'green'
    //            }
    //            steps{

    //            }

    //        }
    //        stage("Redirect service to green container"){

    //            when { branch "green"
    //            }
    //            steps{

    //            }
    //        }
    //        stage("Deploy to Production"){
    //            steps{
    //                script{
    //                    withAWS(credentials: 'aws-creds', region: 'us-east-1'){
    //                     sh 'curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"'
    //                     sh 'chmod u+x ./kubectl'
    //                     sh 'aws eks update-kubeconfig --profile 220307-kevin-sre-team-aqua --name team-aqua-mx2Egug --region us-east-1'
    //                     sh './kubectl get pods -n backend'
    //                     sh "echo $registry:$currentBuild.number"
    //                     sh "./kubectl set image -n backend deployment.apps/backend backend-container=$registry:$currentBuild.number"
    //                    }
    //                }
    //            }
    //        }
    }
    post {
        failure {
            script {
                def statusComment = ""
                if (testfail) {
                    def summary = junit testResults: '**/target/surefire-reports/*.xml'
                    statusComment = "*[${env.JOB_NAME}] <#${env.BUILD_NUMBER}>* failed to build on ${env.GIT_BRANCH} branch."
                    statusComment += "\nRan ${summary.getTotalCount()} total tests."
                    statusComment += "\n\tFailed ${summary.getFailCount()}, Passed ${summary.getPassCount()}, Skipped ${summary.getSkipCount()}"
                    statusComment += "\nSeems you still have a ways to go hm? :face_with_monocle:"
                } else {
                    statusComment = "**${env.JOB_NAME} ended in ${currentBuild.currentResult}**"
                    statusComment += "\n\tCheck the stage that failed for more information"
                }
//                 discordSend description: statusComment, result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
            }
        }
        success {
//             discordSend description: ":potable_water: **Pipeline successful!**", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
            sh 'docker container ls'
        }
    }
}