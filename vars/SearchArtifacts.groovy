import org.contoso.NexusTwo

def call(Map mavenCoordinates) {
    if (mavenCoordinates == null || mavenCoordinates.size() == 0) {
        mavenCoordinates = [
                groupId   : "",
                artifactId: "",
                version   : "",
                packaging : "",
                classifier: ""
        ]
    }
    return NexusTwo.searchArtifacts(mavenCoordinates)
}

def call(jenkins, Map mavenCoordinates) {
    if (mavenCoordinates == null || mavenCoordinates.size() == 0) {
        mavenCoordinates = [
                groupId   : "",
                artifactId: "",
                version   : "",
                packaging : "",
                classifier: ""
        ]
    }
    return NexusTwo.searchArtifacts(jenkins, mavenCoordinates)
}
