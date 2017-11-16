import org.contoso.NexusTwo

def call() {
    return NexusTwo.getStatus()
}

def call(jenkins) {
    return NexusTwo.getStatus(jenkins)
}