import org.contoso.NexusTwo

def call() {
    return NexusTwo.getRepositories()
}

def call(jenkins) {
    return NexusTwo.getRepositories(jenkins)
}
