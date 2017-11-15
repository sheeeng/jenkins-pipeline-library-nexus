package org.contoso

//import static groovy.json.JsonOutput.prettyPrint

class NexusTwo {
    static String ROOT_URL = 'http://nexus-two:8081/nexus'

    static final String LUCENE_SEARCH_URL = "${ROOT_URL}/service/local/lucene/search"
    static final String STATUS_URL = "${ROOT_URL}/service/local/status"
    static final String REPOSITORIES_URL = "${ROOT_URL}/service/local/repositories"
    static final def HTTP_CODE_FOUND_OK = [302, 200]

    static boolean isOnline() {
        def appendableOutput = new StringBuilder(), appendableError = new StringBuilder()
        def proc = ["curl",
                    "--head",
                    "--silent",
                    "--output", "/dev/null",
                    "--write-out", "%{http_code}",
                    "--location", "${ROOT_URL}",
        ].execute()
        proc.consumeProcessOutput(appendableOutput, appendableError)
        proc.waitForOrKill(1000)
        //println "output> ${appendableOutput.toString()}"
        //println "error> ${appendableError.toString()}"

        if (proc.exitValue() != 0) {
            return false
        }

        if (appendableOutput.toInteger() in HTTP_CODE_FOUND_OK) {
            return true
        } else {
            return false
        }
    }

    static boolean isOffline() {
        return !isOnline()
    }

    static def getStatus() {
        def appendableOutput = new StringBuilder(), appendableError = new StringBuilder()

        def proc = ["curl",
                    "--silent",
                    "--fail",
                    "--show-error",  // https://superuser.com/a/1249678
                    "--header", "Content-Type: application/json",
                    "--header", "Accept: application/json",
                    "--request", "GET",
                    "--location", "${STATUS_URL}"
        ].execute()
        proc.consumeProcessOutput(appendableOutput, appendableError)
        proc.waitForOrKill(1000)
        //println "output> ${appendableOutput.toString()}"
        //println "error> ${appendableError.toString()}"

        if (proc.exitValue() != 0)
            return "${appendableError.toString()}"
        else
            return "${appendableOutput.toString()}"
    }

    static def search(String keyword) {
        def appendableOutput = new StringBuilder(), appendableError = new StringBuilder()
        def proc = ["curl",
                    "--silent",
                    "--fail",
                    "--show-error",  // https://superuser.com/a/1249678
                    "--header", "Content-Type: application/json",
                    "--header", "Accept: application/json",
                    "--request", "GET",
                    "--location", "${LUCENE_SEARCH_URL}"
                            + '?' + "q=${keyword}"
        ].execute()
        proc.consumeProcessOutput(appendableOutput, appendableError)
        proc.waitForOrKill(1000)
        //println "output> ${prettyPrint(appendableOutput.toString())}"
        //println "error> ${prettyPrint(appendableError.toString())}"

        if (proc.exitValue() != 0)
            return "${appendableError.toString()}"
        else
            return "${appendableOutput.toString()}"
    }

    static def getArtifacts(Map mavenCoordinates) {
        if (mavenCoordinates == null || mavenCoordinates.isEmpty())
            return {}

        def appendableOutput = new StringBuilder(), appendableError = new StringBuilder()
        def proc = ["curl",
                    "--silent",
                    "--fail",
                    "--show-error",  // https://superuser.com/a/1249678
                    "--header", "Content-Type: application/json",
                    "--header", "Accept: application/json",
                    "--request", "GET",
                    "--location", "${LUCENE_SEARCH_URL}"
                            + '?' + "g=${mavenCoordinates.get('groupId')}"
                            + '&' + "a=${mavenCoordinates.get('artifactId')}"
                            + '&' + "v=${mavenCoordinates.get('version')}"
                            + '&' + "p=${mavenCoordinates.get('packaging')}"
                            + '&' + "c=${mavenCoordinates.get('classifier')}"
        ].execute()
        proc.consumeProcessOutput(appendableOutput, appendableError)
        proc.waitForOrKill(1000)
        //println "output> ${prettyPrint(appendableOutput.toString())}"
        //println "error> ${prettyPrint(appendableError.toString())}"

        if (proc.exitValue() != 0)
            return "${appendableError.toString()}"
        else
            return "${appendableOutput.toString()}"
    }
}