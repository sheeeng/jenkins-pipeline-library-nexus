import org.contoso.NexusTwo

def call(Map mavenCoordinates) {
    if (mavenCoordinates == null || mavenCoordinates.isEmpty()) {
        mavenCoordinates = [
                groupId   : "",
                artifactId: "",
                version   : "",
                packaging : "",
                classifier: ""
        ]
    }
    return NexusTwo.getArtifacts(mavenCoordinates)
}

