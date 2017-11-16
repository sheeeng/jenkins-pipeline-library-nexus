import org.contoso.NexusTwo

def call(jenkins, Map mavenCoordinates) {
    if (mavenCoordinates == null || mavenCoordinates.isEmpty()) {
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
