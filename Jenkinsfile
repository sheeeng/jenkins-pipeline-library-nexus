#!/usr/bin/env groovy

import static groovy.json.JsonOutput.prettyPrint

println "Hello from Jenkinsfile!"

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building.'
                library identifier: 'pipeline-library-nexus@master',
                        retriever: modernSCM([
                                $class: 'GitSCMSource',
                                credentialsId: '',
                                id: 'a7037fe6-c0fb-4416-961c-61a6ba5c8aaf',
                                remote: 'https://github.com/sheeeng/pipeline-library-nexus',
                                traits: [[$class: 'jenkins.plugins.git.traits.BranchDiscoveryTrait']]
                        ])
                script {
                    mavenCoordinates = [
                            groupId   : "log4j",
                            artifactId: "log4j",
                            version   : "",
                            packaging : "",
                            classifier: ""
                    ]
                    for (i = 0; i <1; i++) {
                        echo "SetRootUrl('http://nexus-two:8081/nexus'):\n${SetRootUrl('http://nexus-two:8081/nexus')}"
                        echo "GetRootUrl():\n${GetRootUrl()}"

                        //echo "IsOnline():\n${IsOnline()}"
                        //echo "IsOffline():\n${IsOffline()}"
                        //echo "GetStatus():\n${GetStatus()}"
                        //echo "GetRepositories():\n${GetRepositories()}"
                        //echo "Search('log4j'):\n${Search('log4j')}"
                        //echo "SearchArtifacts(mavenCoordinates):\n${SearchArtifacts(mavenCoordinates)}"
                        //echo "SearchVersions(mavenCoordinates):\n${SearchVersions(mavenCoordinates)}"

                        echo "IsOnline(this):\n${IsOnline(this)}"
                        echo "IsOffline(this):\n${IsOffline(this)}"
                        echo "GetStatus(this):\n${GetStatus(this)}"
                        echo "GetRepositories(this):\n${GetRepositories(this)}"
                        echo "Search(this, 'log4j'):\n${Search(this, 'log4j')}"
                        echo "SearchArtifacts(this, mavenCoordinates):\n${SearchArtifacts(this, mavenCoordinates)}"
                        echo "SearchVersions(this, mavenCoordinates):\n${SearchVersions(this, mavenCoordinates)}"
                        sleep(3)
                    }
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing.'

                script {
                    timeout(time: 30, unit: 'SECONDS') {
                        def userInputResult = input(
                                id: "userInput",
                                submitter: 'admin,jon,daenerys',
                                submitterParameter: 'submitter',
                                message: "Are you sure to proceed?",
                                parameters: [
                                        [$class: 'TextParameterDefinition',
                                         name: 'customText',
                                         defaultValue: "What's on your mind?",
                                         description: "What's on your mind?"],
                                        [$class: 'BooleanParameterDefinition',
                                         name: 'customBoolean',
                                         defaultValue: false,
                                         description: 'Are you sure what are you doing?']
                                ])
                        echo "It was `${userInputResult.submitter}` who submitted the dialog."
                        echo "Received `${userInputResult.customText}` as submitted custom text parameter."
                        echo "Received `${userInputResult.customBoolean}` as submitted custom boolean parameter."
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying.'
            }
        }
    }
}
