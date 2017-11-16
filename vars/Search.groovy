import org.contoso.NexusTwo

def call(String keyword) {
    return NexusTwo.search(keyword)
}

def call(jenkins, String keyword) {
    return NexusTwo.search(jenkins, keyword)
}