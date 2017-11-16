import org.contoso.NexusTwo

def call() {
    return NexusTwo.isOnline()
}

def call(jenkins) {
    return NexusTwo.isOnline(jenkins)
}
