package org.contoso

//import static groovy.json.JsonOutput.prettyPrint

class NexusTwo {
    static String ROOT_URL = 'http://repositories.sonatype.org/nexus'  // 'http://nexus-two:8081/nexus'
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

    static def getRepositories() {
        def appendableOutput = new StringBuilder(), appendableError = new StringBuilder()

        def command = """
                    curl \
                    --silent --fail --show-error \
                    --header "Content-Type: application/json" \
                    --header "Accept: application/json" \
                    --request GET \
                    --location "${REPOSITORIES_URL}" \
                    | jq -r '..|select(has("id"))?|.id'
                    """
        def proc = ['bash',
                    '-c',
                    command].execute()
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

    static def searchArtifacts(Map mavenCoordinates) {
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

    static def searchVersions(Map mavenCoordinates) {
        def appendableOutput = new StringBuilder(), appendableError = new StringBuilder()
        def command = """
                    curl \
                    --silent \
                    --header "Content-Type: application/json" \
                    --header "Accept: application/json" \
                    --location "${LUCENE_SEARCH_URL}?g=${mavenCoordinates.groupId}&a=${mavenCoordinates.artifactId}&v=${mavenCoordinates.version}&p=${mavenCoordinates.packaging}&c=${mavenCoordinates.classifier}" \
                    | jq -r '..|select(has("version"))?|.version' | sort -gr
                    """
        def proc = ['bash',
                    '-c',
                    command].execute()
        proc.consumeProcessOutput(appendableOutput, appendableError)
        proc.waitForOrKill(1000)
        //println "output> ${prettyPrint(appendableOutput.toString())}"
        //println "error> ${prettyPrint(appendableError.toString())}"

        if (proc.exitValue() != 0)
            return "${appendableError.toString()}"
        else
            return "${appendableOutput.toString()}"
    }

    static def isOnline(jenkins) {
        def httpCode = jenkins.sh(
                returnStdout: true,
                script: """
                        curl \
                        --silent --fail --show-error \
                        --head \
                        --output /dev/null \
                        --write-out %{http_code} \
                        --location "${ROOT_URL}"
                        """
        )

        if (httpCode in HTTP_CODE_FOUND_OK) {
            return true
        } else {
            return false
        }
    }

    static def isOffline(jenkins) {
        return !isOnline(jenkins)
    }

    static def getStatus(jenkins) {
        def status = jenkins.sh(
                returnStdout: true,
                script: """
                        curl \
                        --silent --fail --show-error \
                        --header "Content-Type: application/json" \
                        --header "Accept: application/json" \
                        --request GET \
                        --location "${STATUS_URL}"
                        """
        )

        return status
    }

    static def getRepositories(jenkins) {
        def repositories = jenkins.sh(
                returnStdout: true,
                script: """
                        curl \
                        --silent --fail --show-error \
                        --header "Content-Type: application/json" \
                        --header "Accept: application/json" \
                        --request GET \
                        --location "${REPOSITORIES_URL}" \
                        | jq -r '..|select(has("id"))?|.id'
                        """
        ).split("\r?\n")

        return repositories
    }

    static def search(jenkins, String keyword) {
        // Store output in random file as JENKINS-26133 exists might due to typographical error.
        // Beware typing wrongly `returnStdout` as `returnStdOut`, which return null object.
        // Otherwise, waste time trying to debug the above problem.
        // You have been warned!

        def versions = jenkins.sh(
                returnStdout: true,
                script: """
                        curl \
                        --silent \
                        --header "Content-Type: application/json" \
                        --header "Accept: application/json" \
                        --location "${LUCENE_SEARCH_URL}?q=${keyword}"
                        """
        ).split("\r?\n")

        return versions
    }

    static def searchArtifacts(jenkins, mavenCoordinates) {
        // Store output in random file as JENKINS-26133 exists might due to typographical error.
        // Beware typing wrongly `returnStdout` as `returnStdOut`, which return null object.
        // Otherwise, waste time trying to debug the above problem.
        // You have been warned!

        def versions = jenkins.sh(
                returnStdout: true,
                script: """
                        curl \
                        --silent \
                        --header "Content-Type: application/json" \
                        --header "Accept: application/json" \
                        --location "${LUCENE_SEARCH_URL}?g=${mavenCoordinates.groupId}&a=${mavenCoordinates.artifactId}&v=${mavenCoordinates.version}&p=${mavenCoordinates.packaging}&c=${mavenCoordinates.classifier}"
                        """
        ).split("\r?\n")

        return versions
    }

    static def searchVersions(jenkins, mavenCoordinates) {
        // Store output in random file as JENKINS-26133 exists might due to typographical error.
        // Beware typing wrongly `returnStdout` as `returnStdOut`, which return null object.
        // Otherwise, waste time trying to debug the above problem.
        // You have been warned!

        def versions = jenkins.sh(
                returnStdout: true,
                script: """
                        curl \
                        --silent \
                        --header "Content-Type: application/json" \
                        --header "Accept: application/json" \
                        --location "${LUCENE_SEARCH_URL}?g=${mavenCoordinates.groupId}&a=${mavenCoordinates.artifactId}&v=${mavenCoordinates.version}&p=${mavenCoordinates.packaging}&c=${mavenCoordinates.classifier}" \
                        | jq -r '..|select(has("version"))?|.version' | sort -gr
                        """
        ).split("\r?\n")

        return versions
    }

}