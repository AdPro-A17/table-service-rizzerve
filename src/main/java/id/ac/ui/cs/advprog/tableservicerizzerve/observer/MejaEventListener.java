package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MejaEventListener {

    @EventListener
    public void handleMejaCreatedEvent(MejaCreatedEvent event) {
        System.out.println("Event received - Meja created with nomorMeja: " + event.getMeja().getNomorMeja());
    }
}