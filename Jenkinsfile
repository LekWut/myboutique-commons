#!groovy
// Jenkinsfile for MLBParks
podTemplate(
  label: "skopeo-pod",
  cloud: "openshift",
  inheritFrom: "maven",
  containers: [
    containerTemplate(
      name: "jnlp",
      image: "docker-registry.default.svc:5000/${GUID}-jenkins/jenkins-agent-appdev",
      resourceRequestMemory: "1Gi",
      resourceLimitMemory: "2Gi"
    )
  ]
) {
  node('skopeo-pod') {
    echo "GUID: ${GUID}"
    echo "CLUSTER: ${CLUSTER}"
    
    // Checkout Source Code.
    stage('Checkout Source') {
      git url: "${REPO}"
    }

    // Your Pipeline Code goes here. Make sure to use the ${GUID} and ${CLUSTER} parameters where appropriate
    // You need to build the application in directory `MLBParks`.
    // Also copy "../nexus_settings.xml" to your build directory
    // and replace 'GUID' in the file with your ${GUID} to point to >your< Nexus instance
    
    // Define Maven Command to point to the correct
    // settings for our Nexus installation
    def mvnCmd = "mvn -s ../nexus_settings.xml clean package -DskipTests=true" 

    // Build the MLBParks Service
    dir('MLBParks') {
      // The following variables need to be defined at the top level
      // and not inside the scope of a stage - otherwise they would not
      // be accessible from other stages.
      // Extract version from the pom.xml
      def version = getVersionFromPom("pom.xml")

      // TBD Set the tag for the development image: version + build number
      def devTag  = "${version}-${BUILD_NUMBER}"
      // Set the tag for the production image: version
      def prodTag = "${version}"

      // Using Maven build the war file
      // Do not run tests in this step
      stage('Build war') {
        echo "Building version ${devTag}"
        sh "${mvnCmd} clean package -DskipTests"
        // TBD: Execute Maven Build
      }

      // TBD: The next two stages should run in parallel

      // Using Maven run the unit tests
      stage('Unit Tests') {
        echo "Running Unit Tests"
        sh "${mvnCmd} test"
        // TBD: Execute Unit Tests
      }

      // Using Maven to call SonarQube for Code Analysis
      stage('Code Analysis') {
        echo "Running Code Analysis"
        sh "${mvnCmd} sonar:sonar -Dsonar.host.url=http://sonarqube-${GUID}-sonarqube.apps.${CLUSTER}/ -Dsonar.projectName=${JOB_BASE_NAME}-${devTag}"
        // TBD: Execute Sonarqube Tests
      }

      // Publish the built war file to Nexus
      stage('Publish to Nexus') {
        echo "Publish to Nexus"
        sh "${mvnCmd} deploy -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus3.${GUID}-nexus.svc.cluster.local:8081/repository/releases"
        // TBD: Publish to Nexus
      }

      // Build the OpenShift Image in OpenShift and tag it.
      stage('Build and Tag OpenShift Image') {
        echo "Building OpenShift container image mlbparks:${devTag}"
        // Start Binary Build in OpenShift using the file we just published
        // The filename is mlbparks.war in the 'target' directory of your current
        // Jenkins workspace
        // Replace ${GUID}-parks-dev with the name of your dev project
        sh "oc start-build mlbparks --follow --from-file=./target/mlbparks.war -n ${GUID}-parks-dev"

        // OR use the file you just published into Nexus:
        //sh "oc start-build mlbparks --follow --from-file=http://nexus3.${GUID}-nexus.svc.cluster.local:8081/repository/releases/com/openshift/evg/roadshow/mlbparks/${version}/mlbparks-${version}.war -n ${GUID}-parks-dev"

        // Tag the image using the devTag
        openshiftTag alias: 'false', destStream: 'mlbparks', destTag: devTag, destinationNamespace: "${GUID}-parks-dev", namespace: "${GUID}-parks-dev", srcStream: 'mlbparks', srcTag: 'latest', verbose: 'false'
        // TBD: Build Image, tag Image
      }

      // Deploy the built image to the Development Environment.
      stage('Deploy to Dev') {
        echo "Deploying container image to Development Project"
        // Update the Image on the Development Deployment Config
        sh "oc set image dc/mlbparks mlbparks=docker-registry.default.svc:5000/${GUID}-parks-dev/mlbparks:${devTag} -n ${GUID}-parks-dev"

        // Deploy the development application.
        // Replace ${GUID}-parks-dev with the name of your production project
        openshiftDeploy depCfg: 'mlbparks', namespace: "${GUID}-parks-dev", verbose: 'false', waitTime: '', waitUnit: 'sec'
        openshiftVerifyDeployment depCfg: 'mlbparks', namespace: "${GUID}-parks-dev", replicaCount: '1', verbose: 'false', verifyReplicaCount: 'false', waitTime: '', waitUnit: 'sec'
        openshiftVerifyService namespace: "${GUID}-parks-dev", svcName: 'mlbparks', verbose: 'false'
        // TBD: Deploy to development Project
        //      Set Image, Set VERSION
        //      Make sure the application is running and ready before proceeding
      }
      
      stage('Integration Tests') {
        echo "Running Integration Tests"
        sleep 30
        echo "MLB Parks ws check health"
        sh "curl -i -H 'Content-Length: 0' -X GET http://nationalparks-${GUID}-parks-dev.apps.${CLUSTER}/ws/healthz/"
        echo "MLB Parks ws ws info"
        sh "curl -i -H 'Content-Length: 0' -X GET http://nationalparks-${GUID}-parks-dev.apps.${CLUSTER}/ws/info/"
      }


      // Copy Image to Nexus container registry
      stage('Copy Image to Nexus container registry') {
        echo "Copy image to Nexus container registry"

        // TBD: Copy image to Nexus container registry
        sh "skopeo copy --src-tls-verify=false --dest-tls-verify=false --src-creds openshift:\$(oc whoami -t) --dest-creds admin:admin123 docker://docker-registry.default.svc.cluster.local:5000/${GUID}-parks-dev/mlbparks:${devTag} docker://nexus-registry.${GUID}-nexus.svc.cluster.local:5000/mlbparks:${devTag}"

        // TBD: Tag the built image with the production tag.
        openshiftTag alias: 'false', destStream: 'mlbparks', destTag: prodTag, destinationNamespace: "${GUID}-parks-dev", namespace: "${GUID}-parks-dev", srcStream: 'mlbparks', srcTag: devTag, verbose: 'false'
      }

      // Blue/Green Deployment into Production
      // -------------------------------------
      def destApp   = "mlbparks-green"
      def activeApp = ""

      stage('Blue/Green Production Deployment') {
        // TBD: Determine which application is active
        //      Set Image, Set VERSION
        //      Deploy into the other application
        //      Make sure the application is running and ready before proceeding
        // Replace ${GUID}-parks-dev and ${GUID}-parks-prod with
        // your project names
        activeApp = sh(returnStdout: true, script: "oc get route mlbparks -n ${GUID}-parks-prod -o jsonpath='{ .spec.to.name }'").trim()
        if (activeApp == "mlbparks-green") {
          destApp = "mlbparks-blue"
        }
        echo "Active Application:      " + activeApp
        echo "Destination Application: " + destApp

        // Update the Image on the Production Deployment Config
        sh "oc set image dc/${destApp} ${destApp}=docker-registry.default.svc:5000/${GUID}-parks-dev/mlbparks:${prodTag} -n ${GUID}-parks-prod"

        // Update the Config Map which contains the users for the MLBParks application
        //sh "oc delete configmap ${destApp}-config -n ${GUID}-parks-prod --ignore-not-found=true"
        //sh "oc create configmap ${destApp}-config --from-file=./configuration/application-users.properties --from-file=./configuration/application-roles.properties -n ${GUID}-parks-prod"

        // Deploy the inactive application.
        // Replace ${GUID}-parks-prod with the name of your production project
        openshiftDeploy depCfg: destApp, namespace: "${GUID}-parks-prod", verbose: 'false', waitTime: '', waitUnit: 'sec'
        openshiftVerifyDeployment depCfg: destApp, namespace: "${GUID}-parks-prod", replicaCount: '1', verbose: 'false', verifyReplicaCount: 'true', waitTime: '', waitUnit: 'sec'
        openshiftVerifyService namespace: "${GUID}-parks-prod", svcName: destApp, verbose: 'false'
      }

      stage('Switch over to new Version') {
        echo "Switching Production application to ${destApp}."
        // TBD: Execute switch
        sh "oc patch route mlbparks -n ${GUID}-parks-prod -p '{\"spec\":{\"to\":{\"name\":\"" + destApp + "\"}}}'"
      }
    }
  }
}

// Convenience Functions to read variables from the pom.xml
// Do not change anything below this line.
def getVersionFromPom(pom) {
  def matcher = readFile(pom) =~ '<version>(.+)</version>'
  matcher ? matcher[0][1] : null
}
def getGroupIdFromPom(pom) {
  def matcher = readFile(pom) =~ '<groupId>(.+)</groupId>'
  matcher ? matcher[0][1] : null
}
def getArtifactIdFromPom(pom) {
  def matcher = readFile(pom) =~ '<artifactId>(.+)</artifactId>'
  matcher ? matcher[0][1] : null
}

