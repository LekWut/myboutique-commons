#!groovy
// Jenkinsfile for MyBoutique-Commons
podTemplate(
  label: "jenkins-pod",
  cloud: "openshift",
  inheritFrom: "maven",
  containers: [
    containerTemplate(
      name: "jnlp",
      image: "docker-registry.default.svc:5000/${GUID}-tools/jenkins-agent-appdev",
      resourceRequestMemory: "1Gi",
      resourceLimitMemory: "2Gi"
    )
  ]
) {
  node('jenkins-pod') {
    //echo "GUID: ${GUID}"
    //echo "CLUSTER: ${CLUSTER}"
	//echo "Git: ${REPO}"
    
    // Checkout Source Code.
    stage('Checkout Source') {
      git url: ${REPO}"myboutique-commons.git"
    }

    def mvnCmd = "mvn -s nexus_setting.xml clean package -DskipTests=true" 

    stage('Build war') {
	  echo "Building version"
	  sh "${mvnCmd} clean package -DskipTests"
	  // TBD: Execute Maven Build
    }
  
    // Using Maven build the war file
    // Do not run tests in this step
    stage('Build war') {
      echo "Building version"
      sh "${mvnCmd} clean package -DskipTests"
      // TBD: Execute Maven Build
    }
	  
	  stage('Unit Tests') {
        echo "Running Unit Tests"
        sh "${mvnCmd} test"
        // TBD: Execute Unit Tests
      }
	  
	  // Publish the built war file to Nexus
      stage('Publish to Nexus') {
        echo "Publish to Nexus"
        sh "${mvnCmd} deploy -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus3.${GUID}-tools.svc.cluster.local:8081/repository/releases/"
        // TBD: Publish to Nexus
      }
  }
}
