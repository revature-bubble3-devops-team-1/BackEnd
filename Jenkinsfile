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
        COLOR          = ''
    }

    stages {
        // stage('Code Quality Analysis'){
        //     steps{
        //         withSonarQubeEnv(credentialsId: 'sonar-token', installationName: 'sonar'){
        //             sh 'mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=revature-bubble3-devops-team-1_BackEnd'
        //         }
        //     }
        // }
        // stage('Dependecy download') {
        //     steps {
        //         sh 'npm install'
        //     }
        // }

        stage('Clean & Package Directory') {
            steps {
                sh 'mvn clean'
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
        stage('Package Jar') {
            steps {
                sh 'mvn -DskipTests package'
//                 discordSend description: ":package: *Packaged ${env.JOB_NAME}*", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
            }
        }
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
        // stage('Checkout') {
        //     steps {
        //         git 'https://github.com/jenkinsci/docker-jnlp-slave.git'
        //     }
        // }

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
// //         }
        //    stage("Build Docker Image") {
        //        steps{
        //            script{
        //                dockerImage = docker.build "$REGISTRY"
        //            }
        //        }
        //    }
    //         stage('Login-Into-Docker') {
    //         steps {
    //             container('docker') {
    //             sh 'docker login -u <CREDS_username> -p <CREDS_password>'
    //         }
    //     }
    // }
           
        //Determine whether we want blue or green deployment by default
        // Determine when conditions will switch from blue to green and v-v
        //Deploy image depending on the current branch
    //        stage("create kubeconfig file"){

    //        }
        //    stage("deploy blue container"){

            //    when {branch 'blue'}
            //    steps {
            stage('Create Image') {
                steps {
                    container('docker'){
                    script{

                    docker.build("${env.REGISTRY}:${env.VERSION}.${env.BUILD_ID}")
                    }
                }
//                 discordSend description: ":screwdriver: *Built New Docker Image*", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
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

        //    stage("Redirect service to blue container"){

        //        when { branch "blue"
        //        }
        //        steps{

        //        }    
        //    }
        //     stage("deploy green container"){

        //        when {branch 'green'
        //        }
        //        steps{

        //        }

        //    }
        //    stage("Redirect service to green container"){

        //        when { branch "green"
        //        }
        //        steps{

        //        }
        //    }
    //     stage('Set kubectl use-context') {
	// 		steps {
	// 			withAWS(region:'us-east-1', credentials:'aws-creds') {
	// 				sh '''
	// 					kubectl config use-context arn:aws:eks:us-east-1:855430746673:cluster/team-aqua-mx2ESgug
	// 				'''
	// 			}
	// 		}
	// 	}
	// 	stage('Blue replication controller') {
	// 		steps {
	// 			withAWS(region:'us-east-1', credentials:'aws-creds') {
	// 				sh '''
	// 					kubectl apply -f ./bubble-backend-blue-deployment.yml
	// 				'''
	// 			}
	// 		}
	// 	}
	// 	stage('Green replication controller') {
	// 		steps {
	// 			withAWS(region:'us-east-1', credentials:'aws-creds') {
	// 				sh '''
	// 					kubectl apply -f ./bubble-backend-green-deployment.yml
	// 				'''
	// 			}
	// 		}
	// 	}
	// 	stage('Create the service in kubernetes cluster traffic to blue controller') {
	// 		steps {
	// 			withAWS(region:'us-east-1', credentials:'aws-creds') {
	// 				sh '''
	// 					kubectl apply -f ./bubble-backend-service.yml
	// 				'''
	// 			}
	// 		}

    //         stage('User approve to continue') {
    //         steps {
    //             timeout(time: 40, unit: 'MINUTES') {
    //                     approved = input mesasage: 'Ready to change redirect traffic to green?', ok: 'Continue',
    //                         parameters: [choice(name: 'approved', choices: 'Yes\nNo', description: 'Change traffic to green?')]
    //                     if(approved != 'Yes'){
    //                         error('Build not approved')
    //                         }
    //                     }
    //                 } catch (error){
    //                     error('Build not approved in time')
    //                 }
    //             }
    //         }
    //     }
	// 	stage('Create the service in kubernetes cluster traffic to green controller') {
	// 		steps {
	// 			withAWS(region:'us-west-2', credentials:'aws-cli') {
	// 				sh '''
    //                     sed -i 'color/blue/green/' bubble-backend-service.yml
	// 					kubectl apply -f ./bubble-backend-service.yml
	// 				'''
	// 			}
	// 		}
	// 	}
    // }
//     post {
//         failure {
//             script {
//                 def statusComment = ""
//                 if (testfail) {
//                     def summary = junit testResults: '**/target/surefire-reports/*.xml'
//                     statusComment = "*[${env.JOB_NAME}] <#${env.BUILD_NUMBER}>* failed to build on ${env.GIT_BRANCH} branch."
//                     statusComment += "\nRan ${summary.getTotalCount()} total tests."
//                     statusComment += "\n\tFailed ${summary.getFailCount()}, Passed ${summary.getPassCount()}, Skipped ${summary.getSkipCount()}"
//                     statusComment += "\nSeems you still have a ways to go hm? :face_with_monocle:"
//                 } else {
//                     statusComment = "**${env.JOB_NAME} ended in ${currentBuild.currentResult}**"
//                     statusComment += "\n\tCheck the stage that failed for more information"
//                 }
// //                 discordSend description: statusComment, result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
//             }
//         }
//         success {
// //             discordSend description: ":potable_water: **Pipeline successful!**", result: currentBuild.currentResult, webhookURL: env.WEBHO_BE
//             sh 'docker container ls'
//         }
//     }
            stage('Set eks use'){
            steps{
                container('kubectl'){
                    script{
                        withAWS(credentials:'aws-creds', region:'us-east-1'){
                            sh 'aws eks update-kubeconfig --name team-aqua-mx2ESgug'
                        }
                    }
                }
            }
        }//end stage

        stage('Find current color'){
            steps{
                container('kubectl'){
                    script{
                        withAWS(credentials:'aws-creds', region:'us-east-1'){
                           COLOR = '$(kubectl get service backend -o jsonpath="{.spec.selector.color}")'
                        }
                        sh 'echo $COLOR'
                    }
                }
            }
        }
            stage('Create service for deployment') {
			steps {
                container ('kubectl') {
				    withAWS(credentials:'aws-creds', region:'us-east-1') {
					    sh 'kubectl apply -f ./Kubernetes/bubble-backend-service.yml'
                        env.COLOR = sh(script: 'kubectl get service backend -o jsonpath="{.spec.selector.color}"',returnStdout: true)
				    }
                }
			}
		}//end stage
    }
 }

 