package id.ac.ui.cs.advprog.tableservicerizzerve.observer;

import id.ac.ui.cs.advprog.tableservicerizzerve.model.Meja;
import org.springframework.context.ApplicationEvent;

public class MejaCreatedEvent extends ApplicationEvent {

    public MejaCreatedEvent(Object source, Meja meja) {
        super(source);
    }

    public Meja getMeja() {
        return null;
    }
}