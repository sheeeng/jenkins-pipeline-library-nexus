import org.contoso.NexusTwo

def call() {
    return NexusTwo.isOffline()
}

def call(jenkins) {
    return NexusTwo.isOffline(jenkins)
}
