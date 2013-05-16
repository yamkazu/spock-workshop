package learning.mock

class Publisher {
    List<Subscriber> subscribers = []
    void send(String message) {     
        subscribers.each { it.receive(message) }
    }
}
